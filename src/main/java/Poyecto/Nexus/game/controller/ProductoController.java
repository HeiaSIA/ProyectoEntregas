package Poyecto.Nexus.game.controller;

import Poyecto.Nexus.game.entity.Producto;
import Poyecto.Nexus.game.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    private final String RUTA_IMAGENES = "src/main/resources/static/imagenes/";

    @GetMapping
    public String mostrarPanel(Model model) {
        model.addAttribute("nuevoProducto", new Producto());
        model.addAttribute("listaProductos", productoRepository.findAll());
        return "panel_productos";
    }

    @GetMapping("/editar/{id}")
    public String editarProducto(@PathVariable("id") Long id, Model model) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto == null) {
            return "redirect:/productos?error=productoNoEncontrado";
        }
        model.addAttribute("nuevoProducto", producto);
        model.addAttribute("listaProductos", productoRepository.findAll());
        return "panel_productos";
    }

    @PostMapping("/guardar")
    public String guardarProducto(
            @ModelAttribute Producto producto,
            @RequestParam("archivoImagen") MultipartFile archivoImagen) {

        boolean esEdicion = producto.getIdProducto() != null;

        if (!esEdicion && archivoImagen.isEmpty()) {
            return "redirect:/productos?error=imagenVacia";
        }

        try {
            if (!archivoImagen.isEmpty()) {
                Path directorioImagenes = Paths.get(RUTA_IMAGENES).toAbsolutePath();
                
                if (!Files.exists(directorioImagenes)) {
                    Files.createDirectories(directorioImagenes);
                }

                String nombreArchivo = UUID.randomUUID() + "_" + archivoImagen.getOriginalFilename();
                Path rutaDestino = directorioImagenes.resolve(nombreArchivo);

                archivoImagen.transferTo(rutaDestino.toFile());

                if (esEdicion) {
                    Producto productoExistente = productoRepository.findById(producto.getIdProducto()).orElse(null);
                    if (productoExistente != null && productoExistente.getImagen() != null) {
                        eliminarArchivoFisico(productoExistente.getImagen());
                    }
                }

                producto.setImagen(nombreArchivo);
            } else if (esEdicion) {
                Producto productoExistente = productoRepository.findById(producto.getIdProducto()).orElse(null);
                if (productoExistente != null) {
                    producto.setImagen(productoExistente.getImagen());
                }
            }

            productoRepository.save(producto);

        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/productos?error=errorSubiendoArchivo";
        }

        return "redirect:/productos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable("id") Long id) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto != null) {
            if (producto.getImagen() != null) {
                eliminarArchivoFisico(producto.getImagen());
            }
            productoRepository.delete(producto);
        }
        return "redirect:/productos";
    }

    private void eliminarArchivoFisico(String nombreImagen) {
        try {
            Path rutaArchivo = Paths.get(RUTA_IMAGENES).resolve(nombreImagen).toAbsolutePath();
            Files.deleteIfExists(rutaArchivo);
        } catch (IOException e) {
            System.err.println("No se pudo eliminar la imagen físicamente: " + e.getMessage());
        }
    }
}
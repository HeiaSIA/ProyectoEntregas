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

    // Ruta de almacenamiento de imágenes
    private final String RUTA_IMAGENES = "src/main/resources/static/imagenes/";

    // Muestra la pantalla del panel
    @GetMapping
    public String mostrarPanel(Model model) {
        // Inicializa un objeto vacío para el formulario (Crear)
        model.addAttribute("nuevoProducto", new Producto());
        model.addAttribute("listaProductos", productoRepository.findAll());
        return "panel_productos";
    }

    // Carga un producto existente en el formulario para editarlo
    @GetMapping("/editar/{id}")
    public String editarProducto(@PathVariable("id") Long id, Model model) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto == null) {
            return "redirect:/productos?error=productoNoEncontrado";
        }
        // Pasamos el producto encontrado al formulario en lugar de uno vacío
        model.addAttribute("nuevoProducto", producto);
        model.addAttribute("listaProductos", productoRepository.findAll());
        return "panel_productos";
    }

    // Guarda el producto (Soporta tanto creación como actualización)
    @PostMapping("/guardar")
    public String guardarProducto(
            @ModelAttribute Producto producto,
            @RequestParam("archivoImagen") MultipartFile archivoImagen) {

        // Validamos si es una actualización (si ya tiene un ID asignado)
        boolean esEdicion = producto.getIdProducto() != null;

        // Si es una creación y no enviaron imagen, da error
        if (!esEdicion && archivoImagen.isEmpty()) {
            return "redirect:/productos?error=imagenVacia";
        }

        try {
            // Si subieron un archivo nuevo de imagen (aplica para nuevo o para actualizar foto)
            if (!archivoImagen.isEmpty()) {
                Path directorioImagenes = Paths.get(RUTA_IMAGENES).toAbsolutePath();
                
                if (!Files.exists(directorioImagenes)) {
                    Files.createDirectories(directorioImagenes);
                }

                // Generar nombre único
                String nombreArchivo = UUID.randomUUID() + "_" + archivoImagen.getOriginalFilename();
                Path rutaDestino = directorioImagenes.resolve(nombreArchivo);

                // Guardar el archivo físicamente
                archivoImagen.transferTo(rutaDestino.toFile());

                // Si es edición, intentamos eliminar la imagen vieja del disco para no acumular basura
                if (esEdicion) {
                    Producto productoExistente = productoRepository.findById(producto.getIdProducto()).orElse(null);
                    if (productoExistente != null && productoExistente.getImagen() != null) {
                        eliminarArchivoFisico(productoExistente.getImagen());
                    }
                }

                // Asignar la nueva imagen
                producto.setImagen(nombreArchivo);
            } else if (esEdicion) {
                // Si estamos editando pero NO subieron una nueva imagen, conservamos la que ya tenía en la DB
                Producto productoExistente = productoRepository.findById(producto.getIdProducto()).orElse(null);
                if (productoExistente != null) {
                    producto.setImagen(productoExistente.getImagen());
                }
            }

            // Guardar o actualizar en la BD
            productoRepository.save(producto);

        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/productos?error=errorSubiendoArchivo";
        }

        return "redirect:/productos";
    }

    // Elimina el producto y su imagen del almacenamiento físico
    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable("id") Long id) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto != null) {
            // 1. Eliminar la imagen del disco
            if (producto.getImagen() != null) {
                eliminarArchivoFisico(producto.getImagen());
            }
            // 2. Eliminar el registro en la base de datos
            productoRepository.delete(producto);
        }
        return "redirect:/productos";
    }

    // Método auxiliar para borrar físicamente del servidor las imágenes reemplazadas o eliminadas
    private void eliminarArchivoFisico(String nombreImagen) {
        try {
            Path rutaArchivo = Paths.get(RUTA_IMAGENES).resolve(nombreImagen).toAbsolutePath();
            Files.deleteIfExists(rutaArchivo);
        } catch (IOException e) {
            System.err.println("No se pudo eliminar la imagen físicamente: " + e.getMessage());
        }
    }
}
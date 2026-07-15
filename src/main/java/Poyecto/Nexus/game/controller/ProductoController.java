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

    // Muestra la pantalla del panel
    @GetMapping
    public String mostrarPanel(Model model) {
        model.addAttribute("nuevoProducto", new Producto());
        model.addAttribute("listaProductos", productoRepository.findAll());
        return "panel_productos";
    }

    // Guarda el producto y la imagen
    @PostMapping("/guardar")
    public String guardarProducto(
            @ModelAttribute Producto producto,
            @RequestParam("archivoImagen") MultipartFile archivoImagen) {

        // 1. Verificamos que sí enviaron una imagen
        if (archivoImagen.isEmpty()) {
            return "redirect:/productos?error=imagenVacia";
        }

        try {
            // 2. Usar Paths para crear una ruta absoluta
            Path directorioImagenes = Paths.get("src/main/resources/static/imagenes/").toAbsolutePath();
            
            if (!Files.exists(directorioImagenes)) {
                Files.createDirectories(directorioImagenes);
            }

            // 3. Generar el nombre único y la ruta final
            String nombreArchivo = UUID.randomUUID() + "_" + archivoImagen.getOriginalFilename();
            Path rutaDestino = directorioImagenes.resolve(nombreArchivo);

            // 4. Guardar el archivo físicamente
            archivoImagen.transferTo(rutaDestino.toFile());

            // 5. Asignar el nombre de la imagen al producto
            producto.setImagen(nombreArchivo);

            // 6. Guardar en la base de datos (solo si la imagen se guardó bien)
            productoRepository.save(producto);

        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/productos?error=errorSubiendoArchivo";
        }

        return "redirect:/productos";
    }
}
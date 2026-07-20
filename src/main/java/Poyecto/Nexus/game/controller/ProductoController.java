package Poyecto.Nexus.game.controller;

import Poyecto.Nexus.game.entity.Producto;
import Poyecto.Nexus.game.entity.Sede;
import Poyecto.Nexus.game.entity.InventarioSede;
import Poyecto.Nexus.game.service.ProductoService; 
import Poyecto.Nexus.game.repository.ProductoRepository;
import Poyecto.Nexus.game.repository.SedeRepository; 
import Poyecto.Nexus.game.repository.InventarioSedeRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoRepository productoRepository;
    private final ProductoService productoService;
    private final SedeRepository sedeRepository; 
    private final InventarioSedeRepository inventarioSedeRepository;

    private final String RUTA_IMAGENES = "src/main/resources/static/imagenes/";

    // Inyección limpia por constructor
    public ProductoController(ProductoRepository productoRepository, 
                              ProductoService productoService, 
                              SedeRepository sedeRepository,
                              InventarioSedeRepository inventarioSedeRepository) {
        this.productoRepository = productoRepository;
        this.productoService = productoService;
        this.sedeRepository = sedeRepository;
        this.inventarioSedeRepository = inventarioSedeRepository;
    }

    @GetMapping
    public String mostrarPanel(Model model) {
        model.addAttribute("nuevoProducto", new Producto());
        
        // Enviamos la lista del inventario real en lugar de solo productos sueltos
        model.addAttribute("listaInventario", inventarioSedeRepository.findAll());
        model.addAttribute("listaSedes", sedeRepository.findAll()); 
        return "panel_productos";
    }
    
    @GetMapping("/poliza")
    public String verPolizaGarantia(Model model) {
        List<Producto> lista = productoService.listarTodos();
        model.addAttribute("listaProductos", lista);
        return "ver_poliza"; 
    }

    @GetMapping("/editar/{id}")
    public String editarProducto(@PathVariable("id") Long id, Model model) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto == null) {
            return "redirect:/productos?error=productoNoEncontrado";
        }
        model.addAttribute("nuevoProducto", producto);
        model.addAttribute("listaInventario", inventarioSedeRepository.findAll());
        model.addAttribute("listaSedes", sedeRepository.findAll());
        
        // Buscamos si ya tiene un inventario registrado para poblar los inputs de stock y sede en la vista
        InventarioSede inventarioExistente = inventarioSedeRepository.findAll().stream()
                .filter(inv -> inv.getProducto().getIdProducto().equals(id))
                .findFirst().orElse(null);
        
        if (inventarioExistente != null) {
            model.addAttribute("stockActual", inventarioExistente.getStockDisponible());
            model.addAttribute("sedeActualId", inventarioExistente.getSede().getIdSede()); 
        }
        
        return "panel_productos";
    }

    @PostMapping("/guardar")
    public String guardarProducto(
            @ModelAttribute Producto producto,
            @RequestParam("archivoImagen") MultipartFile archivoImagen,
            @RequestParam("idSedeInventario") Long idSedeInventario,
            @RequestParam("stock") Integer stock) { // Capturamos el stock explícitamente desde el formulario

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

            // 1. Guardamos la información base del producto
            Producto productoGuardado = productoRepository.save(producto);

            // 2. Localizamos la sede física asociada al stock
            Sede sedeOrigen = sedeRepository.findById(idSedeInventario)
                    .orElseThrow(() -> new IllegalArgumentException("La Sede ID " + idSedeInventario + " no existe."));

            // 3. Obtenemos el inventario correspondiente o generamos uno nuevo
            InventarioSede inventarioSede = inventarioSedeRepository
                    .findBySedeAndProducto(sedeOrigen, productoGuardado)
                    .orElse(new InventarioSede(sedeOrigen, productoGuardado, 0));

            // 4. Escribimos directamente el stock en el inventario físico de la sede
            inventarioSede.setStockDisponible(stock);
            inventarioSedeRepository.save(inventarioSede);

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
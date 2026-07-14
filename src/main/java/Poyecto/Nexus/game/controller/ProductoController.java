package Poyecto.Nexus.game.controller;

import Poyecto.Nexus.game.entity.Producto;
import Poyecto.Nexus.game.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    // Guarda el producto cuando le das al botón en la web
    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute Producto producto) {
        productoRepository.save(producto);
        return "redirect:/productos"; // Recarga la página
    }
}
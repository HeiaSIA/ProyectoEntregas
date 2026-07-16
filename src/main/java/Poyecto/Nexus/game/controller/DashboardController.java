package Poyecto.Nexus.game.controller;

import Poyecto.Nexus.game.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private ProductoRepository productoRepository; // <-- Inyectamos la conexión a PostgreSQL

    @GetMapping("/")
    public String showIndex() {
        return "index"; 
    }

    @GetMapping("/index")
    public String showIndexAlternativo() {
        return "index";
    }

    // RUTA PARA EL CATÁLOGO COMPLETO (¡Actualizada para alimentar tu frontend!)
    @GetMapping("/catalogo")
    public String showCatalogo(Model model) {
        // Obtenemos los productos reales de la base de datos y se los enviamos al HTML
        model.addAttribute("listaProductos", productoRepository.findAll());
        return "catalogo"; // Retornará templates/catalogo.html
    }

    @GetMapping("/admin")
    public String showDashboard() {
        return "dashboard"; 
    }
}
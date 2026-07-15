package Poyecto.Nexus.game.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/")
    public String showIndex() {
        return "index"; 
    }

    @GetMapping("/index")
    public String showIndexAlternativo() {
        return "index";
    }

    // NUEVA RUTA PARA EL CATÁLOGO COMPLETO
    @GetMapping("/catalogo")
    public String showCatalogo() {
        return "catalogo"; // Retornará templates/catalogo.html
    }

    @GetMapping("/admin")
    public String showDashboard() {
        return "dashboard"; 
    }
}
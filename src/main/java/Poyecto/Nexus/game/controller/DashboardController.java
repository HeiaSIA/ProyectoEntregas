package Poyecto.Nexus.game.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    // 1. Mapea la raíz para que al entrar a http://localhost:8080/ cargue tu index.html
    @GetMapping("/")
    public String showIndex() {
        return "index"; // Busca el archivo index.html en la carpeta templates
    }

    // 2. Mapea también http://localhost:8080/index por si acaso
    @GetMapping("/index")
    public String showIndexAlternativo() {
        return "index";
    }

    // 3. Tu página principal de administración en http://localhost:8080/admin
    @GetMapping("/admin")
    public String showDashboard() {
        return "dashboard"; // Busca el archivo dashboard.html en templates
    }
}
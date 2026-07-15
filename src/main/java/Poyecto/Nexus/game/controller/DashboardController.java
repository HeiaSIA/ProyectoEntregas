package Poyecto.Nexus.game.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    // Esta será tu nueva página principal en http://localhost:8080/admin
    @GetMapping("/admin")
    public String showDashboard() {
        return "dashboard"; // Busca el archivo dashboard.html
    }
}
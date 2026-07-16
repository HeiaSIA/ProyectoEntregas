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

 // 1. Esta ruta muestra el panel SÓLO si tienes el pase VIP (Sesión)
    @GetMapping("/admin")
    public String showDashboard(jakarta.servlet.http.HttpSession session) {
        // Verificamos si existe el pase VIP llamado "adminLogueado"
        if (session.getAttribute("adminLogueado") == null) {
            return "redirect:/index"; // Si no lo tiene, lo expulsamos a la página principal
        }
        return "dashboard"; // Si lo tiene, le mostramos el panel
    }

    // 2. Esta ruta es la que verifica la contraseña que envíes desde el HTML
    @org.springframework.web.bind.annotation.PostMapping("/admin/acceso")
    public String verificarClave(@org.springframework.web.bind.annotation.RequestParam("clave") String clave, jakarta.servlet.http.HttpSession session) {
        // Aquí defines tu clave secreta. ¡Cámbiala por la que quieras!
        if ("Nexus_2026".equals(clave)) {
            session.setAttribute("adminLogueado", true); // Le damos el pase VIP
            return "redirect:/admin"; // Lo dejamos entrar
        }
        return "redirect:/index"; // Contraseña incorrecta, lo devolvemos al inicio
    }
}
package Poyecto.Nexus.game.controller;

import Poyecto.Nexus.game.service.ProductoService;
import Poyecto.Nexus.game.repository.InventarioSedeRepository;
import Poyecto.Nexus.game.repository.SedeRepository;
import org.springframework.beans.factory.annotation.Value; 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

    private final ProductoService productoService; 
    private final SedeRepository sedeRepository;
    private final InventarioSedeRepository inventarioSedeRepository;

    // Inyectamos la contraseña oculta desde el application.properties
    @Value("${admin.panel.password}")
    private String adminPassword;

    public DashboardController(ProductoService productoService, 
                               SedeRepository sedeRepository, 
                               InventarioSedeRepository inventarioSedeRepository) {
        this.productoService = productoService;
        this.sedeRepository = sedeRepository;
        this.inventarioSedeRepository = inventarioSedeRepository;
    }

    @GetMapping("/")
    public String showIndex() {
        return "index"; 
    }

    @GetMapping("/index")
    public String showIndexAlternativo() {
        return "index";
    }

    @GetMapping("/catalogo")
    public String showCatalogo(Model model) {
        model.addAttribute("listaInventario", inventarioSedeRepository.findAll());
        model.addAttribute("listaSedes", sedeRepository.findAll()); 
        return "catalogo"; 
    }

    @GetMapping("/admin")
    public String showDashboard(HttpSession session) {
        if (session.getAttribute("adminLogueado") == null) {
            return "redirect:/index"; 
        }
        return "dashboard"; 
    }

    @PostMapping("/admin/acceso")
    public String verificarClave(@RequestParam("clave") String clave, HttpSession session) {
        if (adminPassword.equals(clave)) {
            session.setAttribute("adminLogueado", true); 
            return "redirect:/admin"; 
        }
        return "redirect:/index"; 
    }
}
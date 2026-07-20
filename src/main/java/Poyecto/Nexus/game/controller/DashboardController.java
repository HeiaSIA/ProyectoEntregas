package Poyecto.Nexus.game.controller;

import Poyecto.Nexus.game.service.ProductoService;
import Poyecto.Nexus.game.repository.InventarioSedeRepository;
import Poyecto.Nexus.game.repository.SedeRepository;
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
    // 1. CORRECCIÓN: Declaramos la variable de instancia para el repositorio de inventarios
    private final InventarioSedeRepository inventarioSedeRepository;

    // 2. CORRECCIÓN: Agregamos InventarioSedeRepository al constructor para activar la inyección de Spring
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
        // 3. ¡LISTO! Ahora esta variable ya existe y no dará error de referencia estática
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
        if ("Nexus_2026".equals(clave)) {
            session.setAttribute("adminLogueado", true); 
            return "redirect:/admin"; 
        }
        return "redirect:/index"; 
    }
}
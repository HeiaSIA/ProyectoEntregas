package Poyecto.Nexus.game.controller;

import Poyecto.Nexus.game.entity.Tramo;
import Poyecto.Nexus.game.service.TramoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tramos")
public class TramoController {

    private final TramoService tramoService;

    // Constructor único, nos deshacemos de los @Autowired sueltos
    public TramoController(TramoService tramoService) {
        this.tramoService = tramoService;
    }

    // 1. Mostrar Panel Principal (Crear y Listar)
    @GetMapping
    public String listarTramos(Model model) {
        if (!model.containsAttribute("nuevoTramo")) {
            model.addAttribute("nuevoTramo", new Tramo());
        }
        model.addAttribute("listaSedes", tramoService.listarTodasLasSedes());
        model.addAttribute("listaTramos", tramoService.listarTodosLosTramos());
        return "nuevo_tramo";
    }

    // Cargar mapa de conexiones
    @GetMapping("/mapa")
    public String mostrarMapaRed(Model model) {
        model.addAttribute("listaSedes", tramoService.listarTodasLasSedes());
        model.addAttribute("listaTramos", tramoService.listarTodosLosTramos());
        return "mapa_tramos";
    }

    // 2. Guardar nuevo tramo
    @PostMapping("/guardar")
    public String guardarTramo(@ModelAttribute("nuevoTramo") Tramo tramo, RedirectAttributes redirectAttributes) {
        try {
            // Le preguntamos al servicio si ya existe, el controlador ya no hace streams
            if (tramoService.existeTramoNuevo(tramo)) {
                redirectAttributes.addFlashAttribute("mensajeError", "El tramo de ida en esta misma dirección ya está registrado.");
                return "redirect:/tramos";
            }

            tramoService.guardar(tramo);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Ruta guardada y conectada con éxito!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al procesar la ruta.");
        }
        return "redirect:/tramos";
    }

    // 3. Eliminar tramo
    @GetMapping("/eliminar/{id}")
    public String eliminarTramo(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            tramoService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Tramo eliminado del sistema.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se pudo eliminar el tramo seleccionado.");
        }
        return "redirect:/tramos";
    }

    // 4. Cargar formulario de edición para el modal
    @GetMapping("/editar/{id}")
    public String editarTramo(@PathVariable("id") Long id, Model model) {
        Tramo tramo = tramoService.buscarPorId(id);
        if (tramo != null) {
            model.addAttribute("tramo", tramo);
            model.addAttribute("listaSedes", tramoService.listarTodasLasSedes());
            return "editar_tramo :: formEditar"; 
        }
        return "error";
    }

    // 5. Procesar la actualización del tramo
    @PostMapping("/actualizar")
    public String actualizarTramo(@ModelAttribute("tramo") Tramo tramo, RedirectAttributes redirectAttributes) {
        try {
            // El servicio se encarga de validar la edición
            if (tramoService.existeOtroTramoIdentico(tramo)) {
                redirectAttributes.addFlashAttribute("mensajeError", "Ya existe otro tramo con esa misma ruta.");
                return "redirect:/tramos";
            }

            tramoService.guardar(tramo);
            redirectAttributes.addFlashAttribute("mensajeExito", "Tramo actualizado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al intentar actualizar el tramo.");
        }
        return "redirect:/tramos";
    }
}
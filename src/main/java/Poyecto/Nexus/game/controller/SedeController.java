package Poyecto.Nexus.game.controller;

import Poyecto.Nexus.game.entity.Sede;
import Poyecto.Nexus.game.service.SedeService;
import org.springframework.beans.factory.annotation.Value; // IMPORTANTE: Para inyectar la variable
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sedes")
public class SedeController {

    private final SedeService sedeService;

    // 1. INYECTAMOS LA CLAVE DESDE application.properties
    @Value("${google.maps.api.key}")
    private String googleApiKey;

    public SedeController(SedeService sedeService) {
        this.sedeService = sedeService;
    }

    @GetMapping
    public String mostrarPanel(Model model) {
        if (!model.containsAttribute("nuevaSede")) {
            model.addAttribute("nuevaSede", new Sede());
        }
        model.addAttribute("listaSedes", sedeService.listarTodas());
        
        // 2. PASAMOS LA LLAVE AL MODELO PARA QUE EL HTML LA PUEDA LEER
        model.addAttribute("googleApiKey", googleApiKey);
        
        return "panel_sedes";
    }

    @PostMapping("/guardar")
    public String guardarSede(@ModelAttribute("nuevaSede") Sede sede, RedirectAttributes redirectAttributes) {
        try {
            if (sedeService.existeSedeNueva(sede)) {
                redirectAttributes.addFlashAttribute("mensajeError", "Ya existe una sede registrada con el nombre '" + sede.getNombreCiudad() + "'.");
                return "redirect:/sedes";
            }

            sedeService.guardar(sede);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Sede registrada exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al registrar la sede en el sistema.");
        }
        return "redirect:/sedes";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarSede(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            sedeService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Sede eliminada del sistema.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se puede eliminar la sede. Asegúrate de que no tenga tramos asociados.");
        }
        return "redirect:/sedes";
    }

    @GetMapping("/editar/{id}")
    public String editarSede(@PathVariable("id") Long id, Model model) {
        Sede sede = sedeService.buscarPorId(id);
        if (sede != null) {
            model.addAttribute("sede", sede);
            return "editar_sede :: formEditarSede"; 
        }
        return "error"; 
    }

    @PostMapping("/actualizar")
    public String actualizarSede(@ModelAttribute("sede") Sede sede, RedirectAttributes redirectAttributes) {
        try {
            if (sedeService.existeOtraSedeIdentica(sede)) {
                redirectAttributes.addFlashAttribute("mensajeError", "Ya existe otra sede registrada con el nombre '" + sede.getNombreCiudad() + "'.");
                return "redirect:/sedes";
            }

            sedeService.guardar(sede);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Datos de la sede actualizados correctamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al intentar actualizar la sede.");
        }
        return "redirect:/sedes";
    }
}
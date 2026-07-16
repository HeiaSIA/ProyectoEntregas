package Poyecto.Nexus.game.controller;

import Poyecto.Nexus.game.entity.Sede;
import Poyecto.Nexus.game.repository.SedeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/sedes")
public class SedeController {

    @Autowired
    private SedeRepository sedeRepository;

    // 1. Mostrar Panel Principal (Crear y Listar)
    @GetMapping
    public String mostrarPanel(Model model) {
        if (!model.containsAttribute("nuevaSede")) {
            model.addAttribute("nuevaSede", new Sede());
        }
        model.addAttribute("listaSedes", sedeRepository.findAll());
        return "panel_sedes";
    }

    // 2. Guardar Sede con validación de nombre duplicado
    @PostMapping("/guardar")
    public String guardarSede(@ModelAttribute("nuevaSede") Sede sede, RedirectAttributes redirectAttributes) {
        try {
            boolean existe = sedeRepository.findAll().stream().anyMatch(s -> 
                s.getNombreCiudad().equalsIgnoreCase(sede.getNombreCiudad())
            );

            if (existe) {
                redirectAttributes.addFlashAttribute("mensajeError", "Ya existe una sede registrada con el nombre '" + sede.getNombreCiudad() + "'.");
                return "redirect:/sedes";
            }

            sedeRepository.save(sede);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Sede registrada exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al registrar la sede en el sistema.");
        }
        return "redirect:/sedes";
    }

    // 3. Eliminar Sede
    @GetMapping("/eliminar/{id}")
    public String eliminarSede(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            sedeRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Sede eliminada del sistema.");
        } catch (Exception e) {
            // Evita caídas del sistema si la sede ya tiene tramos conectados en Base de Datos (Integridad Referencial)
            redirectAttributes.addFlashAttribute("mensajeError", "No se puede eliminar la sede. Asegúrate de que no tenga tramos asociados.");
        }
        return "redirect:/sedes";
    }

    // 4. Retornar fragmento de formulario de edición para el Modal
    @GetMapping("/editar/{id}")
    public String editarSede(@PathVariable("id") Long id, Model model) {
        Optional<Sede> sedeOpt = sedeRepository.findById(id);
        if (sedeOpt.isPresent()) {
            model.addAttribute("sede", sedeOpt.get());
            return "editar_sede :: formEditarSede"; 
        }
        return "error"; 
    }

    // 5. Procesar la actualización de la Sede
    @PostMapping("/actualizar")
    public String actualizarSede(@ModelAttribute("sede") Sede sede, RedirectAttributes redirectAttributes) {
        try {
            boolean existe = sedeRepository.findAll().stream().anyMatch(s -> 
                !s.getIdSede().equals(sede.getIdSede()) &&
                s.getNombreCiudad().equalsIgnoreCase(sede.getNombreCiudad())
            );

            if (existe) {
                redirectAttributes.addFlashAttribute("mensajeError", "Ya existe otra sede registrada con el nombre '" + sede.getNombreCiudad() + "'.");
                return "redirect:/sedes";
            }

            sedeRepository.save(sede);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Datos de la sede actualizados correctamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al intentar actualizar la sede.");
        }
        return "redirect:/sedes";
    }
}
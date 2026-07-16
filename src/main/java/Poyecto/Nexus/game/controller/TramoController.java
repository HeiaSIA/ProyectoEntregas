package Poyecto.Nexus.game.controller;

import Poyecto.Nexus.game.entity.Tramo;
import Poyecto.Nexus.game.repository.TramoRepository;
import Poyecto.Nexus.game.repository.SedeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/tramos")
public class TramoController {

    @Autowired
    private TramoRepository tramoRepository;

    @Autowired
    private SedeRepository sedeRepository;

    // 1. Mostrar Panel Principal (Crear y Listar)
    @GetMapping
    public String listarTramos(Model model) {
        if (!model.containsAttribute("nuevoTramo")) {
            model.addAttribute("nuevoTramo", new Tramo());
        }
        model.addAttribute("listaSedes", sedeRepository.findAll());
        model.addAttribute("listaTramos", tramoRepository.findAll());
        return "nuevo_tramo";
    }

    // NEW ENDPOINT: Cargar mapa de conexiones y sedes fijas
    @GetMapping("/mapa")
    public String mostrarMapaRed(Model model) {
        model.addAttribute("listaSedes", sedeRepository.findAll());
        model.addAttribute("listaTramos", tramoRepository.findAll());
        return "mapa_tramos"; // Retornará la nueva plantilla HTML
    }

    // 2. Guardar nuevo tramo
    @PostMapping("/guardar")
    public String guardarTramo(@ModelAttribute("nuevoTramo") Tramo tramo, RedirectAttributes redirectAttributes) {
        try {
            boolean existe = tramoRepository.findAll().stream().anyMatch(t -> 
                t.getSedeOrigen().getIdSede().equals(tramo.getSedeOrigen().getIdSede()) && 
                t.getSedeDestino().getIdSede().equals(tramo.getSedeDestino().getIdSede())
            );

            if (existe) {
                redirectAttributes.addFlashAttribute("mensajeError", "El tramo de ida en esta misma dirección ya está registrado.");
                return "redirect:/tramos";
            }

            tramoRepository.save(tramo);
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
            tramoRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Tramo eliminado del sistema.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se pudo eliminar el tramo seleccionado.");
        }
        return "redirect:/tramos";
    }

    // 4. Cargar formulario de edición como fragmento para el modal
    @GetMapping("/editar/{id}")
    public String editarTramo(@PathVariable("id") Long id, Model model) {
        Optional<Tramo> tramoOpt = tramoRepository.findById(id);
        if (tramoOpt.isPresent()) {
            model.addAttribute("tramo", tramoOpt.get());
            model.addAttribute("listaSedes", sedeRepository.findAll());
            return "editar_tramo :: formEditar"; 
        }
        return "error";
    }

    // 5. Procesar la actualización del tramo
    @PostMapping("/actualizar")
    public String actualizarTramo(@ModelAttribute("tramo") Tramo tramo, RedirectAttributes redirectAttributes) {
        try {
            boolean existe = tramoRepository.findAll().stream().anyMatch(t -> 
                !t.getIdTramo().equals(tramo.getIdTramo()) &&
                t.getSedeOrigen().getIdSede().equals(tramo.getSedeOrigen().getIdSede()) && 
                t.getSedeDestino().getIdSede().equals(tramo.getSedeDestino().getIdSede())
            );

            if (existe) {
                redirectAttributes.addFlashAttribute("mensajeError", "Ya existe otro tramo con esa misma ruta.");
                return "redirect:/tramos";
            }

            tramoRepository.save(tramo);
            redirectAttributes.addFlashAttribute("mensajeExito", "Tramo actualizado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al intentar actualizar el tramo.");
        }
        return "redirect:/tramos";
    }
}
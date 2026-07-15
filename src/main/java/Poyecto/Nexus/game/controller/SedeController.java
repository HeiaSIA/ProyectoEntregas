package Poyecto.Nexus.game.controller;

import Poyecto.Nexus.game.entity.Sede;
import Poyecto.Nexus.game.repository.SedeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/sedes")
public class SedeController {

    @Autowired
    private SedeRepository sedeRepository;

    @GetMapping
    public String mostrarPanel(Model model) {
        model.addAttribute("nuevaSede", new Sede());
        model.addAttribute("listaSedes", sedeRepository.findAll());
        return "panel_sedes";
    }

    @PostMapping("/guardar")
    public String guardarSede(@ModelAttribute Sede sede) {
        sedeRepository.save(sede);
        return "redirect:/sedes";
    }
}
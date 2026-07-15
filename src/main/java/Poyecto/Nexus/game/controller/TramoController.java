package Poyecto.Nexus.game.controller;

import Poyecto.Nexus.game.entity.Sede;
import Poyecto.Nexus.game.entity.Tramo;
import Poyecto.Nexus.game.repository.SedeRepository;
import Poyecto.Nexus.game.repository.TramoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/tramos")
public class TramoController {

    @Autowired
    private TramoRepository tramoRepository;

    @Autowired
    private SedeRepository sedeRepository;

    @GetMapping("/nuevo")
    public String mostrarFormularioTramo(Model model) {

        List<Sede> listaSedes = sedeRepository.findAll();
        List<Tramo> listaTramos = tramoRepository.findAll();
        model.addAttribute("listaSedes", listaSedes);
        model.addAttribute("listaTramos", listaTramos);
        model.addAttribute("nuevoTramo", new Tramo());
        return "nuevo_tramo";
    }

    @PostMapping("/guardar")
    public String guardarTramo(@ModelAttribute("nuevoTramo") Tramo tramo) {

        tramoRepository.save(tramo);
        return "redirect:/tramos/nuevo";
    }
}
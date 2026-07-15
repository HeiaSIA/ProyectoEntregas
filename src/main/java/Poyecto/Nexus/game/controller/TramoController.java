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

    // Este método carga la página HTML y le envía los datos necesarios
    @GetMapping("/nuevo")
    public String mostrarFormularioTramo(Model model) {
        // 1. Buscamos todas las sedes en la base de datos (Quito, Guayaquil, etc.)
        List<Sede> listaSedes = sedeRepository.findAll();
        
        // 2. Enviamos la lista de sedes al HTML para llenar los <select>
        model.addAttribute("listaSedes", listaSedes);
        
        // 3. Enviamos un objeto Tramo vacío para que el formulario lo llene
        model.addAttribute("nuevoTramo", new Tramo());
        
        // Retorna el nombre de tu archivo HTML (Asegúrate de que se llame "nuevo-tramo.html")
        return "nuevo_tramo"; 
    }

    // Este método recibe los datos del formulario cuando le das a "Guardar Ruta"
    @PostMapping("/guardar")
    public String guardarTramo(@ModelAttribute("nuevoTramo") Tramo tramo) {
        
        // Guardamos la arista con su peso (distancia) en PostgreSQL
        tramoRepository.save(tramo);
        
        // Redirigimos a la misma página limpia para que puedas agregar la siguiente ruta
        return "redirect:/tramos/nuevo";
    }
}
package Poyecto.Nexus.game.controller;

import Poyecto.Nexus.game.entity.Sede;
import Poyecto.Nexus.game.repository.SedeRepository;
import Poyecto.Nexus.game.repository.TramoRepository;
import Poyecto.Nexus.game.service.DijkstraService;
import Poyecto.Nexus.game.service.DijkstraService.ResultadoRuta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class EncomiendaController {

    @Autowired
    private SedeRepository sedeRepository;

    @Autowired
    private TramoRepository tramoRepository;

    @Autowired
    private DijkstraService dijkstraService;

    @GetMapping("/simular")
    public String verSimulacion(Model model) {
        model.addAttribute("listaSedes", sedeRepository.findAll());
        return "simular_ruta";
    }

    @PostMapping("/simular/calcular")
    public String calcularRuta(
            @RequestParam("origenId") Long origenId,
            @RequestParam("destinosId") List<Long> destinosId,
            Model model) {

        List<Sede> todasSedes = sedeRepository.findAll();
        
        // Llamada a la nueva lógica de enrutamiento múltiple
        ResultadoRuta resultado = dijkstraService.calcularRutaMultiplesDestinos(
                todasSedes,
                tramoRepository.findAll(),
                origenId,
                destinosId
        );

        model.addAttribute("listaSedes", todasSedes);
        model.addAttribute("origenId", origenId);
        model.addAttribute("destinosId", destinosId);
        model.addAttribute("rutaOptima", resultado.rutaSedes);
        model.addAttribute("distanciaTotal", resultado.distanciaTotalKm);

        return "simular_ruta";
    }
}
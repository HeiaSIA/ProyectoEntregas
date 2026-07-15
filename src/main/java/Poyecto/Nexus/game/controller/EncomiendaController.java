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

    // Carga inicial del formulario de simulación
    @GetMapping("/simular")
    public String verSimulacion(Model model) {
        model.addAttribute("listaSedes", sedeRepository.findAll());
        return "simular_ruta";
    }

    // Procesa y calcula Dijkstra
    @PostMapping("/simular/calcular")
    public String calcularRuta(
            @RequestParam("origenId") Long origenId,
            @RequestParam("destinoId") Long destinoId,
            Model model) {

        List<Sede> todasSedes = sedeRepository.findAll();
        ResultadoRuta resultado = dijkstraService.calcularRutaOptima(
                todasSedes,
                tramoRepository.findAll(),
                origenId,
                destinoId
        );

        model.addAttribute("listaSedes", todasSedes);
        model.addAttribute("origenId", origenId);
        model.addAttribute("destinoId", destinoId);
        model.addAttribute("rutaOptima", resultado.rutaSedes);
        model.addAttribute("distanciaTotal", resultado.distanciaTotalKm);

        return "simular_ruta";
    }
}
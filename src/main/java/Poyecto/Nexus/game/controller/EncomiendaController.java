package Poyecto.Nexus.game.controller; 

import Poyecto.Nexus.game.entity.Encomienda;
import Poyecto.Nexus.game.entity.Producto;
import Poyecto.Nexus.game.entity.Sede;
import Poyecto.Nexus.game.entity.Usuario;
import Poyecto.Nexus.game.service.SedeService;
import Poyecto.Nexus.game.service.TramoService;
import Poyecto.Nexus.game.service.DijkstraService;
import Poyecto.Nexus.game.service.DijkstraService.ResultadoRuta;
import Poyecto.Nexus.game.service.EncomiendaService;
import Poyecto.Nexus.game.service.UsuarioService;
import Poyecto.Nexus.game.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList; 
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class EncomiendaController {

    private final SedeService sedeService;
    private final TramoService tramoService;
    private final DijkstraService dijkstraService;
    private final EncomiendaService encomiendaService;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;

    public EncomiendaController(SedeService sedeService, 
                                TramoService tramoService, 
                                DijkstraService dijkstraService,
                                EncomiendaService encomiendaService,
                                UsuarioService usuarioService,
                                ProductoService productoService) {
        this.sedeService = sedeService;
        this.tramoService = tramoService;
        this.dijkstraService = dijkstraService;
        this.encomiendaService = encomiendaService;
        this.usuarioService = usuarioService;
        this.productoService = productoService;
    }

    // ==========================================
    //         1. MÓDULO DE SIMULACIÓN (CONSOLIDADO)
    // ==========================================

    @GetMapping("/simular")
    public String verSimulacion(Model model) {
        model.addAttribute("listaSedes", sedeService.listarTodas());
        model.addAttribute("listaEncomiendas", encomiendaService.listarTodas()); 
        return "simular_ruta";
    }

    @PostMapping("/simular/calcular")
    public String calcularRuta(
            @RequestParam("origenId") Long origenId,
            @RequestParam(value = "encomiendasIds", required = false) List<Long> encomiendasIds,
            Model model) {

        List<Sede> todasSedes = sedeService.listarTodas();
        List<Encomienda> todasEncomiendas = encomiendaService.listarTodas();

        List<Sede> rutaFinal = new ArrayList<>();
        double distanciaTotal = 0.0;

        if (encomiendasIds != null && !encomiendasIds.isEmpty()) {
            
            // Filtramos solo las encomiendas seleccionadas por el usuario
            List<Encomienda> encomiendasSeleccionadas = todasEncomiendas.stream()
                    .filter(e -> encomiendasIds.contains(e.getIdEncomienda()))
                    .collect(Collectors.toList());

            // 1. Extraer ÚNICAMENTE los puntos de RECOLECCIÓN (Orígenes) sin duplicados
            List<Long> recoleccionesIds = encomiendasSeleccionadas.stream()
                    .map(e -> e.getSedeOrigen().getIdSede())
                    .distinct()
                    .collect(Collectors.toList());

            // 2. Extraer ÚNICAMENTE los puntos de ENTREGA (Destinos) sin duplicados
            List<Long> entregasIds = encomiendasSeleccionadas.stream()
                    .map(e -> e.getSedeDestino().getIdSede())
                    .distinct()
                    .collect(Collectors.toList());

            // --- FASE 1: RECOLECCIÓN ---
            // El camión viaja desde su base para recoger todos los paquetes
            ResultadoRuta faseRecoleccion = dijkstraService.calcularRutaMultiplesDestinos(
                    todasSedes,
                    tramoService.listarTodosLosTramos(),
                    origenId,
                    recoleccionesIds
            );

            if (faseRecoleccion != null && !faseRecoleccion.rutaSedes.isEmpty()) {
                rutaFinal.addAll(faseRecoleccion.rutaSedes);
                distanciaTotal += faseRecoleccion.distanciaTotalKm;

                // Determinamos en qué ciudad quedó el camión tras recoger todo
                Long ubicacionActualCamion = rutaFinal.get(rutaFinal.size() - 1).getIdSede();

                // --- FASE 2: ENTREGA ---
                // El camión viaja desde la última recolección hacia todos los destinos
                ResultadoRuta faseEntrega = dijkstraService.calcularRutaMultiplesDestinos(
                        todasSedes,
                        tramoService.listarTodosLosTramos(),
                        ubicacionActualCamion,
                        entregasIds
                );

                if (faseEntrega != null && !faseEntrega.rutaSedes.isEmpty()) {
                    // Evitamos duplicar la ciudad pivote (donde terminó la Fase 1 y empieza la Fase 2)
                    List<Sede> subRutaEntrega = new ArrayList<>(faseEntrega.rutaSedes);
                    subRutaEntrega.remove(0); 
                    rutaFinal.addAll(subRutaEntrega);
                    distanciaTotal += faseEntrega.distanciaTotalKm;
                }
            }
        }
        
        // Redondeamos a 2 decimales para evitar números infinitos en la interfaz
        distanciaTotal = Math.round(distanciaTotal * 100.0) / 100.0;

        model.addAttribute("listaSedes", todasSedes);
        model.addAttribute("listaEncomiendas", todasEncomiendas);
        model.addAttribute("origenId", origenId);
        model.addAttribute("encomiendasIds", encomiendasIds);
        model.addAttribute("rutaOptima", rutaFinal.isEmpty() ? null : rutaFinal);
        model.addAttribute("distanciaTotal", distanciaTotal);

        return "simular_ruta";
    }

    // ==========================================
    //         2. MÓDULO DE COMPRAS REALES
    // ==========================================

    @PostMapping("/comprar")
    public String procesarCompraWeb(
            @RequestParam("idUsuario") Long idUsuario,
            @RequestParam("idSedeDestino") Long idSedeDestino,
            @RequestParam("idProducto") Long idProducto,
            @RequestParam("cantidad") Integer cantidad,
            RedirectAttributes flash) {

        try {
            Usuario cliente = usuarioService.buscarPorId(idUsuario);
            Sede sedeDestino = sedeService.buscarPorId(idSedeDestino);
            Producto producto = productoService.buscarPorId(idProducto);

            Encomienda encomienda = encomiendaService.procesarCompra(cliente, sedeDestino, producto, cantidad);

            flash.addFlashAttribute("mensajeExito", "¡Compra exitosa! Tu pedido está PENDIENTE en bodega. Distancia a recorrer: " + encomienda.getDistanciaTotalCalculada() + " Km.");
            return "redirect:/mapa-seguimiento?idEncomienda=" + encomienda.getIdEncomienda();

        } catch (Exception e) {
            flash.addFlashAttribute("mensajeError", "Error procesando la compra: " + e.getMessage());
            return "redirect:/catalogo"; 
        }
    }

    // ==========================================
    //         3. MÓDULO DE SEGUIMIENTO
    // ==========================================

    @GetMapping("/mapa-seguimiento")
    public String verSeguimientoPedido(@RequestParam("idEncomienda") Long idEncomienda, Model model, RedirectAttributes flash) {
        try {
            Encomienda encomienda = encomiendaService.buscarPorId(idEncomienda);
            List<Sede> todasSedes = sedeService.listarTodas();
            List<Long> destinoIdList = List.of(encomienda.getSedeDestino().getIdSede());
            
            ResultadoRuta resultado = dijkstraService.calcularRutaMultiplesDestinos(
                    todasSedes,
                    tramoService.listarTodosLosTramos(),
                    encomienda.getSedeOrigen().getIdSede(),
                    destinoIdList
            );
            
            model.addAttribute("encomienda", encomienda);
            model.addAttribute("sedeOrigen", encomienda.getSedeOrigen());
            model.addAttribute("sedeDestino", encomienda.getSedeDestino());
            model.addAttribute("rutaCompleta", resultado.rutaSedes); 
            model.addAttribute("todasSedes", todasSedes);
            
            return "mapa_seguimiento"; 
            
        } catch (Exception e) {
            flash.addFlashAttribute("mensajeError", "No se pudo acceder al rastreo: " + e.getMessage());
            return "redirect:/catalogo";
        }
    }
}
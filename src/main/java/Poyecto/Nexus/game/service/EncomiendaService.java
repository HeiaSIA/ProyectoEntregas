package Poyecto.Nexus.game.service;

import Poyecto.Nexus.game.entity.*;
import Poyecto.Nexus.game.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EncomiendaService {

    @Autowired
    private EncomiendaRepository encomiendaRepository;

    @Autowired
    private DetalleEncomiendaRepository detalleRepository;

    @Autowired
    private InventarioSedeRepository inventarioRepository;

    @Autowired
    private DijkstraService dijkstraService;

    @Autowired
    private SedeRepository sedeRepository;

    @Autowired
    private TramoRepository tramoRepository;

    @Transactional // Si algo falla, revierte todo para no dañar la BD
    public Encomienda procesarCompra(Usuario cliente, Sede sedeDestino, Producto productoComprado, Integer cantidad) {
        
        // 1. Buscar de qué sede va a salir el producto (donde haya stock)
        List<InventarioSede> inventarios = inventarioRepository.findByProductoAndStockDisponibleGreaterThanEqual(productoComprado, cantidad);
        
        if (inventarios.isEmpty()) {
            throw new RuntimeException("No hay stock disponible en ninguna sede para realizar la compra.");
        }

        // Tomamos la primera sede que tenga stock suficiente
        InventarioSede inventarioOrigen = inventarios.get(0);
        Sede sedeOrigen = inventarioOrigen.getSede();

        // 2. Calcular la ruta con tu algoritmo de Dijkstra
        List<Sede> todasSedes = sedeRepository.findAll();
        List<Tramo> todosTramos = tramoRepository.findAll();

        // Llamamos al método real de tu DijkstraService
        DijkstraService.ResultadoRuta resultado = dijkstraService.calcularRutaOptima(
                todasSedes, 
                todosTramos, 
                sedeOrigen.getIdSede(), 
                sedeDestino.getIdSede()
        );

        // Extraemos la distancia total calculada del objeto ResultadoRuta
        Double distanciaCalculada = resultado.distanciaTotalKm;

        // 3. Crear y guardar la Encomienda
        Encomienda encomienda = new Encomienda();
        encomienda.setUsuario(cliente);
        encomienda.setSedeOrigen(sedeOrigen);
        encomienda.setSedeDestino(sedeDestino);
        encomienda.setConductor(null); // Todavía no hay camión asignado
        encomienda.setFechaEmision(LocalDateTime.now());
        encomienda.setDistanciaTotalCalculada(distanciaCalculada);
        encomienda.setEstado("PENDIENTE");
        
        encomienda = encomiendaRepository.save(encomienda);

        // 4. Crear y guardar el Detalle de la Encomienda
        DetalleEncomienda detalle = new DetalleEncomienda();
        detalle.setEncomienda(encomienda);
        detalle.setProducto(productoComprado);
        detalle.setCantidad(cantidad);
        
        // Cálculo del subtotal en base al precio base y cantidad
        detalle.setSubtotal(productoComprado.getPrecioBase() * cantidad); 
        
        detalleRepository.save(detalle);

        // 5. Restar el stock y actualizar el inventario de la sede de origen
        inventarioOrigen.setStockDisponible(inventarioOrigen.getStockDisponible() - cantidad);
        inventarioRepository.save(inventarioOrigen);

        return encomienda;
    }

    // ========================================================
    // NUEVO MÉTODO COMPLEMENTARIO PARA EL MAPA DE SEGUIMIENTO
    // ========================================================
    public Encomienda buscarPorId(Long id) {
        return encomiendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Encomienda no encontrada con el ID: " + id));
    }

    // ========================================================
    // EL MÉTODO QUE TE HACÍA FALTA PARA EL CONTROLADOR 
    // ========================================================
    public List<Encomienda> listarTodas() {
        return encomiendaRepository.findAll();
    }
}
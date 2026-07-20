package Poyecto.Nexus.game.service;

import java.util.List;
import org.springframework.stereotype.Service;
import Poyecto.Nexus.game.entity.Producto;
import Poyecto.Nexus.game.entity.CategoriaComponente;
import Poyecto.Nexus.game.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    // Inyección limpia por constructor (Reemplaza al @Autowired)
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Obtener todos los productos para la tienda 
    public List<Producto> obtenerCatalogoTienda() {
        return productoRepository.findByDisponibleCatalogoTrue();
    }

    // Obtener componentes específicos 
    public List<Producto> obtenerComponentesParaArmar(CategoriaComponente categoria) {
        return productoRepository.findByDisponibleArmarPcTrueAndCategoriaComponente(categoria);
    }
    
    // Método de Guardado
    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    // ==========================================
    //   MÉTODO NUEVO PARA PROCESAR LA COMPRA
    // ==========================================
    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Producto no encontrado con ID: " + id));
    }
}
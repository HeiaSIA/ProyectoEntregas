package Poyecto.Nexus.game.service;

import java.util.List;
import Poyecto.Nexus.game.entity.Producto;
import Poyecto.Nexus.game.entity.CategoriaComponente;

public interface ProductoService {
    // Obtener todos los productos para la tienda 
    List<Producto> obtenerCatalogoTienda();

    // Obtener componentes específicos 
    List<Producto> obtenerComponentesParaArmar(CategoriaComponente categoria);
    
    // Método de Guardado
    Producto guardarProducto(Producto producto);

	List<Producto> listarTodos();
}
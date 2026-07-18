package Poyecto.Nexus.game.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import Poyecto.Nexus.game.entity.Producto;
import Poyecto.Nexus.game.entity.CategoriaComponente;
import Poyecto.Nexus.game.repository.ProductoRepository;
import Poyecto.Nexus.game.service.ProductoService;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<Producto> obtenerCatalogoTienda() {
        return productoRepository.findByDisponibleCatalogoTrue();
    }

    @Override
    public List<Producto> obtenerComponentesParaArmar(CategoriaComponente categoria) {
        return productoRepository.findByDisponibleArmarPcTrueAndCategoriaComponente(categoria);
    }

    @Override
    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

	@Override
	public List<Producto> listarTodos() {
		return productoRepository.findAll();
	}
}
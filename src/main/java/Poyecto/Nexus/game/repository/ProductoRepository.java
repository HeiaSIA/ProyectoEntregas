package Poyecto.Nexus.game.repository;

import Poyecto.Nexus.game.entity.Producto;
import java.util.List;
import Poyecto.Nexus.game.entity.CategoriaComponente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
	// 1. Traer solo los productos que deben mostrarse en la tienda normal
    List<Producto> findByDisponibleCatalogoTrue();

    // 2. Componentes especificos 
    List<Producto> findByDisponibleArmarPcTrueAndCategoriaComponente(CategoriaComponente categoriaComponente);

}
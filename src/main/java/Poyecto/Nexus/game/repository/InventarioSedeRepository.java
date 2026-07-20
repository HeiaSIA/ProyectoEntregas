package Poyecto.Nexus.game.repository;

import Poyecto.Nexus.game.entity.InventarioSede;
import Poyecto.Nexus.game.entity.Producto;
import Poyecto.Nexus.game.entity.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InventarioSedeRepository extends JpaRepository<InventarioSede, Long> {
    
    // El que usamos antes para el panel:
    Optional<InventarioSede> findBySedeAndProducto(Sede sede, Producto producto);

    // EL QUE TE FALTA PARA LA COMPRA (El que está haciendo chillar a tu Service):
    List<InventarioSede> findByProductoAndStockDisponibleGreaterThanEqual(Producto producto, Integer cantidad);
}
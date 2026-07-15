package Poyecto.Nexus.game.repository;

import Poyecto.Nexus.game.entity.Rols;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RolsRepository extends JpaRepository<Rols, Long> {
    Optional<Rols> findByNombre(String nombre); // O el campo que identifique al rol
}
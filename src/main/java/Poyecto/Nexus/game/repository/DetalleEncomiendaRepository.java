package Poyecto.Nexus.game.repository;

import Poyecto.Nexus.game.entity.DetalleEncomienda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleEncomiendaRepository extends JpaRepository<DetalleEncomienda, Long> {
}
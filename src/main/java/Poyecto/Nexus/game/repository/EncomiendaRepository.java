package Poyecto.Nexus.game.repository;

import Poyecto.Nexus.game.entity.Encomienda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EncomiendaRepository extends JpaRepository<Encomienda, Long> {
    // Te servirá luego para que el Admin busque los paquetes "PENDIENTES"
    List<Encomienda> findByEstado(String estado); 
}
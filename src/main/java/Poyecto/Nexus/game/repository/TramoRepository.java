package Poyecto.Nexus.game.repository;

import Poyecto.Nexus.game.entity.Tramo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TramoRepository extends JpaRepository<Tramo, Long> {
}
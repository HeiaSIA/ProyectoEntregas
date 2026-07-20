package Poyecto.Nexus.game.service;

import Poyecto.Nexus.game.entity.Tramo;
import Poyecto.Nexus.game.repository.TramoRepository;
import Poyecto.Nexus.game.repository.SedeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TramoService {

    private final TramoRepository tramoRepository;
    private final SedeRepository sedeRepository;

    public TramoService(TramoRepository tramoRepository, SedeRepository sedeRepository) {
        this.tramoRepository = tramoRepository;
        this.sedeRepository = sedeRepository;
    }

    public List<Tramo> listarTodosLosTramos() {
        return tramoRepository.findAll();
    }

    public List<?> listarTodasLasSedes() { 
        return sedeRepository.findAll();
    }

    public Tramo buscarPorId(Long id) {
        return tramoRepository.findById(id).orElse(null);
    }

    public void guardar(Tramo tramo) {
        tramoRepository.save(tramo);
    }

    public void eliminar(Long id) {
        tramoRepository.deleteById(id);
    }

    public boolean existeTramoNuevo(Tramo tramo) {
        return tramoRepository.findAll().stream().anyMatch(t -> 
            t.getSedeOrigen().getIdSede().equals(tramo.getSedeOrigen().getIdSede()) && 
            t.getSedeDestino().getIdSede().equals(tramo.getSedeDestino().getIdSede())
        );
    }

    public boolean existeOtroTramoIdentico(Tramo tramo) {
        return tramoRepository.findAll().stream().anyMatch(t -> 
            !t.getIdTramo().equals(tramo.getIdTramo()) &&
            t.getSedeOrigen().getIdSede().equals(tramo.getSedeOrigen().getIdSede()) && 
            t.getSedeDestino().getIdSede().equals(tramo.getSedeDestino().getIdSede())
        );
    }
}
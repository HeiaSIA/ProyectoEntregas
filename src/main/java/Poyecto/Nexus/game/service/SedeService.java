package Poyecto.Nexus.game.service;

import Poyecto.Nexus.game.entity.Sede;
import Poyecto.Nexus.game.repository.SedeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SedeService {

    private final SedeRepository sedeRepository;

    public SedeService(SedeRepository sedeRepository) {
        this.sedeRepository = sedeRepository;
    }

    public List<Sede> listarTodas() {
        return sedeRepository.findAll();
    }

    public Sede buscarPorId(Long id) {
        return sedeRepository.findById(id).orElse(null);
    }

    public void guardar(Sede sede) {
        sedeRepository.save(sede);
    }

    public void eliminar(Long id) {
        sedeRepository.deleteById(id);
    }

    public boolean existeSedeNueva(Sede sede) {
        return sedeRepository.findAll().stream().anyMatch(s -> 
            s.getNombreCiudad().equalsIgnoreCase(sede.getNombreCiudad())
        );
    }

    public boolean existeOtraSedeIdentica(Sede sede) {
        return sedeRepository.findAll().stream().anyMatch(s -> 
            !s.getIdSede().equals(sede.getIdSede()) &&
            s.getNombreCiudad().equalsIgnoreCase(sede.getNombreCiudad())
        );
    }
}
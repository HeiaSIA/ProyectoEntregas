package Poyecto.Nexus.game.service;

import Poyecto.Nexus.game.entity.Usuario;
import Poyecto.Nexus.game.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // Inyección por constructor (Arquitectura limpia)
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Usuario no encontrado con ID: " + id));
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
    
    // Aquí puedes agregar después métodos como guardarUsuario(), validarLogin(), etc.
}
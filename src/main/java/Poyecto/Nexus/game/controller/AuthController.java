package Poyecto.Nexus.game.controller;

import Poyecto.Nexus.game.entity.Usuario;
import Poyecto.Nexus.game.entity.Rols;
import Poyecto.Nexus.game.repository.UsuarioRepository;
import Poyecto.Nexus.game.repository.RolsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Evita bloqueos de seguridad de red (CORS) desde tu HTML local
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolsRepository rolsRepository;

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody Map<String, String> datos) {
        try {
            String nombre = datos.get("nombre");
            String email = datos.get("email");
            String password = datos.get("password");

            // 1. Validar si el correo ya existe en la base de datos
            if (usuarioRepository.existsByEmail(email)) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "El correo ya está registrado en Nexus."));
            }

            // 2. Buscar el rol ID 1. Si no existe en tu tabla "roles", lo creamos de forma segura
            Rols rolPorDefecto = rolsRepository.findById(1L).orElseGet(() -> {
                Rols nuevoRol = new Rols();
                nuevoRol.setNombre("CLIENTE"); // Nombre obligatorio para evitar el nullable = false
                return rolsRepository.save(nuevoRol); // Se guarda y la base de datos le asignará el ID 1 de forma automática
            });

            // 3. Crear el nuevo usuario y asociarle el rol seguro
            Usuario nuevoUsuario = new Usuario(nombre, email, password, rolPorDefecto);

            // 4. Guardar definitivamente en la base de datos
            usuarioRepository.save(nuevoUsuario);

            return ResponseEntity.ok(Map.of("mensaje", "¡Cuenta creada con éxito gamer!"));

        } catch (Exception e) {
            // Imprime el error real en la consola de STS para que no andemos a ciegas
            e.printStackTrace(); 
            return ResponseEntity.status(500).body(Map.of("mensaje", "Error interno en el servidor: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> iniciarSesion(@RequestBody Map<String, String> datos) {
        String email = datos.get("email");
        String password = datos.get("password");

        var usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent() && usuarioOpt.get().getPassword().equals(password)) {
            return ResponseEntity.ok(Map.of(
                "mensaje", "Acceso concedido",
                "nombre", usuarioOpt.get().getNombre()
            ));
        }

        return ResponseEntity.status(401).body(Map.of("mensaje", "Credenciales incorrectas. Inténtalo de nuevo."));
    }
}
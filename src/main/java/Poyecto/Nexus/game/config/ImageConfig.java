package Poyecto.Nexus.game.config; 

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class ImageConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. Buscamos la ruta física real de la carpeta donde se guardan tus fotos en el disco duro
        Path rutaImagenes = Paths.get("src/main/resources/static/imagenes");
        String rutaAbsoluta = rutaImagenes.toFile().getAbsolutePath();

        // 2. Mapeamos la URL "/imagenes/**" directamente a esa ruta física
        // El prefijo "file:" le dice a Spring que busque en el disco local y no dentro del servidor empaquetado
        registry.addResourceHandler("/imagenes/**")
                .addResourceLocations("file:" + rutaAbsoluta + "/");
    }
}


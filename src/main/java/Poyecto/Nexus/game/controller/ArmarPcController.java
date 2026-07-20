package Poyecto.Nexus.game.controller;

import Poyecto.Nexus.game.entity.CategoriaComponente;
import Poyecto.Nexus.game.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/armar-pc")
public class ArmarPcController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public String mostrarInterfazArmado(Model model) {
        // Filtramos y enviamos cada categoría por separado a la vista HTML
        model.addAttribute("procesadores", 
            productoService.obtenerComponentesParaArmar(CategoriaComponente.PROCESADOR));
            
        model.addAttribute("tarjetasVideo", 
            productoService.obtenerComponentesParaArmar(CategoriaComponente.TARJETA_GRAFICA));
            
        model.addAttribute("memoriasRam", 
            productoService.obtenerComponentesParaArmar(CategoriaComponente.MEMORIA_RAM));
            
        model.addAttribute("almacenamientos", 
            productoService.obtenerComponentesParaArmar(CategoriaComponente.ALMACENAMIENTO));
            
        model.addAttribute("teclado", 
            productoService.obtenerComponentesParaArmar(CategoriaComponente.TECLADO));
            
        model.addAttribute("chasis", 
                productoService.obtenerComponentesParaArmar(CategoriaComponente.CHASIS));
        
        model.addAttribute("refrigeraciones", 
            productoService.obtenerComponentesParaArmar(CategoriaComponente.REFRIGERACION));
            
        model.addAttribute("pantallas", 
                productoService.obtenerComponentesParaArmar(CategoriaComponente.PANTALLA));
        
        model.addAttribute("mouses", 
                productoService.obtenerComponentesParaArmar(CategoriaComponente.MOUSE));
        
        model.addAttribute("auriculares", 
                productoService.obtenerComponentesParaArmar(CategoriaComponente.AURICULARES));
        
        model.addAttribute("otros", 
                productoService.obtenerComponentesParaArmar(CategoriaComponente.OTROS));

       
        return "armar_pc"; 
    }
}
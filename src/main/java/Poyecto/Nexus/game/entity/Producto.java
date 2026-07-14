package Poyecto.Nexus.game.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProducto;

    @Column(nullable = false)
    private String nombre; // Ej: Laptop ASUS ROG Strix G17

    @Column(columnDefinition = "TEXT")
    private String descripcion; // Ej: Configurada con RTX 4060 y Ryzen 9 serie 7000

    @Column(nullable = false)
    private Double precioBase;

    // Constructor vacío (Obligatorio para Spring Boot)
    public Producto() {}

    public Producto(String nombre, String descripcion, Double precioBase) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioBase = precioBase;
    }

    // Getters y Setters
    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(Double precioBase) {
        this.precioBase = precioBase;
    }
}

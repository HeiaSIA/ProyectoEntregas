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

    @Column(nullable = false)
    private String imagen; // Ej: laptop_asus.jpg

    @Column(nullable = false)
    private Integer stock = 0; // NUEVO CAMPO: Inicializado en 0 por defecto

    // Constructor vacío (Obligatorio para Spring Boot)
    public Producto() {
    }

    // Constructor con parámetros (Actualizado con stock)
    public Producto(String nombre, String descripcion, Double precioBase, String imagen, Integer stock) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioBase = precioBase;
        this.imagen = imagen;
        this.stock = stock;
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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    // NUEVO GETTER Y SETTER PARA STOCK
    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
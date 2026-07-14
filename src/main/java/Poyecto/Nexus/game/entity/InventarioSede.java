package Poyecto.Nexus.game.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "inventario_sedes")
public class InventarioSede {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInventario;

    // Relación: ¿En qué ciudad (Sede) está este stock?
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_sede", nullable = false)
    private Sede sede;

    // Relación: ¿Qué componente físico es?
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    // La cantidad real de cajas en la bodega
    @Column(nullable = false)
    private Integer stockDisponible;

    // Constructor vacío
    public InventarioSede() {}

    public InventarioSede(Sede sede, Producto producto, Integer stockDisponible) {
        this.sede = sede;
        this.producto = producto;
        this.stockDisponible = stockDisponible;
    }

    // Getters y Setters
    public Long getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(Long idInventario) {
        this.idInventario = idInventario;
    }

    public Sede getSede() {
        return sede;
    }

    public void setSede(Sede sede) {
        this.sede = sede;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getStockDisponible() {
        return stockDisponible;
    }

    public void setStockDisponible(Integer stockDisponible) {
        this.stockDisponible = stockDisponible;
    }
}
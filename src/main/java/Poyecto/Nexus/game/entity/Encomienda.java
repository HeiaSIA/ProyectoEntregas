package Poyecto.Nexus.game.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "encomiendas")
public class Encomienda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEncomienda;

    // ¿Quién hizo la compra? (Conecta con la tabla del Usuario 1)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    // ¿Desde qué bodega física sale el paquete? (Conecta con la tabla del Usuario 2)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_sede_origen", nullable = false)
    private Sede sedeOrigen;

    // ¿A dónde lo pidió el cliente?
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_sede_destino", nullable = false)
    private Sede sedeDestino;

    @Column(nullable = false)
    private LocalDateTime fechaEmision;

    // El resultado matemático exacto de tu algoritmo de caminos mínimos
    @Column(nullable = false)
    private Double distanciaTotalCalculada;

    // Ej: "PENDIENTE", "EN_TRANSITO", "ENTREGADO"
    @Column(nullable = false)
    private String estado;

    public Encomienda() {}

    // Getters y Setters
    public Long getIdEncomienda() {
        return idEncomienda;
    }

    public void setIdEncomienda(Long idEncomienda) {
        this.idEncomienda = idEncomienda;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Sede getSedeOrigen() {
        return sedeOrigen;
    }

    public void setSedeOrigen(Sede sedeOrigen) {
        this.sedeOrigen = sedeOrigen;
    }

    public Sede getSedeDestino() {
        return sedeDestino;
    }

    public void setSedeDestino(Sede sedeDestino) {
        this.sedeDestino = sedeDestino;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public Double getDistanciaTotalCalculada() {
        return distanciaTotalCalculada;
    }

    public void setDistanciaTotalCalculada(Double distanciaTotalCalculada) {
        this.distanciaTotalCalculada = distanciaTotalCalculada;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}

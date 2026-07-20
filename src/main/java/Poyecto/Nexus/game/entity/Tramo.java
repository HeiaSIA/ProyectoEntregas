package Poyecto.Nexus.game.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tramos")
public class Tramo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTramo;

    // Relación con la Sede de Origen
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_sede_origen", nullable = false)
    private Sede sedeOrigen;

    // Relación con la Sede de Destino
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_sede_destino", nullable = false)
    private Sede sedeDestino;

    // El peso de la arista (Distancia exacta en kilómetros)
    @Column(nullable = false)
    private Double distanciaKm;

    public Tramo() {}

    public Tramo(Sede sedeOrigen, Sede sedeDestino, Double distanciaKm) {
        this.sedeOrigen = sedeOrigen;
        this.sedeDestino = sedeDestino;
        this.distanciaKm = distanciaKm;
    }

    public Long getIdTramo() {
        return idTramo;
    }

    public void setIdTramo(Long idTramo) {
        this.idTramo = idTramo;
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

    public Double getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(Double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }
}
package Poyecto.Nexus.game.service;

import Poyecto.Nexus.game.entity.Sede;
import Poyecto.Nexus.game.entity.Tramo;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DijkstraService {

    // Nodos dinámicos (Criterio 3 de la rúbrica)
    public static class Nodo implements Comparable<Nodo> {
        public Long idSede;
        public String nombreCiudad;
        public double distanciaMinima = Double.MAX_VALUE;
        public Nodo nodoPrevio;
        public List<Arista> adyacentes = new ArrayList<>();

        public Nodo(Long idSede, String nombreCiudad) {
            this.idSede = idSede;
            this.nombreCiudad = nombreCiudad;
        }

        @Override
        public int compareTo(Nodo otro) {
            return Double.compare(this.distanciaMinima, otro.distanciaMinima);
        }
    }

    // Aristas (Conexiones con peso en Km)
    public static class Arista {
        public Nodo destino;
        public double peso;

        public Arista(Nodo destino, double peso) {
            this.destino = destino;
            this.peso = peso;
        }
    }

    // Contenedor para enviar el resultado a la vista
    public static class ResultadoRuta {
        public List<Sede> rutaSedes;
        public double distanciaTotalKm;

        public ResultadoRuta(List<Sede> rutaSedes, double distanciaTotalKm) {
            this.rutaSedes = rutaSedes;
            this.distanciaTotalKm = Math.round(distanciaTotalKm * 100.0) / 100.0;
        }
    }

    // Método 1: Dijkstra Nativo (Criterio 4 de la rúbrica)
    public ResultadoRuta calcularRutaOptima(List<Sede> sedesDb, List<Tramo> tramosDb, Long idOrigen, Long idDestino) {
        Map<Long, Nodo> mapaNodos = new HashMap<>();
        
        for (Sede s : sedesDb) {
            mapaNodos.put(s.getIdSede(), new Nodo(s.getIdSede(), s.getNombreCiudad()));
        }

        for (Tramo t : tramosDb) {
            Nodo origen = mapaNodos.get(t.getSedeOrigen().getIdSede());
            Nodo destino = mapaNodos.get(t.getSedeDestino().getIdSede());
            if (origen != null && destino != null) {
                origen.adyacentes.add(new Arista(destino, t.getDistanciaKm()));
                destino.adyacentes.add(new Arista(origen, t.getDistanciaKm()));
                
            }
        }

        Nodo nodoInicio = mapaNodos.get(idOrigen);
        Nodo nodoFin = mapaNodos.get(idDestino);

        if (nodoInicio == null || nodoFin == null) {
            return new ResultadoRuta(new ArrayList<>(), 0.0);
        }

        nodoInicio.distanciaMinima = 0.0;
        PriorityQueue<Nodo> cola = new PriorityQueue<>();
        cola.add(nodoInicio);

        while (!cola.isEmpty()) {
            Nodo u = cola.poll();
            
            for (Arista a : u.adyacentes) {
                Nodo v = a.destino;
                double peso = a.peso;
                double distanciaAlterna = u.distanciaMinima + peso;
                
                if (distanciaAlterna < v.distanciaMinima) {
                    cola.remove(v);
                    v.distanciaMinima = distanciaAlterna;
                    v.nodoPrevio = u;
                    cola.add(v);
                }
            }
        }
        
        List<Sede> rutaSedes = new ArrayList<>();
        for (Nodo n = nodoFin; n != null; n = n.nodoPrevio) {
            Sede sedeDb = null;
            for (Sede s : sedesDb) {
                if (s.getIdSede().equals(n.idSede)) {
                    sedeDb = s;
                    break;
                }
            }
            if (sedeDb != null) {
                rutaSedes.add(sedeDb);
            }
        }
        Collections.reverse(rutaSedes);

        if (nodoFin.distanciaMinima == Double.MAX_VALUE) {
            return new ResultadoRuta(new ArrayList<>(), 0.0);
        }

        return new ResultadoRuta(rutaSedes, nodoFin.distanciaMinima);
    }

    // Método 2: Resolución de Múltiples Destinos (Algoritmo Voraz basado en Dijkstra)
    public ResultadoRuta calcularRutaMultiplesDestinos(List<Sede> sedesDb, List<Tramo> tramosDb, Long idOrigen, List<Long> idsDestinos) {
        List<Sede> rutaFinal = new ArrayList<>();
        double distanciaTotal = 0.0;

        Long actual = idOrigen;
        List<Long> pendientes = new ArrayList<>(idsDestinos);

        Sede sedeOrigen = sedesDb.stream()
                .filter(s -> s.getIdSede().equals(idOrigen))
                .findFirst()
                .orElse(null);

        if (sedeOrigen != null) {
            rutaFinal.add(sedeOrigen);
        }

        while (!pendientes.isEmpty()) {
            Long masCercano = null;
            ResultadoRuta mejorTramo = null;
            double minDistancia = Double.MAX_VALUE;

            for (Long destinoId : pendientes) {
                ResultadoRuta tramoCandidato = calcularRutaOptima(sedesDb, tramosDb, actual, destinoId);
                
                if (tramoCandidato != null && !tramoCandidato.rutaSedes.isEmpty() && tramoCandidato.distanciaTotalKm < minDistancia) {
                    minDistancia = tramoCandidato.distanciaTotalKm;
                    mejorTramo = tramoCandidato;
                    masCercano = destinoId;
                }
            }

            if (masCercano == null) {
                break; // No hay ruta a los destinos restantes
            }

            List<Sede> subRuta = mejorTramo.rutaSedes;
            
            if (!rutaFinal.isEmpty() && !subRuta.isEmpty()) {
                subRuta.remove(0); // Evitar duplicar el nodo de conexión
            }
            
            rutaFinal.addAll(subRuta);
            distanciaTotal += mejorTramo.distanciaTotalKm;

            actual = masCercano;
            pendientes.remove(masCercano);
        }

        return new ResultadoRuta(rutaFinal, distanciaTotal);
    }
}
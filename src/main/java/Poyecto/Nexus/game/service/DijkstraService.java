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
        public List<Arista> adyacentes = new ArrayList<>(); // Lista de adyacencia dinámica

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
            this.distanciaTotalKm = Math.round(distanciaTotalKm * 100.0) / 100.0; // Redondear a 2 decimales
        }
    }

    public ResultadoRuta calcularRutaOptima(List<Sede> sedesDb, List<Tramo> tramosDb, Long idOrigen, Long idDestino) {
        Map<Long, Nodo> mapaNodos = new HashMap<>();
        
        // 1. Inicializar nodos
        for (Sede s : sedesDb) {
            mapaNodos.put(s.getIdSede(), new Nodo(s.getIdSede(), s.getNombreCiudad()));
        }

        // 2. Construir lista de adyacencia leyendo las conexiones existentes
        for (Tramo t : tramosDb) {
            Nodo origen = mapaNodos.get(t.getSedeOrigen().getIdSede());
            Nodo destino = mapaNodos.get(t.getSedeDestino().getIdSede());
            if (origen != null && destino != null) {
                // El grafo es dirigido por defecto, si fuera bidireccional agregas la inversa también.
                origen.adyacentes.add(new Arista(destino, t.getDistanciaKm()));
            }
        }

        Nodo nodoInicio = mapaNodos.get(idOrigen);
        Nodo nodoFin = mapaNodos.get(idDestino);

        if (nodoInicio == null || nodoFin == null) {
            return new ResultadoRuta(new ArrayList<>(), 0.0);
        }

        // 3. Ejecutar algoritmo de Dijkstra nativo (Criterio 4)
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
        
        
     // 4. Reconstruir el camino óptimo
        List<Sede> rutaSedes = new ArrayList<>();
        for (Nodo n = nodoFin; n != null; n = n.nodoPrevio) {
            Sede sedeDb = null;
            // Buscamos la sede correspondiente en la lista de la base de datos
            for (Sede s : sedesDb) {
                if (s.getIdSede().equals(n.idSede)) {
                    sedeDb = s;
                    break; // Ya la encontramos, salimos del ciclo interno
                }
            }
            if (sedeDb != null) {
                rutaSedes.add(sedeDb);
            }
        }
        Collections.reverse(rutaSedes);

        // Si no hay camino viable
        if (nodoFin.distanciaMinima == Double.MAX_VALUE) {
            return new ResultadoRuta(new ArrayList<>(), 0.0);
        }

        return new ResultadoRuta(rutaSedes, nodoFin.distanciaMinima);
    }
}
package sebas.rascacielos;

/*Clase de grafos. Dónde se encuentra la caña, lo rico, lo sabroso. 
Aquí es dónde se maneja el funcionamiento de esos ascensores*/

import java.util.*;

public class GrafoAscensores {
    private Map<EstadoAscensor, Map<EstadoAscensor, Integer>> listaAdyacencia;

    public GrafoAscensores() {
        this.listaAdyacencia = new HashMap<>();
    }

    public void agregarEstado(EstadoAscensor estado) {
        listaAdyacencia.putIfAbsent(estado, new HashMap<>());
    }

    public void agregarTransicion(EstadoAscensor origen, EstadoAscensor destino, int tiempoEspera) {
        agregarEstado(origen);
        agregarEstado(destino);
        listaAdyacencia.get(origen).put(destino, tiempoEspera);
        listaAdyacencia.get(destino).put(origen, tiempoEspera);
    }

    // --- NUEVO: Constructor automático del Rascacielos ---
    public void construirRascacielos(int totalPisos, int totalAscensores) {
        for (int a = 1; a <= totalAscensores; a++) {
            for (int p = 1; p < totalPisos; p++) {
                EstadoAscensor origen = new EstadoAscensor(a, p);
                EstadoAscensor destino = new EstadoAscensor(a, p + 1);
                
                // Lógica de cooldown: 1 segundo por cada piso.

                int pesoCooldown = 1;
                
                agregarTransicion(origen, destino, pesoCooldown);
            }
        }
    }

    // --- Clases de apoyo para el algoritmo Dijkstra ---
    public static class ResultadoRuta {
        public List<EstadoAscensor> camino;
        public int costoTotal;
        public ResultadoRuta(List<EstadoAscensor> c, int costo) { this.camino = c; this.costoTotal = costo; }
    }

    private static class NodoDijkstra implements Comparable<NodoDijkstra> {
        EstadoAscensor estado;
        int costoAcumulado;
        public NodoDijkstra(EstadoAscensor e, int c) { this.estado = e; this.costoAcumulado = c; }
        @Override
        public int compareTo(NodoDijkstra otro) { return Integer.compare(this.costoAcumulado, otro.costoAcumulado); }
    }

    // --- NUEVO: Algoritmo de Dijkstra para pesos variables ---
    public ResultadoRuta optimizarTiempoEspera(EstadoAscensor inicio, int pisoDestino) {
        PriorityQueue<NodoDijkstra> cola = new PriorityQueue<>();
        Map<EstadoAscensor, Integer> distancias = new HashMap<>();
        Map<EstadoAscensor, EstadoAscensor> padres = new HashMap<>();

        for (EstadoAscensor nodo : listaAdyacencia.keySet()) {
            distancias.put(nodo, Integer.MAX_VALUE);
        }

        distancias.put(inicio, 0);
        cola.add(new NodoDijkstra(inicio, 0));
        EstadoAscensor estadoFinalEncontrado = null;

        while (!cola.isEmpty()) {
            NodoDijkstra actual = cola.poll();

            if (actual.estado.getPisoActual() == pisoDestino) {
                estadoFinalEncontrado = actual.estado;
                break;
            }

            if (actual.costoAcumulado > distancias.get(actual.estado)) continue;

            Map<EstadoAscensor, Integer> vecinos = listaAdyacencia.getOrDefault(actual.estado, new HashMap<>());
            for (Map.Entry<EstadoAscensor, Integer> vecinoEntry : vecinos.entrySet()) {
                EstadoAscensor vecino = vecinoEntry.getKey();
                int pesoArista = vecinoEntry.getValue();
                int nuevaDistancia = actual.costoAcumulado + pesoArista;

                if (nuevaDistancia < distancias.get(vecino)) {
                    distancias.put(vecino, nuevaDistancia);
                    padres.put(vecino, actual.estado);
                    cola.add(new NodoDijkstra(vecino, nuevaDistancia));
                }
            }
        }

        List<EstadoAscensor> rutaOptima = new ArrayList<>();
        if (estadoFinalEncontrado == null) return new ResultadoRuta(rutaOptima, Integer.MAX_VALUE);

        EstadoAscensor paso = estadoFinalEncontrado;
        while (paso != null) {
            rutaOptima.add(paso);
            paso = padres.get(paso);
        }
        Collections.reverse(rutaOptima);
        return new ResultadoRuta(rutaOptima, distancias.get(estadoFinalEncontrado));
    }
}
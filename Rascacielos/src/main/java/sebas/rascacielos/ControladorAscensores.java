package sebas.rascacielos;

//Clase encargada para controlar los ascensores 

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class ControladorAscensores implements ActionListener {
    private VistaRascacielos vista;
    private GrafoAscensores modelo;
    private EstadoAscensor[] estadoAscensores;
    
    // Arreglo para saber si un ascensor está en movimiento (true) o disponible (false)
    private boolean[] ocupados;

    public ControladorAscensores(VistaRascacielos vista, GrafoAscensores modelo, EstadoAscensor[] iniciales) {
        this.vista = vista;
        this.modelo = modelo;
        this.estadoAscensores = iniciales;
        this.ocupados = new boolean[iniciales.length]; // Por supuesto que todos inician en false (Libres)
        
        this.vista.setControlador(this);
        this.vista.mostrarMensaje("Bienvenido al edificio ejecutivo Maracaná. ¿A dónde desea ir hoy?");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int pisoDestino;
        try {
            // Leer el piso ingresado en la interfaz por el usuario
            pisoDestino = Integer.parseInt(vista.getCampoPiso());
        } catch (NumberFormatException ex) {
            vista.mostrarMensaje("Error: Ingresa un número válido en la caja de texto.");
            return;
        }

        if (pisoDestino < 1 || pisoDestino > 50) {
            vista.mostrarMensaje("Error: El piso debe estar entre el 1 y el 50.");
            return;
        }

        // Luego verificar si hay al menos un ascensor libre
        boolean todosOcupados = true;
        for (boolean estado : ocupados) {
            if (!estado) { // Si hay un false, significa que está libre
                todosOcupados = false;
                break;
            }
        }

        // Si todos están moviendose, es decir, ocupados pues se lanza la advertencia
        if (todosOcupados) {
            JOptionPane.showMessageDialog(vista, "Espere. Están en uso.", "Sistema Ocupado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        vista.mostrarMensaje("\n--- Solicitud: Ir al Piso " + pisoDestino + " ---");
        
        GrafoAscensores.ResultadoRuta mejorRuta = null;
        int indiceMejorAscensor = -1;

        // Luego toca evaluar usando Dijkstra solo los ascensores que están LIBRES
        for (int i = 0; i < estadoAscensores.length; i++) {
            if (ocupados[i]) continue; // Saltamos los que están ocupados

            // Si el ascensor ya está en el piso que el usuario pide y además está libre mostrará el mensaje
            if (estadoAscensores[i].getPisoActual() == pisoDestino) {
                vista.mostrarMensaje("El Ascensor " + (i + 1) + " ya está en ese piso y está libre.");
                return;
            }

            GrafoAscensores.ResultadoRuta rutaActual = modelo.optimizarTiempoEspera(estadoAscensores[i], pisoDestino);
            
            // Seleccionamos la ruta que tenga el menor costo de cooldown (aplicando la ruta más óptima)
            if (mejorRuta == null || rutaActual.costoTotal < mejorRuta.costoTotal) {
                mejorRuta = rutaActual;
                indiceMejorAscensor = i;
            }
        }

        if (indiceMejorAscensor == -1) return; // Control de seguridad

        final int ascensorElegido = indiceMejorAscensor;
        final GrafoAscensores.ResultadoRuta rutaFinal = mejorRuta;

        vista.mostrarMensaje(">> El Ascensor " + (ascensorElegido + 1) + " responderá al llamado.");
        vista.mostrarMensaje(">> Tiempo de movimiento (cooldown): " + rutaFinal.costoTotal + " segundos.");
        
        // Luego hay que marcar el ascensor como OCUPADO en la lógica y actualizamos el slot visual
        ocupados[ascensorElegido] = true;
        vista.actualizarEstadoAscensor(ascensorElegido, "OCUPADO", rutaFinal.costoTotal);
        
        // FInalemente, aquí el temporizador iterativo (Cuenta regresiva y animación del dibujo)
        int velocidadSimulacion = 500; // 500ms reales equivalen a 1 segundo del juego
        
        Timer timer = new Timer(velocidadSimulacion, null);
        
        timer.addActionListener(new ActionListener() {
            int tiempoRestante = rutaFinal.costoTotal;
            int totalPasos = rutaFinal.camino.size();
            
            //Que horrible fue hacer el dibujo, se me bugeo todo el código este. Nomás.
            
            @Override
            public void actionPerformed(ActionEvent evt) {
                tiempoRestante--; 
                
                // Lógica de Animación: Mapeamos el tiempo transcurrido con la cantidad de pisos de la ruta
                // Esto asegura que la caja del ascensor se mueva fluidamente sin errores de índice (Out of Bounds)
                int tiempoTranscurrido = rutaFinal.costoTotal - tiempoRestante;
                int indicePasoActual = 0;
                
                if (rutaFinal.costoTotal > 0) {
                    indicePasoActual = (int) Math.round((double) tiempoTranscurrido / rutaFinal.costoTotal * (totalPasos - 1));
                }
                
                // Tope de seguridad para no exceder la lista de la ruta
                if (indicePasoActual >= totalPasos) {
                    indicePasoActual = totalPasos - 1;
                }
                
                // Actualizamos internamente el piso del ascensor
                estadoAscensores[ascensorElegido] = rutaFinal.camino.get(indicePasoActual);

                // Mandamos los datos de los 4 ascensores para redibujar el panel gráfico
                int[] pisosActuales = new int[4];
                for (int j = 0; j < 4; j++) {
                    pisosActuales[j] = estadoAscensores[j].getPisoActual();
                }
                vista.actualizarDibujo(pisosActuales, ocupados);
                
                // Control del cronómetro
                if (tiempoRestante > 0) {
                    // Actualizamos el contador regresivo en el slot inferior de la pantalla
                    vista.actualizarEstadoAscensor(ascensorElegido, "OCUPADO", tiempoRestante);
                } else {
                    // El tiempo llegó a 0: El ascensor llegó a su destino definitivo
                    estadoAscensores[ascensorElegido] = rutaFinal.camino.get(totalPasos - 1);
                    ocupados[ascensorElegido] = false; 
                    
                    vista.actualizarEstadoAscensor(ascensorElegido, "LIBRE", 0);
                    vista.mostrarMensaje("¡Ding! Ascensor " + (ascensorElegido + 1) + " ha llegado al piso " + pisoDestino + " y está LIBRE.");
                    
                    // Última actualización visual para asegurar que la caja vuelva a quedar verde
                    pisosActuales[ascensorElegido] = estadoAscensores[ascensorElegido].getPisoActual();
                    vista.actualizarDibujo(pisosActuales, ocupados);
                    
                    ((Timer)evt.getSource()).stop(); // Detenemos este reloj específico
                }
            }
        });
        
        timer.start(); // Iniciamos el movimiento
    }
}
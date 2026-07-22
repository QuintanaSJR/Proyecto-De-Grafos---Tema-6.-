package sebas.rascacielos;

//Pues el main, dónde inicia toda esta belleza... algo así.

public class Main {
    public static void main(String[] args) {
        GrafoAscensores modelo = new GrafoAscensores();
        
        // Construye automáticamente los nodos y aristas con las reglas de 50 pisos y 4 ascensores
        modelo.construirRascacielos(50, 4);
        
        // Los 4 ascensores inician estacionados en el piso 1
        EstadoAscensor[] ascensoresIniciales = {
            new EstadoAscensor(1, 1),
            new EstadoAscensor(2, 1),
            new EstadoAscensor(3, 1),
            new EstadoAscensor(4, 1)
        };
        
        VistaRascacielos vista = new VistaRascacielos();
        ControladorAscensores controlador = new ControladorAscensores(vista, modelo, ascensoresIniciales);
        
        vista.setLocationRelativeTo(null);
        vista.setVisible(true);
    }
}
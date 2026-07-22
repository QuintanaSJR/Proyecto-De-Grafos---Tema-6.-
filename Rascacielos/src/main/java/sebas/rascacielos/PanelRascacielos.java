package sebas.rascacielos;

/*ESTA CLASE LA ODIE PORQUE ME BUGEO EL PROGRAMA HORRIBLE. Pero, después de ciertas cosillas
pude hacer que funcionara. Es la encargada de dibujar los ascensores y se puedan ver.*/

import javax.swing.*;
import java.awt.*;

public class PanelRascacielos extends JPanel {
    private int[] pisosActuales = {1, 1, 1, 1}; 
    private boolean[] ocupados = {false, false, false, false};

    public PanelRascacielos() {
        setPreferredSize(new Dimension(300, 550)); // <- ¡Asegúrate de que esto esté!
        setBackground(new Color(30, 30, 30)); 
    }

    public void actualizarAscensores(int[] nuevosPisos, boolean[] estadosOcupados) {
        this.pisosActuales = nuevosPisos.clone();
        this.ocupados = estadosOcupados.clone();
        repaint(); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int anchoPanel = getWidth();
        
        // 1. Añadimos un pequeño margen de 15 píxeles arriba y abajo para que no pegue del borde
        int margen = 15;
        int altoUtil = getHeight() - (margen * 2); 
        int totalPisos = 50;
        
        // Prevenir errores matemáticos si la ventana se encoge demasiado
        if (altoUtil < 50) altoUtil = 50; 
        
        // Usamos double para mayor precisión en espacios pequeños
        double altoPiso = (double) altoUtil / totalPisos;

        g2d.setColor(new Color(80, 80, 80));
        
        // 2. Dibujamos las líneas de los pisos ajustadas al nuevo margen
        for (int i = 0; i <= totalPisos; i++) {
            int y = margen + altoUtil - (int)(i * altoPiso);
            g2d.drawLine(0, y, anchoPanel, y);
            
            if (i > 0 && i % 5 == 0) {
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                g2d.drawString("P" + i, 5, y - 2);
                g2d.setColor(new Color(80, 80, 80));
            }
        }

        // 3. Ajustamos el ancho de los ascensores para que no se amontonen
        int anchoAscensor = anchoPanel / 6; 
        if (anchoAscensor > 40) anchoAscensor = 40; // Ancho máximo
        int separacion = anchoAscensor + 10;
        int offsetIzquierda = 35; 

        // 4. Dibujamos los ascensores
        for (int i = 0; i < 4; i++) {
            int piso = pisosActuales[i];
            int yAscensor = margen + altoUtil - (int)(piso * altoPiso);
            int xAscensor = offsetIzquierda + (i * separacion);

            if (ocupados[i]) {
                g2d.setColor(new Color(220, 50, 50)); 
            } else {
                g2d.setColor(new Color(50, 220, 50)); 
            }

            g2d.fillRect(xAscensor, yAscensor, anchoAscensor, (int)altoPiso);
            
            g2d.setColor(Color.WHITE);
            g2d.drawRect(xAscensor, yAscensor, anchoAscensor, (int)altoPiso);
            
            g2d.setColor(Color.BLACK);
            g2d.drawString("A"+(i+1), xAscensor + 5, yAscensor + (int)altoPiso - 2);
        }
    }
}

//Me puse a inventar, sí o q.

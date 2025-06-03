/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pruebasbloom.gui;
import pruebasbloom.core.logica.GestorDePruebas;
import pruebasbloom.core.modelo.ItemOpcionMultiple;
import pruebasbloom.core.modelo.ItemPrueba;
import pruebasbloom.core.modelo.ItemVerdaderoFalso;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author carlospetit
 */
public class PanelAplicacionPrueba extends JPanel {

    private VentanaPrincipal ventanaPrincipal;
    private GestorDePruebas gestorDePruebas;

    private JLabel lblNumeroPregunta;
    private JTextArea areaEnunciadoPregunta;
    private JPanel panelOpcionesRespuesta;
    private JButton btnAnterior;
    private JButton btnSiguiente;

    private ButtonGroup grupoOpciones; 
    private List<JRadioButton> radioButtonsActuales; 

    public PanelAplicacionPrueba(VentanaPrincipal ventana, GestorDePruebas gestor) {
        this.ventanaPrincipal = ventana;
        this.gestorDePruebas = gestor;
        this.radioButtonsActuales = new ArrayList<>();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblNumeroPregunta = new JLabel("Pregunta X de Y", SwingConstants.CENTER);
        lblNumeroPregunta.setFont(new Font("Arial", Font.BOLD, 16));

        areaEnunciadoPregunta = new JTextArea(8, 40);
        areaEnunciadoPregunta.setEditable(false);
        areaEnunciadoPregunta.setLineWrap(true);
        areaEnunciadoPregunta.setWrapStyleWord(true);
        areaEnunciadoPregunta.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollEnunciado = new JScrollPane(areaEnunciadoPregunta);

        panelOpcionesRespuesta = new JPanel();
        panelOpcionesRespuesta.setLayout(new BoxLayout(panelOpcionesRespuesta, BoxLayout.Y_AXIS)); 
        JScrollPane scrollOpciones = new JScrollPane(panelOpcionesRespuesta);
        scrollOpciones.setBorder(BorderFactory.createTitledBorder("Seleccione su respuesta:"));


        JPanel panelNavegacion = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnAnterior = new JButton("<< Volver Atrás");
        btnSiguiente = new JButton("Siguiente Ítem >>");
        panelNavegacion.add(btnAnterior);
        panelNavegacion.add(btnSiguiente);

        JPanel panelCentral = new JPanel(new BorderLayout(10,10));
        panelCentral.add(scrollEnunciado, BorderLayout.NORTH);
        panelCentral.add(scrollOpciones, BorderLayout.CENTER);


        add(lblNumeroPregunta, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(panelNavegacion, BorderLayout.SOUTH);

        btnAnterior.addActionListener(e -> {
            gestorDePruebas.retrocederItemEnPrueba(false); 
        });

        btnSiguiente.addActionListener(e -> {
            if (btnSiguiente.getText().equals("Enviar Respuestas")) {
                ventanaPrincipal.irAPanelResultados();
            } else {
                gestorDePruebas.avanzarItemEnPrueba(false);
            }
        });
    }

    /**
     * 
     */
    public void mostrarItem(ItemPrueba item, int indiceActual, int totalItems, Object respuestaPrevia) {
        if (item == null) {
            
            lblNumeroPregunta.setText("Prueba Finalizada o Error");
            areaEnunciadoPregunta.setText("No hay más preguntas o ha ocurrido un error.");
            panelOpcionesRespuesta.removeAll();
            btnAnterior.setEnabled(false);
            btnSiguiente.setText("Finalizar");
            btnSiguiente.setEnabled(false);
            panelOpcionesRespuesta.revalidate();
            panelOpcionesRespuesta.repaint();
            return;
        }

        lblNumeroPregunta.setText(String.format("Pregunta %d de %d (Nivel: %s, Tiempo Est.: %d seg)",
                indiceActual + 1, totalItems, item.getNivelBloom().toString(), item.getTiempoEstimado()));
        areaEnunciadoPregunta.setText(item.getEnunciado());
        areaEnunciadoPregunta.setCaretPosition(0);

        panelOpcionesRespuesta.removeAll();
        radioButtonsActuales.clear();
        grupoOpciones = new ButtonGroup();

        if (item instanceof ItemOpcionMultiple) {
            ItemOpcionMultiple itemOM = (ItemOpcionMultiple) item;
            List<String> opciones = itemOM.getOpciones();
            for (int i = 0; i < opciones.size(); i++) {
                JRadioButton radioOpcion = new JRadioButton("<html>" + (char)('A' + i) + ") " + opciones.get(i) + "</html>");
                radioOpcion.setFont(new Font("Arial", Font.PLAIN, 13));
                final int indiceSeleccionado = i; // Necesario para lambda
                radioOpcion.addActionListener(e -> gestorDePruebas.guardarRespuestaUsuarioParaItemActual(indiceSeleccionado));

                grupoOpciones.add(radioOpcion);
                panelOpcionesRespuesta.add(radioOpcion);
                radioButtonsActuales.add(radioOpcion);

                if (respuestaPrevia instanceof Integer && (Integer) respuestaPrevia == i) {
                    radioOpcion.setSelected(true);
                }
            }
        } else if (item instanceof ItemVerdaderoFalso) {
            JRadioButton radioVerdadero = new JRadioButton("Verdadero");
            radioVerdadero.setFont(new Font("Arial", Font.PLAIN, 13));
            radioVerdadero.addActionListener(e -> gestorDePruebas.guardarRespuestaUsuarioParaItemActual(true));

            JRadioButton radioFalso = new JRadioButton("Falso");
            radioFalso.setFont(new Font("Arial", Font.PLAIN, 13));
            radioFalso.addActionListener(e -> gestorDePruebas.guardarRespuestaUsuarioParaItemActual(false));

            grupoOpciones.add(radioVerdadero);
            grupoOpciones.add(radioFalso);
            panelOpcionesRespuesta.add(radioVerdadero);
            panelOpcionesRespuesta.add(radioFalso);
            radioButtonsActuales.add(radioVerdadero);
            radioButtonsActuales.add(radioFalso);


            if (respuestaPrevia instanceof Boolean) {
                if ((Boolean) respuestaPrevia) {
                    radioVerdadero.setSelected(true);
                } else {
                    radioFalso.setSelected(true);
                }
            }
        }

        btnAnterior.setEnabled(indiceActual > 0);
        if (indiceActual == totalItems - 1) {
            btnSiguiente.setText("Enviar Respuestas");
        } else {
            btnSiguiente.setText("Siguiente Ítem >>");
        }
        btnSiguiente.setEnabled(true);


        panelOpcionesRespuesta.revalidate();
        panelOpcionesRespuesta.repaint();
    }
}

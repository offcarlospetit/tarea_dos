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
public class PanelRevisionDetallada extends JPanel {

    private VentanaPrincipal ventanaPrincipal;
    private GestorDePruebas gestorDePruebas;

    private JLabel lblNumeroPreguntaRevision;
    private JTextArea areaEnunciadoPreguntaRevision;
    private JPanel panelOpcionesRespuestaRevision;
    private JLabel lblResultadoItem;
    private JButton btnAnteriorRevision;
    private JButton btnSiguienteRevision;
    private JButton btnVolverResumen;

    private List<JRadioButton> radioButtonsActualesRevision;

    public PanelRevisionDetallada(VentanaPrincipal ventana, GestorDePruebas gestor) {
        this.ventanaPrincipal = ventana;
        this.gestorDePruebas = gestor;
        this.radioButtonsActualesRevision = new ArrayList<>();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblNumeroPreguntaRevision = new JLabel("Revisión Pregunta X de Y", SwingConstants.CENTER);
        lblNumeroPreguntaRevision.setFont(new Font("Arial", Font.BOLD, 16));

        areaEnunciadoPreguntaRevision = new JTextArea(7, 40);
        areaEnunciadoPreguntaRevision.setEditable(false);
        areaEnunciadoPreguntaRevision.setLineWrap(true);
        areaEnunciadoPreguntaRevision.setWrapStyleWord(true);
        areaEnunciadoPreguntaRevision.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollEnunciado = new JScrollPane(areaEnunciadoPreguntaRevision);

        panelOpcionesRespuestaRevision = new JPanel();
        panelOpcionesRespuestaRevision.setLayout(new BoxLayout(panelOpcionesRespuestaRevision, BoxLayout.Y_AXIS));
        JScrollPane scrollOpciones = new JScrollPane(panelOpcionesRespuestaRevision);
        scrollOpciones.setBorder(BorderFactory.createTitledBorder("Detalle de Respuesta:"));
        
        lblResultadoItem = new JLabel("Resultado: ", SwingConstants.LEFT);
        lblResultadoItem.setFont(new Font("Arial", Font.BOLD, 14));


        JPanel panelNavegacion = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAnteriorRevision = new JButton("<< Anterior");
        btnSiguienteRevision = new JButton("Siguiente >>");
        btnVolverResumen = new JButton("Volver al Resumen");
        panelNavegacion.add(btnAnteriorRevision);
        panelNavegacion.add(btnSiguienteRevision);
        panelNavegacion.add(btnVolverResumen);

        JPanel panelCentralSuperior = new JPanel(new BorderLayout(5,5));
        panelCentralSuperior.add(scrollEnunciado, BorderLayout.NORTH);
        panelCentralSuperior.add(lblResultadoItem, BorderLayout.SOUTH);

        JPanel panelCentral = new JPanel(new BorderLayout(10,10));
        panelCentral.add(panelCentralSuperior, BorderLayout.NORTH);
        panelCentral.add(scrollOpciones, BorderLayout.CENTER);

        add(lblNumeroPreguntaRevision, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(panelNavegacion, BorderLayout.SOUTH);

        btnAnteriorRevision.addActionListener(e -> gestorDePruebas.avanzarItemEnPrueba(true));

        btnSiguienteRevision.addActionListener(e -> gestorDePruebas.avanzarItemEnPrueba(true));

        btnVolverResumen.addActionListener(e -> ventanaPrincipal.mostrarPanel("PANEL_RESULTADOS_ID"));
    }

    /**
     * 
     */
    public void mostrarItemParaRevision(ItemPrueba item, int indiceActual, int totalItems, Object respuestaUsuario, boolean esRespuestaCorrecta) {
        if (item == null) {
            lblNumeroPreguntaRevision.setText("Revisión Finalizada");
            areaEnunciadoPreguntaRevision.setText("No hay más preguntas para revisar.");
            panelOpcionesRespuestaRevision.removeAll();
            lblResultadoItem.setText("");
            btnAnteriorRevision.setEnabled(totalItems > 0); // Habilitar si hay al menos un item
            btnSiguienteRevision.setEnabled(false);
            panelOpcionesRespuestaRevision.revalidate();
            panelOpcionesRespuestaRevision.repaint();
            return;
        }
        
        lblNumeroPreguntaRevision.setText(String.format("Revisión Pregunta %d de %d (Nivel: %s)",
                indiceActual + 1, totalItems, item.getNivelBloom().toString()));
        areaEnunciadoPreguntaRevision.setText(item.getEnunciado());
        areaEnunciadoPreguntaRevision.setCaretPosition(0);

        if (esRespuestaCorrecta) {
            lblResultadoItem.setText("Tu respuesta fue: ¡CORRECTA!");
            lblResultadoItem.setForeground(new Color(0, 128, 0));
        } else {
            lblResultadoItem.setText("Tu respuesta fue: INCORRECTA.");
            lblResultadoItem.setForeground(Color.RED);
        }

        panelOpcionesRespuestaRevision.removeAll();
        radioButtonsActualesRevision.clear();

        if (item instanceof ItemOpcionMultiple) {
            ItemOpcionMultiple itemOM = (ItemOpcionMultiple) item;
            List<String> opciones = itemOM.getOpciones();
            int indiceCorrecto = itemOM.getIndiceOpcionCorrecta();
            Integer indiceUsuario = (respuestaUsuario instanceof Integer) ? (Integer) respuestaUsuario : -1;

            for (int i = 0; i < opciones.size(); i++) {
                JRadioButton radioOpcion = new JRadioButton("<html>" + (char)('A' + i) + ") " + opciones.get(i) + "</html>");
                radioOpcion.setFont(new Font("Arial", Font.PLAIN, 13));
                radioOpcion.setEnabled(false);

                if (i == indiceUsuario) {
                    radioOpcion.setSelected(true);
                }
                if (i == indiceCorrecto) {
                    radioOpcion.setText(radioOpcion.getText() + (esRespuestaCorrecta && i == indiceUsuario ? "" : " (Correcta)"));
                    radioOpcion.setForeground(new Color(0,100,0));
                } else if (i == indiceUsuario && !esRespuestaCorrecta) {
                     radioOpcion.setForeground(Color.RED);
                }


                panelOpcionesRespuestaRevision.add(radioOpcion);
                radioButtonsActualesRevision.add(radioOpcion);
            }
            if (!esRespuestaCorrecta && indiceUsuario == -1) {
                 lblResultadoItem.setText(lblResultadoItem.getText() + " No seleccionaste una opción. La correcta era la opción " + (char)('A' + indiceCorrecto) + ".");
            } else if (!esRespuestaCorrecta && indiceUsuario != -1 && indiceUsuario != indiceCorrecto) {
                 lblResultadoItem.setText(lblResultadoItem.getText() + " La opción correcta era la " + (char)('A' + indiceCorrecto) + ".");
            }


        } else if (item instanceof ItemVerdaderoFalso) {
            ItemVerdaderoFalso itemVF = (ItemVerdaderoFalso) item;
            boolean solucionCorrecta = itemVF.getSolucionCorrectaVF();
            Boolean respuestaUsrBool = (respuestaUsuario instanceof Boolean) ? (Boolean) respuestaUsuario : null;

            JRadioButton radioVerdadero = new JRadioButton("Verdadero");
            radioVerdadero.setFont(new Font("Arial", Font.PLAIN, 13));
            radioVerdadero.setEnabled(false);

            JRadioButton radioFalso = new JRadioButton("Falso");
            radioFalso.setFont(new Font("Arial", Font.PLAIN, 13));
            radioFalso.setEnabled(false);

            if (respuestaUsrBool != null) {
                if (respuestaUsrBool) radioVerdadero.setSelected(true);
                else radioFalso.setSelected(true);
            }
            
            if (solucionCorrecta) {
                radioVerdadero.setText(radioVerdadero.getText() + (esRespuestaCorrecta && respuestaUsrBool != null && respuestaUsrBool ? "" : " (Correcta)"));
                radioVerdadero.setForeground(new Color(0,100,0));
                 if(respuestaUsrBool != null && !respuestaUsrBool && !esRespuestaCorrecta) radioFalso.setForeground(Color.RED);
            } else {
                radioFalso.setText(radioFalso.getText() + (esRespuestaCorrecta && respuestaUsrBool != null && !respuestaUsrBool ? "" : " (Correcta)"));
                radioFalso.setForeground(new Color(0,100,0));
                if(respuestaUsrBool != null && respuestaUsrBool && !esRespuestaCorrecta) radioVerdadero.setForeground(Color.RED);
            }
            
            panelOpcionesRespuestaRevision.add(radioVerdadero);
            panelOpcionesRespuestaRevision.add(radioFalso);
            radioButtonsActualesRevision.add(radioVerdadero);
            radioButtonsActualesRevision.add(radioFalso);

            if (!esRespuestaCorrecta && respuestaUsrBool == null) {
                lblResultadoItem.setText(lblResultadoItem.getText() + " No seleccionaste una opción. La respuesta correcta era " + (solucionCorrecta ? "Verdadero." : "Falso."));
            } else if (!esRespuestaCorrecta && respuestaUsrBool != null) {
                 lblResultadoItem.setText(lblResultadoItem.getText() + " La respuesta correcta era " + (solucionCorrecta ? "Verdadero." : "Falso."));
            }
        }

        btnAnteriorRevision.setEnabled(indiceActual > 0);
        btnSiguienteRevision.setEnabled(indiceActual < totalItems - 1);

        panelOpcionesRespuestaRevision.revalidate();
        panelOpcionesRespuestaRevision.repaint();
    }
}

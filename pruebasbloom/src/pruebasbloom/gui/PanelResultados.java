/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pruebasbloom.gui;
import pruebasbloom.core.modelo.NivelBloom; 

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.text.DecimalFormat;
/**
 *
 * @author carlospetit
 */
public class PanelResultados extends JPanel {

    private VentanaPrincipal ventanaPrincipal;

    private JTextArea areaResultados;
    private JButton btnRevisarDetalladamente;
    private JButton btnCargarNuevaPrueba;

    private static final DecimalFormat df = new DecimalFormat("0.00"); 

    public PanelResultados(VentanaPrincipal ventana, pruebasbloom.core.logica.GestorDePruebas gestor) {
        this.ventanaPrincipal = ventana;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = new JLabel("Resumen de Resultados de la Prueba", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));

        areaResultados = new JTextArea(15, 50);
        areaResultados.setEditable(false);
        areaResultados.setFont(new Font("Monospaced", Font.PLAIN, 14)); 
        JScrollPane scrollResultados = new JScrollPane(areaResultados);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnRevisarDetalladamente = new JButton("Revisar Respuestas Detalladamente");
        btnCargarNuevaPrueba = new JButton("Cargar Nueva Prueba");

        panelBotones.add(btnRevisarDetalladamente);
        panelBotones.add(btnCargarNuevaPrueba);

        add(lblTitulo, BorderLayout.NORTH);
        add(scrollResultados, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        btnRevisarDetalladamente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ventanaPrincipal.irAPanelRevisionDetallada();
            }
        });

        btnCargarNuevaPrueba.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ventanaPrincipal.irAPanelCarga();
            }
        });
    }

    /**
     *
     *
     * @param correctasPorNivel 
     * @param correctasPorTipo
     */
    public void mostrarResultados(Map<NivelBloom, Double> correctasPorNivel, Map<String, Double> correctasPorTipo) {
        StringBuilder sb = new StringBuilder();
        sb.append("Desempeño por Nivel de Taxonomía de Bloom:\n");
        sb.append("--------------------------------------------\n");
        if (correctasPorNivel != null && !correctasPorNivel.isEmpty()) {
            for (NivelBloom nivel : NivelBloom.values()) {
                Double porcentaje = correctasPorNivel.getOrDefault(nivel, 0.0);
                sb.append(String.format("%-15s: %6s%%\n", nivel.toString(), df.format(porcentaje)));
            }
        } else {
            sb.append("No hay datos de resultados por nivel de Bloom.\n");
        }

        sb.append("\nDesempeño por Tipo de Ítem:\n");
        sb.append("---------------------------\n");
        if (correctasPorTipo != null && !correctasPorTipo.isEmpty()) {
            for (Map.Entry<String, Double> entry : correctasPorTipo.entrySet()) {
                sb.append(String.format("%-20s: %6s%%\n", entry.getKey(), df.format(entry.getValue())));
            }
        } else {
            sb.append("No hay datos de resultados por tipo de ítem.\n");
        }
        
        double totalCorrectas = 0;
        double totalPreguntas = 0; 

        areaResultados.setText(sb.toString());
        areaResultados.setCaretPosition(0);
    }
}

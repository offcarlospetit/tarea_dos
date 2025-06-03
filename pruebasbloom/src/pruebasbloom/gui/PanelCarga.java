/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pruebasbloom.gui;
import pruebasbloom.core.logica.GestorDePruebas;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 *
 * @author carlospetit
 */
public class PanelCarga extends JPanel {
private VentanaPrincipal ventanaPrincipal;
    private GestorDePruebas gestorDePruebas;

    private JButton btnSeleccionarArchivo;
    private JLabel lblArchivoSeleccionado;
    private JLabel lblCantidadItems;
    private JLabel lblTiempoEstimado;
    private JButton btnIniciarPrueba;
    private JTextArea areaMensajes; 

    public PanelCarga(VentanaPrincipal ventana, GestorDePruebas gestor) {
        this.ventanaPrincipal = ventana;
        this.gestorDePruebas = gestor;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        btnSeleccionarArchivo = new JButton("Seleccionar Archivo de Prueba...");
        lblArchivoSeleccionado = new JLabel("Ningún archivo seleccionado.");
        panelSuperior.add(btnSeleccionarArchivo);
        panelSuperior.add(lblArchivoSeleccionado);

        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setBorder(BorderFactory.createTitledBorder("Información de la Prueba"));

        lblCantidadItems = new JLabel("Cantidad de ítems: N/A");
        lblTiempoEstimado = new JLabel("Tiempo total estimado: N/A");

        Dimension labelEspacio = new Dimension(0, 5);
        panelInfo.add(lblCantidadItems);
        panelInfo.add(Box.createRigidArea(labelEspacio));
        panelInfo.add(lblTiempoEstimado);

        areaMensajes = new JTextArea(5, 30);
        areaMensajes.setEditable(false);
        areaMensajes.setLineWrap(true);
        areaMensajes.setWrapStyleWord(true);
        JScrollPane scrollMensajes = new JScrollPane(areaMensajes);
        scrollMensajes.setBorder(BorderFactory.createTitledBorder("Mensajes"));


        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnIniciarPrueba = new JButton("Iniciar Prueba");
        btnIniciarPrueba.setFont(new Font("Arial", Font.BOLD, 16));
        btnIniciarPrueba.setEnabled(false);
        panelInferior.add(btnIniciarPrueba);

        add(panelSuperior, BorderLayout.NORTH);
        
        JPanel panelCentroContenedor = new JPanel(new BorderLayout(10,10));
        panelCentroContenedor.add(panelInfo, BorderLayout.NORTH);
        panelCentroContenedor.add(scrollMensajes, BorderLayout.CENTER);
        
        add(panelCentroContenedor, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);

        btnSeleccionarArchivo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seleccionarYProcesarArchivo();
            }
        });

        btnIniciarPrueba.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ventanaPrincipal.irAPanelAplicacionPrueba();
            }
        });
    }

    private void seleccionarYProcesarArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de Definición de Prueba (*.txt)", "txt");
        fileChooser.setFileFilter(filter);

        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();
            lblArchivoSeleccionado.setText(archivoSeleccionado.getName());
            areaMensajes.setText("Cargando archivo: " + archivoSeleccionado.getName() + "...\n");
            btnIniciarPrueba.setEnabled(false);
            gestorDePruebas.cargarPruebaDesdeDefinicion(archivoSeleccionado.getAbsolutePath());
        } else {
            lblArchivoSeleccionado.setText("Ningún archivo seleccionado.");
            areaMensajes.append("Selección de archivo cancelada.\n");
        }
    }

  
    public void actualizarInfoCarga(int totalItems, int tiempoEstimadoTotal, boolean exito, String mensaje) {
        if (exito) {
            lblCantidadItems.setText("Cantidad de ítems: " + totalItems);
            int minutos = tiempoEstimadoTotal / 60;
            int segundos = tiempoEstimadoTotal % 60;
            lblTiempoEstimado.setText(String.format("Tiempo total estimado: %d min %02d seg", minutos, segundos));
            btnIniciarPrueba.setEnabled(true);
            areaMensajes.append(mensaje + "\n");
            areaMensajes.append("Prueba lista para iniciar.\n");
        } else {
            lblCantidadItems.setText("Cantidad de ítems: Error");
            lblTiempoEstimado.setText("Tiempo total estimado: Error");
            btnIniciarPrueba.setEnabled(false);
            areaMensajes.append("Error al cargar la prueba: " + mensaje + "\n");
        }
    }
    
    public void resetearEstado() {
        lblArchivoSeleccionado.setText("Ningún archivo seleccionado.");
        lblCantidadItems.setText("Cantidad de ítems: N/A");
        lblTiempoEstimado.setText("Tiempo total estimado: N/A");
        btnIniciarPrueba.setEnabled(false);
        areaMensajes.setText("");
    }
}
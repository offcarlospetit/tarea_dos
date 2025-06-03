/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pruebasbloom.gui;

import pruebasbloom.core.logica.GestorDePruebas;
import pruebasbloom.core.modelo.ItemPrueba;
import pruebasbloom.core.modelo.NivelBloom;
import pruebasbloom.core.observadores.PruebaObservador;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 *
 * @author carlospetit
 */
public class VentanaPrincipal extends JFrame implements PruebaObservador {

    private CardLayout cardLayout;
    private JPanel panelContenedor;

    private PanelCarga panelCarga;
    private PanelAplicacionPrueba panelAplicacionPrueba;
    private PanelResultados panelResultados;
    private PanelRevisionDetallada panelRevisionDetallada;
    private PanelGestionBancoPreguntas panelGestionBanco;
    private PanelEnsamblarPrueba panelEnsamblarPrueba;
    private GestorDePruebas gestorDePruebas;

    private static final String PANEL_CARGA_ID = "PANEL_CARGA";
    private static final String PANEL_APLICACION_ID = "PANEL_APLICACION";
    private static final String PANEL_RESULTADOS_ID = "PANEL_RESULTADOS";
    private static final String PANEL_REVISION_ID = "PANEL_REVISION";
    private static final String PANEL_GESTION_BANCO_ID = "PANEL_GESTION_BANCO";
    private static final String PANEL_ENSAMBLAR_PRUEBA_ID = "PANEL_ENSAMBLAR_PRUEBA";

    public VentanaPrincipal() {
        super("Sistema de Aplicación y Gestión de Pruebas Bloom");
        this.gestorDePruebas = new GestorDePruebas();
        this.gestorDePruebas.agregarObservadorPrueba(this);

        initComponents();
        configurarVentana();
    }

    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 860);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);

        panelCarga = new PanelCarga(this, gestorDePruebas);
        panelAplicacionPrueba = new PanelAplicacionPrueba(this, gestorDePruebas);
        panelResultados = new PanelResultados(this, gestorDePruebas);
        panelRevisionDetallada = new PanelRevisionDetallada(this, gestorDePruebas);
        panelGestionBanco = new PanelGestionBancoPreguntas(this, gestorDePruebas);
        panelEnsamblarPrueba = new PanelEnsamblarPrueba(this, gestorDePruebas);
        panelContenedor.add(panelCarga, PANEL_CARGA_ID);
        panelContenedor.add(panelAplicacionPrueba, PANEL_APLICACION_ID);
        panelContenedor.add(panelResultados, PANEL_RESULTADOS_ID);
        panelContenedor.add(panelRevisionDetallada, PANEL_REVISION_ID);
        panelContenedor.add(panelGestionBanco, PANEL_GESTION_BANCO_ID);
        panelContenedor.add(panelEnsamblarPrueba, PANEL_ENSAMBLAR_PRUEBA_ID);

        add(panelContenedor);
        mostrarPanel(PANEL_GESTION_BANCO_ID); // O el panel inicial que desees
    }

    public void mostrarPanel(String panelId) {
        if (PANEL_GESTION_BANCO_ID.equals(panelId) && panelGestionBanco != null) {
        }
        cardLayout.show(panelContenedor, panelId);
    }

    @Override
    public void onPruebaCargada(int totalItems, int tiempoEstimadoTotal, boolean exito, String mensajeError) {
        SwingUtilities.invokeLater(() -> {
            panelCarga.actualizarInfoCarga(totalItems, tiempoEstimadoTotal, exito, mensajeError);
        });
    }

    @Override
    public void onItemCambiado(ItemPrueba item, int indiceActual, int totalItems, Object respuestaPrevia) {
        SwingUtilities.invokeLater(() -> {
            panelAplicacionPrueba.mostrarItem(item, indiceActual, totalItems, respuestaPrevia);
        });
    }

    @Override
    public void onResultadosCalculados(Map<NivelBloom, Double> correctasPorNivel, Map<String, Double> correctasPorTipo) {
        SwingUtilities.invokeLater(() -> {
            panelResultados.mostrarResultados(correctasPorNivel, correctasPorTipo);
            mostrarPanel(PANEL_RESULTADOS_ID);
        });
    }

    @Override
    public void onItemRevisionCambiado(ItemPrueba item, int indiceActual, int totalItems, Object respuestaUsuario, boolean esRespuestaCorrecta) {
        SwingUtilities.invokeLater(() -> {
            panelRevisionDetallada.mostrarItemParaRevision(item, indiceActual, totalItems, respuestaUsuario, esRespuestaCorrecta);
            
        });
    }

    public void irAPanelAplicacionPrueba() {
        if (gestorDePruebas.getTotalItemsEnPruebaActual() > 0) {
            mostrarPanel(PANEL_APLICACION_ID);
            gestorDePruebas.irAItemEnPrueba(0, false);
        } else {
            JOptionPane.showMessageDialog(this, "No hay ítems cargados en la prueba actual.", "Prueba Vacía", JOptionPane.INFORMATION_MESSAGE);
            irAPanelCarga();
        }
    }

    public void irAPanelEnsamblarPrueba() {
        mostrarPanel(PANEL_ENSAMBLAR_PRUEBA_ID);
    }

    public void irAPanelResultados() {
        gestorDePruebas.calcularYNotificarResultadosDePrueba();
    }

    public void irAPanelRevisionDetallada() {
        if (gestorDePruebas.getTotalItemsEnPruebaActual() > 0) {
            mostrarPanel(PANEL_REVISION_ID);
            gestorDePruebas.irAItemEnPrueba(0, true);
        } else {
            JOptionPane.showMessageDialog(this, "No hay ítems en la prueba actual para revisar.", "Prueba Vacía", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void irAPanelCarga() {
        mostrarPanel(PANEL_CARGA_ID);
        if (panelCarga != null) {
            panelCarga.resetearEstado();
        }
    }

    public void irAPanelGestionBancoPreguntas() {
        mostrarPanel(PANEL_GESTION_BANCO_ID);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal());
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pruebasbloom.gui;
import pruebasbloom.core.logica.GestorDePruebas;
import pruebasbloom.core.modelo.ItemPrueba;
import pruebasbloom.core.observadores.BancoPreguntasObservador;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author carlospetit
 */
public class PanelGestionBancoPreguntas extends JPanel implements BancoPreguntasObservador {

    private VentanaPrincipal ventanaPrincipal;
    private GestorDePruebas gestorDePruebas;

    private JTable tablaPreguntas;
    private PreguntasTableModel modeloTabla;

    private JButton btnCrearPregunta;
    private JButton btnEditarPregunta;
    private JButton btnEliminarPregunta;
    private JButton btnRefrescarLista;
    private JButton btnEnsamblarPrueba;
    private JButton btnVolverPrincipal;


    public PanelGestionBancoPreguntas(VentanaPrincipal ventana, GestorDePruebas gestor) {
        this.ventanaPrincipal = ventana;
        this.gestorDePruebas = gestor;
        this.gestorDePruebas.agregarObservadorBanco(this);

        initComponents();
        cargarPreguntasEnTabla();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloTabla = new PreguntasTableModel(new ArrayList<>()); 
        tablaPreguntas = new JTable(modeloTabla);
        tablaPreguntas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPreguntas.setFillsViewportHeight(true);
        
        tablaPreguntas.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaPreguntas.getColumnModel().getColumn(0).setMaxWidth(70);
        tablaPreguntas.getColumnModel().getColumn(1).setPreferredWidth(400); 
        tablaPreguntas.getColumnModel().getColumn(2).setPreferredWidth(120);
        tablaPreguntas.getColumnModel().getColumn(3).setPreferredWidth(100); 
        tablaPreguntas.getColumnModel().getColumn(4).setPreferredWidth(80); 


        JScrollPane scrollTabla = new JScrollPane(tablaPreguntas);

        JPanel panelBotonesAccion = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        btnCrearPregunta = new JButton("Crear Pregunta");
        btnEditarPregunta = new JButton("Editar Seleccionada");
        btnEliminarPregunta = new JButton("Eliminar Seleccionada");
        btnRefrescarLista = new JButton("Refrescar Lista");
        btnEnsamblarPrueba = new JButton("Generar Nueva Prueba");


        panelBotonesAccion.add(btnCrearPregunta);
        panelBotonesAccion.add(btnEditarPregunta);
        panelBotonesAccion.add(btnEliminarPregunta);
        panelBotonesAccion.add(btnRefrescarLista);
        panelBotonesAccion.add(btnEnsamblarPrueba);

        JPanel panelBotonesNavegacion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10,5));
        btnVolverPrincipal = new JButton("Volver al Menú Principal");

        JPanel panelSuperiorBotones = new JPanel(new BorderLayout());
        panelSuperiorBotones.add(panelBotonesAccion, BorderLayout.WEST);
        panelSuperiorBotones.add(panelBotonesNavegacion, BorderLayout.EAST);


        add(panelSuperiorBotones, BorderLayout.NORTH);
        add(scrollTabla, BorderLayout.CENTER);

        btnCrearPregunta.addActionListener(e -> abrirDialogoCrearEditarPregunta(null));
        btnEnsamblarPrueba.addActionListener(e -> ventanaPrincipal.irAPanelEnsamblarPrueba());

        btnEditarPregunta.addActionListener(e -> {
            int filaSeleccionada = tablaPreguntas.getSelectedRow();
            if (filaSeleccionada != -1) {
                ItemPrueba itemSeleccionado = modeloTabla.getItemEnFila(filaSeleccionada);
                abrirDialogoCrearEditarPregunta(itemSeleccionado);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Por favor, selecciona una pregunta de la lista para editar.",
                        "Ninguna Pregunta Seleccionada", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnEliminarPregunta.addActionListener(e -> {
            int filaSeleccionada = tablaPreguntas.getSelectedRow();
            if (filaSeleccionada != -1) {
                ItemPrueba itemSeleccionado = modeloTabla.getItemEnFila(filaSeleccionada);
                int confirmacion = JOptionPane.showConfirmDialog(this,
                        "¿Estás seguro de que quieres eliminar la pregunta con ID " + itemSeleccionado.getId() + "?\n\"" + itemSeleccionado.getEnunciado() + "\"",
                        "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirmacion == JOptionPane.YES_OPTION) {
                    boolean exito = gestorDePruebas.removerItemDelBancoPorId(itemSeleccionado.getId());
                    if (exito) {
                        JOptionPane.showMessageDialog(this,"Pregunta eliminada exitosamente.", "Eliminación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,"Error al eliminar la pregunta.", "Error de Eliminación", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Por favor, selecciona una pregunta de la lista para eliminar.",
                        "Ninguna Pregunta Seleccionada", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnRefrescarLista.addActionListener(e -> cargarPreguntasEnTabla());
        
        btnVolverPrincipal.addActionListener(e -> ventanaPrincipal.irAPanelCarga());
    }

    private void cargarPreguntasEnTabla() {
        List<ItemPrueba> items = gestorDePruebas.listarTodosLosItemsDelBanco();
        modeloTabla.setItems(items);
    }

    private void abrirDialogoCrearEditarPregunta(ItemPrueba itemParaEditar) {
        DialogoCrearEditarPregunta dialogo = new DialogoCrearEditarPregunta(ventanaPrincipal, gestorDePruebas, itemParaEditar);
        dialogo.setVisible(true);
    }

    @Override
    public void onBancoPreguntasModificado() {
        SwingUtilities.invokeLater(this::cargarPreguntasEnTabla);
    }

    private static class PreguntasTableModel extends AbstractTableModel {
        private final String[] columnas = {"ID", "Enunciado (Extracto)", "Tipo", "Nivel Bloom", "Año Uso"};
        private List<ItemPrueba> items;

        public PreguntasTableModel(List<ItemPrueba> items) {
            this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        }

        public void setItems(List<ItemPrueba> nuevosItems) {
            this.items = nuevosItems != null ? new ArrayList<>(nuevosItems) : new ArrayList<>();
            fireTableDataChanged();
        }

        public ItemPrueba getItemEnFila(int fila) {
            if (fila >= 0 && fila < items.size()) {
                return items.get(fila);
            }
            return null;
        }

        @Override
        public int getRowCount() {
            return items.size();
        }

        @Override
        public int getColumnCount() {
            return columnas.length;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columnas[columnIndex];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            ItemPrueba item = items.get(rowIndex);
            switch (columnIndex) {
                case 0: return item.getId();
                case 1:
                    String enunciado = item.getEnunciado();
                    return enunciado.length() > 70 ? enunciado.substring(0, 67) + "..." : enunciado;
                case 2: return item.getTipoItem();
                case 3: return item.getNivelBloom().toString();
                case 4: return item.getAnioUso();
                default: return null;
            }
        }
    }
}
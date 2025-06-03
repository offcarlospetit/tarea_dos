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
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author carlospetit
 */
public class PanelEnsamblarPrueba extends JPanel implements BancoPreguntasObservador {

    private static final Logger LOGGER = Logger.getLogger(PanelEnsamblarPrueba.class.getName());

    private VentanaPrincipal ventanaPrincipal;
    private GestorDePruebas gestorDePruebas;

    private JTable tablaPreguntasDisponibles;
    private EnsamblarPruebaTableModel modeloTabla;
    private JTextField txtNombreArchivoPrueba;
    private JButton btnGenerarArchivo;
    private JButton btnVolverAGestion;
    private JSpinner spinnerAnioPruebaAsistente;
    private JSpinner spinnerNumPreguntasAsistente;
    private JButton btnGenerarAleatorioAsistente;

    public PanelEnsamblarPrueba(VentanaPrincipal ventana, GestorDePruebas gestor) {
        this.ventanaPrincipal = ventana;
        this.gestorDePruebas = gestor;
        this.gestorDePruebas.agregarObservadorBanco(this);

        initComponents();
        cargarPreguntasDisponibles();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel("Ensamblar Nueva Prueba", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel panelAsistente = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelAsistente.setBorder(BorderFactory.createTitledBorder("Asistente de Prueba Aleatoria"));

        panelAsistente.add(new JLabel("Año de la prueba:"));
        int anioActual = Calendar.getInstance().get(Calendar.YEAR);
        spinnerAnioPruebaAsistente = new JSpinner(new SpinnerNumberModel(anioActual, anioActual - 10, anioActual + 10, 1));
        panelAsistente.add(spinnerAnioPruebaAsistente);

        panelAsistente.add(Box.createHorizontalStrut(10));

        panelAsistente.add(new JLabel("Número de Preguntas Deseadas:"));
        spinnerNumPreguntasAsistente = new JSpinner(new SpinnerNumberModel(5, 1, 200, 1));
        panelAsistente.add(spinnerNumPreguntasAsistente);

        btnGenerarAleatorioAsistente = new JButton("Generar Selección Aleatoria");
        panelAsistente.add(btnGenerarAleatorioAsistente);

        JPanel panelSuperiorControles = new JPanel(new BorderLayout(0, 10));
        panelSuperiorControles.add(lblTitulo, BorderLayout.NORTH);
        panelSuperiorControles.add(panelAsistente, BorderLayout.CENTER);


        modeloTabla = new EnsamblarPruebaTableModel(new ArrayList<>());
        tablaPreguntasDisponibles = new JTable(modeloTabla);
        tablaPreguntasDisponibles.setFillsViewportHeight(true);

        TableColumn seleccionColumn = tablaPreguntasDisponibles.getColumnModel().getColumn(0);
        seleccionColumn.setPreferredWidth(40);
        seleccionColumn.setMaxWidth(50);
        tablaPreguntasDisponibles.getColumnModel().getColumn(1).setPreferredWidth(40);
        tablaPreguntasDisponibles.getColumnModel().getColumn(1).setMaxWidth(60);
        tablaPreguntasDisponibles.getColumnModel().getColumn(2).setPreferredWidth(350);
        tablaPreguntasDisponibles.getColumnModel().getColumn(3).setPreferredWidth(110); 
        tablaPreguntasDisponibles.getColumnModel().getColumn(4).setPreferredWidth(90); 
        tablaPreguntasDisponibles.getColumnModel().getColumn(5).setPreferredWidth(70);

        JScrollPane scrollTabla = new JScrollPane(tablaPreguntasDisponibles);

        JPanel panelInferior = new JPanel(new BorderLayout(10, 10)); 
        JPanel panelNombreArchivo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNombreArchivo.add(new JLabel("Nombre para archivo de prueba (ej: examen_final):"));
        txtNombreArchivoPrueba = new JTextField(25);
        panelNombreArchivo.add(txtNombreArchivoPrueba);

        JPanel panelBotonesAccion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnGenerarArchivo = new JButton("Generar Archivo de Prueba (con selección actual)");
        btnVolverAGestion = new JButton("Volver a Gestión");

        panelBotonesAccion.add(btnGenerarArchivo);
        panelBotonesAccion.add(btnVolverAGestion);

        panelInferior.add(panelNombreArchivo, BorderLayout.CENTER);
        panelInferior.add(panelBotonesAccion, BorderLayout.SOUTH);

        add(panelSuperiorControles, BorderLayout.NORTH);
        add(scrollTabla, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);

        btnGenerarAleatorioAsistente.addActionListener(e -> generarSeleccionAleatoriaConAsistente());
        btnGenerarArchivo.addActionListener(e -> generarArchivoDePrueba());
        btnVolverAGestion.addActionListener(e -> ventanaPrincipal.irAPanelGestionBancoPreguntas());
    }

    private void cargarPreguntasDisponibles() {
        List<ItemPrueba> items = gestorDePruebas.listarTodosLosItemsDelBanco();
        modeloTabla.setItems(items);
    }

    private void generarSeleccionAleatoriaConAsistente() {
        int anioPrueba = (Integer) spinnerAnioPruebaAsistente.getValue();
        int numPreguntas = (Integer) spinnerNumPreguntasAsistente.getValue();

        if (numPreguntas <= 0) {
            JOptionPane.showMessageDialog(this, "El número de preguntas debe ser mayor que cero.", "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Integer> idsSugeridos = gestorDePruebas.generarSugerenciaPruebaAleatoria(anioPrueba, numPreguntas);

        if (idsSugeridos.isEmpty() && numPreguntas > 0) {
            JOptionPane.showMessageDialog(this,
                    "No se pudieron encontrar preguntas elegibles para los criterios dados.\n" +
                            "Por favor, revisa el año de la prueba, la cantidad de preguntas solicitadas,\n" +
                            "o el contenido del banco de preguntas.",
                    "Asistente Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
        } else if (idsSugeridos.size() < numPreguntas) {
            JOptionPane.showMessageDialog(this,
                    "Advertencia: Solo se pudieron seleccionar " + idsSugeridos.size() + " de " + numPreguntas + " preguntas solicitadas,\n" +
                            "debido a las restricciones y al banco de preguntas disponible.",
                    "Selección Parcial", JOptionPane.WARNING_MESSAGE);
        } else if (!idsSugeridos.isEmpty()){
             JOptionPane.showMessageDialog(this,
                    "Se han preseleccionado " + idsSugeridos.size() + " preguntas aleatoriamente.\n" +
                            "Puedes ajustar la selección manualmente antes de generar el archivo.",
                    "Selección Aleatoria Generada", JOptionPane.INFORMATION_MESSAGE);
        }

        modeloTabla.seleccionarItemsPorIDs(idsSugeridos);
    }

    private void generarArchivoDePrueba() {
        List<ItemPrueba> seleccionados = modeloTabla.getItemsSeleccionados();
        if (seleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, selecciona al menos una pregunta para incluir en la prueba.",
                    "Ninguna Pregunta Seleccionada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombreSugeridoBase = txtNombreArchivoPrueba.getText().trim();
        if (nombreSugeridoBase.isEmpty()) {
            nombreSugeridoBase = "prueba_generada_" + System.currentTimeMillis();
        }
        if (!nombreSugeridoBase.toLowerCase().endsWith(".txt")) {
            nombreSugeridoBase += ".txt";
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Archivo de Definición de Prueba");
        fileChooser.setSelectedFile(new File(nombreSugeridoBase));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de Texto (*.txt)", "txt"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File archivoAGuardar = fileChooser.getSelectedFile();
            String filePath = archivoAGuardar.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".txt")) {
                archivoAGuardar = new File(filePath + ".txt");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoAGuardar))) {
                for (ItemPrueba item : seleccionados) {
                    writer.write(String.valueOf(item.getId()));
                    writer.newLine();
                }
                JOptionPane.showMessageDialog(this,
                        "Archivo de definición de prueba generado exitosamente:\n" + archivoAGuardar.getAbsolutePath(),
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Error al escribir el archivo de definición de prueba.", ex);
                JOptionPane.showMessageDialog(this,
                        "Error al guardar el archivo: " + ex.getMessage(),
                        "Error de Escritura", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void onBancoPreguntasModificado() {
        SwingUtilities.invokeLater(this::cargarPreguntasDisponibles);
    }

    private static class EnsamblarPruebaTableModel extends AbstractTableModel {
        private final String[] columnas = {"Sel.", "ID", "Enunciado (Extracto)", "Tipo", "Nivel Bloom", "Año Uso"};
        private List<ItemPrueba> items;
        private List<Boolean> seleccionados;

        public EnsamblarPruebaTableModel(List<ItemPrueba> items) {
            setItems(items);
        }

        public void setItems(List<ItemPrueba> nuevosItems) {
            this.items = nuevosItems != null ? new ArrayList<>(nuevosItems) : new ArrayList<>();
            this.seleccionados = new ArrayList<>(this.items.size());
            for (int i = 0; i < this.items.size(); i++) {
                this.seleccionados.add(false);
            }
            fireTableDataChanged();
        }

        public List<ItemPrueba> getItemsSeleccionados() {
            List<ItemPrueba> resultado = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                if (seleccionados.get(i) != null && seleccionados.get(i)) {
                    resultado.add(items.get(i));
                }
            }
            return resultado;
        }

        public void seleccionarItemsPorIDs(List<Integer> idsParaSeleccionar) {
            for (int i = 0; i < seleccionados.size(); i++) {
                seleccionados.set(i, false);
            }
            for (int i = 0; i < items.size(); i++) {
                if (idsParaSeleccionar.contains(items.get(i).getId())) {
                    seleccionados.set(i, true);
                }
            }
            fireTableDataChanged();
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
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return Boolean.class;
            }
            return super.getColumnClass(columnIndex);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            ItemPrueba item = items.get(rowIndex);
            switch (columnIndex) {
                case 0: return seleccionados.get(rowIndex);
                case 1: return item.getId();
                case 2:
                    String enunciado = item.getEnunciado();
                    return enunciado.length() > 60 ? enunciado.substring(0, 57) + "..." : enunciado;
                case 3: return item.getTipoItem();
                case 4: return item.getNivelBloom().toString();
                case 5: return item.getAnioUso();
                default: return null;
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 0 && aValue instanceof Boolean) {
                seleccionados.set(rowIndex, (Boolean) aValue);
                fireTableCellUpdated(rowIndex, columnIndex);
            }
        }
    }
}

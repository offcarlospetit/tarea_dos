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
import pruebasbloom.core.modelo.NivelBloom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
/**
 *
 * @author carlospetit
 */
public class DialogoCrearEditarPregunta extends JDialog {

    private GestorDePruebas gestorDePruebas;
    private ItemPrueba itemParaEditar;
    private boolean guardadoExitoso = false;

    private JTextField txtId;
    private JTextArea areaEnunciado;
    private JComboBox<NivelBloom> comboNivelBloom;
    private JSpinner spinnerTiempoEstimado; 
    private JSpinner spinnerAnioUso;
    private JComboBox<String> comboTipoItem;

    private JPanel panelContenedorDetallesTipo;
    private CardLayout cardLayoutDetallesTipo;
    private PanelDetallesOM panelOM;
    private PanelDetallesVF panelVF;

    private static final String DETALLES_OM_ID = "DETALLES_OM";
    private static final String DETALLES_VF_ID = "DETALLES_VF";

    private JButton btnGuardar;
    private JButton btnCancelar;

    public DialogoCrearEditarPregunta(Frame owner, GestorDePruebas gestor, ItemPrueba item) {
        super(owner, true);
        this.gestorDePruebas = gestor;
        this.itemParaEditar = item;

        setTitle(itemParaEditar == null ? "Crear Nueva Pregunta" : "Editar Pregunta (ID: " + itemParaEditar.getId() + ")");
        initComponents();
        configurarDialogo();
        if (itemParaEditar != null) {
            cargarDatosParaEdicion();
        } else {
            comboTipoItem.setSelectedIndex(0);
            actualizarPanelDetallesTipo();
        }
    }

    private void configurarDialogo() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(getParent());
        pack();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelCamposComunes = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panelCamposComunes.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtId = new JTextField(5);
        txtId.setEditable(false);
        txtId.setText(itemParaEditar == null ? "Automático" : String.valueOf(itemParaEditar.getId()));
        panelCamposComunes.add(txtId, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.NORTHEAST; gbc.weightx = 0;
        panelCamposComunes.add(new JLabel("Enunciado:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridheight = 2; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        areaEnunciado = new JTextArea(5, 30);
        areaEnunciado.setLineWrap(true);
        areaEnunciado.setWrapStyleWord(true);
        JScrollPane scrollEnunciado = new JScrollPane(areaEnunciado);
        panelCamposComunes.add(scrollEnunciado, gbc);
        gbc.gridheight = 1; gbc.weighty = 0; // Reset

        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.EAST;
        panelCamposComunes.add(new JLabel("Nivel Bloom:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        comboNivelBloom = new JComboBox<>(NivelBloom.values());
        panelCamposComunes.add(comboNivelBloom, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panelCamposComunes.add(new JLabel("Tiempo Estimado (seg):"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        spinnerTiempoEstimado = new JSpinner(new SpinnerNumberModel(30, 5, 600, 5)); 
        panelCamposComunes.add(spinnerTiempoEstimado, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panelCamposComunes.add(new JLabel("Año de Uso:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        spinnerAnioUso = new JSpinner(new SpinnerNumberModel(2025, 2000, 2050, 1));
        panelCamposComunes.add(spinnerAnioUso, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        panelCamposComunes.add(new JLabel("Tipo de Ítem:"), gbc);
        gbc.gridx = 1; gbc.gridy = 6;
        comboTipoItem = new JComboBox<>(new String[]{"Opción Múltiple", "Verdadero/Falso"});
        panelCamposComunes.add(comboTipoItem, gbc);

        cardLayoutDetallesTipo = new CardLayout();
        panelContenedorDetallesTipo = new JPanel(cardLayoutDetallesTipo);
        panelOM = new PanelDetallesOM();
        panelVF = new PanelDetallesVF();
        panelContenedorDetallesTipo.add(panelOM, DETALLES_OM_ID);
        panelContenedorDetallesTipo.add(panelVF, DETALLES_VF_ID);
        panelContenedorDetallesTipo.setBorder(BorderFactory.createTitledBorder("Detalles Específicos del Tipo"));


        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        add(panelCamposComunes, BorderLayout.NORTH);
        add(panelContenedorDetallesTipo, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        comboTipoItem.addActionListener(e -> actualizarPanelDetallesTipo());

        btnGuardar.addActionListener(e -> guardarPregunta());

        btnCancelar.addActionListener(e -> dispose()); 
    }

    private void actualizarPanelDetallesTipo() {
        String tipoSeleccionado = (String) comboTipoItem.getSelectedItem();
        if ("Opción Múltiple".equals(tipoSeleccionado)) {
            cardLayoutDetallesTipo.show(panelContenedorDetallesTipo, DETALLES_OM_ID);
        } else if ("Verdadero/Falso".equals(tipoSeleccionado)) {
            cardLayoutDetallesTipo.show(panelContenedorDetallesTipo, DETALLES_VF_ID);
        }
        pack(); 
    }

    private void cargarDatosParaEdicion() {
        areaEnunciado.setText(itemParaEditar.getEnunciado());
        comboNivelBloom.setSelectedItem(itemParaEditar.getNivelBloom());
        spinnerTiempoEstimado.setValue(itemParaEditar.getTiempoEstimado());
        spinnerAnioUso.setValue(itemParaEditar.getAnioUso());

        if (itemParaEditar instanceof ItemOpcionMultiple) {
            comboTipoItem.setSelectedItem("Opción Múltiple");
            actualizarPanelDetallesTipo(); // Para mostrar el panel OM
            panelOM.cargarDatos((ItemOpcionMultiple) itemParaEditar);
        } else if (itemParaEditar instanceof ItemVerdaderoFalso) {
            comboTipoItem.setSelectedItem("Verdadero/Falso");
            actualizarPanelDetallesTipo(); // Para mostrar el panel VF
            panelVF.cargarDatos((ItemVerdaderoFalso) itemParaEditar);
        }
        comboTipoItem.setEnabled(false); // No permitir cambiar el tipo de un ítem existente
    }

    private void guardarPregunta() {
        String enunciado = areaEnunciado.getText().trim();
        if (enunciado.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El enunciado no puede estar vacío.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        NivelBloom nivel = (NivelBloom) comboNivelBloom.getSelectedItem();
        int tiempo = (Integer) spinnerTiempoEstimado.getValue();
        int anio = (Integer) spinnerAnioUso.getValue();
        String tipoSeleccionado = (String) comboTipoItem.getSelectedItem();
        ItemPrueba itemAGuardar = null;

        try {
            if ("Opción Múltiple".equals(tipoSeleccionado)) {
                List<String> opciones = panelOM.getOpciones();
                int indiceCorrecto = panelOM.getIndiceRespuestaCorrecta();
                if (opciones.isEmpty() || opciones.size() < 2) {
                     JOptionPane.showMessageDialog(this, "Debe haber al menos dos opciones para Opción Múltiple.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (indiceCorrecto < 0 || indiceCorrecto >= opciones.size()) {
                    JOptionPane.showMessageDialog(this, "El índice de respuesta correcta no es válido para las opciones proporcionadas.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (itemParaEditar != null && itemParaEditar instanceof ItemOpcionMultiple) {
                    itemAGuardar = new ItemOpcionMultiple(itemParaEditar.getId(), enunciado, nivel, tiempo, anio, opciones, indiceCorrecto);
                } else {
                    itemAGuardar = new ItemOpcionMultiple(0, enunciado, nivel, tiempo, anio, opciones, indiceCorrecto);
                }

            } else if ("Verdadero/Falso".equals(tipoSeleccionado)) {
                Boolean solucionVF = panelVF.getSolucionSeleccionada();
                 if (solucionVF == null) {
                    JOptionPane.showMessageDialog(this, "Debe seleccionar si la respuesta es Verdadera o Falsa.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (itemParaEditar != null && itemParaEditar instanceof ItemVerdaderoFalso) {
                    itemAGuardar = new ItemVerdaderoFalso(itemParaEditar.getId(), enunciado, nivel, tiempo, anio, solucionVF);
                } else {
                    itemAGuardar = new ItemVerdaderoFalso(0, enunciado, nivel, tiempo, anio, solucionVF);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al recolectar datos del formulario: " + ex.getMessage(), "Error Interno", JOptionPane.ERROR_MESSAGE);
            return;
        }


        if (itemAGuardar == null) {
            JOptionPane.showMessageDialog(this, "No se pudo determinar el tipo de ítem a guardar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean exito;
        if (itemParaEditar == null) {
            exito = gestorDePruebas.agregarNuevoItemAlBanco(itemAGuardar);
        } else {
            exito = gestorDePruebas.modificarItemExistenteDelBanco(itemAGuardar);
        }

        if (exito) {
            guardadoExitoso = true;
            JOptionPane.showMessageDialog(this, "Pregunta guardada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar la pregunta en la base de datos.", "Error de Guardado", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean seGuardoAlgo() {
        return guardadoExitoso;
    }

    private static class PanelDetallesOM extends JPanel {
        private DefaultListModel<String> listModelOpciones;
        private JList<String> listaOpciones;
        private JTextField txtNuevaOpcion;
        private JButton btnAgregarOpcion;
        private JButton btnEliminarOpcion;
        private JComboBox<Integer> comboRespuestaCorrecta;

        public PanelDetallesOM() {
            setLayout(new BorderLayout(5, 5));

            listModelOpciones = new DefaultListModel<>();
            listaOpciones = new JList<>(listModelOpciones);
            listaOpciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scrollLista = new JScrollPane(listaOpciones);
            scrollLista.setPreferredSize(new Dimension(300, 100));

            JPanel panelAgregar = new JPanel(new BorderLayout(5,0));
            txtNuevaOpcion = new JTextField();
            btnAgregarOpcion = new JButton("Añadir Opción");
            panelAgregar.add(txtNuevaOpcion, BorderLayout.CENTER);
            panelAgregar.add(btnAgregarOpcion, BorderLayout.EAST);

            btnEliminarOpcion = new JButton("Eliminar Opción Seleccionada");

            JPanel panelGestionLista = new JPanel(new BorderLayout(5,5));
            panelGestionLista.add(panelAgregar, BorderLayout.NORTH);
            panelGestionLista.add(btnEliminarOpcion, BorderLayout.SOUTH);


            JPanel panelSeleccionCorrecta = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panelSeleccionCorrecta.add(new JLabel("Opción Correcta (índice):"));
            comboRespuestaCorrecta = new JComboBox<>();
            panelSeleccionCorrecta.add(comboRespuestaCorrecta);
            
            add(scrollLista, BorderLayout.CENTER);
            add(panelGestionLista, BorderLayout.SOUTH);
            add(panelSeleccionCorrecta, BorderLayout.NORTH);

            btnAgregarOpcion.addActionListener(e -> {
                String nuevaOpcion = txtNuevaOpcion.getText().trim();
                if (!nuevaOpcion.isEmpty()) {
                    listModelOpciones.addElement(nuevaOpcion);
                    txtNuevaOpcion.setText("");
                    actualizarComboRespuestaCorrecta();
                }
            });

            btnEliminarOpcion.addActionListener(e -> {
                int indiceSeleccionado = listaOpciones.getSelectedIndex();
                if (indiceSeleccionado != -1) {
                    listModelOpciones.remove(indiceSeleccionado);
                    actualizarComboRespuestaCorrecta();
                }
            });
        }

        private void actualizarComboRespuestaCorrecta() {
            comboRespuestaCorrecta.removeAllItems();
            for (int i = 0; i < listModelOpciones.getSize(); i++) {
                comboRespuestaCorrecta.addItem(i);
            }
            if (comboRespuestaCorrecta.getItemCount() > 0) {
                 comboRespuestaCorrecta.setSelectedIndex(0);
            }
        }

        public List<String> getOpciones() {
            List<String> opciones = new ArrayList<>();
            for (int i = 0; i < listModelOpciones.getSize(); i++) {
                opciones.add(listModelOpciones.getElementAt(i));
            }
            return opciones;
        }

        public int getIndiceRespuestaCorrecta() {
            Integer seleccionado = (Integer) comboRespuestaCorrecta.getSelectedItem();
            return seleccionado != null ? seleccionado : -1;
        }

        public void cargarDatos(ItemOpcionMultiple itemOM) {
            listModelOpciones.clear();
            if (itemOM.getOpciones() != null) {
                for (String opcion : itemOM.getOpciones()) {
                    listModelOpciones.addElement(opcion);
                }
            }
            actualizarComboRespuestaCorrecta(); 
            if (itemOM.getIndiceOpcionCorrecta() >= 0 && itemOM.getIndiceOpcionCorrecta() < comboRespuestaCorrecta.getItemCount()) {
                comboRespuestaCorrecta.setSelectedIndex(itemOM.getIndiceOpcionCorrecta());
            }
        }
    }

    private static class PanelDetallesVF extends JPanel {
        private JRadioButton radioRespuestaVerdadera;
        private JRadioButton radioRespuestaFalsa;
        private ButtonGroup grupoSolucionVF;

        public PanelDetallesVF() {
            setLayout(new FlowLayout(FlowLayout.LEFT));
            setBorder(BorderFactory.createTitledBorder("Definir Solución Correcta"));

            grupoSolucionVF = new ButtonGroup();

            radioRespuestaVerdadera = new JRadioButton("El enunciado es Verdadero");
            radioRespuestaFalsa = new JRadioButton("El enunciado es Falso");

            grupoSolucionVF.add(radioRespuestaVerdadera);
            grupoSolucionVF.add(radioRespuestaFalsa);

            add(radioRespuestaVerdadera);
            add(radioRespuestaFalsa);
        }

        public Boolean getSolucionSeleccionada() {
            if (radioRespuestaVerdadera.isSelected()) return true;
            if (radioRespuestaFalsa.isSelected()) return false;
            return null; // Ninguno seleccionado
        }

        public void cargarDatos(ItemVerdaderoFalso itemVF) {
            if (itemVF.getSolucionCorrectaVF()) {
                radioRespuestaVerdadera.setSelected(true);
            } else {
                radioRespuestaFalsa.setSelected(true);
            }
        }
    }
}
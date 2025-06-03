package pruebasbloom.core.logica;

import pruebasbloom.core.db.ItemDAO;
import pruebasbloom.core.modelo.ItemPrueba;
import pruebasbloom.core.modelo.NivelBloom;
import pruebasbloom.core.observadores.BancoPreguntasObservador;
import pruebasbloom.core.observadores.PruebaObservador;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Collections;
import java.util.stream.Collectors;

public class GestorDePruebas {
    private static final Logger LOGGER = Logger.getLogger(GestorDePruebas.class.getName());

    private List<ItemPrueba> itemsPruebaActual;
    private int indiceItemActual;              

    private final ItemDAO itemDAO;
    private final List<PruebaObservador> observadoresPrueba;
    private final List<BancoPreguntasObservador> observadoresBanco; 

    public GestorDePruebas() {
        this.itemsPruebaActual = new ArrayList<>();
        this.indiceItemActual = -1;
        this.itemDAO = new ItemDAO();
        this.observadoresPrueba = new ArrayList<>();
        this.observadoresBanco = new ArrayList<>();
    }

    public void agregarObservadorPrueba(PruebaObservador observador) {
        if (observador != null && !observadoresPrueba.contains(observador)) {
            this.observadoresPrueba.add(observador);
        }
    }

    public void removerObservadorPrueba(PruebaObservador observador) {
        this.observadoresPrueba.remove(observador);
    }

    public void agregarObservadorBanco(BancoPreguntasObservador observador) {
        if (observador != null && !observadoresBanco.contains(observador)) {
            this.observadoresBanco.add(observador);
        }
    }

    public void removerObservadorBanco(BancoPreguntasObservador observador) {
        this.observadoresBanco.remove(observador);
    }

    public void cargarPruebaDesdeDefinicion(String rutaArchivoDefinicion) {
        itemsPruebaActual.clear();
        indiceItemActual = -1;
        List<Integer> idsItems = new ArrayList<>();
        String mensajeErrorCarga = null;

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivoDefinicion))) {
            String linea;
            int numeroLinea = 0;
            while ((linea = br.readLine()) != null) {
                numeroLinea++;
                linea = linea.trim();
                if (!linea.isEmpty() && !linea.startsWith("#")) {
                    try {
                        idsItems.add(Integer.parseInt(linea));
                    } catch (NumberFormatException e) {
                        LOGGER.log(Level.WARNING, "Formato de ID inválido en archivo '" + rutaArchivoDefinicion + "', línea " + numeroLinea + ": " + linea, e);
                        mensajeErrorCarga = "Error en formato de ID en archivo (línea " + numeroLinea + "): '" + linea + "'.";
                        notificarCargaPrueba(0, 0, false, mensajeErrorCarga);
                        return;
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al leer el archivo de definición de prueba: " + rutaArchivoDefinicion, e);
            notificarCargaPrueba(0, 0, false, "Error al leer archivo de definición: " + e.getMessage());
            return;
        }

        if (idsItems.isEmpty()) {
            LOGGER.info("El archivo de definición de prueba está vacío o no contiene IDs válidos: " + rutaArchivoDefinicion);
            notificarCargaPrueba(0, 0, false, "Archivo de definición vacío o sin IDs válidos.");
            return;
        }

        itemsPruebaActual = itemDAO.getItemsByIds(idsItems);

        if (itemsPruebaActual.isEmpty() && !idsItems.isEmpty()) {
            notificarCargaPrueba(0, 0, false, "No se encontraron ítems en la BD para los IDs especificados.");
        } else {
            int tiempoTotal = calcularTiempoTotalEstimado();
            indiceItemActual = itemsPruebaActual.isEmpty() ? -1 : 0;
            String mensajeExito = "Prueba cargada exitosamente.";
            if (itemsPruebaActual.size() < idsItems.size()) {
                mensajeExito = "Prueba cargada. Advertencia: Algunos IDs del archivo no fueron encontrados en la base de datos.";
            }
            notificarCargaPrueba(itemsPruebaActual.size(), tiempoTotal, true, mensajeExito);
            if (!itemsPruebaActual.isEmpty()) {
                notificarCambioItemEnPrueba(false); 
            }
        }
    }

    private int calcularTiempoTotalEstimado() {
        if (itemsPruebaActual == null) return 0;
        return itemsPruebaActual.stream().mapToInt(ItemPrueba::getTiempoEstimado).sum();
    }

    public ItemPrueba getItemActualDePrueba() { 
        if (indiceItemActual >= 0 && indiceItemActual < itemsPruebaActual.size()) {
            return itemsPruebaActual.get(indiceItemActual);
        }
        return null;
    }

    public int getIndiceItemActualDePrueba() { 
        return indiceItemActual;
    }

    public int getTotalItemsEnPruebaActual() {
        return itemsPruebaActual.size();
    }

    public void avanzarItemEnPrueba(boolean esModoRevision) {
        if (indiceItemActual < itemsPruebaActual.size() - 1) {
            indiceItemActual++;
            notificarCambioItemEnPrueba(esModoRevision);
        } else {
            notificarCambioItemEnPrueba(esModoRevision);
        }
    }

    public void retrocederItemEnPrueba(boolean esModoRevision) {
        if (indiceItemActual > 0) {
            indiceItemActual--;
            notificarCambioItemEnPrueba(esModoRevision);
        } else {
            notificarCambioItemEnPrueba(esModoRevision);
        }
    }

    public void irAItemEnPrueba(int indice, boolean esModoRevision) { 
        if (indice >= 0 && indice < itemsPruebaActual.size()) {
            indiceItemActual = indice;
            notificarCambioItemEnPrueba(esModoRevision);
        }
    }

    public void guardarRespuestaUsuarioParaItemActual(Object respuesta) {
        ItemPrueba actual = getItemActualDePrueba();
        if (actual != null) {
            actual.setRespuestaUsuario(respuesta);
        }
    }

    public void calcularYNotificarResultadosDePrueba() { 
        if (itemsPruebaActual == null || itemsPruebaActual.isEmpty()) {
            notificarResultados(new EnumMap<>(NivelBloom.class), new HashMap<>());
            return;
        }

        Map<NivelBloom, Long> correctasPorNivelContador = new EnumMap<>(NivelBloom.class);
        Map<NivelBloom, Long> totalesPorNivelContador = new EnumMap<>(NivelBloom.class);
        Map<String, Long> correctasPorTipoContador = new HashMap<>();
        Map<String, Long> totalesPorTipoContador = new HashMap<>();

        for (ItemPrueba item : itemsPruebaActual) {
            NivelBloom nivel = item.getNivelBloom();
            String tipo = item.getTipoItem();

            totalesPorNivelContador.merge(nivel, 1L, Long::sum);
            totalesPorTipoContador.merge(tipo, 1L, Long::sum);

            if (item.esRespuestaCorrecta()) {
                correctasPorNivelContador.merge(nivel, 1L, Long::sum);
                correctasPorTipoContador.merge(tipo, 1L, Long::sum);
            }
        }

        Map<NivelBloom, Double> porcentajeCorrectasPorNivel = new EnumMap<>(NivelBloom.class);
        for (NivelBloom nivel : NivelBloom.values()) {
            long correctas = correctasPorNivelContador.getOrDefault(nivel, 0L);
            long totales = totalesPorNivelContador.getOrDefault(nivel, 0L);
            porcentajeCorrectasPorNivel.put(nivel, (totales == 0) ? 0.0 : (double) correctas * 100.0 / totales);
        }

        Map<String, Double> porcentajeCorrectasPorTipo = new HashMap<>();
        for (String tipo : totalesPorTipoContador.keySet()) {
            long correctas = correctasPorTipoContador.getOrDefault(tipo, 0L);
            long totales = totalesPorTipoContador.get(tipo);
            porcentajeCorrectasPorTipo.put(tipo, (totales == 0) ? 0.0 : (double) correctas * 100.0 / totales);
        }
        notificarResultados(porcentajeCorrectasPorNivel, porcentajeCorrectasPorTipo);
    }

    public boolean agregarNuevoItemAlBanco(ItemPrueba nuevoItem) {
        if (nuevoItem == null) {
            LOGGER.log(Level.WARNING, "Intento de agregar un ítem nulo al banco.");
            return false;
        }
        boolean exito = itemDAO.insertarItem(nuevoItem);
        if (exito) {
            LOGGER.log(Level.INFO, "Ítem agregado al banco: " + nuevoItem.getEnunciado());
            notificarModificacionBanco();
        } else {
            LOGGER.log(Level.WARNING, "No se pudo agregar el ítem al banco: " + nuevoItem.getEnunciado());
        }
        return exito;
    }

    public boolean modificarItemExistenteDelBanco(ItemPrueba itemModificado) {
        if (itemModificado == null || itemModificado.getId() <= 0) {
            LOGGER.log(Level.WARNING, "Intento de modificar un ítem nulo o con ID inválido.");
            return false;
        }
        boolean exito = itemDAO.actualizarItem(itemModificado);
        if (exito) {
            LOGGER.log(Level.INFO, "Ítem modificado en el banco - ID: " + itemModificado.getId());
            notificarModificacionBanco();
        } else {
            LOGGER.log(Level.WARNING, "No se pudo modificar el ítem en el banco - ID: " + itemModificado.getId());
        }
        return exito;
    }

    public boolean removerItemDelBancoPorId(int idDelItem) {
        if (idDelItem <= 0) {
            LOGGER.log(Level.WARNING, "Intento de eliminar un ítem con ID inválido: " + idDelItem);
            return false;
        }
        boolean exito = itemDAO.eliminarItem(idDelItem);
        if (exito) {
            LOGGER.log(Level.INFO, "Ítem eliminado del banco - ID: " + idDelItem);
            notificarModificacionBanco();
        } else {
            LOGGER.log(Level.WARNING, "No se pudo eliminar el ítem del banco - ID: " + idDelItem);
        }
        return exito;
    }

    public List<ItemPrueba> listarTodosLosItemsDelBanco() {
        List<ItemPrueba> todosLosItems = itemDAO.obtenerTodosLosItems();
        LOGGER.log(Level.INFO, "Se listaron " + todosLosItems.size() + " ítems del banco.");
        return todosLosItems;
    }

    private void notificarCargaPrueba(int totalItems, int tiempoTotal, boolean exito, String mensaje) {
        for (PruebaObservador obs : new ArrayList<>(observadoresPrueba)) {
            try {
                obs.onPruebaCargada(totalItems, tiempoTotal, exito, mensaje);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error al notificar onPruebaCargada a observador: " + obs.getClass().getName(), e);
            }
        }
    }

    private void notificarCambioItemEnPrueba(boolean esModoRevision) {
        ItemPrueba item = getItemActualDePrueba();
        if (item != null || indiceItemActual == -1 || indiceItemActual >= itemsPruebaActual.size()) {
             for (PruebaObservador obs : new ArrayList<>(observadoresPrueba)) {
                try {
                    if (esModoRevision) {
                        obs.onItemRevisionCambiado(item, indiceItemActual, itemsPruebaActual.size(), (item != null ? item.getRespuestaUsuario() : null), (item != null && item.esRespuestaCorrecta()));
                    } else {
                        obs.onItemCambiado(item, indiceItemActual, itemsPruebaActual.size(), (item != null ? item.getRespuestaUsuario() : null));
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error al notificar cambio de ítem en prueba a observador: " + obs.getClass().getName(), e);
                }
            }
        }
    }

    private void notificarResultados(Map<NivelBloom, Double> porNivel, Map<String, Double> porTipo) {
        for (PruebaObservador obs : new ArrayList<>(observadoresPrueba)) {
            try {
                obs.onResultadosCalculados(porNivel, porTipo);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error al notificar resultados a observador: " + obs.getClass().getName(), e);
            }
        }
    }

    private void notificarModificacionBanco() {
        for (BancoPreguntasObservador obs : new ArrayList<>(observadoresBanco)) {
            try {
                obs.onBancoPreguntasModificado();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error al notificar modificación del banco a observador: " + obs.getClass().getName(), e);
            }
        }
    }
    
    /**
     * 
     * 
     *
     * @param anioPruebaDestino
     * @param numeroPreguntasDeseadas
     * @return
     * 
     */
    public List<Integer> generarSugerenciaPruebaAleatoria(int anioPruebaDestino, int numeroPreguntasDeseadas) {
        if (numeroPreguntasDeseadas <= 0) {
            return new ArrayList<>();
        }

        List<ItemPrueba> todosLosItemsDelBanco = itemDAO.obtenerTodosLosItems();
        List<ItemPrueba> itemsElegibles = new ArrayList<>();

        int anioAnterior = anioPruebaDestino - 1;
        for (ItemPrueba item : todosLosItemsDelBanco) {
            if (item.getAnioUso() != anioAnterior) {
                itemsElegibles.add(item);
            }
        }
        LOGGER.log(Level.INFO, "Items elegibles después del filtro de año ({0} total): {1}", new Object[]{itemsElegibles.size(), todosLosItemsDelBanco.size()});

        Collections.shuffle(itemsElegibles);

        List<ItemPrueba> seleccionadosParaPrueba = new ArrayList<>();
        for (int i = 0; i < itemsElegibles.size() && seleccionadosParaPrueba.size() < numeroPreguntasDeseadas; i++) {
            seleccionadosParaPrueba.add(itemsElegibles.get(i));
        }
        
        if (seleccionadosParaPrueba.size() < numeroPreguntasDeseadas) {
            LOGGER.log(Level.WARNING, "No se encontraron suficientes ítems elegibles ({0}) para cumplir con el número deseado ({1}) de preguntas para el año {2}.",
                    new Object[]{seleccionadosParaPrueba.size(), numeroPreguntasDeseadas, anioPruebaDestino});
        }

        return seleccionadosParaPrueba.stream() .map(ItemPrueba::getId) .collect(Collectors.toList());
    }

}
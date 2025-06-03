/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pruebasbloom.core.db;
import pruebasbloom.core.modelo.ItemOpcionMultiple;
import pruebasbloom.core.modelo.ItemPrueba;
import pruebasbloom.core.modelo.ItemVerdaderoFalso;
import pruebasbloom.core.modelo.NivelBloom;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlospetit
 */
public class ItemDAO {
    private static final Logger LOGGER = Logger.getLogger(ItemDAO.class.getName());
    private final Gson gson = new Gson();
    
    /**
     * 
     *
     * @param id El ID del item
     * @return 
     */
    public ItemPrueba getItemById(int id) {
        String sql = "SELECT id, enunciado, nivel_bloom, tiempo_estimado_seg, anio_uso, tipo_item, opciones_json, solucion_texto FROM items WHERE id = ?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToItemPrueba(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener ítem por ID: " + id, e);
        }
        return null;
    }
    
    /**
     * 
     *
     * @param ids
     * @return 
     */
    public List<ItemPrueba> getItemsByIds(List<Integer> ids) {
        List<ItemPrueba> items = new ArrayList<>();
        if (ids == null || ids.isEmpty()) {
            return items;
        }
        
        StringBuilder sqlBuilder = new StringBuilder("SELECT id, enunciado, nivel_bloom, tiempo_estimado_seg, anio_uso, tipo_item, opciones_json, solucion_texto FROM items WHERE id IN (");
        for (int i = 0; i < ids.size(); i++) {
            sqlBuilder.append("?");
            if (i < ids.size() - 1) {
                sqlBuilder.append(",");
            }
        }
        sqlBuilder.append(")");

        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {

            // Establecer los valores de los IDs en el PreparedStatement
            for (int i = 0; i < ids.size(); i++) {
                pstmt.setInt(i + 1, ids.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ItemPrueba item = mapResultSetToItemPrueba(rs);
                    if (item != null) {
                        items.add(item);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener ítems por lista de IDs", e);
        }
        return items;
    }

    /**
     * 
     *
     * @param rs
     * @returnItemPrueba.
     * @throws SQLException
     */
    private ItemPrueba mapResultSetToItemPrueba(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String enunciado = rs.getString("enunciado");
        NivelBloom nivel = NivelBloom.fromString(rs.getString("nivel_bloom"));
        int tiempo = rs.getInt("tiempo_estimado_seg");
        int anioUso = rs.getInt("anio_uso");
        String tipoItem = rs.getString("tipo_item");
        String opcionesJson = rs.getString("opciones_json");
        String solucionTexto = rs.getString("solucion_texto");

        try {
            if ("OM".equalsIgnoreCase(tipoItem)) {
                Type listType = new TypeToken<ArrayList<String>>() {}.getType();
                List<String> opciones = gson.fromJson(opcionesJson, listType);
                if (opciones == null) { 
                    opciones = new ArrayList<>();
                     LOGGER.log(Level.WARNING, "Ítem de Opción Múltiple (ID: " + id + ") tiene opciones_json nulas en BD. Usando lista vacía.");
                }
                int indiceCorrecto = Integer.parseInt(solucionTexto);
                return new ItemOpcionMultiple(id, enunciado, nivel, tiempo, anioUso, opciones, indiceCorrecto);
            } else if ("VF".equalsIgnoreCase(tipoItem)) {
                boolean solucionCorrecta = Boolean.parseBoolean(solucionTexto);
                return new ItemVerdaderoFalso(id, enunciado, nivel, tiempo, anioUso, solucionCorrecta);
            } else {
                LOGGER.log(Level.WARNING, "Tipo de ítem desconocido en BD: '" + tipoItem + "' para ID: " + id);
                return null;
            }
        } catch (JsonSyntaxException e) {
            LOGGER.log(Level.SEVERE, "Error al parsear JSON para opciones del ítem ID: " + id + ". JSON: " + opcionesJson, e);
            return null;
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Error al parsear solucion_texto a número para ítem OM ID: " + id + ". Texto: " + solucionTexto, e);
            return null;
        } catch (IllegalArgumentException e) { // Captura de NivelBloom.fromString
            LOGGER.log(Level.SEVERE, "Error al parsear nivel_bloom para ítem ID: " + id + ". Valor en BD: " + rs.getString("nivel_bloom"), e);
            return null;
        }
    }

    /**
     * 
     *
     * @param item 
     * @return true
     */
    public boolean insertarItem(ItemPrueba item) {
        String sql = "INSERT INTO items (enunciado, nivel_bloom, tiempo_estimado_seg, anio_uso, tipo_item, opciones_json, solucion_texto) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getEnunciado());
            pstmt.setString(2, item.getNivelBloom().name());
            pstmt.setInt(3, item.getTiempoEstimado());
            pstmt.setInt(4, item.getAnioUso());

            if (item instanceof ItemOpcionMultiple) {
                ItemOpcionMultiple om = (ItemOpcionMultiple) item;
                pstmt.setString(5, "OM");
                pstmt.setString(6, gson.toJson(om.getOpciones()));
                pstmt.setString(7, String.valueOf(om.getIndiceOpcionCorrecta()));
            } else if (item instanceof ItemVerdaderoFalso) {
                ItemVerdaderoFalso vf = (ItemVerdaderoFalso) item;
                pstmt.setString(5, "VF");
                pstmt.setNull(6, java.sql.Types.VARCHAR); 
                pstmt.setString(7, String.valueOf(vf.getSolucionCorrectaVF()));
            } else {
                LOGGER.log(Level.WARNING, "Tipo de ítem no soportado para inserción: " + item.getClass().getName());
                return false;
            }

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al insertar ítem: " + item.getEnunciado(), e);
        }
        return false;
    }

    /**
     *
     * @param item
     * @return true
     */
    public boolean actualizarItem(ItemPrueba item) {
        String sql = "UPDATE items SET enunciado = ?, nivel_bloom = ?, tiempo_estimado_seg = ?, anio_uso = ?, tipo_item = ?, opciones_json = ?, solucion_texto = ? WHERE id = ?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getEnunciado());
            pstmt.setString(2, item.getNivelBloom().name());
            pstmt.setInt(3, item.getTiempoEstimado());
            pstmt.setInt(4, item.getAnioUso());

            if (item instanceof ItemOpcionMultiple) {
                ItemOpcionMultiple om = (ItemOpcionMultiple) item;
                pstmt.setString(5, "OM");
                pstmt.setString(6, gson.toJson(om.getOpciones()));
                pstmt.setString(7, String.valueOf(om.getIndiceOpcionCorrecta()));
            } else if (item instanceof ItemVerdaderoFalso) {
                ItemVerdaderoFalso vf = (ItemVerdaderoFalso) item;
                pstmt.setString(5, "VF");
                pstmt.setNull(6, java.sql.Types.VARCHAR);
                pstmt.setString(7, String.valueOf(vf.getSolucionCorrectaVF()));
            } else {
                LOGGER.log(Level.WARNING, "Tipo de ítem no soportado para actualización: " + item.getClass().getName());
                return false;
            }
            pstmt.setInt(8, item.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar ítem con ID: " + item.getId(), e);
        }
        return false;
    }

    /**
     * 
     *
     * @param id
     * @return true
     */
    public boolean eliminarItem(int id) {
        String sql = "DELETE FROM items WHERE id = ?";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar ítem con ID: " + id, e);
        }
        return false;
    }

    /**
     * 
     * @return list
     */
    public List<ItemPrueba> obtenerTodosLosItems() {
        List<ItemPrueba> items = new ArrayList<>();
        String sql = "SELECT id, enunciado, nivel_bloom, tiempo_estimado_seg, anio_uso, tipo_item, opciones_json, solucion_texto FROM items ORDER BY id ASC";
        try (Connection conn = ConexionMySQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                ItemPrueba item = mapResultSetToItemPrueba(rs);
                if (item != null) {
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener todos los ítems", e);
        }
        return items;
    }
    
}

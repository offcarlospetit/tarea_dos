/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pruebasbloom.core.db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlospetit
 */
public class ConexionMySQL {
    private static final String URL_BASE_DATOS = "jdbc:mysql://localhost:3306/tarea_dos";
    private static final String USUARIO_BD = "super_dev_local";
    private static final String CONTRASENA_BD = "micontrasena123";
    private static final Logger LOGGER = Logger.getLogger(ConexionMySQL.class.getName());
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error: Driver JDBC de MySQL no encontrado. Asegúrate de que el conector JAR esté en el classpath.", e);
            throw new SQLException("Driver MySQL no encontrado.", e);
        }
        return DriverManager.getConnection(URL_BASE_DATOS, USUARIO_BD, CONTRASENA_BD);
    }
    
    public static boolean probarConexion() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                LOGGER.info("¡Conexión a la base de datos exitosa!");
                return true;
            } else {
                LOGGER.warning("No se pudo establecer la conexión a la base de datos, la conexión es nula o está cerrada.");
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al conectar con la base de datos durante la prueba de conexión.", e);
            return false;
        }
    }
    
    public static void main(String[] args) {
        if (probarConexion()) {
            System.out.println("La prueba de conexión desde main() fue exitosa.");
        } else {
            System.err.println("La prueba de conexión desde main() falló. Revisa la consola y la configuración.");
        }
    }
}

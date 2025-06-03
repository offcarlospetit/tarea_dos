/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pruebasbloom;
import javax.swing.SwingUtilities;
import pruebasbloom.core.db.ConexionMySQL;
import pruebasbloom.gui.VentanaPrincipal;

/**
 *
 * @author carlospetit
 */
public class Pruebasbloom {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
            new VentanaPrincipal(); // Esto crea y muestra tu ventana
        }
    });
        
//        boolean conexionExitosa = ConexionMySQL.probarConexion();
//        if (conexionExitosa) {
//            System.out.println("¡Conexión a la base de datos establecida exitosamente desde Pruebasbloom!");
//            pruebasbloom.core.db.ItemDAO itemDAO = new pruebasbloom.core.db.ItemDAO();
//            pruebasbloom.core.modelo.ItemPrueba item = itemDAO.getItemById(1); // Asume que existe un ítem con ID 1
//            if (item != null) {
//                System.out.println("Ítem recuperado: " + item.getEnunciado());
//            } else {
//                System.out.println("No se pudo recuperar el ítem con ID 1.");
//            }
//            */
//        } else {
//            System.err.println("Fallo al conectar con la base de datos desde Pruebasbloom. Revisa la consola para más detalles.");
//        }
//
//        System.out.println("Prueba de conexión finalizada.");
    }
    
}

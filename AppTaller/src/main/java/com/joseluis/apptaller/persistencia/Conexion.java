package com.joseluis.apptaller.persistencia;

/**
 * Implementa el patrón de diseño Singleton para gestionar de forma centralizada y 
 * eficiente el acceso al servidor de base de datos MySQL.
 */

/**
 *
 * @author Jose Luis Cárdenas Barroso
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * Gestión de la conexión a la base de datos apptaller_db
 * Patrón Singleton para garantizar que solo exista una instancia de la conexión activa en memoria, 
 * evitando saturar el servidor MySQL con múltiples conexiones innecesarias cada vez que se hace una consulta.
 */
public class Conexion {
    private static Conexion instancia;
    private Connection connection;
    
    // Configuración de la BD
    private final String URL = "jdbc:mysql://localhost:3306/apptaller_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private final String USER = "root";
    private final String PASSWORD = ""; 

    // Constructor privado (Singleton)
    private Conexion() {
        try {
            // Cargar el driver 
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println(">>> Conexión a MySQL establecida con éxito.");
            
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error conectando a la base de datos: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Error crítico de conexión a Base de Datos.\nVerifique que MySQL está corriendo.", 
                "Error de Conexión", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Obtiene la instancia única de la clase Conexion.
     * @return instancia de Conexion
     */
    public static Conexion getInstancia() {
        if (instancia == null) {
            instancia = new Conexion();
        } else {
            try {
                // Si la conexión se cerró o no es válida, intentamos reconectar
                if (instancia.connection == null || instancia.connection.isClosed()) {
                    instancia = new Conexion();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return instancia;
    }

    /**
     * Devuelve el objeto Connection para realizar consultas.
     * @return objeto java.sql.Connection
     */
    public Connection getConnection() {
        return connection;
    }
    
    /**
     * Cierra la conexión manualmente si es necesario.
     */
    public void cerrarConexion() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println(">>> Conexión cerrada.");
            } catch (SQLException ex) {
                Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}

package com.joseluis.apptaller.persistencia;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
/**
* Gestiona la conexión de la aplicación con la base de datos MySQL.
* Utiliza el patrón de diseño Singleton para garantizar que solo exista una
* única conexión activa en todo momento, optimizando los recursos del sistema.
*
* @author José Luis Cárdenas Barroso
* @info Proyecto Intermodular del Grado Superior DAM
* @institution IES Augustóbriga
*/
public class Conexion {
    private static Conexion instancia;
    private Connection connection;

    // Valores por defecto
    private String url = "jdbc:mysql://localhost:3306/apptaller_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private String user = "root";
    private String password = "";

    // Constructor privado (Singleton)
    private Conexion() {
        cargarPropiedades();
        try {
            // Cargar el driver 
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, user, password);
            System.out.println(">>> Conexión a MySQL establecida con éxito.");

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error conectando a la base de datos: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Error crítico de conexión a Base de Datos.\nVerifique que MySQL está corriendo y que 'config.properties' tiene los datos correctos.", 
                "Error de Conexión", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Lee las credenciales desde un archivo externo.
     */
    private void cargarPropiedades() {
        Properties prop = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            prop.load(fis);
            String dbHost = prop.getProperty("db.host", "localhost");
            String dbPort = prop.getProperty("db.port", "3306");
            String dbName = prop.getProperty("db.name", "apptaller_db");
            
            this.url = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            this.user = prop.getProperty("db.user", "root");
            this.password = prop.getProperty("db.password", "");
            System.out.println(">>> Archivo config.properties cargado correctamente.");
        } catch (IOException ex) {
            System.out.println(">>> Archivo config.properties no encontrado. Se usarán credenciales por defecto.");
        }
    }

    public static Conexion getInstancia() {
        if (instancia == null) {
            instancia = new Conexion();
        } else {
            try {
                if (instancia.connection == null || instancia.connection.isClosed()) {
                    instancia = new Conexion();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return instancia;
    }

    public Connection getConnection() {
        return connection;
    }

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
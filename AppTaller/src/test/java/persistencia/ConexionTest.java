package com.joseluis.apptaller.persistencia;

import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase Conexion (Patrón Singleton).
 */
public class ConexionTest {
    
    private Connection connection;

    @BeforeEach
    public void setUp() {
        // Se ejecuta ANTES de cada prueba
        // Intentamos obtener la instancia
        connection = Conexion.getInstancia().getConnection();
    }

    @AfterEach
    public void tearDown() {
        // Se ejecuta DESPUÉS de cada prueba
        // Cerramos la conexión para no dejar procesos abiertos
        Conexion.getInstancia().cerrarConexion();
    }

    @Test
    public void testConexionExitosa() {
        System.out.println("Prueba: Verificar conexión a apptaller_db");
        
        // 1. Verificamos que el objeto connection no sea null
        assertNotNull(connection, "La conexión no debería ser nula");
        
        // 2. Verificamos que la conexión esté válida (abierta)
        try {
            assertFalse(connection.isClosed(), "La conexión debería estar abierta");
        } catch (SQLException e) {
            fail("Excepción SQL al verificar estado: " + e.getMessage());
        }
    }
    
    @Test
    public void testSingleton() {
        System.out.println("Prueba: Verificar patrón Singleton (Misma instancia)");
        
        // Obtenemos dos instancias diferentes
        Conexion c1 = Conexion.getInstancia();
        Conexion c2 = Conexion.getInstancia();
        
        // Deben ser exactamente el mismo objeto en memoria
        assertSame(c1, c2, "El patrón Singleton falló: se crearon dos instancias diferentes");
    }
}

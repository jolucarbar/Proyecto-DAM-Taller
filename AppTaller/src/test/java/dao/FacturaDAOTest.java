package com.joseluis.apptaller.modelo.dao;

import com.joseluis.apptaller.modelo.vo.FacturaVO;
import com.joseluis.apptaller.persistencia.Conexion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de integración para FacturaDAO.
 */
public class FacturaDAOTest {

    private static FacturaDAO facturaDAO;

    @BeforeAll
    public static void setUpClass() {
        facturaDAO = new FacturaDAO();
        
        // TECH LEAD: Inyectamos los datos maestros en minúsculas para evitar problemas de Case-Sensitivity en Linux
        try (Connection conn = Conexion.getInstancia().getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 1. Aseguramos que existe el usuario y el empleado 1 (necesario para la reparación)
            stmt.executeUpdate("INSERT IGNORE INTO usuarios (id_usuario, username, password_hash, rol) VALUES (1, 'testuser', 'hash', 'Empleado')");
            stmt.executeUpdate("INSERT IGNORE INTO empleados (id_empleado, usuario_id, dni, nombre, apellidos, fecha_alta) VALUES (1, 1, '00000000T', 'Mecánico', 'Test', CURDATE())");
            
            // 2. Insertamos Cliente, Vehículo y Reparación
            stmt.executeUpdate("INSERT IGNORE INTO clientes (dni_cif, nombre, fecha_registro, activo) VALUES ('12345678Z', 'Cliente Test JUnit', CURDATE(), 1)");
            stmt.executeUpdate("INSERT IGNORE INTO vehiculos (bastidor, matricula, propietario_actual_dni) VALUES ('BASTIDOR_TEST_01', '9999TST', '12345678Z')");
            stmt.executeUpdate("INSERT IGNORE INTO reparaciones (id_reparacion, vehiculo_bastidor, cliente_dni, empleado_asignado_id, fecha_entrada, estado) VALUES (1, 'BASTIDOR_TEST_01', '12345678Z', 1, NOW(), 'FINALIZADA')");
            
            System.out.println(">>> Base de datos preparada para el test de Facturas.");
        } catch (SQLException e) {
            System.err.println("Error preparando datos de prueba: " + e.getMessage());
        }
    }

    @AfterEach
    public void tearDown() {
        // TECH LEAD: Limpiamos en minúsculas
        try (Connection conn = Conexion.getInstancia().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM facturas WHERE numero_factura LIKE 'TEST-%'");
            System.out.println(">>> Datos de prueba limpiados correctamente.");
        } catch (SQLException e) {
            System.err.println("Error limpiando datos de prueba: " + e.getMessage());
        }
    }

    @Test
    public void testInsertarFactura() {
        FacturaVO nuevaFactura = new FacturaVO();
        
        // Asignamos datos. Usamos timestamp para garantizar un numero_factura único.
        nuevaFactura.setNumeroFactura("TEST-" + System.currentTimeMillis()); 
        nuevaFactura.setIdReparacion(1); // Ya existe gracias al @BeforeAll
        nuevaFactura.setClienteDni("12345678Z"); 
        nuevaFactura.setVehiculoBastidor("BASTIDOR_TEST_01"); 
        
        nuevaFactura.setFechaEmision(LocalDate.now());
        nuevaFactura.setFechaVencimiento(LocalDate.now().plusDays(15));
        nuevaFactura.setBaseImponible(200.00);
        nuevaFactura.setIva(21.00);
        nuevaFactura.setTotalCobrado(242.00);
        nuevaFactura.setMetodoPago("Tarjeta");
        nuevaFactura.setEstado("EMITIDA");
        nuevaFactura.setObservaciones("Factura generada desde JUnit");
        nuevaFactura.setUsuarioEmisor(1); // El usuario Admin

        // Ejecutar el DAO
        boolean insertado = facturaDAO.insertar(nuevaFactura);

        // Validar
        assertTrue(insertado, "La factura debe insertarse correctamente en la base de datos.");
    }
}
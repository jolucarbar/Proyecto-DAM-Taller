package com.joseluis.apptaller.modelo.dao;

import com.joseluis.apptaller.modelo.vo.ClienteVO;
import com.joseluis.apptaller.persistencia.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClienteDAOTest {

    private ClienteDAO dao;
    private ClienteVO testCliente;
    private final String DNI_TEST = "12345678Z";

    @BeforeEach
    public void setUp() {
        dao = new ClienteDAO();
        
        // 1. LIMPIEZA PROFUNDA (Orden correcto para respetar Foreign Keys)
        limpiarDatosDePrueba();

        // 2. Preparamos el objeto para el test
        testCliente = new ClienteVO(DNI_TEST, "Juan Pérez", "600123456", "juan@email.com");
    }

   private void limpiarDatosDePrueba() {
        try (Connection conn = Conexion.getInstancia().getConnection()) {
            
            // PASO 0: Desactivamos temporalmente la comprobación de FK (sólo para Testing)
            try (java.sql.Statement stmt = conn.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            }

            // PASO 1: Borrar Reparaciones asociadas a los vehículos de este cliente
            String sqlReparaciones = "DELETE FROM reparaciones WHERE vehiculo_bastidor IN (SELECT bastidor FROM vehiculos WHERE propietario_actual_dni = ?)";
            try (PreparedStatement stmtR = conn.prepareStatement(sqlReparaciones)) {
                stmtR.setString(1, DNI_TEST);
                stmtR.executeUpdate();
            }

            // PASO 2: Borrar Facturas asociadas a este cliente (por si las hubiera)
            String sqlFacturas = "DELETE FROM facturas WHERE cliente_dni = ?";
            try (PreparedStatement stmtF = conn.prepareStatement(sqlFacturas)) {
                stmtF.setString(1, DNI_TEST);
                stmtF.executeUpdate();
            }

            // PASO 3: Borrar Vehículos del cliente
            String sqlVehiculos = "DELETE FROM vehiculos WHERE propietario_actual_dni = ?";
            try (PreparedStatement stmtV = conn.prepareStatement(sqlVehiculos)) {
                stmtV.setString(1, DNI_TEST);
                stmtV.executeUpdate();
            }

            // PASO 4: Borrar el Cliente
            String sqlCliente = "DELETE FROM clientes WHERE dni_cif = ?";
            try (PreparedStatement stmtC = conn.prepareStatement(sqlCliente)) {
                stmtC.setString(1, DNI_TEST);
                stmtC.executeUpdate();
            }
            
            // PASO 5: Volvemos a activar la seguridad referencial de MySQL 
            try (java.sql.Statement stmt = conn.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
            }

        } catch (Exception e) {
            System.err.println("Error crítico limpiando datos en ClienteDAOTest: " + e.getMessage());
        }
    }

    @Test
    public void testCrudCliente() {
        // 1. INSERTAR
        boolean insertado = dao.insertar(testCliente);
        assertTrue(insertado, "Debería insertar el cliente");

        // 2. LISTAR / BUSCAR
        // Usamos listar() y filtramos
        List<ClienteVO> lista = dao.listar(); 
        boolean existe = lista.stream().anyMatch(c -> c.getDni().equals(DNI_TEST));
        assertTrue(existe, "El cliente debería aparecer en el listado");

        // 3. ELIMINAR (Lógico)
        boolean eliminado = dao.eliminar(DNI_TEST); 
        assertTrue(eliminado, "Debería marcarse como eliminado/inactivo");
        
        // Verificar que ya no sale en la lista de activos
        lista = dao.listar();
        existe = lista.stream().anyMatch(c -> c.getDni().equals(DNI_TEST));
        assertFalse(existe, "El cliente eliminado no debe aparecer en la lista de activos");
    }
}
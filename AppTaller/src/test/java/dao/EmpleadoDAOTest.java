package com.joseluis.apptaller.modelo.dao;

import com.joseluis.apptaller.modelo.vo.EmpleadoVO;
import com.joseluis.apptaller.persistencia.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

public class EmpleadoDAOTest {

    private EmpleadoDAO dao;
    private EmpleadoVO testEmpleado;
    private final String DNI_TEST = "98765432D";

    @BeforeEach
    public void setUp() {
        dao = new EmpleadoDAO();
        
        limpiarDatosDePrueba();

        testEmpleado = new EmpleadoVO(2, 0, DNI_TEST, "Juan", "Gómez Pérez", "600123456", "juan@email.com", "Calle Baja", "Mecánico", LocalDate.of(2021, 10, 28), LocalDate.of(2025, 5, 28), (float) 22000.0);
    }

    private void limpiarDatosDePrueba() {
        try (Connection conn = Conexion.getInstancia().getConnection()) {
            
            // PASO 0: Desactivamos temporalmente la comprobación de FK (sólo para Testing)
            try (java.sql.Statement stmt = conn.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            }

            // PASO 1: Borrar el empleado
            String sqlEmpleado = "DELETE FROM empleados WHERE dni = ?";
            try (PreparedStatement stmtC = conn.prepareStatement(sqlEmpleado)) {
                stmtC.setString(1, DNI_TEST);
                stmtC.executeUpdate();
            }
            
            // PASO 2: Volvemos a activar la seguridad referencial de MySQL 
            try (java.sql.Statement stmt = conn.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
            }

        } catch (Exception e) {
            System.err.println("Error crítico limpiando datos en EmpleadoDAOTest: " + e.getMessage());
        }
    }

    @Test
    public void testCrudEmpleado() {
        // 1. INSERTAR
        boolean insertado = dao.insertar(testEmpleado);
        assertTrue(insertado, "Debería insertar el empleado");

        // 2. LISTAR / BUSCAR
        // Usamos listar() y filtramos
        List<EmpleadoVO> lista = dao.listar(); 
        boolean existe = lista.stream().anyMatch(c -> c.getDni().equals(DNI_TEST));
        assertTrue(existe, "El empleado debería aparecer en el listado");

        // 3. ELIMINAR (Lógico)
        boolean eliminado = dao.eliminar(DNI_TEST); 
        assertTrue(eliminado, "Debería marcarse como eliminado/inactivo");
        
        // Verificar que ya no sale en la lista de activos
        lista = dao.listar();
        existe = lista.stream().anyMatch(c -> c.getDni().equals(DNI_TEST));
        assertFalse(existe, "El empleado eliminado no debe aparecer en la lista de activos");
    }
}
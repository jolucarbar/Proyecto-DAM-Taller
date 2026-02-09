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
            // PASO 1: Borrar vehículos asociados a este cliente (si los hay)
            // Usamos 'propietario_actual_dni' como definimos en el DAO de vehículos
            String sqlVehiculos = "DELETE FROM vehiculos WHERE propietario_actual_dni = ?";
            try (PreparedStatement stmtV = conn.prepareStatement(sqlVehiculos)) {
                stmtV.setString(1, DNI_TEST);
                stmtV.executeUpdate();
            }

            // PASO 2: Ahora sí podemos borrar el cliente sin error de FK
            String sqlCliente = "DELETE FROM clientes WHERE dni_cif = ?";
            try (PreparedStatement stmtC = conn.prepareStatement(sqlCliente)) {
                stmtC.setString(1, DNI_TEST);
                stmtC.executeUpdate();
            }
        } catch (Exception e) {
            System.err.println("Error limpiando datos en ClienteDAOTest: " + e.getMessage());
        }
    }

    @Test
    public void testCrudCliente() {
        // 1. INSERTAR
        boolean insertado = dao.insertar(testCliente);
        assertTrue(insertado, "Debería insertar el cliente");

        // 2. LISTAR / BUSCAR
        // Usamos listar() y filtramos, o si tienes buscarPorDni() mejor.
        // Aquí asumo que comprobamos que existe en la lista general.
        List<ClienteVO> lista = dao.listar(); // Asumiendo que tienes un método listar()
        boolean existe = lista.stream().anyMatch(c -> c.getDni().equals(DNI_TEST));
        assertTrue(existe, "El cliente debería aparecer en el listado");

        // 3. ELIMINAR (Lógico)
        boolean eliminado = dao.eliminar(DNI_TEST); // Asumiendo que tu método recibe el DNI
        assertTrue(eliminado, "Debería marcarse como eliminado/inactivo");
        
        // Verificar que ya no sale en la lista de activos
        lista = dao.listar();
        existe = lista.stream().anyMatch(c -> c.getDni().equals(DNI_TEST));
        assertFalse(existe, "El cliente eliminado no debe aparecer en la lista de activos");
    }
}
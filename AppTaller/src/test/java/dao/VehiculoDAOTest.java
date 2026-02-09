package com.joseluis.apptaller.modelo.dao;

import com.joseluis.apptaller.modelo.vo.VehiculoVO;
import com.joseluis.apptaller.modelo.vo.ClienteVO;
import com.joseluis.apptaller.persistencia.Conexion; // Necesario para limpieza física
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VehiculoDAOTest {
    
    private VehiculoDAO vehiculoDAO;
    private ClienteDAO clienteDAO;
    private VehiculoVO testVehiculo;
    private final String DNI_TEST = "12345678Z"; 
    private final String BASTIDOR_TEST = "CHASIS_TEST_01";
    private final String MATRICULA_TEST = "9999TST";

    @BeforeEach
    public void setUp() {
        vehiculoDAO = new VehiculoDAO();
        clienteDAO = new ClienteDAO();

        // 1. LIMPIEZA FÍSICA (CRÍTICO PARA EVITAR ERROR DUPLICATE ENTRY)
        limpiarDatosDePrueba();

        // 2. Aseguramos que existe el Cliente
        ClienteVO cliente = new ClienteVO(DNI_TEST, "Cliente Test", "600123456", "test@email.com");
        try {
            clienteDAO.insertar(cliente); 
        } catch (Exception e) { 
            // Ignoramos si ya existe
        }

        // 3. Inicializamos el vehículo
        testVehiculo = new VehiculoVO(
            BASTIDOR_TEST,      // bastidor (PK)
            MATRICULA_TEST,     // matricula
            "TestMarca",        
            "TestModelo",       
            2024,               
            "Blanco",           
            DNI_TEST            
        );
    }
    
    /**
     * Método auxiliar para borrar físicamente los datos de prueba
     * y evitar conflictos de Clave Primaria en ejecuciones repetidas.
     */
    private void limpiarDatosDePrueba() {
        try (Connection conn = Conexion.getInstancia().getConnection()) {
            // Borramos primero el vehículo para no romper FK
            String sqlVehiculo = "DELETE FROM vehiculos WHERE bastidor = ? OR matricula = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlVehiculo)) {
                stmt.setString(1, BASTIDOR_TEST);
                stmt.setString(2, MATRICULA_TEST);
                stmt.executeUpdate();
            }
            
            // Opcional: Borrar el cliente también si quieres un test 100% limpio
            // Pero como lo reutilizamos, no es estrictamente necesario aquí.
        } catch (Exception e) {
            System.err.println("Error limpiando datos de prueba: " + e.getMessage());
        }
    }

    @Test
    public void testInsertarYListar() {
        // 1. Insertar
        boolean insertado = vehiculoDAO.insertar(testVehiculo);
        assertTrue(insertado, "El vehículo debería insertarse correctamente");

        // 2. Verificar
        List<VehiculoVO> lista = vehiculoDAO.listar();
        boolean encontrado = lista.stream()
                .anyMatch(v -> v.getMatricula().equals(MATRICULA_TEST));
        
        assertTrue(encontrado, "El vehículo insertado debe estar en la lista");
    }

    @Test
    public void testBorradoLogico() {
        // Aseguramos inserción previa
        vehiculoDAO.insertar(testVehiculo);
        
        // Ejecutamos borrado lógico
        boolean eliminado = vehiculoDAO.eliminar(MATRICULA_TEST);
        assertTrue(eliminado, "El método eliminar debe devolver true");
        
        // Verificamos que ya no sale en la lista (porque listar() filtra por activo=1)
        List<VehiculoVO> lista = vehiculoDAO.listar();
        boolean encontrado = lista.stream()
                .anyMatch(v -> v.getMatricula().equals(MATRICULA_TEST));
        
        assertFalse(encontrado, "El vehículo no debe aparecer tras el borrado lógico");
    }
}
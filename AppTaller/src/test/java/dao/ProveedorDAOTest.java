package com.joseluis.apptaller.modelo.dao;

import com.joseluis.apptaller.modelo.vo.ProveedorVO;
import com.joseluis.apptaller.persistencia.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProveedorDAOTest {

    private ProveedorDAO dao;
    private ProveedorVO testProveedor;
    private final String CIF_TEST = "B12345678";

    @BeforeEach
    public void setUp() {
        dao = new ProveedorDAO();
        limpiarDatosDePrueba();

        // Instanciamos un proveedor de prueba
        testProveedor = new ProveedorVO();
        testProveedor.setCif(CIF_TEST);
        testProveedor.setNombre("Recambios Paco S.L.");
        testProveedor.setDireccion("Polígono Industrial, Nave 4");
        testProveedor.setTelefono("900123456");
        testProveedor.setEmail("ventas@recambiospaco.com");
        testProveedor.setContacto("Paco Gómez");
        testProveedor.setWeb("www.recambiospaco.com");
        testProveedor.setCreated_at(java.time.LocalDate.now());
        testProveedor.setActivo(true);
    }

    private void limpiarDatosDePrueba() {
        try (Connection conn = Conexion.getInstancia().getConnection()) {
            try (java.sql.Statement stmt = conn.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            }

            String sql = "DELETE FROM proveedores WHERE cif = ?";
            try (PreparedStatement stmtC = conn.prepareStatement(sql)) {
                stmtC.setString(1, CIF_TEST);
                stmtC.executeUpdate();
            }

            try (java.sql.Statement stmt = conn.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
            }
        } catch (Exception e) {
            System.err.println("Error limpiando datos en ProveedorDAOTest: " + e.getMessage());
        }
    }

    @Test
    public void testCrudProveedor() {
        // 1. INSERTAR
        boolean insertado = dao.insertar(testProveedor);
        assertTrue(insertado, "Debería insertar el proveedor correctamente");

        // 2. LISTAR / BUSCAR
        List<ProveedorVO> lista = dao.listar(); 
        boolean existe = lista.stream().anyMatch(p -> p.getCif().equals(CIF_TEST));
        assertTrue(existe, "El proveedor debería aparecer en el listado de activos");

        // 3. ELIMINAR (Lógico)
        boolean eliminado = dao.eliminar(CIF_TEST); 
        assertTrue(eliminado, "Debería marcarse el proveedor como inactivo");
        
        // Verificar borrado lógico
        lista = dao.listar();
        existe = lista.stream().anyMatch(p -> p.getCif().equals(CIF_TEST));
        assertFalse(existe, "El proveedor eliminado no debe aparecer en la lista");
    }
}
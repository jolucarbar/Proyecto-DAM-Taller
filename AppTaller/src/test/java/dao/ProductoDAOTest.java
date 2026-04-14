package com.joseluis.apptaller.modelo.dao;

import com.joseluis.apptaller.modelo.vo.ProductoVO;
import com.joseluis.apptaller.persistencia.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProductoDAOTest {

    private ProductoDAO dao;
    private ProductoVO testProducto;
    private final String CODIGO_TEST = "PROD-TEST-001";

    @BeforeEach
    public void setUp() {
        dao = new ProductoDAO();
        limpiarDatosDePrueba();

        testProducto = new ProductoVO();
        testProducto.setIdProducto(CODIGO_TEST); 
        testProducto.setNombre("Filtro de Aceite Bosh");
        testProducto.setDescripcion("Filtro de aceite estándar para motor diésel");
        testProducto.setCategoria("Filtros");
        testProducto.setCantidadStock(50);
        testProducto.setStockMinimo(10);
        testProducto.setPrecioCompra(5.50f);
        testProducto.setPrecioUnitario(12.99f);
        testProducto.setProveedorCif(null); 
        testProducto.setCreated_at(new java.util.Date());
        testProducto.setActivo(true);
    }

    private void limpiarDatosDePrueba() {
        try (Connection conn = Conexion.getInstancia().getConnection()) {
            try (java.sql.Statement stmt = conn.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            }

            String sql = "DELETE FROM productos WHERE id_producto = ?";
            try (PreparedStatement stmtC = conn.prepareStatement(sql)) {
                stmtC.setString(1, CODIGO_TEST);
                stmtC.executeUpdate();
            }

            try (java.sql.Statement stmt = conn.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
            }
        } catch (Exception e) {
            System.err.println("Error limpiando datos en ProductoDAOTest: " + e.getMessage());
        }
    }

    @Test
    public void testCrudProducto() {
        boolean insertado = dao.insertar(testProducto);
        assertTrue(insertado, "Debería insertar el producto");

        List<ProductoVO> lista = dao.listar(); 
        boolean existe = lista.stream().anyMatch(p -> p.getIdProducto().equals(CODIGO_TEST));
        assertTrue(existe, "El producto debería aparecer listado");

        boolean eliminado = dao.eliminar(CODIGO_TEST); 
        assertTrue(eliminado, "Debería eliminar (lógicamente) el producto");
    }
}
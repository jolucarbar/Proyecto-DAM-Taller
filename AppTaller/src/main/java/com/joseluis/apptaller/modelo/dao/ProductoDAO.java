
package com.joseluis.apptaller.modelo.dao;

import com.joseluis.apptaller.modelo.vo.ProductoVO;
import com.joseluis.apptaller.persistencia.Conexion; 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
* DAO para la gestión de productos e inventario.
* Implementa control de stock y alertas de stock bajo.
* 
*  @author Jose Luis Cárdenas Barroso
*/
public class ProductoDAO {
    
// Consultas SQL  
    private final String SQL_INSERT = "INSERT INTO productos (id_producto, nombre, descripcion, categoria, "
            + "cantidad_stock, stock_minimo, precio_compra, precio_unitario, proveedor_cif, ubicacion_almacen) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
   
    private final String SQL_SELECT_ALL = "SELECT * FROM productos WHERE activo = TRUE";
   
    private final String SQL_SELECT_BY_ID = "SELECT * FROM productos WHERE id_producto = ? AND activo = TRUE";
   
    private final String SQL_UPDATE = "UPDATE productos SET nombre=?, descripcion=?, categoria=?, "
            + "stock_minimo=?, precio_compra=?, precio_unitario=?, proveedor_cif=?, ubicacion_almacen=? "
            + "WHERE id_producto=?";
   
    private final String SQL_DELETE = "UPDATE productos SET activo = FALSE WHERE id_producto = ?";
   
    // Consulta para la vista de stock bajo definida en el script
    private final String SQL_STOCK_BAJO = "SELECT * FROM vw_productos_stock_bajo";

    /**
     * Registra un nuevo producto en la base de datos.
     */
    public boolean insertar(ProductoVO p) {
        try (Connection conn = Conexion.getInstancia().getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {
           
            stmt.setString(1, p.getIdProducto());
            stmt.setString(2, p.getNombre());
            stmt.setString(3, p.getDescripcion());
            stmt.setString(4, p.getCategoria());
            stmt.setInt(5, p.getCantidadStock());
            stmt.setInt(6, p.getStockMinimo());
            stmt.setDouble(7, p.getPrecioCompra());
            stmt.setDouble(8, p.getPrecioUnitario());
            stmt.setString(9, p.getProveedorCif());
            stmt.setString(10, p.getUbicacionAlmacen());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar producto: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lista todos los productos activos para la tabla principal.
     */
    public List<ProductoVO> listar() {
        List<ProductoVO> lista = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
           
            while (rs.next()) {
                lista.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar productos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Obtiene productos que están bajo el stock mínimo.
     */
    public List<ProductoVO> listarStockBajo() {
        List<ProductoVO> lista = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(SQL_STOCK_BAJO);
             ResultSet rs = stmt.executeQuery()) {
           
            while (rs.next()) {
                lista.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar stock bajo: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Actualiza el stock de forma atómica (usado por reparaciones).
     * @param id Identificador del producto
     * @param cantidad Cantidad a sumar (positiva) o restar (negativa)
     */
    public boolean actualizarStock(String id, int cantidad) {
        String sql = "UPDATE productos SET cantidad_stock = cantidad_stock + ? WHERE id_producto = ?";
        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cantidad);
            stmt.setString(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar stock: " + e.getMessage());
            return false;
        }
    }

    /**
     * Mapea una fila de la base de datos a un objeto VO.
     */
    private ProductoVO mapearProducto(ResultSet rs) throws SQLException {
        ProductoVO p = new ProductoVO();
        p.setIdProducto(rs.getString("id_producto"));
        p.setNombre(rs.getString("nombre"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setCategoria(rs.getString("categoria"));
        p.setCantidadStock(rs.getInt("cantidad_stock"));
        p.setStockMinimo(rs.getInt("stock_minimo"));
        p.setPrecioCompra(rs.getDouble("precio_compra"));
        p.setPrecioUnitario(rs.getDouble("precio_unitario"));
        p.setProveedorCif(rs.getString("proveedor_cif"));
        p.setUbicacionAlmacen(rs.getString("ubicacion_almacen"));
        p.setActivo(rs.getBoolean("activo"));
        p.setCreated_at(rs.getTimestamp("created_at"));
        return p;
    }

    public boolean eliminar(String idProducto) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = Conexion.getInstancia().getConnection();
            stmt = conn.prepareStatement(SQL_DELETE);
            stmt.setString(1, idProducto);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            return false;
        } finally {
            close(stmt);
        }
    }
    
    public boolean modificar(ProductoVO p) {
    try (Connection conn = Conexion.getInstancia().getConnection();
         PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
        
        stmt.setString(1, p.getNombre());
        stmt.setString(2, p.getDescripcion());
        stmt.setString(3, p.getCategoria());
        stmt.setInt(4, p.getStockMinimo());
        stmt.setDouble(5, p.getPrecioCompra());
        stmt.setDouble(6, p.getPrecioUnitario());
        stmt.setString(7, p.getProveedorCif());
        stmt.setString(8, p.getUbicacionAlmacen());
        stmt.setString(9, p.getIdProducto());

        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error al modificar: " + e.getMessage());
        return false;
    }
}
    
    // Métodos auxiliares para cerrar recursos
    private void close(PreparedStatement stmt) { close(stmt, null); }
    private void close(PreparedStatement stmt, ResultSet rs) {
        try { if(rs != null) rs.close(); } catch(SQLException e){}
        try { if(stmt != null) stmt.close(); } catch(SQLException e){}
    }

    public ProductoVO buscarPorIU(String id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

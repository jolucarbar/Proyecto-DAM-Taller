
package com.joseluis.apptaller.modelo.dao;

import com.joseluis.apptaller.modelo.vo.ProductoVO;
import com.joseluis.apptaller.persistencia.Conexion; 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona el inventario de piezas y materiales en la base de datos.
 * Permite registrar, editar, eliminar y consultar los productos, además de 
 * llevar el control del stock y detectar cuándo un artículo está bajo mínimos.
 * 
 * @author José Luis Cárdenas Barroso
 * @info Proyecto Intermodular del Grado Superior DAM
 * @institution IES Augustóbriga
 */
public class ProductoDAO {
    
// Consultas SQL  
    private final String SQL_INSERT = "INSERT INTO productos (id_producto, nombre, descripcion, categoria, "
            + "cantidad_stock, stock_minimo, precio_compra, precio_unitario, proveedor_cif, ubicacion_almacen) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
   
    private final String SQL_SELECT_ALL = "SELECT * FROM productos WHERE activo = TRUE";
   
    private final String SQL_SELECT_BUSCAR = "SELECT * FROM productos WHERE id_producto LIKE ? OR nombre LIKE ? AND activo = TRUE";
   
    private final String SQL_UPDATE = "UPDATE productos SET nombre=?, descripcion=?, categoria=?, "
            + "stock_minimo=?, precio_compra=?, precio_unitario=?, proveedor_cif=?, ubicacion_almacen=? "
            + "WHERE id_producto=?";
   
    private final String SQL_DELETE = "UPDATE productos SET activo = FALSE WHERE id_producto = ?";
   
    // Consulta para la vista de stock bajo definida en el script
    private final String SQL_STOCK_BAJO = "SELECT * FROM vw_productos_stock_bajo";
    
    // Actualiza el stock de un producto
    private final String SQL_UPDATE_STOCK = "UPDATE productos SET cantidad_stock = cantidad_stock + ? WHERE id_producto = ?";

    // Actualiza el stock descontando el producto indicado
    // Hacemos la resta directamente en el motor de base de datos (stock = cantidad_stock - ?)
    private final String SQL_DESCUENTA_STOCK = "UPDATE productos SET cantidad_stock = cantidad_stock - ? WHERE id_producto = ?";
    
    // Selecciona productos con stock por debajo del mínimo
    // WHERE: comparamos dos columnas de la misma tabla
    private final String SQL_SELECT_STOCK_BAJO = "SELECT * FROM productos WHERE cantidad_stock <= stock_minimo";
    
    // Suma y actualiza el stock de un producto
    // Utilizamos el motor SQL para sumar de forma atómica y segura
    private final String SQL_SUMA_STOCK = "UPDATE productos SET cantidad_stock = cantidad_stock + ? WHERE id_producto = ?";
    
    
    
    
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
            stmt.setDouble(8, p.getPrecioUnitario(12.99F));
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
        
        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_STOCK)) {
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
        stmt.setDouble(6, p.getPrecioUnitario(12.99F));
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

    /**
     * Método que busca un producto y devuelve las coincidencias del parámetro.
     * @param busqueda texto introducido por el usuario
     * @return lista con los resultados encotrados
     */
    public List<ProductoVO> buscarProducto (String busqueda) {
        List<ProductoVO> lista = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BUSCAR)) {
            
            String busquedaFormateada = "%" + busqueda + "%";
            stmt.setString(1, busquedaFormateada);  // para el id_producto
            stmt.setString(2, busquedaFormateada);  // para el nombre
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearProducto(rs));
                }
            } 
            
        } catch (SQLException e) {
            System.err.println("Error al buscar producto: " + e.getMessage());
        }
        return lista;
    }
    
    
    
    /**
     * Actualiza la cantidad de stock de un producto específico.
     * Utiliza una resta relativa en SQL para evitar problemas de concurrencia 
     * si varios mecánicos guardan reparaciones al mismo tiempo.
     * * @param idProducto El identificador único del recambio.
     * @param cantidadUsada La cantidad que se ha consumido y se debe restar.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean descontarStock(String idProducto, int cantidadUsada) {
                
        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_DESCUENTA_STOCK)) {
            
            pstmt.setInt(1, cantidadUsada);
            pstmt.setString(2, idProducto);
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al descontar stock del producto " + idProducto + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Consulta la base de datos para recuperar únicamente aquellos productos 
     * cuya cantidad actual es igual o inferior a su stock de seguridad (stock mínimo).
     * * @return Una lista con los productos que necesitan ser repuestos.
     */
    public List<ProductoVO> listarBajoStock() {
        List<ProductoVO> listaBajoStock = new ArrayList<>();
        
        
        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_STOCK_BAJO);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                com.joseluis.apptaller.modelo.vo.ProductoVO p = new com.joseluis.apptaller.modelo.vo.ProductoVO();
                p.setIdProducto(rs.getString("id_producto"));
                p.setNombre(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setCategoria(rs.getString("categoria"));
                p.setCantidadStock(rs.getInt("cantidad_stock"));
                p.setStockMinimo(rs.getInt("stock_minimo"));
                p.setPrecioCompra(rs.getDouble("precio_compra"));
                p.setPrecioUnitario(rs.getDouble("precio_unitario"));
                p.setProveedorCif(rs.getString("proveedor_cif"));
                
                listaBajoStock.add(p);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al consultar el stock bajo: " + e.getMessage());
        }
        
        return listaBajoStock;
    }
    
    
     /**
     * Incrementa la cantidad de stock de un producto específico.
     * Para recepciones de mercancía de proveedores o ajustes de inventario.
     * * @param idProducto El identificador único del recambio.
     * @param cantidadRecibida La cantidad de unidades que entran al almacén.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean sumarStock(String idProducto, int cantidadRecibida) {
                
        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SUMA_STOCK)) {
            
            pstmt.setInt(1, cantidadRecibida);
            pstmt.setString(2, idProducto);
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
                        
        } catch (java.sql.SQLException e) {
            System.err.println("Error al sumar stock del producto " + idProducto + ": " + e.getMessage());
            return false;
        }
        
    }
    
}

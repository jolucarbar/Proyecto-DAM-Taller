
package com.joseluis.apptaller.modelo.dao;

import com.joseluis.apptaller.modelo.vo.ProveedorVO;
import com.joseluis.apptaller.persistencia.Conexion;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joseluis
 */
public class ProveedorDAO {
    
    // Consultas SQL
    private final String SQL_INSERT = "INSERT INTO proveedores (cif, nombre, direccion, telefono, email, contacto, web, activo, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final String SQL_SELECT_ALL = "SELECT * FROM proveedores WHERE activo = 1";
    private final String SQL_SELECT_BY_CIF = "SELECT * FROM proveedores WHERE cif = ? AND activo = 1";
    private final String SQL_UPDATE = "UPDATE proveedores SET cif=?, nombre=?, direccion=?, telefono=?, email=?, contacto=?, web=?, activo=?, created_at=? WHERE cif=?";
    private final String SQL_DELETE = "UPDATE proveedores SET activo = 0 WHERE cif = ?"; // Borrado lógico, no físico
    
    
    /**
     * Inserta un nuevo proveedor en la base de datos.
     */
    public boolean insertar(ProveedorVO proveedor) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = Conexion.getInstancia().getConnection();
            stmt = conn.prepareStatement(SQL_INSERT);
            
            stmt.setString(1, proveedor.getCif());
            stmt.setString(2, proveedor.getNombre());
            stmt.setString(3, proveedor.getDireccion());
            stmt.setString(4, proveedor.getTelefono());
            stmt.setString(5, proveedor.getEmail());
            stmt.setString(6, proveedor.getContacto());
            stmt.setString(7, proveedor.getWeb());
            stmt.setBoolean(8, proveedor.isActivo());
            // Convertir LocalDate a java.sql.Date
            stmt.setDate(9, Date.valueOf(proveedor.getCreated_at()));
            
            int filas = stmt.executeUpdate();
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al insertar proveedor: " + e.getMessage());
            return false;
        } finally {
             close(stmt);
        }
        
    }
    
    
    /**
     * Obtiene todos los clientes activos.
     * @return 
     */
    public List<ProveedorVO> listar() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<ProveedorVO> lista = new ArrayList<>();

        try {
            conn = Conexion.getInstancia().getConnection();
            stmt = conn.prepareStatement(SQL_SELECT_ALL);
            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearProveedor(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar proveedores: " + e.getMessage());
        } finally {
            close(stmt, rs);
        }
        return lista;
    }
    
    
    /**
     * Busca un cliente por su DNI.
     */
    public ProveedorVO buscarPorCif(String cif) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ProveedorVO proveedor = null;

        try {
            conn = Conexion.getInstancia().getConnection();
            stmt = conn.prepareStatement(SQL_SELECT_BY_CIF);
            stmt.setString(1, cif);
            rs = stmt.executeQuery();

            if (rs.next()) {
                proveedor = mapearProveedor(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar proveedor: " + e.getMessage());
        } finally {
            close(stmt, rs);
        }
        return proveedor;
    }
    
    /**
     * Actualiza los datos de un cliente.
     */
    public boolean modificar(ProveedorVO proveedor) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = Conexion.getInstancia().getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE);
            
            stmt.setString(1, proveedor.getCif());
            stmt.setString(2, proveedor.getNombre());
            stmt.setString(3, proveedor.getDireccion());
            stmt.setString(4, proveedor.getTelefono());
            stmt.setString(5, proveedor.getEmail());
            stmt.setString(6, proveedor.getContacto());
            stmt.setString(7, proveedor.getWeb());
            stmt.setBoolean(8, proveedor.isActivo());
            // Convertir LocalDate a java.sql.Date
            stmt.setDate(9, Date.valueOf(proveedor.getCreated_at()));

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar proveedor: " + e.getMessage());
            return false;
        } finally {
            close(stmt);
        }
    }
    
    
    /**
     * Realiza un borrado lógico (desactiva el cliente).
     * @param dni
     * @return 
     */
    public boolean eliminar(String cif) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = Conexion.getInstancia().getConnection();
            stmt = conn.prepareStatement(SQL_DELETE);
            stmt.setString(1, cif);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar proveedor: " + e.getMessage());
            return false;
        } finally {
            close(stmt);
        }
    }
    
    
    private ProveedorVO mapearProveedor(ResultSet rs) throws SQLException{
        ProveedorVO p = new ProveedorVO();
        p.setCif(rs.getString("cif"));
        p.setNombre(rs.getString("nombre"));
        p.setDireccion(rs.getString("direccion"));
        p.setTelefono(rs.getString("telefono"));
        p.setEmail(rs.getString("email"));
        p.setContacto(rs.getString("contacto"));
        p.setWeb(rs.getString("web"));
        p.setActivo(rs.getBoolean("activo"));
        
        Date fechaSql = rs.getDate("created_at");
        if(fechaSql != null) p.setCreated_at(fechaSql.toLocalDate());
        
        return p;    
    }
        
    
    // Métodos auxiliares para cerrar recursos
    private void close(PreparedStatement stmt) { close(stmt, null); }
    private void close(PreparedStatement stmt, ResultSet rs) {
        try { if(rs != null) rs.close(); } catch(SQLException e){}
        try { if(stmt != null) stmt.close(); } catch(SQLException e){}
    }
    
}


package com.joseluis.apptaller.modelo.dao;

import com.joseluis.apptaller.modelo.vo.EmpleadoVO;
import com.joseluis.apptaller.persistencia.Conexion;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joseluis
 */
public class EmpleadoDAO {
    // Consultas SQL
    private final String SQL_INSERT = "INSERT INTO empleados (usuario_id, dni, nombre, apellidos, telefono, email, "
            + "direccion, cargo, fecha_alta, fecha_baja, salario_base, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final String SQL_SELECT_ALL = "SELECT * FROM empleados WHERE activo = 1";
    private final String SQL_SELECT_BY_DNI = "SELECT * FROM empleados WHERE dni = ? AND activo = 1";
    private final String SQL_UPDATE = "UPDATE empleados SET nombre=?, apellidos=?, telefono=?, email=?, direccion=?, cargo=?, fecha_alta=?, fecha_baja=?, salario_base=? WHERE dni=?";
    private final String SQL_DELETE = "UPDATE empleados SET activo = 0 WHERE dni = ?"; // Borrado lógico, no físico

     /**
     * Inserta un nuevo empleado en la base de datos.
     */
    public boolean insertar(EmpleadoVO empleado) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = Conexion.getInstancia().getConnection();
            stmt = conn.prepareStatement(SQL_INSERT);
            // usuario_id: si es mayor que 0 mandamos el ID; si no, mandamos null a MySQL
            if (empleado.getUsuario_id() > 0) {
                stmt.setInt(1, empleado.getUsuario_id());
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER);
            }
            stmt.setString(2, empleado.getDni());
            stmt.setString(3, empleado.getNombre());
            stmt.setString(4, empleado.getApellidos());
            stmt.setString(5, empleado.getTelefono());
            stmt.setString(6, empleado.getEmail());
            stmt.setString(7, empleado.getDireccion());
            stmt.setString(8, empleado.getCargo());
            // Fecha alta obligatorio para MySQL
            if (empleado.getFecha_alta() != null) {
                stmt.setDate(9, java.sql.Date.valueOf(empleado.getFecha_alta()));
            } else {
                stmt.setDate(9, java.sql.Date.valueOf(java.time.LocalDate.now()));
            }
            if (empleado.getFecha_baja() != null) {
                stmt.setDate(10, java.sql.Date.valueOf(empleado.getFecha_baja()));
            } else {
                stmt.setNull(10, java.sql.Types.DATE);
            }
            stmt.setFloat(11, empleado.getSalario_base());
            stmt.setBoolean(12, empleado.isActivo());
            
            
            int filas = stmt.executeUpdate();
            return filas > 0;
            
            
        } catch (Exception e) {
            System.err.println("Error al insertar empleado: " + e.getMessage());
            return false;
        } finally {
            close(stmt);
        }
    }
    
    
    /**
     * Obtiene todos los empledos activos.
     */
    public List<EmpleadoVO> listar() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<EmpleadoVO> lista = new ArrayList<>();
        
        try {
            conn = Conexion.getInstancia().getConnection();
            stmt = conn.prepareStatement(SQL_SELECT_ALL);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                lista.add(mapearEmpleado(rs));
        }
        } catch (Exception e) {
            System.err.println("Error al listar empleados: " + e.getMessage());
        } finally {
             close(stmt, rs);
        }
        return lista;
    }
    
    
    /**
     * Busca un empleado por su DNI.
     */
    public EmpleadoVO buscarPorDni(String dni) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        EmpleadoVO empleado = null;
        
        try {
            conn = Conexion.getInstancia().getConnection();
            stmt = conn.prepareStatement(SQL_SELECT_BY_DNI);
            stmt.setString(1, dni);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                empleado = mapearEmpleado(rs);
            }
        } catch (Exception e) {
            System.err.println("Error al buscar empleado: " + e.getMessage());
        } finally {
            close(stmt, rs);
        }
        return empleado;
    }
    
    
    /**
     * Actualiza los datos de un empleado.
     */
    public boolean modificar(EmpleadoVO empleado) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = Conexion.getInstancia().getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE);
            //      salario_base=
            stmt.setString(1, empleado.getNombre());
            stmt.setString(2, empleado.getApellidos());
            stmt.setString(3, empleado.getTelefono());
            stmt.setString(4, empleado.getEmail());
            stmt.setString(5, empleado.getDireccion());
            stmt.setString(6, empleado.getCargo());
            stmt.setDate(7, Date.valueOf(empleado.getFecha_alta()));
            stmt.setDate(8, Date.valueOf(empleado.getFecha_baja()));
            stmt.setFloat(9, empleado.getSalario_base());
                        
            return stmt.executeUpdate() >= 0;
        } catch (Exception e) {
            System.err.println("Error al modificar empleado: " + e.getMessage());
            return false;
        } finally {
            close(stmt);
        }
    }
    
    
    
    /**
     * Realiza un borrado lógico (desactiva el empleado).
     * @param dni
     * @return 
     */
    public boolean eliminar(String dni) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = Conexion.getInstancia().getConnection();
            stmt = conn.prepareStatement(SQL_DELETE);
            stmt.setString(1, dni);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar empleado: " + e.getMessage());
            return false;
        } finally {
            close(stmt);
        }
    }
    
    
    // Método auxiliar para convertir ResultSet a Objeto
    private EmpleadoVO mapearEmpleado(ResultSet rs) throws SQLException {
        EmpleadoVO e = new EmpleadoVO();
        e.setUsuario_id(rs.getInt("usuario_id"));
        e.setDni(rs.getString("dni"));
        e.setNombre(rs.getString("nombre"));
        e.setApellidos(rs.getString("apellidos"));
        e.setTelefono(rs.getString("telefono"));
        e.setEmail(rs.getString("email"));
        e.setDireccion(rs.getString("direccion"));
        e.setCargo(rs.getString("cargo"));
        Date fechaAltaSql = rs.getDate("fecha_alta");
        if(fechaAltaSql != null) e.setFecha_alta(fechaAltaSql.toLocalDate());
        Date fechaBajaSql = rs.getDate("fecha_baja");
        if(fechaBajaSql != null) e.setFecha_baja(fechaBajaSql.toLocalDate());
        e.setSalario_base(rs.getFloat("salario_base"));
        e.setActivo(rs.getBoolean("activo"));
        
        return e;
     }
    
    // Métodos auxiliares para cerrar recursos
    private void close(PreparedStatement stmt) { 
        close(stmt, null); 
    }
    private void close(PreparedStatement stmt, ResultSet rs) {
        try { if(rs != null) rs.close(); } catch(SQLException e){}
        try { if(stmt != null) stmt.close(); } catch(SQLException e){}
    }

    

}

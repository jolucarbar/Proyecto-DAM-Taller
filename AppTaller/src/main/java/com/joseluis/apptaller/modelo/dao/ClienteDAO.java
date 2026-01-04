package com.joseluis.apptaller.modelo.dao;

import com.joseluis.apptaller.modelo.vo.ClienteVO;
import com.joseluis.apptaller.persistencia.Conexion;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    // Consultas SQL
    private final String SQL_INSERT = "INSERT INTO clientes (dni_cif, nombre, telefono, email, direccion, fecha_registro, activo) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private final String SQL_SELECT_ALL = "SELECT * FROM clientes WHERE activo = 1";
    private final String SQL_SELECT_BY_DNI = "SELECT * FROM clientes WHERE dni_cif = ? AND activo = 1";
    private final String SQL_UPDATE = "UPDATE clientes SET nombre=?, telefono=?, email=?, direccion=? WHERE dni_cif=?";
    private final String SQL_DELETE = "UPDATE clientes SET activo = 0 WHERE dni_cif = ?"; // Borrado lógico, no físico

    /**
     * Inserta un nuevo cliente en la base de datos.
     */
    public boolean insertar(ClienteVO cliente) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = Conexion.getInstancia().getConnection();
            stmt = conn.prepareStatement(SQL_INSERT);
            
            stmt.setString(1, cliente.getDni());
            stmt.setString(2, cliente.getNombre());
            stmt.setString(3, cliente.getTelefono());
            stmt.setString(4, cliente.getEmail());
            stmt.setString(5, cliente.getDireccion());
            // Convertir LocalDate a java.sql.Date
            stmt.setDate(6, Date.valueOf(cliente.getFechaRegistro()));
            stmt.setBoolean(7, cliente.isActivo());

            int filas = stmt.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar cliente: " + e.getMessage());
            return false;
        } finally {
            close(stmt);
        }
    }

    /**
     * Obtiene todos los clientes activos.
     */
    public List<ClienteVO> listar() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<ClienteVO> lista = new ArrayList<>();

        try {
            conn = Conexion.getInstancia().getConnection();
            stmt = conn.prepareStatement(SQL_SELECT_ALL);
            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearCliente(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar clientes: " + e.getMessage());
        } finally {
            close(stmt, rs);
        }
        return lista;
    }
    
    /**
     * Busca un cliente por su DNI.
     */
    public ClienteVO buscarPorDni(String dni) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ClienteVO cliente = null;

        try {
            conn = Conexion.getInstancia().getConnection();
            stmt = conn.prepareStatement(SQL_SELECT_BY_DNI);
            stmt.setString(1, dni);
            rs = stmt.executeQuery();

            if (rs.next()) {
                cliente = mapearCliente(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar cliente: " + e.getMessage());
        } finally {
            close(stmt, rs);
        }
        return cliente;
    }
    
    /**
     * Actualiza los datos de un cliente.
     */
    public boolean modificar(ClienteVO cliente) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = Conexion.getInstancia().getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE);
            
            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getTelefono());
            stmt.setString(3, cliente.getEmail());
            stmt.setString(4, cliente.getDireccion());
            stmt.setString(5, cliente.getDni()); // El DNI es la clave para buscar cuál modificar

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar cliente: " + e.getMessage());
            return false;
        } finally {
            close(stmt);
        }
    }
    
    /**
     * Realiza un borrado lógico (desactiva el cliente).
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
            System.err.println("Error al eliminar cliente: " + e.getMessage());
            return false;
        } finally {
            close(stmt);
        }
    }

    // Método auxiliar para convertir ResultSet a Objeto
    private ClienteVO mapearCliente(ResultSet rs) throws SQLException {
        ClienteVO c = new ClienteVO();
        c.setDni(rs.getString("dni_cif"));
        c.setNombre(rs.getString("nombre"));
        c.setTelefono(rs.getString("telefono"));
        c.setEmail(rs.getString("email"));
        c.setDireccion(rs.getString("direccion"));
        c.setActivo(rs.getBoolean("activo"));
        
        Date fechaSql = rs.getDate("fecha_registro");
        if(fechaSql != null) c.setFechaRegistro(fechaSql.toLocalDate());
        
        return c;
    }

    // Métodos auxiliares para cerrar recursos
    private void close(PreparedStatement stmt) { close(stmt, null); }
    private void close(PreparedStatement stmt, ResultSet rs) {
        try { if(rs != null) rs.close(); } catch(SQLException e){}
        try { if(stmt != null) stmt.close(); } catch(SQLException e){}
    }
}

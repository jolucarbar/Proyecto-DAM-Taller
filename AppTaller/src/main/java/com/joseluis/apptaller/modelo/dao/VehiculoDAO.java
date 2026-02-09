
package com.joseluis.apptaller.modelo.dao;

import com.joseluis.apptaller.modelo.vo.VehiculoVO;
import com.joseluis.apptaller.persistencia.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Operaciones CRUD para la tabla vehiculos.
 */

/**
 *
 * @author joseluis
 */
public class VehiculoDAO {
    
    private final String SQL_INSERT = "INSERT INTO vehiculos (matricula, marca, modelo, anio, color, num_chasis, dni_cliente, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private final String SQL_SELECT_ALL = "SELECT * FROM vehiculos WHERE activo = 1";
    private final String SQL_SELECT_BY_MATRICULA = "SELECT * FROM vehiculos WHERE matricula = ? AND activo = 1";
    private final String SQL_SELECT_BY_CLIENTE = "SELECT * FROM vehiculos WHERE dni_cliente = ? AND activo = 1";
    private final String SQL_UPDATE = "UPDATE vehiculos SET marca=?, modelo=?, anio=?, color=?, num_chasis=? WHERE matricula=?";
    private final String SQL_DELETE = "UPDATE vehiculos SET activo = 0 WHERE matricula = ?";

    public boolean insertar(VehiculoVO v) {
        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {
            stmt.setString(1, v.getMatricula());
            stmt.setString(2, v.getMarca());
            stmt.setString(3, v.getModelo());
            stmt.setInt(4, v.getAnio());
            stmt.setString(5, v.getColor());
            stmt.setString(6, v.getNumeroChasis());
            stmt.setString(7, v.getDniCliente());
            stmt.setBoolean(8, v.isActivo());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar vehículo: " + e.getMessage());
            return false;
        }
    }

    public List<VehiculoVO> listar() {
        List<VehiculoVO> lista = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearVehiculo(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar vehículos: " + e.getMessage());
        }
        return lista;
    }

    public List<VehiculoVO> listarPorCliente(String dni) {
        List<VehiculoVO> lista = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_CLIENTE)) {
            stmt.setString(1, dni);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearVehiculo(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar vehículos por cliente: " + e.getMessage());
        }
        return lista;
    }

    public boolean eliminar(String matricula) {
        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
            stmt.setString(1, matricula);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar vehículo: " + e.getMessage());
            return false;
        }
    }

    private VehiculoVO mapearVehiculo(ResultSet rs) throws SQLException {
        VehiculoVO v = new VehiculoVO();
        v.setMatricula(rs.getString("matricula"));
        v.setMarca(rs.getString("marca"));
        v.setModelo(rs.getString("modelo"));
        v.setAnio(rs.getInt("anio"));
        v.setColor(rs.getString("color"));
        v.setNumeroChasis(rs.getString("num_chasis"));
        v.setDniCliente(rs.getString("dni_cliente"));
        v.setActivo(rs.getBoolean("activo"));
        return v;
    }
    
}

package com.joseluis.apptaller.modelo.dao;

import com.joseluis.apptaller.modelo.vo.VehiculoVO;
import com.joseluis.apptaller.persistencia.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehiculoDAO {

    // Consultas adaptadas a tu estructura DESCRIBE vehiculos
    private final String SQL_INSERT = "INSERT INTO vehiculos (bastidor, matricula, marca, modelo, anio_fabricacion, color, propietario_actual_dni, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private final String SQL_SELECT_ALL = "SELECT * FROM vehiculos WHERE activo = 1";
    // Aunque bastidor es PK, mantenemos búsqueda por matrícula que es lo habitual en talleres
    private final String SQL_SELECT_BY_MATRICULA = "SELECT * FROM vehiculos WHERE matricula = ? AND activo = 1";
    private final String SQL_SELECT_BY_CLIENTE = "SELECT * FROM vehiculos WHERE propietario_actual_dni = ? AND activo = 1";
    
    private final String SQL_UPDATE = "UPDATE vehiculos SET marca=?, modelo=?, anio_fabricacion=?, color=?, propietario_actual_dni=? WHERE matricula=?"; // Actualizamos buscando por matrícula
    private final String SQL_DELETE = "UPDATE vehiculos SET activo = 0 WHERE matricula = ?";

    public boolean insertar(VehiculoVO v) {
        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {
            
            stmt.setString(1, v.getBastidor());        // <--- AHORA SÍ COINCIDE
            stmt.setString(2, v.getMatricula());
            stmt.setString(3, v.getMarca());
            stmt.setString(4, v.getModelo());
            stmt.setInt(5, v.getAnioFabricacion());    // <--- AHORA SÍ COINCIDE
            stmt.setString(6, v.getColor());
            stmt.setString(7, v.getDniPropietario());  // <--- AHORA SÍ COINCIDE
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
        
        // --- AQUÍ ESTABA EL ERROR, AHORA ESTÁ CORREGIDO ---
        v.setBastidor(rs.getString("bastidor"));              // Columna BD -> Método VO
        v.setMatricula(rs.getString("matricula"));
        v.setMarca(rs.getString("marca"));
        v.setModelo(rs.getString("modelo"));
        v.setAnioFabricacion(rs.getInt("anio_fabricacion"));  // Columna BD -> Método VO
        v.setColor(rs.getString("color"));
        v.setDniPropietario(rs.getString("propietario_actual_dni")); // Columna BD -> Método VO
        
        // Verificamos si la columna activo existe (si tu tabla no la tiene, dará false por defecto)
        try {
            v.setActivo(rs.getBoolean("activo"));
        } catch (SQLException ex) {
            v.setActivo(true); // Si no existe la columna, asumimos activo
        }
        
        return v;
    }
}
package com.joseluis.apptaller.modelo.dao;

import com.joseluis.apptaller.modelo.vo.VehiculoVO;
import com.joseluis.apptaller.persistencia.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehiculoDAO {

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
            
            stmt.setString(1, v.getBastidor());        
            stmt.setString(2, v.getMatricula());
            stmt.setString(3, v.getMarca());
            stmt.setString(4, v.getModelo());
            stmt.setInt(5, v.getAnioFabricacion());    
            stmt.setString(6, v.getColor());
            stmt.setString(7, v.getDniPropietario());  
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
        
       
        v.setBastidor(rs.getString("bastidor"));              // Columna BD -> Método VO
        v.setMatricula(rs.getString("matricula"));
        v.setMarca(rs.getString("marca"));
        v.setModelo(rs.getString("modelo"));
        v.setAnioFabricacion(rs.getInt("anio_fabricacion"));  // Columna BD -> Método VO
        v.setColor(rs.getString("color"));
        v.setDniPropietario(rs.getString("propietario_actual_dni")); // Columna BD -> Método VO
        
        // Verificamos si la columna activo existe 
        try {
            v.setActivo(rs.getBoolean("activo"));
        } catch (SQLException ex) {
            v.setActivo(true); // Si no existe la columna, asumimos activo
        }
        
        return v;
    }

    public VehiculoVO buscarPorBastidor(String bastidor) {
        VehiculoVO vehiculo = null;
        // Ajusta el nombre de la tabla si en tu BD se llama distinto (ej: Vehiculos)
        String sql = "SELECT * FROM vehiculos WHERE bastidor = ?";
       
        try (java.sql.Connection conn = com.joseluis.apptaller.persistencia.Conexion.getInstancia().getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
           
            stmt.setString(1, bastidor);
           
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    vehiculo = new VehiculoVO();
                    vehiculo.setBastidor(rs.getString("bastidor"));
                    vehiculo.setMatricula(rs.getString("matricula"));
                    vehiculo.setMarca(rs.getString("marca"));
                    vehiculo.setModelo(rs.getString("modelo"));
                    vehiculo.setColor(rs.getString("color"));
                    vehiculo.setAnioFabricacion(rs.getInt("anio_fabricacion"));
                    vehiculo.setDniPropietario(rs.getString("propietario_actual_dni"));
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error al buscar vehículo por bastidor: " + e.getMessage());
        }
       
        return vehiculo;
    }

    public boolean modificar(VehiculoVO vehiculo) {
        // En el UPDATE actualizamos todo MENOS el bastidor, que se usa en el WHERE
        String sql = "UPDATE vehiculos SET matricula = ?, marca = ?, modelo = ?, color = ?, anio_fabricacion = ?, propietario_actual_dni = ? WHERE bastidor = ?";
       
        try (java.sql.Connection conn = com.joseluis.apptaller.persistencia.Conexion.getInstancia().getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
           
            stmt.setString(1, vehiculo.getMatricula());
            stmt.setString(2, vehiculo.getMarca());
            stmt.setString(3, vehiculo.getModelo());
            stmt.setString(4, vehiculo.getColor());
            stmt.setInt(5, vehiculo.getAnioFabricacion());
            stmt.setString(6, vehiculo.getDniPropietario());
           
            // El parámetro 7 es el bastidor para la cláusula WHERE
            stmt.setString(7, vehiculo.getBastidor());
           
            
            
            int filasAfectadas = stmt.executeUpdate();
            
            boolean exito = filasAfectadas >= 0;  
           
            return exito;
            
            //return filasAfectadas >= 0; // Si es mayor que 0, se actualizó con éxito
           
        } catch (java.sql.SQLException e) {
            System.err.println("Error al modificar vehículo: " + e.getMessage());
            return false;
        }
    }
}
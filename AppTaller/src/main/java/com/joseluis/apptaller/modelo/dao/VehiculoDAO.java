package com.joseluis.apptaller.modelo.dao;

import com.joseluis.apptaller.modelo.vo.VehiculoVO;
import com.joseluis.apptaller.persistencia.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Gestiona la persistencia de los datos de los vehículos en la base de datos.
 * Permite realizar operaciones CRUD, incluyendo el registro de nuevos coches, 
 * la edición de sus datos y la consulta de vehículos asociados a un cliente específico.
 * 
 * @author José Luis Cárdenas Barroso
 * @info Proyecto Intermodular del Grado Superior DAM
 * @institution IES Augustóbriga
 */
public class VehiculoDAO {

    private final String SQL_INSERT = "INSERT INTO vehiculos (bastidor, matricula, marca, modelo, anio_fabricacion, color, propietario_actual_dni, activo) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private final String SQL_SELECT_ALL = "SELECT v.*, c.nombre AS nombre_propietario "
            + "FROM vehiculos v LEFT JOIN clientes c ON v.propietario_actual_dni = c.dni_cif "
            + "WHERE v.activo= 1 ORDER BY nombre_propietario ASC";
    // Aunque bastidor es PK, mantenemos búsqueda por matrícula que es lo habitual en talleres
    private final String SQL_SELECT_POR_MATRICULA = "SELECT v.*, c.nombre as nombre_propietario "
            + "FROM vehiculos v LEFT JOIN clientes c ON v.propietario_actual_dni = c.dni_cif "
            + "WHERE v.matricula LIKE ? OR v.bastidor LIKE ? AND v.activo = 1";
    private final String SQL_SELECT_BY_CLIENTE = "SELECT v.*, c.nombre AS nombre_propietario "
        + "FROM vehiculos v LEFT JOIN clientes c ON v.propietario_actual_dni = c.dni_cif "
        + "WHERE v.propietario_actual_dni = ? AND v.activo = 1";
    
    private final String SQL_UPDATE = "UPDATE vehiculos SET marca=?, modelo=?, anio_fabricacion=?, color=?, propietario_actual_dni=? WHERE matricula=?"; // Actualizamos buscando por matrícula
    private final String SQL_DELETE = "UPDATE vehiculos SET activo = 0 WHERE matricula = ?";

    // Selecciona por bastidor (por si es necesario en un futuro)
    private final String SQL_SELECT_BASTIDOR = "SELECT * FROM vehiculos WHERE bastidor = ?";
    
    // Actualiza por bastidor
    private final String SQL_UPDATE_BASTIDOR = "UPDATE vehiculos SET matricula = ?, marca = ?, modelo = ?, color = ?, anio_fabricacion = ?, propietario_actual_dni = ? WHERE bastidor = ?";

    
    
    
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
               
        v.setBastidor(rs.getString("bastidor"));  // Columna BD -> Método VO
        v.setMatricula(rs.getString("matricula"));
        v.setMarca(rs.getString("marca"));
        v.setModelo(rs.getString("modelo"));
        v.setAnioFabricacion(rs.getInt("anio_fabricacion"));  // Columna BD -> Método VO
        v.setColor(rs.getString("color"));
        v.setDniPropietario(rs.getString("propietario_actual_dni")); // Columna BD -> Método VO
        v.setNombrePropietario(rs.getString("nombre_propietario"));
        
        // Verificamos si la columna activo existe 
        try {
            v.setActivo(rs.getBoolean("activo"));
        } catch (SQLException ex) {
            v.setActivo(true); // Si no existe la columna, asumimos activo
        }
        
        return v;
    }

    
    /**
     * Método que busca un vehículo por su bastidor.
     * También se emplea para buscar por matrícula.
     * @param bastidor
     * @return 
     */
    public VehiculoVO buscarPorBastidor(String bastidor) {
        VehiculoVO vehiculo = null;
        
        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BASTIDOR)) {
           
            stmt.setString(1, bastidor);
           
            try (ResultSet rs = stmt.executeQuery()) {
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
        } catch (SQLException e) {
            System.err.println("Error al buscar vehículo por bastidor: " + e.getMessage());
        }
       
        return vehiculo;
    }
    
    

    
    public boolean modificar(VehiculoVO vehiculo) {
        
        try (Connection conn = Conexion.getInstancia().getConnection();
            PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_BASTIDOR)) {
           
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
           
        } catch (SQLException e) {
            System.err.println("Error al modificar vehículo: " + e.getMessage());
            return false;
        }
    }

    public List<VehiculoVO> buscarPorMatricula(String busqueda) {
       List<VehiculoVO> vehiculo = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_POR_MATRICULA)) {
           
            String busquedaFormateada = "%" + busqueda + "%";
            stmt.setString(1, busquedaFormateada);
            stmt.setString(2, busquedaFormateada);
           try (ResultSet rs = stmt.executeQuery()) {
               while (rs.next()) {
                   vehiculo.add(mapearVehiculo(rs));
               }
           }
           
        } catch (SQLException e) {
            System.err.println("Error al buscar por matrícula: " + e.getMessage());
        }
       return vehiculo;     
    }
    
    
}
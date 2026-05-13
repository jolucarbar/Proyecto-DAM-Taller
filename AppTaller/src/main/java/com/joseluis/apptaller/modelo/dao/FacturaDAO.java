
package com.joseluis.apptaller.modelo.dao;

import com.joseluis.apptaller.modelo.vo.FacturaVO;
import com.joseluis.apptaller.persistencia.Conexion;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO para gestionar la persistencia de las Facturas en la base de datos.
 * (implementa las operaciones para gestionar la creación de facturas, asignarlas a un 
 * cliente y calcular totales).
 * 
 * @author José Luis Cárdenas Barroso
 * @info Proyecto Intermodular del Grado Superior DAM
 * @institution IES Augustóbriga
 */
public class FacturaDAO {

    private final String SQL_INSERT = "INSERT INTO facturas (numero_factura, id_reparacion, id_presupuesto, "
            + "cliente_dni, vehiculo_bastidor, fecha_emision, fecha_vencimiento, base_imponible, iva, "
            + "total_cobrado, metodo_pago, estado, observaciones, usuario_emisor) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
           
    private final String SQL_SELECT_ALL = "SELECT f.*, c.nombre AS cliente_nombre, v.matricula " +
            "FROM facturas f " +
            "LEFT JOIN clientes c ON f.cliente_dni = c.dni_cif " +
            "LEFT JOIN vehiculos v ON f.vehiculo_bastidor = v.bastidor " +
            "ORDER BY f.fecha_emision DESC";
           
    private final String SQL_SELECT_POR_NUMERO = "SELECT f.*, c.nombre AS cliente_nombre, v.matricula "
            + "FROM facturas f "
            + "LEFT JOIN clientes c ON f.cliente_dni = c.dni_cif "
            + "LEFT JOIN vehiculos v ON f.vehiculo_bastidor = v.bastidor "
            + "WHERE f.numero_factura = ?";

    private final String SQL_UPDATE_ESTADO = "UPDATE facturas SET estado = ? WHERE id_factura = ?";

    private final String SQL_SELECT_POR_CLIENTE = "SELECT f.*, c.nombre AS cliente_nombre, v.matricula "
            + "FROM facturas f "
            + "LEFT JOIN clientes c ON f.cliente_dni = c.dni_cif "
            + "LEFT JOIN vehiculos v ON f.vehiculo_bastidor = v.bastidor "
            + "WHERE f.cliente_dni = ? ORDER BY f.fecha_emision DESC";

    private final String SQL_SELECT_POR_ID = "SELECT f.*, c.nombre AS cliente_nombre, v.matricula " +
            "FROM facturas f " +
            "LEFT JOIN clientes c ON f.cliente_dni = c.dni_cif " +
            "LEFT JOIN vehiculos v ON f.vehiculo_bastidor = v.bastidor " +
            "WHERE f.id_factura = ?";

    private final String SQL_BUSCAR_FACTURA = "SELECT f.*, c.nombre AS cliente_nombre, v.matricula " +
            "FROM facturas f "
            + "LEFT JOIN clientes c ON f.cliente_dni = c.dni_cif "
            + "LEFT JOIN vehiculos v ON f.vehiculo_bastidor = v.bastidor "
            + "WHERE f.id_factura LIKE ? OR f.fecha_emision LIKE ? OR c.nombre LIKE ? OR v.matricula LIKE ?";
    
    
    
    /**
     * Registra una nueva factura en el sistema.
     * @param factura Objeto de tipo Factura (debes tener el VO correspondiente)
     * @return true si la operación tuvo éxito
     */
    public boolean insertar(FacturaVO factura) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = Conexion.getInstancia().getConnection();  
            stmt = conn.prepareStatement(SQL_INSERT);

            stmt.setString(1, factura.getNumeroFactura());
            stmt.setInt(2, factura.getIdReparacion());
            
            // Campos que pueden ser nulos 
            if (factura.getIdPresupuesto() > 0) {
                stmt.setInt(3, factura.getIdPresupuesto());
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            
            stmt.setString(4, factura.getClienteDni());
            stmt.setString(5, factura.getVehiculoBastidor());
            stmt.setDate(6, Date.valueOf(factura.getFechaEmision())); // Conversión de LocalDate
            stmt.setDate(7, Date.valueOf(factura.getFechaVencimiento()));
            stmt.setDouble(8, factura.getBaseImponible());
            stmt.setDouble(9, factura.getIva());
            stmt.setDouble(10, factura.getTotalCobrado());
            stmt.setString(11, factura.getMetodoPago());
            stmt.setString(12, factura.getEstado());
            stmt.setString(13, factura.getObservaciones());
            stmt.setInt(14, factura.getUsuarioEmisor());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar factura: " + e.getMessage());
            return false;
        } finally {
            close(stmt);
        }
    }

    
    /**
     * Genera un listado de todas las facturas registradas.
     */
    public List<FacturaVO> listar() {
       List<FacturaVO> lista = new ArrayList<>();

        try (Connection conn = Conexion.getInstancia().getConnection();
            PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_ALL)) {
            
            try(ResultSet rs = stmt.executeQuery();) {
                while (rs.next()) {
                    lista.add(mapearFactura(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar facturas: " + e.getMessage());
        } 
        return lista;
    }

    
    /**
     * Mapea una fila de ResultSet a un objeto FacturaVO.
     */
    private FacturaVO mapearFactura(ResultSet rs) throws SQLException {
        FacturaVO f = new FacturaVO();
        f.setIdFactura(rs.getInt("id_factura"));
        f.setNumeroFactura(rs.getString("numero_factura"));
        f.setIdReparacion(rs.getInt("id_reparacion"));
        f.setIdPresupuesto(rs.getInt("id_presupuesto"));
        f.setClienteDni(rs.getString("cliente_dni"));
        f.setVehiculoBastidor(rs.getString("vehiculo_bastidor"));
        
        if (rs.getDate("fecha_emision") != null) {
            f.setFechaEmision(rs.getDate("fecha_emision").toLocalDate());
        }
        
        f.setTotalCobrado(rs.getDouble("total_cobrado"));
        f.setBaseImponible(rs.getDouble("base_imponible"));
        f.setIva(rs.getDouble("iva"));
        f.setEstado(rs.getString("estado"));
        
        // Intentamos leer 'cliente_nombre'.
        try {
            f.setClienteNombre(rs.getString("cliente_nombre"));
        } catch (SQLException e) {
            f.setClienteNombre("Desconocido");
        }
        
        // Intentamos leer la matrícula
        try {
            f.setVehiculoMatricula(rs.getString("matricula"));
        } catch(SQLException e) {
            f.setVehiculoMatricula("Desconocido");
        }      
        return f;
    }

    // Métodos auxiliares para el cierre de recursos
    private void close(PreparedStatement stmt) { close(stmt, null); }
    private void close(PreparedStatement stmt, ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
    }
    
    
    /**
     * Genera el siguiente número de factura secuencial para el año en curso.
     * Formato: FACT-YYYY-NNNN (ej. FACT-2026-0001)
     */
    public String generarSiguienteNumeroFactura() {
        String anioActual = String.valueOf(java.time.Year.now().getValue());
        String patron = "FACT-" + anioActual + "-%";
        String sql = "SELECT MAX(numero_factura) AS ultimo_numero FROM facturas WHERE numero_factura LIKE ?";
        
        @SuppressWarnings("UnusedAssignment")
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = Conexion.getInstancia().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, patron);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                String ultimoNumero = rs.getString("ultimo_numero");
                if (ultimoNumero != null) {
                    // Extraer la parte numérica (los últimos 4 caracteres)
                    String secuenciaStr = ultimoNumero.substring(ultimoNumero.lastIndexOf("-") + 1);
                    int secuencia = Integer.parseInt(secuenciaStr);
                    secuencia++;
                    return String.format("FACT-%s-%04d", anioActual, secuencia);
                }
            }
            
            // Si no hay facturas este año, devolvemos la primera
            return "FACT-" + anioActual + "-0001";
            
        } catch (SQLException e) {
            System.err.println("Error al generar secuencia de factura: " + e.getMessage());
            return "ERROR-SECUENCIA";
        } finally {
            close(stmt, rs);
        }
    }
    
    
    /**
    * Recupera una factura completa por su ID, incluyendo el nombre del cliente.
    */
   public FacturaVO obtenerPorId(int idFactura) {
       
       try (Connection conn = Conexion.getInstancia().getConnection();
            PreparedStatement ps = conn.prepareStatement(SQL_SELECT_POR_ID)) {

           ps.setInt(1, idFactura);
           try (ResultSet rs = ps.executeQuery()) {
               if (rs.next()) {
                   return mapearFactura(rs); // Reutilizamos el mapeador existente
               }
           }
       } catch (SQLException e) {
           System.err.println("Error al obtener factura por ID: " + e.getMessage());
       }
       return null;
   }
    
   
   /**
     * Actualiza el estado de una factura en la base de datos.
     */
    public boolean actualizarEstado(int idFactura, String nuevoEstado) {
        
        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_ESTADO)) {
            
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idFactura);
            
            return ps.executeUpdate() > 0;
        } catch (java.sql.SQLException e) {
            System.err.println("Error al actualizar el estado de la factura: " + e.getMessage());
            return false;
        }
    }
   
    
    
    public List<FacturaVO> listarPorCliente(String dniCliente) {
        List<FacturaVO> lista = new ArrayList<>();
        
        try (Connection conn = Conexion.getInstancia().getConnection();
            PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_POR_CLIENTE)) {
            stmt.setString(1, dniCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearFactura(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar facturas por cliente: " + e.getMessage());
        }
        return lista;
    }

    
    /**
    * Recupera una factura completa por su número de factura, incluyendo el nombre del cliente.
    */
    public FacturaVO obtenerPorNumero(String numeroFactura) {
        FacturaVO factura = null;
        
        try (Connection conn = Conexion.getInstancia().getConnection();
            PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_POR_NUMERO)) {
            
            stmt.setString(1, numeroFactura);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                   return mapearFactura(rs);                    
                }
            }
            
        } catch (SQLException e) {
             System.err.println("Error al obtener factura por su número: " + e.getMessage());
        }
        return null;
    }

    public List<FacturaVO> buscarFactura(String busqueda) {
        List<FacturaVO> lista = new ArrayList<>();
        try(Connection conn = Conexion.getInstancia().getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_BUSCAR_FACTURA)) {
            
            String busquedaFormateada = "%" + busqueda + "%";
            stmt.setString(1, busquedaFormateada); // para el nº de factura
            stmt.setString(2, busquedaFormateada); // para la fecha
            stmt.setString(3, busquedaFormateada); // para el nombre
            stmt.setString(4, busquedaFormateada); // para la matrícula del vehículo
            
            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearFactura(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar factura: " + e.getMessage());
        }
        return lista;
    }
   
}

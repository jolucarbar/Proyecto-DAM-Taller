
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
 * Sigue el patrón Singleton para la conexión y las reglas de negocio del ERP.
 * 
 * * @author José Luis Cárdenas Barroso
 */
public class FacturaDAO {

    // Consultas SQL basadas en la estructura de la tabla Facturas
    private final String SQL_INSERT = "INSERT INTO facturas (numero_factura, id_reparacion, id_presupuesto, "
            + "cliente_dni, vehiculo_bastidor, fecha_emision, fecha_vencimiento, base_imponible, iva, "
            + "total_cobrado, metodo_pago, estado, observaciones, usuario_emisor) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private final String SQL_SELECT_ALL = "SELECT f.*, c.nombre AS cliente_nombre " +
        "FROM facturas f " +
        "LEFT JOIN clientes c ON f.cliente_dni = c.dni_cif " +
        "ORDER BY f.fecha_emision DESC";
    
    private final String SQL_SELECT_BY_NUMERO = "SELECT * FROM facturas WHERE numero_factura = ?";

    /**
     * Registra una nueva factura en el sistema.
     * @param factura Objeto de tipo Factura (debes tener el VO correspondiente)
     * @return true si la operación tuvo éxito
     */
    public boolean insertar(FacturaVO factura) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            System.out.println(">>> DEBUG FACTURA - DNI: [" + factura.getClienteDni() + "] | Bastidor: [" + factura.getVehiculoBastidor() + "]");
            conn = Conexion.getInstancia().getConnection(); // Uso del Singleton 
            stmt = conn.prepareStatement(SQL_INSERT);

            stmt.setString(1, factura.getNumeroFactura());
            stmt.setInt(2, factura.getIdReparacion());
            
            // Campos que pueden ser nulos según el script SQL
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
     * Útil para alimentar el tblFacturas del panel principal
     */
    public List<FacturaVO> listar() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<FacturaVO> lista = new ArrayList<>();

        try {
            conn = Conexion.getInstancia().getConnection();
            stmt = conn.prepareStatement(SQL_SELECT_ALL);
            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearFactura(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar facturas: " + e.getMessage());
        } finally {
            close(stmt, rs);
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
        f.setEstado(rs.getString("estado"));
        
        // Intentamos leer 'cliente_nombre'.
        try {
            f.setClienteNombre(rs.getString("cliente_nombre"));
        } catch (SQLException e) {
            f.setClienteNombre("Desconocido");
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
       String sql = "SELECT f.*, c.nombre AS cliente_nombre " +
                    "FROM facturas f " +
                    "LEFT JOIN clientes c ON f.cliente_dni = c.dni_cif " +
                    "WHERE f.id_factura = ?";

       try (Connection conn = Conexion.getInstancia().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

           ps.setInt(1, idFactura);
           try (ResultSet rs = ps.executeQuery()) {
               if (rs.next()) {
                   return mapearFactura(rs); // Reutilizamos tu mapeador existente
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
        String sql = "UPDATE facturas SET estado = ? WHERE id_factura = ?";
        
        try (java.sql.Connection conn = com.joseluis.apptaller.persistencia.Conexion.getInstancia().getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idFactura);
            
            return ps.executeUpdate() > 0;
        } catch (java.sql.SQLException e) {
            System.err.println("Error al actualizar el estado de la factura: " + e.getMessage());
            return false;
        }
    }
   
   
}

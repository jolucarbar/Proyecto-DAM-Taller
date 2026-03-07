
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
    
    private final String SQL_SELECT_ALL = "SELECT * FROM Facturas ORDER BY fecha_emision DESC";
    
    private final String SQL_SELECT_BY_NUMERO = "SELECT * FROM Facturas WHERE numero_factura = ?";

    /**
     * Registra una nueva factura en el sistema.
     * @param factura Objeto de tipo Factura (debes tener el VO correspondiente)
     * @return true si la operación tuvo éxito
     */
    public boolean insertar(FacturaVO factura) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
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
        f.setFechaEmision(rs.getDate("fecha_emision").toLocalDate());
        f.setTotalCobrado(rs.getDouble("total_cobrado"));
        f.setEstado(rs.getString("estado"));
        return f;
    }

    // Métodos auxiliares para el cierre de recursos
    private void close(PreparedStatement stmt) { close(stmt, null); }
    private void close(PreparedStatement stmt, ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
    }
}

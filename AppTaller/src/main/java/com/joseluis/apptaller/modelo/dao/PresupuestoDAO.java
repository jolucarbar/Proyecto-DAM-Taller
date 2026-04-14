
package com.joseluis.apptaller.modelo.dao;

import com.joseluis.apptaller.modelo.vo.DetalleManoObraVO;
import com.joseluis.apptaller.modelo.vo.DetalleProductoVO;
import com.joseluis.apptaller.modelo.vo.PresupuestoVO;
import com.joseluis.apptaller.persistencia.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona toda la información de los presupuestos en la base de datos.
 * Guarda de forma segura el presupuesto junto con las piezas y la mano de obra, 
 * y permite listarlos, borrarlos o cambiar su estado (Pendiente, Aprobado, etc.).
 * 
 * @author José Luis Cárdenas Barroso
 * @info Proyecto Intermodular del Grado Superior DAM
 * @institution IES Augustóbriga
 */
public class PresupuestoDAO {

    // Consulta para la cabecera del presupuesto
    private final String SQL_INSERT_CABECERA = 
        "INSERT INTO presupuestos (vehiculo_bastidor, cliente_dni, fecha_emision, fecha_validez, estado, descripcion_trabajo, total_estimado) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    // Consulta para la mano de obra (no inserto 'subtotal' porque en MySQL es GENERATED ALWAYS)
    private final String SQL_INSERT_MANO_OBRA = 
        "INSERT INTO detalle_mano_obra (id_presupuesto, descripcion_trabajo, tiempo_empleado_horas, tarifa_por_hora) " +
        "VALUES (?, ?, ?, ?)";

    //  Consulta para los materiales (tampoco inserto 'subtotal' por la misma razón)
    private final String SQL_INSERT_PRODUCTO = 
        "INSERT INTO detalle_productos (id_presupuesto, id_producto, cantidad_usada, precio_venta_unitario, descuento) " +
        "VALUES (?, ?, ?, ?, ?)";
    
    // Consulta para obtener presupuestos para mostrarlos en el panel de presupuestos
    private final String SQL_LISTAR_PRESUPUESTOS = "SELECT p.id_presupuesto, p.fecha_emision, c.nombre, v.matricula, p.total_estimado, p.estado " +
                     "FROM presupuestos p " +
                     "LEFT JOIN clientes c ON p.cliente_dni = c.dni_cif " +
                     "LEFT JOIN vehiculos v ON p.vehiculo_bastidor = v.bastidor " +
                     "ORDER BY p.fecha_emision DESC";
    
    // Consulta para sacar el nombre de un producto de un presupuesto
    private final String SQL_SELECT_PRODUCTO = "SELECT dp.*, p.nombre AS nombre_producto FROM detalle_productos dp " +
                     "LEFT JOIN productos p ON dp.id_producto = p.id_producto " +
                     "WHERE dp.id_presupuesto = ?";
    
    // Consulta para obtener las líneas de mano de obra de un presupuesto específico
    private final String SQL_MANO_OBRA = "SELECT * FROM detalle_mano_obra WHERE id_presupuesto = ?";
    
    // Consulta para obtener la cabecera de un presupuesto
    private final String SQL_CABECERA_PRESUPUESTO = "SELECT * FROM presupuestos WHERE id_presupuesto = ?";
    
    // Actualiza el estado de un presupuesto
    private final String SQL_UPDATE_ESTADO = "UPDATE presupuestos SET estado = ? WHERE id_presupuesto = ?";
    
    

    /**
     * Guarda un presupuesto completo (Cabecera + Tareas + Materiales) usando una Transacción.
     * 
     * @param presupuesto Objeto completo con sus listas de detalles llenas.
     * @return true si se guardó todo correctamente, false si hubo rollback.
     */
    public boolean insertarPresupuestoCompleto(PresupuestoVO presupuesto) {
        Connection conn = null;
        PreparedStatement psCabecera = null;
        PreparedStatement psManoObra = null;
        PreparedStatement psProductos = null;
        ResultSet rsKeys = null;

        try {
            // Obtenemos la conexión 
            conn = Conexion.getInstancia().getConnection();
            
            // Desactivamos el auto-commit para iniciar la transacción
            conn.setAutoCommit(false);

            // Insertamos  en presupuestos
            // Usamos RETURN_GENERATED_KEYS para obtener el ID que MySQL le asigne al presupuesto
            psCabecera = conn.prepareStatement(SQL_INSERT_CABECERA, Statement.RETURN_GENERATED_KEYS);
            psCabecera.setString(1, presupuesto.getVehiculoBastidor());
            psCabecera.setString(2, presupuesto.getClienteDni());
            psCabecera.setDate(3, java.sql.Date.valueOf(presupuesto.getFechaEmision()));
            psCabecera.setDate(4, java.sql.Date.valueOf(presupuesto.getFechaValidez()));
            psCabecera.setString(5, presupuesto.getEstado());
            psCabecera.setString(6, presupuesto.getDescripcionTrabajo());
            psCabecera.setBigDecimal(7, presupuesto.getTotalEstimado());

            int filasAfectadas = psCabecera.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("Fallo al insertar la cabecera del presupuesto.");
            }

            // Recuperar el ID autogenerado
            int idPresupuestoGenerado;
            rsKeys = psCabecera.getGeneratedKeys();
            if (rsKeys.next()) {
                idPresupuestoGenerado = rsKeys.getInt(1);
            } else {
                throw new SQLException("Fallo al obtener el ID del presupuesto.");
            }

            // Insertamos las líneas de la mano de obra en detalle_mano_obra
            if (!presupuesto.getLineasManoObra().isEmpty()) {
                psManoObra = conn.prepareStatement(SQL_INSERT_MANO_OBRA);
                for (DetalleManoObraVO mo : presupuesto.getLineasManoObra()) {
                    psManoObra.setInt(1, idPresupuestoGenerado);
                    psManoObra.setString(2, mo.getDescripcionTrabajo());
                    psManoObra.setBigDecimal(3, mo.getTiempoEmpleadoHoras());
                    psManoObra.setBigDecimal(4, mo.getTarifaPorHora());
                    // Agregamos al lote (batch) para ejecutar todo de golpe y optimizar rendimiento
                    psManoObra.addBatch(); 
                }
                psManoObra.executeBatch();
            }

            // Insertamos las líneas de los productos en detalle_productos
            if (!presupuesto.getLineasProductos().isEmpty()) {
                psProductos = conn.prepareStatement(SQL_INSERT_PRODUCTO);
                for (DetalleProductoVO prod : presupuesto.getLineasProductos()) {
                    psProductos.setInt(1, idPresupuestoGenerado);
                    psProductos.setString(2, prod.getIdProducto());
                    psProductos.setInt(3, prod.getCantidadUsada());
                    psProductos.setBigDecimal(4, prod.getPrecioVentaUnitario());
                    psProductos.setBigDecimal(5, prod.getDescuento());
                    // Agregamos al lote
                    psProductos.addBatch();
                }
                psProductos.executeBatch();
            }

            
            // Si llegamos hasta aquí, nada falló. Le decimos a MySQL que aplique los cambios.
            conn.commit();
            return true;

        } catch (SQLException ex) {
            // Si algo falla, deshacemos todo (ROLLBACK)
            System.err.println("Error en la transacción de Presupuesto. Se realizará Rollback. Motivo: " + ex.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Fallo crítico al hacer rollback: " + rollbackEx.getMessage());
            }
            return false;

        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
                // Cerramos recursos
                if (rsKeys != null) rsKeys.close();
                if (psCabecera != null) psCabecera.close();
                if (psManoObra != null) psManoObra.close();
                if (psProductos != null) psProductos.close();
            } catch (SQLException closeEx) {
                System.err.println("Fallo al cerrar recursos: " + closeEx.getMessage());
            }
        }
    }
    
    
    /**
     * Obtiene la lista de presupuestos formateada para la tabla visual.
     */
    public List<Object[]> listarParaTabla() {
        List<Object[]> lista = new ArrayList<>();
                   
        try (Connection conn = com.joseluis.apptaller.persistencia.Conexion.getInstancia().getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_LISTAR_PRESUPUESTOS);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                lista.add(new Object[]{
                    "PRE-" + rs.getInt("id_presupuesto"),
                    rs.getDate("fecha_emision"),
                    rs.getString("nombre") != null ? rs.getString("nombre") : "Desconocido",
                    rs.getString("matricula") != null ? rs.getString("matricula") : "Desconocido",
                    rs.getBigDecimal("total_estimado") + " €",
                    rs.getString("estado")
                });
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error al cargar la tabla de presupuestos: " + e.getMessage());
        }
        return lista;
    }
    
    
    /**
     * Elimina un presupuesto y todas sus líneas de detalle de forma transaccional.
     * @param idPresupuesto El ID del presupuesto a eliminar.
     * @return true si se eliminó con éxito, false en caso de error.
     */
    public boolean eliminarPresupuesto(int idPresupuesto) {
        Connection conn = null;
        try {
            conn = Conexion.getInstancia().getConnection();
            conn.setAutoCommit(false); // Iniciamos transacción

            // Borrar mano de obra asociada
            try (PreparedStatement ps1 = conn.prepareStatement("DELETE FROM detalle_mano_obra WHERE id_presupuesto = ?")) {
                ps1.setInt(1, idPresupuesto);
                ps1.executeUpdate();
            }

            // Borrar materiales asociados
            try (PreparedStatement ps2 = conn.prepareStatement("DELETE FROM detalle_productos WHERE id_presupuesto = ?")) {
                ps2.setInt(1, idPresupuesto);
                ps2.executeUpdate();
            }

            // Borrar la cabecera del presupuesto
            try (PreparedStatement ps3 = conn.prepareStatement("DELETE FROM presupuestos WHERE id_presupuesto = ?")) {
                ps3.setInt(1, idPresupuesto);
                int filasAfectadas = ps3.executeUpdate();
                
                if (filasAfectadas == 0) {
                    throw new java.sql.SQLException("No se encontró el presupuesto principal.");
                }
            }

            conn.commit(); // Confirmamos los cambios
            return true;

        } catch (java.sql.SQLException e) {
            System.err.println("Error al eliminar presupuesto. Haciendo rollback: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (java.sql.SQLException ex) {
                System.err.println("Error en el rollback: " + ex.getMessage());
            }
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (java.sql.SQLException e) { }
        }
    }
    
    
    /**
     * Obtiene las líneas de mano de obra de un presupuesto específico.
     */
    public List<DetalleManoObraVO> obtenerManoObraPorPresupuesto(int idPresupuesto) {
        List<DetalleManoObraVO> lista = new java.util.ArrayList<>();
        
        
        try (Connection conn = Conexion.getInstancia().getConnection();
            PreparedStatement ps = conn.prepareStatement(SQL_MANO_OBRA)) {
            ps.setInt(1, idPresupuesto);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetalleManoObraVO mo = new DetalleManoObraVO();
                    mo.setIdDetalle(rs.getInt("id_detalle"));
                    mo.setIdPresupuesto(rs.getInt("id_presupuesto"));
                    mo.setDescripcionTrabajo(rs.getString("descripcion_trabajo"));
                    mo.setTiempoEmpleadoHoras(rs.getBigDecimal("tiempo_empleado_horas"));
                    mo.setTarifaPorHora(rs.getBigDecimal("tarifa_por_hora"));
                    mo.setSubtotal(rs.getBigDecimal("subtotal"));
                    lista.add(mo);
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error al cargar mano de obra: " + e.getMessage());
        }
        return lista;
    }
    
    
    /**
     * Obtiene las líneas de productos/materiales de un presupuesto específico.
     */
    public List<DetalleProductoVO> obtenerProductosPorPresupuesto(int idPresupuesto) {
        List<DetalleProductoVO> lista = new ArrayList<>();
        
        try (Connection conn = Conexion.getInstancia().getConnection();
            PreparedStatement ps = conn.prepareStatement(SQL_SELECT_PRODUCTO)) {
            ps.setInt(1, idPresupuesto);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetalleProductoVO prod = new DetalleProductoVO();
                    prod.setIdPresupuesto(rs.getInt("id_presupuesto"));
                    prod.setIdProducto(rs.getString("id_producto"));
                    prod.setNombreProducto(rs.getString("nombre_producto"));
                    prod.setCantidadUsada(rs.getInt("cantidad_usada"));
                    prod.setPrecioVentaUnitario(rs.getBigDecimal("precio_venta_unitario"));
                    prod.setDescuento(rs.getBigDecimal("descuento"));
                    prod.setSubtotal(rs.getBigDecimal("subtotal"));
                    lista.add(prod);
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error al cargar productos: " + e.getMessage());
        }
        return lista;
    }
    
    
    /**
     * Obtiene la cabecera de un presupuesto por su ID.
     */
    public PresupuestoVO obtenerPresupuestoPorId(int idPresupuesto) {
        PresupuestoVO p = null;
        
        try (Connection conn = Conexion.getInstancia().getConnection();
            PreparedStatement ps = conn.prepareStatement(SQL_CABECERA_PRESUPUESTO)) {
             
            ps.setInt(1, idPresupuesto);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p = new com.joseluis.apptaller.modelo.vo.PresupuestoVO();
                    p.setIdPresupuesto(rs.getInt("id_presupuesto"));
                    p.setClienteDni(rs.getString("cliente_dni"));
                    p.setVehiculoBastidor(rs.getString("vehiculo_bastidor"));
                    p.setEstado(rs.getString("estado"));
                    p.setDescripcionTrabajo(rs.getString("descripcion_trabajo"));
                    p.setTotalEstimado(rs.getBigDecimal("total_estimado"));
                    
                    // Conversión segura de SQL Date a LocalDate
                    if (rs.getDate("fecha_emision") != null) {
                        p.setFechaEmision(rs.getDate("fecha_emision").toLocalDate());
                    }
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error al cargar cabecera del presupuesto: " + e.getMessage());
        }
        return p;
    }
    
    
    /**
     * Actualiza el estado de un presupuesto.
     */
    public boolean actualizarEstado(int idPresupuesto, String nuevoEstado) {
        
        try (Connection conn = com.joseluis.apptaller.persistencia.Conexion.getInstancia().getConnection();
            PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_ESTADO)) {
            
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idPresupuesto);
            
            return ps.executeUpdate() > 0;
        } catch (java.sql.SQLException e) {
            System.err.println("Error al actualizar el estado del presupuesto: " + e.getMessage());
            return false;
        }
    }
    
    
}
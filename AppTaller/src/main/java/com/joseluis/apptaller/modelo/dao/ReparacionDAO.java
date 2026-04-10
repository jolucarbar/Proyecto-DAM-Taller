
package com.joseluis.apptaller.modelo.dao;

import com.joseluis.apptaller.modelo.vo.ReparacionVO;
import com.joseluis.apptaller.persistencia.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author joseluis
 */
public class ReparacionDAO {
    // Consultas SQL centralizadas
    private final String SQL_INSERT = "INSERT INTO reparaciones (vehiculo_bastidor, cliente_dni, "
            + "empleado_asignado_id, fecha_entrada, kilometraje_entrada, nivel_combustible, "
            + "estado, prioridad, diagnostico, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final String SQL_SELECT_TABLE = 
            "SELECT r.id_reparacion, v.matricula, c.nombre, e.nombre, r.prioridad, r.estado, r.fecha_entrada " +
            "FROM reparaciones r " +
            "LEFT JOIN vehiculos v ON r.vehiculo_bastidor = v.bastidor " +
            "LEFT JOIN clientes c ON r.cliente_dni = c.dni_cif " +
            "LEFT JOIN empleados e ON r.empleado_asignado_id = e.id_empleado " +
            "ORDER BY r.fecha_entrada DESC";
    
    // Consulta para contar las prioridades de las reparaciones
    private final String SQL_COUNT = "SELECT COUNT(*) FROM reparaciones WHERE prioridad = ? AND estado != 'ENTREGADA'";

    // Consulta específica para obtener el estado
    private final String SQL_SELECT_BY_ESTADO = 
        "SELECT r.id_reparacion, v.matricula, c.nombre, e.nombre, r.prioridad, r.estado, r.fecha_entrada " +
        "FROM reparaciones AS r " +
        "LEFT JOIN vehiculos AS v ON r.vehiculo_bastidor = v.bastidor " +
        "LEFT JOIN clientes AS c ON r.cliente_dni = c.dni_cif " +
        "LEFT JOIN empleados AS e ON r.empleado_asignado_id = e.id_empleado " +
        "WHERE r.estado = ? " +
        "ORDER BY r.fecha_entrada DESC";
    
    // Consulta para seleccionar mecánicos activos
    private final String SQL_ASIGNAR_MECANICO = "UPDATE reparaciones SET empleado_asignado_id = ? WHERE id_reparacion = ?";
    
    // Consulta para actualizar el estado de una reparación
    private final String SQL_UPDATE_ESTADO = "UPDATE reparaciones SET estado = ? WHERE id_reparacion = ?";
    
    // Consulta para rellenar los datos de una reparación en DialogDetallesReparacion
    private final String SQL_DETALLES_REPARACION = "SELECT r.*, c.nombre AS cliente_nom, c.telefono AS cliente_tel, c.email AS cliente_email, "
                   + "v.matricula, v.marca, v.modelo, v.tipo_combustible, e.nombre AS mecanico_nom "
                   + "FROM reparaciones AS r "
                   + "LEFT JOIN clientes AS c ON r.cliente_dni = c.dni_cif "
                   + "LEFT JOIN vehiculos AS v ON r.vehiculo_bastidor = v.bastidor "
                   + "LEFT JOIN empleados AS e ON r.empleado_asignado_id = e.id_empleado "
                   + "WHERE r.id_reparacion = ?";
    
    // Consulta para actualizarlos datos editables de la ficha de reparación. 
    private final String SQL_UPDATE_REPARACION = "UPDATE reparaciones SET estado = ?, prioridad = ?, nivel_combustible = ?, kilometraje_entrada = ?, diagnostico = ? WHERE id_reparacion = ?";
    
    // Consulta para rellenar tabla de trabajos realizados de detalle de reparación.
    private final String SQL_TRABAJOS_REALIZADOS = "SELECT mo.descripcion_trabajo, e.nombre AS mecanico, mo.tiempo_empleado_horas, mo.tarifa_por_hora, mo.subtotal "
                   + "FROM reparacion_detalle_mano_obra AS mo "
                   + "LEFT JOIN empleados AS e ON mo.empleado_id = e.id_empleado "
                   + "WHERE mo.id_reparacion = ?";
    
    // Consulta para obtener las piezas de una reparación
    private final String SQL_PIEZAS_REPARACION = "SELECT rdp.id_producto, p.nombre AS producto, rdp.cantidad_usada, rdp.precio_venta_unitario, rdp.subtotal "
                   + "FROM reparacion_detalle_productos AS rdp "
                   + "LEFT JOIN productos AS p ON rdp.id_producto = p.id_producto "
                   + "WHERE rdp.id_reparacion = ?";
    
   // Consulta para calcular el coste total de la reparación.
   private final String SQL_COSTE_TOTAL = "SELECT "
                   + "(SELECT COALESCE(SUM(subtotal), 0) FROM reparacion_detalle_mano_obra WHERE id_reparacion = ?) AS total_mo, "
                   + "(SELECT COALESCE(SUM(subtotal), 0) FROM reparacion_detalle_productos WHERE id_reparacion = ?) AS total_piezas";
    
   //  
   private final String SQL_HISTORIAL_REPARACIONES = "SELECT r.fecha_entrada, r.estado, e.nombre AS mecanico, r.diagnostico AS notas "
                   + "FROM reparaciones AS r "
                   + "LEFT JOIN empleados AS e ON r.empleado_asignado_id = e.id_empleado "
                   + "WHERE r.vehiculo_bastidor = (SELECT vehiculo_bastidor FROM reparaciones WHERE id_reparacion = ?) "
                   + "ORDER BY r.fecha_entrada DESC"; // Las más recientes primero
    
    
    public boolean insertar(ReparacionVO rep) {
        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            
            ps.setString(1, rep.getVehiculoBastidor());
            ps.setString(2, rep.getClienteDni());
            ps.setInt(3, rep.getEmpleadoAsignadoId());
            ps.setTimestamp(4, Timestamp.valueOf(rep.getFechaEntrada()));
            ps.setInt(5, rep.getKilometrajeEntrada());
            ps.setString(6, rep.getNivelCombustible());
            ps.setString(7, rep.getEstado());
            ps.setString(8, rep.getPrioridad());
            ps.setString(9, rep.getDiagnostico());
            ps.setString(10, rep.getObservaciones());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error SQL al insertar reparación: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Object[]> obtenerListaParaTabla() {
        List<Object[]> datos = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_TABLE);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                // Validación para que no salga "null" en la tabla si no hay mecánico
                String mecanico = rs.getString(4);
                if (mecanico == null || mecanico.trim().isEmpty()) {
                    mecanico = "Sin asignar";
                }
                
                datos.add(new Object[]{
                    "REP-" + rs.getInt(1), // ID formateado
                    rs.getString(2) != null ? rs.getString(2) : "Desconocido", // Matrícula
                    rs.getString(3) != null ? rs.getString(3) : "Desconocido", // Cliente
                    mecanico,              // Nombre Mecánico 
                    rs.getString(5),       // Prioridad
                    rs.getString(6),       // Estado
                    rs.getTimestamp(7)     // Fecha Entrada
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar la tabla de reparaciones: " + e.getMessage());
        }
        return datos;
    }
    
    
    /**
     * Método para contar cuántas reparaciones hay según su prioridad o estado. 
     * 
     * @param prioridad
     * @return 
     */
    public int contarPorPrioridad(String prioridad) {
        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_COUNT)) {
            ps.setString(1, prioridad);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
        }
        return 0;
    }
    
    /**
     * Método para filtrar los vehiculos en reparaciones por estado
     * 
     * @param estado
     * @return 
     */
    public List<Object[]> obtenerListaFiltrada(String estado) {
        // Si eligen "TODOS", reutilizamos el método que ya tienes
        if (estado.equals("TODOS")) {
            return obtenerListaParaTabla(); 
        }

        List<Object[]> datos = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ESTADO)) {
            
            ps.setString(1, estado); // Inyectamos el estado (Ej: "EN_COLA")
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String mecanico = rs.getString(4);
                    if (mecanico == null || mecanico.trim().isEmpty()) {
                        mecanico = "Sin asignar";
                    }
                    
                    datos.add(new Object[]{
                        "REP-" + rs.getInt(1),
                        rs.getString(2) != null ? rs.getString(2) : "Desconocido",
                        rs.getString(3) != null ? rs.getString(3) : "Desconocido",
                        mecanico,
                        rs.getString(5),
                        rs.getString(6),
                        rs.getTimestamp(7)
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al filtrar reparaciones: " + e.getMessage());
        }
        return datos;
    }
    
    public boolean actualizarEstado(int idReparacion, String nuevoEstado) {
        

        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_ESTADO)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, idReparacion);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado: " + e.getMessage());
            return false;
        }
    }
    
    
    /**
     * Asigna un mecánico a una reparación específica.
     */
    public boolean asignarMecanico(int idReparacion, int idEmpleado) {
                
        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_ASIGNAR_MECANICO)) {
            
            ps.setInt(1, idEmpleado);
            ps.setInt(2, idReparacion);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al asignar mecánico: " + e.getMessage());
            return false;
        }
    }
    
    
    /**
     * Obtiene la ficha completa de una reparación cruzando datos con Clientes, Vehículos y Empleados.
     */
    public java.util.Map<String, String> obtenerFichaCompleta(int idReparacion) {
        java.util.Map<String, String> ficha = new java.util.HashMap<>();
        
        

        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DETALLES_REPARACION)) {
            
            ps.setInt(1, idReparacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Datos de la Reparación
                    ficha.put("id", "REP-" + rs.getInt("id_reparacion"));
                    ficha.put("fechaEntrada", rs.getTimestamp("fecha_entrada").toString());
                    ficha.put("estado", rs.getString("estado"));
                    ficha.put("prioridad", rs.getString("prioridad"));
                    ficha.put("kilometraje", String.valueOf(rs.getInt("kilometraje_entrada")));
                    ficha.put("combustible", rs.getString("nivel_combustible"));
                    ficha.put("diagnostico", rs.getString("diagnostico") != null ? rs.getString("diagnostico") : "");
                    ficha.put("trabajos", rs.getString("trabajos_realizados") != null ? rs.getString("trabajos_realizados") : "");
                    ficha.put("observaciones", rs.getString("observaciones") != null ? rs.getString("observaciones") : "");
                    ficha.put("cliente_nombre", rs.getString("cliente_nom") != null ? rs.getString("cliente_nom") : "Sin cliente");
                    ficha.put("cliente_telefono", rs.getString("cliente_tel") != null ? rs.getString("cliente_tel") : "N/A");
                    ficha.put("vehiculo_marca", rs.getString("marca") != null ? rs.getString("marca") : "Desconocida");
                    ficha.put("vehiculo_modelo", rs.getString("modelo") != null ? rs.getString("modelo") : "Desconocido");
                    ficha.put("vehiculo_matricula", rs.getString("matricula") != null ? rs.getString("matricula") : "S/M");
                    
                    // Datos Relacionales (Con control de nulos)
                    ficha.put("cliente", rs.getString("cliente_nom") != null ? rs.getString("cliente_nom") : "Sin cliente");
                    ficha.put("telefono", rs.getString("cliente_tel") != null ? rs.getString("cliente_tel") : "N/A");
                    ficha.put("vehiculo", rs.getString("marca") + " " + rs.getString("modelo") + " (" + rs.getString("matricula") + ")");
                    ficha.put("mecanico", rs.getString("mecanico_nom") != null ? rs.getString("mecanico_nom") : "Sin asignar");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar ficha de reparación: " + e.getMessage());
        }
        return ficha;
    }
    
    
    /**
     * Actualiza los datos editables de la ficha de reparación.
     */
    public boolean actualizarFicha(int idReparacion, String estado, String prioridad, String combustible, int kilometraje, String diagnostico) {
        
        
        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_REPARACION)) {
            
            ps.setString(1, estado);
            ps.setString(2, prioridad);
            ps.setString(3, combustible);
            ps.setInt(4, kilometraje);
            ps.setString(5, diagnostico);
            ps.setInt(6, idReparacion);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar la ficha: " + e.getMessage());
            return false;
        }
    }
    
    
    /**
     * Obtiene la lista de trabajos (mano de obra) de una reparación.
     */
    /**
     * Obtiene la lista de trabajos (mano de obra) reales de una reparación.
     */
    public java.util.List<Object[]> obtenerTrabajosRealizados(int idReparacion) {
        java.util.List<Object[]> lista = new java.util.ArrayList<>();
        

        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_TRABAJOS_REALIZADOS)) {
            
            ps.setInt(1, idReparacion);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Mapeamos a las 5 columnas de tu JTable visual: 
                    // [Concepto, Mecánico, Horas, Precio/Hora, Total]
                    lista.add(new Object[]{
                        rs.getString("descripcion_trabajo"),
                        rs.getString("mecanico") != null ? rs.getString("mecanico") : "Sin asignar",
                        rs.getBigDecimal("tiempo_empleado_horas"),
                        rs.getBigDecimal("tarifa_por_hora") + " €",
                        rs.getBigDecimal("subtotal") + " €"
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar trabajos de mano de obra: " + e.getMessage());
        }
        return lista;
    }
    
    
    /**
     * Obtiene la lista de piezas (productos) reales utilizados en una reparación.
     */
    public java.util.List<Object[]> obtenerPiezasUtilizadas(int idReparacion) {
        java.util.List<Object[]> lista = new java.util.ArrayList<>();
        
        

        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_PIEZAS_REPARACION)) {
            
            ps.setInt(1, idReparacion);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                        rs.getString("id_producto"),
                        rs.getString("producto") != null ? rs.getString("producto") : "Producto Desconocido",
                        rs.getInt("cantidad_usada"),
                        rs.getBigDecimal("precio_venta_unitario") + " €",
                        rs.getBigDecimal("subtotal") + " €"
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar las piezas de la reparación: " + e.getMessage());
        }
        return lista;
    }
    
    
    /**
     * Obtiene los sumatorios de costos (mano de obra y piezas) de una reparación.
     */
    public java.util.Map<String, java.math.BigDecimal> obtenerCostos(int idReparacion) {
        java.util.Map<String, java.math.BigDecimal> costos = new java.util.HashMap<>();
        
        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_COSTE_TOTAL)) {
            
            ps.setInt(1, idReparacion);
            ps.setInt(2, idReparacion); // Le pasamos el ID dos veces (uno por cada subconsulta)
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    costos.put("mano_obra", rs.getBigDecimal("total_mo"));
                    costos.put("piezas", rs.getBigDecimal("total_piezas"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al calcular costos: " + e.getMessage());
        }
        return costos;
    }
    
    
    /**
     * Obtiene el historial de todas las visitas al taller del vehículo asociado a una reparación.
     */
    public java.util.List<Object[]> obtenerHistorialVehiculo(int idReparacion) {
        java.util.List<Object[]> lista = new java.util.ArrayList<>();
        
        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_HISTORIAL_REPARACIONES)) {
            
            ps.setInt(1, idReparacion);
            
            try (ResultSet rs = ps.executeQuery()) {
                // Formateador para que la fecha se vea limpia (Ej: 26/12/2025)
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                
                while (rs.next()) {
                    // Si no hay fecha (no debería pasar), ponemos N/A. Si la hay, la formateamos.
                    String fechaLimpia = "N/A";
                    if (rs.getTimestamp("fecha_entrada") != null) {
                        fechaLimpia = sdf.format(rs.getTimestamp("fecha_entrada"));
                    }
                    
                    // Mapeamos a las 4 columnas de tu JTable visual: 
                    // [Fecha, Estado, Usuario, Notas]
                    lista.add(new Object[]{
                        fechaLimpia,
                        rs.getString("estado"),
                        rs.getString("mecanico") != null ? rs.getString("mecanico") : "Sin asignar",
                        rs.getString("notas") != null ? rs.getString("notas") : ""
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar el historial del vehículo: " + e.getMessage());
        }
        return lista;
    }
    
}

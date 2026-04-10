
package com.joseluis.apptaller.controlador;

import com.joseluis.apptaller.modelo.dao.ReparacionDAO;
import com.joseluis.apptaller.modelo.vo.ReparacionVO;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * Controlador que gestiona la lógica de negocio de las reparaciones.
 * 
 * @author joseluis
 */
public class ControladorReparaciones {
    private final ReparacionDAO reparacionDAO;

    public ControladorReparaciones() {
        this.reparacionDAO = new ReparacionDAO();
    }

    /**
     * Llena el modelo de una JTable con los datos de las reparaciones activas.
     */
    public void llenarTablaReparaciones(DefaultTableModel modelo) {
        modelo.setRowCount(0); // Resetear tabla
        List<Object[]> filas = reparacionDAO.obtenerListaParaTabla();
        for (Object[] fila : filas) {
            modelo.addRow(fila);
        }
    }

    /**
     * Procesa el registro de una nueva reparación con validaciones previas.
     */
    public boolean guardarNuevaReparacion(ReparacionVO rep) {
        // Lógica de negocio: Validar que el kilometraje sea coherente
        if (rep.getKilometrajeEntrada() < 0) {
            return false; 
        }
        
        // Lógica de negocio: Forzar mayúsculas en el bastidor por seguridad
        rep.setVehiculoBastidor(rep.getVehiculoBastidor().toUpperCase());
        
        return reparacionDAO.insertar(rep);
    }
    
    /**
     * Función que devuelva un objeto con todas las estadísticas necesarias 
     * para los paneles superiores.
     * @return 
     */
    public java.util.Map<String, Integer> obtenerEstadisticas() {
        java.util.Map<String, Integer> stats = new java.util.HashMap<>();

        // Contamos en BD y guardamos el resultado con una clave clara
        stats.put("Urgente", reparacionDAO.contarPorPrioridad("Urgente"));
        stats.put("Alta", reparacionDAO.contarPorPrioridad("Alta"));
        stats.put("Normal", reparacionDAO.contarPorPrioridad("Normal"));
        stats.put("Baja", reparacionDAO.contarPorPrioridad("Baja"));
        stats.put("NA", reparacionDAO.contarPorPrioridad("NA"));

        return stats;
    }
    
    /**
     * Filtra la tblReparaciones según el estado seleccionado en el cbxFiltrarEstado
     */
    public void filtrarTablaReparaciones(DefaultTableModel modelo, String estado) {
        modelo.setRowCount(0); // Limpiamos la tabla visual
        List<Object[]> filas = reparacionDAO.obtenerListaFiltrada(estado);
        for (Object[] fila : filas) {
            modelo.addRow(fila);
        }
    }
    
    /**
     * Método para cambiar el estado de reparación de un vehículo.
     * @param idReparacion
     * @param nuevoEstado
     * @param modelo
     * @return 
     */
    public boolean cambiarEstado(int idReparacion, String nuevoEstado, DefaultTableModel modelo) {
        if (reparacionDAO.actualizarEstado(idReparacion, nuevoEstado)) {
            llenarTablaReparaciones(modelo);
            return true;
        }
        return false;
    }
    
    
    /**
     * Asigna un mecánico y refresca la tabla manteniendo el filtro actual.
     */
    public boolean asignarMecanico(int idReparacion, int idEmpleado, DefaultTableModel modelo, String estadoFiltro) {
        if (reparacionDAO.asignarMecanico(idReparacion, idEmpleado)) {
            filtrarTablaReparaciones(modelo, estadoFiltro);
            return true;
        }
        return false;
    }
    
    
    /**
     * Valida los datos de la vista y delega la actualización al DAO.
     */
    public boolean guardarCambiosFicha(int idReparacion, String estado, String prioridad, String combustible, String kilometrajeCrudo, String diagnostico) {
        
        int kilometrajeInt = 0;
        try {
            // Quitamos letras como " Km" y dejamos solo números
            String kmLimpio = kilometrajeCrudo.replaceAll("[^0-9]", "");
            kilometrajeInt = Integer.parseInt(kmLimpio);
        } catch (NumberFormatException ex) {
            System.err.println("Controlador: Error en el formato del kilometraje.");
            return false; 
        }

        return reparacionDAO.actualizarFicha(idReparacion, estado, prioridad, combustible, kilometrajeInt, diagnostico);
    }
    
    
    /**
     * Llena la tabla de Trabajos Realizados en la ficha de detalles.
     */
    public void cargarTablaTrabajos(int idReparacion, javax.swing.table.DefaultTableModel modelo) {
        // Limpiamos la tabla por si tuviera datos anteriores
        modelo.setRowCount(0);
        
        // Pedimos los datos al DAO
        java.util.List<Object[]> trabajos = reparacionDAO.obtenerTrabajosRealizados(idReparacion);
        
        // Rellenamos la vista
        for (Object[] fila : trabajos) {
            modelo.addRow(fila);
        }
    }
    
    
    /**
     * Llena la tabla de Piezas (Materiales) en la ficha de detalles.
     */
    public void cargarTablaPiezas(int idReparacion, javax.swing.table.DefaultTableModel modelo) {
        // Limpiamos la tabla
        modelo.setRowCount(0);
        
        // Pedimos los datos al DAO
        java.util.List<Object[]> piezas = reparacionDAO.obtenerPiezasUtilizadas(idReparacion);
        
        // Rellenamos la vista
        for (Object[] fila : piezas) {
            modelo.addRow(fila);
        }
    }
    
    
    /**
     * Calcula los impuestos y devuelve los textos formateados para la vista.
     */
    public java.util.Map<String, String> calcularYFormatearCostos(int idReparacion) {
        // Pedimos los datos al DAO
        java.util.Map<String, java.math.BigDecimal> costos = reparacionDAO.obtenerCostos(idReparacion);
        
        java.math.BigDecimal totalMO = costos.getOrDefault("mano_obra", java.math.BigDecimal.ZERO);
        java.math.BigDecimal totalPiezas = costos.getOrDefault("piezas", java.math.BigDecimal.ZERO);
        
        // Matemáticas de facturación
        java.math.BigDecimal baseImponible = totalMO.add(totalPiezas);
        // Multiplicamos por 0.21 para sacar el IVA
        java.math.BigDecimal iva = baseImponible.multiply(new java.math.BigDecimal("0.21")); 
        java.math.BigDecimal total = baseImponible.add(iva);
        
        // Formateo (Ej: 1.250,50 €)
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00 €");
        
        // Empaquetamos para la vista
        java.util.Map<String, String> resultado = new java.util.HashMap<>();
        resultado.put("mano_obra", df.format(totalMO));
        resultado.put("piezas", df.format(totalPiezas));
        resultado.put("base", df.format(baseImponible));
        resultado.put("iva", df.format(iva));
        resultado.put("total", df.format(total));
        
        return resultado;
    }
    
    
    /**
     * Llena la tabla de Historial del vehículo en la ficha de detalles.
     */
    public void cargarTablaHistorial(int idReparacion, javax.swing.table.DefaultTableModel modelo) {
        // Limpiamos la tabla
        modelo.setRowCount(0);
        
        // Pedimos los datos al DAO
        java.util.List<Object[]> historial = reparacionDAO.obtenerHistorialVehiculo(idReparacion);
        
        // Rellenamos la vista
        for (Object[] fila : historial) {
            modelo.addRow(fila);
        }
    }
    
}

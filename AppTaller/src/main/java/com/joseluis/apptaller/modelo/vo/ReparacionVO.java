
package com.joseluis.apptaller.modelo.vo;

import java.time.LocalDateTime;

/**
 * Value Object para la tabla Reparaciones.
 * Refleja fielmente la estructura de la base de datos apptaller_db.
 * 
 * @author joseluis
 */
public class ReparacionVO {
    private int idReparacion;
    private String vehiculoBastidor;
    private String clienteDni;
    private int empleadoAsignadoId;
    private Integer idPresupuesto; 
    private LocalDateTime fechaEntrada;
    private int kilometrajeEntrada;
    private String nivelCombustible;
    private String estado; 
    private String prioridad;
    private String diagnostico;
    private String observaciones;
    
    // --- Campos adicionales para las vistas (Rellenados mediante JOIN) ---
    private String clienteNombre;
    private String vehiculoMatricula;
    private double totalPresupuesto;

    public ReparacionVO() {
        this.fechaEntrada = LocalDateTime.now(); // Por defecto ahora
        this.estado = "EN_COLA"; // Estado inicial estándar
    }

    // Getters y Setters
    public int getIdReparacion() { return idReparacion; }
    public void setIdReparacion(int idReparacion) { this.idReparacion = idReparacion; }

    public String getVehiculoBastidor() { return vehiculoBastidor; }
    public void setVehiculoBastidor(String vehiculoBastidor) { this.vehiculoBastidor = vehiculoBastidor; }

    public String getClienteDni() { return clienteDni; }
    public void setClienteDni(String clienteDni) { this.clienteDni = clienteDni; }

    public int getEmpleadoAsignadoId() { return empleadoAsignadoId; }
    public void setEmpleadoAsignadoId(int empleadoAsignadoId) { this.empleadoAsignadoId = empleadoAsignadoId; }

    public LocalDateTime getFechaEntrada() { return fechaEntrada; }
    public void setFechaEntrada(LocalDateTime fechaEntrada) { this.fechaEntrada = fechaEntrada; }

    public int getKilometrajeEntrada() { return kilometrajeEntrada; }
    public void setKilometrajeEntrada(int kilometrajeEntrada) { this.kilometrajeEntrada = kilometrajeEntrada; }

    public String getNivelCombustible() { return nivelCombustible; }
    public void setNivelCombustible(String nivelCombustible) { this.nivelCombustible = nivelCombustible; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Integer getIdPresupuesto() { return idPresupuesto; }

    public void setIdPresupuesto(Integer idPresupuesto) { this.idPresupuesto = idPresupuesto; }
    
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public String getVehiculoMatricula() { return vehiculoMatricula; }
    public void setVehiculoMatricula(String vehiculoMatricula) { this.vehiculoMatricula = vehiculoMatricula; }

    public double getTotalPresupuesto() { return totalPresupuesto; }
    public void setTotalPresupuesto(double totalPresupuesto) { this.totalPresupuesto = totalPresupuesto; }
}

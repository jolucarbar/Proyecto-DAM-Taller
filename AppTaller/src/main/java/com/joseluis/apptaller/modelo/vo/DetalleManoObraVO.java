
package com.joseluis.apptaller.modelo.vo;

import java.math.BigDecimal;

/**
 * Clase que almacena la información de los servicios de mano de obra.
 * Guarda las horas trabajadas y las tarifas aplicadas a cada tarea
 * dentro de un presupuesto o reparación.
 * 
 * @author José Luis Cárdenas Barroso
 * @info Proyecto Intermodular del Grado Superior DAM
 * @institution IES Augustóbriga
 */
public class DetalleManoObraVO {
    private Integer idDetalle;
    private Integer idPresupuesto;
    private String descripcionTrabajo;
    private BigDecimal tiempoEmpleadoHoras;  // BigDecimal por precisión matemática en los cálculos
    private BigDecimal tarifaPorHora;
    private BigDecimal subtotal; 

    public DetalleManoObraVO() {}

    public DetalleManoObraVO(String descripcionTrabajo, BigDecimal tiempoEmpleadoHoras, BigDecimal tarifaPorHora) {
        this.descripcionTrabajo = descripcionTrabajo;
        this.tiempoEmpleadoHoras = tiempoEmpleadoHoras;
        this.tarifaPorHora = tarifaPorHora;
        this.subtotal = tiempoEmpleadoHoras.multiply(tarifaPorHora);
    }

    // Getters y Setters
    public Integer getIdDetalle() { return idDetalle; }
    public void setIdDetalle(Integer idDetalle) { this.idDetalle = idDetalle; }

    public Integer getIdPresupuesto() { return idPresupuesto; }
    public void setIdPresupuesto(Integer idPresupuesto) { this.idPresupuesto = idPresupuesto; }

    public String getDescripcionTrabajo() { return descripcionTrabajo; }
    public void setDescripcionTrabajo(String descripcionTrabajo) { this.descripcionTrabajo = descripcionTrabajo; }

    public BigDecimal getTiempoEmpleadoHoras() { return tiempoEmpleadoHoras; }
    public void setTiempoEmpleadoHoras(BigDecimal tiempoEmpleadoHoras) { this.tiempoEmpleadoHoras = tiempoEmpleadoHoras; }

    public BigDecimal getTarifaPorHora() { return tarifaPorHora; }
    public void setTarifaPorHora(BigDecimal tarifaPorHora) { this.tarifaPorHora = tarifaPorHora; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}

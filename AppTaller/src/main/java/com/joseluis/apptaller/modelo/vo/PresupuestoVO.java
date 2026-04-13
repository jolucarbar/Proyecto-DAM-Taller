
package com.joseluis.apptaller.modelo.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Value Object principal (Aggregate Root) para la tabla Presupuestos.
 * @author joseluis
 */
public class PresupuestoVO {
    private Integer idPresupuesto;
    private String vehiculoBastidor;
    private String clienteDni;
    private LocalDate fechaEmision;
    private LocalDate fechaValidez;
    private String estado;
    private String descripcionTrabajo;
    private BigDecimal totalEstimado;
    private String observaciones;
    private String motivoRechazo;
    private Integer usuarioCreador;
    
    // --- Atributos de Agregación (Las líneas del presupuesto) ---
    private List<DetalleManoObraVO> lineasManoObra;
    private List<DetalleProductoVO> lineasProductos;

    public PresupuestoVO() {
        this.fechaEmision = LocalDate.now();
        this.fechaValidez = LocalDate.now().plusDays(15); // Validez estándar de 15 días
        this.estado = "PENDIENTE";
        this.totalEstimado = BigDecimal.ZERO;
        this.lineasManoObra = new ArrayList<>();
        this.lineasProductos = new ArrayList<>();
    }

    // --- Métodos de utilidad para las líneas ---
    public void addLineaManoObra(DetalleManoObraVO linea) {
        this.lineasManoObra.add(linea);
    }

    public void addLineaProducto(DetalleProductoVO linea) {
        this.lineasProductos.add(linea);
    }

    // Getters y Setters
    public Integer getIdPresupuesto() { return idPresupuesto; }
    public void setIdPresupuesto(Integer idPresupuesto) { this.idPresupuesto = idPresupuesto; }

    public String getVehiculoBastidor() { return vehiculoBastidor; }
    public void setVehiculoBastidor(String vehiculoBastidor) { this.vehiculoBastidor = vehiculoBastidor; }

    public String getClienteDni() { return clienteDni; }
    public void setClienteDni(String clienteDni) { this.clienteDni = clienteDni; }

    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }

    public LocalDate getFechaValidez() { return fechaValidez; }
    public void setFechaValidez(LocalDate fechaValidez) { this.fechaValidez = fechaValidez; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getDescripcionTrabajo() { return descripcionTrabajo; }
    public void setDescripcionTrabajo(String descripcionTrabajo) { this.descripcionTrabajo = descripcionTrabajo; }

    public BigDecimal getTotalEstimado() { return totalEstimado; }
    public void setTotalEstimado(BigDecimal totalEstimado) { this.totalEstimado = totalEstimado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }

    public Integer getUsuarioCreador() { return usuarioCreador; }
    public void setUsuarioCreador(Integer usuarioCreador) { this.usuarioCreador = usuarioCreador; }

    public List<DetalleManoObraVO> getLineasManoObra() { return lineasManoObra; }
    public void setLineasManoObra(List<DetalleManoObraVO> lineasManoObra) { this.lineasManoObra = lineasManoObra; }

    public List<DetalleProductoVO> getLineasProductos() { return lineasProductos; }
    public void setLineasProductos(List<DetalleProductoVO> lineasProductos) { this.lineasProductos = lineasProductos; }
}

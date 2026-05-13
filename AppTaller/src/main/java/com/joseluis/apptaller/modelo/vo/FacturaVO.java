package com.joseluis.apptaller.modelo.vo;

import java.time.LocalDate;

/**
 * Clase que representa el documento contable de una factura en el sistema.
 * Contiene toda la información fiscal, importes, impuestos y datos relacionales 
 * necesarios para la emisión legal del cobro por los servicios del taller.
 *
 * @author José Luis Cárdenas Barroso
 * @info Proyecto Intermodular del Grado Superior DAM
 * @institution IES Augustóbriga
 */
public class FacturaVO {
    private int idFactura;
    private String numeroFactura;
    private int idReparacion;
    private int idPresupuesto; 
    private String clienteDni;
    private String vehiculoBastidor;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento; 
    private double baseImponible; 
    private double iva; 
    private double totalCobrado;
    private String metodoPago; 
    private String estado;
    private String observaciones; 
    private int usuarioEmisor; 
    private String clienteNombre;
    private String vehiculoMatricula;

    

    // Constructor vacío requerido para el DAO
    public FacturaVO() {
    }

    // --- GETTERS Y SETTERS ---

    public int getIdFactura() { return idFactura; }
    public void setIdFactura(int idFactura) { this.idFactura = idFactura; }

    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }

    public int getIdReparacion() { return idReparacion; }
    public void setIdReparacion(int idReparacion) { this.idReparacion = idReparacion; }

    public int getIdPresupuesto() { return idPresupuesto; }
    public void setIdPresupuesto(int idPresupuesto) { this.idPresupuesto = idPresupuesto; }

    public String getClienteDni() { return clienteDni; }
    public void setClienteDni(String clienteDni) { this.clienteDni = clienteDni; }

    public String getVehiculoBastidor() { return vehiculoBastidor; }
    public void setVehiculoBastidor(String vehiculoBastidor) { this.vehiculoBastidor = vehiculoBastidor; }

    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public double getBaseImponible() { return baseImponible; }
    public void setBaseImponible(double baseImponible) { this.baseImponible = baseImponible; }

    public double getIva() { return iva; }
    public void setIva(double iva) { this.iva = iva; }

    public double getTotalCobrado() { return totalCobrado; }
    public void setTotalCobrado(double totalCobrado) { this.totalCobrado = totalCobrado; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public int getUsuarioEmisor() { return usuarioEmisor; }
    public void setUsuarioEmisor(int usuarioEmisor) { this.usuarioEmisor = usuarioEmisor; }
    
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    
    public String getVehiculoMatricula() { return vehiculoMatricula; }

    public void setVehiculoMatricula(String vehiculoMatricula) { this.vehiculoMatricula = vehiculoMatricula; }
}
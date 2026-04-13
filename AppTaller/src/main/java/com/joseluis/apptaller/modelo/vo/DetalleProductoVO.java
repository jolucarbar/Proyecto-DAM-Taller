
package com.joseluis.apptaller.modelo.vo;

import java.math.BigDecimal;

/**
 * Value Object para la tabla detalle_productos de Presupuestos.
 * @author joseluis
 */
public class DetalleProductoVO {
    private Integer idPresupuesto;
    private String idProducto;
    private String nombreProducto; // Dato extraído mediante JOIN para la tabla visual
    private int cantidadUsada;
    private BigDecimal precioVentaUnitario;
    private BigDecimal descuento;
    private BigDecimal subtotal; // En BD es GENERATED ALWAYS

    public DetalleProductoVO() {}

    public DetalleProductoVO(String idProducto, String nombreProducto, int cantidadUsada, BigDecimal precioVentaUnitario, BigDecimal descuento) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.cantidadUsada = cantidadUsada;
        this.precioVentaUnitario = precioVentaUnitario;
        this.descuento = (descuento != null) ? descuento : BigDecimal.ZERO;
        
        // Cálculo visual del subtotal antes de ir a la BD
        BigDecimal totalSinDescuento = precioVentaUnitario.multiply(new BigDecimal(cantidadUsada));
        BigDecimal multiplicadorDescuento = BigDecimal.ONE.subtract(this.descuento.divide(new BigDecimal("100")));
        this.subtotal = totalSinDescuento.multiply(multiplicadorDescuento);
    }

    // Getters y Setters
    public Integer getIdPresupuesto() { return idPresupuesto; }
    public void setIdPresupuesto(Integer idPresupuesto) { this.idPresupuesto = idPresupuesto; }

    public String getIdProducto() { return idProducto; }
    public void setIdProducto(String idProducto) { this.idProducto = idProducto; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public int getCantidadUsada() { return cantidadUsada; }
    public void setCantidadUsada(int cantidadUsada) { this.cantidadUsada = cantidadUsada; }

    public BigDecimal getPrecioVentaUnitario() { return precioVentaUnitario; }
    public void setPrecioVentaUnitario(BigDecimal precioVentaUnitario) { this.precioVentaUnitario = precioVentaUnitario; }

    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}

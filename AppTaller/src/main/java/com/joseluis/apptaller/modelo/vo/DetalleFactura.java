
package com.joseluis.apptaller.modelo.vo;

/**
 *
 * @author joseluis
 */
public class DetalleFactura {
    private final String descripcion;
    private final double cantidad;
    private final double precioUnitario;
    private final double subtotal;

    public DetalleFactura(String descripcion, double cantidad, double precioUnitario, double subtotal) {
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    // GETTERS (para usarlos en JasperReports)
    public String getDescripcion() { return descripcion; }
    public double getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public double getSubtotal() { return subtotal; }
}

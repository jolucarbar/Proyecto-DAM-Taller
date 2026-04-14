
package com.joseluis.apptaller.modelo.vo;

/**
 * Representa cada una de las líneas individuales de una factura.
 * Contiene el desglose de los conceptos, cantidades y precios necesarios 
 * para generar el informe visual y el cálculo de subtotales.
 * 
 * @author José Luis Cárdenas Barroso
 * @info Proyecto Intermodular del Grado Superior DAM
 * @institution IES Augustóbriga
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

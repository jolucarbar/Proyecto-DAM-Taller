
package com.joseluis.apptaller.modelo.vo;

import java.util.Date;


/**
 * Clase que representa un producto o pieza de recambio en el inventario del taller.
 * Almacena la información de stock, precios y ubicación, e incluye lógica de validación
 * interna para garantizar la consistencia de los datos antes de guardarlos.
 *
 * @author José Luis Cárdenas Barroso
 * @info Proyecto Intermodular del Grado Superior DAM
 * @institution IES Augustóbriga
 */
public class ProductoVO {
    // Atributos basados en la estructura de la tabla 'productos'
    private String idProducto;      // PK: Referencia o Código de barras
    private String nombre;
    private String descripcion;
    private String categoria;
    private int cantidadStock;
    private int stockMinimo;
    private double precioCompra;
    private double precioUnitario;  // Precio de venta al público
    private String proveedorCif;    // FK a proveedores
    private String ubicacionAlmacen;
    private Date created_at;
    private boolean activo;

    // Constructor vacío (Requerido por el DAO) 
    public ProductoVO() {
        this.activo = true;
        this.cantidadStock = 0;
        this.stockMinimo = 5; // Valor por defecto en SQL
    }

    
    // --- MÉTODOS DE VALIDACIÓN DE NEGOCIO ---

    /**
     * Valida si el producto tiene los datos mínimos consistentes.
     * @throws IllegalArgumentException si algún dato es inválido.
     */
    public void validar() throws IllegalArgumentException {
        if (idProducto == null || idProducto.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID/Referencia del producto no puede estar vacío.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        if (precioUnitario <= 0) {
            throw new IllegalArgumentException("El precio de venta debe ser mayor a 0."); // Regla SQL
        }
        if (cantidadStock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo."); // Regla SQL
        }
    }
    

    /**
     * Comprueba si el producto necesita reposición inmediata.
     * @return true si el stock actual es igual o menor al mínimo.
     */
    public boolean necesitaPedido() {
        return this.cantidadStock <= this.stockMinimo;
    }

    
    // --- GETTERS Y SETTERS ---

    public String getIdProducto() { return idProducto; }
    public void setIdProducto(String idProducto) { this.idProducto = idProducto; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public int getCantidadStock() { return cantidadStock; }
    public void setCantidadStock(int cantidadStock) { this.cantidadStock = cantidadStock; }

    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }

    public double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(double precioCompra) { this.precioCompra = precioCompra; }

    public double getPrecioUnitario(float par) { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public String getProveedorCif() { return proveedorCif; }
    public void setProveedorCif(String proveedorCif) { this.proveedorCif = proveedorCif; }

    public String getUbicacionAlmacen() { return ubicacionAlmacen; }
    public void setUbicacionAlmacen(String ubicacionAlmacen) { this.ubicacionAlmacen = ubicacionAlmacen; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    
    public Date getCreated_at() { return created_at; }
    public void setCreated_at(Date created_at) { this.created_at = created_at; }

    @Override
    public String toString() {
        return nombre + " [" + idProducto + "]";
    }
}

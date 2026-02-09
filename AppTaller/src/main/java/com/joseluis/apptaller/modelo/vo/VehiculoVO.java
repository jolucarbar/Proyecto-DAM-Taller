
package com.joseluis.apptaller.modelo.vo;

/**
 * Contiene los datos técnicos del coche
 */

/**
 *
 * @author joseluis
 */
public class VehiculoVO {
    
    private String matricula; // Clave primaria
    private String marca;
    private String modelo;
    private int anio;
    private String color;
    private String numeroChasis;
    private String dniCliente; // Relación con ClienteVO
    private boolean activo;

    public VehiculoVO() {
    }
    
    public VehiculoVO(boolean activo) {
        this.activo = activo;
    }

    public VehiculoVO(String matricula, String marca, String modelo, int anio, String color, String numeroChasis, String dniCliente) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.color = color;
        this.numeroChasis = numeroChasis;
        this.dniCliente = dniCliente;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getNumeroChasis() {
        return numeroChasis;
    }

    public void setNumeroChasis(String numeroChasis) {
        this.numeroChasis = numeroChasis;
    }

    public String getDniCliente() {
        return dniCliente;
    }

    public void setDniCliente(String dniCliente) {
        this.dniCliente = dniCliente;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "VehiculoVO{" + "matricula=" + matricula + ", marca=" + marca + ", modelo=" + modelo + ", anio=" + anio + ", color=" + color + ", numeroChasis=" + numeroChasis + ", dniCliente=" + dniCliente + ", activo=" + activo + '}';
    }
    
    
    
}

package com.joseluis.apptaller.modelo.vo;

public class VehiculoVO {
    // Usamos los nombres alineados con tu BD (respetando camelCase)
    private String bastidor;   // Antes numeroChasis (PK en BD)
    private String matricula;  // (Unique en BD)
    private String marca;
    private String modelo;
    private int anioFabricacion; // Antes anio
    private String color;
    private String dniPropietario; // Antes dniCliente (mapea a propietario_actual_dni)
    private boolean activo;

    // --- Constructor Vacío ---
    public VehiculoVO() {
        this.activo = true;
    }

    // --- Constructor Completo ---
    public VehiculoVO(String bastidor, String matricula, String marca, String modelo, 
                      int anioFabricacion, String color, String dniPropietario) {
        this.bastidor = bastidor;
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.anioFabricacion = anioFabricacion;
        this.color = color;
        this.dniPropietario = dniPropietario;
        this.activo = true;
    }

    // --- Getters y Setters (Actualizados) ---
    public String getBastidor() { return bastidor; }
    public void setBastidor(String bastidor) { this.bastidor = bastidor; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public int getAnioFabricacion() { return anioFabricacion; }
    public void setAnioFabricacion(int anioFabricacion) { this.anioFabricacion = anioFabricacion; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getDniPropietario() { return dniPropietario; }
    public void setDniPropietario(String dniPropietario) { this.dniPropietario = dniPropietario; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return matricula + " (" + marca + " " + modelo + ")";
    }
}

package com.joseluis.apptaller.modelo.vo;

import java.time.LocalDate;

/**
 * Clase que representa a un empleado de la plantilla del taller.
 * Almacena información personal, laboral y salarial, facilitando el transporte
 * de estos datos entre la base de datos y la interfaz de usuario.
 *
 * @author José Luis Cárdenas Barroso
 * @info Proyecto Intermodular del Grado Superior DAM
 * @institution IES Augustóbriga
 */
public class EmpleadoVO {
    private int id_empleado;
    private Integer usuario_id;
    private String dni;
    private String nombre;
    private String apellidos;
    private String telefono;
    private String email;
    private String direccion;
    private String cargo;
    private LocalDate fecha_alta;
    private LocalDate fecha_baja;
    private float salario_base;
    private boolean activo;

    public EmpleadoVO() {
        this.activo = true;  // Todo empleado nuevo nace activo por defecto
    }

    public EmpleadoVO(int id_empleado, int usuario_id, String dni, String nombre, String apellidos, String telefono, String email, String direccion, String cargo, LocalDate fecha_alta, LocalDate fecha_baja, float salario_base) {
        this.id_empleado = id_empleado;
        this.usuario_id = usuario_id;
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.cargo = cargo;
        this.fecha_alta = fecha_alta;
        this.fecha_baja = fecha_baja;
        this.salario_base = salario_base;
        this.activo = true;
    }

    // Getters y Setters
    public int getId_empleado() { return id_empleado; }

    public void setId_empleado(int id_empleado) { this.id_empleado = id_empleado; }

    public int getUsuario_id() { return usuario_id; }

    public void setUsuario_id(int usuario_id) { this.usuario_id = usuario_id; }

    public String getDni() { return dni; }

    public void setDni(String dni) { this.dni = dni; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }

    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getTelefono() { return telefono; }

    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getDireccion() { return direccion; }

    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCargo() { return cargo; }

    public void setCargo(String cargo) { this.cargo = cargo; }

    public LocalDate getFecha_alta() { return fecha_alta; }

    public void setFecha_alta(LocalDate fecha_alta) { this.fecha_alta = fecha_alta; }

    public LocalDate getFecha_baja() { return fecha_baja; }

    public void setFecha_baja(LocalDate fecha_baja) { this.fecha_baja = fecha_baja; }

    public float getSalario_base() { return salario_base; }

    public void setSalario_base(float salario_base) { this.salario_base = salario_base; }

    @Override
    public String toString() {
        return nombre + " " + apellidos; 
    }
    
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

}

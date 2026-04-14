
package com.joseluis.apptaller.modelo.vo;

import java.time.LocalDate;

/**
 * Clase que representa a un proveedor o distribuidor de recambios del taller.
 * Almacena sus datos fiscales (CIF), información de contacto y página web, 
 * facilitando el transporte de esta información hacia y desde la base de datos.
 *
 * @author José Luis Cárdenas Barroso
 * @info Proyecto Intermodular del Grado Superior DAM
 * @institution IES Augustóbriga
 */
public class ProveedorVO {
     
    private String cif;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private String contacto;
    private String web;
    private boolean activo;
    private LocalDate created_at;

    public ProveedorVO() {
        this.activo = true; // Todo proveedor nuevo nace activo por defecto
    }
    
    public ProveedorVO(String cif, String nombre, String direccion, String telefono, String email, String contacto, String web, boolean activo, LocalDate created_at) {
        this.cif = cif;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.contacto = contacto;
        this.web = web;
        this.created_at = created_at;
    }

    
    // Getters y Setters
    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDate getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDate created_at) {
        this.created_at = created_at;
    }
    
}

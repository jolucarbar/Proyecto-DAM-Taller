
package com.joseluis.apptaller.modelo.vo;

/**
 * Representa a un empleado o administrador que puede iniciar sesión en el sistema.
 */

import java.time.LocalDateTime;

public class UsuarioVO {
    private int idUsuario;
    private String username;
    private String passwordHash;
    private String rol; // "Administrador" o "Empleado"
    private boolean activo;
    private LocalDateTime ultimoAcceso;

    // Constructor vacío
    public UsuarioVO() {
    }

    // Constructor completo (sin ID, ya que es AutoIncrement)
    public UsuarioVO(String username, String passwordHash, String rol, boolean activo) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.activo = activo;
    }

    // Getters y Setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public LocalDateTime getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(LocalDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }

    @Override
    public String toString() {
        return username + " (" + rol + ")";
    }
}

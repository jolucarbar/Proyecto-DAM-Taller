package com.joseluis.apptaller.modelo.dao;

import com.joseluis.apptaller.modelo.vo.UsuarioVO;
import com.joseluis.apptaller.persistencia.Conexion;
import com.joseluis.apptaller.util.SeguridadUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;


/**
 * Gestiona la seguridad y el acceso de los usuarios al sistema.
 * Se encarga de verificar las credenciales durante el inicio de sesión (Login)
 * y de registrar automáticamente la fecha y hora del último acceso al taller.
 * 
 * @author José Luis Cárdenas Barroso
 * @info Proyecto Intermodular del Grado Superior DAM
 * @institution IES Augustóbriga
 */
public class UsuarioDAO {

    // SQL para buscar usuario activo
    private static final String SQL_LOGIN = "SELECT * FROM usuarios WHERE username = ? AND password_hash = ? AND activo = 1";
    // SQL para actualizar la fecha de último acceso
    private static final String SQL_UPDATE_ACCESO = "UPDATE usuarios SET ultimo_acceso = ? WHERE id_usuario = ?";

    
    /**
     * Intenta loguear al usuario.
     * @return UsuarioVO si es correcto, null si falla.
     */
    public UsuarioVO autenticar(String user, String pass) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        UsuarioVO usuario = null;

        try {
            conn = Conexion.getInstancia().getConnection();
            stmt = conn.prepareStatement(SQL_LOGIN);
            stmt.setString(1, user);
            stmt.setString(2, pass); // Más adelante implementaremos hash real (SHA-256/Bcrypt)

            rs = stmt.executeQuery();

            if (rs.next()) {
                usuario = new UsuarioVO();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setUsername(rs.getString("username"));
                usuario.setPasswordHash(rs.getString("password_hash"));
                usuario.setRol(rs.getString("rol"));
                usuario.setActivo(rs.getBoolean("activo"));
                
                // Conversión de SQL Timestamp a Java LocalDateTime
                Timestamp ts = rs.getTimestamp("ultimo_acceso");
                if (ts != null) {
                    usuario.setUltimoAcceso(ts.toLocalDateTime());
                }

                // Actualizar su última conexión
                registrarAcceso(usuario.getIdUsuario());
            }

        } catch (SQLException e) {
            System.err.println("Error en login: " + e.getMessage());
        } finally {
            // Cerramos recursos pero no la conexión 
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
        }

        return usuario;
    }

    
    /**
     * Actualiza el campo ultimo_acceso del usuario
     */
    private void registrarAcceso(int idUsuario) {
        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_ACCESO)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, idUsuario);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("No se pudo actualizar el último acceso: " + e.getMessage());
        }
    }
    
    
     public int insertarYObtenerId(UsuarioVO usuario) {
        int idGenerado = -1;
        String sql = "INSERT INTO usuarios (username, password_hash, rol) VALUES (?, ?, ?)";
       
        try (Connection conn = Conexion.getInstancia().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getPasswordHash());
            stmt.setString(3, usuario.getRol());
           
            int filasAfectadas = stmt.executeUpdate();
           
            if (filasAfectadas > 0) {
                try (java.sql.ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        idGenerado = rs.getInt(1);
                    }
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
        }
        return idGenerado;
    }
     
     
    public boolean validarLogin(String username, String passwordTecleada) {
        String sql = "SELECT password_hash FROM usuarios WHERE username = ?";
        try (Connection conn = Conexion.getInstancia().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hashGuardado = rs.getString("password_hash");
                    // Verificamos usando Java puro
                    return SeguridadUtil.verificarPassword(passwordTecleada, hashGuardado);
                }
            }
        } catch (Exception e) {
            System.err.println("Error en Login: " + e.getMessage());
        }
        return false;
    }
    
}
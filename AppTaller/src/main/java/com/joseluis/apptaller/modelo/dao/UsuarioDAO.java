package com.joseluis.apptaller.modelo.dao;

import com.joseluis.apptaller.modelo.vo.UsuarioVO;
import com.joseluis.apptaller.persistencia.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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
            stmt.setString(2, pass); // TODO: Más adelante implementaremos hash real (SHA-256/Bcrypt)

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

                // Tarea extra: Actualizar su última conexión
                registrarAcceso(usuario.getIdUsuario());
            }

        } catch (SQLException e) {
            System.err.println("Error en login: " + e.getMessage());
        } finally {
            // Cerramos recursos pero NO la conexión (es Singleton)
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
}
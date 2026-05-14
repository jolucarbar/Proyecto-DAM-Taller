
package com.joseluis.apptaller.modelo.dao;

import com.joseluis.apptaller.persistencia.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO encargado de gestionar la persistencia de las notas rápidas de los usuarios.
 * 
 * @author José Luis Cárdenas Barroso
 * @info Proyecto Intermodular del Grado Superior DAM
 * @institution IES Augustóbriga
 */
public class NotaDAO {
    
    // Recupera la nota guardada
    private final String SQL_SELECT_NOTA = "SELECT texto_nota FROM notas_sistema ORDER BY id_nota DESC LIMIT 1";
    
    // Guarda o actualiza una nota
    // Si el usuario no tiene nota, hace un INSERT. 
    // Si ya tiene una (porque id_usuario es clave primaria/única), hace un UPDATE.
    String SQL_UPDATE_NOTA = "INSERT INTO notas_sistema (texto_nota) VALUES (?) "
               + "ON DUPLICATE KEY UPDATE texto_nota = ?";

    /**
     * Recupera la nota guardada por un usuario específico.
     * @param idUsuario El identificador del usuario actual.
     * @return El texto de la nota, o una cadena vacía si el usuario aún no tiene notas.
     */
    public String recuperarNota() {
        String texto = "";
        
        
        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_NOTA)) {
                        
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Si encuentra la nota, extrae el texto
                    texto = rs.getString("texto_nota");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al recuperar las notas." + e.getMessage());
        }
        
        return texto;
    }

    /**
     * Guarda o actualiza la nota de un usuario.
     * Utiliza la función UPSERT (Insert On Duplicate Key Update) de MySQL.
     * @param idUsuario El identificador del usuario.
     * @param texto El contenido del bloc de notas.
     * @return true si la operación fue exitosa, false en caso de error.
     */
    public boolean guardarNota(String texto) {
                
        try (Connection conn = Conexion.getInstancia().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE_NOTA)) {
            
            // Parámetros para el INSERT
            pstmt.setString(1, texto);
            
            // Parámetro para el UPDATE (se repite el texto)
            pstmt.setString(2, texto);
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al guardar la nota del usuario " +  e.getMessage());
            return false;
        }
    }
}

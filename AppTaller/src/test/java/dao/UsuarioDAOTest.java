package com.joseluis.apptaller.modelo.dao;

import com.joseluis.apptaller.modelo.vo.UsuarioVO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UsuarioDAOTest {

    @Test
    public void testLoginAdmin() {
        System.out.println("Prueba: Login de Administrador");
        UsuarioDAO dao = new UsuarioDAO();
        
        // Probamos con los datos del INSERT que hicimos en SQL
        UsuarioVO usuario = dao.autenticar("admin", "admin123");
        
        assertNotNull(usuario, "El usuario admin debería existir y estar activo");
        assertEquals("Administrador", usuario.getRol(), "El rol debe ser Administrador");
        System.out.println("Login exitoso: " + usuario.toString());
    }
    
    @Test
    public void testLoginFallido() {
        System.out.println("Prueba: Login con contraseña incorrecta");
        UsuarioDAO dao = new UsuarioDAO();
        
        UsuarioVO usuario = dao.autenticar("admin", "clave_falsa");
        
        assertNull(usuario, "El login debería fallar y devolver null");
    }
}

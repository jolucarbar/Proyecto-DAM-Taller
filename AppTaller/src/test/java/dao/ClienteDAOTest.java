package com.joseluis.apptaller.modelo.dao;

import com.joseluis.apptaller.modelo.vo.ClienteVO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClienteDAOTest {

    @Test
    public void testCrudCliente() {
        System.out.println("Prueba: Ciclo completo CRUD de Cliente");
        ClienteDAO dao = new ClienteDAO();
        String dniPrueba = "12345678Z";
        
        // 1. Limpieza previa (por si falló un test anterior)
        dao.eliminar(dniPrueba); 

        // 2. CREATE
        ClienteVO nuevo = new ClienteVO(dniPrueba, "Pepe Pruebas", "600000000", "pepe@test.com");
        nuevo.setDireccion("Calle Test 1");
        assertTrue(dao.insertar(nuevo), "Debería insertar el cliente");

        // 3. READ
        ClienteVO encontrado = dao.buscarPorDni(dniPrueba);
        assertNotNull(encontrado, "Debería encontrar al cliente insertado");
        assertEquals("Pepe Pruebas", encontrado.getNombre());

        // 4. UPDATE
        encontrado.setNombre("Pepe Modificado");
        assertTrue(dao.modificar(encontrado), "Debería modificar el cliente");
        
        ClienteVO modificado = dao.buscarPorDni(dniPrueba);
        assertEquals("Pepe Modificado", modificado.getNombre());

        // 5. DELETE (Lógico)
        assertTrue(dao.eliminar(dniPrueba), "Debería desactivar el cliente");
        assertNull(dao.buscarPorDni(dniPrueba), "No debería encontrar clientes inactivos");
    }
}

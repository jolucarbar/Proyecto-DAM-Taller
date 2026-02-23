package com.joseluis.apptaller.modelo.vo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase FacturaVO.
 */
public class FacturaVOTest {

    private FacturaVO factura;

    @BeforeEach
    public void setUp() {
        factura = new FacturaVO();
    }

    @Test
    public void testSetAndGetNumeroFactura() {
        String numeroEsperado = "FACT-2026-0001";
        factura.setNumeroFactura(numeroEsperado);
        assertEquals(numeroEsperado, factura.getNumeroFactura(), "El número de factura no coincide");
    }

    @Test
    public void testSetAndGetFechas() {
        LocalDate fechaEmision = LocalDate.now();
        LocalDate fechaVencimiento = fechaEmision.plusDays(30);

        factura.setFechaEmision(fechaEmision);
        factura.setFechaVencimiento(fechaVencimiento);

        assertEquals(fechaEmision, factura.getFechaEmision(), "La fecha de emisión es incorrecta");
        assertEquals(fechaVencimiento, factura.getFechaVencimiento(), "La fecha de vencimiento es incorrecta");
    }

    @Test
    public void testCalculosImportes() {
        double baseImponible = 100.0;
        double iva = 21.0;
        double totalCobrado = 121.0;

        factura.setBaseImponible(baseImponible);
        factura.setIva(iva);
        factura.setTotalCobrado(totalCobrado);

        assertEquals(100.0, factura.getBaseImponible(), 0.01, "Fallo en la base imponible");
        assertEquals(21.0, factura.getIva(), 0.01, "Fallo en el IVA");
        assertEquals(121.0, factura.getTotalCobrado(), 0.01, "Fallo en el total cobrado");
    }
}
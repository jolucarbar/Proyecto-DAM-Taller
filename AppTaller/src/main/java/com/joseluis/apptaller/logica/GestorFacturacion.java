package com.joseluis.apptaller.logica;

import com.joseluis.apptaller.modelo.dao.FacturaDAO;
import com.joseluis.apptaller.modelo.dao.ReparacionDAO;
import com.joseluis.apptaller.modelo.vo.DetalleFactura;
import com.joseluis.apptaller.modelo.vo.FacturaVO;
import java.time.LocalDate;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 * Gestiona la lógica de negocio para la emisión de facturas.
 * Centraliza los cálculos para no sobrecargar el DAO ni la Vista.
 * 
 * @author José Luis Cárdenas Barroso
 * @info Proyecto Intermodular del Grado Superior DAM
 * @institution IES Augustóbriga
 */
public class GestorFacturacion {
    
    private final FacturaDAO facturaDAO;
    private static final double PORCENTAJE_IVA = 21.0;

    public GestorFacturacion() {
        this.facturaDAO = new FacturaDAO();
    }

    /**
     * Construye un objeto FacturaVO a partir de una Reparación finalizada.
     */
    public FacturaVO prepararFacturaDesdeReparacion(int idReparacion, int idPresupuesto, String clienteDni, String bastidor, double totalManoObra, double totalProductos) {
        
        FacturaVO factura = new FacturaVO();
        factura.setNumeroFactura(facturaDAO.generarSiguienteNumeroFactura());
        factura.setIdReparacion(idReparacion);
        factura.setIdPresupuesto(idPresupuesto > 0 ? idPresupuesto : 0);
        factura.setClienteDni(clienteDni);
        factura.setVehiculoBastidor(bastidor);
        
        // Fechas por defecto
        factura.setFechaEmision(LocalDate.now());
        factura.setFechaVencimiento(LocalDate.now().plusDays(15)); // Política estándar de 15 días
        
        // Cálculos económicos
        double baseImponible = totalManoObra + totalProductos;
        double importeIva = baseImponible * (PORCENTAJE_IVA / 100);
        double totalCobrado = baseImponible + importeIva;
        
        factura.setBaseImponible(redondear(baseImponible));
        factura.setIva(PORCENTAJE_IVA);
        factura.setTotalCobrado(redondear(totalCobrado));
        
        factura.setEstado("PENDIENTE"); // Nace como pendiente hasta que se cobre
        
        return factura;
    }
    
    
    // Utilidad de redondeo a 2 decimales
    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
    
    
    /**
     * Ejerce como Controlador/Servicio para guardar la factura.
     */
    public boolean registrarNuevaFactura(FacturaVO factura) {
        if (factura.getMetodoPago() == null || factura.getMetodoPago().isEmpty()) {
            return false;
        }
        
        // Mandamos al Modelo (DAO)
        return facturaDAO.insertar(factura);
    }
    
    
    /**
     * Genera y muestra el PDF de la factura usando JasperReports.
     * @param factura El objeto con los datos de cabecera.
     * @param detalles La lista de recambios y mano de obra (para la tabla).
     */
    public void generarInformePDF(FacturaVO factura, List<DetalleFactura> detalles) {
        try {
            // Cargar el reporte compilado (.jasper) y el logo desde la carpeta resources
            InputStream reportStream = getClass().getResourceAsStream("/informes/FacturaTaller.jasper");
            InputStream logoStream = getClass().getResourceAsStream("/images/logo_apptaller_small.png");

            if (reportStream == null) {
                System.err.println("Error crítico: No se encuentra el archivo FacturaTaller.jasper en resources/reportes/");
                return;
            }

            // Mapear los Parámetros (Los $P{} de la cabecera y totales)
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PARAM_LOGO", logoStream); 
            parametros.put("PARAM_NUM_FACTURA", factura.getNumeroFactura());
            
            // Validamos que el nombre no sea nulo; si lo es, ponemos "Desconocido"
            String nombreCliente = (factura.getClienteNombre() != null) ? factura.getClienteNombre() : "Cliente Desconocido";
            parametros.put("PARAM_CLIENTE", nombreCliente); 
            
            // Validamos también el DNI por si acaso
            String dniCliente = (factura.getClienteDni() != null) ? factura.getClienteDni() : "Sin DNI";
            parametros.put("PARAM_DNI", dniCliente);
            
            // Calculamos la base imponible dividiendo el total entre 1.21 (IVA 21%)
            double baseImponible = factura.getTotalCobrado() / 1.21;
            parametros.put("PARAM_BASE_IMPONIBLE", baseImponible);
            parametros.put("PARAM_TOTAL", factura.getTotalCobrado());

            // Fuente de datos para la tabla (Los $F{} de la banda Detail)
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(detalles);

            // Llenar el reporte (Fusionar diseño + datos)
            JasperPrint jp = JasperFillManager.fillReport(reportStream, parametros, dataSource);

            // Mostrar el visor de Jasper (Permite ver, imprimir directamente o guardar como PDF)
            // El 'false' evita que al cerrar el PDF se cierre también la aplicación entera.
            JasperViewer.viewReport(jp, false);

        } catch (JRException e) {
            System.err.println("Error de JasperReports al generar la factura: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    /**
     * Recopila la mano de obra y los materiales de una reparación y los convierte
     * en líneas de detalle para imprimir la factura.
     */
    public java.util.List<DetalleFactura> obtenerDetallesParaFactura(int idReparacion) {
        List<DetalleFactura> detalles = new ArrayList<>();
        ReparacionDAO repDao = new ReparacionDAO();
        
        // Añadir líneas de Mano de Obra
        for (Object[] mo : repDao.obtenerTrabajosRealizados(idReparacion)) {
            String desc = "Mano de Obra: " + mo[0].toString();
            double cantidad = Double.parseDouble(mo[2].toString());
            // Limpiamos el " €" y cambiamos comas por puntos para parsear bien
            double precio = Double.parseDouble(mo[3].toString().replace(" €", "").replace(",", ".").trim());
            double subtotal = Double.parseDouble(mo[4].toString().replace(" €", "").replace(",", ".").trim());
            
            detalles.add(new DetalleFactura(desc, cantidad, precio, subtotal));
        }
        
        // Añadir líneas de Piezas / Materiales
        for (Object[] prod : repDao.obtenerPiezasUtilizadas(idReparacion)) {
            String desc = "Recambio: " + prod[0].toString() + " - " + prod[1].toString();
            double cantidad = Double.parseDouble(prod[2].toString());
            double precio = Double.parseDouble(prod[3].toString().replace(" €", "").replace(",", ".").trim());
            double subtotal = Double.parseDouble(prod[4].toString().replace(" €", "").replace(",", ".").trim());
            
            detalles.add(new DetalleFactura(desc, cantidad, precio, subtotal));
        }
        
        return detalles;
    }
    
    
}
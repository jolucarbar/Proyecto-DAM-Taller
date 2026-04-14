package com.joseluis.apptaller.util;

import com.joseluis.apptaller.persistencia.Conexion;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

/**
 * Clase de utilidad encargada de la generación y visualización de informes.
 * Utiliza la librería JasperReports para compilar plantillas (.jrxml), inyectar
 * parámetros (como logos o identificadores) y mostrar documentos oficiales 
 * listos para imprimir o exportar a PDF.
 *
 * @author José Luis Cárdenas Barroso
 * @info Proyecto Intermodular del Grado Superior DAM
 * @institution IES Augustóbriga
 */
public class GeneradorInformes {

    public static void mostrarInformePresupuesto(int idPresupuesto) {
        try {
            // Fuerzo a JASPER a ignorar la validacíón xml para evitar conflictos de versión
            System.setProperty("net.sf.jasperreports.xml.validating", "false");


            // Cargamos el archivo fuente (.jrxml)
            InputStream reporteStream = GeneradorInformes.class.getResourceAsStream("/informes/Presupuesto.jrxml");
            
            if (reporteStream == null) {
                javax.swing.JOptionPane.showMessageDialog(null, "No se encontró el archivo Presupuesto.jrxml en la ruta especificada.");
                return;
            }

            // Compilamos en tiempo de ejecución
            JasperReport report = JasperCompileManager.compileReport(reporteStream);

            // Obtener la ruta del LOGO
            java.net.URL logoUrl = GeneradorInformes.class.getResource("/images/logo_apptaller_small.png");
            String rutaLogo = (logoUrl != null) ? logoUrl.getPath() : "";

            // Parámetros
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("ID_PRESUPUESTO", idPresupuesto);
            parametros.put("LOGO_PATH", rutaLogo);

            // Llenar el informe
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    report, 
                    parametros, 
                    Conexion.getInstancia().getConnection()
            );

            // Mostrar el visor
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setTitle("Presupuesto Oficial - App Mi Taller");
            viewer.setVisible(true);

        } catch (JRException e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Error de Jasper: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
}
package com.joseluis.apptaller.util;

import com.joseluis.apptaller.persistencia.Conexion;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
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
    
    
    public static void mostrarInformeHistorialVehiculo(String bastidor) {
        try {
            System.setProperty("net.sf.jasperreports.xml.validating", "false");
            InputStream reporteStream = GeneradorInformes.class.getResourceAsStream("/informes/HistorialVehiculo.jrxml");
           
            if (reporteStream == null) {
                JOptionPane.showMessageDialog(null, "Atención: Debe crear el diseño HistorialVehiculo.jrxml en JasperSoft Studio.");
                return;
            }
           
            JasperReport report = JasperCompileManager.compileReport(reporteStream);
            URL logoUrl = GeneradorInformes.class.getResource("/images/logo_apptaller_small.png");
            String rutaLogo = (logoUrl != null) ? logoUrl.getPath() : "";

            Map<String, Object> parametros = new HashMap<>();
            // Pasamos el bastidor como parámetro para que el .jrxml filtre la consulta internamente
            parametros.put("PARAM_BASTIDOR", bastidor);
            parametros.put("LOGO_PATH", rutaLogo);

            JasperPrint jp = JasperFillManager.fillReport(report, parametros, Conexion.getInstancia().getConnection());

            // Usamos nuestro JDialog personalizado para respetar la modalidad (Z-Index)
            net.sf.jasperreports.swing.JRViewer visorPanel = new net.sf.jasperreports.swing.JRViewer(jp);
            javax.swing.JDialog dialogVisor = new javax.swing.JDialog();
            dialogVisor.setModal(true);
            dialogVisor.setTitle("Historial de Vehículo - App Mi Taller");
            dialogVisor.setSize(950, 750);
            dialogVisor.setLocationRelativeTo(null);
            dialogVisor.setDefaultCloseOperation(javax.swing.JDialog.DISPOSE_ON_CLOSE);
            dialogVisor.getContentPane().add(visorPanel);
            dialogVisor.setVisible(true);

        } catch (JRException e) {
            JOptionPane.showMessageDialog(null, "Error al generar informe: " + e.getMessage());
        }
    }
    
}
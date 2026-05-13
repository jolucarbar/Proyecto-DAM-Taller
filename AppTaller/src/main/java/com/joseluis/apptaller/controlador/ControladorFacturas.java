
package com.joseluis.apptaller.controlador;

import com.joseluis.apptaller.logica.GestorFacturacion;
import com.joseluis.apptaller.modelo.dao.FacturaDAO;
import com.joseluis.apptaller.modelo.vo.FacturaVO;
import com.joseluis.apptaller.vista.ventanas.VentanaPrincipal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author José Luis Cárdenas Barroso
 * @info Proyecto Intermodular del Grado Superior DAM
 * @institution IES Augustóbriga
 */
public class ControladorFacturas implements ActionListener {
    private final VentanaPrincipal vista;
    private final FacturaDAO modeloDAO;
    private DefaultTableModel modeloTabla;
    
    public ControladorFacturas(VentanaPrincipal vista, FacturaDAO modeloDAO) {
        this.vista = vista;
        this.modeloDAO = modeloDAO;
        
        if (this.vista != null) {
            initListeners();
            initTabla();
            cargarTablaFacturas();
        }
    }
    
    
    
    private void initListeners() {
        vista.getBtnBuscarFactura().addActionListener(this);
        vista.getBtnRecargarFactura().addActionListener(this);
        vista.getBtnVerFactura().addActionListener(this);
        vista.getBtnRegistrarPago().addActionListener(this);
    }

    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnBuscarFactura()) {
            buscarFactura();
        } else if (e.getSource() == vista.getBtnRecargarFactura()) {
            cargarTablaFacturas();
        } else if (e.getSource() == vista.getBtnVerFactura()) {
            verFactura();
        } else if (e.getSource() == vista.getBtnRegistrarPago()) {
            registrarPago();
        }
    }

    
    
    private void initTabla() {
        // Configuramos el modelo para que las celdas no sean editables directamente haciendo doble clic
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        String[] columnas = {"ID", "Nº Factura", "Fecha", "Cliente", "Matrícula", "Total €", "Estado"};
        modeloTabla.setColumnIdentifiers(columnas);
        vista.getTblFacturas().setModel(modeloTabla);
        vista.getTblFacturas().setRowHeight(30);
        
        // Ocultar la columna ID (índice 0) visualmente pero mantiene el dato
        vista.getTblFacturas().getColumnModel().getColumn(0).setMinWidth(0);
        vista.getTblFacturas().getColumnModel().getColumn(0).setMaxWidth(0);
        vista.getTblFacturas().getColumnModel().getColumn(0).setWidth(0);
        
    }
    
    
     public void cargarTablaFacturas() {
        // Limpiamos filas antiguas
        modeloTabla.setRowCount(0);

        // Obtenemos la lista de la BD
        List<FacturaVO> lista = modeloDAO.listar();

        // Rellenamos la tabla
        for (FacturaVO f : lista) {
            Object[] fila = new Object[7];
            fila[0] = f.getIdFactura();
            fila[1] = f.getNumeroFactura();
            fila[2] = f.getFechaEmision();
            // Formato "DNI - Nombre del Cliente"
            String textoCliente = f.getClienteDni();
            if (f.getClienteNombre() != null && !f.getClienteNombre().trim().isEmpty() && !f.getClienteNombre().equals("Desconocido")) {
                textoCliente = f.getClienteDni() + " - " + f.getClienteNombre();
            }
            fila[3] = textoCliente;
            fila[4] = (f.getVehiculoMatricula() != null) ? f.getVehiculoMatricula() : f.getVehiculoBastidor(); // Mostraría bastidor en caso de fallo
            
            // Formateo de moneda
            fila[5] = String.format("%.2f €", f.getTotalCobrado());
            fila[6] = f.getEstado();

            modeloTabla.addRow(fila);
        }
    }
    
    
    private void buscarFactura() {
        String busqueda = vista.getTxtBuscarFactura().getText().trim();
        String placeholder = "Buscar factura..."; 
        if (busqueda.isEmpty() || busqueda.equals(placeholder)) {
            JOptionPane.showMessageDialog(vista,
                "Por favor, introduzca un término antes de buscar.",
                "Aviso de Búsqueda",
                JOptionPane.WARNING_MESSAGE);
            vista.getTxtBuscarFactura().requestFocus();
            return;
        }
        modeloTabla.setRowCount(0); // Limpiamos la tabla antes de rellenar
        List<FacturaVO> lista = modeloDAO.buscarFactura(busqueda);
        
        if (lista != null && !lista.isEmpty()) {
            for (FacturaVO f : lista) {
                Object[] fila = new Object[7];
                fila[0] = f.getIdFactura();
                fila[1] = f.getNumeroFactura();
                fila[2] = f.getFechaEmision();
                // Formato "DNI - Nombre del Cliente"
                String textoCliente = f.getClienteDni();
                if (f.getClienteNombre() != null && !f.getClienteNombre().trim().isEmpty() && !f.getClienteNombre().equals("Desconocido")) {
                    textoCliente = f.getClienteDni() + " - " + f.getClienteNombre();
                }
                fila[3] = textoCliente;
                fila[4] = (f.getVehiculoMatricula() != null) ? f.getVehiculoMatricula() : f.getVehiculoBastidor(); // Mostraría bastidor en caso de fallo
                // Formateo de moneda
                fila[5] = String.format("%.2f €", f.getTotalCobrado());
                fila[6] = f.getEstado();

                modeloTabla.addRow(fila);
            }
            vista.getTxtBuscarFactura().setText("");
        } else {
            JOptionPane.showMessageDialog(vista, "No se han encontrado facturas con el parámetro " + busqueda, "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            cargarTablaFacturas();
            vista.getTxtBuscarFactura().setText("");
        }
        
        
    }

    private void verFactura() {
        int fila = vista.getTblFacturas().getSelectedRow();
        if (fila == -1) {
            //JOptionPane.showMessageDialog(this, "Seleccione una factura de la lista para visualizarla.");
            return;
        }

        try {
            // Extraemos el ID de la tabla
            int idFactura = Integer.parseInt(vista.getTblFacturas().getValueAt(fila, 0).toString());

            GestorFacturacion gestor = new GestorFacturacion();
            gestor.visualizarFacturaGenerica(idFactura);

        } catch (Exception e) {
            e.printStackTrace();
            //JOptionPane.showMessageDialog(this, "Error al intentar visualizar la factura: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarPago() {
        // Verificamos que hay una fila seleccionada
        int fila = vista.getTblFacturas().getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista,
                    "Por favor, seleccione una factura de la lista para registrar su pago.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Extraemos el ID oculto y el estado actual de la tabla
        int idFactura = Integer.parseInt(vista.getTblFacturas().getValueAt(fila, 0).toString());
        String numFactura = vista.getTblFacturas().getValueAt(fila, 1).toString();
        String estadoActual = vista.getTblFacturas().getValueAt(fila, 6).toString(); // Columna 6 es el Estado

        // Validamos que no esté pagada ya
        if (estadoActual.equalsIgnoreCase("PAGADA")) {
            JOptionPane.showMessageDialog(vista,
                    "Esta factura ya figura como PAGADA en el sistema.",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Pedimos confirmación de seguridad al usuario
        int confirmacion = JOptionPane.showConfirmDialog(vista,
                "¿Confirmas la recepción del pago para la factura " + numFactura + "?\nEl estado cambiará a PAGADA.",
                "Confirmar Registro de Pago",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        // Si dice que SÍ, actualizamos la base de datos
        if (confirmacion == JOptionPane.YES_OPTION) {
            //FacturaDAO fDao = new FacturaDAO();

            if (modeloDAO.actualizarEstado(idFactura, "PAGADA")) {
                JOptionPane.showMessageDialog(vista, "¡Pago registrado correctamente!");
                // Recargamos la tabla para que se actualice visualmente
                cargarTablaFacturas();
            } else {
                JOptionPane.showMessageDialog(vista,
                        "Error al registrar el pago en la base de datos.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
}

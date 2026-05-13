
package com.joseluis.apptaller.controlador;

import com.joseluis.apptaller.modelo.dao.PresupuestoDAO;
import com.joseluis.apptaller.util.GeneradorInformes;
import com.joseluis.apptaller.vista.dialogos.DialogDetallesPresupuesto;
import com.joseluis.apptaller.vista.dialogos.DialogNuevaReparacion;
import com.joseluis.apptaller.vista.dialogos.DialogNuevoPresupuesto;
import com.joseluis.apptaller.vista.ventanas.VentanaPrincipal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;



/**
 * Controlador principal encargado de gestionar el módulo de Presupuestos en la Ventana Principal:
 * lista los presupuestos históricos, aplica filtros de búsqueda y actúa como
 * puente o "lanzador" de la interfaz gráfica al pulsar el botón de Nuevo Presupuesto.
 *
 * @author José Luis Cárdenas Barroso
 * @info Proyecto Intermodular del Grado Superior DAM
 * @institution IES Augustóbriga
 */
public class ControladorPresupuestos implements ActionListener {
    private final VentanaPrincipal vista;
    private final PresupuestoDAO modeloDAO;
    private DefaultTableModel modeloTabla;
    
    public ControladorPresupuestos (VentanaPrincipal vista, PresupuestoDAO modeloDAO) {
        this.vista = vista;
        this.modeloDAO = modeloDAO;
        
        if (this.vista != null) {
            initListeners();
            cargarTablaPresupuestos();
        }
    }
    

    private void initListeners() {
        vista.getBtnNuevoPresupuesto().addActionListener(this);
        vista.getBtnBuscarPresupuesto().addActionListener(this);
        vista.getBtnEstadoPresupuesto().addActionListener(this);
        vista.getBtnEliminarPresupuesto().addActionListener(this);
        vista.getBtnImprimirPresupuesto().addActionListener(this);
        vista.getBtnDetallesPresupuesto().addActionListener(this);
        vista.getBtnCrearOrdenReparacion().addActionListener(this);
        vista.getBtnRecargarPresupuestos().addActionListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnNuevoPresupuesto()) {
            abrirDialogNuevoPresupuesto();
        } else if (e.getSource() == vista.getBtnBuscarPresupuesto()) {
            buscarPresupuesto();
        } else if (e.getSource() == vista.getBtnEstadoPresupuesto()) {
            cambiarEstado();
        } 
         else if (e.getSource() == vista.getBtnEliminarPresupuesto()) {
            eliminarPresupuesto();
        }
         else if (e.getSource() == vista.getBtnImprimirPresupuesto()) {
            imprimirPresupuesto();
        } 
         else if (e.getSource() == vista.getBtnDetallesPresupuesto()) {
            verDetalles();
        } 
         else if (e.getSource() == vista.getBtnCrearOrdenReparacion()) {
            crearOrdenReparacion();
        } else if (e.getSource() == vista.getBtnRecargarPresupuestos()) {
            
        }
    }

    
     /**
     * Consulta la BD y recarga la tabla de presupuestos.
     */
    public void cargarTablaPresupuestos() {
        // Obtenemos el modelo de la tabla y lo limpiamos
        modeloTabla = (DefaultTableModel) vista.getTblPresupuestos().getModel();
        modeloTabla.setRowCount(0); // Borra las filas vacías que trae NetBeans por defecto

        List<Object[]> datos = modeloDAO.listarParaTabla();

        // Añadimos los datos fila a fila
        for (Object[] fila : datos) {
            modeloTabla.addRow(fila);
        }
    }
    
    
    private void abrirDialogNuevoPresupuesto() {
        DialogNuevoPresupuesto dialog = new DialogNuevoPresupuesto(vista, true);
        ControladorNuevoPresupuesto ctrlDialog = new ControladorNuevoPresupuesto(dialog, modeloDAO);
        dialog.setControlador(ctrlDialog);
        ctrlDialog.inicializar();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        cargarTablaPresupuestos(); // Refrescar tras cerrar
    }

    
    private void buscarPresupuesto(){
        String busqueda = vista.getTxtBuscarPresupuesto().getText().trim();
        String placeholder = "Buscar presupuesto..."; 
        if (busqueda.isEmpty() || busqueda.equals(placeholder)) {
            JOptionPane.showMessageDialog(vista,
                "Por favor, introduzca un término antes de buscar.",
                "Aviso de Búsqueda",
                JOptionPane.WARNING_MESSAGE);
            vista.getTxtBuscarCliente().requestFocus();
            return;
        }
        DefaultTableModel modelo = (DefaultTableModel) vista.getTblPresupuestos().getModel();
        modelo.setRowCount(0); 

        List<Object[]> lista = modeloDAO.buscarPresupuesto(busqueda);
        if(lista != null && !lista.isEmpty()){
            for(Object[] fila : lista) modelo.addRow(fila);
        } else {
            JOptionPane.showMessageDialog(vista, "No se encontraron presupuestos para: " + busqueda, "Aviso", JOptionPane.INFORMATION_MESSAGE);
            cargarTablaPresupuestos(); // Recarga todo si no hay resultados
            //vista.getTxtBuscarPresupuesto().setText("");
        }
        vista.getTxtBuscarPresupuesto().setText("");
    }
    
     
    private void cambiarEstado() {
       
        // Verificamos si hay una fila seleccionada
        int filaSeleccionada = vista.getTblPresupuestos().getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null,
                    "Por favor, selecciona un presupuesto de la tabla.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtenemos el ID del presupuesto (Quitando el "PRE-")
        String idTexto = vista.getTblPresupuestos().getValueAt(filaSeleccionada, 0).toString();
        int idPresupuesto = Integer.parseInt(idTexto.replace("PRE-", "").trim());

        // Mostramos opciones de estado 
        String[] estados = {"PENDIENTE", "APROBADO", "RECHAZADO"};

        // Obtenemos el estado actual para que salga preseleccionado
        String estadoActual = vista.getTblPresupuestos().getValueAt(filaSeleccionada, 5).toString();

        String nuevoEstado = (String) JOptionPane.showInputDialog(
                vista,
                "Selecciona el nuevo estado para el presupuesto " + idTexto + ":",
                "Actualizar Estado",
                JOptionPane.QUESTION_MESSAGE,
                null,
                estados,
                estadoActual
        );

        // Ejecutamos el cambio si el usuario no canceló y el estado es diferente
        if (nuevoEstado != null && !nuevoEstado.equals(estadoActual)) {
            //PresupuestoDAO dao = new PresupuestoDAO();

            if (modeloDAO.actualizarEstado(idPresupuesto, nuevoEstado)) {
                JOptionPane.showMessageDialog(vista, "Estado actualizado correctamente.");
                // Recargamos la tabla para que el cambio se vea reflejado inmediatamente
                cargarTablaPresupuestos();
            } else {
                JOptionPane.showMessageDialog(vista, "Error al actualizar el estado en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } 
        
    }  

    private void eliminarPresupuesto() {
        
        // Verificamos si hay una fila seleccionada en la tabla
        int filaSeleccionada = vista.getTblPresupuestos().getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista,
                    "Por favor, selecciona un presupuesto de la tabla para eliminarlo.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Extraemos el ID de la fila seleccionada (Quitamos el prefijo "PRE-")
        String idTexto = vista.getTblPresupuestos().getValueAt(filaSeleccionada, 0).toString();
        int idPresupuesto = Integer.parseInt(idTexto.replace("PRE-", ""));

        // Confirmación de seguridad 
        int confirmacion = JOptionPane.showConfirmDialog(vista,
                "¿Estás seguro de que deseas eliminar permanentemente el presupuesto " + idTexto + "?\nEsta acción no se puede deshacer.",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            // Llamamos al DAO para eliminar
            boolean exito = modeloDAO.eliminarPresupuesto(idPresupuesto);

            if (exito) {
                JOptionPane.showMessageDialog(vista, "Presupuesto eliminado con éxito.");
                // Recargamos la tabla para que desaparezca visualmente
                cargarTablaPresupuestos();
            } else {
                JOptionPane.showMessageDialog(vista, "Error al eliminar el presupuesto.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        } 
    }

    private void imprimirPresupuesto() {
        
        int fila = vista.getTblPresupuestos().getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un presupuesto de la tabla para imprimir.");
            return;
        }

        try {
            // Extraemos el ID (como hice con los detalles)
            String idTexto = vista.getTblPresupuestos().getValueAt(fila, 0).toString();
            int idPresupuesto = Integer.parseInt(idTexto.replace("PRE-", "").trim());

            // Llamamos a la utilidad
            GeneradorInformes.mostrarInformePresupuesto(idPresupuesto);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error: " + e.getMessage());
        }
        
    }

    private void verDetalles() {
        
        // Comprobamos que hay una fila seleccionada
        int filaSeleccionada = vista.getTblPresupuestos().getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista,
                    "Por favor, selecciona un presupuesto de la tabla para ver sus detalles.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Extraemos el ID (Quitando el "PRE-")
            String idTexto = vista.getTblPresupuestos().getValueAt(filaSeleccionada, 0).toString();
            int idPresupuesto = Integer.parseInt(idTexto.replace("PRE-", "").trim());

            // Abrimos el diálogo 
            DialogDetallesPresupuesto dialog = new DialogDetallesPresupuesto(vista, true);

            // Cargamos los datos y mostramos
            dialog.cargarDatos(idPresupuesto);
            dialog.setLocationRelativeTo(vista);
            dialog.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista,
                    "Error al abrir los detalles: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } 
    }

    private void crearOrdenReparacion() {
        
        // Comprobamos que hay una fila seleccionada
        int filaSeleccionada = vista.getTblPresupuestos().getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista,
                    "Por favor, selecciona un presupuesto para crear la orden de reparación.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Comprobamos que el presupuesto esté APROBADO
        String estado = vista.getTblPresupuestos().getValueAt(filaSeleccionada, 5).toString();
        if (!estado.equalsIgnoreCase("APROBADO")) {
            JOptionPane.showMessageDialog(vista,
                    "Solo puedes iniciar reparaciones de presupuestos que estén en estado APROBADO.",
                    "Operación no permitida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Extraemos el ID del presupuesto (Quitando el "PRE-")
            String idTexto = vista.getTblPresupuestos().getValueAt(filaSeleccionada, 0).toString();
            int idPresupuesto = Integer.parseInt(idTexto.replace("PRE-", "").trim());

            // Abrimos el dialog de Nueva Reparación pasándole el ID
            DialogNuevaReparacion dialog = new DialogNuevaReparacion(vista, true, idPresupuesto);

            dialog.setVisible(true);

            // Tras crear la orden, salta automáticamente a la pestaña de reparaciones:
            // btnReparaciones.doClick();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista,
                    "Error al iniciar la reparación: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } 
    }
    
  
}


package com.joseluis.apptaller.controlador;

import com.joseluis.apptaller.modelo.dao.EmpleadoDAO;
import com.joseluis.apptaller.modelo.vo.EmpleadoVO;
import com.joseluis.apptaller.vista.dialogos.DialogNuevoEmpleado;
import com.joseluis.apptaller.vista.ventanas.VentanaPrincipal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author joseluis
 */
public class ControladorEmpleados implements ActionListener{

    private final VentanaPrincipal vista;
    private final EmpleadoDAO modeloDAO;
    private DefaultTableModel modeloTabla;
    
    public ControladorEmpleados(VentanaPrincipal vista, EmpleadoDAO modeloDAO) {
        this.vista = vista;
        this.modeloDAO = modeloDAO;
        initTabla();
        initListeners();
        cargarEmpleados(); // Carga los empleados al arrancar
    }
    
    
    private void initTabla() {
        // Configuramos el modelo para que las celdas no sean editables directamente haciendo doble clic
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Las columnas definidas en el GUI
        String[] columnas = {"DNI", "Nombre", "Apellidos", "Teléfono","Email", "Direccion", "Cargo", "Salario Base"};
        modeloTabla.setColumnIdentifiers(columnas);
        vista.getTblEmpleado().setModel(modeloTabla);
        vista.getTblEmpleado().setRowHeight(30);
    }
    
    
    
    private void initListeners() {
        // Escuchamos los botones
        vista.getBtnNuevoEmpleado().addActionListener(this);
        vista.getBtnEliminarEmpleado().addActionListener(this);
        vista.getBtnBuscarEmpleado().addActionListener(this);
        // Escuchamos el doble clic en la tabla para editar
        vista.getTblEmpleado().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarEmpleadoSeleccionado();
                }
            }
        });
    }
    
    
    private void cargarEmpleados() {
        modeloTabla.setRowCount(0); // Limpiar tabla
        List<EmpleadoVO> lista = modeloDAO.listar();
        
        for  (EmpleadoVO e: lista) {
            Object[] fila = new Object[8];
            fila[0] = e.getDni();
            fila[1] = e.getNombre();
            fila[2] = e.getApellidos();
            fila[3] = e.getTelefono();
            fila[4] = e.getEmail();
            fila[5] = e.getDireccion();
            fila[6] = e.getCargo();
            fila[7] = e.getSalario_base() + " €";
            modeloTabla.addRow(fila);
        }
        vista.getTblEmpleado().revalidate();
        vista.getTblEmpleado().repaint();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnNuevoEmpleado()) {
            abrirDialogoNuevoEmpleado();
        } else if (e.getSource() == vista.getBtnEliminarEmpleado()) {
            eliminarEmpleadoSeleccionado();
        } else if (e.getSource() == vista.getBtnBuscarVehiculo()) {
            JOptionPane.showMessageDialog(vista, "Búsqueda en construcción...", "Info",
            JOptionPane.INFORMATION_MESSAGE);
        }
    }

    
    private void abrirDialogoNuevoEmpleado() {
        // Abrimos el modal vacío (Alta)
        DialogNuevoEmpleado dialog = new DialogNuevoEmpleado(vista, true);
        dialog.setVisible(true);
       
        // Si el usuario pulsó Guardar
        if (dialog.isGuardado()) {
            EmpleadoVO nuevoEmpleado = dialog.getEmpleado();
           
            if (modeloDAO.insertar(nuevoEmpleado)) {
                JOptionPane.showMessageDialog(vista, "Empleado registrado con éxito.");
                cargarEmpleados();
            } else {
                JOptionPane.showMessageDialog(vista, "Error al guardar el empleado. Revise si el DNI ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void editarEmpleadoSeleccionado() {
        int filaSelec = vista.getTblEmpleado().getSelectedRow();
       
        if (filaSelec != -1) {
            String dni = (String) modeloTabla.getValueAt(filaSelec, 0); // El DNI está en la columna 0
            EmpleadoVO empleadoAEditar = modeloDAO.buscarPorDni(dni);
           
            if (empleadoAEditar != null) {
                // Abrimos el modal pasándole los datos (Edición)
                DialogNuevoEmpleado dialog = new DialogNuevoEmpleado(vista, true, empleadoAEditar);
                dialog.setVisible(true);
               
                if (dialog.isGuardado()) {
                    EmpleadoVO empleadoModificado = dialog.getEmpleado();
                   
                    if (modeloDAO.modificar(empleadoModificado)) {
                        JOptionPane.showMessageDialog(vista, "Empleado actualizado con éxito.");
                        cargarEmpleados();
                    } else {
                        JOptionPane.showMessageDialog(vista, "Error al actualizar el empleado.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }
    
    private void eliminarEmpleadoSeleccionado() {
         int filaSelec = vista.getTblEmpleado().getSelectedRow();
        if (filaSelec != -1) {
            String dni = (String) modeloTabla.getValueAt(filaSelec, 0);
            String nombre = (String) modeloTabla.getValueAt(filaSelec, 1);
           
            int confirmacion = JOptionPane.showConfirmDialog(vista, "¿Seguro que desea dar de baja al empleado " + nombre + " (" + dni + ")?", "Confirmar baja", JOptionPane.YES_NO_OPTION);
           
            if (confirmacion == JOptionPane.YES_OPTION) {
                if (modeloDAO.eliminar(dni)) {
                    JOptionPane.showMessageDialog(vista, "Empleado dado de baja correctamente.");
                    cargarEmpleados();
                } else {
                    JOptionPane.showMessageDialog(vista, "Error al dar de baja al empleado.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(vista, "Por favor, seleccione un empleado de la tabla primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    
    }
    
}

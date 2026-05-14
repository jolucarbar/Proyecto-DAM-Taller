
package com.joseluis.apptaller.controlador;

import com.joseluis.apptaller.modelo.dao.EmpleadoDAO;
import com.joseluis.apptaller.modelo.dao.UsuarioDAO;
import com.joseluis.apptaller.modelo.vo.EmpleadoVO;
import com.joseluis.apptaller.modelo.vo.UsuarioVO;
import com.joseluis.apptaller.util.SeguridadUtil;
import com.joseluis.apptaller.vista.dialogos.DialogNuevoEmpleado;
import com.joseluis.apptaller.vista.ventanas.VentanaPrincipal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Clase que gestiona la lógica de negocio de los Empleados.
 * 
 * @author José Luis Cárdenas Barroso
 * @info Proyecto Intermodular del Grado Superior DAM
 * @institution IES Augustóbriga
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
        vista.getBtnRecargarEmpleados().addActionListener(this);
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
        } else if (e.getSource() == vista.getBtnBuscarEmpleado()) {
            buscarEmpleado();
        } else if (e.getSource() == vista.getBtnRecargarEmpleados()) {
            cargarEmpleados();
        }
    }

    
    private void abrirDialogoNuevoEmpleado() {
        // 1. Abrimos el diálogo 
        DialogNuevoEmpleado dialog = new DialogNuevoEmpleado(vista, true);
        dialog.setVisible(true);

        // 2. Si el usuario pulsó "Guardar"
        if (dialog.isGuardado()) {
            try {
                // Extraemos los datos del diálogo (Empleado, Usuario y Password)
                EmpleadoVO nuevoEmp = dialog.getEmpleado();
                String user = dialog.getTxtUsuario().getText();
                String pass = new String(dialog.getTxtPassword().getText());
                
                String passwordSeguro = SeguridadUtil.generarHash(pass);

                
                // A. Creamos el Usuario primero
                UsuarioVO uVO = new UsuarioVO();
                uVO.setUsername(user);
                uVO.setPasswordHash(passwordSeguro); // Guardamos el hash
                String rolElegido = dialog.getRolSeleccionado();
                uVO.setRol(rolElegido);

                UsuarioDAO uDAO = new UsuarioDAO();
                int idUsuarioCreado = uDAO.insertarYObtenerId(uVO);

                if (idUsuarioCreado > 0) {
                    // B. Vinculamos el ID del usuario al empleado
                    nuevoEmp.setUsuarioId(idUsuarioCreado);

                    // C. Insertamos el empleado físico
                    if (modeloDAO.insertar(nuevoEmp)) {
                        JOptionPane.showMessageDialog(vista, "Empleado y acceso creados correctamente.");
                        cargarEmpleados(); // Refresca la tabla
                    } else {
                        JOptionPane.showMessageDialog(vista, "Error al crear el empleado físico.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(vista, "Error: El nombre de usuario ya existe o es inválido.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, "Error en los datos: " + ex.getMessage());
            }
        }
    
    }
    
    private void editarEmpleadoSeleccionado() {
        int filaSelec = vista.getTblEmpleado().getSelectedRow();
       
        if (filaSelec != -1) {
            String dni = (String) modeloTabla.getValueAt(filaSelec, 0); // El DNI está en la columna 0
            EmpleadoVO empleadoAEditar = modeloDAO.buscarPorDni(dni);
           
            if (empleadoAEditar != null) {
                // Abrimos el dialog pasándole los datos (Edición)
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

    private void buscarEmpleado() {
        String busqueda = vista.getTxtBuscarEmpleado().getText().trim();
        String placeholder = "Buscar empleado..."; 
        if (busqueda.isEmpty() || busqueda.equals(placeholder)) {
            JOptionPane.showMessageDialog(vista,
                "Por favor, introduzca un término antes de buscar.",
                "Aviso de Búsqueda",
                JOptionPane.WARNING_MESSAGE);
            vista.getTxtBuscarEmpleado().requestFocus();
            return;
        }
        modeloTabla.setRowCount(0);
        List<EmpleadoVO> lista = modeloDAO.buscarEmpleado(busqueda);
        
        if (lista != null && !lista.isEmpty()) {
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
            vista.getTxtBuscarEmpleado().setText("");
        } else {
            JOptionPane.showMessageDialog(vista, "No se han encontrado empleados con el parámetro " + busqueda, "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            cargarEmpleados();
            vista.getTxtBuscarEmpleado().setText("");
        }
    }
}

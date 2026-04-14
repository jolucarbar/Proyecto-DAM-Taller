
package com.joseluis.apptaller.controlador;

import com.joseluis.apptaller.modelo.dao.ClienteDAO;
import com.joseluis.apptaller.modelo.vo.ClienteVO;
import com.joseluis.apptaller.vista.ventanas.VentanaPrincipal;
import com.joseluis.apptaller.vista.dialogos.DialogNuevoCliente;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import com.joseluis.apptaller.vista.dialogos.DialogHistorialCliente;

/**
 * Clase que gestiona la lógica de negocio de los Clientes.
 * 
 * @author José Luis Cárdenas Barroso
 * @info Proyecto Intermodular del Grado Superior DAM
 * @institution IES Augustóbriga
 */

public class ControladorClientes implements ActionListener {

    private final VentanaPrincipal vista;
    private final ClienteDAO modeloDAO;
    private DefaultTableModel modeloTabla;

    public ControladorClientes(VentanaPrincipal vista, ClienteDAO modeloDAO) {
        this.vista = vista;
        this.modeloDAO = modeloDAO;
        
        // Inicializamos la configuración de la tabla y los listeners
        initTabla();
        initListeners();
        cargarClientes(); // Carga inicial de datos al abrir la pestaña
    }

    private void initTabla() {
        // Configuramos el modelo para que las celdas no sean editables directamente haciendo doble clic
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Nombres de las columnas que verá el usuario
        String[] columnas = {"DNI", "Nombre", "Teléfono", "Email", "Dirección"};
        modeloTabla.setColumnIdentifiers(columnas);
        
        // Asignamos el modelo a la tabla visual de VentanaPrincipal
        vista.getTblClientes().setModel(modeloTabla);
        vista.getTblClientes().setRowHeight(30); 
    }

    private void initListeners() {
        // Ponemos a "escuchar" los botones de la vista
        vista.getBtnNuevoCliente().addActionListener(this);
        vista.getBtnEliminarCliente().addActionListener(this);
        vista.getBtnHistorialCliente().addActionListener(this);
        
        // --- AÑADIMOS EL DOBLE CLIC PARA EDITAR ---
        vista.getTblClientes().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) { // Si es doble clic
                    editarClienteSeleccionado();
                }
            }
        });
    }

    public void cargarClientes() {
        modeloTabla.setRowCount(0); // Limpiamos la tabla antes de rellenar
        List<ClienteVO> lista = modeloDAO.listar();

        for (ClienteVO c : lista) {
            Object[] fila = new Object[5];
            fila[0] = c.getDni();
            fila[1] = c.getNombre();
            fila[2] = c.getTelefono();
            fila[3] = c.getEmail();
            fila[4] = c.getDireccion();
            modeloTabla.addRow(fila);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // ¿Qué botón se ha pulsado?
        if (e.getSource() == vista.getBtnNuevoCliente()) {
            abrirDialogoNuevoCliente();
        } else if (e.getSource() == vista.getBtnEliminarCliente()) {
            eliminarClienteSeleccionado();
        } else if (e.getSource() == vista.getBtnHistorialCliente()) {
            abrirHistorialCliente();
        }
    }

    private void abrirDialogoNuevoCliente() {
        // Abrimos el dialog
        DialogNuevoCliente dialog = new DialogNuevoCliente(vista, true);
        dialog.setVisible(true); // La aplicación se "pausa" aquí hasta que el usuario cierra la ventana
                
        // Cuando el diálogo se cierra, comprobamos si el usuario pulsó el botón "Guardar"
        if (dialog.isGuardado()) {
            
            // Le pedimos al diálogo los datos que ha introducido el usuario
            ClienteVO nuevoCliente = dialog.getCliente();
            
            // Se los pasamos al DAO para que haga el INSERT en MySQL
            if (modeloDAO.insertar(nuevoCliente)) {
                JOptionPane.showMessageDialog(vista, "Cliente guardado con éxito.");
                cargarClientes(); // Recargamos la tabla visual para que aparezca el nuevo registro
            } else {
                JOptionPane.showMessageDialog(vista, "Error al guardar el cliente en la base de datos.\nRevise si el DNI ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarClienteSeleccionado() {
        int filaSelec = vista.getTblClientes().getSelectedRow();
        if (filaSelec != -1) { // Si hay una fila seleccionada
            String dni = (String) modeloTabla.getValueAt(filaSelec, 0); // El DNI está en la columna 0
            
            // Pedimos confirmación antes de borrar
            int confirmacion = JOptionPane.showConfirmDialog(vista, "¿Seguro que desea eliminar al cliente con DNI " + dni + "?", "Confirmar borrado", JOptionPane.YES_NO_OPTION);
            
            if (confirmacion == JOptionPane.YES_OPTION) {
                if (modeloDAO.eliminar(dni)) {
                    JOptionPane.showMessageDialog(vista, "Cliente eliminado correctamente.");
                    cargarClientes(); // Refrescamos la tabla
                } else {
                    JOptionPane.showMessageDialog(vista, "Error al eliminar el cliente.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(vista, "Por favor, seleccione un cliente de la tabla primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void editarClienteSeleccionado() {
        int filaSelec = vista.getTblClientes().getSelectedRow();
        if (filaSelec != -1) {
            // Obtenemos el DNI de la fila seleccionada
            String dni = (String) modeloTabla.getValueAt(filaSelec, 0);
            
            // Buscamos el cliente completo en la base de datos usando el DAO
            ClienteVO clienteAEditar = modeloDAO.buscarPorDni(dni);
            
            if (clienteAEditar != null) {
                // Abrimos el diálogo usando el constructor sobrecargado 
                DialogNuevoCliente dialog = new DialogNuevoCliente(vista, true, clienteAEditar);
                dialog.setVisible(true);
                
                // Al cerrar, comprobamos si pulsó guardar
                if (dialog.isGuardado()) {
                    ClienteVO clienteModificado = dialog.getCliente();
                    // Al ser modificación, no alteramos la fecha de registro original ni el estado activo
                    clienteModificado.setFechaRegistro(clienteAEditar.getFechaRegistro());
                    clienteModificado.setActivo(clienteAEditar.isActivo());
                    
                    // Mandamos actualizar a MySQL
                    if (modeloDAO.modificar(clienteModificado)) {
                        javax.swing.JOptionPane.showMessageDialog(vista, "Cliente actualizado con éxito.");
                        cargarClientes(); // Refrescamos la tabla
                    } else {
                        javax.swing.JOptionPane.showMessageDialog(vista, "Error al actualizar el cliente.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    private void abrirHistorialCliente() {
        int filaSelec = vista.getTblClientes().getSelectedRow();
        if (filaSelec != -1) {
            // Sacamos el DNI y buscamos al cliente completo
            String dni = (String) modeloTabla.getValueAt(filaSelec, 0);
            ClienteVO clienteSeleccionado = modeloDAO.buscarPorDni(dni);
            
            if (clienteSeleccionado != null) {
                // Abrimos el diálogo 
                DialogHistorialCliente dialog = new DialogHistorialCliente(vista, true, clienteSeleccionado);
                dialog.setVisible(true);
            }
        } else {
            javax.swing.JOptionPane.showMessageDialog(vista, "Seleccione un cliente en la tabla para ver su historial.", "Aviso", javax.swing.JOptionPane.WARNING_MESSAGE);
        }
    }
    
}

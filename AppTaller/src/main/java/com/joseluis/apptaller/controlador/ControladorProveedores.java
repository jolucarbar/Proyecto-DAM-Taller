
package com.joseluis.apptaller.controlador;

import com.joseluis.apptaller.modelo.dao.ProveedorDAO;
import com.joseluis.apptaller.modelo.vo.ProveedorVO;
import com.joseluis.apptaller.vista.dialogos.DialogNuevoProveedor;
import com.joseluis.apptaller.vista.ventanas.VentanaPrincipal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Controlador encargado de gestionar la lógica de negocio del módulo de Proveedores.
 * Actúa como intermediario entre la interfaz gráfica (VentanaPrincipal y diálogos) 
 * y la capa de acceso a datos (ProveedorDAO).
 * 
 * @author José Luis Cárdenas Barroso
 * @info Proyecto Intermodular del Grado Superior DAM
 * @institution IES Augustóbriga
 */
public class ControladorProveedores implements ActionListener{

    private final VentanaPrincipal vista;
    private final ProveedorDAO modeloDAO;
    private DefaultTableModel modeloTabla;

    public ControladorProveedores(VentanaPrincipal vista, ProveedorDAO modeloDAO) {
        this.vista = vista;
        this.modeloDAO = modeloDAO;
        
        initTabla();
        initListeners();
        cargarProveedores(); // Carga inicial de datos al abrir la pestaña
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
        String[] columnas = {"CIF", "Nombre","Dirección", "Teléfono", "Email", "Contacto", "Web"};
        modeloTabla.setColumnIdentifiers(columnas);
        
        // Asignamos el modelo a la tabla visual de VentanaPrincipal
        vista.getTblProveedores().setModel(modeloTabla);
        vista.getTblProveedores().setRowHeight(30); 
    }
    
    
    private void initListeners() {
        // Ponemos a "escuchar" los botones de la vista
        vista.getBtnNuevoProveedor().addActionListener(this);
        vista.getBtnEliminarProveedor().addActionListener(this);
        vista.getBtnBuscarProveedor().addActionListener(this);
        
        // Doble click para editar
        vista.getTblProveedores().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) { // Si es doble click
                    editarProveedorSeleccionado();
                }
            }
        });
    }
    
    
    private void cargarProveedores() {
       modeloTabla.setRowCount(0); // Limpiamos la tabla antes de rellenar
        List<ProveedorVO> lista = modeloDAO.listar();

        for (ProveedorVO p : lista) {
            Object[] fila = new Object[7];
            fila[0] = p.getCif();
            fila[1] = p.getNombre();
            fila[2] = p.getDireccion();
            fila[3] = p.getTelefono();
            fila[4] = p.getEmail();
            fila[5] = p.getContacto();
            fila[6] = p.getWeb();
            
            modeloTabla.addRow(fila);
        }
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // ¿Qué botón se ha pulsado?
        if (e.getSource() == vista.getBtnNuevoProveedor()) {
            abrirDialogoNuevoProveedor();
        } else if (e.getSource() == vista.getBtnEliminarProveedor()) {
            eliminarProveedorSeleccionado();
        } else if (e.getSource() == vista.getBtnBuscarProveedor()) {
            JOptionPane.showMessageDialog(vista, "Búsqueda en construcción...", "Info",
            JOptionPane.INFORMATION_MESSAGE);
        }
    }

   
    
    private void editarProveedorSeleccionado() {
        int filaSelec = vista.getTblProveedores().getSelectedRow();
        if (filaSelec != -1) {
            // Obtenemos el CIF de la fila seleccionada
            String cif = (String) modeloTabla.getValueAt(filaSelec, 0);
            
            // Buscamos el proveedor completo en la base de datos usando el DAO
            ProveedorVO proveedorAEditar = modeloDAO.buscarPorCif(cif);
            
            if (proveedorAEditar != null) {
                // Abrimos el diálogo usando el constructor sobrecargado 
                DialogNuevoProveedor dialog = new DialogNuevoProveedor(vista, true, proveedorAEditar);
                dialog.setVisible(true);
                
                // Al cerrar, comprobamos si pulsó guardar
                if (dialog.isGuardado()) {
                    ProveedorVO proveedorModificado = dialog.getProveedor();
                    // Al ser modificación, no alteramos la fecha de registro original ni el estado activo
                    proveedorModificado.setCreated_at(proveedorAEditar.getCreated_at());
                    proveedorModificado.setActivo(proveedorAEditar.isActivo());
                    
                    // Mandamos actualizar a MySQL
                    if (modeloDAO.modificar(proveedorModificado)) {
                        javax.swing.JOptionPane.showMessageDialog(vista, "Proveedor actualizado con éxito.");
                        cargarProveedores(); // Refrescamos la tabla
                    } else {
                        javax.swing.JOptionPane.showMessageDialog(vista, "Error al actualizar el proveedor.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    
    private void abrirDialogoNuevoProveedor() {
        // Abrimos el dialog
        DialogNuevoProveedor dialog = new DialogNuevoProveedor(vista, true);
        dialog.setVisible(true); // La aplicación se "pausa" aquí hasta que el usuario cierra la ventana
                
        // Cuando el diálogo se cierra, comprobamos si el usuario pulsó el botón "Guardar"
        if (dialog.isGuardado()) {
            
            // Le pedimos al diálogo los datos que ha introducido el usuario
            ProveedorVO nuevoProveedor = dialog.getProveedor();
            
            // Se los pasamos al DAO para que haga el INSERT en MySQL
            if (modeloDAO.insertar(nuevoProveedor)) {
                JOptionPane.showMessageDialog(vista, "Proveedor guardado con éxito.");
                cargarProveedores(); // Recargamos la tabla visual para que aparezca el nuevo registro
            } else {
                JOptionPane.showMessageDialog(vista, "Error al guardar el proveedor en la base de datos.\nRevise si el CIF ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarProveedorSeleccionado() {
        int filaSelec = vista.getTblProveedores().getSelectedRow();
        if (filaSelec != -1) { // Si hay una fila seleccionada
            String cif = (String) modeloTabla.getValueAt(filaSelec, 0); // El CIF está en la columna 0
            
            // Pedimos confirmación antes de borrar
            int confirmacion = JOptionPane.showConfirmDialog(vista, "¿Seguro que desea eliminar al proveedor con CIF " + cif + "?", "Confirmar borrado", JOptionPane.YES_NO_OPTION);
            
            if (confirmacion == JOptionPane.YES_OPTION) {
                if (modeloDAO.eliminar(cif)) {
                    JOptionPane.showMessageDialog(vista, "Proveedor eliminado correctamente.");
                    cargarProveedores(); // Refrescamos la tabla
                } else {
                    JOptionPane.showMessageDialog(vista, "Error al eliminar el proveedor.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(vista, "Por favor, seleccione un proveedor de la tabla primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
    
}

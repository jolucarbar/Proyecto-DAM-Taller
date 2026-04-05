
package com.joseluis.apptaller.controlador;

import com.joseluis.apptaller.modelo.dao.ClienteDAO;
import com.joseluis.apptaller.modelo.dao.VehiculoDAO;
import com.joseluis.apptaller.modelo.vo.ClienteVO;
import com.joseluis.apptaller.modelo.vo.VehiculoVO;
import com.joseluis.apptaller.vista.dialogos.DialogNuevoVehiculo;
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
public class ControladorVehiculos implements ActionListener {
    private final VentanaPrincipal vista;
    private final VehiculoDAO modeloDAO;
    private DefaultTableModel modeloTabla;
    
    public ControladorVehiculos(VentanaPrincipal vista, VehiculoDAO modeloDAO) {
        this.vista = vista;
        this.modeloDAO = modeloDAO;
        
        initTabla();
        initListeners();
        cargarVehiculos();  // Carga los vehículos al arancar
    }
    
    private void initTabla() {
        // Celdas no editables directamente
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
         // Las columnas definidadse en el GUI, más el DNI del propietario
        String[] columnas = {"Bastidor", "Matrícula", "Marca", "Modelo", "Color", "DNI Propietario"};
        modeloTabla.setColumnIdentifiers(columnas);
        vista.getTblVehiculo().setModel(modeloTabla);
        vista.getTblVehiculo().setRowHeight(30);
    }
    
    private void initListeners() {
        // Escuchamos los botones
        vista.getBtnNuevoVehiculo().addActionListener(this);
        vista.getBtnEliminarVehiculo().addActionListener(this);
        vista.getBtnBuscarVehiculo().addActionListener(this);
        
        // Escuchamos el doble click en la tabla para editar
        vista.getTblVehiculo().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount()==2) {
                    editarVehiculoSeleccionado();
                }
            }
        });
    }

    private void cargarVehiculos() {
        modeloTabla.setRowCount(0); // Limpiar tabla
        List<VehiculoVO> lista = modeloDAO.listar();
        for (VehiculoVO v : lista) {
            Object[] fila = new Object[6];
            fila[0] = v.getBastidor();
            fila[1] = v.getMatricula();
            fila[2] = v.getMarca();
            fila[3] = v.getModelo();
            fila[4] = v.getColor();
            fila[5] = v.getDniPropietario();
            modeloTabla.addRow(fila);
         }
        vista.getTblVehiculo().revalidate();
        vista.getTblVehiculo().repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnNuevoVehiculo()) {
            abrirDialogoNuevoVehiculo();
        } else if (e.getSource() == vista.getBtnEliminarVehiculo()) {
            eliminarVehiculoSeleccionado();
        } else if (e.getSource() == vista.getBtnBuscarVehiculo()) {
            JOptionPane.showMessageDialog(vista, "Búsqueda en construcción...", "Info",
            JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void abrirDialogoNuevoVehiculo() {
       // 1. Pedimos la lista de clientes activos para pasársela al diálogo
        ClienteDAO clienteDAO = new ClienteDAO();
        List<ClienteVO> clientesActivos = clienteDAO.listar();
        
        // 2. Abrimos el diálogo pasándole la lista
        DialogNuevoVehiculo dialog = new DialogNuevoVehiculo(vista, true, clientesActivos);
        dialog.setVisible(true); // Se pausa aquí hasta que el usuario cierra
        
        // 3. Cuando se cierra, comprobamos si guardó
        if (dialog.isGuardado()) {
            VehiculoVO nuevoVehiculo = dialog.getVehiculo();
            
            // 4. Mandamos a la Base de Datos
            if (modeloDAO.insertar(nuevoVehiculo)) {
                JOptionPane.showMessageDialog(vista, "Vehículo registrado con éxito.");
                cargarVehiculos(); // Refrescamos la tabla
            } else {
                JOptionPane.showMessageDialog(vista, "Error al guardar el vehículo.\nRevise si la matrícula o bastidor ya existen.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } 
    }
    
    private void editarVehiculoSeleccionado() {
        int filaSelec = vista.getTblVehiculo().getSelectedRow();
        if (filaSelec != -1) {
            // 1. Obtenemos el Bastidor de la fila seleccionada (que está en la columna 0)
            String bastidor = (String) modeloTabla.getValueAt(filaSelec, 0);
            
            // 2. Buscamos el vehículo completo en la BD
            VehiculoVO vehiculoAEditar = modeloDAO.buscarPorBastidor(bastidor);
            if (vehiculoAEditar != null) {
                // 3. Cargamos los clientes para el desplegable
                ClienteDAO clienteDAO = new ClienteDAO();
                List<ClienteVO> clientesActivos = clienteDAO.listar();
        
                // 4. Abrimos el modal en modo EDICIÓN
                DialogNuevoVehiculo dialog = new DialogNuevoVehiculo(vista, true, clientesActivos,
                vehiculoAEditar);
                dialog.setVisible(true);
        
                // 5. Al cerrar, si el usuario pulsó Guardar, hacemos el UPDATE
                if (dialog.isGuardado()) {
                    VehiculoVO vehiculoModificado = dialog.getVehiculo();
                   
                    // Interceptamos la respuesta en una variable antes del IF
                    boolean resultadoDAO = modeloDAO.modificar(vehiculoModificado);
                   
                    if (resultadoDAO) {
                        JOptionPane.showMessageDialog(vista, "Vehículo actualizado con éxito.");
                        cargarVehiculos(); // Refrescamos la tabla visual
                    } else {
                        JOptionPane.showMessageDialog(vista, "Error al actualizar el vehículo en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                
                }
            }
        }
    }
    
    private void eliminarVehiculoSeleccionado() {
        int filaSelec = vista.getTblVehiculo().getSelectedRow();
        if (filaSelec != -1) {
            String matricula = (String) modeloTabla.getValueAt(filaSelec, 1); // La matrícula está en la columna 1
            int confirmacion = JOptionPane.showConfirmDialog(vista, "¿Seguro que desea eliminar el vehículo con matrícula " + matricula + "?", "Confirmar borrado", JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                if (modeloDAO.eliminar(matricula)) {
                    JOptionPane.showMessageDialog(vista, "Vehículo eliminado correctamente.");
                    cargarVehiculos(); // Refrescar tabla
                } else {
                    JOptionPane.showMessageDialog(vista, "Error al eliminar el vehículo.\nEs posible que tenga reparaciones asociadas.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(vista, "Por favor, seleccione un vehículo de la tabla primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
    
}

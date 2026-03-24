
package com.joseluis.apptaller.controlador;

import com.joseluis.apptaller.modelo.dao.VehiculoDAO;
import com.joseluis.apptaller.modelo.vo.VehiculoVO;
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
        // TODO: Lo programaremos en el Paso 2.3
        JOptionPane.showMessageDialog(vista, "Preparando modal para Alta de Vehículos (Paso 2.3).", "Próximamente", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void editarVehiculoSeleccionado() {
        // TODO: Lo programaremos en el Paso 2.3
        JOptionPane.showMessageDialog(vista, "La edición compartirá formulario con el Alta (Paso 2.3).", "Próximamente", JOptionPane.INFORMATION_MESSAGE);
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

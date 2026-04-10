
package com.joseluis.apptaller.controlador;

import com.joseluis.apptaller.modelo.dao.ProductoDAO;
import com.joseluis.apptaller.modelo.dao.ProveedorDAO;
import com.joseluis.apptaller.modelo.vo.ProductoVO;
import com.joseluis.apptaller.vista.dialogos.DialogNuevoProducto;
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
public class ControladorProductos implements ActionListener {
    private final VentanaPrincipal vista;
    private final ProductoDAO modeloDAO;
    private DefaultTableModel modeloTabla;
    
    
    public ControladorProductos(VentanaPrincipal vista, ProductoDAO modeloDAO) {
        this.vista = vista;
        this.modeloDAO = modeloDAO;
        
        initTabla();
        initListeners();
        cargarProductos();  // Carga los productos al arancar
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnNuevoProducto()) {
            abrirDialogoNuevoProducto();
        } else if (e.getSource() == vista.getBtnEliminarProducto()) {
            eliminarProductoSeleccionado();
        } else if (e.getSource() == vista.getBtnBuscarProducto()) {
            JOptionPane.showMessageDialog(vista, "Búsqueda en construcción...", "Info",
            JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void initTabla() {
        // Celdas no editables directamente
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
         
        String[] columnas = {"ID Producto", "Nombre", "Descripcion", "Categoria", "Stock", "Stock minimo", "Precio compra", "PVP venta", "Proveedor", "Fecha alta"};
        modeloTabla.setColumnIdentifiers(columnas);
        vista.getTblProductos().setModel(modeloTabla);
        vista.getTblProductos().setRowHeight(30);
    }

    private void initListeners() {
        // Escuchamos los botones
        vista.getBtnNuevoProducto().addActionListener(this);
        vista.getBtnEliminarProducto().addActionListener(this);
        vista.getBtnBuscarProducto().addActionListener(this);
        
        // Escuchamos el doble click en la tabla para editar
        vista.getTblProductos().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount()==2) {
                    editarProductoSeleccionado();
                }
            }
        });
    }

    private void cargarProductos() {
    modeloTabla.setRowCount(0);
    List<ProductoVO> lista = modeloDAO.listar();
    for (ProductoVO p : lista) {
        Object[] fila = {
            p.getIdProducto(),
            p.getNombre(),
            p.getDescripcion(),
            p.getCategoria(),
            p.getCantidadStock(),
            p.getStockMinimo(),
            p.getPrecioCompra(),
            p.getPrecioUnitario(12.99F),
            p.getProveedorCif(),
            p.getCreated_at()
        };
        modeloTabla.addRow(fila);
    }
}
    
    private void abrirDialogoNuevoProducto() {
        DialogNuevoProducto dialog = new DialogNuevoProducto(vista, true);
        
        // 1. Cargar los proveedores antes de mostrar el diálogo
        ProveedorDAO provDAO = new ProveedorDAO();
        dialog.cargarProveedores(provDAO.listar());
        
        // 2. Configurar el listener de guardado
        dialog.getBtnGuardar().addActionListener(e -> {
            try {
                ProductoVO p = dialog.guardarDatos();
                p.validar();
                if (modeloDAO.insertar(p)) {
                    JOptionPane.showMessageDialog(dialog, "Producto guardado con éxito.");
                    dialog.dispose();
                    cargarProductos(); // Refresca la tabla principal
                } else {
                    JOptionPane.showMessageDialog(dialog, "Error al guardar en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        dialog.setVisible(true);
}

private void editarProductoSeleccionado() {
    int fila = vista.getTblProductos().getSelectedRow();
        if (fila != -1) {
            String id = (String) modeloTabla.getValueAt(fila, 0);
            ProductoVO p = obtenerProductoDeLaLista(id); 
            
            DialogNuevoProducto dialog = new DialogNuevoProducto(vista, true);
            
            // 1. Cargar proveedores de la BD
            ProveedorDAO provDAO = new ProveedorDAO();
            dialog.cargarProveedores(provDAO.listar());
            
            // 2. Cargar los datos del producto     
            dialog.cargarDatos(p);
            
            dialog.getBtnGuardar().addActionListener(e -> {
                try {
                    ProductoVO pEditado = dialog.guardarDatos();
                    pEditado.validar();
                    if (modeloDAO.modificar(pEditado)) {
                        JOptionPane.showMessageDialog(dialog, "Producto modificado con éxito.");
                        dialog.dispose();
                        cargarProductos();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Error al actualizar en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
                }
            });
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(vista, "Seleccione un producto para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
}

    private void eliminarProductoSeleccionado() {
        int filaSelec = vista.getTblProductos().getSelectedRow();
        if (filaSelec != -1) {
            String idProducto = (String) modeloTabla.getValueAt(filaSelec, 1); 
            int confirmacion = JOptionPane.showConfirmDialog(vista, "¿Seguro que desea eliminar el producto con ID " + idProducto + "?", "Confirmar borrado", JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                if (modeloDAO.eliminar(idProducto)) {
                    JOptionPane.showMessageDialog(vista, "Vehículo eliminado correctamente.");
                    cargarProductos(); // Refrescar tabla
                } else {
                    JOptionPane.showMessageDialog(vista, "Error al eliminar el producto.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(vista, "Por favor, seleccione un producto de la tabla primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
    

    private ProductoVO obtenerProductoDeLaLista(String id) {
        List<ProductoVO> lista = modeloDAO.listar();
        for (ProductoVO p : lista) {
            if (p.getIdProducto().equals(id)) {
                return p;
            }
        }
        return null;
    }
    
}

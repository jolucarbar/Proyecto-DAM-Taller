
package com.joseluis.apptaller.controlador;

import com.joseluis.apptaller.modelo.dao.ProductoDAO;
import com.joseluis.apptaller.modelo.dao.ProveedorDAO;
import com.joseluis.apptaller.modelo.vo.ProductoVO;
import com.joseluis.apptaller.util.RendererStockBajo;
import com.joseluis.apptaller.vista.dialogos.DialogNuevoProducto;
import com.joseluis.apptaller.vista.ventanas.VentanaPrincipal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Controlador que gestiona la interacción del usuario con el inventario del taller.
 * Coordina las operaciones CRUD de los productos y mantiene sincronizada la 
 * tabla visual con la base de datos MySQL.
 * Actúa como intermediario entre la vista (VentanaPrincipal y diálogos asociados) y la 
 * capa de acceso a datos (ProductoDAO y ProveedorDAO).
 * 
 * @author José Luis Cárdenas Barroso
 * @info Proyecto Intermodular del Grado Superior DAM
 * @institution IES Augustóbriga
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
    
    
    private void initListeners() {
        // 1. Escuchamos los botones
        vista.getBtnNuevoProducto().addActionListener(this);
        vista.getBtnEliminarProducto().addActionListener(this);
        vista.getBtnBuscarProducto().addActionListener(this);
        vista.getBtnAnadirStock().addActionListener(this); 
        vista.getBtnRecargarProductos().addActionListener(this);

        // 2. Escuchamos el doble click en la tabla (SOLO PARA EDITAR)
        vista.getTblProductos().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarProductoSeleccionado();
                }
            }
        });
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnNuevoProducto()) {
            abrirDialogoNuevoProducto();
        } else if (e.getSource() == vista.getBtnEliminarProducto()) {
            eliminarProductoSeleccionado();
        } else if (e.getSource() == vista.getBtnBuscarProducto()) {
            buscarProducto();
        } else if (e.getSource() == vista.getBtnAnadirStock()) {
            anadirStockSeleccionado();
        } else if (e.getSource() == vista.getBtnRecargarProductos()) {
            cargarProductos();
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

        // Le aplicamos el pintor a la tabla del Controlador
        RendererStockBajo rendererAlarma = new RendererStockBajo(4, 5);
        for (int i = 0; i < vista.getTblProductos().getColumnCount(); i++) {
            vista.getTblProductos().getColumnModel().getColumn(i).setCellRenderer(rendererAlarma);
        }
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
        
        // Cargamos los proveedores antes de mostrar el diálogo
        ProveedorDAO provDAO = new ProveedorDAO();
        dialog.cargarProveedores(provDAO.listar());
        
        // Configuramos el listener de guardado
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

            // Cargamos proveedores de la BD
            ProveedorDAO provDAO = new ProveedorDAO();
            dialog.cargarProveedores(provDAO.listar());

            // Cargamos los datos del producto     
            dialog.cargarDatos(p);

            dialog.getBtnGuardar().addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
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

    private void anadirStockSeleccionado() {
        int filaSeleccionada = vista.getTblProductos().getSelectedRow();
       
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor, seleccione un producto de la tabla para añadir stock.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idProducto = vista.getTblProductos().getValueAt(filaSeleccionada, 0).toString();
        String nombreProducto = vista.getTblProductos().getValueAt(filaSeleccionada, 1).toString();

        String input = JOptionPane.showInputDialog(vista, "¿Cuántas unidades de [" + nombreProducto + "] han llegado?", "Recepción", JOptionPane.QUESTION_MESSAGE);

        if (input != null && !input.trim().isEmpty()) {
            try {
                int cantidad = Integer.parseInt(input.trim());
                if (cantidad > 0) {
                    // Usamos el modeloDAO que ya tienes instanciado en esta clase
                    if (modeloDAO.sumarStock(idProducto, cantidad)) {
                        JOptionPane.showMessageDialog(vista, "Stock actualizado correctamente. Se sumaron " + cantidad + " unidades.");
                        cargarProductos(); // Refresca la tabla
                        vista.refrescarAlertasStock(); // Apaga alertas rojas
                    } else {
                        JOptionPane.showMessageDialog(vista, "Error al actualizar la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(vista, "La cantidad debe ser mayor que cero.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(vista, "Introduzca un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void buscarProducto() {
        String busqueda = vista.getTxtBuscarProductos().getText().trim();
        String placeholder = "Buscar producto..."; 
        if (busqueda.isEmpty() || busqueda.equals(placeholder)) {
            JOptionPane.showMessageDialog(vista,
                "Por favor, introduzca un término antes de buscar.",
                "Aviso de Búsqueda",
                JOptionPane.WARNING_MESSAGE);
            vista.getTxtBuscarCliente().requestFocus();
            return;
        }
        modeloTabla.setRowCount(0); // Limpiamos la tabla antes de rellenar
        List<ProductoVO> lista = modeloDAO.buscarProducto(busqueda);
        
        if (lista != null && !lista.isEmpty()) {
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
            vista.getTxtBuscarProductos().setText("");
        } else {
            JOptionPane.showMessageDialog(vista, "No se han encontrado productos con el texto " + busqueda, "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            cargarProductos();
            vista.getTxtBuscarProductos().setText("");
        }
    }
    
    
}

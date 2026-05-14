package com.joseluis.apptaller.controlador;

import com.joseluis.apptaller.modelo.dao.ClienteDAO;
import com.joseluis.apptaller.modelo.dao.PresupuestoDAO;
import com.joseluis.apptaller.modelo.dao.ProductoDAO;
import com.joseluis.apptaller.modelo.dao.VehiculoDAO;
import com.joseluis.apptaller.modelo.vo.ClienteVO;
import com.joseluis.apptaller.modelo.vo.DetalleManoObraVO;
import com.joseluis.apptaller.modelo.vo.DetalleProductoVO;
import com.joseluis.apptaller.modelo.vo.PresupuestoVO;
import com.joseluis.apptaller.modelo.vo.ProductoVO;
import com.joseluis.apptaller.modelo.vo.VehiculoVO;
import com.joseluis.apptaller.vista.dialogos.DialogNuevoPresupuesto;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Controlador para la gestión de creación de presupuestos.
 * Conecta la Vista (DialogNuevoPresupuesto) con el Modelo (DAO/VO).
 * 
 * @author José Luis Cárdenas Barroso
 * @info Proyecto Intermodular del Grado Superior DAM
 * @institution IES Augustóbriga
 */
public class ControladorNuevoPresupuesto {

    private final DialogNuevoPresupuesto vista;
    private final PresupuestoDAO dao;

    // Inyectamos la vista y el DAO en el constructor
    public ControladorNuevoPresupuesto(DialogNuevoPresupuesto vista, PresupuestoDAO dao) {
        this.vista = vista;
        this.dao = dao;
    }

    /**
     * Método principal llamado por la vista cuando el usuario pulsa "Guardar".
     */
    public void guardarPresupuesto() {
        // Validación básica
        if (vista.getCbxCliente().getSelectedItem() == null || vista.getCbxVehiculo().getSelectedItem() == null) {
            mostrarError("Debe seleccionar un Cliente y un Vehículo.");
            return;
        }

        DefaultTableModel modManoObra = (DefaultTableModel) vista.getTablaManoObra().getModel();
        DefaultTableModel modMateriales = (DefaultTableModel) vista.getTablaMateriales().getModel();
        
        if (!hayDatosEnTabla(modManoObra) && !hayDatosEnTabla(modMateriales)) {
             mostrarError("El presupuesto debe contener al menos una Tarea o un Material.");
             return;
        }

        // Creación del modelo (VO)
        PresupuestoVO presupuesto = new PresupuestoVO();
        
        String clienteDni = vista.getCbxCliente().getSelectedItem().toString().split(" - ")[0]; 
        String vehiculoBastidor = vista.getCbxVehiculo().getSelectedItem().toString().split(" - ")[0]; 
        
        presupuesto.setClienteDni(clienteDni);
        presupuesto.setVehiculoBastidor(vehiculoBastidor);
        
        // Convertimos la fecha del JSpinner a LocalDate
        Date dateObj = (Date) vista.getSpnFecha().getValue();
        LocalDate fecha = dateObj.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        presupuesto.setFechaEmision(fecha);
        presupuesto.setFechaValidez(fecha.plusDays(15));
        
        presupuesto.setEstado(vista.getCbxEstado().getSelectedItem().toString().toUpperCase());
        presupuesto.setDescripcionTrabajo(vista.getTxtAveria().getText());
        presupuesto.setTotalEstimado(parsearCelda(vista.getTxtTotal().getText()));

        // Cogemos los datos de mano de obra
        for (int i = 0; i < modManoObra.getRowCount(); i++) {
            Object desc = modManoObra.getValueAt(i, 0);
            if (desc != null && !desc.toString().trim().isEmpty()) {
                BigDecimal horas = parsearCelda(modManoObra.getValueAt(i, 1));
                BigDecimal precio = parsearCelda(modManoObra.getValueAt(i, 2));
                presupuesto.addLineaManoObra(new DetalleManoObraVO(desc.toString(), horas, precio));
            }
        }

        // Cogemos los datos de materiales
        for (int i = 0; i < modMateriales.getRowCount(); i++) {
            Object ref = modMateriales.getValueAt(i, 0);
            Object concepto = modMateriales.getValueAt(i, 1);
            if (ref != null && !ref.toString().trim().isEmpty()) {
                
                BigDecimal cantidadObj = parsearCelda(modMateriales.getValueAt(i, 2));
                BigDecimal precio = parsearCelda(modMateriales.getValueAt(i, 3));
                presupuesto.addLineaProducto(new DetalleProductoVO(
                    ref.toString(), (concepto != null) ? concepto.toString() : "", 
                    cantidadObj.intValue(), precio, BigDecimal.ZERO
                ));
            }
        }

        boolean exito = dao.insertarPresupuestoCompleto(presupuesto);

        if (exito) {
            JOptionPane.showMessageDialog(vista, "Presupuesto guardado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            vista.dispose(); // Cerramos la ventana desde el controlador
        } else {
            mostrarError("Hubo un error al guardar el presupuesto en la base de datos.");
        }
    }
    
    
    /**
     * Lógica para el botón "Añadir Tarea" (Mano de Obra)
     */
    public void aniadirTarea() {
        // Catálogo rápido de tareas comunes 
        String[] tareasComunes = {
            "Mantenimiento General", 
            "Cambio de Aceite y Filtros", 
            "Sustitución Pastillas de Freno", 
            "Cambio Kit Distribución", 
            "Alineación de Dirección", 
            "Diagnosis Electrónica", 
            "Escribir tarea manual..."
        };

        String seleccion = (String) JOptionPane.showInputDialog(
                vista, "Seleccione o describa el trabajo a realizar:", 
                "Catálogo de Mano de Obra", JOptionPane.QUESTION_MESSAGE, 
                null, tareasComunes, tareasComunes[0]);

        if (seleccion != null) {
            // Si elige manual, abrimos un popup para que escriba
            if (seleccion.equals("Escribir tarea manual...")) {
                seleccion = JOptionPane.showInputDialog(vista, "Introduzca la descripción de la tarea:");
                if (seleccion == null || seleccion.trim().isEmpty()) return; // El usuario canceló
            }

            DefaultTableModel modelo = (DefaultTableModel) vista.getTablaManoObra().getModel();
            int filaVacia = buscarPrimeraFilaVacia(modelo);

            // Insertamos los datos. Por defecto: 1 hora a 40.00€
            if (filaVacia != -1) {
                modelo.setValueAt(seleccion, filaVacia, 0); // Descripción
                modelo.setValueAt("1", filaVacia, 1);       // Horas
                modelo.setValueAt("40.00", filaVacia, 2);   // Precio/Hora
                // El listener calculará el subtotal automáticamente
            } else {
                // Si no hay filas vacías, añadimos una nueva fila al final
                modelo.addRow(new Object[]{seleccion, "1", "40.00", "40.00"});
            }
        }
    }

    /**
     * Lógica para el botón "Añadir Material"
     * Reutiliza ProductoDAO.listar() para mostrar el catálogo real.
     */
    public void aniadirMaterial() {
        ProductoDAO prodDAO = new ProductoDAO();
        
        List<ProductoVO> productos = prodDAO.listar();

        if (productos.isEmpty()) {
            mostrarError("No hay productos registrados en la base de datos.");
            return;
        }

        // Transformamos la lista de objetos en un array de Strings para el JComboBox del JOptionPane
        String[] arrayCatalogo = new String[productos.size()];
        for (int i = 0; i < productos.size(); i++) {
            com.joseluis.apptaller.modelo.vo.ProductoVO p = productos.get(i);
            // Formato: "P001 - Aceite Motor - 25.50"
            arrayCatalogo[i] = p.getIdProducto() + " - " + p.getNombre() + " - " + p.getPrecioUnitario(0f); 
        }

        String seleccion = (String) JOptionPane.showInputDialog(
                vista, "Seleccione la pieza o repuesto:", 
                "Catálogo de Productos", JOptionPane.QUESTION_MESSAGE, 
                null, arrayCatalogo, arrayCatalogo[0]);

        if (seleccion != null) {
            // Rompemos el String por los guiones para extraer los datos
            String[] partes = seleccion.split(" - ");
            String ref = partes[0].trim();
            String concepto = partes[1].trim();
            String precio = partes[2].trim();

            DefaultTableModel modelo = (DefaultTableModel) vista.getTablaMateriales().getModel();
            // Comprobamos si el producto ya está en la tabla
            boolean productoYaExiste = false;
            for (int i = 0; i < modelo.getRowCount(); i++) {
                Object celdaRef = modelo.getValueAt(i, 0);
                // Si encontramos la misma referencia en la columna 0...
                if (celdaRef != null && celdaRef.toString().equals(ref)) {
                    // Sumamos 1 a la cantidad actual
                    int cantidadActual = Integer.parseInt(modelo.getValueAt(i, 2).toString());
                    modelo.setValueAt(String.valueOf(cantidadActual + 1), i, 2);
                    
                    // El TableModelListener se encargará de recalcular el subtotal automáticamente
                    productoYaExiste = true;
                    break;
                }
            }

            // Si NO existía, lo añadimos en una fila nueva o vacía
            if (!productoYaExiste) {
                int filaVacia = buscarPrimeraFilaVacia(modelo);

                if (filaVacia != -1) {
                    modelo.setValueAt(ref, filaVacia, 0);       // Referencia
                    modelo.setValueAt(concepto, filaVacia, 1);  // Concepto
                    modelo.setValueAt("1", filaVacia, 2);       // Cantidad
                    modelo.setValueAt(precio, filaVacia, 3);    // Precio Unidad
                } else {
                    modelo.addRow(new Object[]{ref, concepto, "1", precio, precio});
                }
            }
    
        }
        
    }

    /**
     * Busca la primera fila de la tabla que tenga la primera columna vacía.
     */
    private int buscarPrimeraFilaVacia(DefaultTableModel modelo) {
        for (int i = 0; i < modelo.getRowCount(); i++) {
            Object valorCelda = modelo.getValueAt(i, 0);
            if (valorCelda == null || valorCelda.toString().trim().isEmpty()) {
                return i; // Hueco encontrado
            }
        }
        return -1; // Tabla llena
    }
    

    // Métodos Auxiliares
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(vista, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private boolean hayDatosEnTabla(DefaultTableModel modelo) {
        for(int i = 0; i < modelo.getRowCount(); i++) {
             Object valor = modelo.getValueAt(i, 0);
             if (valor != null && !valor.toString().trim().isEmpty()) {
                 return true;
             }
        }
        return false;
    }

    private BigDecimal parsearCelda(Object valor) {
        if (valor == null || valor.toString().trim().isEmpty()) return BigDecimal.ZERO;
        try {
            // Cambiamos coma por punto para evitar fallos con teclados numéricos
            return new BigDecimal(valor.toString().replace(",", "."));
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Prepara la vista antes de ser mostrada al usuario.
     * Carga los datos de la base de datos en los componentes visuales.
     */
    
    public void inicializar() {
       // 1. Cargar Clientes desde el Backend
        cargarDesplegableClientes();

        // 2. Configurar el evento para que al cambiar de cliente se carguen sus vehículos
        configurarEventosDesplegables(); 

        // 3. Sincronizar el estado inicial (
        if (vista.getCbxCliente().getSelectedItem() != null) {
            // Si hay clientes cargados, forzamos la carga de los vehículos del primer cliente
            String dniSeleccionado = vista.getCbxCliente().getSelectedItem().toString().split(" - ")[0];
            cargarVehiculosDelCliente(dniSeleccionado);
        } else {
            // Si la base de datos de clientes está vacía
            vista.getCbxVehiculo().removeAllItems();
            vista.getCbxVehiculo().addItem("No hay clientes registrados");
        }

        // 4. Limpiar campos de texto
        vista.getTxtAveria().setText("");
        vista.getTxtBase().setText("0.00");
        vista.getTxtIva().setText("0.00");
        vista.getTxtTotal().setText("0.00");
    }

    private void cargarDesplegableClientes() {
       vista.getCbxCliente().removeAllItems();
    
        ClienteDAO clienteDAO = new ClienteDAO();
        List<ClienteVO> clientes = clienteDAO.listarTodos();

        for (ClienteVO c : clientes) {
            vista.getCbxCliente().addItem(c.getDni() + " - " + c.getNombre());
        }
        // El primer item quedará seleccionado y disparará el listener,
        // cargando automáticamente sus vehículos
    }

    private void configurarEventosDesplegables() {
        vista.getCbxCliente().addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // Cuando el usuario hace clic en un cliente distinto...
                if (vista.getCbxCliente().getSelectedItem() != null) {
                    String dniSeleccionado = vista.getCbxCliente().getSelectedItem().toString().split(" - ")[0];
                    cargarVehiculosDelCliente(dniSeleccionado);
                }
            }
        });
    }

    private void cargarVehiculosDelCliente(String dni) {
        vista.getCbxVehiculo().removeAllItems();
        
        VehiculoDAO vehiculoDAO = new VehiculoDAO();
        
        List<VehiculoVO> vehiculos = vehiculoDAO.listarPorCliente(dni);
        
        if (vehiculos.isEmpty()) {
            vista.getCbxVehiculo().addItem("Sin vehículos registrados");
        } else {
            for (com.joseluis.apptaller.modelo.vo.VehiculoVO v : vehiculos) {
                vista.getCbxVehiculo().addItem(v.getBastidor() + " - " + v.getMatricula() + " (" + v.getMarca() + ")");
            }
        }
    }
}
package com.joseluis.apptaller.vista.ventanas;

import com.joseluis.apptaller.modelo.dao.ClienteDAO;
import com.joseluis.apptaller.controlador.ControladorClientes;
import com.joseluis.apptaller.modelo.dao.FacturaDAO;
import com.joseluis.apptaller.controlador.ControladorFacturas;
import com.joseluis.apptaller.modelo.dao.VehiculoDAO;
import com.joseluis.apptaller.controlador.ControladorVehiculos;
import com.joseluis.apptaller.modelo.dao.EmpleadoDAO;
import com.joseluis.apptaller.controlador.ControladorEmpleados;
import com.joseluis.apptaller.controlador.ControladorNuevoPresupuesto;
import com.joseluis.apptaller.controlador.ControladorPresupuestos;
import com.joseluis.apptaller.modelo.dao.ProveedorDAO;
import com.joseluis.apptaller.controlador.ControladorProveedores;
import com.joseluis.apptaller.modelo.dao.ProductoDAO;
import com.joseluis.apptaller.controlador.ControladorProductos;
import com.joseluis.apptaller.controlador.ControladorReparaciones;
import com.joseluis.apptaller.modelo.dao.NotaDAO;
import com.joseluis.apptaller.modelo.dao.PresupuestoDAO;
import com.joseluis.apptaller.modelo.dao.ReparacionDAO;
import com.joseluis.apptaller.modelo.vo.ProductoVO;
import com.joseluis.apptaller.modelo.vo.ReparacionVO;
import com.joseluis.apptaller.util.GestorAyuda;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import com.joseluis.apptaller.vista.dialogos.DialogNuevoPresupuesto;
import java.awt.CardLayout;
import java.text.SimpleDateFormat;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Ventana principal de la aplicación AppTaller. Interfaz gráfica central que
 * agrupa todos los módulos del sistema (Clientes, Vehículos, Presupuestos,
 * Reparaciones y Facturación). Se encarga de la captura de eventos del usuario,
 * validación de la interfaz y comunicación con los Controladores del patrón
 * MVC.
 *
 * @author José Luis Cárdenas Barroso
 * @info Proyecto Intermodular del Grado Superior DAM
 * @institution IES Augustóbriga
 */
public class VentanaPrincipal extends javax.swing.JFrame {

    // ATRIBUTOS PARA EL MÓDULO DE FACTURAS
    //private FacturaDAO facturaDAO;
    private DefaultTableModel modeloFacturas;

    // DECLARACIÓN DE DAOs Y CONTROLADORES
    // --- CLIENTES ---
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ControladorClientes ctrlClientes;

    // --- VEHÍCULOS ---
    private final VehiculoDAO vehiculoDAO = new VehiculoDAO();
    private final ControladorVehiculos ctrlVehiculos;

    // --- EMPLEADOS ---
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final ControladorEmpleados ctrlEmpleados;

    // --- PROVEEDORES ---
    private final ProveedorDAO proveedorDAO = new ProveedorDAO();
    private final ControladorProveedores ctrlProveedores;

    // --- PRODUCTOS ---
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final ControladorProductos ctrlProductos;

    // --- REPARACIONES ---
    private final ReparacionDAO reparacionDAO = new ReparacionDAO();
    private final ControladorReparaciones ctrlReparaciones;

    // --- FACTURAS ---
    private final FacturaDAO facturaDAO = new FacturaDAO();
    private final ControladorFacturas ctrlFacturas;
    
    // --- PRESUPUESTOS ---
    private final PresupuestoDAO presupuestoDAO = new PresupuestoDAO();
    private final ControladorPresupuestos ctrlPresupuestos;

    private String nombreUsuario;

    /**
     * Creates new form VentanaPrincipal
     */
    @SuppressWarnings("empty-statement")
    public VentanaPrincipal(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;

        initComponents();
        this.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        setTitle("APP MI TALLER");
        setSize(1200, 850);
        lblBienvenida.setText("BIENVENIDO " + nombreUsuario.toUpperCase());
        setLocationRelativeTo(null);
        lstAyuda.setFixedCellHeight(35);

        iniciarReloj();
        //initPanelFacturas();
        refrescarAlertasStock();
        actualizarContadorVehiculos();
        actualizarAgenda();

        // Panel de Ayuda
        lstAyuda.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String temaSeleccionado = lstAyuda.getSelectedValue();

                    if (temaSeleccionado != null) {
                        // Pedimos el texto a nuestra clase centralizada
                        String html = GestorAyuda.obtenerContenido(temaSeleccionado);

                        // Se lo pasamos al EditorPane
                        editorContenidoAyuda.setText(html);

                        // Subimos el scroll arriba del todo
                        editorContenidoAyuda.setCaretPosition(0);
                    }
                }
            }
        });

        // Actualizar el text de notas
        // Instanciamos el DAO de las notas
        NotaDAO notaDAO = new NotaDAO();

        // Cargamos la nota guardada de la base de datos al abrir el programa
        String notaGuardada = notaDAO.recuperarNota();
        txtNotas.setText(notaGuardada);

        // Añadimos el "Escuchador" para el autoguardado invisible
        txtNotas.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                // Este código se ejecuta automáticamente cuando el usuario hace clic fuera del bloc de notas
                String textoActual = txtNotas.getText();
                notaDAO.guardarNota(textoActual);
            }
        });
        // Fin del bloque de actualizar el text de notas

        // Inyección de dependencias e inicialización de controladores
        ctrlClientes = new ControladorClientes(this, clienteDAO);
        ctrlVehiculos = new ControladorVehiculos(this, vehiculoDAO);
        ctrlEmpleados = new ControladorEmpleados(this, empleadoDAO);
        ctrlProveedores = new ControladorProveedores(this, proveedorDAO);
        ctrlProductos = new ControladorProductos(this, productoDAO);
        ctrlReparaciones = new ControladorReparaciones(this, reparacionDAO);
        ctrlFacturas = new ControladorFacturas(this, facturaDAO);
        ctrlPresupuestos = new ControladorPresupuestos(this, presupuestoDAO);

        // Inicialización de componentes manuales de la vista
        iniciarFiltros();
        
        // Configuras todas tus cajas de búsqueda aquí en una sola línea por caja:
        configurarPlaceholder(txtBuscarCliente, "Buscar cliente...");
        configurarPlaceholder(txtBuscarVehiculo, "Buscar vehículo...");
        configurarPlaceholder(txtBuscarReparacion, "Buscar reparación...");
        configurarPlaceholder(txtBuscarProveedores, "Buscar proveedor...");
        configurarPlaceholder(txtBuscarProductos, "Buscar producto...");
        configurarPlaceholder(txtBuscarPresupuestos, "Buscar presupuesto...");
        configurarPlaceholder(txtBuscarFacturas, "Buscar factura...");
        configurarPlaceholder(txtBuscarEmpleado, "Buscar empleado...");
    }

    /**
     * Configura los modelos de datos y los eventos de los filtros de la
     * interfaz.
     */
    private void iniciarFiltros() {
        // Cargamos las opciones del desplegable
        cbxFiltrarEstado.setModel(new DefaultComboBoxModel<>(
                new String[]{"TODOS", "EN_COLA", "EN_PROCESO", "PAUSADA", "FINALIZADA", "ENTREGADA"}
        ));

        // Evento para filtrar la tabla cuando cambie el JComboBox de Estado
        cbxFiltrarEstado.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Obtenemos el texto seleccionado (TODOS, EN_COLA, EN_PROCESO, etc.)
                String estadoSeleccionado = cbxFiltrarEstado.getSelectedItem().toString();

                // Llamamos al controlador pasándole el modelo de la tabla y el estado
                ctrlReparaciones.filtrarTablaReparaciones(
                        (DefaultTableModel) tblReparaciones.getModel(),
                        estadoSeleccionado
                );
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelTitulo = new javax.swing.JPanel();
        etqLogo = new javax.swing.JLabel();
        etqTitulo = new javax.swing.JLabel();
        panelNavegacion = new javax.swing.JPanel();
        btnInicio = new javax.swing.JButton();
        btnClientes = new javax.swing.JButton();
        btnVehiculos = new javax.swing.JButton();
        btnReparaciones = new javax.swing.JButton();
        btnProveedores = new javax.swing.JButton();
        btnProductos = new javax.swing.JButton();
        btnPresupuestos = new javax.swing.JButton();
        btnFacturas = new javax.swing.JButton();
        btnEmpleados = new javax.swing.JButton();
        btnAyuda = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        panelPrincipal = new javax.swing.JPanel();
        panelInicio = new javax.swing.JPanel();
        panelBienvenida = new javax.swing.JPanel();
        lblBienvenida = new javax.swing.JLabel();
        lblReloj = new javax.swing.JLabel();
        panelEstadoTaller = new javax.swing.JPanel();
        lblEtiquetaVehiculo = new javax.swing.JLabel();
        lblNumeroVehiculos = new javax.swing.JLabel();
        panelAgenda = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jltAgenda = new javax.swing.JList<>();
        lblEtiquetaAgenda = new javax.swing.JLabel();
        panelStock = new javax.swing.JPanel();
        jScrollPaneStock = new javax.swing.JScrollPane();
        jltStock = new javax.swing.JList<>();
        lblStock = new javax.swing.JLabel();
        panelInferior = new javax.swing.JPanel();
        lblNotas = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtNotas = new javax.swing.JTextArea();
        panelClientes = new javax.swing.JPanel();
        panelHerramientas = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtBuscarCliente = new javax.swing.JTextField();
        btnBuscarCliente = new javax.swing.JButton();
        btnRecargarClientes = new javax.swing.JButton();
        btnNuevoCliente = new javax.swing.JButton();
        btnHistorialCliente = new javax.swing.JButton();
        btnEliminarCliente = new javax.swing.JButton();
        jScrollPaneClientes = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();
        panelVehiculos = new javax.swing.JPanel();
        panelHerramientasVehiculos = new javax.swing.JPanel();
        etqGestionVehiculos = new javax.swing.JLabel();
        txtBuscarVehiculo = new javax.swing.JTextField();
        btnBuscarVehiculo = new javax.swing.JButton();
        btnRecargarVehiculos = new javax.swing.JButton();
        btnNuevoVehiculo = new javax.swing.JButton();
        btnInformeVehiculo = new javax.swing.JButton();
        btnEliminarVehiculo = new javax.swing.JButton();
        jScrollPaneVehiculos = new javax.swing.JScrollPane();
        tblVehiculo = new javax.swing.JTable();
        panelReparaciones = new javax.swing.JPanel();
        panelCuerpo = new javax.swing.JPanel();
        panelBarraHerramientas = new javax.swing.JPanel();
        lblGestionReparaciones = new javax.swing.JLabel();
        txtBuscarReparacion = new javax.swing.JTextField();
        btnBuscarReparacion = new javax.swing.JButton();
        btnRecargarReparaciones = new javax.swing.JButton();
        btnDetallesReparacion = new javax.swing.JButton();
        btnEstadoReparacion = new javax.swing.JButton();
        btnAsignarReparacion = new javax.swing.JButton();
        btnGenerarFactura = new javax.swing.JButton();
        lblFiltrarEstado = new javax.swing.JLabel();
        cbxFiltrarEstado = new javax.swing.JComboBox<>();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblReparaciones = new javax.swing.JTable();
        panelProveedores = new javax.swing.JPanel();
        panelHerramientasProveedores = new javax.swing.JPanel();
        etqGestionProveedores = new javax.swing.JLabel();
        txtBuscarProveedores = new javax.swing.JTextField();
        btnBuscarProveedores = new javax.swing.JButton();
        btnRecargarProveedores = new javax.swing.JButton();
        btnNuevoProveedores = new javax.swing.JButton();
        btnEliminarProveedores = new javax.swing.JButton();
        jScrollPaneProveedores = new javax.swing.JScrollPane();
        tblProveedores = new javax.swing.JTable();
        panelProductos = new javax.swing.JPanel();
        panelHerramientasProductos = new javax.swing.JPanel();
        etqGestionProductos = new javax.swing.JLabel();
        txtBuscarProductos = new javax.swing.JTextField();
        btnBuscarProductos = new javax.swing.JButton();
        btnRecargarProductos = new javax.swing.JButton();
        btnNuevoProductos = new javax.swing.JButton();
        btnAnadirStock = new javax.swing.JButton();
        btnEliminarProductos = new javax.swing.JButton();
        jScrollPaneProductos = new javax.swing.JScrollPane();
        tblProductos = new javax.swing.JTable();
        panelPresupuestos = new javax.swing.JPanel();
        panelHerramientasPresupuestos = new javax.swing.JPanel();
        etqGestionPresupuestos = new javax.swing.JLabel();
        txtBuscarPresupuestos = new javax.swing.JTextField();
        btnBuscarPresupuestos = new javax.swing.JButton();
        btnRecargarPresupuestos = new javax.swing.JButton();
        btnNuevoPresupuesto = new javax.swing.JButton();
        btnDetallesPresupuesto = new javax.swing.JButton();
        btnEstadoPresupuesto = new javax.swing.JButton();
        btnCrearOrdenReparacion = new javax.swing.JButton();
        btnImprimirPresupuesto = new javax.swing.JButton();
        btnEliminarPresupuestos = new javax.swing.JButton();
        jScrollPanePresupuestos = new javax.swing.JScrollPane();
        tblPresupuestos = new javax.swing.JTable();
        panelFacturas = new javax.swing.JPanel();
        panelHerramientasFacturas = new javax.swing.JPanel();
        etqGestionFacturas = new javax.swing.JLabel();
        txtBuscarFacturas = new javax.swing.JTextField();
        btnBuscarFacturas = new javax.swing.JButton();
        btnRecargarFacturas = new javax.swing.JButton();
        btnRegistrarPago = new javax.swing.JButton();
        btnVerFactura = new javax.swing.JButton();
        btnEliminarFacturas = new javax.swing.JButton();
        jScrollPaneFacturas = new javax.swing.JScrollPane();
        tblFacturas = new javax.swing.JTable();
        panelEmpleados = new javax.swing.JPanel();
        panelHerramientasEmpleados = new javax.swing.JPanel();
        etqGestionEmpleados = new javax.swing.JLabel();
        txtBuscarEmpleado = new javax.swing.JTextField();
        btnBuscarEmpleado = new javax.swing.JButton();
        btnRecargarEmpleados = new javax.swing.JButton();
        btnNuevoEmpleado = new javax.swing.JButton();
        btnEliminarEmpleado = new javax.swing.JButton();
        jScrollPaneEmpleado = new javax.swing.JScrollPane();
        tblEmpleados = new javax.swing.JTable();
        panelAyuda = new javax.swing.JPanel();
        panelCabeceraAyuda = new javax.swing.JPanel();
        etqTituloAyuda = new javax.swing.JLabel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        lstAyuda = new javax.swing.JList<>();
        jScrollPane5 = new javax.swing.JScrollPane();
        editorContenidoAyuda = new javax.swing.JEditorPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(new java.awt.Dimension(1200, 850));

        panelTitulo.setBackground(new java.awt.Color(33, 150, 243));

        etqLogo.setIcon(new javax.swing.ImageIcon("/mnt/0FEC087D0FEC087D/ProyectoDAM/Proyecto-DAM-Taller/AppTaller/src/main/resources/images/logo_apptaller_small.png")); // NOI18N

        etqTitulo.setFont(new java.awt.Font("Roboto", 1, 48)); // NOI18N
        etqTitulo.setForeground(new java.awt.Color(255, 255, 255));
        etqTitulo.setText("APP MI TALLER - INICIO");

        javax.swing.GroupLayout panelTituloLayout = new javax.swing.GroupLayout(panelTitulo);
        panelTitulo.setLayout(panelTituloLayout);
        panelTituloLayout.setHorizontalGroup(
            panelTituloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTituloLayout.createSequentialGroup()
                .addComponent(etqLogo)
                .addGap(319, 319, 319)
                .addComponent(etqTitulo)
                .addContainerGap(480, Short.MAX_VALUE))
        );
        panelTituloLayout.setVerticalGroup(
            panelTituloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(etqLogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panelTituloLayout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addComponent(etqTitulo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(panelTitulo, java.awt.BorderLayout.PAGE_START);

        panelNavegacion.setBackground(new java.awt.Color(250, 250, 250));
        panelNavegacion.setPreferredSize(new java.awt.Dimension(180, 100));
        panelNavegacion.setLayout(new java.awt.GridLayout(11, 0, 0, 5));

        btnInicio.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnInicio.setText("Inicio");
        btnInicio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInicioActionPerformed(evt);
            }
        });
        panelNavegacion.add(btnInicio);

        btnClientes.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnClientes.setText("Clientes");
        btnClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClientesActionPerformed(evt);
            }
        });
        panelNavegacion.add(btnClientes);

        btnVehiculos.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnVehiculos.setText("Vehículos");
        btnVehiculos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVehiculosActionPerformed(evt);
            }
        });
        panelNavegacion.add(btnVehiculos);

        btnReparaciones.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        btnReparaciones.setText("Reparaciones");
        btnReparaciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReparacionesActionPerformed(evt);
            }
        });
        panelNavegacion.add(btnReparaciones);

        btnProveedores.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnProveedores.setText("Proveedores");
        btnProveedores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProveedoresActionPerformed(evt);
            }
        });
        panelNavegacion.add(btnProveedores);

        btnProductos.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnProductos.setText("Productos");
        btnProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductosActionPerformed(evt);
            }
        });
        panelNavegacion.add(btnProductos);

        btnPresupuestos.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnPresupuestos.setText("Presupuestos");
        btnPresupuestos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPresupuestosActionPerformed(evt);
            }
        });
        panelNavegacion.add(btnPresupuestos);

        btnFacturas.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnFacturas.setText("Facturas");
        btnFacturas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFacturasActionPerformed(evt);
            }
        });
        panelNavegacion.add(btnFacturas);

        btnEmpleados.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        btnEmpleados.setText("Empleados");
        btnEmpleados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmpleadosActionPerformed(evt);
            }
        });
        panelNavegacion.add(btnEmpleados);

        btnAyuda.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnAyuda.setText("Ayuda");
        btnAyuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAyudaActionPerformed(evt);
            }
        });
        panelNavegacion.add(btnAyuda);

        btnSalir.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnSalir.setText("Salir");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });
        panelNavegacion.add(btnSalir);

        getContentPane().add(panelNavegacion, java.awt.BorderLayout.LINE_START);

        panelPrincipal.setBackground(new java.awt.Color(250, 250, 250));
        panelPrincipal.setLayout(new java.awt.CardLayout());

        panelInicio.setBackground(new java.awt.Color(245, 245, 245));
        panelInicio.setLayout(new java.awt.GridBagLayout());

        panelBienvenida.setBackground(new java.awt.Color(255, 255, 255));
        panelBienvenida.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(224, 224, 224)), javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panelBienvenida.setMinimumSize(new java.awt.Dimension(100, 100));
        panelBienvenida.setPreferredSize(new java.awt.Dimension(0, 100));
        panelBienvenida.setLayout(new java.awt.BorderLayout(0, 20));

        lblBienvenida.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        lblBienvenida.setText("BIENVENIDO, JOSÉ LUIS");
        panelBienvenida.add(lblBienvenida, java.awt.BorderLayout.LINE_START);

        lblReloj.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        lblReloj.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblReloj.setText("Cargando fecha...");
        lblReloj.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 20));
        panelBienvenida.add(lblReloj, java.awt.BorderLayout.LINE_END);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        panelInicio.add(panelBienvenida, gridBagConstraints);

        panelEstadoTaller.setBackground(new java.awt.Color(255, 255, 255));
        panelEstadoTaller.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(224, 224, 224)), javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panelEstadoTaller.setMinimumSize(new java.awt.Dimension(100, 150));
        panelEstadoTaller.setPreferredSize(new java.awt.Dimension(100, 280));
        panelEstadoTaller.setLayout(new java.awt.BorderLayout());

        lblEtiquetaVehiculo.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        lblEtiquetaVehiculo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEtiquetaVehiculo.setText("Vehículos en Taller");
        panelEstadoTaller.add(lblEtiquetaVehiculo, java.awt.BorderLayout.PAGE_END);

        lblNumeroVehiculos.setFont(new java.awt.Font("Roboto", 0, 36)); // NOI18N
        lblNumeroVehiculos.setForeground(new java.awt.Color(0, 0, 255));
        lblNumeroVehiculos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        panelEstadoTaller.add(lblNumeroVehiculos, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.33;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panelInicio.add(panelEstadoTaller, gridBagConstraints);

        panelAgenda.setBackground(new java.awt.Color(255, 255, 255));
        panelAgenda.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(224, 224, 224)), javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panelAgenda.setMinimumSize(new java.awt.Dimension(100, 150));
        panelAgenda.setPreferredSize(new java.awt.Dimension(100, 280));
        panelAgenda.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setBorder(null);
        jScrollPane1.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N

        jltAgenda.setBorder(null);
        jScrollPane1.setViewportView(jltAgenda);

        panelAgenda.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        lblEtiquetaAgenda.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        lblEtiquetaAgenda.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEtiquetaAgenda.setText("Agenda del día");
        panelAgenda.add(lblEtiquetaAgenda, java.awt.BorderLayout.PAGE_END);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.33;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panelInicio.add(panelAgenda, gridBagConstraints);

        panelStock.setBackground(new java.awt.Color(255, 255, 255));
        panelStock.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(224, 224, 224)), javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panelStock.setMinimumSize(new java.awt.Dimension(100, 150));
        panelStock.setPreferredSize(new java.awt.Dimension(100, 280));
        panelStock.setLayout(new java.awt.BorderLayout());

        jScrollPaneStock.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPaneStock.setBorder(null);

        jltStock.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPaneStock.setViewportView(jltStock);

        panelStock.add(jScrollPaneStock, java.awt.BorderLayout.CENTER);

        lblStock.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        lblStock.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStock.setText("Stock");
        panelStock.add(lblStock, java.awt.BorderLayout.PAGE_END);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.33;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panelInicio.add(panelStock, gridBagConstraints);

        panelInferior.setBackground(new java.awt.Color(255, 255, 255));
        panelInferior.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(224, 224, 224)), javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panelInferior.setLayout(new java.awt.BorderLayout());

        lblNotas.setFont(new java.awt.Font("Roboto", 1, 13)); // NOI18N
        lblNotas.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNotas.setText("Notas Rápidas / Tareas");
        lblNotas.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        panelInferior.add(lblNotas, java.awt.BorderLayout.PAGE_START);

        jScrollPane3.setBorder(null);

        txtNotas.setColumns(20);
        txtNotas.setLineWrap(true);
        txtNotas.setRows(5);
        txtNotas.setWrapStyleWord(true);
        jScrollPane3.setViewportView(txtNotas);

        panelInferior.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelInicio.add(panelInferior, gridBagConstraints);

        panelPrincipal.add(panelInicio, "cardInicio");

        panelClientes.setBackground(new java.awt.Color(250, 250, 250));
        panelClientes.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelClientes.setLayout(new java.awt.BorderLayout());

        panelHerramientas.setBackground(new java.awt.Color(255, 255, 255));
        panelHerramientas.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 15, 5));

        jLabel1.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel1.setText("Gestión de Clientes");
        panelHerramientas.add(jLabel1);

        txtBuscarCliente.setColumns(20);
        txtBuscarCliente.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        txtBuscarCliente.setToolTipText("Buscar por nombre o DNI");
        txtBuscarCliente.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(33, 150, 243)));
        panelHerramientas.add(txtBuscarCliente);

        btnBuscarCliente.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnBuscarCliente.setText("Buscar");
        btnBuscarCliente.setFocusPainted(false);
        panelHerramientas.add(btnBuscarCliente);

        btnRecargarClientes.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        btnRecargarClientes.setText("Recargar");
        panelHerramientas.add(btnRecargarClientes);

        btnNuevoCliente.setBackground(new java.awt.Color(33, 150, 243));
        btnNuevoCliente.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnNuevoCliente.setText("Nuevo");
        btnNuevoCliente.setFocusPainted(false);
        panelHerramientas.add(btnNuevoCliente);

        btnHistorialCliente.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        btnHistorialCliente.setText("Historial");
        panelHerramientas.add(btnHistorialCliente);

        btnEliminarCliente.setBackground(new java.awt.Color(229, 57, 53));
        btnEliminarCliente.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnEliminarCliente.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarCliente.setText("Eliminar");
        btnEliminarCliente.setFocusPainted(false);
        panelHerramientas.add(btnEliminarCliente);

        panelClientes.add(panelHerramientas, java.awt.BorderLayout.PAGE_START);

        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "DNI", "Nombre", "Apellidos", "Dirección", "Teléfono", "Email"
            }
        ));
        jScrollPaneClientes.setViewportView(tblClientes);

        panelClientes.add(jScrollPaneClientes, java.awt.BorderLayout.CENTER);

        panelPrincipal.add(panelClientes, "cardClientes");

        panelVehiculos.setBackground(new java.awt.Color(250, 250, 250));
        panelVehiculos.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelVehiculos.setLayout(new java.awt.BorderLayout());

        panelHerramientasVehiculos.setBackground(new java.awt.Color(255, 255, 255));
        panelHerramientasVehiculos.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 15, 5));

        etqGestionVehiculos.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        etqGestionVehiculos.setText("Gestión de Vehículos");
        panelHerramientasVehiculos.add(etqGestionVehiculos);

        txtBuscarVehiculo.setColumns(20);
        txtBuscarVehiculo.setToolTipText("Buscar por nombre o DNI");
        txtBuscarVehiculo.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(33, 150, 243)));
        panelHerramientasVehiculos.add(txtBuscarVehiculo);

        btnBuscarVehiculo.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnBuscarVehiculo.setText("Buscar");
        panelHerramientasVehiculos.add(btnBuscarVehiculo);

        btnRecargarVehiculos.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        btnRecargarVehiculos.setText("Recargar");
        panelHerramientasVehiculos.add(btnRecargarVehiculos);

        btnNuevoVehiculo.setBackground(new java.awt.Color(33, 150, 243));
        btnNuevoVehiculo.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnNuevoVehiculo.setForeground(new java.awt.Color(255, 255, 255));
        btnNuevoVehiculo.setText("Nuevo");
        panelHerramientasVehiculos.add(btnNuevoVehiculo);

        btnInformeVehiculo.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnInformeVehiculo.setText("Informe");
        panelHerramientasVehiculos.add(btnInformeVehiculo);

        btnEliminarVehiculo.setBackground(new java.awt.Color(229, 57, 53));
        btnEliminarVehiculo.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnEliminarVehiculo.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarVehiculo.setText("Eliminar");
        panelHerramientasVehiculos.add(btnEliminarVehiculo);

        panelVehiculos.add(panelHerramientasVehiculos, java.awt.BorderLayout.PAGE_START);

        tblVehiculo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Bastidor", "Matrícula", "Marca", "Modelo", "Color", "Propietario"
            }
        ));
        jScrollPaneVehiculos.setViewportView(tblVehiculo);

        panelVehiculos.add(jScrollPaneVehiculos, java.awt.BorderLayout.CENTER);

        panelPrincipal.add(panelVehiculos, "cardVehiculos");

        panelReparaciones.setLayout(new java.awt.BorderLayout());

        panelCuerpo.setLayout(new java.awt.BorderLayout());

        lblGestionReparaciones.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        lblGestionReparaciones.setText("Gestión de Reparaciones");
        panelBarraHerramientas.add(lblGestionReparaciones);

        txtBuscarReparacion.setColumns(20);
        txtBuscarReparacion.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        panelBarraHerramientas.add(txtBuscarReparacion);

        btnBuscarReparacion.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnBuscarReparacion.setText("Buscar");
        panelBarraHerramientas.add(btnBuscarReparacion);

        btnRecargarReparaciones.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnRecargarReparaciones.setText("Recargar");
        panelBarraHerramientas.add(btnRecargarReparaciones);

        btnDetallesReparacion.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnDetallesReparacion.setText("Detalles");
        panelBarraHerramientas.add(btnDetallesReparacion);

        btnEstadoReparacion.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnEstadoReparacion.setText("Estado");
        panelBarraHerramientas.add(btnEstadoReparacion);

        btnAsignarReparacion.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnAsignarReparacion.setText("Asignar");
        panelBarraHerramientas.add(btnAsignarReparacion);

        btnGenerarFactura.setBackground(new java.awt.Color(33, 150, 243));
        btnGenerarFactura.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        btnGenerarFactura.setText("Generar Factura");
        panelBarraHerramientas.add(btnGenerarFactura);

        lblFiltrarEstado.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        lblFiltrarEstado.setText("Filtrar estado: ");
        panelBarraHerramientas.add(lblFiltrarEstado);

        cbxFiltrarEstado.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        cbxFiltrarEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS", "EN_COLA", "EN_PROCESO", "FINALIZADA" }));
        panelBarraHerramientas.add(cbxFiltrarEstado);

        panelCuerpo.add(panelBarraHerramientas, java.awt.BorderLayout.PAGE_START);

        tblReparaciones.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        tblReparaciones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Vehículo", "Cliente", "Mecánico", "Prioridad", "Estado", "Fecha Entrada"
            }
        ));
        jScrollPane6.setViewportView(tblReparaciones);

        panelCuerpo.add(jScrollPane6, java.awt.BorderLayout.CENTER);

        panelReparaciones.add(panelCuerpo, java.awt.BorderLayout.CENTER);

        panelPrincipal.add(panelReparaciones, "cardReparaciones");

        panelProveedores.setBackground(new java.awt.Color(250, 250, 250));
        panelProveedores.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelProveedores.setLayout(new java.awt.BorderLayout());

        panelHerramientasProveedores.setBackground(new java.awt.Color(255, 255, 255));
        panelHerramientasProveedores.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 15, 5));

        etqGestionProveedores.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        etqGestionProveedores.setText("Gestión de Proveedores");
        panelHerramientasProveedores.add(etqGestionProveedores);

        txtBuscarProveedores.setColumns(20);
        txtBuscarProveedores.setToolTipText("Buscar por nombre o DNI");
        txtBuscarProveedores.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(33, 150, 243)));
        panelHerramientasProveedores.add(txtBuscarProveedores);

        btnBuscarProveedores.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnBuscarProveedores.setText("Buscar");
        panelHerramientasProveedores.add(btnBuscarProveedores);

        btnRecargarProveedores.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnRecargarProveedores.setText("Recargar");
        panelHerramientasProveedores.add(btnRecargarProveedores);

        btnNuevoProveedores.setBackground(new java.awt.Color(33, 150, 243));
        btnNuevoProveedores.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnNuevoProveedores.setForeground(new java.awt.Color(255, 255, 255));
        btnNuevoProveedores.setText("Nuevo");
        panelHerramientasProveedores.add(btnNuevoProveedores);

        btnEliminarProveedores.setBackground(new java.awt.Color(229, 57, 53));
        btnEliminarProveedores.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnEliminarProveedores.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarProveedores.setText("Eliminar");
        panelHerramientasProveedores.add(btnEliminarProveedores);

        panelProveedores.add(panelHerramientasProveedores, java.awt.BorderLayout.PAGE_START);

        tblProveedores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "CIF", "Nombre", "Dirección", "Teléfono", "Email", "Contacto", "Web"
            }
        ));
        jScrollPaneProveedores.setViewportView(tblProveedores);

        panelProveedores.add(jScrollPaneProveedores, java.awt.BorderLayout.CENTER);

        panelPrincipal.add(panelProveedores, "cardProveedores");

        panelProductos.setBackground(new java.awt.Color(250, 250, 250));
        panelProductos.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelProductos.setLayout(new java.awt.BorderLayout());

        panelHerramientasProductos.setBackground(new java.awt.Color(255, 255, 255));
        panelHerramientasProductos.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 15, 5));

        etqGestionProductos.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        etqGestionProductos.setText("Gestión de Productos");
        panelHerramientasProductos.add(etqGestionProductos);

        txtBuscarProductos.setColumns(20);
        txtBuscarProductos.setToolTipText("Buscar por nombre o DNI");
        txtBuscarProductos.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(33, 150, 243)));
        panelHerramientasProductos.add(txtBuscarProductos);

        btnBuscarProductos.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnBuscarProductos.setText("Buscar");
        panelHerramientasProductos.add(btnBuscarProductos);

        btnRecargarProductos.setText("Recargar");
        panelHerramientasProductos.add(btnRecargarProductos);

        btnNuevoProductos.setBackground(new java.awt.Color(33, 150, 243));
        btnNuevoProductos.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnNuevoProductos.setForeground(new java.awt.Color(255, 255, 255));
        btnNuevoProductos.setText("Nuevo");
        panelHerramientasProductos.add(btnNuevoProductos);

        btnAnadirStock.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnAnadirStock.setText("+ Añadir Unidades");
        panelHerramientasProductos.add(btnAnadirStock);

        btnEliminarProductos.setBackground(new java.awt.Color(229, 57, 53));
        btnEliminarProductos.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnEliminarProductos.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarProductos.setText("Eliminar");
        panelHerramientasProductos.add(btnEliminarProductos);

        panelProductos.add(panelHerramientasProductos, java.awt.BorderLayout.PAGE_START);

        tblProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID Producto", "Nombre", "Descripción", "Categoría", "Stock", "Stock minimo", "Precio compra", "PVP venta", "Proveedor", "Fecha alta"
            }
        ));
        jScrollPaneProductos.setViewportView(tblProductos);

        panelProductos.add(jScrollPaneProductos, java.awt.BorderLayout.CENTER);

        panelPrincipal.add(panelProductos, "cardProductos");

        panelPresupuestos.setBackground(new java.awt.Color(250, 250, 250));
        panelPresupuestos.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelPresupuestos.setLayout(new java.awt.BorderLayout());

        panelHerramientasPresupuestos.setBackground(new java.awt.Color(255, 255, 255));
        panelHerramientasPresupuestos.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 15, 5));

        etqGestionPresupuestos.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        etqGestionPresupuestos.setText("Gestión de Presupuestos");
        panelHerramientasPresupuestos.add(etqGestionPresupuestos);

        txtBuscarPresupuestos.setColumns(20);
        txtBuscarPresupuestos.setToolTipText("Buscar por nombre o DNI");
        txtBuscarPresupuestos.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(33, 150, 243)));
        panelHerramientasPresupuestos.add(txtBuscarPresupuestos);

        btnBuscarPresupuestos.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnBuscarPresupuestos.setText("Buscar");
        panelHerramientasPresupuestos.add(btnBuscarPresupuestos);

        btnRecargarPresupuestos.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnRecargarPresupuestos.setText("Recargar");
        panelHerramientasPresupuestos.add(btnRecargarPresupuestos);

        btnNuevoPresupuesto.setBackground(new java.awt.Color(33, 150, 243));
        btnNuevoPresupuesto.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnNuevoPresupuesto.setForeground(new java.awt.Color(255, 255, 255));
        btnNuevoPresupuesto.setText("Nuevo");
        panelHerramientasPresupuestos.add(btnNuevoPresupuesto);

        btnDetallesPresupuesto.setText("Ver Detalles");
        panelHerramientasPresupuestos.add(btnDetallesPresupuesto);

        btnEstadoPresupuesto.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnEstadoPresupuesto.setText("Cambiar Estado");
        panelHerramientasPresupuestos.add(btnEstadoPresupuesto);

        btnCrearOrdenReparacion.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnCrearOrdenReparacion.setText("Crear Orden Reparación");
        panelHerramientasPresupuestos.add(btnCrearOrdenReparacion);

        btnImprimirPresupuesto.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnImprimirPresupuesto.setText("Imprimir");
        panelHerramientasPresupuestos.add(btnImprimirPresupuesto);

        btnEliminarPresupuestos.setBackground(new java.awt.Color(229, 57, 53));
        btnEliminarPresupuestos.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnEliminarPresupuestos.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarPresupuestos.setText("Eliminar");
        panelHerramientasPresupuestos.add(btnEliminarPresupuestos);

        panelPresupuestos.add(panelHerramientasPresupuestos, java.awt.BorderLayout.PAGE_START);

        tblPresupuestos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Nº Presupuesto", "Fecha", "Cliente", "Vehículo", "Total (€)", "Estado (Pendiente/Aceptado)"
            }
        ));
        jScrollPanePresupuestos.setViewportView(tblPresupuestos);

        panelPresupuestos.add(jScrollPanePresupuestos, java.awt.BorderLayout.CENTER);

        panelPrincipal.add(panelPresupuestos, "cardPresupuestos");

        panelFacturas.setBackground(new java.awt.Color(250, 250, 250));
        panelFacturas.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelFacturas.setLayout(new java.awt.BorderLayout());

        panelHerramientasFacturas.setBackground(new java.awt.Color(255, 255, 255));
        panelHerramientasFacturas.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 15, 5));

        etqGestionFacturas.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        etqGestionFacturas.setText("Gestión de Facturas");
        panelHerramientasFacturas.add(etqGestionFacturas);

        txtBuscarFacturas.setColumns(20);
        txtBuscarFacturas.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        txtBuscarFacturas.setToolTipText("Buscar por Nº Factura, fecha o nombre");
        txtBuscarFacturas.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(33, 150, 243)));
        panelHerramientasFacturas.add(txtBuscarFacturas);

        btnBuscarFacturas.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnBuscarFacturas.setText("Buscar");
        panelHerramientasFacturas.add(btnBuscarFacturas);

        btnRecargarFacturas.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnRecargarFacturas.setText("Recargar");
        panelHerramientasFacturas.add(btnRecargarFacturas);

        btnRegistrarPago.setBackground(new java.awt.Color(33, 150, 243));
        btnRegistrarPago.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnRegistrarPago.setForeground(new java.awt.Color(255, 255, 255));
        btnRegistrarPago.setText("Registrar Pago");
        panelHerramientasFacturas.add(btnRegistrarPago);

        btnVerFactura.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnVerFactura.setText("Ver Factura");
        panelHerramientasFacturas.add(btnVerFactura);

        btnEliminarFacturas.setBackground(new java.awt.Color(229, 57, 53));
        btnEliminarFacturas.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnEliminarFacturas.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarFacturas.setText("Eliminar");
        panelHerramientasFacturas.add(btnEliminarFacturas);

        panelFacturas.add(panelHerramientasFacturas, java.awt.BorderLayout.PAGE_START);

        tblFacturas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Nº Factura", "Fecha", "Cliente", "Base imponible", "IVA", "Total (€)"
            }
        ));
        jScrollPaneFacturas.setViewportView(tblFacturas);

        panelFacturas.add(jScrollPaneFacturas, java.awt.BorderLayout.CENTER);

        panelPrincipal.add(panelFacturas, "cardFacturas");

        panelEmpleados.setBackground(new java.awt.Color(250, 250, 250));
        panelEmpleados.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelEmpleados.setLayout(new java.awt.BorderLayout());

        panelHerramientasEmpleados.setBackground(new java.awt.Color(255, 255, 255));
        panelHerramientasEmpleados.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 15, 5));

        etqGestionEmpleados.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        etqGestionEmpleados.setText("Gestión de Empleados");
        panelHerramientasEmpleados.add(etqGestionEmpleados);

        txtBuscarEmpleado.setColumns(20);
        txtBuscarEmpleado.setToolTipText("Buscar por nombre o DNI");
        txtBuscarEmpleado.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(33, 150, 243)));
        panelHerramientasEmpleados.add(txtBuscarEmpleado);

        btnBuscarEmpleado.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnBuscarEmpleado.setText("Buscar");
        btnBuscarEmpleado.setFocusPainted(false);
        panelHerramientasEmpleados.add(btnBuscarEmpleado);

        btnRecargarEmpleados.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnRecargarEmpleados.setText("Recargar");
        panelHerramientasEmpleados.add(btnRecargarEmpleados);

        btnNuevoEmpleado.setBackground(new java.awt.Color(33, 150, 243));
        btnNuevoEmpleado.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnNuevoEmpleado.setText("Nuevo");
        btnNuevoEmpleado.setFocusPainted(false);
        panelHerramientasEmpleados.add(btnNuevoEmpleado);

        btnEliminarEmpleado.setBackground(new java.awt.Color(229, 57, 53));
        btnEliminarEmpleado.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        btnEliminarEmpleado.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarEmpleado.setText("Eliminar");
        btnEliminarEmpleado.setFocusPainted(false);
        panelHerramientasEmpleados.add(btnEliminarEmpleado);

        panelEmpleados.add(panelHerramientasEmpleados, java.awt.BorderLayout.PAGE_START);

        tblEmpleados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID_Empleado", "Id usuario", "DNI", "Nombre", "Apellidos", "Teléfono", "Email", "Dirección", "Cargo", "Fecha Alta", "Fecha Baja", "Salario base"
            }
        ));
        jScrollPaneEmpleado.setViewportView(tblEmpleados);

        panelEmpleados.add(jScrollPaneEmpleado, java.awt.BorderLayout.CENTER);

        panelPrincipal.add(panelEmpleados, "cardEmpleados");

        panelAyuda.setBackground(new java.awt.Color(245, 245, 245));
        panelAyuda.setLayout(new java.awt.BorderLayout());

        panelCabeceraAyuda.setBackground(new java.awt.Color(255, 255, 255));

        etqTituloAyuda.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        etqTituloAyuda.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        etqTituloAyuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icono_ayuda_128.png"))); // NOI18N
        etqTituloAyuda.setText("Centro de Ayuda y Soporte");

        javax.swing.GroupLayout panelCabeceraAyudaLayout = new javax.swing.GroupLayout(panelCabeceraAyuda);
        panelCabeceraAyuda.setLayout(panelCabeceraAyudaLayout);
        panelCabeceraAyudaLayout.setHorizontalGroup(
            panelCabeceraAyudaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCabeceraAyudaLayout.createSequentialGroup()
                .addGap(386, 386, 386)
                .addComponent(etqTituloAyuda)
                .addContainerGap(597, Short.MAX_VALUE))
        );
        panelCabeceraAyudaLayout.setVerticalGroup(
            panelCabeceraAyudaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelCabeceraAyudaLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(etqTituloAyuda)
                .addGap(38, 38, 38))
        );

        panelAyuda.add(panelCabeceraAyuda, java.awt.BorderLayout.PAGE_START);

        jSplitPane1.setDividerLocation(250);

        jScrollPane4.setMinimumSize(new java.awt.Dimension(250, 250));

        lstAyuda.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        lstAyuda.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Primeros Pasos", "Gestión de Clientes", "Cómo crear una Factura", "Control de Stock", "Gestión de Empleados" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        lstAyuda.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstAyuda.setMinimumSize(new java.awt.Dimension(250, 0));
        jScrollPane4.setViewportView(lstAyuda);

        jSplitPane1.setLeftComponent(jScrollPane4);

        editorContenidoAyuda.setEditable(false);
        editorContenidoAyuda.setBackground(new java.awt.Color(255, 255, 255));
        editorContenidoAyuda.setContentType("text/html"); // NOI18N
        editorContenidoAyuda.setFont(new java.awt.Font("Roboto", 0, 13)); // NOI18N
        editorContenidoAyuda.setText("          <html>\n            <body style=\"font-family: sans-serif; padding: 20px;\">\n              <h1>Bienvenido a la Ayuda</h1>\n              <p>Selecciona un tema del menú de la izquierda.</p>\n            </body>\n          </html>\n");
        jScrollPane5.setViewportView(editorContenidoAyuda);

        jSplitPane1.setRightComponent(jScrollPane5);

        panelAyuda.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        panelPrincipal.add(panelAyuda, "cardAyuda");

        getContentPane().add(panelPrincipal, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnSalirActionPerformed

    private void btnClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClientesActionPerformed
        // Obtener el Layout de panelPrincipal
        CardLayout cardLayout = (CardLayout) panelPrincipal.getLayout();

        // Mostrar la tarjeta usando el "Card Name" que definimos
        cardLayout.show(panelPrincipal, "cardClientes");

        etqTitulo.setText("APP MI TALLER - CLIENTES");
    }//GEN-LAST:event_btnClientesActionPerformed

    private void btnInicioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInicioActionPerformed
        // Obtener el Layout de panelPrincipal
        CardLayout cardLayout = (CardLayout) panelPrincipal.getLayout();

        // Mostrar la tarjeta usando el "Card Name" que definimos
        cardLayout.show(panelPrincipal, "cardInicio");

        etqTitulo.setText("APP MI TALLER - INICIO");

        // Refrescamos los datos del panel de stcok bajo cada vez que el usuario hace clic en el botón Inicio
        refrescarAlertasStock();
        actualizarContadorVehiculos();
    }//GEN-LAST:event_btnInicioActionPerformed

    private void btnVehiculosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVehiculosActionPerformed
        // Obtener el Layout de panelPrincipal
        CardLayout cardLayout = (CardLayout) panelPrincipal.getLayout();

        // Mostrar la tarjeta usando el "Card Name" que definimos
        cardLayout.show(panelPrincipal, "cardVehiculos");

        etqTitulo.setText("APP MI TALLER - VEHÍCULOS");
    }//GEN-LAST:event_btnVehiculosActionPerformed

    private void btnProveedoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProveedoresActionPerformed
        // Obtener el Layout de panelPrincipal
        CardLayout cardLayout = (CardLayout) panelPrincipal.getLayout();

        // Mostrar la tarjeta usando el "Card Name" que definimos
        cardLayout.show(panelPrincipal, "cardProveedores");

        etqTitulo.setText("APP MI TALLER - PROVEEDORES");
    }//GEN-LAST:event_btnProveedoresActionPerformed

    private void btnProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductosActionPerformed
        // Obtener el Layout de panelPrincipal
        CardLayout cardLayout = (CardLayout) panelPrincipal.getLayout();

        // Mostrar la tarjeta usando el "Card Name" que definimos
        cardLayout.show(panelPrincipal, "cardProductos");

        etqTitulo.setText("APP MI TALLER - PRODUCTOS");
    }//GEN-LAST:event_btnProductosActionPerformed

    private void btnPresupuestosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPresupuestosActionPerformed
        // Obtener el Layout de panelPrincipal
        CardLayout cardLayout = (CardLayout) panelPrincipal.getLayout();

        // Mostrar la tarjeta usando el "Card Name" que definimos
        cardLayout.show(panelPrincipal, "cardPresupuestos");

        etqTitulo.setText("APP MI TALLER - PRESUPUESTOS");

        ctrlPresupuestos.cargarTablaPresupuestos();
    }//GEN-LAST:event_btnPresupuestosActionPerformed

    private void btnFacturasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFacturasActionPerformed
        // Obtener el Layout de panelPrincipal
        CardLayout cardLayout = (CardLayout) panelPrincipal.getLayout();

        // Mostrar la tarjeta usando el "Card Name" que definimos
        cardLayout.show(panelPrincipal, "cardFacturas");

        etqTitulo.setText("APP MI TALLER - FACTURAS");
        
        ctrlFacturas.cargarTablaFacturas();
    }//GEN-LAST:event_btnFacturasActionPerformed

    private void btnAyudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAyudaActionPerformed
        // Obtener el Layout de panelPrincipal
        CardLayout cardLayout = (CardLayout) panelPrincipal.getLayout();

        // Mostrar la tarjeta usando el "Card Name" que definimos
        cardLayout.show(panelPrincipal, "cardAyuda");

        etqTitulo.setText("APP MI TALLER - AYUDA");
    }//GEN-LAST:event_btnAyudaActionPerformed

    private void btnEmpleadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmpleadosActionPerformed
        // Obtener el Layout de panelPrincipal
        CardLayout cardLayout = (CardLayout) panelPrincipal.getLayout();

        // Mostrar la tarjeta usando el "Card Name" que definimos
        cardLayout.show(panelPrincipal, "cardEmpleados");

        etqTitulo.setText("APP MI TALLER - EMPLEADOS");
    }//GEN-LAST:event_btnEmpleadosActionPerformed

    private void btnReparacionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReparacionesActionPerformed
        // Obtener el Layout de panelPrincipal
        CardLayout cardLayout = (CardLayout) panelPrincipal.getLayout();

        // Mostrar la tarjeta usando el "Card Name" que definimos
        cardLayout.show(panelPrincipal, "cardReparaciones");

        etqTitulo.setText("APP MI TALLER - REPARACIONES");

        // Pedimos al controlador que nos dé las estadísticas actuales
        Map<String, Integer> stats = ctrlReparaciones.obtenerEstadisticas();
        
        // Carga la tabla cada vez que se accede al panel Reparaciones
        ctrlReparaciones.cargarReparaciones();
    }//GEN-LAST:event_btnReparacionesActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAnadirStock;
    private javax.swing.JButton btnAsignarReparacion;
    private javax.swing.JButton btnAyuda;
    private javax.swing.JButton btnBuscarCliente;
    private javax.swing.JButton btnBuscarEmpleado;
    private javax.swing.JButton btnBuscarFacturas;
    private javax.swing.JButton btnBuscarPresupuestos;
    private javax.swing.JButton btnBuscarProductos;
    private javax.swing.JButton btnBuscarProveedores;
    private javax.swing.JButton btnBuscarReparacion;
    private javax.swing.JButton btnBuscarVehiculo;
    private javax.swing.JButton btnClientes;
    private javax.swing.JButton btnCrearOrdenReparacion;
    private javax.swing.JButton btnDetallesPresupuesto;
    private javax.swing.JButton btnDetallesReparacion;
    private javax.swing.JButton btnEliminarCliente;
    private javax.swing.JButton btnEliminarEmpleado;
    private javax.swing.JButton btnEliminarFacturas;
    private javax.swing.JButton btnEliminarPresupuestos;
    private javax.swing.JButton btnEliminarProductos;
    private javax.swing.JButton btnEliminarProveedores;
    private javax.swing.JButton btnEliminarVehiculo;
    private javax.swing.JButton btnEmpleados;
    private javax.swing.JButton btnEstadoPresupuesto;
    private javax.swing.JButton btnEstadoReparacion;
    private javax.swing.JButton btnFacturas;
    private javax.swing.JButton btnGenerarFactura;
    private javax.swing.JButton btnHistorialCliente;
    private javax.swing.JButton btnImprimirPresupuesto;
    private javax.swing.JButton btnInformeVehiculo;
    private javax.swing.JButton btnInicio;
    private javax.swing.JButton btnNuevoCliente;
    private javax.swing.JButton btnNuevoEmpleado;
    private javax.swing.JButton btnNuevoPresupuesto;
    private javax.swing.JButton btnNuevoProductos;
    private javax.swing.JButton btnNuevoProveedores;
    private javax.swing.JButton btnNuevoVehiculo;
    private javax.swing.JButton btnPresupuestos;
    private javax.swing.JButton btnProductos;
    private javax.swing.JButton btnProveedores;
    private javax.swing.JButton btnRecargarClientes;
    private javax.swing.JButton btnRecargarEmpleados;
    private javax.swing.JButton btnRecargarFacturas;
    private javax.swing.JButton btnRecargarPresupuestos;
    private javax.swing.JButton btnRecargarProductos;
    private javax.swing.JButton btnRecargarProveedores;
    private javax.swing.JButton btnRecargarReparaciones;
    private javax.swing.JButton btnRecargarVehiculos;
    private javax.swing.JButton btnRegistrarPago;
    private javax.swing.JButton btnReparaciones;
    private javax.swing.JButton btnSalir;
    private javax.swing.JButton btnVehiculos;
    private javax.swing.JButton btnVerFactura;
    private javax.swing.JComboBox<String> cbxFiltrarEstado;
    private javax.swing.JEditorPane editorContenidoAyuda;
    private javax.swing.JLabel etqGestionEmpleados;
    private javax.swing.JLabel etqGestionFacturas;
    private javax.swing.JLabel etqGestionPresupuestos;
    private javax.swing.JLabel etqGestionProductos;
    private javax.swing.JLabel etqGestionProveedores;
    private javax.swing.JLabel etqGestionVehiculos;
    private javax.swing.JLabel etqLogo;
    private javax.swing.JLabel etqTitulo;
    private javax.swing.JLabel etqTituloAyuda;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPaneClientes;
    private javax.swing.JScrollPane jScrollPaneEmpleado;
    private javax.swing.JScrollPane jScrollPaneFacturas;
    private javax.swing.JScrollPane jScrollPanePresupuestos;
    private javax.swing.JScrollPane jScrollPaneProductos;
    private javax.swing.JScrollPane jScrollPaneProveedores;
    private javax.swing.JScrollPane jScrollPaneStock;
    private javax.swing.JScrollPane jScrollPaneVehiculos;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JList<String> jltAgenda;
    private javax.swing.JList<String> jltStock;
    private javax.swing.JLabel lblBienvenida;
    private javax.swing.JLabel lblEtiquetaAgenda;
    private javax.swing.JLabel lblEtiquetaVehiculo;
    private javax.swing.JLabel lblFiltrarEstado;
    private javax.swing.JLabel lblGestionReparaciones;
    private javax.swing.JLabel lblNotas;
    private javax.swing.JLabel lblNumeroVehiculos;
    private javax.swing.JLabel lblReloj;
    private javax.swing.JLabel lblStock;
    private javax.swing.JList<String> lstAyuda;
    private javax.swing.JPanel panelAgenda;
    private javax.swing.JPanel panelAyuda;
    private javax.swing.JPanel panelBarraHerramientas;
    private javax.swing.JPanel panelBienvenida;
    private javax.swing.JPanel panelCabeceraAyuda;
    private javax.swing.JPanel panelClientes;
    private javax.swing.JPanel panelCuerpo;
    private javax.swing.JPanel panelEmpleados;
    private javax.swing.JPanel panelEstadoTaller;
    private javax.swing.JPanel panelFacturas;
    private javax.swing.JPanel panelHerramientas;
    private javax.swing.JPanel panelHerramientasEmpleados;
    private javax.swing.JPanel panelHerramientasFacturas;
    private javax.swing.JPanel panelHerramientasPresupuestos;
    private javax.swing.JPanel panelHerramientasProductos;
    private javax.swing.JPanel panelHerramientasProveedores;
    private javax.swing.JPanel panelHerramientasVehiculos;
    private javax.swing.JPanel panelInferior;
    private javax.swing.JPanel panelInicio;
    private javax.swing.JPanel panelNavegacion;
    private javax.swing.JPanel panelPresupuestos;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JPanel panelProductos;
    private javax.swing.JPanel panelProveedores;
    private javax.swing.JPanel panelReparaciones;
    private javax.swing.JPanel panelStock;
    private javax.swing.JPanel panelTitulo;
    private javax.swing.JPanel panelVehiculos;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTable tblEmpleados;
    private javax.swing.JTable tblFacturas;
    private javax.swing.JTable tblPresupuestos;
    private javax.swing.JTable tblProductos;
    private javax.swing.JTable tblProveedores;
    private javax.swing.JTable tblReparaciones;
    private javax.swing.JTable tblVehiculo;
    private javax.swing.JTextField txtBuscarCliente;
    private javax.swing.JTextField txtBuscarEmpleado;
    private javax.swing.JTextField txtBuscarFacturas;
    private javax.swing.JTextField txtBuscarPresupuestos;
    private javax.swing.JTextField txtBuscarProductos;
    private javax.swing.JTextField txtBuscarProveedores;
    private javax.swing.JTextField txtBuscarReparacion;
    private javax.swing.JTextField txtBuscarVehiculo;
    private javax.swing.JTextArea txtNotas;
    // End of variables declaration//GEN-END:variables

    private void iniciarReloj() {
        // Actualiza cada 1000ms (1 segundo)
        new Timer(1000, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                SimpleDateFormat formato = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy - HH:mm:ss");
                String fecha = formato.format(new java.util.Date());
                lblReloj.setText(fecha.toUpperCase());
            }
        }).start();
    }


    /**
     * Consulta el DAO y refresca el JList del panel de Inicio con las alertas
     * de stock.
     */
    public void refrescarAlertasStock() {
        List<ProductoVO> listaBajoStock = productoDAO.listarBajoStock();

        // Creamos un modelo específico para JList (almacena Strings)
        DefaultListModel<String> modeloLista = new DefaultListModel<>();

        // Si no hay productos bajo stock, mostramos un mensaje tranquilizador
        if (listaBajoStock.isEmpty()) {
            modeloLista.addElement("✅ Inventario en niveles óptimos. No hay alertas de reposición.");
        } else {
            // Si hay problemas, recorremos la lista y damos formato al texto
            for (ProductoVO p : listaBajoStock) {
                // Formateamos el texto: "⚠️ Ref: ACE01 - Filtro de Aceite | Stock actual: 2 (Mínimo: 5)"
                String alertaFormateada = String.format("⚠️ Ref: %s - %s | Stock actual: %d (Mínimo: %d)",
                        p.getIdProducto(),
                        p.getNombre(),
                        p.getCantidadStock(),
                        p.getStockMinimo());

                modeloLista.addElement(alertaFormateada);
            }
        }

        // Aplicamos el modelo al componente JList real
        jltStock.setModel(modeloLista);
    }

    /**
     * Consulta el DAO y actualiza el indicador visual de vehículos en el
     * taller.
     */
    public void actualizarContadorVehiculos() {
        int cantidad = reparacionDAO.contarVehiculosEnTaller();

        // Actualizamos el texto de la etiqueta
        // Formateamos con un String.format para añadir ceros a la izquierda (ej: "05")
        lblNumeroVehiculos.setText(String.format("%02d", cantidad));
    }

    /**
     * Carga los trabajos pendientes en la lista de la agenda.
     */
    public void actualizarAgenda() {
        List<ReparacionVO> pendientes = reparacionDAO.listarTrabajosPendientes();

        DefaultListModel<String> modeloLista = new DefaultListModel<>();

        for (ReparacionVO r : pendientes) {
            String alertaFormateada = String.format("⚠️ ID Reparación: %d | Matrícula: %s | Avería: %s",
                    r.getIdReparacion(),
                    r.getVehiculoMatricula(),
                    r.getObservaciones() != null ? r.getObservaciones() : "Sin datos");

            modeloLista.addElement(alertaFormateada);
        }
        // Aplicamos el modelo al componente JList real
        jltAgenda.setModel(modeloLista);
    }

    
    private void configurarPlaceholder(javax.swing.JTextField textField, String placeholder) {
        // 1. Configuramos el estado inicial de la caja
        textField.setText(placeholder);
        textField.setForeground(java.awt.Color.GRAY);

        // 2. Le agregamos el comportamiento del foco mediante un Listener
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(java.awt.Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(java.awt.Color.GRAY);
                }
            }
        });
    }
    
    // ====================================
    // Métodos para que ControladorClientes 
    // pueda manejar los botones y a la
    // tabla del panel Clientes
    //======================================
    public JTable getTblClientes() {
        return tblClientes;
    }

    public JButton getBtnNuevoCliente() {
        return btnNuevoCliente;
    }

    public JButton getBtnEliminarCliente() {
        return btnEliminarCliente;
    }

    
    public JButton getBtnHistorialCliente() {
        return btnHistorialCliente;
    }
    

    public JButton getBtnBuscarCliente() {
        return btnBuscarCliente;
    }

    public JButton getBtnRecargarClientes() {
        return btnRecargarClientes;
    }

    public JTextField getTxtBuscarCliente() {
        return txtBuscarCliente;
    }

    // ======================================
    // Métodos para  que ControladorVehiculos
    // pueda manejar los botones y a la
    // tabla del panel Vehiculos
    // ======================================
    public JTable getTblVehiculo() {
        return tblVehiculo;
    }

    public JButton getBtnNuevoVehiculo() {
        return btnNuevoVehiculo;
    }

    public JButton getBtnEliminarVehiculo() {
        return btnEliminarVehiculo;
    }

    public JButton getBtnBuscarVehiculo() {
        return btnBuscarVehiculo;
    }

    public JButton getBtnRecargarVehiculos() {
        return btnRecargarVehiculos;
    }
    
    public JButton getBtnInformeVehiculo() {
        return btnInformeVehiculo;
    }

    public JTextField getTxtBuscarVehiculo() {
        return txtBuscarVehiculo;
    }

    // ======================================
    // Métodos para  que ControladorEmpleados
    // pueda manejar los botones y a la
    // tabla del panel Empleados
    // ======================================
    public JTable getTblEmpleado() {
        return tblEmpleados;
    }

    public JButton getBtnNuevoEmpleado() {
        return btnNuevoEmpleado;
    }

    public JButton getBtnEliminarEmpleado() {
        return btnEliminarEmpleado;
    }

    public JButton getBtnBuscarEmpleado() {
        return btnBuscarEmpleado;
    }
    
    public JTextField getTxtBuscarEmpleado() {
        return txtBuscarEmpleado;
    }
    
    public JButton getBtnRecargarEmpleados() {
        return btnRecargarEmpleados;
    }

    // ======================================
    // Métodos para que ControladorProveedores
    // pueda manejar los botones y a la
    // tabla del panel Proveedores
    // ======================================
    public JTable getTblProveedores() {
        return tblProveedores;
    }

    public JButton getBtnNuevoProveedor() {
        return btnNuevoProveedores;
    }

    public JButton getBtnEliminarProveedor() {
        return btnEliminarProveedores;
    }

    public JButton getBtnBuscarProveedor() {
        return btnBuscarProveedores;
    }

    public JTextField getTxtBuscarProveedores() {
        return txtBuscarProveedores;
    }

    public JButton getBtnRecargarProveedores() {
        return btnRecargarProveedores;
    }

    // ======================================
    // Métodos para que ControladorProductos
    // pueda manejar los botones y a la
    // tabla del panel Productos
    // ======================================
    public JTable getTblProductos() {
        return tblProductos;
    }

    public JButton getBtnNuevoProducto() {
        return btnNuevoProductos;
    }

    public JButton getBtnEliminarProducto() {
        return btnEliminarProductos;
    }

    public JButton getBtnBuscarProducto() {
        return btnBuscarProductos;
    }

    public JButton getBtnAnadirStock() {
        return btnAnadirStock;
    }

    public JTextField getTxtBuscarProductos() {
        return txtBuscarProductos;
    }

    public JButton getBtnRecargarProductos() {
        return btnRecargarProductos;
    }

    // ========================================
    // Métodos para que ControladorReparaciones
    // pueda manejar los botones y etiquetas
    // del panel Reparacion
    // ========================================
    public JButton getBtnBuscarReparacion() {
        return btnBuscarReparacion;
    }

    public JButton getBtnRecargarReparaciones() {
        return btnRecargarReparaciones;
    }
    
    public JButton getBtnDetallesReparacion() {
        return btnDetallesReparacion;
    }            
    
    public JButton getBtnEstadoReparacion() {
        return btnEstadoReparacion;
    }
    
    public JButton getBtnAsignarReparacion() {
        return btnAsignarReparacion;
    }
    
    public JButton getBtnGenerarFactura() {
        return btnGenerarFactura;
    }

    public JTable getTblReparaciones() {
        return tblReparaciones;
    }

    public JTextField getTxtBuscarReparacion() {
        return txtBuscarReparacion;
    }
    
    public JComboBox getCbxFiltrarEstado() {
        return cbxFiltrarEstado;
    }

    // ============================================
    // Métodos para que ControladorNuevoPresupuesto
    // pueda manejar los botones y a la
    // tabla del panel Presupuestos
    // ============================================
    public JButton getBtnNuevoPresupuesto() {
        return btnNuevoPresupuesto;
    }
    
    public JButton getBtnBuscarPresupuesto() {
        return btnBuscarPresupuestos;
    }

    public JButton getBtnRecargarPresupuestos() {
        return btnRecargarPresupuestos;
    }
    
    public JButton getBtnEliminarPresupuesto() {
        return btnEliminarPresupuestos;
    }

    public JTextField getTxtBuscarPresupuesto() {
        return txtBuscarPresupuestos;
    }
    
    public JTable getTblPresupuestos() {
        return tblPresupuestos;
    }
    
    
    // ========================================
    // Métodos para que ControladorFacturas
    // pueda manejar los botones y etiquetas
    // del panel Facturas
    // ========================================
    public JButton getBtnBuscarFactura() {
        return btnBuscarFacturas;
    }
    
    public JButton getBtnRecargarFactura() {
        return btnRecargarFacturas;
    }
    
    public JButton getBtnVerFactura() {
        return btnVerFactura;
    }
    
    public JButton getBtnRegistrarPago() {
        return btnRegistrarPago;
    }
    
    public JTable getTblFacturas() {
        return tblFacturas;
    }
    
    public JTextField getTxtBuscarFactura() {
        return txtBuscarFacturas;
    }
    
    
    // ========================================
    // Métodos para que ControladorPresupuestos
    // pueda manejar los botones y a la
    // tabla del panel Presupuestos
    // ========================================
    public JButton getBtnEstadoPresupuesto() {
        return btnEstadoPresupuesto;
    }
    
    public JButton getBtnImprimirPresupuesto() {
        return btnImprimirPresupuesto;
    }
    
    public JButton getBtnDetallesPresupuesto() {
        return btnDetallesPresupuesto;
    }
    
    public JButton getBtnCrearOrdenReparacion() {
        return btnCrearOrdenReparacion;
    }
    
}

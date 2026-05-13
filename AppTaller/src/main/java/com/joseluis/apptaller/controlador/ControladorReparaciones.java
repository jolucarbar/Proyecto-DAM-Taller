
package com.joseluis.apptaller.controlador;

import com.joseluis.apptaller.modelo.dao.EmpleadoDAO;
import com.joseluis.apptaller.modelo.dao.ReparacionDAO;
import com.joseluis.apptaller.modelo.vo.DetalleProductoVO;
import com.joseluis.apptaller.modelo.vo.EmpleadoVO;
import com.joseluis.apptaller.modelo.vo.ReparacionVO;
import com.joseluis.apptaller.vista.dialogos.DialogDetallesReparacion;
import com.joseluis.apptaller.vista.dialogos.DialogGenerarFactura;
import com.joseluis.apptaller.vista.ventanas.VentanaPrincipal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Controlador encargado de gestionar la lógica de negocio del módulo de Reparaciones.
 * Se encarga de conectar la interfaz visual de las reparaciones con la base
 * de datos (ReparacionDAO), creando órdenes de reparación y cambiando sus estados,
 * asignando mecánicos a la reparación, muestra el desglose de piezas, mano de 
 * obra y el historial del coche y calcula las sumas, base imponible e IVA 
 * listos para la factura.
 *  
 * @author José Luis Cárdenas Barroso
 * @info Proyecto Intermodular del Grado Superior DAM
 * @institution IES Augustóbriga
 */
public class ControladorReparaciones implements ActionListener {
    private ReparacionDAO modeloDAO;
    private VentanaPrincipal vista;

    public ControladorReparaciones() {
        this.modeloDAO = new ReparacionDAO();
    }
    
    
    public ControladorReparaciones(VentanaPrincipal vista, ReparacionDAO modeloDAO) {
        this.vista = vista;
        this.modeloDAO = new ReparacionDAO();
        
        if (this.vista != null) {
            // Extraemos el modelo de la tabla
            DefaultTableModel modelo = (DefaultTableModel) this.vista.getTblReparaciones().getModel();
            // Pasamos el el modelo al método
            llenarTablaReparaciones(modelo);
            
            initListeners();
        }
   }
        
    private void initListeners() {
        vista.getBtnBuscarReparacion().addActionListener(this);
        vista.getBtnRecargarReparaciones().addActionListener(this);
        vista.getBtnDetallesReparacion().addActionListener(this);
        vista.getBtnEstadoReparacion().addActionListener(this);
        vista.getBtnAsignarReparacion().addActionListener(this);
        vista.getBtnGenerarFactura().addActionListener(this);
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnBuscarReparacion()) {
            buscarReparacion();
        } else if (e.getSource() == vista.getBtnRecargarReparaciones()) {
            cargarReparaciones();
        } else if (e.getSource() == vista.getBtnDetallesReparacion()) {
            mostrarDetalles();
        } else if (e.getSource() == vista.getBtnEstadoReparacion()) {
            cambiarEstado();
        } else if (e.getSource() == vista.getBtnAsignarReparacion()) {
            asignarMecanico();
        } else if (e.getSource() == vista.getBtnGenerarFactura()) {
            generarFactura();
        }
    }
    
    
    /**
     * Llena el modelo de una JTable con los datos de las reparaciones activas.
     */
    public void llenarTablaReparaciones(DefaultTableModel modelo) {
        modelo.setRowCount(0); // Resetear tabla
        //modelo.setColumnIdentifiers(new String[]{"ID", "Matricula", "Cliente", "Mecánico", "Prioridad", "Estado", "Fecha"});

        List<Object[]> filas = modeloDAO.obtenerListaParaTabla();
        for (Object[] fila : filas) {
            modelo.addRow(fila);
        }
    }

    
    /**
     * Procesa el registro de una nueva reparación con validaciones previas.
     */
    public boolean guardarNuevaReparacion(ReparacionVO rep) {
        if (rep.getKilometrajeEntrada() < 0) return false; 
    
            rep.setVehiculoBastidor(rep.getVehiculoBastidor().toUpperCase());

            // Llamamos al método que copia los detalles
            return modeloDAO.insertarConDetalles(rep);
    }
    
    
    /**
     * Función que devuelva un objeto con todas las estadísticas necesarias 
     * para los paneles superiores.
     * @return 
     */
    public Map<String, Integer> obtenerEstadisticas() {
        Map<String, Integer> stats = new HashMap<>();

        // Contamos en BD y guardamos el resultado en una clave 
        stats.put("Urgente", modeloDAO.contarPorPrioridad("Urgente"));
        stats.put("Alta", modeloDAO.contarPorPrioridad("Alta"));
        stats.put("Normal", modeloDAO.contarPorPrioridad("Normal"));
        stats.put("Baja", modeloDAO.contarPorPrioridad("Baja"));
        stats.put("NA", modeloDAO.contarPorPrioridad("NA"));

        return stats;
    }
    
    
    /**
     * Filtra la tblReparaciones según el estado seleccionado en el cbxFiltrarEstado
     */
    public void filtrarTablaReparaciones(DefaultTableModel modelo, String estado) {
        modelo.setRowCount(0); // Limpiamos la tabla visual
        List<Object[]> filas = modeloDAO.obtenerListaFiltrada(estado);
        for (Object[] fila : filas) {
            modelo.addRow(fila);
        }
    }
    
    /**
     * 
     */
    private void cambiarEstado() {
        // Verificamos si hay una fila seleccionada
        int filaSeleccionada = vista.getTblReparaciones().getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor, selecciona una reparación de la tabla.");
            return;
        }

        // Obtenemos el ID de la reparación (está en la columna 0, ej: "REP-8")
        String idTexto = vista.getTblReparaciones().getValueAt(filaSeleccionada, 0).toString();
        int idReparacion = Integer.parseInt(idTexto.replace("REP-", ""));

        // Mostramos opciones de estado 
        String[] estados = {"EN_COLA", "EN_PROCESO", "PAUSADA", "FINALIZADA", "ENTREGADA"};
        String nuevoEstado = (String) JOptionPane.showInputDialog(
                vista,
                "Selecciona el nuevo estado:",
                "Actualizar Estado",
                JOptionPane.QUESTION_MESSAGE,
                null,
                estados,
                estados[0]
        );

        // Ejecutamos el cambio si el usuario no canceló
        if (nuevoEstado != null) {
            if (modeloDAO.actualizarEstado(idReparacion, nuevoEstado)) {
                DefaultTableModel modelo = (DefaultTableModel) this.vista.getTblReparaciones().getModel();
                llenarTablaReparaciones(modelo);
                JOptionPane.showMessageDialog(vista, "Estado actualizado correctamente.");
            } else {
                JOptionPane.showMessageDialog(vista, "Error al actualizar el estado en la base de datos.");
            }
        }
    }
    
    
    /**
     * Asigna un mecánico y refresca la tabla manteniendo el filtro actual.
     */
    private void asignarMecanico() {
        // Verificamos si hay una fila seleccionada
        int filaSeleccionada = vista.getTblReparaciones().getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor, selecciona una reparación de la tabla para asignar un mecánico.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtenemos el ID de la reparación (Columna 0)
        String idTexto = vista.getTblReparaciones().getValueAt(filaSeleccionada, 0).toString();
        int idReparacion = Integer.parseInt(idTexto.replace("REP-", ""));

        // Obtenemos la lista de mecánicos desde la BD
        EmpleadoDAO empleadoDAO = new EmpleadoDAO();
        List<EmpleadoVO> mecanicos = empleadoDAO.listarMecanicosActivos();

        if (mecanicos.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "No hay mecánicos activos registrados en el sistema.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Mostramos el cuadro de diálogo para seleccionar el mecánico
        EmpleadoVO mecanicoSeleccionado = (EmpleadoVO) JOptionPane.showInputDialog(
                vista, "Selecciona el mecánico a asignar a la orden " + idTexto + ":",
                "Asignar Mecánico", JOptionPane.QUESTION_MESSAGE,
                null, mecanicos.toArray(), // Pasamos la lista convertida a array
                mecanicos.get(0) // Valor por defecto
        );

        // Si el usuario pulsó "Aceptar" y eligió un mecánico
        if (mecanicoSeleccionado != null) {
            // Obtenemos el filtro actual del combo de estados para no perder la vista del usuario
            String estadoFiltroActual = vista.getCbxFiltrarEstado().getSelectedItem().toString();
            
            if (modeloDAO.asignarMecanico(idReparacion, mecanicoSeleccionado.getId_empleado())) {
                filtrarTablaReparaciones((DefaultTableModel) vista.getTblReparaciones().getModel(), estadoFiltroActual);
                
                JOptionPane.showMessageDialog(vista, "Mecánico asignado con éxito a la reparación.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(vista, "Ocurrió un error al guardar en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    
    /**
     * Valida los datos de la vista y delega la actualización al DAO.
     */
    public boolean guardarCambiosFicha(int idReparacion, String estado, String prioridad, String combustible, String kilometrajeCrudo, String diagnostico) {
        
        int kilometrajeInt = 0;
        try {
            // Quitamos letras como " Km" y dejamos solo números
            String kmLimpio = kilometrajeCrudo.replaceAll("[^0-9]", "");
            kilometrajeInt = Integer.parseInt(kmLimpio);
        } catch (NumberFormatException ex) {
            System.err.println("Controlador: Error en el formato del kilometraje.");
            return false; 
        }

        return modeloDAO.actualizarFicha(idReparacion, estado, prioridad, combustible, kilometrajeInt, diagnostico);
    }
    
    
    /**
     * Llena la tabla de Trabajos Realizados en la ficha de detalles.
     */
    public void cargarTablaTrabajos(int idReparacion, DefaultTableModel modelo) {
        // Limpiamos la tabla por si tuviera datos anteriores
        modelo.setRowCount(0);
        
        // Pedimos los datos al DAO
        List<Object[]> trabajos = modeloDAO.obtenerTrabajosRealizados(idReparacion);
        
        // Rellenamos la vista
        for (Object[] fila : trabajos) {
            modelo.addRow(fila);
        }
    }
    
    
    /**
     * Llena la tabla de Piezas (Materiales) en la ficha de detalles.
     */
    public void cargarTablaPiezas(int idReparacion, DefaultTableModel modelo) {
        // Limpiamos la tabla
        modelo.setRowCount(0);
        
        // Pedimos los datos al DAO
        List<Object[]> piezas = modeloDAO.obtenerPiezasUtilizadas(idReparacion);
        
        // Rellenamos la vista
        for (Object[] fila : piezas) {
            modelo.addRow(fila);
        }
    }
    
    
    /**
     * Calcula los impuestos y devuelve los textos formateados para la vista.
     */
    public Map<String, String> calcularYFormatearCostos(int idReparacion) {
        // Pedimos los datos al DAO
        Map<String, BigDecimal> costos = modeloDAO.obtenerCostos(idReparacion);
        
        // Usamos BigDecimal porque garantiza precisión matemática exacta
        BigDecimal totalMO = costos.getOrDefault("mano_obra", BigDecimal.ZERO);
        BigDecimal totalPiezas = costos.getOrDefault("piezas", BigDecimal.ZERO);
        
        // Matemáticas de facturación
        BigDecimal baseImponible = totalMO.add(totalPiezas);
        // Multiplicamos por 0.21 para sacar el IVA
        BigDecimal iva = baseImponible.multiply(new java.math.BigDecimal("0.21")); 
        BigDecimal total = baseImponible.add(iva);
        
        // Formateo (Ej: 1.250,50 €)
        DecimalFormat df = new java.text.DecimalFormat("#,##0.00 €");
        
        // Empaquetamos para la vista
        Map<String, String> resultado = new HashMap<>();
        resultado.put("mano_obra", df.format(totalMO));
        resultado.put("piezas", df.format(totalPiezas));
        resultado.put("base", df.format(baseImponible));
        resultado.put("iva", df.format(iva));
        resultado.put("total", df.format(total));
        
        return resultado;
    }
    
    
    /**
     * Llena la tabla de Historial del vehículo en la ficha de detalles.
     */
    public void cargarTablaHistorial(int idReparacion, DefaultTableModel modelo) {
        // Limpiamos la tabla
        modelo.setRowCount(0);
        
        // Pedimos los datos al DAO
        List<Object[]> historial = modeloDAO.obtenerHistorialVehiculo(idReparacion);
        
        // Rellenamos la vista
        for (Object[] fila : historial) {
            modelo.addRow(fila);
        }
    }
    
    
    public boolean procesarNuevaReparacion(ReparacionVO reparacion, List<DetalleProductoVO> listaMateriales) {
        
        // 1. Guardamos la reparación y sus detalles
        boolean guardadoExitoso = modeloDAO.insertarConDetalles(reparacion);
        
        // Si la reparación se guardó bien, descontamos el stock de las piezas
        if (guardadoExitoso) {
            com.joseluis.apptaller.modelo.dao.ProductoDAO prodDAO = new com.joseluis.apptaller.modelo.dao.ProductoDAO();
            
            for (DetalleProductoVO material : listaMateriales) {
                // Sacamos la referencia del producto y la cantidad usando el VO real
                String idProd = material.getIdProducto(); 
                int cantidadUsada = material.getCantidadUsada();
                
                // Llamamos al método de descontar stock
                boolean descontado = prodDAO.descontarStock(idProd, cantidadUsada);
                
                if (!descontado) {
                    System.err.println("Atención: No se pudo descontar el stock de la referencia " + idProd);
                }
            }
        }
        
        return guardadoExitoso;
    }

    private void buscarReparacion() {
        String busqueda = vista.getTxtBuscarReparacion().getText().trim();
        String placeholder = "Buscar reparación..."; 
        if (busqueda.isEmpty() || busqueda.equals(placeholder)) {
            JOptionPane.showMessageDialog(vista,
                "Por favor, introduzca un término antes de buscar.",
                "Aviso de Búsqueda",
                JOptionPane.WARNING_MESSAGE);
            vista.getTxtBuscarCliente().requestFocus();
            return;
        }
        // Extraemos el modelo de la tabla 
        DefaultTableModel modelo = (DefaultTableModel) vista.getTblReparaciones().getModel();
        modelo.setColumnIdentifiers(new String[]{"ID", "Matricula", "Cliente", "Mecánico", "Prioridad", "Estado", "Fecha"});
        modelo.setRowCount(0); // Limpiamos la tabla antes de rellenar
        List<Object[]> resultados = modeloDAO.buscarReparacion(busqueda);

        if (resultados != null && !resultados.isEmpty()) {
            for (Object[] fila : resultados) {
                modelo.addRow(fila);
            }
            vista.getTxtBuscarReparacion().setText(""); 
        } else {
            JOptionPane.showMessageDialog(vista,
                "No se encontraron reparaciones para: " + busqueda,
                "Aviso", JOptionPane.INFORMATION_MESSAGE);
               
            // Volvemos a cargar la tabla completa para que no se quede vacía
            llenarTablaReparaciones(modelo);
            vista.getTxtBuscarReparacion().setText("");
        }
    }

    public void cargarReparaciones() {
        DefaultTableModel modelo = (DefaultTableModel) vista.getTblReparaciones().getModel();
        llenarTablaReparaciones(modelo);
    }

    private void mostrarDetalles() {
        // Verificamos si el usuario ha hecho clic en alguna fila de la tabla
        int filaSeleccionada = vista.getTblReparaciones().getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor, selecciona una reparación de la tabla para ver sus detalles.", "Aviso", javax.swing.JOptionPane.WARNING_MESSAGE);
            return; // Cortamos la ejecución aquí si no hay fila seleccionada
        }

        // Extraemos el ID de esa fila (Está en la columna 0 y le quitamos el "REP-")
        String idTexto = vista.getTblReparaciones().getValueAt(filaSeleccionada, 0).toString();
        int idReparacion = Integer.parseInt(idTexto.replace("REP-", ""));

        // Creamos el Dialog Modal
        DialogDetallesReparacion detallesDialog = new DialogDetallesReparacion(vista, true);

        // Inyectamos datos: llama al método cargarDatosReparacion y rellena los JText
        detallesDialog.cargarDatosReparacion(idReparacion);

        // Mostramos en pantalla 
        detallesDialog.setLocationRelativeTo(vista);
        detallesDialog.setVisible(true);
    }

    private void generarFactura() {
        int filaSeleccionada = vista.getTblReparaciones().getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista,
                    "Por favor, selecciona una reparación de la lista para facturar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Comprobamos el estado
        String estado = vista.getTblReparaciones().getValueAt(filaSeleccionada, 5).toString();
        if (!estado.equals("FINALIZADA")) {
            JOptionPane.showMessageDialog(vista,
                    "Solo se pueden facturar reparaciones cuyo trabajo haya finalizado (Estado: FINALIZADA).",
                    "Operación no permitida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Extraemos el ID de la tabla visual 
            String idTexto = vista.getTblReparaciones().getValueAt(filaSeleccionada, 0).toString();
            int idReparacion = Integer.parseInt(idTexto.replace("REP-", "").trim());

            int idPresupuesto = 0;

            // Sacamos el DNI y Bastidor de la base de datos
            String[] clavesReales = modeloDAO.obtenerDniYBastidor(idReparacion);
            String clienteDni = clavesReales[0];
            String bastidor = clavesReales[1];

            // Calculamos los importes
            double totalManoObra = modeloDAO.calcularTotalManoObra(idReparacion);
            double totalProductos = modeloDAO.calcularTotalProductos(idReparacion);

            // Instanciamos y abrimos el dialog
            DialogGenerarFactura dialog = new DialogGenerarFactura(
                    vista, true, idReparacion, idPresupuesto, clienteDni, bastidor,
                    totalManoObra, totalProductos
            );

            dialog.setVisible(true);

            // Al cerrar el dialog, refrescamos la tabla para que se actualice el estado
            llenarTablaReparaciones((DefaultTableModel) vista.getTblReparaciones().getModel());

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(vista,
                    "Error al leer los datos de la reparación: " + e.getMessage(),
                    "Error Interno", JOptionPane.ERROR_MESSAGE);
        }
    }

     
}

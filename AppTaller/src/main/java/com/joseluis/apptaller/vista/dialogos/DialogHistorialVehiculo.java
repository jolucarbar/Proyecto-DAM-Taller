package com.joseluis.apptaller.vista.dialogos;

import com.joseluis.apptaller.modelo.dao.ReparacionDAO;
import com.joseluis.apptaller.util.GeneradorInformes;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DialogHistorialVehiculo extends JDialog {

    private String bastidorActual;
    private JTable tblHistorial;
    private DefaultTableModel modeloTabla;
    private JLabel lblTituloVehiculo;

    public DialogHistorialVehiculo(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(700, 400);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setTitle("Historial de Reparaciones del Vehículo");
        setLayout(new BorderLayout(10, 10));

        // Panel Superior (Título)
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTop.setBackground(new Color(33, 150, 243));
        lblTituloVehiculo = new JLabel("Historial de Mantenimiento");
        lblTituloVehiculo.setFont(new Font("Roboto", Font.BOLD, 18));
        lblTituloVehiculo.setForeground(Color.WHITE);
        panelTop.add(lblTituloVehiculo);
        add(panelTop, BorderLayout.NORTH);

        // Tabla Central
        modeloTabla = new DefaultTableModel(new String[]{"ID Rep.", "Fecha Entrada", "Estado", "Mecánico", "Diagnóstico/Notas"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblHistorial = new JTable(modeloTabla);
        tblHistorial.setRowHeight(25);
        add(new JScrollPane(tblHistorial), BorderLayout.CENTER);

        // Panel Inferior (Botones)
        JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnImprimir = new JButton("Imprimir Informe");
        JButton btnCerrar = new JButton("Cerrar");

        btnCerrar.addActionListener(e -> dispose());
       
        btnImprimir.addActionListener(e -> {
            if (bastidorActual != null && !bastidorActual.isEmpty()) {
                GeneradorInformes.mostrarInformeHistorialVehiculo(bastidorActual);
            }
        });

        panelBottom.add(btnImprimir);
        panelBottom.add(btnCerrar);
        add(panelBottom, BorderLayout.SOUTH);
    }

    public void cargarDatos(String bastidor, String matricula, String marca, String modelo) {
        this.bastidorActual = bastidor;
        lblTituloVehiculo.setText("Historial: " + matricula + " (" + marca + " " + modelo + ")");
       
        modeloTabla.setRowCount(0); // Limpiar tabla
        ReparacionDAO repDao = new ReparacionDAO();
        List<Object[]> historial = repDao.obtenerHistorialPorBastidor(bastidor);
       
        for (Object[] fila : historial) {
            modeloTabla.addRow(fila);
        }
    }
    
}

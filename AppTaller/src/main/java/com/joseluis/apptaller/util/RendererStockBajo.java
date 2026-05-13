
package com.joseluis.apptaller.util;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Personaliza el pintado de las celdas de una JTable para resaltar en rojo 
 * aquellas filas donde el stock actual es igual o inferior al stock mínimo.
 * 
 * @author José Luis Cárdenas Barroso
 * @info Proyecto Intermodular del Grado Superior DAM
 * @institution IES Augustóbriga
 */
public class RendererStockBajo extends DefaultTableCellRenderer {
    
    private final int colStock;
    private final int colStockMinimo;

    /**
     * @param colStock Índice de la columna donde está el Stock Actual
     * @param colStockMinimo Índice de la columna donde está el Stock Mínimo
     */
    public RendererStockBajo(int colStock, int colStockMinimo) {
        this.colStock = colStock;
        this.colStockMinimo = colStockMinimo;
    }
  

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        // Dejamos que Java pinte la celda por defecto primero
        Component celda = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        try {
            
            // Extraemos los valores de esa fila específica
            int stockActual = Integer.parseInt(table.getValueAt(row, colStock).toString());
            int stockMinimo = Integer.parseInt(table.getValueAt(row, colStockMinimo).toString());

            // Lógica de negocio visual
            if (stockActual <= stockMinimo) {
                if (isSelected) {
                    celda.setBackground(new Color(255, 102, 102)); // Rojo oscuro si la fila está seleccionada
                } else {
                    celda.setBackground(new Color(255, 204, 204)); // Rojo pastel para el fondo normal
                }
                celda.setForeground(Color.BLACK); // Letra negra para que se lea bien
            } else {
                // Si el stock está bien, recuperamos los colores originales del L&F (Material Design)
                if (isSelected) {
                    celda.setBackground(table.getSelectionBackground());
                    celda.setForeground(table.getSelectionForeground());
                } else {
                    celda.setBackground(table.getBackground());
                    celda.setForeground(table.getForeground());
                }
            }
        } catch (Exception e) {
            // Si hay un error al leer el número o la celda está vacía, no hacemos nada raro
        }

        return celda;
    }
}
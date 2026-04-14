

package com.joseluis.apptaller;

import com.joseluis.apptaller.vista.ventanas.VentanaLogin;

/**
 * Clase principal y punto de entrada de la aplicación AppTaller.
 * Contiene el método main que inicia la ejecución del programa
 * instanciando y mostrando la pantalla de autenticación (VentanaLogin).
 * 
 * @author José Luis Cárdenas Barroso
 * @info Proyecto Intermodular del Grado Superior DAM
 * @institution IES Augustóbriga
 */
public class AppTaller {

    public static void main(String[] args) {
        VentanaLogin login = new VentanaLogin();
        login.setVisible(true);
    }
}

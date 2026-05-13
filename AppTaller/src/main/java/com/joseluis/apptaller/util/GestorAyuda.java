
package com.joseluis.apptaller.util;

import java.net.URL;

 /**
 * Clase centralizada que actúa como repositorio de los manuales de ayuda.
 * Al usar métodos estáticos, no necesitamos instanciar la clase para usarla.
 * 
 * @author José Luis Cárdenas Barroso
 * @info Proyecto Intermodular del Grado Superior DAM
 * @institution IES Augustóbriga
 */
public class GestorAyuda {
    
    /**
    * Convierte una ruta de recursos a una URL absoluta válida para JEditorPane
    */
    private static String resolverImagen(String rutaRelativa) {
        java.net.URL url = GestorAyuda.class.getResource(rutaRelativa);
        if (url != null) {
            // JEditorPane requiere la URL completa (ej: "jar:file:/C:/...!/images/foto.png")
            // Usamos atributos HTML en lugar de CSS para máxima compatibilidad con Swing
            return "<img src='" + url.toExternalForm() + "' width='500'>";
        } else {
            System.err.println("⚠️ Advertencia: No se encontró la imagen en " + rutaRelativa);
            return "<b style='color:red;'>[Imagen no encontrada: " + rutaRelativa + "]</b>";
        }
    }
    
    
    /**
     * Devuelve el código HTML correspondiente al tema solicitado.
     */
    public static String obtenerContenido(String tituloTema) {
        
        switch (tituloTema) {
            case "Primeros Pasos":
                return "<html>"
                      + "<body style='font-family: sans-serif; padding: 20px; color: #333333; line-height: 1.6;'>"
                     + "<h2 style='color: #1976D2; border-bottom: 2px solid #1976D2; padding-bottom: 5px;'>🚀 Primeros Pasos en APP Mi Taller</h2>"
                     + "<p>¡Bienvenido al sistema de gestión integral de tu taller mecánico! <b>APP Mi Taller</b> ha sido diseñada para optimizar y centralizar todo tu flujo de trabajo diario, desde la recepción de un cliente en el mostrador hasta la entrega de su vehículo y la posterior emisión de la factura.</p>"
                     + "<h3>1. El Panel de Control (Dashboard)</h3>"
                     + "<p>Nada más iniciar sesión, el sistema te presenta un panel de control diseñado para ofrecerte una visión general del estado de tu negocio. Aquí encontrarás:</p>"
                     + "<ul>"
                     + "<li><b>Vehículos en Taller:</b> Un indicador numérico rápido que te muestra la carga de trabajo actual.</li>"
                     + "<li><b>Agenda del día:</b> Un resumen de las citas o tareas prioritarias planificadas.</li>"
                     + "<li><b>Alertas de Stock:</b> El sistema te avisará automáticamente si algún recambio clave se está agotando.</li>"
                     + "<li><b>Bloc de Notas:</b> Un espacio de autoguardado para apuntar recordatorios rápidos sin necesidad de salir de la pantalla principal.</li>"
                     + "</ul>"
                     + "<h3>2. El Flujo de Trabajo Recomendado</h3>"
                     + "<p>Para sacarle el máximo partido al ERP y mantener la base de datos coherente, te recomendamos seguir siempre este orden lógico al recibir un nuevo encargo:</p>"
                     + "<ol>"
                     + "<li><b>Identificar al Cliente:</b> Dirígete a la pestaña <i>Clientes</i>. Búscalo en el sistema. Si es la primera vez que visita el taller, procede a darle de alta rellenando sus datos básicos.</li>"
                     + "<li><b>Registrar el Vehículo:</b> Ve a la sección <i>Vehículos</i> y asocia la matrícula y bastidor al DNI del cliente creado en el paso anterior.</li>"
                     + "<li><b>Generar la Orden de Reparación:</b> Con el cliente y el vehículo en el sistema, ya puedes abrir una nueva reparación. Aquí registrarás los kilómetros, el nivel de combustible, el diagnóstico inicial y asignarás un mecánico al trabajo.</li>"
                     + "<li><b>Presupuestos y Facturación:</b> A medida que la reparación avance, podrás generar presupuestos formales para la aceptación del cliente y, una vez finalizado el trabajo, emitir la factura final con un solo clic.</li>"
                     + "</ol>"
                     + "<br>"
                     + resolverImagen("/images/capturas_ayuda/inicio.png")
                     + "<br>"
                     + "<p><i>💡 <b>Consejo Pro:</b> Utiliza el menú lateral izquierdo para saltar entre los distintos módulos de la aplicación. Tu información no se perderá al cambiar de pantalla.</i></p>"
                     + "</body>"
                     + "</html>";

            case "Gestión de Clientes":
                return "<html>"
                     + "<body style='font-family: sans-serif; padding: 20px; color: #333333; line-height: 1.6;'>"
                     + "<h2 style='color: #1976D2; border-bottom: 2px solid #1976D2; padding-bottom: 5px;'>👥 Gestión de Clientes</h2>"
                     + "<p>El módulo de Clientes es el pilar fundamental sobre el que se construye toda la operatividad del taller. Un registro preciso y detallado de tu clientela no solo agiliza el contacto, sino que es un requisito legal indispensable para la posterior facturación de los servicios.</p>"
                     + "<h3>Alta y Registro</h3>"
                     + "<p>Para añadir un nuevo cliente, haz clic en el botón <b>Nuevo</b> situado en la barra de herramientas superior. Se desplegará un formulario donde deberás introducir la información de contacto. Recuerda que el <b>DNI o CIF</b> es el identificador único del cliente en todo el sistema; asegúrate de introducirlo correctamente, ya que será la clave que vincule sus vehículos y sus facturas.</p>"
                     + "<h3>Búsqueda y Filtros</h3>"
                     + "<p>A medida que tu base de datos crezca, localizar a un cliente será muy sencillo. Utiliza la barra de búsqueda introduciendo parcial o totalmente su nombre, apellidos o documento de identidad. El sistema filtrará la tabla en tiempo real, mostrándote solo las coincidencias.</p>"
                     + "<h3>La Vista 360º (Historial del Cliente)</h3>"
                     + "<p>Una de las herramientas más potentes de esta aplicación es la <b>Ficha Histórica</b>. Si seleccionas a un cliente en la tabla y pulsas el botón <i>Historial</i>, se abrirá una ventana que centraliza toda su actividad histórica con tu taller. Esta ventana cuenta con varias pestañas:</p>"
                     + "<ul>"
                     + "<li><b>Vehículos:</b> Listado de todos los coches que este cliente tiene registrados a su nombre en el taller.</li>"
                     + "<li><b>Presupuestos:</b> Histórico de presupuestos presentados, tanto los que fueron aceptados como los rechazados.</li>"
                     + "<li><b>Facturas:</b> Acceso directo a su facturación, ideal para revisar saldos pendientes o emitir duplicados en PDF.</li>"
                     + "</ul>"
                     + "<br>"
                     + resolverImagen("/images/capturas_ayuda/gestion_clientes.png")
                     + "<br>"
                     + "<h3>Bajas y Privacidad de Datos</h3>"
                     + "<p>Si un cliente solicita ejercer su derecho al borrado de datos o deseas retirarlo de tu lista activa, utiliza la opción de <i>Eliminar</i>. Por motivos de integridad fiscal y de bases de datos, el sistema realiza un <b>borrado lógico</b>. Esto oculta al cliente de tus operativas diarias, pero preserva la coherencia de las facturas antiguas emitidas a su nombre.</p>"
                     + "</body>"
                     + "</html>";

            case "Cómo crear una Factura":
                 return "<html>"
                     + "<body style='font-family: sans-serif; padding: 20px; color: #333333; line-height: 1.6;'>"
                     + "<h2 style='color: #1976D2; border-bottom: 2px solid #1976D2; padding-bottom: 5px;'>📄 Emisión y Gestión de Facturas</h2>"
                     + "<p>El módulo de facturación es el último paso en el flujo de trabajo del taller. Para garantizar la estricta trazabilidad fiscal y evitar descuadres en el inventario, el sistema está diseñado para generar facturas de forma automatizada a partir de los trabajos reales, evitando la escritura manual propensa a errores.</p>"
                     + "<h3>1. Requisito Previo: Finalizar la Reparación</h3>"
                     + "<p>En APP Mi Taller, no se pueden crear facturas en blanco desde cero. Toda factura debe estar respaldada por una Orden de Reparación. Para poder facturar a un cliente, debes asegurarte de que el trabajo sobre su vehículo se encuentre registrado en la pestaña <b>Reparaciones</b> y que su estado actual sea <b>FINALIZADA</b>.</p>"
                     + "<h3>2. Generar la Factura</h3>"
                     + "<p>Una vez completado el trabajo mecánico, el proceso de facturación es instantáneo:</p>"
                     + "<ol>"
                     + "<li>Dirígete a la sección principal de <i>Reparaciones</i>.</li>"
                     + "<li>Selecciona en la tabla la orden de reparación pertinente (recuerda, debe estar marcada como <b>FINALIZADA</b>).</li>"
                     + "<li>Haz clic en el botón <b>Generar Factura</b> situado en la barra de herramientas superior.</li>"
                     + "<li>El sistema cruzará los datos automáticamente: extraerá los datos fiscales del cliente, calculará la base imponible sumando los recambios utilizados y la mano de obra registrada, y aplicará el IVA correspondiente.</li>"
                     + "</ol>"
                     + "<br>"
                     + resolverImagen("/images/capturas_ayuda/crear_factura.png")
                     + "<br>"
                     + "<h3>3. Panel de Facturas y Registro de Pagos</h3>"
                     + "<p>Todas las facturas emitidas quedan consolidadas en el módulo central de <b>Facturas</b> (accesible desde el menú lateral izquierdo). Este panel es tu centro de control de tesorería:</p>"
                     + "<ul>"
                     + "<li><b>Control de Cobros:</b> Toda factura recién emitida nace con el estado <i>PENDIENTE</i>. Cuando el cliente realice el abono en el mostrador, selecciona la factura en la tabla y pulsa el botón <b>Registrar Pago</b>. El estado se actualizará a <i>PAGADA</i>.</li>"
                     + "<li><b>Visualización e Impresión:</b> Si seleccionas una factura y pulsas <b>Ver Factura</b>, el sistema generará un documento oficial en PDF con el logotipo y membrete de tu taller. Desde esa ventana podrás enviar el documento directamente a la impresora o guardarlo en tu ordenador para enviarlo por email.</li>"
                     + "</ul>"
                     + "</body>"
                     + "</html>";
            
            case "Control de Stock":
                 return "<html>"
                     + "<body style='font-family: sans-serif; padding: 20px; color: #333333; line-height: 1.6;'>"
                     + "<h2 style='color: #1976D2; border-bottom: 2px solid #1976D2; padding-bottom: 5px;'>📦 Control de Stock e Inventario</h2>"
                     + "<p>Una gestión eficiente del inventario es vital para la rentabilidad del taller. El módulo de Productos está diseñado para evitar rupturas de stock que puedan paralizar las reparaciones, automatizando el control de las entradas y salidas de mercancía.</p>"
                     + "<h3>Catálogo de Productos</h3>"
                     + "<p>En esta pantalla visualizarás todos los recambios, líquidos y piezas registrados en tu base de datos. Cada producto cuenta con un identificador único (referencia), su proveedor asociado, el precio de coste y el PVP (Precio de Venta al Público).</p>"
                     + "<h3>El Sistema de Alertas Visuales</h3>"
                     + "<p>Para facilitar tu trabajo, la tabla de inventario está programada para comunicarse visualmente contigo:</p>"
                     + "<ul>"
                     + "<li><b>Filas en Rojo:</b> Cualquier producto cuya fila se muestre resaltada en color rojo te está indicando que su cantidad actual en el almacén ha caído por debajo del <i>Stock Mínimo</i> de seguridad configurado. Es una alerta visual para que contactes con tu proveedor.</li>"
                     + "<li><b>Alertas en el Dashboard:</b> Estas mismas advertencias se resumen en tu Panel de Inicio al arrancar el programa, para que tu primera tarea del día pueda ser gestionar los pedidos pendientes.</li>"
                     + "</ul>"
                     + "<h3>Entradas y Salidas de Mercancía</h3>"
                     + "<p>El inventario es dinámico y responde a tu actividad en el taller:</p>"
                     + "<ol>"
                     + "<li><b>Descuento Automático (Salidas):</b> No necesitas restar piezas manualmente. Cada vez que cierras una orden de reparación y declaras los materiales utilizados, el sistema descuenta automáticamente esas cantidades de tu almacén.</li>"
                     + "<li><b>Reposición (Entradas):</b> Cuando recibes un envío de tu proveedor, simplemente selecciona el artículo afectado en la tabla y haz clic en el botón <i>+ Añadir Unidades</i>. Introduce la cantidad recibida y el stock se actualizará al instante, apagando la alerta roja si procede.</li>"
                     + "</ol>"
                     + "<br>"
                     + resolverImagen("/images/capturas_ayuda/control_stock.png")
                     + "</body>"
                     + "</html>";
                
            case "Gestión de Empleados":
                    return "<html>"
                     + "<body style='font-family: sans-serif; padding: 20px; color: #333333; line-height: 1.6;'>"
                     + "<h2 style='color: #1976D2; border-bottom: 2px solid #1976D2; padding-bottom: 5px;'>🛠️ Gestión de Empleados y Seguridad</h2>"
                     + "<p>El módulo de Empleados cumple una doble función: actúa como una base de datos de Recursos Humanos para el control de tu plantilla y, al mismo tiempo, administra los permisos y accesos de seguridad a la aplicación.</p>"
                     + "<h3>Registro y Creación de Credenciales</h3>"
                     + "<p>Al dar de alta a un nuevo trabajador, la ficha te solicitará sus datos personales, su cargo dentro de la empresa y las condiciones salariales. Adicionalmente, el formulario incluye un apartado fundamental: las <b>Credenciales de Acceso</b>.</p>"
                     + "<p>Si el empleado necesita utilizar este software, deberás generarle un nombre de usuario y una contraseña (la cual se encriptará de forma segura en la base de datos). Además, deberás asignarle un Rol operativo:</p>"
                     + "<ul>"
                     + "<li><b>Rol Administrador:</b> Diseñado para la gerencia. Otorga control total sobre la aplicación, permitiendo ver informes financieros, gestionar altas y bajas de otros empleados y modificar configuraciones críticas.</li>"
                     + "<li><b>Rol Empleado:</b> Pensado para los mecánicos y personal de mostrador. Su acceso está limitado a la operatividad del taller: crear reparaciones, buscar clientes, añadir vehículos y consultar el catálogo de piezas.</li>"
                     + "</ul>"
                     + "<h3>Asignación de Trabajos</h3>"
                     + "<p>Todo mecánico dado de alta aparecerá automáticamente en los desplegables de asignación al momento de crear una Nueva Reparación. Esto permite auditar la productividad y saber exactamente quién ha sido el responsable de cada intervención en los vehículos.</p>"
                     + "<br>"
                     + resolverImagen("/images/capturas_ayuda/gestion_empleados.png")
                     + "<br>"
                     + "<h3>Bajas y Trazabilidad</h3>"
                     + "<p>Si un trabajador finaliza su contrato, debes utilizar la opción <i>Eliminar</i>. Al igual que con los clientes, el sistema efectúa una <b>baja lógica</b>. El empleado perderá instantáneamente su capacidad para iniciar sesión, pero su identidad seguirá vinculada a las órdenes de reparación y facturas que emitió en el pasado, garantizando un registro de trazabilidad impecable.</p>"
                     + "</body>"
                     + "</html>";

            default:
                return "<html><body style='font-family: sans-serif; padding: 10px;'>"
                     + "<h3>⚠️ Contenido en construcción</h3>"
                     + "<p>El manual para este tema estará disponible en la próxima actualización.</p>"
                     + "</body></html>";
        }
    }
}

**Proyecto-DAM-Taller**  
El proyecto consiste en el desarrollo de una aplicación de escritorio en Java (Swing) que permita gestionar de forma unificada la información de un taller de reparación de automóviles.  
**APPTaller: Sistema de Gestión Integral para Taller de Reparación de Automóviles**  
**Proyecto Intermodular de Fin de Ciclo**  
   
 **Alumno:** José Luis Cárdenas Barroso  
   
 **Ciclo:** Grado Superior en Desarrollo de Aplicaciones Multiplataforma (DAM)  
   
 **Centro:** I.E.S. Augustóbriga  
   
 **Año:** 2025/2026  
![](data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAnEAAAACCAYAAAA3pIp+AAAABmJLR0QA/wD/AP+gvaeTAAAACXBIWXMAAA7EAAAOxAGVKw4bAAAANUlEQVR4nO3OMQ2AABAAsSNBCUrfDqrYGVDAgAU2QtIq6DIzW7UHAMBfHGt1V+fXEwAAXrseHCQGBEuErVgAAAAASUVORK5CYII=)  
**1. Descripción del Proyecto**  
El proyecto "APPTaller" consiste en el desarrollo de una aplicación de escritorio en Java (Swing) que permite gestionar de forma unificada la información de un taller de reparación de automóviles.  
El sistema centraliza las operaciones del negocio y facilita la gestión diaria, cubriendo el ciclo de vida completo de los servicios: desde la gestión de clientes, vehículos y proveedores, hasta la creación de presupuestos, su aceptación y la emisión final de facturas.  
**2. Objetivos Principales**  
- **Objetivo General:** Desarrollar una aplicación de escritorio funcional que integre la gestión de clientes, vehículos, proveedores, presupuestos y facturas en un único sistema.  
- **Objetivos Específicos:**  
- Diseñar e implementar una base de datos relacional (MySQL).  
- Implementar una interfaz gráfica de usuario (GUI) intuitiva con Java Swing.  
- Aplicar el patrón de diseño Modelo-Vista-Controlador (MVC) y el patrón DAO para el acceso a datos (JDBC).  
- Desarrollar un sistema de autenticación de usuarios.  
- Generar informes (presupuestos, facturas) en PDF utilizando JasperReports.  
**3. Stack Tecnológico**  
- **Lenguaje:** Java 17 (o superior)  
- **Entorno de Desarrollo (IDE):** NetBeans 28  
- **Interfaz Gráfica:** Java Swing  
- **Base de Datos:** MySQL  
- **Acceso a Datos:** JDBC (con patrón DAO)  
- **Informes:** iReports / JasperReports Studio  
- **Pruebas:** JUnit  
- **Control de Versiones:** Git y GitHub  
**4. Estructura **  
El proyecto sigue una arquitectura Modelo-Vista-Controlador (MVC) robusta y fuertemente tipada, organizada en los siguientes paquetes:  
- com.joseluis.apptaller.modelo.vo: Objetos de Valor o Entidades (Mapeo de la base de datos).  
- com.joseluis.apptaller.modelo.dao: Objetos de Acceso a Datos (Lógica de consultas SQL).  
- com.joseluis.apptaller.controlador: Orquestadores que conectan la interfaz gráfica con la base de datos.  
- com.joseluis.apptaller.vista:  
- .ventanas: Marcos principales (VentanaLogin, VentanaPrincipal).  
- .dialogos: Modales (JDialog) para inserción y gestión de datos específicos.  
- com.joseluis.apptaller.persistencia: Gestión de la conexión a MySQL mediante el patrón Singleton.  
- com.joseluis.apptaller.utilidades: Clases auxiliares de seguridad (ej. Encriptación PBKDF2), generador de informes, gestor de ayuda y renderer de stock bajo.  
.  
**5. Instalación y Ejecución **  
Siga estos pasos para ejecutar la aplicacion en su entorno local:  
   
Requisitos Previos:   
- Java: Version 17 o superior. Base de Datos: MySQL Server 8.0+ (ejemplo XAMPP o WAMP) en el puerto 3306.  
- Pasos de Configuracion:   
- Paso 1. Importar Base de Datos: Cree la base de datos apptaller_db e importe el archivo apptaller_db.sql incluido en la entrega.  
- Paso 2. Configurar Conexion: La aplicacion lee las credenciales desde un archivo externo. Abra el archivo config.properties (ubicado junto al ejecutable) y ajuste el usuario y contrasena segun su entorno local de esta forma: db.url=jdbc:mysql://localhost:3306/apptaller_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true   
db.user=TU_USUARIO   
db.password=TU_CONTRASENA  
- Paso 3. Ejecutar: Abra una terminal en la carpeta del proyecto y ejecute el comando "java -jar AppTaller.jar" o haga doble clic sobre el archivo.  
   
Credenciales de Acceso (Prueba) Para facilitar la evaluacion, utilice estas cuentas preconfiguradas:  
   
| | | |  
|-|-|-|  
| Rol  | Usuario | Contraseña |   
| Administrador  | admin | admin123 |   
| Mecanico  | javi  | 1234 |   
   
**6. Estado actual de desarrollo**  
Actualmente, el proyecto se encuentra finalizado y empaquetado para su entrega y evaluación. La arquitectura base está completamente consolidada, el código ha sido revisado y todos los módulos críticos de negocio se encuentran 100% operativos e integrados.  
   
Funcionalidades y Objetivos Completados:  
- Arquitectura y Seguridad: Implementación estricta del patrón MVC, apoyado en DAO/VO y Singleton (MySQL). Sistema de autenticación de usuarios validado y seguro.  
- Módulos de Gestión Base: Interfaces y lógica de negocio (altas, bajas, modificaciones y consultas) totalmente operativas para Clientes, Vehículos, Proveedores, Productos/Recambios y Empleados.  
- Gestión de Taller: Flujo de trabajo completo para recepción de vehículos, registro de inspecciones (kilometraje, combustible, diagnósticos) y generación de Órdenes de Reparación.  
- Módulo Económico y Stock: Sistema interactivo de Presupuestos y conversión automática a Facturas. Integración final del control de inventario, automatizando el descuento de piezas al facturar.  
- Reportes (PDF): Integración total de la librería JasperReports para la generación y exportación de facturas y presupuestos oficiales.  
- Interfaz de Usuario (UX/UI): Capa de presentación validada e impulsada por MaterialLookAndFeel, garantizando bloqueos lógicos y navegación fluida por pestañas.  
- Despliegue y Documentación: Aplicación testeada de extremo a extremo, empaquetada en un ejecutable (.jar) autónomo y documentada con su respectiva Memoria Técnica y Manual de Usuario.  
   

# Proyecto-DAM-Taller
El proyecto consiste en el desarrollo de una aplicación de escritorio en Java (Swing) que permita gestionar de forma unificada la información de un taller de reparación de automóviles.

# APPTaller: Sistema de Gestión Integral para Taller de Reparación de Automóviles

**Proyecto Intermodular de Fin de Ciclo**
**Alumno:** José Luis Cárdenas Barroso
**Ciclo:** Grado Superior en Desarrollo de Aplicaciones Multiplataforma (DAM)
**Centro:** I.E.S. Augustóbriga
**Año:** 2025/2026

---

## 1. Descripción del Proyecto

El proyecto "APPTaller" consiste en el desarrollo de una aplicación de escritorio en Java (Swing) que permite gestionar de forma unificada la información de un taller de reparación de automóviles.

El sistema centraliza las operaciones del negocio y facilita la gestión diaria, cubriendo el ciclo de vida completo de los servicios: desde la gestión de clientes, vehículos y proveedores, hasta la creación de presupuestos, su aceptación y la emisión final de facturas.


## 2. Objetivos Principales

* **Objetivo General:** Desarrollar una aplicación de escritorio funcional que integre la gestión de clientes, vehículos, proveedores, presupuestos y facturas en un único sistema.
* **Objetivos Específicos:**
    * Diseñar e implementar una base de datos relacional (MySQL).
    * Implementar una interfaz gráfica de usuario (GUI) intuitiva con Java Swing.
    * Aplicar el patrón de diseño Modelo-Vista-Controlador (MVC) y el patrón DAO para el acceso a datos (JDBC).
    * Desarrollar un sistema de autenticación de usuarios.
    * Generar informes (presupuestos, facturas) en PDF utilizando JasperReports.
    * 

## 3. Stack Tecnológico

* **Lenguaje:** Java 17 (o superior)
* **Entorno de Desarrollo (IDE):** NetBeans 28
* **Interfaz Gráfica:** Java Swing
* **Base de Datos:** MySQL
* **Acceso a Datos:** JDBC (con patrón DAO)
* **Informes:** iReports / JasperReports Studio
* **Pruebas:** JUnit
* **Control de Versiones:** Git y GitHub
  

## 4. Estructura (En desarrollo)

El proyecto sigue una arquitectura MVC (Modelo-Vista-Controlador) robusta:

   * com.joseluis.apptaller.modelo.vo: Objetos de valor (Entidades).
   * com.joseluis.apptaller.modelo.dao: Objetos de Acceso a Datos (Lógica SQL).
   * com.joseluis.apptaller.controlador: Orquestadores de eventos y datos.
   * com.joseluis.apptaller.vista:
      * .ventanas: Frames principales (VentanaLogin, VentanaPrincipal).
      * .dialogos: Modales de gestión específica.
   * com.joseluis.apptaller.persistencia: Gestión de la conexión Singleton a MySQL.
     
     
## 5. Instalación y Ejecución (En desarrollo)

*(Esta sección se completará al final del proyecto con los pasos para compilar y ejecutar la aplicación)*


## 6. Estado actual de desarrollo

A día de hoy, el proyecto ha completado su Fase 1: Diseño de Interfaz y Experiencia de Usuario (UI/UX).

   * Interfaz de Usuario: Implementada al 100% con Java Swing y el wrapper material-ui-swing, ofreciendo una estética moderna bajo          los principios de Material Design.
   * Navegación: Estructura de ventana única (Single Window) gestionada mediante CardLayout para una navegación fluida entre módulos       (Dashboard, Clientes, Reparaciones, etc.).
   * Componentes Avanzados: Diseñados más de 10 cuadros de diálogo técnicos (JDialog) para operaciones específicas como recepción de       vehículos con sliders de combustible y gestión detallada de reparaciones.


## 7. Próximos objetivos
   * Arquitectura DAO: Desarrollo de las clases de acceso a datos para mapear las tablas de apptaller_db a objetos Java (VO/POJO).
   * Seguridad: Implementación del sistema de login real contra la base de datos con control de roles (Administrador/Empleado).

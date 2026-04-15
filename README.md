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

Actualmente, el proyecto se encuentra en fase de Beta Avanzada (rama develop). La arquitectura base está completamente consolidada y todos los módulos críticos de negocio se encuentran operativos e integrados.

Funcionalidades Completadas

   * Arquitectura de Software: Implementación estricta del patrón MVC (Modelo-Vista-Controlador) apoyado en los patrones de diseño DAO/VO y Singleton para garantizar una conexión a base de datos (MySQL) robusta y escalable.

   * Seguridad y Acceso: Sistema de autenticación de usuarios (Login) validado contra la base de datos.

   * Módulos de Gestión Base: Interfaces de usuario y lógica de negocio (Altas, modificaciones y consultas) operativas para las entidades principales: Clientes, Vehículos, Proveedores, Productos/Recambios y Empleados.

   * Gestión de Taller: Flujo de trabajo completo para la recepción de vehículos, registro de inspecciones (kilometraje, combustible, diagnósticos) y generación de Órdenes de Reparación con asignación de mecánicos.

   * Módulo Económico: Sistema interactivo para la creación de Presupuestos (con recálculo dinámico de subtotales, mano de obra e IVA) y su posterior conversión automática a Facturas registradas.

   * Reportes y Documentación: Integración de la librería JasperReports para la generación y exportación de documentos oficiales (Facturas y Presupuestos) en formato PDF.

   * Interfaz de Usuario (UX/UI): Capa de presentación intuitiva impulsada por MaterialLookAndFeel, con validaciones de campos, bloqueos lógicos de edición y navegación fluida por pestañas.


## 7. Próximos objetivos
   De cara a la presentación y entrega final del proyecto (Release Candidate), el desarrollo se centrará en:

   * Finalización del Módulo de Gestión de Stock e Inventario. Actualmente se está implementando la lógica para automatizar el descuento de piezas al facturar y el sistema de alertas para umbrales mínimos de reposición
   
   * Refactorización y limpieza de código muerto (Dead Code).

   * Pruebas integrales (Testing) de todos los flujos de extremo a extremo.

   * Empaquetado final de la aplicación en un ejecutable .jar.

   * Redacción del manual de usuario y finalización de la memoria técnica del proyecto.


-- =====================================================================
-- SCRIPT DE BASE DE DATOS - APPTaller
-- Versión: 1.0 
-- Autor: José Luis Cárdenas Barroso
-- Fecha: Diciembre 2025
-- =====================================================================

-- Creación de la base de datos
CREATE DATABASE IF NOT EXISTS apptaller_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE apptaller_db;

-- =====================================================================
-- SECCIÓN 1: GESTIÓN DE USUARIOS Y EMPLEADOS
-- =====================================================================

-- Tabla de Usuarios (Autenticación y control de acceso)
CREATE TABLE Usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    rol ENUM('Empleado', 'Administrador') NOT NULL DEFAULT 'Empleado',
    activo BOOLEAN DEFAULT TRUE,
    ultimo_acceso DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_rol (rol)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de Empleados (Datos personales y profesionales)
CREATE TABLE Empleados (
    id_empleado INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT UNIQUE COMMENT 'FK opcional a Usuarios. NULL si no tiene acceso al sistema',
    dni VARCHAR(10) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    telefono VARCHAR(15),
    email VARCHAR(100) UNIQUE,
    direccion VARCHAR(255),
    cargo VARCHAR(50) DEFAULT 'Mecánico' COMMENT 'Ej: Mecánico, Jefe de Taller, Recepcionista',
    fecha_alta DATE NOT NULL,
    fecha_baja DATE,
    foto_perfil VARCHAR(255) COMMENT 'Ruta relativa a la imagen de perfil',
    salario_base DECIMAL(10, 2),
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (usuario_id) REFERENCES Usuarios(id_usuario) ON DELETE SET NULL,
    INDEX idx_dni (dni),
    INDEX idx_activo (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =====================================================================
-- SECCIÓN 2: GESTIÓN DE CLIENTES Y VEHÍCULOS
-- =====================================================================

-- Tabla de Clientes
CREATE TABLE Clientes (
    dni VARCHAR(10) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(255),
    telefono VARCHAR(15),
    email VARCHAR(100) UNIQUE,
    fecha_registro DATE NOT NULL,
    notas TEXT COMMENT 'Observaciones sobre el cliente',
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_nombre (nombre),
    INDEX idx_telefono (telefono),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de Vehículos
CREATE TABLE Vehiculos (
    bastidor VARCHAR(50) PRIMARY KEY,
    matricula VARCHAR(10) NOT NULL UNIQUE,
    marca VARCHAR(50),
    modelo VARCHAR(50),
    color VARCHAR(30),
    anio_fabricacion YEAR,
    tipo_combustible ENUM('Gasolina', 'Diesel', 'Híbrido', 'Eléctrico', 'GLP', 'Otro'),
    propietario_actual_dni VARCHAR(10),
    fecha_compra DATE,
    kilometraje_actual INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (propietario_actual_dni) REFERENCES Clientes(dni) ON DELETE SET NULL,
    INDEX idx_matricula (matricula),
    INDEX idx_propietario (propietario_actual_dni),
    INDEX idx_marca_modelo (marca, modelo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de Historial_Propiedad (Relación M:N histórica)
CREATE TABLE Historial_Propiedad (
    id_registro INT AUTO_INCREMENT PRIMARY KEY,
    vehiculo_bastidor VARCHAR(50) NOT NULL,
    cliente_dni VARCHAR(10) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE,
    precio_compra DECIMAL(10, 2),
    observaciones TEXT,
    
    FOREIGN KEY (vehiculo_bastidor) REFERENCES Vehiculos(bastidor) ON DELETE CASCADE,
    FOREIGN KEY (cliente_dni) REFERENCES Clientes(dni) ON DELETE CASCADE,
    INDEX idx_vehiculo (vehiculo_bastidor),
    INDEX idx_cliente (cliente_dni),
    INDEX idx_fecha_inicio (fecha_inicio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =====================================================================
-- SECCIÓN 3: GESTIÓN DE PROVEEDORES E INVENTARIO
-- =====================================================================

-- Tabla de Proveedores
CREATE TABLE Proveedores (
    cif VARCHAR(10) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(255),
    telefono VARCHAR(15),
    email VARCHAR(100) UNIQUE,
    contacto VARCHAR(100) COMMENT 'Nombre de la persona de contacto',
    web VARCHAR(150),
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de Productos (Suministros y piezas)
CREATE TABLE Productos (
    id_producto VARCHAR(50) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    categoria VARCHAR(50) COMMENT 'Ej: Aceites, Filtros, Neumáticos, Frenos, etc.',
    cantidad_stock INT NOT NULL DEFAULT 0,
    stock_minimo INT DEFAULT 5 COMMENT 'Umbral para alertas de stock bajo',
    precio_compra DECIMAL(10, 2),
    precio_unitario DECIMAL(10, 2) NOT NULL COMMENT 'Precio de venta',
    proveedor_cif VARCHAR(10),
    ubicacion_almacen VARCHAR(50),
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (proveedor_cif) REFERENCES Proveedores(cif) ON DELETE SET NULL,
    INDEX idx_nombre (nombre),
    INDEX idx_categoria (categoria),
    INDEX idx_stock_bajo (cantidad_stock),
    INDEX idx_proveedor (proveedor_cif),
    
    CONSTRAINT chk_stock_no_negativo CHECK (cantidad_stock >= 0),
    CONSTRAINT chk_precio_positivo CHECK (precio_unitario > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de Movimientos_Stock (Auditoría de cambios en inventario)
CREATE TABLE Movimientos_Stock (
    id_movimiento INT AUTO_INCREMENT PRIMARY KEY,
    id_producto VARCHAR(50) NOT NULL,
    tipo_movimiento ENUM('ENTRADA', 'SALIDA', 'AJUSTE', 'DEVOLUCION') NOT NULL,
    cantidad INT NOT NULL,
    stock_anterior INT NOT NULL,
    stock_posterior INT NOT NULL,
    motivo VARCHAR(255),
    usuario_id INT,
    fecha_movimiento DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (id_producto) REFERENCES Productos(id_producto) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES Usuarios(id_usuario) ON DELETE SET NULL,
    INDEX idx_producto (id_producto),
    INDEX idx_fecha (fecha_movimiento),
    INDEX idx_tipo (tipo_movimiento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =====================================================================
-- SECCIÓN 4: GESTIÓN DE PRESUPUESTOS
-- =====================================================================

-- Tabla de Presupuestos
CREATE TABLE Presupuestos (
    id_presupuesto INT AUTO_INCREMENT PRIMARY KEY,
    vehiculo_bastidor VARCHAR(50) NOT NULL,
    cliente_dni VARCHAR(10) NOT NULL,
    fecha_emision DATE NOT NULL,
    fecha_validez DATE COMMENT 'Fecha hasta la que es válido el presupuesto',
    estado ENUM('PENDIENTE', 'APROBADO', 'RECHAZADO') NOT NULL DEFAULT 'PENDIENTE',
    descripcion_trabajo TEXT,
    total_estimado DECIMAL(10, 2),
    observaciones TEXT,
    motivo_rechazo TEXT COMMENT 'Si estado=RECHAZADO, motivo del rechazo',
    usuario_creador INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (vehiculo_bastidor) REFERENCES Vehiculos(bastidor),
    FOREIGN KEY (cliente_dni) REFERENCES Clientes(dni),
    FOREIGN KEY (usuario_creador) REFERENCES Usuarios(id_usuario) ON DELETE SET NULL,
    INDEX idx_cliente (cliente_dni),
    INDEX idx_vehiculo (vehiculo_bastidor),
    INDEX idx_estado (estado),
    INDEX idx_fecha_emision (fecha_emision),
    
    CONSTRAINT chk_total_positivo CHECK (total_estimado >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Detalle_Mano_Obra (Líneas de trabajo del presupuesto)
CREATE TABLE Detalle_Mano_Obra (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_presupuesto INT NOT NULL,
    descripcion_trabajo VARCHAR(255) NOT NULL,
    tiempo_empleado_horas DECIMAL(5, 2) NOT NULL,
    tarifa_por_hora DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) GENERATED ALWAYS AS (tiempo_empleado_horas * tarifa_por_hora) STORED,
    
    FOREIGN KEY (id_presupuesto) REFERENCES Presupuestos(id_presupuesto) ON DELETE CASCADE,
    INDEX idx_presupuesto (id_presupuesto),
    
    CONSTRAINT chk_tiempo_positivo CHECK (tiempo_empleado_horas > 0),
    CONSTRAINT chk_tarifa_positiva CHECK (tarifa_por_hora > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla Detalle_Productos (Productos usados en el presupuesto)
CREATE TABLE Detalle_Productos (
    id_presupuesto INT NOT NULL,
    id_producto VARCHAR(50) NOT NULL,
    cantidad_usada INT NOT NULL,
    precio_venta_unitario DECIMAL(10, 2) NOT NULL,
    descuento DECIMAL(5, 2) DEFAULT 0 CHECK (descuento >= 0 AND descuento <= 100),
    subtotal DECIMAL(10, 2) GENERATED ALWAYS AS (
        cantidad_usada * precio_venta_unitario * (1 - descuento/100)
    ) STORED,
    
    PRIMARY KEY (id_presupuesto, id_producto),
    FOREIGN KEY (id_presupuesto) REFERENCES Presupuestos(id_presupuesto) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES Productos(id_producto),
    
    CONSTRAINT chk_cantidad_positiva CHECK (cantidad_usada > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
-- SECCIÓN 5: GESTIÓN DE REPARACIONES (NUEVO - NÚCLEO OPERATIVO)
-- =====================================================================

-- Tabla de Reparaciones (Órdenes de trabajo)
CREATE TABLE Reparaciones (
    id_reparacion INT AUTO_INCREMENT PRIMARY KEY,
    vehiculo_bastidor VARCHAR(50) NOT NULL,
    cliente_dni VARCHAR(10) NOT NULL,
    empleado_asignado_id INT NOT NULL COMMENT 'Mecánico responsable',
    id_presupuesto INT NULL COMMENT 'FK opcional. Puede haber reparaciones sin presupuesto previo',
    
    -- Fechas y tiempos
    fecha_entrada DATETIME NOT NULL,
    fecha_salida_estimada DATE,
    fecha_salida_real DATETIME,
    
    -- Datos del vehículo al entrar
    kilometraje_entrada INT,
    nivel_combustible ENUM('Vacío', '1/4', '1/2', '3/4', 'Lleno'),
    
    -- Estado de la reparación
    estado ENUM(
        'EN_COLA',       -- Vehículo en espera, no se ha empezado
        'EN_PROCESO',    -- Se está reparando actualmente
        'PAUSADA',       -- Trabajo pausado (esperando piezas, autorización, etc.)
        'FINALIZADA',    -- Reparación completada, pendiente de facturar/entregar
        'ENTREGADA'      -- Vehículo entregado al cliente
    ) NOT NULL DEFAULT 'EN_COLA',
    
    prioridad ENUM('Baja', 'Normal', 'Alta', 'Urgente') DEFAULT 'Normal',
    
    -- Observaciones
    diagnostico TEXT COMMENT 'Diagnóstico inicial del problema',
    trabajos_realizados TEXT COMMENT 'Descripción detallada de los trabajos',
    observaciones TEXT,
    
    -- Auditoría
    usuario_recepcion INT COMMENT 'Usuario que registró la entrada',
    usuario_entrega INT COMMENT 'Usuario que entregó el vehículo',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (vehiculo_bastidor) REFERENCES Vehiculos(bastidor),
    FOREIGN KEY (cliente_dni) REFERENCES Clientes(dni),
    FOREIGN KEY (empleado_asignado_id) REFERENCES Empleados(id_empleado),
    FOREIGN KEY (id_presupuesto) REFERENCES Presupuestos(id_presupuesto) ON DELETE SET NULL,
    FOREIGN KEY (usuario_recepcion) REFERENCES Usuarios(id_usuario) ON DELETE SET NULL,
    FOREIGN KEY (usuario_entrega) REFERENCES Usuarios(id_usuario) ON DELETE SET NULL,
    
    INDEX idx_vehiculo (vehiculo_bastidor),
    INDEX idx_cliente (cliente_dni),
    INDEX idx_empleado (empleado_asignado_id),
    INDEX idx_estado (estado),
    INDEX idx_fecha_entrada (fecha_entrada),
    INDEX idx_prioridad (prioridad),
    
    CONSTRAINT chk_fecha_salida_posterior CHECK (
        fecha_salida_real IS NULL OR fecha_salida_real >= fecha_entrada
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de Reparacion_Detalle_Mano_Obra (Trabajo real realizado en la reparación)
-- Puede diferir del presupuesto si hubo cambios durante la reparación
CREATE TABLE Reparacion_Detalle_Mano_Obra (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_reparacion INT NOT NULL,
    empleado_id INT NOT NULL COMMENT 'Mecánico que realizó este trabajo específico',
    descripcion_trabajo VARCHAR(255) NOT NULL,
    fecha_inicio DATETIME,
    fecha_fin DATETIME,
    tiempo_empleado_horas DECIMAL(5, 2) NOT NULL,
    tarifa_por_hora DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) GENERATED ALWAYS AS (tiempo_empleado_horas * tarifa_por_hora) STORED,
    
    FOREIGN KEY (id_reparacion) REFERENCES Reparaciones(id_reparacion) ON DELETE CASCADE,
    FOREIGN KEY (empleado_id) REFERENCES Empleados(id_empleado),
    INDEX idx_reparacion (id_reparacion),
    INDEX idx_empleado (empleado_id),
    
    CONSTRAINT chk_tiempo_real_positivo CHECK (tiempo_empleado_horas > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de Reparacion_Detalle_Productos (Productos realmente usados)
CREATE TABLE Reparacion_Detalle_Productos (
    id_reparacion INT NOT NULL,
    id_producto VARCHAR(50) NOT NULL,
    cantidad_usada INT NOT NULL,
    precio_venta_unitario DECIMAL(10, 2) NOT NULL,
    descuento DECIMAL(5, 2) DEFAULT 0 CHECK (descuento >= 0 AND descuento <= 100),
    subtotal DECIMAL(10, 2) GENERATED ALWAYS AS (
        cantidad_usada * precio_venta_unitario * (1 - descuento/100)
    ) STORED,
    fecha_aplicacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (id_reparacion, id_producto),
    FOREIGN KEY (id_reparacion) REFERENCES Reparaciones(id_reparacion) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES Productos(id_producto),
    
    CONSTRAINT chk_cantidad_real_positiva CHECK (cantidad_usada > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
-- SECCIÓN 6: GESTIÓN DE FACTURAS
-- =====================================================================

-- Tabla de Facturas (Modificada para vincular con Reparaciones)
CREATE TABLE Facturas (
    id_factura INT AUTO_INCREMENT PRIMARY KEY,
    numero_factura VARCHAR(20) NOT NULL UNIQUE COMMENT 'Formato: FACT-YYYY-NNNN',
    
    -- Relaciones (una factura se basa en una reparación, que puede tener un presupuesto)
    id_reparacion INT NOT NULL UNIQUE COMMENT 'Relación 1:1 con Reparaciones',
    id_presupuesto INT NULL COMMENT 'Presupuesto origen (si existe)',
    
    cliente_dni VARCHAR(10) NOT NULL,
    vehiculo_bastidor VARCHAR(50) NOT NULL,
    
    -- Fechas y montos
    fecha_emision DATE NOT NULL,
    fecha_vencimiento DATE,
    fecha_cobro DATE,
    total_cobrado DECIMAL(10, 2) NOT NULL,
    
    -- Método de pago
    metodo_pago ENUM('Efectivo', 'Tarjeta', 'Transferencia', 'Financiación', 'Mixto'),
    
    -- Estado
    estado ENUM('EMITIDA', 'PAGADA', 'PENDIENTE', 'ANULADA') DEFAULT 'EMITIDA',
    motivo_anulacion TEXT,
    
    -- IVA y descuentos
    base_imponible DECIMAL(10, 2),
    iva DECIMAL(5, 2) DEFAULT 21.00,
    descuento_global DECIMAL(5, 2) DEFAULT 0,
    
    -- Observaciones y auditoría
    observaciones TEXT,
    usuario_emisor INT,
    fecha_anulacion DATETIME,
    usuario_anulacion INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (id_reparacion) REFERENCES Reparaciones(id_reparacion),
    FOREIGN KEY (id_presupuesto) REFERENCES Presupuestos(id_presupuesto) ON DELETE SET NULL,
    FOREIGN KEY (cliente_dni) REFERENCES Clientes(dni),
    FOREIGN KEY (vehiculo_bastidor) REFERENCES Vehiculos(bastidor),
    FOREIGN KEY (usuario_emisor) REFERENCES Usuarios(id_usuario) ON DELETE SET NULL,
    FOREIGN KEY (usuario_anulacion) REFERENCES Usuarios(id_usuario) ON DELETE SET NULL,
    
    INDEX idx_numero_factura (numero_factura),
    INDEX idx_cliente (cliente_dni),
    INDEX idx_fecha_emision (fecha_emision),
    INDEX idx_estado (estado),
    INDEX idx_reparacion (id_reparacion),
    
    CONSTRAINT chk_total_factura_positivo CHECK (total_cobrado >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =====================================================================
-- SECCIÓN 7: TRIGGERS Y FUNCIONES AUXILIARES
-- =====================================================================

-- Trigger: Actualizar stock al crear/modificar detalle de reparación
DELIMITER //

CREATE TRIGGER after_reparacion_producto_insert
AFTER INSERT ON Reparacion_Detalle_Productos
FOR EACH ROW
BEGIN
    -- Descontar stock
    UPDATE Productos 
    SET cantidad_stock = cantidad_stock - NEW.cantidad_usada
    WHERE id_producto = NEW.id_producto;
    
    -- Registrar movimiento
    INSERT INTO Movimientos_Stock (
        id_producto, 
        tipo_movimiento, 
        cantidad, 
        stock_anterior, 
        stock_posterior,
        motivo
    )
    SELECT 
        NEW.id_producto,
        'SALIDA',
        NEW.cantidad_usada,
        cantidad_stock + NEW.cantidad_usada,
        cantidad_stock,
        CONCAT('Usado en reparación #', NEW.id_reparacion)
    FROM Productos
    WHERE id_producto = NEW.id_producto;
END//

-- Trigger: Actualizar kilometraje del vehículo al finalizar reparación
CREATE TRIGGER after_reparacion_finalizada
AFTER UPDATE ON Reparaciones
FOR EACH ROW
BEGIN
    IF NEW.estado = 'ENTREGADA' AND OLD.estado != 'ENTREGADA' THEN
        UPDATE Vehiculos
        SET kilometraje_actual = NEW.kilometraje_entrada
        WHERE bastidor = NEW.vehiculo_bastidor
        AND (kilometraje_actual IS NULL OR NEW.kilometraje_entrada > kilometraje_actual);
    END IF;
END//

-- Trigger: Validar que una factura solo se puede anular si no tiene más de 30 días
CREATE TRIGGER before_factura_anular
BEFORE UPDATE ON Facturas
FOR EACH ROW
BEGIN
    IF NEW.estado = 'ANULADA' AND OLD.estado != 'ANULADA' THEN
        IF DATEDIFF(NOW(), OLD.fecha_emision) > 30 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'No se puede anular una factura con más de 30 días de antigüedad';
        END IF;
        SET NEW.fecha_anulacion = NOW();
    END IF;
END//

DELIMITER ;


-- =====================================================================
-- SECCIÓN 8: VISTAS ÚTILES
-- =====================================================================

-- Vista: Vehículos actualmente en el taller
CREATE VIEW vw_vehiculos_en_taller AS
SELECT 
    r.id_reparacion,
    r.vehiculo_bastidor,
    v.matricula,
    v.marca,
    v.modelo,
    c.nombre AS nombre_cliente,
    c.telefono AS telefono_cliente,
    e.nombre AS nombre_empleado,
    e.apellidos AS apellidos_empleado,
    r.fecha_entrada,
    r.fecha_salida_estimada,
    r.estado,
    r.prioridad,
    r.diagnostico,
    DATEDIFF(NOW(), r.fecha_entrada) AS dias_en_taller
FROM Reparaciones r
INNER JOIN Vehiculos v ON r.vehiculo_bastidor = v.bastidor
INNER JOIN Clientes c ON r.cliente_dni = c.dni
INNER JOIN Empleados e ON r.empleado_asignado_id = e.id_empleado
WHERE r.estado IN ('EN_COLA', 'EN_PROCESO', 'PAUSADA', 'FINALIZADA')
ORDER BY r.prioridad DESC, r.fecha_entrada ASC;

-- Vista: Productos con stock bajo
CREATE VIEW vw_productos_stock_bajo AS
SELECT 
    p.id_producto,
    p.nombre,
    p.categoria,
    p.cantidad_stock,
    p.stock_minimo,
    (p.stock_minimo - p.cantidad_stock) AS cantidad_faltante,
    pr.nombre AS proveedor,
    pr.telefono AS telefono_proveedor,
    pr.email AS email_proveedor
FROM Productos p
LEFT JOIN Proveedores pr ON p.proveedor_cif = pr.cif
WHERE p.cantidad_stock <= p.stock_minimo
AND p.activo = TRUE
ORDER BY (p.stock_minimo - p.cantidad_stock) DESC;

-- Vista: Resumen financiero de facturas
CREATE VIEW vw_resumen_facturas AS
SELECT 
    DATE_FORMAT(fecha_emision, '%Y-%m') AS mes,
    COUNT(*) AS total_facturas,
    SUM(CASE WHEN estado = 'PAGADA' THEN 1 ELSE 0 END) AS facturas_pagadas,
    SUM(CASE WHEN estado = 'PENDIENTE' THEN 1 ELSE 0 END) AS facturas_pendientes,
    SUM(total_cobrado) AS total_facturado,
    SUM(CASE WHEN estado = 'PAGADA' THEN total_cobrado ELSE 0 END) AS total_cobrado,
    SUM(CASE WHEN estado = 'PENDIENTE' THEN total_cobrado ELSE 0 END) AS total_pendiente
FROM Facturas
WHERE estado != 'ANULADA'
GROUP BY DATE_FORMAT(fecha_emision, '%Y-%m')
ORDER BY mes DESC;

-- Vista: Historial completo de vehículos
CREATE VIEW vw_historial_vehiculo AS
SELECT 
    v.bastidor,
    v.matricula,
    'Reparación' AS tipo_registro,
    r.id_reparacion AS id_registro,
    r.fecha_entrada AS fecha,
    r.diagnostico AS descripcion,
    COALESCE(f.total_cobrado, 0) AS importe,
    r.estado
FROM Vehiculos v
LEFT JOIN Reparaciones r ON v.bastidor = r.vehiculo_bastidor
LEFT JOIN Facturas f ON r.id_reparacion = f.id_reparacion
UNION ALL
SELECT 
    v.bastidor,
    v.matricula,
    'Cambio de Propietario' AS tipo_registro,
    hp.id_registro AS id_registro,
    hp.fecha_inicio AS fecha,
    CONCAT('Nuevo propietario: ', c.nombre) AS descripcion,
    hp.precio_compra AS importe,
    'Completado' AS estado
FROM Vehiculos v
LEFT JOIN Historial_Propiedad hp ON v.bastidor = hp.vehiculo_bastidor
LEFT JOIN Clientes c ON hp.cliente_dni = c.dni
ORDER BY fecha DESC;


-- =====================================================================
-- SECCIÓN 9: DATOS INICIALES (OPCIONAL)
-- =====================================================================

-- Usuario administrador por defecto (contraseña: admin123 - CAMBIAR EN PRODUCCIÓN)
-- Hash BCrypt de "admin123": $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
INSERT INTO Usuarios (username, password_hash, rol) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Administrador');

-- Empleado de ejemplo vinculado al usuario admin
INSERT INTO Empleados (usuario_id, dni, nombre, apellidos, telefono, email, cargo, fecha_alta) VALUES
(1, '12345678Z', 'José Luis', 'Cárdenas Barroso', '666777888', 'admin@apptaller.com', 'Jefe de Taller', CURDATE());

-- =====================================================================
-- FIN DEL SCRIPT
-- =====================================================================



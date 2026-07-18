CREATE DATABASE BancoSimulacion;


USE BancoSimulacion;
GO



CREATE TABLE usuarios (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    username        NVARCHAR(50)  NOT NULL UNIQUE,
    password_hash   NVARCHAR(200) NOT NULL,
    rol             NVARCHAR(20)  NOT NULL
        CONSTRAINT CK_usuarios_rol CHECK (rol IN ('ADMINISTRADOR', 'ESTANDAR')),
    fecha_creacion  DATETIME2     NOT NULL DEFAULT SYSDATETIME()
);
GO



CREATE TABLE clientes (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    nombre          NVARCHAR(80)  NOT NULL,
    apellido        NVARCHAR(80)  NOT NULL,
    cedula          NVARCHAR(10)  NOT NULL UNIQUE,
    email           NVARCHAR(120) NOT NULL,
    telefono        NVARCHAR(10)  NOT NULL,
    direccion       NVARCHAR(200) NULL,
    usuario_id      BIGINT        NOT NULL UNIQUE,
    fecha_registro  DATETIME2     NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT FK_clientes_usuario FOREIGN KEY (usuario_id)
        REFERENCES dbo.usuarios(id)
);
GO


CREATE TABLE cuentas (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    numero_cuenta   NVARCHAR(10)   NOT NULL UNIQUE,
    cliente_id      BIGINT         NOT NULL,
    tipo            NVARCHAR(20)   NOT NULL
        CONSTRAINT CK_cuentas_tipo CHECK (tipo IN ('AHORROS', 'CORRIENTE')),
    saldo           DECIMAL(14,2)  NOT NULL DEFAULT 0
        CONSTRAINT CK_cuentas_saldo CHECK (saldo >= 0),
    estado          NVARCHAR(20)   NOT NULL DEFAULT 'ACTIVA'
        CONSTRAINT CK_cuentas_estado CHECK (estado IN ('ACTIVA', 'INACTIVA')),
    fecha_apertura  DATETIME2      NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT FK_cuentas_cliente FOREIGN KEY (cliente_id)
        REFERENCES dbo.clientes(id)
);
GO

CREATE TABLE transacciones (
    id                BIGINT IDENTITY(1,1) PRIMARY KEY,
    tipo              NVARCHAR(20)  NOT NULL
        CONSTRAINT CK_transacciones_tipo CHECK (tipo IN ('DEPOSITO', 'RETIRO', 'TRANSFERENCIA')),
    monto             DECIMAL(14,2) NOT NULL
        CONSTRAINT CK_transacciones_monto CHECK (monto > 0),
    cuenta_origen_id  BIGINT NULL,
    cuenta_destino_id BIGINT NULL,
    fecha             DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    usuario_id        BIGINT NOT NULL,
    CONSTRAINT FK_transacciones_origen  FOREIGN KEY (cuenta_origen_id)  REFERENCES dbo.cuentas(id),
    CONSTRAINT FK_transacciones_destino FOREIGN KEY (cuenta_destino_id) REFERENCES dbo.cuentas(id),
    CONSTRAINT FK_transacciones_usuario FOREIGN KEY (usuario_id)        REFERENCES dbo.usuarios(id)
);
GO


CREATE TRIGGER trg_cuentas_saldo_no_negativo
ON dbo.cuentas
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS (SELECT 1 FROM inserted WHERE saldo < 0)
    BEGIN
        RAISERROR('No se permite un saldo negativo en la cuenta.', 16, 1);
        ROLLBACK TRANSACTION;
    END
END
GO




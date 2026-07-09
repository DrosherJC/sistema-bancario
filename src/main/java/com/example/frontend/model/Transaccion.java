package com.example.frontend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaccion {
    private Long id;
    private TipoTransaccion tipo;
    private BigDecimal monto;
    private Long cuentaOrigenId;
    private Long cuentaDestinoId;
    private String cuentaOrigenNumero;
    private String cuentaDestinoNumero;
    private String clienteOrigenNombre;
    private String clienteDestinoNombre;
    private LocalDateTime fecha;
    private Long usuarioId;

    public Transaccion() {
    }

    public Transaccion(Long id, TipoTransaccion tipo, BigDecimal monto, Long cuentaOrigenId,
                       Long cuentaDestinoId, LocalDateTime fecha, Long usuarioId) {
        this.id = id;
        this.tipo = tipo;
        this.monto = monto;
        this.cuentaOrigenId = cuentaOrigenId;
        this.cuentaDestinoId = cuentaDestinoId;
        this.fecha = fecha;
        this.usuarioId = usuarioId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TipoTransaccion getTipo() { return tipo; }
    public void setTipo(TipoTransaccion tipo) { this.tipo = tipo; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public Long getCuentaOrigenId() { return cuentaOrigenId; }
    public void setCuentaOrigenId(Long cuentaOrigenId) { this.cuentaOrigenId = cuentaOrigenId; }

    public Long getCuentaDestinoId() { return cuentaDestinoId; }
    public void setCuentaDestinoId(Long cuentaDestinoId) { this.cuentaDestinoId = cuentaDestinoId; }

    public String getCuentaOrigenNumero() { return cuentaOrigenNumero; }
    public void setCuentaOrigenNumero(String cuentaOrigenNumero) { this.cuentaOrigenNumero = cuentaOrigenNumero; }

    public String getCuentaDestinoNumero() { return cuentaDestinoNumero; }
    public void setCuentaDestinoNumero(String cuentaDestinoNumero) { this.cuentaDestinoNumero = cuentaDestinoNumero; }

    public String getClienteOrigenNombre() { return clienteOrigenNombre; }
    public void setClienteOrigenNombre(String clienteOrigenNombre) { this.clienteOrigenNombre = clienteOrigenNombre; }

    public String getClienteDestinoNombre() { return clienteDestinoNombre; }
    public void setClienteDestinoNombre(String clienteDestinoNombre) { this.clienteDestinoNombre = clienteDestinoNombre; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

}

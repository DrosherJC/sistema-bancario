package com.example.frontend.model;

import java.math.BigDecimal;

public class Cuenta {

    private Long id;
    private String numeroCuenta;
    private Long clienteId;
    private String clienteNombre;
    private TipoCuenta tipo;
    private BigDecimal saldo;
    private EstadoCuenta estado;

    public Cuenta() {
    }

    public Cuenta(Long id, String numeroCuenta, Long clienteId, TipoCuenta tipo,
                   BigDecimal saldo, EstadoCuenta estado) {
        this.id = id;
        this.numeroCuenta = numeroCuenta;
        this.clienteId = clienteId;
        this.tipo = tipo;
        this.saldo = saldo;
        this.estado = estado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public TipoCuenta getTipo() { return tipo; }
    public void setTipo(TipoCuenta tipo) { this.tipo = tipo; }

    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }

    public EstadoCuenta getEstado() { return estado; }
    public void setEstado(EstadoCuenta estado) { this.estado = estado; }

    @Override
    public String toString() {
        return numeroCuenta + " - " + (clienteNombre != null ? clienteNombre : "");
    }
}

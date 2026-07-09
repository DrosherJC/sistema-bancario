package com.example.frontend.model;

public class Usuario {
    //para recordar la estructura que hemos venido repasando
    //parametros
    private Long id;
    private String username;
    private String passwordHash;
    private Rol rol;

    //constructor(es)
    public Usuario() {}

    public Usuario(Long id, String username, String passwordHash, Rol rol) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    //getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}

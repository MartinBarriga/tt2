package com.example.martin.AndroidApp;

public class Contacto {
    private Long idContacto;
    private Long telefono;
    private String nombre;
    private Boolean recibeSMS;
    private Boolean recibeNotificaciones;
    private Boolean esUsuario;
    private String idUsuario;
    private Boolean enNube;

    public Contacto(Long idContacto, Long telefono, String nombre, Boolean recibeSMS,
                    Boolean recibeNotificaciones, Boolean esUsuario, String idUsuario,
                    Boolean enNube) {
        this.idContacto = idContacto;
        this.telefono = telefono;
        this.nombre = nombre;
        this.recibeSMS = recibeSMS;
        this.recibeNotificaciones = recibeNotificaciones;
        this.esUsuario = esUsuario;
        this.idUsuario = idUsuario;
        this.enNube = enNube;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getTelefono() {
        return telefono;
    }

    public void setTelefono(Long telefono) {
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getRecibeSMS() {
        return recibeSMS;
    }

    public void setRecibeSMS(Boolean messageSelected) {
        recibeSMS = messageSelected;
    }

    public Boolean getRecibeNotificaciones() {
        return recibeNotificaciones;
    }

    public void setRecibeNotificaciones(Boolean notificationSelected) {
        recibeNotificaciones = notificationSelected;
    }

    public Boolean getEsUsuario() {
        return esUsuario;
    }

    public void setEsUsuario(Boolean esUsuario) {
        this.esUsuario = esUsuario;
    }

    public Long getIdContacto() {
        return idContacto;
    }

    public void setIdContacto(Long idContacto) {
        this.idContacto = idContacto;
    }

    public Boolean getEnNube() {
        return enNube;
    }

    public void setEnNube(Boolean enNube) {
        this.enNube = enNube;
    }
}

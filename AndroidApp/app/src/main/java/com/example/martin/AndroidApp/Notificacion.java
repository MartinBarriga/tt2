package com.example.martin.AndroidApp;

public class Notificacion {
    private Long idNotificacion;
    private String fecha;
    private String nombre;
    private String mensaje;
    private Boolean leido;
    private String idUsuario;
    private Boolean enNube;

    public Notificacion(Long idNotificacion, String fecha, String nombre, String mensaje, Boolean leido, String idUsuario, Boolean enNube) {
        this.fecha = fecha;
        this.nombre = nombre;
        this.mensaje = mensaje;
        this.idUsuario = idUsuario;
        this.leido = leido;
        this.idNotificacion = idNotificacion;
        this.enNube = enNube;
    }

    public String getIdUsuario(){
        return idUsuario;
    }
    public Long getIdNotificacion() {
        return idNotificacion;
    }

    public String getFecha() {
        return fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Boolean getLeido() {
        return leido;
    }

    public Boolean getEnNube() {
        return enNube;
    }

    public void setIdNotificacion(Long idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public void setLeido(Boolean leido) {
        this.leido = leido;
    }
    public void setIdUsuario(String idUsuario){
        this.idUsuario = idUsuario;
    }

    public void setEnNube(Boolean enNube) {
        this.enNube = enNube;
    }
}


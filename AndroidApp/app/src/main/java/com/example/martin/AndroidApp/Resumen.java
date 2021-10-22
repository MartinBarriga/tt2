package com.example.martin.AndroidApp;

public class Resumen {
    private Long idResumen;
    private Long idNotificacion;

    String nombre;
    String comentario;
    String desenlace;
    String detalles;
    String duracion;
    int cantidadDePersonasEnviado;
    int seguidores;
    boolean enNube;

    public Resumen(Long idResumen, Long idNotificacion, String nombre, String comentario, String desenlace, String detalles,
                   String duracion, int cantidadDePersonasEnviado, int seguidores, boolean enNube) {
        this.idResumen = idResumen;
        this.idNotificacion = idNotificacion;
        this.nombre = nombre;
        this.comentario = comentario;
        this.desenlace = desenlace;
        this.detalles = detalles;
        this.duracion = duracion;
        this.cantidadDePersonasEnviado = cantidadDePersonasEnviado;
        this.seguidores = seguidores;
        this.enNube = enNube;
    }

    public Long getIdResumen() {
        return idResumen;
    }

    public void setIdResumen(Long idResumen) {
        this.idResumen = idResumen;
    }

    public Long getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(Long idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getDesenlace() {
        return desenlace;
    }

    public void setDesenlace(String desenlace) {
        this.desenlace = desenlace;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public int getCantidadDePersonasEnviado() {
        return cantidadDePersonasEnviado;
    }

    public void setCantidadDePersonasEnviado(int cantidadDePersonasEnviado) {
        this.cantidadDePersonasEnviado = cantidadDePersonasEnviado;
    }

    public int getSeguidores() {
        return seguidores;
    }

    public void setSeguidores(int seguidores) {
        this.seguidores = seguidores;
    }

    public boolean isEnNube() {
        return enNube;
    }

    public void setEnNube(boolean enNube) {
        this.enNube = enNube;
    }
}

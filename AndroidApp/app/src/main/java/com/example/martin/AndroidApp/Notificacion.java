package com.example.martin.AndroidApp;

public class Notificacion {
    private Long idNotificacion;
    private String idUsuario;
    private String idEmergencia;
    private String titulo;
    private int estado;
    private String fecha;
    private Boolean leido;
    private  Boolean esPropia;
    private Boolean enNube;

    public Notificacion(Long idNotificacion,String idUsuario, String idEmergencia, String titulo,
                        int estado, String fecha, Boolean leido, Boolean esPropia, Boolean enNube) {
        this.idNotificacion = idNotificacion;
        this.idUsuario = idUsuario;
        this.idEmergencia = idEmergencia;
        this.titulo = titulo;
        this.estado = estado;
        this.fecha = fecha;
        this.leido = leido;
        this.esPropia = esPropia;
        this.enNube = enNube;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(Long idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTitulo() {
        return titulo;
    }

    public Boolean getLeido() {
        return leido;
    }

    public void setLeido(Boolean leido) {
        this.leido = leido;
    }

    public Boolean getEnNube() {
        return enNube;
    }

    public void setEnNube(Boolean enNube) {
        this.enNube = enNube;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIdEmergencia() {
        return idEmergencia;
    }

    public void setIdEmergencia(String idEmergencia) {
        this.idEmergencia = idEmergencia;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public Boolean getEsPropia() {
        return esPropia;
    }

    public void setEsPropia(Boolean esPropia) {
        this.esPropia = esPropia;
    }
}


package com.example.martin.AndroidApp;

public class Medicion {
    private Long idMedicion;
    private String idUsuario;
    private String fecha;
    private Boolean enNube;

    public Medicion(Long idMedicion, String idUsuario, String fecha, Boolean enNube) {
        this.idMedicion = idMedicion;
        this.idUsuario = idUsuario;
        this.fecha = fecha;
        this.enNube = enNube;
    }

    public Long getIdMedicion() {
        return idMedicion;
    }

    public void setIdMedicion(Long idMedicion) {
        this.idMedicion = idMedicion;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Boolean getEnNube() {
        return enNube;
    }

    public void setEnNube(Boolean enNube) {
        this.enNube = enNube;
    }
}

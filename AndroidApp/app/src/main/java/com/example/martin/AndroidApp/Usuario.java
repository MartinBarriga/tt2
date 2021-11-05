package com.example.martin.AndroidApp;

public class Usuario {
    private String idUsuario;
    private String nombre;
    private Long telefono;
    private int edad;
    private Long nss;
    private String medicacion;
    private String toxicomanias;
    private String tipoSangre;
    private String alergias;
    private String religion;
    private Boolean enNube;
    private String fechaUltimoRespaldo;
    private String frecuenciaRespaldo;
    private int frecuenciaCardiacaMinima;
    private int frecuenciaCardiacaMaxima;
    private Boolean enviaAlertasAUsuariosCercanos;
    private Boolean recibeAlertasDeUsuariosCercanos;

    public Usuario(String idUsuario, String nombre, Long telefono, int edad, Long nss, String medicacion,
                   String toxicomanias, String tipoSangre, String alergias,
                   String religion, Boolean enNube, String fechaUltimoRespaldo, String frecuenciaRespaldo,
                   int frecuenciaCardiacaMinima, int frecuenciaCardiacaMaxima,
                   Boolean enviaAlertasAUsuariosCercanos, Boolean recibeAlertasDeUsuariosCercanos) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.telefono = telefono;
        this.edad = edad;
        this.nss = nss;
        this.medicacion = medicacion;
        this.toxicomanias = toxicomanias;
        this.tipoSangre = tipoSangre;
        this.alergias = alergias;
        this.religion = religion;
        this.enNube = enNube;
        this.fechaUltimoRespaldo = fechaUltimoRespaldo;
        this.frecuenciaRespaldo = frecuenciaRespaldo;
        this.frecuenciaCardiacaMinima = frecuenciaCardiacaMinima;
        this.frecuenciaCardiacaMaxima = frecuenciaCardiacaMaxima;
        this.enviaAlertasAUsuariosCercanos = enviaAlertasAUsuariosCercanos;
        this.recibeAlertasDeUsuariosCercanos = recibeAlertasDeUsuariosCercanos;
    }

    public String getMedicacion() {
        return medicacion;
    }

    public void setMedicacion(String medicacion) {
        this.medicacion = medicacion;
    }

    public Long getTelefono() {
        return telefono;
    }

    public void setTelefono(Long telefono) {
        this.telefono = telefono;
    }

    public Long getNss() {
        return nss;
    }

    public void setNss(Long nss) {
        this.nss = nss;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getToxicomanias() {
        return toxicomanias;
    }

    public void setToxicomanias(String toxicomanias) {
        this.toxicomanias = toxicomanias;
    }

    public String getTipoSangre() {
        return tipoSangre;
    }

    public void setTipoSangre(String tipoSangre) {
        this.tipoSangre = tipoSangre;
    }

    public String getAlergias() {
        return alergias;
    }

    public void setAlergias(String alergias) {
        this.alergias = alergias;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public Boolean getEnNube() {
        return enNube;
    }

    public void setEnNube(Boolean enNube) {
        this.enNube = enNube;
    }

    public String getFechaUltimoRespaldo() {
        return fechaUltimoRespaldo;
    }

    public void setFechaUltimoRespaldo(String fechaUltimoRespaldo) {
        this.fechaUltimoRespaldo = fechaUltimoRespaldo;
    }

    public String getFrecuenciaRespaldo() {
        return frecuenciaRespaldo;
    }

    public void setFrecuenciaRespaldo(String frecuenciaRespaldo) {
        this.frecuenciaRespaldo = frecuenciaRespaldo;
    }

    public int getFrecuenciaCardiacaMinima() {
        return frecuenciaCardiacaMinima;
    }

    public void setFrecuenciaCardiacaMinima(int frecuenciaCardiacaMinima) {
        this.frecuenciaCardiacaMinima = frecuenciaCardiacaMinima;
    }

    public int getFrecuenciaCardiacaMaxima() {
        return frecuenciaCardiacaMaxima;
    }

    public void setFrecuenciaCardiacaMaxima(int frecuenciaCardiacaMaxima) {
        this.frecuenciaCardiacaMaxima = frecuenciaCardiacaMaxima;
    }

    public Boolean getEnviaAlertasAUsuariosCercanos() {
        return enviaAlertasAUsuariosCercanos;
    }

    public void setEnviaAlertasAUsuariosCercanos(Boolean enviaAlertasAUsuariosCercanos) {
        this.enviaAlertasAUsuariosCercanos = enviaAlertasAUsuariosCercanos;
    }

    public Boolean getRecibeAlertasDeUsuariosCercanos() {
        return recibeAlertasDeUsuariosCercanos;
    }

    public void setRecibeAlertasDeUsuariosCercanos(Boolean recibeAlertasDeUsuariosCercanos) {
        this.recibeAlertasDeUsuariosCercanos = recibeAlertasDeUsuariosCercanos;
    }
}

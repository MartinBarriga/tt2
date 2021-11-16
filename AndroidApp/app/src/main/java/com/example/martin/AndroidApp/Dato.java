package com.example.martin.AndroidApp;

public class Dato {
    private Long idDato;
    private Long idMedicion;
    private int frecuenciaCardiaca;
    private int ecg;
    private int spo2;
    private String hora;
    private Boolean enNube;

    public Dato(Long idDato, Long idMedicion, int frecuenciaCardiaca, int ecg, int spo2,
                String hora, Boolean enNube) {
        this.idDato = idDato;
        this.idMedicion = idMedicion;
        this.frecuenciaCardiaca = frecuenciaCardiaca;
        this.ecg = ecg;
        this.spo2 = spo2;
        this.hora = hora;
        this.enNube = enNube;
    }

    public Long getIdDato() {
        return idDato;
    }

    public void setIdDato(Long idDato) {
        this.idDato = idDato;
    }

    public Long getIdMedicion() {
        return idMedicion;
    }

    public void setIdMedicion(Long idMedicion) {
        this.idMedicion = idMedicion;
    }

    public int getFrecuenciaCardiaca() {
        return frecuenciaCardiaca;
    }

    public void setFrecuenciaCardiaca(int frecuenciaCardiaca) {
        this.frecuenciaCardiaca = frecuenciaCardiaca;
    }

    public int getEcg() {
        return ecg;
    }

    public void setEcg(int ecg) {
        this.ecg = ecg;
    }

    public int getSpo2() {
        return spo2;
    }

    public void setSpo2(int spo2) {
        this.spo2 = spo2;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Boolean getEnNube() {
        return enNube;
    }

    public void setEnNube(Boolean enNube) {
        this.enNube = enNube;
    }
}

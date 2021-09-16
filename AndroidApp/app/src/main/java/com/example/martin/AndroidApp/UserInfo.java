package com.example.martin.AndroidApp;

public class UserInfo {
    private String uID;
    private String nombre;
    private Long telefono;
    private String correo;
    private int edad;
    private String mensaje;
    private Long nss;
    private String medicacion;
    private String enfermedades;
    private String toxicomanias;
    private String tipoSangre;
    private String alergias;
    private String religion;

    public UserInfo(String uID, String nombre, Long telefono, String correo, int edad,
                    String mensaje, Long nss, String medicacion, String enfermedades,
                    String toxicomanias, String tipoSangre, String alergias, String religion) {
        this.uID = uID;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.edad = edad;
        this.mensaje = mensaje;
        this.nss = nss;
        this.medicacion = medicacion;
        this.enfermedades = enfermedades;
        this.toxicomanias = toxicomanias;
        this.tipoSangre = tipoSangre;
        this.alergias = alergias;
        this.religion = religion;
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

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getEnfermedades() {
        return enfermedades;
    }

    public void setEnfermedades(String enfermedades) {
        this.enfermedades = enfermedades;
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

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}

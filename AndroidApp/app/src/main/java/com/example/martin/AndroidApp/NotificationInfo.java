package com.example.martin.AndroidApp;

public class NotificationInfo {
    private Long id;
    private String fecha;
    private String nombre;
    private String mensaje;
    private Boolean leido;
    private String userID;

    public NotificationInfo(Long id, String fecha, String nombre, String mensaje, Boolean leido, String userID) {
        this.fecha = fecha;
        this.nombre = nombre;
        this.mensaje = mensaje;
        this.userID = userID;
        this.leido = leido;
        this.id = id;
    }

    public String getUserID(){
        return userID;
    }
    public Long getId() {
        return id;
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

    public void setId(Long id) {
        this.id = id;
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
    public void setUserID(String userID){
        this.userID = userID;
    }
}


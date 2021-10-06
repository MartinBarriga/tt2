package com.example.martin.AndroidApp.ui.contactos;

import android.content.ContentValues;
import android.content.Context;

import com.example.martin.AndroidApp.Contacto;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosLocal;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosNube;

import java.util.ArrayList;

public class ManejadorContactos {

    private ManejadorBaseDeDatosLocal mManejadorBaseDeDatosLocal;
    private ManejadorBaseDeDatosNube mManejadorBaseDeDatosNube;
    private ArrayList<Contacto> mContactos;
    private Context mContexto;

    public ManejadorContactos(Context contexto) {
        mContexto = contexto;
        mContactos = new ArrayList<Contacto>();
        mManejadorBaseDeDatosLocal = new ManejadorBaseDeDatosLocal(contexto, null);
        mManejadorBaseDeDatosNube = new ManejadorBaseDeDatosNube();
        llenarArregloContactos();
    }

    private void llenarArregloContactos() {
        mContactos.clear();
        for (Contacto contacto : mManejadorBaseDeDatosLocal
                .obtenerContactos(mManejadorBaseDeDatosNube.obtenerIdUsuario())) {
            mContactos.add(contacto);
        }
    }

    public void eliminarContacto(int posicion) {
        //delete from database
        ContentValues contacto = mManejadorBaseDeDatosLocal
                .generarFormatoDeContactoParaIntroducirEnBD(mContactos.get(posicion));
        mManejadorBaseDeDatosLocal
                .eliminarContacto(Long.toString(mContactos.get(posicion).getIdContacto()),
                        contacto);
        llenarArregloContactos();
    }

    public void cambiarNombreContacto(int posicion, final String nuevoNombre) {
        mContactos.get(posicion).setNombre(nuevoNombre);
        mContactos.get(posicion).setEnNube(false);
        ContentValues contacto = mManejadorBaseDeDatosLocal
                .generarFormatoDeContactoParaIntroducirEnBD(mContactos.get(posicion));
        mManejadorBaseDeDatosLocal
                .actualizarContacto(Long.toString(mContactos.get(posicion).getIdContacto()),
                        contacto);
    }

    public void cambiarEstadoEnvioDeMensajesSeleccionado(int posicion) {
        if (mContactos.get(posicion).getRecibeSMS()) {
            mContactos.get(posicion).setRecibeSMS(false);
        } else {
            mContactos.get(posicion).setRecibeSMS(true);
        }
        mContactos.get(posicion).setEnNube(false);
        ContentValues contacto = mManejadorBaseDeDatosLocal
                .generarFormatoDeContactoParaIntroducirEnBD(mContactos.get(posicion));
        mManejadorBaseDeDatosLocal
                .actualizarContacto(Long.toString(mContactos.get(posicion).getIdContacto()),
                        contacto);
    }

    private void actualizarCampoNotificacion(final int posicion) {
        ContentValues contacto = mManejadorBaseDeDatosLocal
                .generarFormatoDeContactoParaIntroducirEnBD(mContactos.get(posicion));
        mManejadorBaseDeDatosLocal
                .actualizarContacto(Long.toString(mContactos.get(posicion).getIdContacto()),
                        contacto);
    }

    public void cambiarEstadoEnvioDeNotificacionesSeleccionado(final int posicion) {
        mContactos.get(posicion).setEnNube(false);
        if (mContactos.get(posicion).getRecibeNotificaciones()) {
            mContactos.get(posicion).setRecibeNotificaciones(false);
            actualizarCampoNotificacion(posicion);
        } else {
            if (mManejadorBaseDeDatosNube
                    .existeNumeroDeContactoRegistradoEnFirebaseComoUsuario(
                            mContactos.get(posicion).getTelefono(), mContexto)) {
                mContactos.get(posicion).setRecibeNotificaciones(true);
                actualizarCampoNotificacion(posicion);
            }
        }
    }

    public void agregarNuevoContacto(Contacto contacto) {
        ContentValues contactoConFormato = mManejadorBaseDeDatosLocal
                .generarFormatoDeContactoParaIntroducirEnBD(contacto);
        Long idNuevoContacto = mManejadorBaseDeDatosLocal.agregarNuevoContacto(contactoConFormato);
        contacto.setIdContacto(idNuevoContacto);
        mContactos.clear();
        llenarArregloContactos();
    }

    public ArrayList<Contacto> obtenerArregloContactos() {
        return mContactos;
    }

}

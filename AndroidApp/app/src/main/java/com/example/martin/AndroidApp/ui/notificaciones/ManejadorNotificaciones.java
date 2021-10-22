package com.example.martin.AndroidApp.ui.notificaciones;


import android.content.Context;

import com.example.martin.AndroidApp.ManejadorBaseDeDatosLocal;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosNube;
import com.example.martin.AndroidApp.Notificacion;

import java.util.ArrayList;

public class ManejadorNotificaciones {
    private ManejadorBaseDeDatosLocal mManejadorBaseDeDatosLocal;
    private ManejadorBaseDeDatosNube mManejadorBaseDeDatosNube;
    private ArrayList<Notificacion> mNotificaciones;
    private Context mContext;

    public ManejadorNotificaciones(Context context) {
        mContext = context;
        mNotificaciones = new ArrayList<Notificacion>();
        mManejadorBaseDeDatosLocal = new ManejadorBaseDeDatosLocal(context, null);
        mManejadorBaseDeDatosNube = new ManejadorBaseDeDatosNube();
        llenarNotificacionArrayList();
    }

    private void llenarNotificacionArrayList() {
        mNotificaciones.clear();
        for (Notificacion notificacion : mManejadorBaseDeDatosLocal
                .obtenerNotificaciones(mManejadorBaseDeDatosNube.obtenerIdUsuario())) {
            mNotificaciones.add(notificacion);
        }
    }

    public void eliminarNotificacion(int posicion) {
        mManejadorBaseDeDatosLocal
                .eliminarNotificacion(mManejadorBaseDeDatosNube.obtenerIdUsuario(),
                        Long.toString(mNotificaciones.get(posicion).getIdNotificacion()));
        llenarNotificacionArrayList();
    }

    public void actualizarCampoLeido(final int posicion) {
        if (!mNotificaciones.get(posicion).getLeido()) {
            mNotificaciones.get(posicion).setLeido(true);
            mNotificaciones.get(posicion).setEnNube(false);
            mManejadorBaseDeDatosLocal
                    .actualizarNotificacion(mManejadorBaseDeDatosNube.obtenerIdUsuario(),
                            mManejadorBaseDeDatosLocal.generarFormatoDeNotificacionParaIntroducirEnBD(
                                    mNotificaciones.get(posicion)));
        }
    }

    public void actualizarEstado(final int posicion, int estado) {
        if (mNotificaciones.get(posicion).getEsPropia() && estado==1)
            mNotificaciones.get(posicion).setEstado(2);
         else
            mNotificaciones.get(posicion).setEstado(estado);
        mNotificaciones.get(posicion).setEnNube(false);
        mManejadorBaseDeDatosLocal
                .actualizarNotificacion(mManejadorBaseDeDatosNube.obtenerIdUsuario(),
                        mManejadorBaseDeDatosLocal.generarFormatoDeNotificacionParaIntroducirEnBD(
                                mNotificaciones.get(posicion)));

    }

    public void actualizarTitulo(final int posicion) {
        if (mNotificaciones.get(posicion).getEsPropia())
            mNotificaciones.get(posicion).setNombre("Se detectó una anomalía en tus mediciones");
        else{
            mNotificaciones.get(posicion).setNombre(mManejadorBaseDeDatosNube.obtenerNombreDeUsuarioConIdDeEmergencia(
                    mNotificaciones.get(posicion).getIdEmergencia())+" tuvo una emergencia");
        }
        mNotificaciones.get(posicion).setEnNube(false);
        mManejadorBaseDeDatosLocal
                .actualizarNotificacion(mManejadorBaseDeDatosNube.obtenerIdUsuario(),
                        mManejadorBaseDeDatosLocal.generarFormatoDeNotificacionParaIntroducirEnBD(
                                mNotificaciones.get(posicion)));
    }

    public ArrayList<Notificacion> getArrayNotifications() {
        return mNotificaciones;
    }

}

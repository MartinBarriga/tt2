package com.example.martin.AndroidApp.ui.dashboard;


import android.content.ContentValues;
import android.content.Context;

import com.example.martin.AndroidApp.ContactsInfo;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosLocal;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosNube;
import com.example.martin.AndroidApp.NotificationInfo;

import java.util.ArrayList;

public class NotificationsManager {
    private ManejadorBaseDeDatosLocal mManejadorBaseDeDatosLocal;
    private ManejadorBaseDeDatosNube mManejadorBaseDeDatosNube;
    private ArrayList<NotificationInfo> mNotifications;
    private Context mContext;

    public NotificationsManager(Context context) {
        mContext = context;
        mNotifications = new ArrayList<NotificationInfo>();
        mManejadorBaseDeDatosLocal = new ManejadorBaseDeDatosLocal(context, null);
        mManejadorBaseDeDatosNube = new ManejadorBaseDeDatosNube();
        fillNotificationArrayList();
    }

    private void fillNotificationArrayList() {
        mNotifications.clear();
        for (NotificationInfo notificacion : mManejadorBaseDeDatosLocal
                .obtenerNotificaciones(mManejadorBaseDeDatosNube.obtenerIdUsuario())) {
            mNotifications.add(notificacion);
        }
    }

    public void deleteNotification(int position) {
        mManejadorBaseDeDatosLocal
                .eliminarNotificacion(mManejadorBaseDeDatosNube.obtenerIdUsuario(),
                        Long.toString(mNotifications.get(position).getId()));
        mManejadorBaseDeDatosNube.eliminarNotificacion(mNotifications.get(position));
        fillNotificationArrayList();
    }


    public ContentValues getNotificationValue(NotificationInfo notification) {
        ContentValues notifiactionValues = new ContentValues();
        notifiactionValues.put("fecha", notification.getFecha());
        notifiactionValues.put("nombre", notification.getNombre());
        notifiactionValues.put("mensaje", notification.getMensaje());

        if (notification.getLeido()) {
            notifiactionValues.put("leido", 1);
        } else {
            notifiactionValues.put("leido", 0);
        }
        notifiactionValues.put("userID", notification.getUserID());
        return notifiactionValues;
    }

    public void addNewNotification(NotificationInfo notification) {
        Long idNotificacion = mManejadorBaseDeDatosLocal
                .agregarNotificacion(getNotificationValue(notification));
        notification.setId(idNotificacion);
        mManejadorBaseDeDatosNube.agregarNotificacion(notification);

        //Toast.makeText(mContext, "Contacto agregado ID: " + idRes, Toast.LENGTH_LONG).show();
        mNotifications.clear();
        fillNotificationArrayList();
    }

    private void updateLeido(final int position) {
        mManejadorBaseDeDatosLocal
                .actualizarNotificacion(mManejadorBaseDeDatosNube.obtenerIdUsuario(),
                        Long.toString(mNotifications.get(position).getId()),
                        getNotificationValue(mNotifications.get(position)));
        mManejadorBaseDeDatosNube.actualizarNotificacion(mNotifications.get(position));
    }

    public void changeLeido(final int position) {
        if (!mNotifications.get(position).getLeido()) {
            mNotifications.get(position).setLeido(true);
            updateLeido(position);
        }
    }

    public ArrayList<NotificationInfo> getArrayNotifications() {
        return mNotifications;
    }

}

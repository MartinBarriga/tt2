package com.example.martin.AndroidApp.ui.usuario;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.martin.AndroidApp.MainActivity;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosLocal;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosNube;
import com.example.martin.AndroidApp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RespaldoAutomatico extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal =
                new ManejadorBaseDeDatosLocal(context, null);
        ManejadorBaseDeDatosNube manejadorBaseDeDatosNube = new ManejadorBaseDeDatosNube();
        ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocalParaObtenerFrecuencia =
                new ManejadorBaseDeDatosLocal(context, null);
        String frecuenciaRespaldo = manejadorBaseDeDatosLocalParaObtenerFrecuencia
                .obtenerUsuario(manejadorBaseDeDatosNube.obtenerIdUsuario())
                .getFrecuenciaRespaldo();
        int equivalenciaEnMilisegundosEnUnDia = 24 * 60 * 60 * 1000;
        int frecuenciaRespaldoEnMillis;
        switch (frecuenciaRespaldo) {
            case "Cada 2 dias":
                frecuenciaRespaldoEnMillis = equivalenciaEnMilisegundosEnUnDia * 2;
                break;
            case "Cada 5 dias":
                frecuenciaRespaldoEnMillis = equivalenciaEnMilisegundosEnUnDia * 5;
                break;
            case "Cada 7 dias":
                frecuenciaRespaldoEnMillis = equivalenciaEnMilisegundosEnUnDia * 7;
                break;
            default:
                frecuenciaRespaldoEnMillis = equivalenciaEnMilisegundosEnUnDia;
                break;
        }

        String fechaUltimoRespaldo = manejadorBaseDeDatosLocal.obtenerUsuario(manejadorBaseDeDatosNube.obtenerIdUsuario()).getFechaUltimoRespaldo();
        Date fechaUltimoRespaldoTipoDate = new Date();
        try {
            fechaUltimoRespaldoTipoDate =
                    new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(fechaUltimoRespaldo);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Long fechaDeRespaldoEsperada = fechaUltimoRespaldoTipoDate.getTime() + frecuenciaRespaldoEnMillis;
        if(System.currentTimeMillis() >= fechaDeRespaldoEsperada) {
            manejadorBaseDeDatosNube.realizarRespaldo(manejadorBaseDeDatosLocal);

            AlarmManager alarma = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intentRespaldo =
                    new Intent(context.getApplicationContext(), RespaldoAutomatico.class);
            PendingIntent intentPendiente =
                    PendingIntent
                            .getBroadcast(context.getApplicationContext(), 0, intentRespaldo, 0);
            if (alarma != null) {
                alarma.cancel(intentPendiente);
            }
            Calendar fechaDeHoy = Calendar.getInstance();
            fechaDeHoy.set(Calendar.HOUR_OF_DAY, 23);
            fechaDeHoy.set(Calendar.MINUTE, 0);
            fechaDeHoy.set(Calendar.SECOND, 0);
            fechaDeHoy.set(Calendar.MILLISECOND, 0);
            alarma.setExact(AlarmManager.RTC_WAKEUP,
                    fechaDeHoy.getTimeInMillis() + frecuenciaRespaldoEnMillis, intentPendiente);


            Intent intentNotificacion =
                    new Intent(context.getApplicationContext(), MainActivity.class);
            intentNotificacion.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("respaldoAutomatico", true);

            PendingIntent intentNotificacionPendiente =
                    PendingIntent.getActivity(context, 0, intentNotificacion,
                            PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notificacionBuilder =
                    new NotificationCompat.Builder(context, "Alerta")
                            .setSmallIcon(R.drawable.ic_baseline_backup_24)
                            .setContentTitle("Respaldo automatico")
                            .setContentText("Un respaldo ha sido programado para el día de hoy")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setVibrate(new long[]{3000, 3000, 3000, 3000, 3000,})
                            .setLights(Color.RED, 2000, 2000)
                            .setContentIntent(intentNotificacionPendiente)
                            .setAutoCancel(true);
            NotificationManagerCompat notificacion = NotificationManagerCompat.from(context);
            Toast.makeText(context, "Respaldo Automatico", Toast.LENGTH_LONG).show();
            notificacion.notify(0, notificacionBuilder.build());
        }
    }
}

package com.example.martin.AndroidApp;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private ManejadorBaseDeDatosLocal mManejadorBaseDeDatosLocal;
    private ManejadorBaseDeDatosNube mManejadorBaseDeDatosNube;
    private FirebaseAuth mAuth;
    FirebaseUser usuarioActual;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        mAuth = FirebaseAuth.getInstance();
        mManejadorBaseDeDatosLocal = new ManejadorBaseDeDatosLocal(getApplicationContext(), null);
        usuarioActual = mAuth.getCurrentUser();
        mManejadorBaseDeDatosNube = new ManejadorBaseDeDatosNube();

        Log.d("LOG", "From: " + remoteMessage.getFrom());

        if (remoteMessage.getFrom().matches("/topics/UsuariosCercanos")){
            if (remoteMessage.getData().size() > 0) {
                notificarSiEsCercano(remoteMessage);
            }
        } else {
            if (remoteMessage.getData().size() > 0) {
                Log.d("LOG", "Message data payload: " + remoteMessage.getData());

                long idNotificacion =
                        mManejadorBaseDeDatosLocal
                                .agregarNotificacion(remoteMessage, usuarioActual.getUid(), 0);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("nuevaAlerta", true);
                intent.putExtra("titulo", remoteMessage.getData().get("titulo"));
                intent.putExtra("idEmergencia", remoteMessage.getData().get("idEmergencia"));
                intent.putExtra("idNotificacion", idNotificacion);
                intent.putExtra("fecha", remoteMessage.getData().get("fecha"));
                intent.putExtra("esPropia", false);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                PendingIntent pendingIntent =
                        PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(this, "Alerta")
                                .setSmallIcon(R.drawable.ic_warning_black_24dp)
                                .setContentTitle(
                                        remoteMessage.getData().get("titulo"))
                                .setContentText("Haz click aquí para ver la notificación y ayudarle.")
                                .setStyle(new NotificationCompat.BigTextStyle())
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setDefaults(Notification.DEFAULT_SOUND)
                                .setSound(alarmSound, AudioManager.STREAM_NOTIFICATION)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(1, builder.build());
            }
        }

    }

    private void notificarSiEsCercano(RemoteMessage remoteMessage) {
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(30000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(getApplicationContext())
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(getApplicationContext())
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            Location miLocalizacion = locationResult.getLocations().get(latestLocationIndex);
                            String[] longitudYLatitud = remoteMessage.getData().get("localizacion").split(",");
                            Double longitud = Double.parseDouble(longitudYLatitud[0]);
                            Double latitud = Double.parseDouble(longitudYLatitud[1]);
                            Location localizacionDeEmergencia = new Location(miLocalizacion);
                            localizacionDeEmergencia.setLongitude(longitud);
                            localizacionDeEmergencia.setLatitude(latitud);
                            float distancia = miLocalizacion.distanceTo(localizacionDeEmergencia);

                            if (distancia < 2000.0){
                                if (!mManejadorBaseDeDatosLocal.existeLaEmergencia(
                                        remoteMessage.getData().get("idEmergencia") , usuarioActual.getUid())) {

                                    long idNotificacion = mManejadorBaseDeDatosLocal.agregarNotificacion(
                                            remoteMessage, usuarioActual.getUid(), 0);

                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.putExtra("nuevaAlerta", true);
                                    intent.putExtra("titulo", "Un usuario cercano tiene una emergencia");
                                    intent.putExtra("idEmergencia", remoteMessage.getData().get("idEmergencia"));
                                    intent.putExtra("idNotificacion", idNotificacion);
                                    intent.putExtra("fecha", remoteMessage.getData().get("fecha"));
                                    intent.putExtra("esPropia", false);
                                    intent.putExtra("localizacion", remoteMessage.getData().get("localizacion"));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    PendingIntent pendingIntent =
                                            PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    NotificationCompat.Builder builder =
                                            new NotificationCompat.Builder(getApplicationContext(), "Alerta")
                                                    .setSmallIcon(R.drawable.ic_warning_black_24dp)
                                                    .setContentTitle("Un usuario cercano tiene una emergencia")
                                                    .setContentText("Haz click aquí para ver la notificación y ayudarle.")
                                                    .setStyle(new NotificationCompat.BigTextStyle())
                                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                    .setDefaults(Notification.DEFAULT_SOUND)
                                                    .setSound(alarmSound, AudioManager.STREAM_NOTIFICATION)
                                                    .setContentIntent(pendingIntent)
                                                    .setAutoCancel(true);

                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                                    notificationManager.notify(1, builder.build());
                                }
                            }
                        }
                    }
                }, Looper.getMainLooper());
    }
}

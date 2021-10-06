package com.example.martin.AndroidApp;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private ManejadorBaseDeDatosLocal mManejadorBaseDeDatosLocal;
    private ManejadorBaseDeDatosNube mManejadorBaseDeDatosNube;
    private FirebaseAuth mAuth;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        mAuth = FirebaseAuth.getInstance();
        mManejadorBaseDeDatosLocal = new ManejadorBaseDeDatosLocal(getApplicationContext(), null);
        FirebaseUser usuarioActual = mAuth.getCurrentUser();
        mManejadorBaseDeDatosNube = new ManejadorBaseDeDatosNube();

        Log.d("LOG", "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d("LOG", "Message data payload: " + remoteMessage.getData());

            String fecha = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss")
                    .format(Calendar.getInstance().getTime());
            long idNotificacion =
                    mManejadorBaseDeDatosLocal
                            .agregarNotificacion(remoteMessage, usuarioActual.getUid(), fecha);

            Notificacion notificacion =
                    new Notificacion(idNotificacion, fecha, remoteMessage.getData().get("nombre"),
                            remoteMessage.getData().get("mensaje"), false,
                            usuarioActual.getUid(), false);

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("nuevaAlerta", true);
            intent.putExtra("nombre", remoteMessage.getData().get("nombre"));
            intent.putExtra("mensaje", remoteMessage.getData().get("mensaje"));
            String idUsuario = usuarioActual.getUid();
            intent.putExtra("idUsuario", idUsuario);
            intent.putExtra("idNotificacion", idNotificacion);
            intent.putExtra("fecha", fecha);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent =
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this, "Alerta LifeGuard")
                            .setSmallIcon(R.drawable.ic_warning_black_24dp)
                            .setContentTitle(
                                    "Nueva alerta de " + remoteMessage.getData().get("nombre"))
                            .setContentText("Has recibido una alerta de " +
                                    remoteMessage.getData().get("nombre") +
                                    ". Haz click aquí para ver la notificación y ayudarle.")
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText("Has recibido una alerta de " +
                                            remoteMessage.getData().get("nombre") +
                                            ". Haz click aquí para ver la notificación y ayudarle" +
                                            "."))
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setVibrate(new long[]{3000, 3000, 3000, 3000, 3000,})
                            .setLights(Color.RED, 2000, 2000)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(1, builder.build());

        }
    }
}

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

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this, "Alerta LifeGuard")
                            .setSmallIcon(R.drawable.ic_warning_black_24dp)
                            .setContentTitle(
                                    remoteMessage.getData().get("titulo"))
                            .setContentText("Haz click aquí para ver la notificación y ayudarle.")
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(remoteMessage.getData().get("titulo") +
                                            "Haz click aquí para ver la notificación y ayudarle."))
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

package com.example.martin.AndroidApp;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;

import com.example.martin.AndroidApp.ui.dashboard.NotificationInfo;
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
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private ConexionSQLiteHelper mConectionSQLiteHelper;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        mAuth = FirebaseAuth.getInstance();
        mConectionSQLiteHelper = new ConexionSQLiteHelper(getApplicationContext(), "lifeguard", null, 2);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        Log.d("LOG", "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d("LOG", "Message data payload: " + remoteMessage.getData());

            Date c = Calendar.getInstance().getTime();
            Log.d("LOG","Current time => " + c);

            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            String formattedDate = df.format(c);
            Log.d("LOG","Current time FORMATTED => " + formattedDate);

            SQLiteDatabase writingDatabase = mConectionSQLiteHelper.getWritableDatabase();
            ContentValues userValues = new ContentValues();
            userValues.put("fecha", formattedDate);
            userValues.put("nombre", remoteMessage.getData().get("nombre"));
            userValues.put("mensaje", remoteMessage.getData().get("mensaje"));
            userValues.put("leido", 0);
            userValues.put("userID", currentUser.getUid());
            long idRes = writingDatabase.insert("notificacion", "id", userValues);
            Log.d("LOG", "Notificación agregada. ID: "+idRes);
            writingDatabase.close();

            NotificationInfo notification = new NotificationInfo(idRes, formattedDate, userValues.get("nombre").toString(), userValues.get("mensaje").toString(), false, userValues.get("userID").toString());

            db.collection("notificacion").add(notification)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("LOG", "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("LOG", "Error adding document", e);
                        }
                    });

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("nuevaAlerta", true);
            intent.putExtra("nombre", remoteMessage.getData().get("nombre"));
            intent.putExtra("mensaje", remoteMessage.getData().get("mensaje"));
            String uid = currentUser.getUid();
            intent.putExtra("userID", uid);
            String idS = ""+idRes;
            intent.putExtra("idS", idS);
            intent.putExtra("id", idRes);
            intent.putExtra("fecha", formattedDate);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Alerta LifeGuard")
                    .setSmallIcon(R.drawable.ic_warning_black_24dp)
                    .setContentTitle("Nueva alerta de "+remoteMessage.getData().get("nombre"))
                    .setContentText("Has recibido una alerta de "+remoteMessage.getData().get("nombre")+". Haz click aquí para ver la notificación y ayudarle.")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Has recibido una alerta de "+remoteMessage.getData().get("nombre")+". Haz click aquí para ver la notificación y ayudarle."))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVibrate(new long[] { 3000, 3000, 3000, 3000, 3000,})
                    .setLights(Color.RED, 2000, 2000)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(1, builder.build());

        }
    }
}

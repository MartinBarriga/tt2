package com.example.martin.AndroidApp.ui.dashboard;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.martin.AndroidApp.ManejadorBaseDeDatosLocal;
import com.example.martin.AndroidApp.NotificationInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NotificationsManager {
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private ManejadorBaseDeDatosLocal mManejadorBaseDeDatosLocal;
    private ArrayList<NotificationInfo> mNotifications;
    private Context mContext;

    public NotificationsManager(Context context) {
        mContext = context;
        mNotifications = new ArrayList<NotificationInfo>();
        mManejadorBaseDeDatosLocal = new ManejadorBaseDeDatosLocal(context, null);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        fillNotificationArrayList();
        db = FirebaseFirestore.getInstance();
    }

    private void fillNotificationArrayList() {
        mNotifications.clear();
        mNotifications = mManejadorBaseDeDatosLocal.obtenerNotificaciones(user.getUid());
    }

    public void deleteNotification(int position) {
        mManejadorBaseDeDatosLocal.eliminarNotificacion(user.getUid(),
                Long.toString(mNotifications.get(position).getId()));

        db.collection("notificacion")
                .whereEqualTo("userID", mNotifications.get(position).getUserID())
                .whereEqualTo("id", mNotifications.get(position).getId()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("LOG", document.getId() + " => " + document.getData());

                                db.collection("notificacion").document(document.getId()).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("LOG",
                                                        "DocumentSnapshot successfully deleted!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("LOG", "Error deleting document", e);
                                            }
                                        });

                            }
                        } else {
                            Log.d("LOG", "Error getting documents: ", task.getException());
                        }
                    }
                });
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
        db.collection("notificacion").add(notification)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("LOG",
                                "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("LOG", "Error adding document", e);
                    }
                });

        //Toast.makeText(mContext, "Contacto agregado ID: " + idRes, Toast.LENGTH_LONG).show();
        mNotifications.clear();
        fillNotificationArrayList();
    }

    private void updateLeido(final int position) {
        mManejadorBaseDeDatosLocal.actualizarNotificacion(user.getUid(),
                Long.toString(mNotifications.get(position).getId()),
                getNotificationValue(mNotifications.get(position)));

        db.collection("notificacion")
                .whereEqualTo("userID", mNotifications.get(position).getUserID())
                .whereEqualTo("id", mNotifications.get(position).getId()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("LOG", document.getId() + " => " + document.getData());

                                DocumentReference notification =
                                        db.collection("notificacion").document(document.getId());
                                notification
                                        .update("leido", mNotifications.get(position).getLeido())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("LOG",
                                                        "DocumentSnapshot successfully updated!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("LOG", "Error updating document", e);
                                            }
                                        });

                            }
                        } else {
                            Log.d("LOG", "Error getting documents: ", task.getException());
                        }
                    }
                });
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

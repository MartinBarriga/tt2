package com.example.martin.AndroidApp.ui.dashboard;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.martin.AndroidApp.ConexionSQLiteHelper;
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

import androidx.annotation.NonNull;

public class NotificationsManager {
    private ConexionSQLiteHelper mConnectionSQLiteHelper;

    FirebaseFirestore db;

    FirebaseAuth mAuth;
    FirebaseUser user;

    private ArrayList<NotificationInfo> mNotifications;
    private Context mContext;
    public NotificationsManager(Context context){
        mContext = context;
        mNotifications = new ArrayList<NotificationInfo>();
        mConnectionSQLiteHelper = new ConexionSQLiteHelper(context, "lifeguard", null, 2);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        fillNotificationArrayList();
        db = FirebaseFirestore.getInstance();
    }

    private void fillNotificationArrayList(){
        mNotifications.clear();
        //get array from database
        SQLiteDatabase readingDatabase = mConnectionSQLiteHelper.getReadableDatabase();
        Cursor cursor = readingDatabase.rawQuery("SELECT * FROM notificacion WHERE userID LIKE '"+user.getUid()+"' ORDER BY id DESC", null);
        while(cursor.moveToNext()){
            Long id = cursor.getLong(0);
            String fecha = cursor.getString(1);
            String nombre = cursor.getString(2);
            String mensaje = cursor.getString(3);
            Boolean leido = false;
            if(cursor.getInt(4) == 1) leido = true;
            String userID = cursor.getString(5);
            NotificationInfo notification = new NotificationInfo(id, fecha, nombre, mensaje, leido, userID);
            mNotifications.add(notification);
        }
        readingDatabase.close();
    }

    public void deleteNotification(int position){
        //delete from database
        SQLiteDatabase writingDatabase = mConnectionSQLiteHelper.getWritableDatabase();
        writingDatabase.delete("notificacion","id = ? AND userID = ? " , new String[]{Long.toString(mNotifications.get(position).getId()), mNotifications.get(position).getUserID()});
        writingDatabase.close();
        //Toast.makeText(mContext, id.toString() + "  " + contactPhoneNumber, Toast.LENGTH_LONG).show();

        db.collection("notificacion").whereEqualTo( "userID",mNotifications.get(position).getUserID()).whereEqualTo("id", mNotifications.get(position).getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("LOG", document.getId() + " => " + document.getData());

                        db.collection("notificacion").document(document.getId()).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("LOG", "DocumentSnapshot successfully deleted!");
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




    public ContentValues getContactValue(NotificationInfo notification){
        ContentValues contactValues = new ContentValues();
        contactValues.put("fecha", notification.getFecha());
        contactValues.put("nombre", notification.getNombre());
        contactValues.put("mensaje", notification.getMensaje());

        if(notification.getLeido()){
            contactValues.put("leido", 1);
        }
        else{
            contactValues.put("leido", 0);
        }
        contactValues.put("userID", notification.getUserID());
        return contactValues;
    }
    public void addNewNotification(NotificationInfo notification){

        //add to database
        SQLiteDatabase writingDatabase = mConnectionSQLiteHelper.getWritableDatabase();
        ContentValues contactValues = getContactValue(notification);

        Long idRes = writingDatabase.insert("notificacion", "id", contactValues);
        notification.setId(idRes);
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

        //Toast.makeText(mContext, "Contacto agregado ID: " + idRes, Toast.LENGTH_LONG).show();
        writingDatabase.close();
        mNotifications.clear();
        fillNotificationArrayList();

    }
    private void updateLeido(final int position){
        SQLiteDatabase writingDatabase = mConnectionSQLiteHelper.getWritableDatabase();
        ContentValues contactValues = getContactValue(mNotifications.get(position));
        writingDatabase.update("notificacion", contactValues, "id = ? AND userID = ? " , new String[]{Long.toString(mNotifications.get(position).getId()), mNotifications.get(position).getUserID()});
        writingDatabase.close();

        db.collection("notificacion").whereEqualTo( "userID", mNotifications.get(position).getUserID()).whereEqualTo("id", mNotifications.get(position).getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("LOG", document.getId() + " => " + document.getData());

                        DocumentReference notification = db.collection("notificacion").document(document.getId());
                        notification.update("leido", mNotifications.get(position).getLeido())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("LOG", "DocumentSnapshot successfully updated!");
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
    public void changeLeido(final int position){
        if(!mNotifications.get(position).getLeido()){
            mNotifications.get(position).setLeido(true);
            updateLeido(position);
        }
    }
    public void deleteAllNotifications(){
        SQLiteDatabase writingDatabase = mConnectionSQLiteHelper.getWritableDatabase();
        writingDatabase.execSQL("delete from notificacion");;
        writingDatabase.close();

    }
    public ArrayList<NotificationInfo> getArrayNotifications(){
        return mNotifications;
    }

}

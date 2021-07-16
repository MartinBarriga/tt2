package com.example.martin.AndroidApp.ui.contacts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

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

public class ContactsManager {
    private ConexionSQLiteHelper mConnectionSQLiteHelper;

    FirebaseFirestore db;

    FirebaseAuth mAuth;
    FirebaseUser user;

    private ArrayList<ContactsInfo> mContacts;
    private Context mContext;
    private String contactID;
    public ContactsManager(Context context){
        mContext = context;
        mContacts = new ArrayList<ContactsInfo>();
        mConnectionSQLiteHelper = new ConexionSQLiteHelper(context, "lifeguard", null, 2);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        fillContactArrayList();
        db = FirebaseFirestore.getInstance();
    }

    private void fillContactArrayList(){
        mContacts.clear();
        //get array from database
        SQLiteDatabase readingDatabase = mConnectionSQLiteHelper.getReadableDatabase();
        Cursor cursor = readingDatabase.rawQuery("SELECT * FROM contact WHERE userID LIKE '"+user.getUid()+"'", null);
        while(cursor.moveToNext()){
            Long id = cursor.getLong(0);
            String phoneNumber = cursor.getString(1);
            String name = cursor.getString(2);
            Boolean isMessageSelected = false;
            Boolean isNotificationSelected = false;
            Boolean isUser = false;
            if(cursor.getInt(3) == 1) isMessageSelected = true;
            if(cursor.getInt(4) == 1) isNotificationSelected = true;
            if(cursor.getInt(5) == 1) isUser = true;
            String userID = cursor.getString(6);
            ContactsInfo contact = new ContactsInfo(id, phoneNumber, name, isMessageSelected, isNotificationSelected, isUser, userID);
            mContacts.add(contact);
        }
        readingDatabase.close();
    }

    public void deleteContact(int position){
        //delete from database
        String contactPhoneNumber = mContacts.get(position).getPhoneNumber();
        Long id = mContacts.get(position).getId();
        SQLiteDatabase writingDatabase = mConnectionSQLiteHelper.getWritableDatabase();
        writingDatabase.delete("contact","id = ? AND userID = ? " , new String[]{Long.toString(mContacts.get(position).getId()), mContacts.get(position).getUserID()});
        writingDatabase.close();
        //Toast.makeText(mContext, id.toString() + "  " + contactPhoneNumber, Toast.LENGTH_LONG).show();

        db.collection("contact").whereEqualTo( "userID",mContacts.get(position).getUserID()).whereEqualTo("phoneNumber", mContacts.get(position).getPhoneNumber()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("LOG", document.getId() + " => " + document.getData());

                        db.collection("contact").document(document.getId()).delete()
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
        fillContactArrayList();

    }

    public void changeContactName(int position, final String newName){
        mContacts.get(position).setName(newName);
        ContactsInfo contact = mContacts.get(position);
        ContentValues contactValues = getContactValue(contact);
        Long id = contact.getId();

        SQLiteDatabase writingDatabase = mConnectionSQLiteHelper.getWritableDatabase();
        writingDatabase.update("contact", contactValues, "id = " + Long.toString(id), null);
        writingDatabase.close();

        db.collection("contact").whereEqualTo( "userID",mContacts.get(position).getUserID()).whereEqualTo("phoneNumber", mContacts.get(position).getPhoneNumber()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("LOG", document.getId() + " => " + document.getData());

                        DocumentReference contact = db.collection("contact").document(document.getId());
                        contact.update("name", newName)
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
    public void changeIsMessageSelected(int position){
        final Boolean bool;
        if(mContacts.get(position).getIsMessageSelected()){
            mContacts.get(position).setIsMessageSelected(false);
            bool = false;
        }
        else{
            mContacts.get(position).setIsMessageSelected(true);
            bool = true;
        }

        SQLiteDatabase writingDatabase = mConnectionSQLiteHelper.getWritableDatabase();
        ContentValues contactValues = getContactValue(mContacts.get(position));
        writingDatabase.update("contact", contactValues, "id = ? AND userID = ? " , new String[]{Long.toString(mContacts.get(position).getId()), mContacts.get(position).getUserID()});
        writingDatabase.close();

        db.collection("contact").whereEqualTo( "userID",mContacts.get(position).getUserID()).whereEqualTo("phoneNumber", mContacts.get(position).getPhoneNumber()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("LOG", document.getId() + " => " + document.getData());

                        DocumentReference contact = db.collection("contact").document(document.getId());
                        contact.update("isMessageSelected", bool)
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

    private void updateNotifications(final int position){
        SQLiteDatabase writingDatabase = mConnectionSQLiteHelper.getWritableDatabase();
        ContentValues contactValues = getContactValue(mContacts.get(position));
        writingDatabase.update("contact", contactValues, "id = ? AND userID = ? " , new String[]{Long.toString(mContacts.get(position).getId()), mContacts.get(position).getUserID()});
        writingDatabase.close();

        db.collection("contact").whereEqualTo( "userID",mContacts.get(position).getUserID()).whereEqualTo("phoneNumber", mContacts.get(position).getPhoneNumber()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("LOG", document.getId() + " => " + document.getData());

                        DocumentReference contact = db.collection("contact").document(document.getId());
                        contact.update("isNotificationSelected", mContacts.get(position).getIsNotificationSelected())
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
    public void changeIsNotificationSelected(final int position){
        if(mContacts.get(position).getIsNotificationSelected()){
            mContacts.get(position).setIsNotificationSelected(false);
            updateNotifications(position);
        }
        else{
            String telefono = mContacts.get(position).getPhoneNumber();
            String telefonoSinCaracteres = "";
            for(int i = 0; i < telefono.length(); i++){
                if(telefono.charAt(i) >= '0' && telefono.charAt(i) <= '9'){
                    telefonoSinCaracteres += "" + telefono.charAt(i);
                }
            }
            String telefonoFiltrado = "";
            for(int i = telefonoSinCaracteres.length() - 10; i < telefonoSinCaracteres.length(); i++){
                telefonoFiltrado += "" + telefonoSinCaracteres.charAt(i);
            }

            Long telefonoLong = Long.parseLong(telefonoFiltrado);
            db.collection("usuario").whereEqualTo("telefono", telefonoLong).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if(!task.getResult().isEmpty()){
                            mContacts.get(position).setIsNotificationSelected(true);
                            updateNotifications(position);
                        }
                        else{
                            Toast.makeText(mContext, "No encontramos un usuario con ese teléfono", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(mContext, "No se puede ejecutar esta petición", Toast.LENGTH_LONG).show();

                        Log.d("LOG", "Error getting documents: ", task.getException());
                    }
                }
            });
        }

    }
    public ContentValues getContactValue(ContactsInfo contact){
        ContentValues contactValues = new ContentValues();
        contactValues.put("phoneNumber", contact.getPhoneNumber());
        contactValues.put("name", contact.getName());
        if(contact.getIsMessageSelected()){
            contactValues.put("isMessageSelected", 1);
        }
        else{
            contactValues.put("isMessageSelected", 0);
        }
        if(contact.getIsNotificationSelected()){
            contactValues.put("isNotificationSelected", 1);
        }
        else{
            contactValues.put("isNotificationSelected", 0);
        }
        if(contact.getIsUser()){
            contactValues.put("isUser", 1);
        }
        else{
            contactValues.put("isUser", 0);
        }
        contactValues.put("userID", contact.getUserID());
        return contactValues;
    }
    public void addNewContact(ContactsInfo contact){

        //add to database
        SQLiteDatabase writingDatabase = mConnectionSQLiteHelper.getWritableDatabase();
        ContentValues contactValues = getContactValue(contact);

        Long idRes = writingDatabase.insert("contact", "id", contactValues);
        contact.setId(idRes);
        db.collection("contact").add(contact)
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
        mContacts.clear();
        fillContactArrayList();

    }


    public void deleteAllContacts(){
        SQLiteDatabase writingDatabase = mConnectionSQLiteHelper.getWritableDatabase();
        writingDatabase.execSQL("delete from contact");;
        writingDatabase.close();

    }
    public ArrayList<ContactsInfo> getArrayContacts(){
        return mContacts;
    }

}

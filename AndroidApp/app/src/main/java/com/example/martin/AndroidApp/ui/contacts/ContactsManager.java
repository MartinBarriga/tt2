package com.example.martin.AndroidApp.ui.contacts;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.martin.AndroidApp.ContactsInfo;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosLocal;
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

public class ContactsManager {
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private ManejadorBaseDeDatosLocal mManejadorBaseDeDatosLocal;
    private ArrayList<ContactsInfo> mContacts;
    private Context mContext;
    private String contactID;

    public ContactsManager(Context context) {
        mContext = context;
        mContacts = new ArrayList<ContactsInfo>();
        mManejadorBaseDeDatosLocal = new ManejadorBaseDeDatosLocal(context, null);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        fillContactArrayList();
        db = FirebaseFirestore.getInstance();
    }

    private void fillContactArrayList() {
        mContacts.clear();
        for (ContactsInfo contacto : mManejadorBaseDeDatosLocal.obtenerContactos(user.getUid())) {
            mContacts.add(contacto);
        }
    }

    public void deleteContact(int position) {
        //delete from database
        ContentValues contacto = getContactValue(mContacts.get(position));
        mManejadorBaseDeDatosLocal
                .eliminarContacto(Long.toString(mContacts.get(position).getId()), contacto);

        db.collection("contact").whereEqualTo("userID", mContacts.get(position).getUserID())
                .whereEqualTo("phoneNumber", mContacts.get(position).getPhoneNumber()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("LOG", document.getId() + " => " + document.getData());

                                db.collection("contact").document(document.getId()).delete()
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
        fillContactArrayList();
    }

    public void changeContactName(int position, final String newName) {
        mContacts.get(position).setName(newName);
        ContentValues contacto = getContactValue(mContacts.get(position));
        mManejadorBaseDeDatosLocal
                .actualizarContacto(Long.toString(mContacts.get(position).getId()), contacto);

        db.collection("contact").whereEqualTo("userID", mContacts.get(position).getUserID())
                .whereEqualTo("phoneNumber", mContacts.get(position).getPhoneNumber()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("LOG", document.getId() + " => " + document.getData());

                                DocumentReference contact =
                                        db.collection("contact").document(document.getId());
                                contact.update("name", newName)
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

    public void changeIsMessageSelected(int position) {
        final Boolean bool;
        if (mContacts.get(position).getIsMessageSelected()) {
            mContacts.get(position).setIsMessageSelected(false);
            bool = false;
        } else {
            mContacts.get(position).setIsMessageSelected(true);
            bool = true;
        }
        ContentValues contacto = getContactValue(mContacts.get(position));
        mManejadorBaseDeDatosLocal
                .actualizarContacto(Long.toString(mContacts.get(position).getId()), contacto);

        db.collection("contact").whereEqualTo("userID", mContacts.get(position).getUserID())
                .whereEqualTo("phoneNumber", mContacts.get(position).getPhoneNumber()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("LOG", document.getId() + " => " + document.getData());

                                DocumentReference contact =
                                        db.collection("contact").document(document.getId());
                                contact.update("isMessageSelected", bool)
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

    private void updateNotifications(final int position) {

        ContentValues contacto = getContactValue(mContacts.get(position));
        mManejadorBaseDeDatosLocal
                .actualizarContacto(Long.toString(mContacts.get(position).getId()), contacto);
        db.collection("contact").whereEqualTo("userID", mContacts.get(position).getUserID())
                .whereEqualTo("phoneNumber", mContacts.get(position).getPhoneNumber()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("LOG", document.getId() + " => " + document.getData());

                                DocumentReference contact =
                                        db.collection("contact").document(document.getId());
                                contact.update("isNotificationSelected",
                                        mContacts.get(position).getIsNotificationSelected())
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

    public void changeIsNotificationSelected(final int position) {
        if (mContacts.get(position).getIsNotificationSelected()) {
            mContacts.get(position).setIsNotificationSelected(false);
            updateNotifications(position);
        } else {
            String telefono = mContacts.get(position).getPhoneNumber();
            String telefonoSinCaracteres = "";
            for (int i = 0; i < telefono.length(); i++) {
                if (telefono.charAt(i) >= '0' && telefono.charAt(i) <= '9') {
                    telefonoSinCaracteres += "" + telefono.charAt(i);
                }
            }
            String telefonoFiltrado = "";
            for (int i = telefonoSinCaracteres.length() - 10; i < telefonoSinCaracteres.length();
                 i++) {
                telefonoFiltrado += "" + telefonoSinCaracteres.charAt(i);
            }

            Long telefonoLong = Long.parseLong(telefonoFiltrado);
            db.collection("usuario").whereEqualTo("telefono", telefonoLong).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (!task.getResult().isEmpty()) {
                                    mContacts.get(position).setIsNotificationSelected(true);
                                    updateNotifications(position);
                                } else {
                                    Toast.makeText(mContext,
                                            "No encontramos un usuario con ese teléfono",
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(mContext, "No se puede ejecutar esta petición",
                                        Toast.LENGTH_LONG).show();

                                Log.d("LOG", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    private ContentValues getContactValue(ContactsInfo contact) {
        ContentValues contactValues = new ContentValues();
        contactValues.put("phoneNumber", contact.getPhoneNumber());
        contactValues.put("name", contact.getName());
        if (contact.getIsMessageSelected()) {
            contactValues.put("isMessageSelected", 1);
        } else {
            contactValues.put("isMessageSelected", 0);
        }
        if (contact.getIsNotificationSelected()) {
            contactValues.put("isNotificationSelected", 1);
        } else {
            contactValues.put("isNotificationSelected", 0);
        }
        if (contact.getIsUser()) {
            contactValues.put("isUser", 1);
        } else {
            contactValues.put("isUser", 0);
        }
        contactValues.put("userID", contact.getUserID());
        return contactValues;
    }

    public void addNewContact(ContactsInfo contacto) {
        ContentValues contactoConFormato = getContactValue(contacto);
        Long idNuevoContacto = mManejadorBaseDeDatosLocal.agregarNuevoContacto(contactoConFormato);
        contacto.setId(idNuevoContacto);
        db.collection("contact").add(contacto)
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
        mContacts.clear();
        fillContactArrayList();
    }

    public ArrayList<ContactsInfo> getArrayContacts() {
        return mContacts;
    }

}

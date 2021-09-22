package com.example.martin.AndroidApp;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

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

public class ManejadorBaseDeDatosNube {
    private FirebaseFirestore BaseDeDatos;
    private FirebaseAuth Auth;
    private FirebaseUser Usuario;

    public ManejadorBaseDeDatosNube() {
        Auth = FirebaseAuth.getInstance();
        Usuario = Auth.getCurrentUser();
        BaseDeDatos = FirebaseFirestore.getInstance();
    }

    public FirebaseUser obtenerUsuario() {
        return Usuario;
    }

    public FirebaseAuth obtenerAuth() {
        return Auth;
    }

    public String obtenerIdUsuario() {
        return Usuario.getUid();
    }


    public void eliminarContacto(ContentValues contacto) {
        BaseDeDatos.collection("contact").whereEqualTo("userID", contacto.get("userID"))
                .whereEqualTo("phoneNumber", contacto.get("phoneNumber")).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("LOG", document.getId() + " => " + document.getData());

                                BaseDeDatos.collection("contact").document(document.getId())
                                        .delete()
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
    }

    public void actualizarContacto(ContentValues contacto, String campoACambiar) {
        BaseDeDatos.collection("contact").whereEqualTo("userID", contacto.get("userID"))
                .whereEqualTo("phoneNumber", contacto.get("phoneNumber")).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("LOG", document.getId() + " => " + document.getData());

                                DocumentReference contact =
                                        BaseDeDatos.collection("contact")
                                                .document(document.getId());
                                contact.update(campoACambiar, contacto.get(campoACambiar))
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

    Boolean[] existeNumeroDeContacto = {false};
    public Boolean existeNumeroDeContactoRegistradoEnFirebaseComoUsuario(Long telefono, Context context) {
        BaseDeDatos.collection("usuario").whereEqualTo("telefono", telefono).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) existeNumeroDeContacto[0] = true;
                        } else {
                            Log.d("LOG", "Error getting documents: ", task.getException());
                            Toast.makeText(context,
                                    "No encontramos un usuario con ese teléfono",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
        return existeNumeroDeContacto[0];
    }

    public void agregarContacto(ContactsInfo contacto) {
        BaseDeDatos.collection("contact").add(contacto)
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
    }

    public void eliminarNotificacion(NotificationInfo notificacion) {
        BaseDeDatos.collection("notificacion")
                .whereEqualTo("userID", notificacion.getUserID())
                .whereEqualTo("id", notificacion.getId()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("LOG", document.getId() + " => " + document.getData());

                                BaseDeDatos.collection("notificacion").document(document.getId())
                                        .delete()
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
    }

    public void agregarNotificacion(NotificationInfo notificacion) {
        BaseDeDatos.collection("notificacion").add(notificacion)
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
    }

    public void actualizarNotificacion(NotificationInfo notificacion) {
        BaseDeDatos.collection("notificacion")
                .whereEqualTo("userID", notificacion.getUserID())
                .whereEqualTo("id", notificacion.getId()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("LOG", document.getId() + " => " + document.getData());

                                DocumentReference notification =
                                        BaseDeDatos.collection("notificacion")
                                                .document(document.getId());
                                notification
                                        .update("leido", notificacion.getLeido())
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

    public void actualizarUsuario(ContentValues usuario) {
        BaseDeDatos.collection("usuario").whereEqualTo("uID", obtenerIdUsuario()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("LOG", document.getId() + " => " + document.getData());

                                DocumentReference user =
                                        BaseDeDatos.collection("usuario")
                                                .document(document.getId());
                                user.update("nombre", usuario.get("nombre"))
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
                                user.update("telefono", usuario.get("telefono"))
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
                                user.update("edad", usuario.get("edad"))
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
                                user.update("nss", usuario.get("nss"))
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
                                user.update("medicacion", usuario.get("medicacion"))
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
                                user.update("enfermedades", usuario.get("enfermedades"))
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
                                user.update("taxicomanias", usuario.get("toxicomanias"))
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
                                user.update("tipoSangre", usuario.get("tiposangre"))
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
                                user.update("alergias", usuario.get("alergias"))
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
                                user.update("religion", usuario.get("religion"))
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

    public void agregarUsuario(UserInfo usuario) {
        BaseDeDatos.collection("usuario").add(usuario)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("LOG", "DocumentSnapshot added with ID: " +
                                documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("LOG", "Error adding document", e);
                    }
                });
    }

}

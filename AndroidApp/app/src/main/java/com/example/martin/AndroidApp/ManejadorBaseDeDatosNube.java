package com.example.martin.AndroidApp;

import android.Manifest;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class ManejadorBaseDeDatosNube {
    Boolean[] existeNumeroDeContacto = {false};
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

    private void eliminarContacto(Long idContacto) {
        BaseDeDatos.collection("contacto").whereEqualTo("idUsuario", obtenerIdUsuario())
                .whereEqualTo("idContacto", idContacto).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("LOG", document.getId() + " => " + document.getData());

                                BaseDeDatos.collection("contacto").document(document.getId())
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

    private void actualizarContacto(Contacto contactoCrudo,
                                    ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal) {
        ContentValues contacto = generarFormatoDeContactoParaActualizarBD(contactoCrudo);
        BaseDeDatos.collection("contacto").whereEqualTo("idUsuario", contacto.get("idUsuario"))
                .whereEqualTo("idContacto", contacto.get("idContacto")).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("LOG", document.getId() + " => " + document.getData());

                                DocumentReference contactoDoc =
                                        BaseDeDatos.collection("contacto")
                                                .document(document.getId());
                                contactoDoc.update("esUsuario", contacto.get("esUsuario"))
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
                                contactoDoc.update("enNube", contacto.get("enNube"))
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
                                contactoDoc.update("nombre", contacto.get("nombre"))
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
                                contactoDoc.update("recibeNotificaciones",
                                        contacto.get("recibeNotificaciones"))
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
                                contactoDoc.update("recibeSMS", contacto.get("recibeSMS"))
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
                                contactoDoc.update("telefono", contacto.get("telefono"))
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
                            manejadorBaseDeDatosLocal
                                    .actualizarContacto(
                                            String.valueOf(contactoCrudo.getIdContacto()),
                                            manejadorBaseDeDatosLocal
                                                    .generarFormatoDeContactoParaIntroducirEnBD(
                                                            contactoCrudo));
                        } else {
                            Log.d("LOG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public Boolean existeNumeroDeContactoRegistradoEnFirebaseComoUsuario(Long telefono,
                                                                         Context context) {
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

    private void eliminarNotificacion(Long idNotificacion) {
        BaseDeDatos.collection("notificacion")
                .whereEqualTo("idUsuario", obtenerIdUsuario())
                .whereEqualTo("idNotificacion", idNotificacion).get()
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

    private void agregarContacto(Contacto contacto,
                                 ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal) {
        BaseDeDatos.collection("contacto").add(contacto)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("LOG",
                                "DocumentSnapshot added with ID: " + documentReference.getId());
                        manejadorBaseDeDatosLocal
                                .actualizarContacto(String.valueOf(contacto.getIdContacto()),
                                        manejadorBaseDeDatosLocal
                                                .generarFormatoDeContactoParaIntroducirEnBD(
                                                        contacto));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("LOG", "Error adding document", e);
                    }
                });
    }

    private void agregarNotificacion(Notificacion notificacion,
                                     ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal) {
        BaseDeDatos.collection("notificacion").add(notificacion)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("LOG",
                                "DocumentSnapshot added with ID: " + documentReference.getId());
                        manejadorBaseDeDatosLocal
                                .actualizarNotificacion(notificacion.getIdUsuario(),
                                        manejadorBaseDeDatosLocal
                                                .generarFormatoDeNotificacionParaIntroducirEnBD(
                                                        notificacion));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("LOG", "Error adding document", e);
                    }
                });
    }

    private void actualizarNotificacion(Notificacion notificacion,
                                        ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal) {
        BaseDeDatos.collection("notificacion")
                .whereEqualTo("idUsuario", notificacion.getIdUsuario())
                .whereEqualTo("idNotificacion", notificacion.getIdNotificacion()).get()
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
                                        .update("leido", (Boolean) notificacion.getLeido())
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
                            manejadorBaseDeDatosLocal
                                    .actualizarNotificacion(notificacion.getIdUsuario(),
                                            manejadorBaseDeDatosLocal
                                                    .generarFormatoDeNotificacionParaIntroducirEnBD(
                                                            notificacion));
                        } else {
                            Log.d("LOG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void actualizarUsuario(Usuario usuarioCrudo,
                                   ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal) {
        ContentValues usuario =
                generarFormatoDeUsuarioParaActualizarBD(usuarioCrudo, manejadorBaseDeDatosLocal);
        BaseDeDatos.collection("usuario").whereEqualTo("idUsuario", obtenerIdUsuario()).get()
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
                                user.update("toxicomanias", usuario.get("toxicomanias"))
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
                                user.update("fechaUltimoRespaldo",
                                        usuario.get("fechaUltimoRespaldo"))
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
                                user.update("frecuenciaRespaldo", usuario.get("frecuenciaRespaldo"))
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
                                user.update("frecuenciaCardiacaMinima",
                                        usuario.get("frecuenciaCardiacaMinima"))
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
                                user.update("frecuenciaCardiacaMaxima",
                                        usuario.get("frecuenciaCardiacaMaxima"))
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
                                user.update("enviaAlertasAUsuariosCercanos",
                                        usuario.get("enviaAlertasAUsuariosCercanos"))
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
                                user.update("recibeAlertasDeUsuariosCercanos",
                                        usuario.get("recibeAlertasDeUsuariosCercanos"))
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
                                manejadorBaseDeDatosLocal.actualizarUsuario(
                                        manejadorBaseDeDatosLocal
                                                .generarFormatoDeUsuarioParaIntroducirEnBD(
                                                        usuarioCrudo));
                            }
                        } else {
                            Log.d("LOG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void agregarUsuario(Usuario usuario) {
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

    public String obtenerNombreDeUsuarioConIdDeEmergencia(String idEmergencia) {
        try {
            String idUsuario = idEmergencia.substring(0, idEmergencia.length() - 20);
            return (String) Tasks.await(BaseDeDatos.collection("usuario").whereEqualTo("idUsuario",
                    idUsuario).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot =
                                        task.getResult().getDocuments().get(0);
                                if (documentSnapshot.exists()) {
                                    Log.d("LOG", "Usuario encontrado: " +
                                            (String) documentSnapshot.get("nombre"));
                                } else {
                                    Log.d("LOG", "No se encontró el usuario");
                                }
                            }
                            Log.d("LOG", "Task complete");
                        }
                    })).getDocuments().get(0).get("nombre");
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void descargarUsuario(ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal) {
        try {
            String UID = obtenerIdUsuario();
            Tasks.await(BaseDeDatos.collection("usuario").whereEqualTo("idUsuario", UID).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            Log.d("LOG", "UID: " + UID);
                            DocumentSnapshot document =
                                    queryDocumentSnapshots.getDocuments().get(0);
                            Log.d("LOG", document.getId() + " => " + document.getData());
                            com.example.martin.AndroidApp.Usuario usuario =
                                    new Usuario((String) document.get("idUsuario"),
                                            (String) document.get("nombre"),
                                            (Long) document.get("telefono"),
                                            ((Long) document.get("edad")).intValue(),
                                            (Long) document.get("nss"),
                                            (String) document.get("medicacion"),
                                            (String) document.get("toxicomanias"),
                                            (String) document.get("tipoSangre"),
                                            (String) document.get("alergias"),
                                            (String) document.get("religion"), true,
                                            (String) document.get("fechaUltimoRespaldo"),
                                            (String) document.get("frecuenciaRespaldo"),
                                            ((Long) document.get("frecuenciaCardiacaMinima"))
                                                    .intValue(),
                                            ((Long) document.get("frecuenciaCardiacaMaxima"))
                                                    .intValue(),
                                            (boolean) document.get("enviaAlertasAUsuariosCercanos"),
                                            (boolean) document
                                                    .get("recibeAlertasDeUsuariosCercanos"));
                            manejadorBaseDeDatosLocal.agregarUsuario(manejadorBaseDeDatosLocal
                                    .generarFormatoDeUsuarioParaIntroducirEnBD(usuario));

                            //Agregar enfermedades del respaldo
                            ArrayList<String> enferdadesEnBD =
                                    manejadorBaseDeDatosLocal.obtenerEnfermedades();
                            String enfermedadesDelUsuario = (String) document.get("enfermedades");
                            String[] enfermedadesDelUsuarioArray =
                                    enfermedadesDelUsuario.split(", ");
                            for (String enfermedad :
                                    enfermedadesDelUsuarioArray) {
                                if (enferdadesEnBD.contains(enfermedad))
                                    manejadorBaseDeDatosLocal
                                            .agregarEnfermadadAUsuario(usuario.getIdUsuario(),
                                                    (long) (enferdadesEnBD.indexOf(enfermedad) +
                                                            1));
                                else
                                    manejadorBaseDeDatosLocal
                                            .agregarEnfermadadAUsuario(usuario.getIdUsuario(),
                                                    manejadorBaseDeDatosLocal
                                                            .agregarEnfermedad(enfermedad));
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("LOG", "Error adding document", e);
                        }
                    }));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void descargarContactos(ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal) {
        try {
            Tasks.await(BaseDeDatos.collection("contacto")
                    .whereEqualTo("idUsuario", obtenerIdUsuario()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("LOG", document.getId() + " => " + document.getData());
                                    Contacto contacto =
                                            new Contacto((Long) document.get("idContacto"),
                                                    (Long) document.get("telefono"),
                                                    (String) document.get("nombre"),
                                                    (Boolean) document.get("recibeSMS"),
                                                    (Boolean) document.get("recibeNotificaciones"),
                                                    (Boolean) document.get("esUsuario"),
                                                    (String) document.get("idUsuario"),
                                                    (Boolean) document.get("enNube"));
                                    manejadorBaseDeDatosLocal
                                            .agregarNuevoContacto(manejadorBaseDeDatosLocal
                                                    .generarFormatoDeContactoParaIntroducirEnBD(
                                                            contacto));

                                }
                            } else {
                                Log.d("LOG", "Error getting documents: ", task.getException());
                            }
                        }
                    }));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void descargarNotificaciones(ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal) {
        try {
            Tasks.await(BaseDeDatos.collection("notificacion")
                    .whereEqualTo("idUsuario", obtenerIdUsuario()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("LOG", document.getId() + " => " + document.getData());
                                    Notificacion notificacion =
                                            new Notificacion((Long) document.get("idNotificacion"),
                                                    (String) document.get("idUsuario"),
                                                    (String) document.get("idEmergencia"),
                                                    (String) document.get("titulo"),
                                                    ((Long) document.get("estado")).intValue(),
                                                    (String) document.get("fecha"),
                                                    (Boolean) document.get("leido"),
                                                    (Boolean) document.get("esPropia"), true);
                                    manejadorBaseDeDatosLocal
                                            .agregarNotificacion(manejadorBaseDeDatosLocal
                                                    .generarFormatoDeNotificacionParaIntroducirEnBD(
                                                            notificacion));

                                }
                            } else {
                                Log.d("LOG", "Error getting documents: ", task.getException());
                            }
                        }
                    }));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void descargarMedicionesYDatos(ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal) {
            BaseDeDatos.collection("medicion")
                    .whereEqualTo("idUsuario", obtenerIdUsuario()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("LOG", document.getId() + " => " + document.getData());
                                    Medicion medicion =
                                            new Medicion((Long) document.get("idMedicion"),
                                                    (String) document.get("idUsuario"),
                                                    (String) document.get("fecha"),
                                                    true);
                                    manejadorBaseDeDatosLocal
                                            .agregarMedicion(manejadorBaseDeDatosLocal
                                                    .generarFormatoDeMedicionParaIntroducirEnBD(
                                                            medicion));
                                    BaseDeDatos.collection("medicion").document(document.getId())
                                            .collection("dato").get().addOnCompleteListener(
                                            new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(
                                                        @NonNull Task<QuerySnapshot> taskDato) {
                                                    if (taskDato.isSuccessful()) {
                                                        for (QueryDocumentSnapshot documentDato :
                                                                taskDato
                                                                .getResult()) {
                                                            Log.d("LOG",
                                                                    documentDato.getId() + " => " +
                                                                            documentDato.getData());
                                                            Dato dato = new Dato((Long) documentDato
                                                                    .get("idDato"),
                                                                    (Long) documentDato
                                                                            .get("idMedicion"),
                                                                    ((Long) documentDato
                                                                            .get("frecuenciaCardiaca"))
                                                                            .intValue(),
                                                                    ((Long) documentDato.get("ecg"))
                                                                            .intValue(),
                                                                    ((Long) documentDato
                                                                            .get("spo2"))
                                                                            .intValue(),
                                                                    (String) documentDato
                                                                            .get("hora"), true);
                                                            manejadorBaseDeDatosLocal
                                                                    .agregarDato(manejadorBaseDeDatosLocal
                                                                            .generarFormatoDeDatoParaIntroducirEnBD(
                                                                                    dato));
                                                        }
                                                    }
                                                }
                                            });
                                }
                            } else {
                                Log.d("LOG", "Error getting documents: ", task.getException());
                            }
                        }
                    });
    }

    public void descargarResumen(ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal,
                                 String idEmergencia,
                                 Long idNotificacion, String nombre) {
        try {
            DocumentSnapshot document =
                    Tasks.await(BaseDeDatos.collection("emergencias").document(idEmergencia).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot document) {
                                }
                            }));
            Log.d("LOG", "Descargando resumen: " + idEmergencia);
            Log.d("LOG", document.getId() + " => " + document.getData());
            String detalles;
            if (!((String) document.get("hospitalTrasladado")).matches("")) {
                detalles = (String) document.get("hospitalTrasladado");
            } else {
                detalles = (String) document.get("nombreFamiliar");
            }
            com.example.martin.AndroidApp.Resumen resumen =
                    new Resumen(null, idNotificacion,
                            nombre,
                            (String) document.get("comentariosAdicionales"),
                            (String) document.get("Desenlace"),
                            detalles,
//                          (String) document.get("duracion"), DE MOMENTO ESTE NO ESTÁ EN FIREBASE
                            "8 minutos 12 segundos (prueba)",
                            ((Long) document.get("cantidadDePersonasEnviado")).intValue(),
//                          (int) document.get("seguidores"), DE MOMENTO ESTE NO ESTÁ EN FIREBASE
                            0,
                            false);

            manejadorBaseDeDatosLocal.agregarResumen(manejadorBaseDeDatosLocal
                    .generarFormatoDeResumenParaIntroducirEnBD(resumen));

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void descargarRespaldo(
            ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal) {
        HiloParaDescargarRespaldo hiloParaDescargarRespaldo =
                new HiloParaDescargarRespaldo(manejadorBaseDeDatosLocal);
        hiloParaDescargarRespaldo.start();
        try {
            hiloParaDescargarRespaldo.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ContentValues generarFormatoDeContactoParaActualizarBD(Contacto contacto) {
        ContentValues contentValuesContacto = new ContentValues();
        contentValuesContacto.put("telefono", contacto.getTelefono());
        contentValuesContacto.put("nombre", contacto.getNombre());
        contentValuesContacto.put("recibeSMS", contacto.getRecibeSMS());
        contentValuesContacto.put("recibeNotificaciones", contacto.getRecibeNotificaciones());
        contentValuesContacto.put("esUsuario", contacto.getEsUsuario());
        contentValuesContacto.put("idUsuario", contacto.getIdUsuario());
        contentValuesContacto.put("idContacto", contacto.getIdContacto());
        contentValuesContacto.put("enNube", contacto.getEnNube());
        return contentValuesContacto;
    }

    private ContentValues generarFormatoDeUsuarioParaActualizarBD(
            com.example.martin.AndroidApp.Usuario usuario,
            ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal) {
        ContentValues contentUsuario = new ContentValues();
        contentUsuario.put("idUsuario", usuario.getIdUsuario());
        contentUsuario.put("nombre", usuario.getNombre());
        contentUsuario.put("telefono", usuario.getTelefono());
        contentUsuario.put("edad", usuario.getEdad());
        contentUsuario.put("nss", usuario.getNss());
        contentUsuario.put("medicacion", usuario.getMedicacion());
        String enfermedades = "";
        for (String enfermedad :
                manejadorBaseDeDatosLocal.obtenerEnfermedadesDeUnUsuario(obtenerIdUsuario())) {
            if (enfermedades.matches(""))
                enfermedades += enfermedad;
            else
                enfermedades += ", " + enfermedad;
        }
        contentUsuario.put("enfermedades", enfermedades);
        contentUsuario.put("toxicomanias", usuario.getToxicomanias());
        contentUsuario.put("tiposangre", usuario.getTipoSangre());
        contentUsuario.put("alergias", usuario.getAlergias());
        contentUsuario.put("religion", usuario.getReligion());
        contentUsuario.put("enNube", usuario.getEnNube());
        contentUsuario.put("fechaUltimoRespaldo", usuario.getFechaUltimoRespaldo());
        contentUsuario.put("frecuenciaRespaldo", usuario.getFrecuenciaRespaldo());
        contentUsuario.put("frecuenciaCardiacaMinima", usuario.getFrecuenciaCardiacaMinima());
        contentUsuario.put("frecuenciaCardiacaMaxima", usuario.getFrecuenciaCardiacaMaxima());
        contentUsuario
                .put("enviaAlertasAUsuariosCercanos", usuario.getEnviaAlertasAUsuariosCercanos());
        contentUsuario.put("recibeAlertasDeUsuariosCercanos",
                usuario.getRecibeAlertasDeUsuariosCercanos());
        return contentUsuario;
    }

    private Set<Long> obtenerIdsContactos() {
        Set<Long> idsContactos = new HashSet<Long>();
        try {
            Tasks.await(BaseDeDatos.collection("contacto")
                    .whereEqualTo("idUsuario", obtenerIdUsuario()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    idsContactos.add((Long) document.get("idContacto"));
                                }
                            } else {
                                Log.d("LOG", "Error getting documents: ", task.getException());
                            }
                        }
                    }));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return idsContactos;
    }

    private Set<Long> obtenerIdsNotificaciones() {
        Set<Long> idsNotificaciones = new HashSet<Long>();
        try {
            Tasks.await(BaseDeDatos.collection("notificacion")
                    .whereEqualTo("idUsuario", obtenerIdUsuario()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    idsNotificaciones.add((Long) document.get("idNotificacion"));
                                }
                            } else {
                                Log.d("LOG", "Error getting documents: ", task.getException());
                            }
                        }
                    }));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return idsNotificaciones;
    }

    public void realizarRespaldo(ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal) {
        HiloParaHacerRespaldo hiloParaHacerRespaldo =
                new HiloParaHacerRespaldo(manejadorBaseDeDatosLocal);
        hiloParaHacerRespaldo.start();
    }

    public boolean crearEmergencia(String idEmergencia, String fecha, String localizacion,
                                   int cantidadDePersonasEnviado) {
        Map<String, Object> datosIniciales = new HashMap<>();
        datosIniciales.put("inicio", fecha);
        datosIniciales.put("terminada", false);
        datosIniciales.put("cantidadDePersonasEnviado", cantidadDePersonasEnviado);

        try {
            Tasks.await(
                    BaseDeDatos.collection("emergencias").document(idEmergencia).set(datosIniciales)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("LOG", "EMERGENCIA CREADA");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("LOG", "Error al crear emergencia", e);
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                        }
                    }));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String[] longitudYLatitud = localizacion.split(",");
        Map<String, Object> mLocalizacion = new HashMap<>();
        mLocalizacion.put("usuario", "Emergencia");
        mLocalizacion.put("longitud", longitudYLatitud[0]);
        mLocalizacion.put("latitud", longitudYLatitud[1]);
        try {
            Tasks.await(BaseDeDatos.collection("emergencias").document(idEmergencia)
                    .collection("localizacion").document(idEmergencia).set(mLocalizacion)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("LOG", "Localizacion agregada");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("LOG", "Error al agregar localizacion", e);
                        }
                    }));
            return Tasks.await(BaseDeDatos.collection("emergencias").document(idEmergencia)
                    .collection("localizacion").document(idEmergencia).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {
                                    Log.d("LOG", "Emergencia creada y localización agregada");
                                } else {
                                    Log.d("LOG", "No se encontró el documento");
                                }
                            }
                            Log.d("LOG", "Task complete");
                        }
                    })).exists();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean revisarSiUnaEmergenciaFueTerminada(String idEmergencia) {
        try {
            return (boolean) Tasks
                    .await(BaseDeDatos.collection("emergencias").document(idEmergencia).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        if (documentSnapshot.exists()) {
                                            Log.d("LOG", "Emergencia terminada: " +
                                                    (boolean) documentSnapshot.get("terminada"));
                                        } else {
                                            Log.d("LOG", "No se encontró el documento");
                                        }
                                    }
                                    Log.d("LOG", "Task complete");
                                }
                            })).get("terminada");
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (NullPointerException e) {
            Log.d("LOG", "La emergencia podría no haber sido creada aún.");
            return false;
        }
    }

    public void iniciarDescargaDeResumen(ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal,
                                         String idEmergencia,
                                         Long idNotificacion) {
        HiloParaDescargarResumen hiloParaDescargarResumen =
                new HiloParaDescargarResumen(manejadorBaseDeDatosLocal, idEmergencia,
                        idNotificacion);
        hiloParaDescargarResumen.start();
        try {
            hiloParaDescargarResumen.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void agregarDatoAMedicion(Medicion medicion, ArrayList<Dato> datos,
                                      ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal) {
        BaseDeDatos.collection("medicion")
                .whereEqualTo("idUsuario", medicion.getIdUsuario())
                .whereEqualTo("idMedicion", medicion.getIdMedicion()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("LOG", document.getId() + " => " + document.getData());
                                for (Dato dato : datos) {
                                    dato.setEnNube(true);
                                    BaseDeDatos.collection("medicion").document(document.getId())
                                            .collection("dato").add(dato)
                                            .addOnSuccessListener(
                                                    new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(
                                                                DocumentReference documentReference) {
                                                            Log.d("LOG",
                                                                    "DocumentSnapshot added with " +
                                                                            "ID: " +
                                                                            documentReference
                                                                                    .getId());
                                                            System.out.println(
                                                                    "Se agregó el Dato: " +
                                                                            dato.getIdDato());
                                                            manejadorBaseDeDatosLocal
                                                                    .actualizarDato(
                                                                            manejadorBaseDeDatosLocal
                                                                                    .generarFormatoDeDatoParaIntroducirEnBD(
                                                                                            dato),
                                                                            medicion
                                                                                    .getIdUsuario());
                                                        }
                                                    });
                                }
                            }
                        } else {
                            Log.d("LOG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void agregarMedicionYDatos(Medicion medicion, ArrayList<Dato> datos,
                                       ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal) {
        medicion.setEnNube(true);
        BaseDeDatos.collection("medicion").add(medicion)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("LOG",
                                "DocumentSnapshot added with ID: " + documentReference.getId());
                        System.out.println("Se agregó la Medicion: " + medicion.getIdMedicion());
                        manejadorBaseDeDatosLocal
                                .actualizarMedicion(
                                        manejadorBaseDeDatosLocal
                                                .generarFormatoDeMedicionParaIntroducirEnBD(
                                                        medicion));
                        agregarDatoAMedicion(medicion, datos, manejadorBaseDeDatosLocal);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("LOG", "Error adding document", e);
                    }
                });
    }

    private void subirIdsMedicionesYDatos(
            Map<Medicion, ArrayList<Dato>> idsMedicionesYDatosEnBDLocal,
            ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal) {
        for (Map.Entry<Medicion, ArrayList<Dato>> medicionYDato : idsMedicionesYDatosEnBDLocal
                .entrySet()) {
            BaseDeDatos.collection("medicion")
                    .whereEqualTo("idUsuario", obtenerIdUsuario())
                    .whereEqualTo("idMedicion", medicionYDato.getKey().getIdMedicion()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.isEmpty()) {
                                    // No existe la medicion, toca que agregar medicion y datos
                                    agregarMedicionYDatos(medicionYDato.getKey(),
                                            medicionYDato.getValue(),
                                            manejadorBaseDeDatosLocal);
                                } else {
                                    // Si existe la medicion y toca que agregar sólo datos
                                    agregarDatoAMedicion(medicionYDato.getKey(),
                                            medicionYDato.getValue(),
                                            manejadorBaseDeDatosLocal);
                                }

                            } else {
                                Log.d("LOG", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    class HiloParaHacerRespaldo extends Thread {
        ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal;

        HiloParaHacerRespaldo(ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal) {
            this.manejadorBaseDeDatosLocal = manejadorBaseDeDatosLocal;
        }

        public void run() {
            Usuario usuario = manejadorBaseDeDatosLocal.obtenerUsuario(obtenerIdUsuario());
            String fechaUltimoRespaldo = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                    Calendar.getInstance().getTime());
            usuario.setEnNube(true);
            usuario.setFechaUltimoRespaldo(fechaUltimoRespaldo);
            actualizarUsuario(usuario, manejadorBaseDeDatosLocal);


            //Realizar recorrido por cada registro de cada tabla en la nube y meter cada id de cada
            // registro en un set de id's
            Set<Long> idsContactosEnNube = obtenerIdsContactos();
            Set<Long> idsNotificacionesEnNube = obtenerIdsNotificaciones();
            subirIdsMedicionesYDatos(manejadorBaseDeDatosLocal
                            .obtenerMedicionesYDatosEnFormatoDeMap(obtenerIdUsuario()),
                    manejadorBaseDeDatosLocal);


            //Realizar recorrido por cada registro de cada tabla de nuestra BD Local y eliminamos el
            // id del set
            for (Contacto contacto :
                    manejadorBaseDeDatosLocal.obtenerContactos(obtenerIdUsuario())) {
                if (!contacto.getEnNube()) {
                    contacto.setEnNube(true);
                    //Si el registro existe en la Nube, realizamos un update para ese registro
                    if (idsContactosEnNube.contains(contacto.getIdContacto())) {
                        actualizarContacto(contacto, manejadorBaseDeDatosLocal);
                    } else {
                        // Si el registro no existe en la nube entonces lo agregamos
                        agregarContacto(contacto, manejadorBaseDeDatosLocal);
                    }
                }
                //Eliminamos el id del set
                idsContactosEnNube.remove(contacto.getIdContacto());
            }
            //Todoo aquel id que haya quedado dentro del set significara que la bd local no lo
            //contenía, por lo que se eliminara ese registro de la bd de la nube
            for (Long idContactoEnNubeRestante : idsContactosEnNube) {
                eliminarContacto(idContactoEnNubeRestante);
            }

            //Realizar recorrido por cada registro de cada tabla de nuestra BD Local y eliminamos el
            // id del set
            for (Notificacion notificacion :
                    manejadorBaseDeDatosLocal.obtenerNotificaciones(obtenerIdUsuario())) {
                if (!notificacion.getEnNube()) {
                    notificacion.setEnNube(true);
                    //Si el registro existe en la Nube, realizamos un update para ese registro
                    if (idsNotificacionesEnNube.contains(notificacion.getIdNotificacion())) {
                        actualizarNotificacion(notificacion, manejadorBaseDeDatosLocal);
                    } else {
                        // Si el registro no existe en la nube entonces lo agregamos
                        agregarNotificacion(notificacion, manejadorBaseDeDatosLocal);
                    }
                }
                //Eliminamos el id del set
                idsNotificacionesEnNube.remove(notificacion.getIdNotificacion());
            }
            //Todoo aquel id que haya quedado dentro del set significara que la bd local no lo
            //contenía, por lo que se eliminara ese registro de la bd de la nube
            for (Long idNotificacionEnNubeRestante : idsNotificacionesEnNube) {
                eliminarNotificacion(idNotificacionEnNubeRestante);
            }

        }
    }

    class HiloParaDescargarRespaldo extends Thread {
        ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal;

        HiloParaDescargarRespaldo(ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal) {
            this.manejadorBaseDeDatosLocal = manejadorBaseDeDatosLocal;
        }

        public void run() {
            descargarUsuario(manejadorBaseDeDatosLocal);
            descargarContactos(manejadorBaseDeDatosLocal);
            descargarNotificaciones(manejadorBaseDeDatosLocal);
            descargarMedicionesYDatos(manejadorBaseDeDatosLocal);
        }
    }

    class HiloParaDescargarResumen extends Thread {
        private ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal;
        private String idEmergencia;
        private Long idNotificacion;

        HiloParaDescargarResumen(ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal,
                                 String idEmergencia,
                                 Long idNotificacion) {
            this.manejadorBaseDeDatosLocal = manejadorBaseDeDatosLocal;
            this.idEmergencia = idEmergencia;
            this.idNotificacion = idNotificacion;
        }

        public void run() {
            String nombre = obtenerNombreDeUsuarioConIdDeEmergencia(idEmergencia);
            descargarResumen(manejadorBaseDeDatosLocal, idEmergencia, idNotificacion, nombre);
            Looper.prepare();
            (new Handler()).postDelayed(new Runnable() {
                public void run() {
                    System.exit(0);
                }
            }, 200);
        }
    }

    public boolean tieneConexionAInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);

        return connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    class HiloParaActualizarDatosEnLaEmergencia extends Thread{
        private String idEmergencia;
        private Context context;
        private BroadcastReceiver actualizacionesEnConexion;
        private boolean TERMINADA = false;

        HiloParaActualizarDatosEnLaEmergencia(String idEmergencia , Context context){
            this.idEmergencia = idEmergencia;
            this.context = context;

            actualizacionesEnConexion = new BroadcastReceiver() {
                @Override
                public void onReceive(Context arg0, Intent intent) {
                    String mensajeSucio = intent.getStringExtra("MENSAJE");
                    Long tiempo = intent.getLongExtra("TIEMPO", 0);

                    //El mensaje viene en forma de un string con corchetes y comas, asi que lo vamos
                    // a limpiar dejando solo el string identico a como se mando del arduino
                    String mensaje = "";
                    for (int i = 0; i < mensajeSucio.length(); i++) {
                        if (mensajeSucio.charAt(i) >= '0' && mensajeSucio.charAt(i) <= '9') {
                            mensaje += mensajeSucio.charAt(i);
                        }
                    }
                    if (mensaje.length() == 12) {
                        int valorSpo2 =
                                (mensaje.charAt(4) - '0') * 100 + (mensaje.charAt(5) - '0') * 10 +
                                        (mensaje.charAt(6) - '0');
                        int valorCardiaco =
                                (mensaje.charAt(7) - '0') * 100 + (mensaje.charAt(8) - '0') * 10 +
                                        (mensaje.charAt(9) - '0');

                        if (tieneConexionAInternet( context )){
                            Map<String, Object> mLocalizacion = new HashMap<>();
                            mLocalizacion.put("frecuencia", valorCardiaco);
                            mLocalizacion.put("niveloxigeno", valorSpo2);

                            BaseDeDatos.collection("emergencias").document(idEmergencia)
                                    .collection("signosvitales").document(idEmergencia)
                                    .set(mLocalizacion).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("LOG", "Signos vitales actualizados");
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("LOG", "Error al actualizar signos vitales: ", e);
                                        }
                                    });
                        }
                    }
                }
            };
        }

        @Override
        public void run() {
            Looper.prepare();
            do {
                BaseDeDatos.collection("emergencias").document(idEmergencia).
                        addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                                @Nullable FirebaseFirestoreException error) {
                                if ( documentSnapshot != null ){
                                    if (documentSnapshot.exists()){
                                        TERMINADA = (boolean) documentSnapshot.get("terminada");
                                    } else {
                                        Log.d("LOG" ,
                                                "HiloParaActualizarDatosEnLaEmergencia: " +
                                                        "No se encontró la emergencia.");
                                    }
                                }
                            }
                        });


                LocalBroadcastManager.getInstance(context).registerReceiver( actualizacionesEnConexion,
                        new IntentFilter("INTENT_MENSAJE"));

                final LocationRequest locationRequest = new LocationRequest();
                locationRequest.setInterval(10000);
                locationRequest.setFastestInterval(30000);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat
                        .checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                LocationServices.getFusedLocationProviderClient( context )
                    .requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient( context )
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            double latitude = locationResult.getLocations().get(latestLocationIndex)
                                    .getLatitude();
                            double longitude =
                                    locationResult.getLocations().get(latestLocationIndex)
                                            .getLongitude();
                            if (tieneConexionAInternet( context )){
                                Map<String, Object> mLocalizacion = new HashMap<>();
                                mLocalizacion.put("longitud", Double.toString(longitude));
                                mLocalizacion.put("latitud", Double.toString(latitude));

                                BaseDeDatos.collection("emergencias").document(idEmergencia)
                                    .collection("localizacion").document(idEmergencia)
                                    .set(mLocalizacion).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("LOG", "Localizacion actualizada");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("LOG", "Error al actualizar localizacion: ", e);
                                        }
                                    });
                            }
                        }
                        }
                    }, Looper.myLooper());
                Handler handler = new Handler();
                handler.postDelayed(() -> {}, 1000);
            } while (!TERMINADA);
        }
    }

    public void ejecutarHiloParaActualizarDatosEnLaEmergencia (String idEmergencia, Context context){
        HiloParaActualizarDatosEnLaEmergencia hiloParaActualizarDatosEnLaEmergencia =
                new HiloParaActualizarDatosEnLaEmergencia( idEmergencia, context);
        hiloParaActualizarDatosEnLaEmergencia.start();
    }

}

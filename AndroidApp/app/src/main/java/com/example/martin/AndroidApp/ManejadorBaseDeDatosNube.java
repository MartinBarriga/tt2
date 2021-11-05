package com.example.martin.AndroidApp;

import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
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
        ContentValues usuario = generarFormatoDeUsuarioParaActualizarBD(usuarioCrudo, manejadorBaseDeDatosLocal);
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

    public String obtenerNombreDeUsuarioConIdDeEmergencia(String idEmergencia){
        try {
            String idUsuario = idEmergencia.substring(0,idEmergencia.length()-20);
            return (String) Tasks.await(BaseDeDatos.collection("usuario").whereEqualTo("idUsuario",
                    idUsuario).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                if (documentSnapshot.exists()) {
                                    Log.d("LOG", "Usuario encontrado: "+
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
                            Log.d("LOG", "UID: "+UID);
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
                                            (boolean) document.get("recibeAlertasDeUsuariosCercanos"));
                            manejadorBaseDeDatosLocal.agregarUsuario(manejadorBaseDeDatosLocal
                                    .generarFormatoDeUsuarioParaIntroducirEnBD(usuario));

                            //Agregar enfermedades del respaldo
                            ArrayList<String> enferdadesEnBD = manejadorBaseDeDatosLocal.obtenerEnfermedades();
                            String enfermedadesDelUsuario = (String) document.get("enfermedades");
                            String[] enfermedadesDelUsuarioArray = enfermedadesDelUsuario.split(", ");
                            for (String enfermedad :
                                    enfermedadesDelUsuarioArray) {
                                if (enferdadesEnBD.contains(enfermedad))
                                    manejadorBaseDeDatosLocal.agregarEnfermadadAUsuario(usuario.getIdUsuario(),
                                            (long) (enferdadesEnBD.indexOf(enfermedad) + 1));
                                else
                                    manejadorBaseDeDatosLocal.agregarEnfermadadAUsuario(usuario.getIdUsuario(),
                                            manejadorBaseDeDatosLocal.agregarEnfermedad(enfermedad));
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

    public void descargarResumen(ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal, String idEmergencia,
                                  Long idNotificacion, String nombre) {
        try {
            DocumentSnapshot document = Tasks.await(BaseDeDatos.collection("emergencias").document(idEmergencia).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot document) {
                            }
                    }));
            Log.d("LOG", "Descargando resumen: "+idEmergencia);
            Log.d("LOG", document.getId() + " => " + document.getData());
            String detalles;
            if (!((String)document.get("hospitalTrasladado")).matches("")){
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
                            ((Long)document.get("cantidadDePersonasEnviado")).intValue(),
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
        HiloParaDescargarRespaldo hiloParaDescargarRespaldo = new HiloParaDescargarRespaldo(manejadorBaseDeDatosLocal);
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
        contentUsuario.put("enviaAlertasAUsuariosCercanos", usuario.getEnviaAlertasAUsuariosCercanos());
        contentUsuario.put("recibeAlertasDeUsuariosCercanos", usuario.getRecibeAlertasDeUsuariosCercanos());
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
        HiloParaHacerRespaldo hiloParaHacerRespaldo = new HiloParaHacerRespaldo(manejadorBaseDeDatosLocal);
        hiloParaHacerRespaldo.start();
    }

    public boolean crearEmergencia(String idEmergencia, String fecha, String localizacion, int cantidadDePersonasEnviado){
        Map<String, Object> datosIniciales = new HashMap<>();
        datosIniciales.put("inicio", fecha);
        datosIniciales.put("terminada", false);
        datosIniciales.put("cantidadDePersonasEnviado", cantidadDePersonasEnviado);

        try {
            Tasks.await(BaseDeDatos.collection("emergencias").document(idEmergencia).set(datosIniciales)
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
                    }).addOnCompleteListener(new OnCompleteListener<Void>(){
                        @Override
                        public void onComplete(Task<Void> task){
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
                            if (task.isSuccessful()){
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

    public boolean revisarSiUnaEmergenciaFueTerminada(String idEmergencia){
        try {
            return (boolean) Tasks.await(BaseDeDatos.collection("emergencias").document(idEmergencia).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {
                                    Log.d("LOG", "Emergencia terminada: "+
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
        }
    }

    public void iniciarDescargaDeResumen(ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal, String idEmergencia,
                                         Long idNotificacion){
        HiloParaDescargarResumen hiloParaDescargarResumen = new HiloParaDescargarResumen(manejadorBaseDeDatosLocal,idEmergencia,idNotificacion);
        hiloParaDescargarResumen.start();
        try {
            hiloParaDescargarResumen.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class HiloParaHacerRespaldo extends Thread{
        ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal;
        HiloParaHacerRespaldo(ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal){
            this.manejadorBaseDeDatosLocal = manejadorBaseDeDatosLocal;
        }

        public void run(){
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

    class HiloParaDescargarRespaldo extends Thread{
        ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal;
        HiloParaDescargarRespaldo(ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal){
            this.manejadorBaseDeDatosLocal = manejadorBaseDeDatosLocal;
        }

        public void run(){
            descargarUsuario(manejadorBaseDeDatosLocal);
            descargarContactos(manejadorBaseDeDatosLocal);
            descargarNotificaciones(manejadorBaseDeDatosLocal);
        }
    }

    class HiloParaDescargarResumen extends Thread{
        private ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal;
        private String idEmergencia;
        private Long idNotificacion;

        HiloParaDescargarResumen(ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal, String idEmergencia,
                                 Long idNotificacion){
            this.manejadorBaseDeDatosLocal = manejadorBaseDeDatosLocal;
            this.idEmergencia = idEmergencia;
            this.idNotificacion = idNotificacion;
        }

        public void run(){
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

}

package com.example.martin.AndroidApp.ui.datosUsuario;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.martin.AndroidApp.ManejadorBaseDeDatosLocal;
import com.example.martin.AndroidApp.R;
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

public class DatosUsuarioFragment extends Fragment implements FirebaseAuth.AuthStateListener {

    FirebaseAuth mAuth;
    View root;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener asl = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            if (firebaseAuth.getCurrentUser() == null) {
                Log.d("LOG", "Current user = null.");
                (new Handler()).postDelayed(new Runnable() {
                    public void run() {
                        System.exit(0);
                    }
                }, 3000);
            }
        }
    };
    private FirebaseFirestore fdb;
    private DatosUsuarioViewModel datosUsuarioViewModel;
    private ManejadorBaseDeDatosLocal mManejadorBaseDeDatosLocal;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        datosUsuarioViewModel =
                ViewModelProviders.of(this).get(DatosUsuarioViewModel.class);
        mAuth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(asl);

        final FirebaseUser currentUser = mAuth.getCurrentUser();
        fdb = FirebaseFirestore.getInstance();
        Log.d("LOG", "Ya se debió agregar el listener");

        mManejadorBaseDeDatosLocal = new ManejadorBaseDeDatosLocal(this.getContext(), null);
        UserInfo usuario = mManejadorBaseDeDatosLocal.obtenerUsuario(currentUser.getUid());

        root = inflater.inflate(R.layout.fragment_datos_usuario, container, false);

        EditText text;

        text = root.findViewById(R.id.nombre);
        text.setText(usuario.getNombre());
        text = root.findViewById(R.id.telefono);
        if (usuario.getTelefono() != 0)
            text.setText(String.valueOf(usuario.getTelefono()));
        text = root.findViewById(R.id.edad);
        if (usuario.getEdad() != 0)
            text.setText(String.valueOf(usuario.getEdad()));
        text = root.findViewById(R.id.mensaje);
        text.setText(usuario.getMensaje());
        text = root.findViewById(R.id.nss);
        if (usuario.getNss() != 0)
            text.setText(String.valueOf(String.valueOf(usuario.getNss())));
        text = root.findViewById(R.id.medicacion);
        text.setText(usuario.getMedicacion());
        text = root.findViewById(R.id.enfermedades);
        text.setText(usuario.getEnfermedades());
        text = root.findViewById(R.id.toxicomanias);
        text.setText(usuario.getToxicomanias());
        text = root.findViewById(R.id.tipoSangre);
        text.setText(usuario.getTipoSangre());
        text = root.findViewById(R.id.Alergias);
        text.setText(usuario.getAlergias());
        text = root.findViewById(R.id.religion);
        text.setText(usuario.getReligion());

        Button cerrarSesion = root.findViewById(R.id.cerrarsSesion);
        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion(v, getContext());
            }
        });

        Button guardar = root.findViewById(R.id.guardar);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDatos(v, getContext());
            }
        });


        return root;
    }

    public void cerrarSesion(View view, Context context) {
        Toast.makeText(context, "Cerrando sesión...", Toast.LENGTH_LONG).show();
        auth.signOut();
    }

    public void guardarDatos(View view, Context context) {
        EditText text;
        final String nombre, medicacion, enfermedades, taxicomanias, tipoSangre, mensaje, alergias,
                religion;
        final int edad;
        final Long nss, telefono;

        ContentValues usuario = new ContentValues();
        text = root.findViewById(R.id.nombre);
        nombre = text.getText().toString();

        text = root.findViewById(R.id.telefono);
        if (!text.getText().toString().matches("")) {
            String tel = text.getText().toString().replace(" ", "");
            telefono = Long.valueOf(tel.substring(tel.length() - 10));

        } else {
            telefono = Long.valueOf(0);
        }

        text = root.findViewById(R.id.edad);
        if (!text.getText().toString().matches("")) {
            edad = Integer.parseInt(text.getText().toString());

        } else {
            edad = 0;
        }

        text = root.findViewById(R.id.mensaje);
        if (!text.getText().toString().matches("")) {
            mensaje = text.getText().toString();
        } else {
            mensaje =
                    "Me encuentro en una emergencia. A continuación se muestra mi ubicación " +
                            "actual y algunos datos personales.";
        }


        text = root.findViewById(R.id.nss);
        if (!text.getText().toString().matches("")) {
            nss = Long.valueOf(text.getText().toString());
        } else {
            nss = Long.valueOf(0);
        }


        text = root.findViewById(R.id.medicacion);
        medicacion = text.getText().toString();

        text = root.findViewById(R.id.enfermedades);
        enfermedades = text.getText().toString();

        text = root.findViewById(R.id.toxicomanias);
        taxicomanias = text.getText().toString();

        text = root.findViewById(R.id.tipoSangre);
        tipoSangre = text.getText().toString();

        text = root.findViewById(R.id.Alergias);
        alergias = text.getText().toString();

        text = root.findViewById(R.id.religion);
        religion = text.getText().toString();

        usuario.put("nombre", nombre);
        usuario.put("telefono", telefono);
        usuario.put("edad", edad);
        usuario.put("mensaje", mensaje);
        usuario.put("nss", nss);
        usuario.put("medicacion", medicacion);
        usuario.put("enfermedades", enfermedades);
        usuario.put("toxicomanias", taxicomanias);
        usuario.put("tiposangre", tipoSangre);
        usuario.put("alergias", alergias);
        usuario.put("religion", religion);
        String idUsuario = mAuth.getCurrentUser().getUid();

        mManejadorBaseDeDatosLocal.actualizarUsuario(idUsuario, usuario);

        fdb.collection("usuario").whereEqualTo("uID", idUsuario).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("LOG", document.getId() + " => " + document.getData());

                                DocumentReference user =
                                        fdb.collection("usuario").document(document.getId());
                                user.update("nombre", nombre)
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
                                user.update("telefono", telefono)
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
                                user.update("edad", edad)
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
                                user.update("nss", nss)
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
                                user.update("medicacion", medicacion)
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
                                user.update("enfermedades", enfermedades)
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
                                user.update("taxicomanias", taxicomanias)
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
                                user.update("tipoSangre", tipoSangre)
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
                                user.update("alergias", alergias)
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
                                user.update("religion", religion)
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
        Toast.makeText(context, "Datos actualizados.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth auth) {
        if (auth.getCurrentUser() == null) {
            Log.d("LOG", "Current user = null.");
            Toast.makeText(getContext(), "Ha cerrado su sesión.", Toast.LENGTH_LONG).show();
            (new Handler()).postDelayed(new Runnable() {
                public void run() {
                    System.exit(0);
                }
            }, 3000);
        }
    }
}

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

import com.example.martin.AndroidApp.ConexionSQLiteHelper;
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
    private FirebaseFirestore fdb;


    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener asl = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            if(firebaseAuth.getCurrentUser()==null) {
                Log.d("LOG", "Current user = null.");
                (new Handler()).postDelayed(new Runnable() {
                    public void run() {
                        System.exit(0);
                    }
                }, 3000);
            }
        }
    };
    private DatosUsuarioViewModel datosUsuarioViewModel;
    ConexionSQLiteHelper conn;
    SQLiteDatabase db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        datosUsuarioViewModel =
                ViewModelProviders.of(this).get(DatosUsuarioViewModel.class);
        mAuth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(asl);

        final FirebaseUser currentUser = mAuth.getCurrentUser();
        fdb = FirebaseFirestore.getInstance();
        Log.d("LOG", "Ya se debió agregar el listener");
        conn = conn = new ConexionSQLiteHelper(this.getContext(),"lifeguard", null, 2);
        db = conn.getReadableDatabase();




        Cursor cursor = db.rawQuery("SELECT * FROM usuario WHERE Uid LIKE '"+currentUser.getUid()+"'", null);

        root = inflater.inflate(R.layout.fragment_datos_usuario, container, false);;
        EditText text;
        while (cursor.moveToNext()){
            text = root.findViewById(R.id.nombre);
            text.setText(cursor.getString(1));
            text = root.findViewById(R.id.telefono);
            if(cursor.getInt(2)!=0)
                text.setText(String.valueOf(String.valueOf(cursor.getLong(2))));
            text.setText(String.valueOf(String.valueOf(cursor.getLong(2))));
            text = root.findViewById(R.id.edad);
            if(cursor.getInt(4)!=0)
                text.setText(String.valueOf(cursor.getInt(4)));
            text = root.findViewById(R.id.mensaje);
            text.setText(cursor.getString(5));
            text = root.findViewById(R.id.nss);
            if(cursor.getLong(6)!=0)
                text.setText(String.valueOf(String.valueOf(cursor.getLong(6))));
            text = root.findViewById(R.id.medicacion);
            text.setText(cursor.getString(7));
            text = root.findViewById(R.id.enfermedades);
            text.setText(cursor.getString(8));
            text = root.findViewById(R.id.toxicomanias);
            text.setText(cursor.getString(9));
            text = root.findViewById(R.id.tipoSangre);
            text.setText(cursor.getString(10));
            text = root.findViewById(R.id.Alergias);
            text.setText(cursor.getString(11));
            text = root.findViewById(R.id.religion);
            text.setText(cursor.getString(12));
        }

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

    public void cerrarSesion(View view, Context context){
        Toast.makeText(context, "Cerrando sesión...", Toast.LENGTH_LONG).show();
        auth.signOut();
    }

    public void guardarDatos(View view, Context context){
        db = conn.getWritableDatabase();
        EditText text;
        final String nombre, medicacion, enfermedades, taxicomanias, tipoSangre, mensaje, alergias, religion;
        final int edad;
        final Long nss, telefono;
        ContentValues userValues = new ContentValues();
        text = root.findViewById(R.id.nombre);
        nombre = text.getText().toString();
        userValues.put("nombre", nombre);
        text = root.findViewById(R.id.telefono);
        if (!text.getText().toString().matches("")){
            String tel = text.getText().toString().replace(" ", "");
            telefono = Long.valueOf(tel.substring(tel.length()-10));

        }else{
            telefono = Long.valueOf(0);
        }
        userValues.put("telefono", telefono);
        text = root.findViewById(R.id.edad);
        if (!text.getText().toString().matches("")){
            edad = Integer.parseInt(text.getText().toString());

        }else{
            edad = 0;
        }
        userValues.put("edad", edad);
        text = root.findViewById(R.id.mensaje);
        if (!text.getText().toString().matches("")){
            mensaje = text.getText().toString();
        }else{
            mensaje = "Me encuentro en una emergencia. A continuación se muestra mi ubicación actual y algunos datos personales.";
        }
        userValues.put("mensaje", mensaje);

        text = root.findViewById(R.id.nss);
        if (!text.getText().toString().matches("")){
            nss = Long.valueOf(text.getText().toString());
        }else{
            nss = Long.valueOf(0);
        }
        userValues.put("nss", nss);

        text = root.findViewById(R.id.medicacion);
        medicacion = text.getText().toString();
        userValues.put("medicacion", medicacion);
        text = root.findViewById(R.id.enfermedades);
        enfermedades = text.getText().toString();
        userValues.put("enfermedades", enfermedades);
        text = root.findViewById(R.id.toxicomanias);
        taxicomanias = text.getText().toString();
        userValues.put("toxicomanias", taxicomanias);
        text = root.findViewById(R.id.tipoSangre);
        tipoSangre = text.getText().toString();
        userValues.put("tiposangre", tipoSangre);
        text = root.findViewById(R.id.Alergias);
        alergias = text.getText().toString();
        userValues.put("alergias", alergias);
        text = root.findViewById(R.id.religion);
        religion = text.getText().toString();
        userValues.put("religion", religion);

        String uID = mAuth.getCurrentUser().getUid();
        db.update("usuario", userValues, "Uid LIKE '"+uID+"'", null);
        fdb.collection("usuario").whereEqualTo("uID", uID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("LOG", document.getId() + " => " + document.getData());

                        DocumentReference user = fdb.collection("usuario").document(document.getId());
                        user.update("nombre", nombre)
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
                        user.update("telefono", telefono)
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
                        user.update("edad", edad)
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
                        user.update("nss", nss)
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
                        user.update("medicacion", medicacion)
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
                        user.update("enfermedades", enfermedades)
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
                        user.update("taxicomanias", taxicomanias)
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
                        user.update("tipoSangre", tipoSangre)
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
                        user.update("alergias", alergias)
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
                        user.update("religion", religion)
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
        Toast.makeText(context, "Datos actualizados.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth auth) {
        if(auth.getCurrentUser()==null){
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

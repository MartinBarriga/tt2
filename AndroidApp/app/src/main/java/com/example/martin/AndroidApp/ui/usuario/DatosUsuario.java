package com.example.martin.AndroidApp.ui.usuario;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.martin.AndroidApp.ManejadorBaseDeDatosLocal;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosNube;
import com.example.martin.AndroidApp.R;
import com.example.martin.AndroidApp.UserInfo;

public class DatosUsuario extends AppCompatActivity {
    private ManejadorBaseDeDatosLocal mManejadorBaseDeDatosLocal;
    private ManejadorBaseDeDatosNube mManejadorBaseDeDatosNube;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_usuario);

        Log.d("LOG", "Ya se debió agregar el listener");

        mManejadorBaseDeDatosLocal = new ManejadorBaseDeDatosLocal(getApplicationContext(), null);
        mManejadorBaseDeDatosNube = new ManejadorBaseDeDatosNube();
        UserInfo usuario = mManejadorBaseDeDatosLocal
                .obtenerUsuario(mManejadorBaseDeDatosNube.obtenerIdUsuario());

        EditText text;

        text = findViewById(R.id.nombre);
        text.setText(usuario.getNombre());
        text = findViewById(R.id.telefono);
        if (usuario.getTelefono() != 0)
            text.setText(String.valueOf(usuario.getTelefono()));
        text = findViewById(R.id.edad);
        if (usuario.getEdad() != 0)
            text.setText(String.valueOf(usuario.getEdad()));
        text = findViewById(R.id.mensaje);
        text.setText(usuario.getMensaje());
        text = findViewById(R.id.nss);
        if (usuario.getNss() != 0)
            text.setText(String.valueOf(String.valueOf(usuario.getNss())));
        text = findViewById(R.id.medicacion);
        text.setText(usuario.getMedicacion());
        text = findViewById(R.id.enfermedades);
        text.setText(usuario.getEnfermedades());
        text = findViewById(R.id.toxicomanias);
        text.setText(usuario.getToxicomanias());
        text = findViewById(R.id.tipoSangre);
        text.setText(usuario.getTipoSangre());
        text = findViewById(R.id.Alergias);
        text.setText(usuario.getAlergias());
        text = findViewById(R.id.religion);
        text.setText(usuario.getReligion());

        Button guardar = findViewById(R.id.guardar);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDatos(v, getApplicationContext());
            }
        });
    }


    public void guardarDatos(View view, Context context) {
        EditText text;
        final String nombre, medicacion, enfermedades, taxicomanias, tipoSangre, mensaje, alergias,
                religion;
        final int edad;
        final Long nss, telefono;

        ContentValues usuario = new ContentValues();
        text = findViewById(R.id.nombre);
        nombre = text.getText().toString();

        text = findViewById(R.id.telefono);
        if (!text.getText().toString().matches("")) {
            String tel = text.getText().toString().replace(" ", "");
            telefono = Long.valueOf(tel.substring(tel.length() - 10));

        } else {
            telefono = Long.valueOf(0);
        }

        text = findViewById(R.id.edad);
        if (!text.getText().toString().matches("")) {
            edad = Integer.parseInt(text.getText().toString());

        } else {
            edad = 0;
        }

        text = findViewById(R.id.mensaje);
        if (!text.getText().toString().matches("")) {
            mensaje = text.getText().toString();
        } else {
            mensaje =
                    "Me encuentro en una emergencia. A continuación se muestra mi ubicación " +
                            "actual y algunos datos personales.";
        }


        text = findViewById(R.id.nss);
        if (!text.getText().toString().matches("")) {
            nss = Long.valueOf(text.getText().toString());
        } else {
            nss = Long.valueOf(0);
        }


        text = findViewById(R.id.medicacion);
        medicacion = text.getText().toString();

        text = findViewById(R.id.enfermedades);
        enfermedades = text.getText().toString();

        text = findViewById(R.id.toxicomanias);
        taxicomanias = text.getText().toString();

        text = findViewById(R.id.tipoSangre);
        tipoSangre = text.getText().toString();

        text = findViewById(R.id.Alergias);
        alergias = text.getText().toString();

        text = findViewById(R.id.religion);
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

        mManejadorBaseDeDatosLocal
                .actualizarUsuario(mManejadorBaseDeDatosNube.obtenerIdUsuario(), usuario);
        mManejadorBaseDeDatosNube.actualizarUsuario(usuario);

        Toast.makeText(context, "Datos actualizados.", Toast.LENGTH_LONG).show();
    }
}
package com.example.martin.AndroidApp.ui.usuario;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.martin.AndroidApp.Countdown;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosLocal;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosNube;
import com.example.martin.AndroidApp.R;
import com.example.martin.AndroidApp.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class UsuarioFragment extends Fragment{

    private View vista;
    private ManejadorBaseDeDatosLocal mManejadorBaseDeDatosLocal;
    private ManejadorBaseDeDatosNube mManejadorBaseDeDatosNube;
    private FirebaseAuth auth = FirebaseAuth.getInstance();;

    public UsuarioFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        auth.addAuthStateListener(asl);
        mManejadorBaseDeDatosLocal = new ManejadorBaseDeDatosLocal(this.getContext(), null);
        mManejadorBaseDeDatosNube = new ManejadorBaseDeDatosNube();
        Usuario usuario = mManejadorBaseDeDatosLocal
                .obtenerUsuario(mManejadorBaseDeDatosNube.obtenerIdUsuario());

        vista = inflater.inflate(R.layout.fragment_usuario, container, false);
        TextView saludo = vista.findViewById(R.id.saludo);
        String nombre = usuario.getNombre();
        if (nombre.contains(" ")) {
            nombre = nombre.substring(0, nombre.indexOf(" "));
        }
        saludo.setText("¡Hola, " + nombre + "!");

        CardView datos_medicos = vista.findViewById(R.id.datos_medicos);
        datos_medicos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("PRECIONASTE DATOS MEDICOS");
                Intent intent = new Intent(getContext(), DatosUsuario.class);
                startActivity(intent);
            }
        });

        CardView instructivo = vista.findViewById(R.id.instructivo);
        instructivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("PRESIONASTE INSTRUCTIVO");
            }
        });

        CardView respaldo = vista.findViewById(R.id.respaldo);
        respaldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("PRESIONASTE RESPALDO");
                Intent intent = new Intent(getContext(), Respaldo.class);
                startActivity(intent);
            }
        });

        CardView cerrar_sesion = vista.findViewById(R.id.cerrar_sesion);
        cerrar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("PRESIONASTE CERRAR SESION");
                cerrarSesion(view, getContext());
            }
        });

        FloatingActionButton botonEmergencia = vista.findViewById(R.id.boton_emergencia);
        botonEmergencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Countdown.class);
                startActivity(intent);
            }
        });
        return vista;
    }

    public void cerrarSesion(View view, Context context) {
        Toast.makeText(context, "Cerrando sesión...", Toast.LENGTH_LONG).show();
        auth.signOut();
    }

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
}
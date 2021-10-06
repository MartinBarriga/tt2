package com.example.martin.AndroidApp.ui.usuario;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.martin.AndroidApp.ManejadorBaseDeDatosLocal;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosNube;
import com.example.martin.AndroidApp.R;
import com.example.martin.AndroidApp.Usuario;

import java.util.Set;

public class Respaldo extends AppCompatActivity {
    private ManejadorBaseDeDatosLocal mManejadorBaseDeDatosLocal;
    private ManejadorBaseDeDatosNube mManejadorBaseDeDatosNube;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respaldo);

        mManejadorBaseDeDatosLocal = new ManejadorBaseDeDatosLocal(getApplicationContext(), null);
        mManejadorBaseDeDatosNube = new ManejadorBaseDeDatosNube();
        usuario = mManejadorBaseDeDatosLocal
                .obtenerUsuario(mManejadorBaseDeDatosNube.obtenerIdUsuario());

        TextView fechaUltimoRespaldo = findViewById(R.id.textViewFechaUltimoRespaldo);
        Button botonRealizarRespaldo = findViewById(R.id.botonRealizarCopia);
        ImageButton botonInformacion = findViewById(R.id.botonInformacionRespaldo);
        Spinner spinnerFrecuenciaRespaldo = findViewById(R.id.spinnerFrecuenciaDeRespaldo);

        fechaUltimoRespaldo.setText(usuario.getFechaUltimoRespaldo());
        String[] opcionesFrecuenciaRespaldo =
                {"Cada dia", "Cada 2 dias", "Cada 5 dias", "Cada 7 dias"};
        ArrayAdapter<String> adaptadorFrecuenciaRespaldo =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                        opcionesFrecuenciaRespaldo);
        spinnerFrecuenciaRespaldo.setAdapter(adaptadorFrecuenciaRespaldo);
        spinnerFrecuenciaRespaldo.setSelection(
                adaptadorFrecuenciaRespaldo.getPosition(usuario.getFrecuenciaRespaldo()));

        spinnerFrecuenciaRespaldo
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                               long arg3) {
                        //do something here
                        String frecuenciaRespaldo =
                                spinnerFrecuenciaRespaldo.getSelectedItem().toString();
                        usuario.setFrecuenciaRespaldo(frecuenciaRespaldo);
                        usuario.setEnNube(false);
                        mManejadorBaseDeDatosLocal.actualizarUsuario(
                                mManejadorBaseDeDatosLocal
                                        .generarFormatoDeUsuarioParaIntroducirEnBD(usuario));
                        Log.i("Frecuencia Seleccionada : ", frecuenciaRespaldo);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        //optionally do something here
                    }
                });


        botonRealizarRespaldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mManejadorBaseDeDatosNube.realizarRespaldo(mManejadorBaseDeDatosLocal);
            }
        });

        botonInformacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
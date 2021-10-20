package com.example.martin.AndroidApp.ui.usuario;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

public class Respaldo extends AppCompatActivity {
    private ManejadorBaseDeDatosLocal mManejadorBaseDeDatosLocal;
    private ManejadorBaseDeDatosNube mManejadorBaseDeDatosNube;
    private Usuario usuario;
    private AlarmManager alarma;
    private Intent intent;
    private PendingIntent intentPendiente;

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
                        System.out.println("Frecuencia Seleccionada: " + frecuenciaRespaldo);
                        programarRespaldo();
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

    private void programarRespaldo() {
        int equivalenciaEnMilisegundosEnUnDia = 24 * 60 * 60 * 1000;
        int frecuenciaRespaldoEnMillis;
        Usuario usuario = mManejadorBaseDeDatosLocal
                .obtenerUsuario(mManejadorBaseDeDatosNube.obtenerIdUsuario());
        String frecuenciaRespaldo = usuario.getFrecuenciaRespaldo();
        String fechaUltimoRespaldoString = usuario.getFechaUltimoRespaldo();
        Calendar fechaUltimoRespaldo = Calendar.getInstance();
        if (!fechaUltimoRespaldoString.matches("Sin respaldo previo")) {
            fechaUltimoRespaldoString =
                    fechaUltimoRespaldoString.substring(0, fechaUltimoRespaldoString.indexOf(" "));
            SimpleDateFormat formatoParaFechas = new SimpleDateFormat("yyyy/MM/dd");

            fechaUltimoRespaldo = Calendar.getInstance();
            try {
                fechaUltimoRespaldo.setTime(formatoParaFechas.parse(fechaUltimoRespaldoString));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        fechaUltimoRespaldo.set(Calendar.HOUR_OF_DAY, 0);
        fechaUltimoRespaldo.set(Calendar.MINUTE, 0);
        fechaUltimoRespaldo.set(Calendar.SECOND, 0);
        fechaUltimoRespaldo.set(Calendar.MILLISECOND, 0);

        switch (frecuenciaRespaldo) {
            case "Cada 2 dias":
                frecuenciaRespaldoEnMillis = equivalenciaEnMilisegundosEnUnDia * 2;
                break;
            case "Cada 5 dias":
                frecuenciaRespaldoEnMillis = equivalenciaEnMilisegundosEnUnDia * 5;
                break;
            case "Cada 7 dias":
                frecuenciaRespaldoEnMillis = equivalenciaEnMilisegundosEnUnDia * 7;
                break;
            default:
                frecuenciaRespaldoEnMillis = equivalenciaEnMilisegundosEnUnDia;
                break;
        }


        alarma = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(getApplicationContext(), RespaldoAutomatico.class);
        intentPendiente = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0);
        //alarma.setExact(AlarmManager.RTC_WAKEUP, calendario.getTimeInMillis(), intentPendiente);
        //alarma.setRepeating(AlarmManager.RTC_WAKEUP, calendario.getTimeInMillis(), 10*1000,
        // intentPendiente);
        if (alarma != null) {
            alarma.cancel(intentPendiente);
        }
        alarma.setExact(AlarmManager.RTC_WAKEUP,
                fechaUltimoRespaldo.getTimeInMillis() + frecuenciaRespaldoEnMillis,
                intentPendiente);
    }
}
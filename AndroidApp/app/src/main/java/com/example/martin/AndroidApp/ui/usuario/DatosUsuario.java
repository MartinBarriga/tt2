package com.example.martin.AndroidApp.ui.usuario;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.martin.AndroidApp.Countdown;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosLocal;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosNube;
import com.example.martin.AndroidApp.R;
import com.example.martin.AndroidApp.Usuario;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

import static com.example.martin.AndroidApp.R.color.browser_actions_text_color;
import static com.example.martin.AndroidApp.R.color.common_google_signin_btn_text_light_default;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DatosUsuario extends AppCompatActivity {
    private ManejadorBaseDeDatosLocal mManejadorBaseDeDatosLocal;
    private ManejadorBaseDeDatosNube mManejadorBaseDeDatosNube;
    ArrayList listaDeEnfermedades;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_usuario);

        Log.d("LOG", "Ya se debió agregar el listener");

        mManejadorBaseDeDatosLocal = new ManejadorBaseDeDatosLocal(getApplicationContext(), null);
        mManejadorBaseDeDatosNube = new ManejadorBaseDeDatosNube();
        Usuario usuario = mManejadorBaseDeDatosLocal
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
        text = findViewById(R.id.nss);
        if (usuario.getNss() != 0)
            text.setText(String.valueOf(usuario.getNss()));
        text = findViewById(R.id.medicacion);
        text.setText(usuario.getMedicacion());
        listaDeEnfermedades = mManejadorBaseDeDatosLocal.obtenerEnfermedades();
        ArrayAdapter<String> adaptadorEnfermedades = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, listaDeEnfermedades);
        AutoCompleteTextView textViewEnfermedades = findViewById(R.id.enfermedades);
        textViewEnfermedades.setAdapter(adaptadorEnfermedades);
        textViewEnfermedades.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                listaDeEnfermedades = mManejadorBaseDeDatosLocal.obtenerEnfermedades();
                mManejadorBaseDeDatosLocal.agregarEnfermadadAUsuario(usuario.getIdUsuario(),
                        (long) (listaDeEnfermedades.indexOf(adapterView.getItemAtPosition(position)) + 1) );
                actualizarChipGroupDeEnfermedades();
                textViewEnfermedades.setText("");
            }
        });
        textViewEnfermedades.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction()==KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    final AlertDialog.Builder mensajeDePermiso = new AlertDialog.Builder(DatosUsuario.this);
                    mensajeDePermiso.setTitle("Agregar enfermedad");
                    mensajeDePermiso.setMessage( "La enfermedad \"" + textViewEnfermedades.getText().toString() +
                            "\" no había sido registrada anteriormente, ¿deseas agregarla? Por favor, revisa que" +
                            " esté escrita correctamente.");
                    mensajeDePermiso.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mManejadorBaseDeDatosLocal.agregarEnfermadadAUsuario(usuario.getIdUsuario(),
                                    mManejadorBaseDeDatosLocal.agregarEnfermedad(
                                            textViewEnfermedades.getText().toString()));
                            listaDeEnfermedades = mManejadorBaseDeDatosLocal.obtenerEnfermedades();
                            adaptadorEnfermedades.add(textViewEnfermedades.getText().toString());
                            textViewEnfermedades.setAdapter(adaptadorEnfermedades);
                            textViewEnfermedades.setText("");
                            actualizarChipGroupDeEnfermedades();
                        }
                    });
                    mensajeDePermiso.setNegativeButton(getString(R.string.newNameDialogCancelingButton),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // InputMethodManager imm = (InputMethodManager)getSystemService
                                    // (Context.INPUT_METHOD_SERVICE);
                                    //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                    dialog.cancel();
                                }
                            });
                    mensajeDePermiso.create().show();
                    return true;
                }
                return false;
            }
        });
        actualizarChipGroupDeEnfermedades();
        text = findViewById(R.id.toxicomanias);
        text.setText(usuario.getToxicomanias());
        text = findViewById(R.id.tipoSangre);
        text.setText(usuario.getTipoSangre());
        text = findViewById(R.id.Alergias);
        text.setText(usuario.getAlergias());
        text = findViewById(R.id.religion);
        text.setText(usuario.getReligion());
        text = findViewById(R.id.frecuenciaCardiacaMinima);
        if (usuario.getFrecuenciaCardiacaMinima() != -1)
            text.setText(String.valueOf(usuario.getFrecuenciaCardiacaMinima()));
        text = findViewById(R.id.frecuenciaCardiacaMaxima);
        if (usuario.getFrecuenciaCardiacaMaxima() != -1)
            text.setText(String.valueOf(usuario.getFrecuenciaCardiacaMaxima()));

        Button guardar = findViewById(R.id.guardar);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDatos(v, getApplicationContext());
            }
        });

        FloatingActionButton botonEmergencia = findViewById(R.id.boton_emergencia);
        botonEmergencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Countdown.class);
                startActivity(intent);
            }
        });
    }


    public void guardarDatos(View view, Context context) {
        EditText text;
        final String nombre, medicacion, enfermedades, toxicomanias, tipoSangre, mensaje, alergias,
                religion;
        final int edad, frecuenciaCardiacaMinima, frecuenciaCardiacaMaxima;
        final Long nss, telefono;

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

        text = findViewById(R.id.nss);
        if (!text.getText().toString().matches("")) {
            nss = Long.valueOf(text.getText().toString());
        } else {
            nss = Long.valueOf(0);
        }


        text = findViewById(R.id.medicacion);
        medicacion = text.getText().toString();

        text = findViewById(R.id.toxicomanias);
        toxicomanias = text.getText().toString();

        text = findViewById(R.id.tipoSangre);
        tipoSangre = text.getText().toString();

        text = findViewById(R.id.Alergias);
        alergias = text.getText().toString();

        text = findViewById(R.id.religion);
        religion = text.getText().toString();

        text = findViewById(R.id.frecuenciaCardiacaMinima);
        if(!(text.getText().toString().matches(""))) {
            frecuenciaCardiacaMinima = Integer.parseInt(text.getText().toString());
        } else {
            frecuenciaCardiacaMinima = -1;
        }


        text = findViewById(R.id.frecuenciaCardiacaMaxima);
        if(!(text.getText().toString().matches(""))) {
            frecuenciaCardiacaMaxima = Integer.parseInt(text.getText().toString());
        } else {
            frecuenciaCardiacaMaxima = -1;
        }

        String idUsuario = mManejadorBaseDeDatosNube.obtenerIdUsuario();
        Usuario usuarioViejo = mManejadorBaseDeDatosLocal.obtenerUsuario(idUsuario);
        Usuario usuario =
                new Usuario(idUsuario, nombre, telefono, edad, nss,
                        medicacion, toxicomanias, tipoSangre, alergias, religion,
                        false, usuarioViejo.getFechaUltimoRespaldo(),
                        usuarioViejo.getFrecuenciaRespaldo(), frecuenciaCardiacaMinima,
                        frecuenciaCardiacaMaxima, usuarioViejo.getEnviaAlertasAUsuariosCercanos(),
                        usuarioViejo.getRecibeAlertasDeUsuariosCercanos());

        mManejadorBaseDeDatosLocal
                .actualizarUsuario(mManejadorBaseDeDatosLocal
                        .generarFormatoDeUsuarioParaIntroducirEnBD(usuario));

        Toast.makeText(context, "Datos actualizados.", Toast.LENGTH_LONG).show();
    }

    private void actualizarChipGroupDeEnfermedades(){
        ChipGroup enfermedadesChipGroup = findViewById(R.id.enfermedadesChipGroup);
        enfermedadesChipGroup.removeAllViews();
        ArrayList<String> enfermedadesDelUsuario = mManejadorBaseDeDatosLocal.obtenerEnfermedadesDeUnUsuario(
                mManejadorBaseDeDatosNube.obtenerIdUsuario());
        for (String enfermedad : enfermedadesDelUsuario) {
            Chip chip = new Chip(this);
            chip.setText(enfermedad);
            chip.setCheckable(false);
            chip.setTextSize(18);
            chip.setCheckedIconVisible(false);
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Log.d("OnCloseIconClickListener", (String) ((Chip) view).getText());
                        listaDeEnfermedades = mManejadorBaseDeDatosLocal.obtenerEnfermedades();
                        mManejadorBaseDeDatosLocal.eliminarEnfermedadDeUsuario(
                                mManejadorBaseDeDatosNube.obtenerIdUsuario(),
                                (long) (listaDeEnfermedades.indexOf(((Chip) view).getText()) + 1) );
                        actualizarChipGroupDeEnfermedades();
                    } catch ( Exception e){
                        Log.d("OnCloseIconClickListener", e.getMessage());
                    }
                }
            });
            enfermedadesChipGroup.addView(chip);
        }
    }
}
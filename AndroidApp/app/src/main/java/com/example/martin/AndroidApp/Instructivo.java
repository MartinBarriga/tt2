package com.example.martin.AndroidApp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class Instructivo extends AppCompatActivity {
    public static final int PANTALLA_INSTRUCTIVO = 1;
    public static final int PANTALLA_CONTACTOS = 2;
    public static final int PANTALLA_NOTIFICACIONES = 3;
    public static final int PANTALLA_USUARIO = 4;
    public static final int PANTALLA_DATOS_USUARIO = 5;
    public static final int PANTALLA_RESPALDO = 6;
    public static final int PANTALLA_MEDICIONES_TIEMPO_REAL = 7;
    public static final int PANTALLA_HISTORIAL_MEDICIONES = 8;
    private static final int DISTANCIA_MINIMA = 150;
    private VideoView video;
    private TextView tituloPantalla;
    private TextView descripcionPantalla;
    private ImageView circulo1;
    private ImageView circulo2;
    private ImageView circulo3;
    private ImageView circulo4;
    private ImageView circulo5;
    private ImageView circulo6;
    private ImageView circulo7;
    private ImageView circulo8;
    private float posicionX1, posicionX2;
    private int pantallaActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructivo);
        pantallaActual = getIntent().getExtras().getInt("pantalla");
        video = findViewById(R.id.videoInstructivo);
        tituloPantalla = findViewById(R.id.tituloPantalla);
        descripcionPantalla = findViewById(R.id.descripcionPantalla);
        circulo1 = findViewById(R.id.circulo1);
        circulo2 = findViewById(R.id.circulo2);
        circulo3 = findViewById(R.id.circulo3);
        circulo4 = findViewById(R.id.circulo4);
        circulo5 = findViewById(R.id.circulo5);
        circulo6 = findViewById(R.id.circulo6);
        circulo7 = findViewById(R.id.circulo7);
        circulo8 = findViewById(R.id.circulo8);
        configurarVistasConInformacionCorrespondiente();
        MediaController controladorVideo = new MediaController(this);
        video.setMediaController(controladorVideo);
        controladorVideo.setAnchorView(video);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                posicionX1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                posicionX2 = event.getX();
                float deltaX = posicionX2 - posicionX1;
                if (Math.abs(deltaX) > DISTANCIA_MINIMA) {
                    if (posicionX2 > posicionX1) {
                        if (pantallaActual > PANTALLA_INSTRUCTIVO) {
                            pantallaActual--;
                            configurarVistasConInformacionCorrespondiente();
                        }
                    } else {
                        if (pantallaActual < PANTALLA_HISTORIAL_MEDICIONES) {
                            pantallaActual++;
                            configurarVistasConInformacionCorrespondiente();
                        }
                    }

                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void configurarVistasConInformacionCorrespondiente() {
        circulo1.setColorFilter(Color.LTGRAY);
        circulo2.setColorFilter(Color.LTGRAY);
        circulo3.setColorFilter(Color.LTGRAY);
        circulo4.setColorFilter(Color.LTGRAY);
        circulo5.setColorFilter(Color.LTGRAY);
        circulo6.setColorFilter(Color.LTGRAY);
        circulo7.setColorFilter(Color.LTGRAY);
        circulo8.setColorFilter(Color.LTGRAY);
        switch (pantallaActual) {
            case PANTALLA_CONTACTOS:
                tituloPantalla.setText("Contactos");
                descripcionPantalla.setText("Descripción pantalla contactos. En el presente " +
                        "documento se propone un prototipo de sistema, el cual tiene como " +
                        "objetivo alertar y dar seguimiento en tiempo real cuando un usuario " +
                        "presente anomalías en su frecuencia cardiaca o niveles bajos de " +
                        "saturación de oxígeno en la sangre, para de este modo aumentar las " +
                        "probabilidades de que sea atendido prontamente evitando la muerte o daño" +
                        " cerebral.");
                circulo2.setColorFilter(Color.GRAY);
                video.setVideoURI(Uri.parse(
                        "android.resource://" + getPackageName() + "/" +
                                R.raw.instructivo_contactos));
                break;
            case PANTALLA_NOTIFICACIONES:
                tituloPantalla.setText("Notificaciones");
                descripcionPantalla.setText("Descripción pantalla notificaciones. En el presente " +
                        "documento se propone un prototipo de sistema, el cual tiene como " +
                        "objetivo alertar y dar seguimiento en tiempo real cuando un usuario " +
                        "presente anomalías en su frecuencia cardiaca o niveles bajos de " +
                        "saturación de oxígeno en la sangre, para de este modo aumentar las " +
                        "probabilidades de que sea atendido prontamente evitando la muerte o daño" +
                        " cerebral.");
                circulo3.setColorFilter(Color.GRAY);
                video.setVideoURI(Uri.parse(
                        "android.resource://" + getPackageName() + "/" +
                                R.raw.instructivo_notificaciones));
                break;
            case PANTALLA_USUARIO:
                tituloPantalla.setText("Usuario");
                descripcionPantalla.setText("Descripción pantalla usuario. En el presente " +
                        "documento se propone un prototipo de sistema, el cual tiene como " +
                        "objetivo alertar y dar seguimiento en tiempo real cuando un usuario " +
                        "presente anomalías en su frecuencia cardiaca o niveles bajos de " +
                        "saturación de oxígeno en la sangre, para de este modo aumentar las " +
                        "probabilidades de que sea atendido prontamente evitando la muerte o daño" +
                        " cerebral.");
                circulo4.setColorFilter(Color.GRAY);
                video.setVideoURI(Uri.parse(
                        "android.resource://" + getPackageName() + "/" +
                                R.raw.instructivo_usuario));
                break;
            case PANTALLA_DATOS_USUARIO:
                tituloPantalla.setText("Datos Del Usuario");
                descripcionPantalla
                        .setText("Descripción pantalla datos del usuario. En el presente " +
                                "documento se propone un prototipo de sistema, el cual tiene como" +
                                " " +
                                "objetivo alertar y dar seguimiento en tiempo real cuando un " +
                                "usuario " +
                                "presente anomalías en su frecuencia cardiaca o niveles bajos de " +
                                "saturación de oxígeno en la sangre, para de este modo aumentar " +
                                "las " +
                                "probabilidades de que sea atendido prontamente evitando la " +
                                "muerte o daño" +
                                " cerebral.");
                circulo5.setColorFilter(Color.GRAY);
                video.setVideoURI(Uri.parse(
                        "android.resource://" + getPackageName() + "/" +
                                R.raw.instructivo_datos_usuario));
                break;
            case PANTALLA_RESPALDO:
                tituloPantalla.setText("Respaldo");
                descripcionPantalla.setText("Descripción pantalla respaldo. En el presente " +
                        "documento se propone un prototipo de sistema, el cual tiene como " +
                        "objetivo alertar y dar seguimiento en tiempo real cuando un usuario " +
                        "presente anomalías en su frecuencia cardiaca o niveles bajos de " +
                        "saturación de oxígeno en la sangre, para de este modo aumentar las " +
                        "probabilidades de que sea atendido prontamente evitando la muerte o daño" +
                        " cerebral.");
                circulo6.setColorFilter(Color.GRAY);
                video.setVideoURI(Uri.parse(
                        "android.resource://" + getPackageName() + "/" +
                                R.raw.instructivo_respaldo));
                break;
            case PANTALLA_MEDICIONES_TIEMPO_REAL:
                tituloPantalla.setText("Mediciones En Tiempo Real");
                descripcionPantalla
                        .setText("Descripción pantalla mediciones en tiempo real. En el presente " +
                                "documento se propone un prototipo de sistema, el cual tiene como" +
                                " " +
                                "objetivo alertar y dar seguimiento en tiempo real cuando un " +
                                "usuario " +
                                "presente anomalías en su frecuencia cardiaca o niveles bajos de " +
                                "saturación de oxígeno en la sangre, para de este modo aumentar " +
                                "las " +
                                "probabilidades de que sea atendido prontamente evitando la " +
                                "muerte o daño" +
                                " cerebral.");
                circulo7.setColorFilter(Color.GRAY);
                video.setVideoURI(Uri.parse(
                        "android.resource://" + getPackageName() + "/" +
                                R.raw.instructivo_mediciones_tiempo_real));
                break;
            case PANTALLA_HISTORIAL_MEDICIONES:
                tituloPantalla.setText("Historial de Mediciones");
                descripcionPantalla
                        .setText("Descripción pantalla historial de mediciones. En el presente " +
                                "documento se propone un prototipo de sistema, el cual tiene como" +
                                " " +
                                "objetivo alertar y dar seguimiento en tiempo real cuando un " +
                                "usuario " +
                                "presente anomalías en su frecuencia cardiaca o niveles bajos de " +
                                "saturación de oxígeno en la sangre, para de este modo aumentar " +
                                "las " +
                                "probabilidades de que sea atendido prontamente evitando la " +
                                "muerte o daño" +
                                " cerebral.");
                circulo8.setColorFilter(Color.GRAY);
                video.setVideoURI(Uri.parse(
                        "android.resource://" + getPackageName() + "/" +
                                R.raw.instructivo_historial_mediciones));
                break;
            case PANTALLA_INSTRUCTIVO:
            default:
                tituloPantalla.setText("Pantalla Instructivo");
                descripcionPantalla.setText("Descripción pantalla instructivo. En el presente " +
                        "documento se propone un prototipo de sistema, el cual tiene como " +
                        "objetivo alertar y dar seguimiento en tiempo real cuando un usuario " +
                        "presente anomalías en su frecuencia cardiaca o niveles bajos de " +
                        "saturación de oxígeno en la sangre, para de este modo aumentar las " +
                        "probabilidades de que sea atendido prontamente evitando la muerte o daño" +
                        " cerebral.");
                circulo1.setColorFilter(Color.GRAY);
                video.setVideoURI(Uri.parse(
                        "android.resource://" + getPackageName() + "/" +
                                R.raw.instructivo_general));
                break;
        }
        video.seekTo(1);
        video.pause();
    }
}
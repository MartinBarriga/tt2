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
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
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
                descripcionPantalla.setText(
                        "Podr??s agregar contactos a partir de la agenda personal en tu tel??fono, " +
                                "as?? como editar su nombre y seleccionar si deseas que se env??en " +
                                "notificaciones o mensajes. Adem??s de poder seleccionar si " +
                                "quieres enviar y recibir notificaciones de usuarios cercanos a " +
                                "ti.");
                circulo2.setColorFilter(Color.GRAY);
                video.setVideoURI(Uri.parse(
                        "android.resource://" + getPackageName() + "/" +
                                R.raw.instructivo_contactos));
                break;
            case PANTALLA_NOTIFICACIONES:
                tituloPantalla.setText("Notificaciones");
                descripcionPantalla.setText(
                        "Se muestra la colecci??n de notificaciones tanto tuyas como las recibidas" +
                                " de otros usuarios, tanto activas como terminadas. Puedes entrar" +
                                " a ver m??s informaci??n extra sobre cada emergencia as?? como " +
                                "quitarla de tu colecci??n");
                circulo3.setColorFilter(Color.GRAY);
                video.setVideoURI(Uri.parse(
                        "android.resource://" + getPackageName() + "/" +
                                R.raw.instructivo_notificaciones));
                break;
            case PANTALLA_USUARIO:
                tituloPantalla.setText("Usuario");
                descripcionPantalla.setText(
                        "Desde la pantalla usuario puedes seleccionar entre entrar a los datos " +
                                "medicos para hacer modificaciones, visualizar el instructivo de " +
                                "la aplicaci??n, crear un respaldo y cerrar sesi??n");
                circulo4.setColorFilter(Color.GRAY);
                video.setVideoURI(Uri.parse(
                        "android.resource://" + getPackageName() + "/" +
                                R.raw.instructivo_usuario));
                break;
            case PANTALLA_DATOS_USUARIO:
                tituloPantalla.setText("Datos Del Usuario");
                descripcionPantalla
                        .setText(
                                "Podr??s actualizar tu informaci??n personal como nombre, n??mero de" +
                                        " tel??fono y datos m??dicos relevantes para una emergencia" +
                                        " as?? como tus rangos estables en tu frecuencia card??aca");
                circulo5.setColorFilter(Color.GRAY);
                video.setVideoURI(Uri.parse(
                        "android.resource://" + getPackageName() + "/" +
                                R.raw.instructivo_datos_usuario));
                break;
            case PANTALLA_RESPALDO:
                tituloPantalla.setText("Respaldo");
                descripcionPantalla.setText(
                        "Tienes la opci??n de elegir entre hacer un respaldo de manera manual, al " +
                                "instante, o de programar un respaldo para que se ejecute cada " +
                                "cierto tiempo. Si la aplicaci??n llega a ser borrada, al volverla" +
                                " a instalar se descargar?? la informaci??n del respaldo.");
                circulo6.setColorFilter(Color.GRAY);
                video.setVideoURI(Uri.parse(
                        "android.resource://" + getPackageName() + "/" +
                                R.raw.instructivo_respaldo));
                break;
            case PANTALLA_MEDICIONES_TIEMPO_REAL:
                tituloPantalla.setText("Mediciones En Tiempo Real");
                descripcionPantalla
                        .setText(
                                "Desde esta pantalla se podr?? conectar al circuito para obtener " +
                                        "tus mediciones y podr??s ver ??stas en tiempo real, " +
                                        "cambiando entre ecg y frecuencia card??aca al presionar " +
                                        "su respectivo texto. Tambi??n podr??s acceder a historial " +
                                        "de mediciones desde esta pantalla.");
                circulo7.setColorFilter(Color.GRAY);
                video.setVideoURI(Uri.parse(
                        "android.resource://" + getPackageName() + "/" +
                                R.raw.instructivo_mediciones_tiempo_real));
                break;
            case PANTALLA_HISTORIAL_MEDICIONES:
                tituloPantalla.setText("Historial de Mediciones");
                descripcionPantalla
                        .setText(
                                "Seleccionando el d??a y hora en la que quieres ver tus mediciones" +
                                        " podr??s obtener todos los datos obtenidos en ese rango");
                circulo8.setColorFilter(Color.GRAY);
                video.setVideoURI(Uri.parse(
                        "android.resource://" + getPackageName() + "/" +
                                R.raw.instructivo_historial_mediciones));
                break;
            case PANTALLA_INSTRUCTIVO:
            default:
                tituloPantalla.setText("Pantalla Instructivo");
                descripcionPantalla.setText(
                        "A continuaci??n podr??s ver una breve explicaci??n de cada una de las " +
                                "pantallas que se encuentran en la aplicaci??n, desliza de " +
                                "izquierda a derecha para visualizarlas.");
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
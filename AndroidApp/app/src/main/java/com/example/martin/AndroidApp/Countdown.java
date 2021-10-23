package com.example.martin.AndroidApp;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Countdown extends AppCompatActivity {

    CountDownTimer cdt;
    private String location;
    private ManejadorBaseDeDatosLocal mManejadorBaseDeDatosLocal;
    private ManejadorBaseDeDatosNube mManejadorBaseDeDatosNube;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);

        mManejadorBaseDeDatosLocal = new ManejadorBaseDeDatosLocal(getApplicationContext(), null);
        mManejadorBaseDeDatosNube = new ManejadorBaseDeDatosNube();

        cdt = new CountDownTimer(11000, 1000) {
            TextView text = findViewById(R.id.segundos);

            public void onTick(long millisUntilFinished) {
                text.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                Toast.makeText(getApplicationContext(), "Enviando alertas...", Toast.LENGTH_LONG)
                        .show();

                String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                        Calendar.getInstance().getTime());
                String idUsuario = mManejadorBaseDeDatosNube.obtenerIdUsuario();
                String idEmergencia = (idUsuario+" "+fecha).replace(" ", "_");
                obtenerLocalizacion();
                //timer para que de chance de obtener la localización sin problemas
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        HiloParaEnviarEmergencias hiloParaEnviarEmergencias = new HiloParaEnviarEmergencias(idEmergencia, idUsuario, fecha, location, mManejadorBaseDeDatosLocal, mManejadorBaseDeDatosNube);
                        hiloParaEnviarEmergencias.start();
                        try {
                            hiloParaEnviarEmergencias.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, 5000);
            }
        };
        cdt.start();
    }

    public void saltarCountDown(View view) {
        cdt.cancel();
        Toast.makeText(getApplicationContext(), "Enviando alertas...", Toast.LENGTH_LONG).show();

        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
                Calendar.getInstance().getTime());
        String idUsuario = mManejadorBaseDeDatosNube.obtenerIdUsuario();
        String idEmergencia = (idUsuario+" "+fecha).replace(" ", "_");
        obtenerLocalizacion();
        //timer para que de chance de obtener la localización sin problemas
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                HiloParaEnviarEmergencias hiloParaEnviarEmergencias = new HiloParaEnviarEmergencias(idEmergencia, idUsuario, fecha, location, mManejadorBaseDeDatosLocal, mManejadorBaseDeDatosNube);
                hiloParaEnviarEmergencias.start();
                try {
                    hiloParaEnviarEmergencias.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 5000);
    }

    public void cancelarAlerta(View view) {
        cdt.cancel();
        System.exit(0);
    }

    public void agregarNotificacionPropia(String idUsuario, String idEmergencia, String fecha){
        Notificacion notificacion = new Notificacion(null,idUsuario,idEmergencia,"Te encuentras en una emergencia", 0, fecha, false, true, false);
        notificacion.setIdNotificacion(mManejadorBaseDeDatosLocal.agregarNotificacion(mManejadorBaseDeDatosLocal.generarFormatoDeNotificacionParaIntroducirEnBD(notificacion)));
        notificarAlUsuario(notificacion);
    }

    public void enviarNotificacion(String idEmergencia, String idUsuario, String fecha,
                                   String localizacion, ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal, ManejadorBaseDeDatosNube manejadorBaseDeDatosNube) {
        try {
            if(manejadorBaseDeDatosNube.crearEmergencia(idEmergencia, fecha, localizacion,
                    manejadorBaseDeDatosLocal.obtenerCantidadDeContactos(idUsuario)))
                Log.d("LOG", "Ya se peude notificar.");
            JSONObject datosDelUsuario = manejadorBaseDeDatosLocal
                    .obtenerDatosDelUsuarioEnFormatoJsonParaEnvioDeNotificaciones(
                           idUsuario , idEmergencia, fecha);
            RequestQueue myrequest = Volley.newRequestQueue(getApplicationContext());
            String URL = "https://fcm.googleapis.com/fcm/send";

            JsonObjectRequest request =
                    new JsonObjectRequest(Request.Method.POST, URL, datosDelUsuario, null,
                            null) {
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> header = new HashMap<>();

                            header.put("content-type", "application/json");
                            header.put("authorization",
                                    "key=AAAA3NKU1ZY:APA91bFGJAcm3kPvzQftNXIir4fzQj9jjo9Li-PXZ70JJOxNJAL" +
                                            "9xfK-IiXhez0_TxsginhawfnMfa9FwfVBD4ULwEzX88bvjCRk_Yed2KRvprMhwZ" +
                                            "UUuBQY4tvlZ8txWE1ir5XTWfi2");
                            return header;
                        }
                    };

            myrequest.add(request);

            Log.d("LOG", "Request añadida.");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void enviarSMS(View view, Context context, String idEmergencia, String localizacion, ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal, ManejadorBaseDeDatosNube manejadorBaseDeDatosNube) {
        ArrayList<Pair<String, String>> mensajesYNumerosDeTelefonos =
                manejadorBaseDeDatosLocal
                        .obtenerMensajeYNumerosDeTelefonosParaEnvioDeSMS(
                                manejadorBaseDeDatosNube.obtenerIdUsuario(),
                                localizacion, idEmergencia);
        for (Pair<String, String> mensajeYTelefono : mensajesYNumerosDeTelefonos) {
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> mensajeEnPartes = null;
            try {
                mensajeEnPartes = sms.divideMessage(mensajeYTelefono.first);
            } catch (Exception e) {
                Log.d("LOG", "Excepcion:" + e);
            }
            for (String msj : mensajeEnPartes ) {
                Log.d("LOG", "Mensaje: " + msj);
            }
            sms.sendMultipartTextMessage(mensajeYTelefono.second, null, mensajeEnPartes, null, null);
            Log.d("LOG", "Se envió un mensaje a: " + mensajeYTelefono.second);
        }

    }

    public void notificarAlUsuario(Notificacion notificacion){

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("nuevaAlerta", true);
        intent.putExtra("titulo", notificacion.getTitulo());
        intent.putExtra("idEmergencia", notificacion.getIdEmergencia());
        intent.putExtra("idNotificacion", notificacion.getIdNotificacion());
        intent.putExtra("fecha", notificacion.getFecha());
        intent.putExtra("esPropia", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "Alerta")
                        .setSmallIcon(R.drawable.ic_warning_black_24dp)
                        .setContentTitle(notificacion.getTitulo())
                        .setContentText("Haz click aquí para ver el seguimiento de tu emergencia.")
                        .setStyle(new NotificationCompat.BigTextStyle())
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setSound(alarmSound, AudioManager.STREAM_NOTIFICATION)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    private void obtenerLocalizacion() {
        location = "";
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(30000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(getApplicationContext())
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(getApplicationContext())
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            double latitude = locationResult.getLocations().get(latestLocationIndex)
                                    .getLatitude();
                            double longitude =
                                    locationResult.getLocations().get(latestLocationIndex)
                                            .getLongitude();
                            location =Double.toString(longitude) + "," +Double.toString(latitude);
                        }
                    }
                }, Looper.getMainLooper());
    }

    class HiloParaEnviarEmergencias extends Thread{
        private String idEmergencia;
        private String idUsuario;
        private String fecha;
        private String localizacion;
        private ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal;
        private ManejadorBaseDeDatosNube manejadorBaseDeDatosNube;

        HiloParaEnviarEmergencias(String idEmergencia, String idUsuario, String fecha, String localizacion,
                                  ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal,
                                  ManejadorBaseDeDatosNube manejadorBaseDeDatosNube){
            this.idEmergencia = idEmergencia;
            this.idUsuario = idUsuario;
            this.fecha = fecha;
            this.localizacion = localizacion;
            this.manejadorBaseDeDatosLocal = manejadorBaseDeDatosLocal;
            this.manejadorBaseDeDatosNube = manejadorBaseDeDatosNube;
        }

        @Override
        public void run() {
            enviarNotificacion(idEmergencia, idUsuario, fecha, localizacion, manejadorBaseDeDatosLocal, manejadorBaseDeDatosNube);
            enviarSMS(getCurrentFocus(), getApplicationContext(), idEmergencia, localizacion, manejadorBaseDeDatosLocal, manejadorBaseDeDatosNube);
            agregarNotificacionPropia(idUsuario,idEmergencia,fecha);
            System.exit(0);
        }
    }
}

package com.example.martin.AndroidApp;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class ServicioParaActualizarDatosEnLaEmergencia extends Service{
    private boolean TERMINADA = false;
    private FirebaseFirestore BaseDeDatos;
    private String idEmergencia;
    private BroadcastReceiver actualizacionesEnConexion;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;

    public boolean tieneConexionAInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);

        return connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public ServicioParaActualizarDatosEnLaEmergencia() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate (){
        Log.d("LOG", "ServicioParaActualizarDatosEnLaEmergencia creado");
        BaseDeDatos = FirebaseFirestore.getInstance();
    }

    public int onStartCommand (Intent intent,
                               int flags,
                               int startId){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), "Servicio")
                        .setSmallIcon(R.drawable.ic_warning_black_24dp)
                        .setContentTitle("Servicio en ejecución")
                        .setContentText("Tu localización y signos vitales se están actualizando.")
                        .setStyle(new NotificationCompat.BigTextStyle())
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        startForeground(2, builder.build());

        idEmergencia = intent.getStringExtra("idEmergencia");

        //Revisar si ya terminó para cerrar el hilo
        BaseDeDatos.collection("emergencias").document(idEmergencia).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                TERMINADA = (boolean) documentSnapshot.get("terminada");

                                if (TERMINADA){
                                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    stopForeground(true);
                                    stopSelf();
                                }
                            } else {
                                Log.d("LOG", "No se encontró el documento");
                            }
                        }
                        Log.d("LOG", "Task complete");
                    }
                });

        BaseDeDatos.collection("emergencias").document(idEmergencia).
                addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                        @Nullable FirebaseFirestoreException error) {
                        if ( documentSnapshot != null ){
                            if (documentSnapshot.exists()){
                                TERMINADA = (boolean) documentSnapshot.get("terminada");

                                if (TERMINADA){
                                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    stopForeground(true);
                                    stopSelf();
                                }
                            } else {
                                Log.d("LOG" ,
                                        "ServicioParaActualizarDatosEnLaEmergencia: " +
                                                "No se encontró la emergencia.");
                            }
                        }
                    }
                });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d("LOG", "onLocationResult");
                if (locationResult == null) {
                    return;
                }

                int latestLocationIndex = locationResult.getLocations().size() - 1;
                double latitude = locationResult.getLocations().get(latestLocationIndex)
                        .getLatitude();
                double longitude =
                        locationResult.getLocations().get(latestLocationIndex)
                                .getLongitude();
                if (tieneConexionAInternet( getApplicationContext() )){
                    Log.d("LOG", "Se va a subir la localizacion");
                    Map<String, Object> mLocalizacion = new HashMap<>();
                    mLocalizacion.put("longitud", Double.toString(longitude));
                    mLocalizacion.put("latitud", Double.toString(latitude));

                    BaseDeDatos.collection("emergencias").document(idEmergencia)
                            .collection("localizacion").document(idEmergencia)
                            .set(mLocalizacion).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("LOG", "Localizacion actualizada");
                        }}).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("LOG", "Error al actualizar localizacion: ", e);
                        }
                    });
                }
            }
        };

        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocalBroadcastManager.getInstance( getApplicationContext() ).registerReceiver(actualizacionesEnConexion,
                new IntentFilter("INTENT_MENSAJE"));

        if (ActivityCompat.checkSelfPermission( getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission( getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission( getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return START_STICKY_COMPATIBILITY;
        } else {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
                    getApplicationContext() );
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback,
                    Looper.getMainLooper());
        }

        actualizacionesEnConexion = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                String mensajeSucio = intent.getStringExtra("MENSAJE");
                Long tiempo = intent.getLongExtra("TIEMPO", 0);

                //El mensaje viene en forma de un string con corchetes y comas, asi que lo vamos
                // a limpiar dejando solo el string identico a como se mando del arduino
                String mensaje = "";
                for (int i = 0; i < mensajeSucio.length(); i++) {
                    if (mensajeSucio.charAt(i) >= '0' && mensajeSucio.charAt(i) <= '9') {
                        mensaje += mensajeSucio.charAt(i);
                    }
                }
                if (mensaje.length() == 12) {
                    int valorSpo2 =
                            (mensaje.charAt(4) - '0') * 100 + (mensaje.charAt(5) - '0') * 10 +
                                    (mensaje.charAt(6) - '0');
                    int valorCardiaco =
                            (mensaje.charAt(7) - '0') * 100 + (mensaje.charAt(8) - '0') * 10 +
                                    (mensaje.charAt(9) - '0');

                    if (tieneConexionAInternet( getApplicationContext() )){
                        Map<String, Object> mLocalizacion = new HashMap<>();
                        mLocalizacion.put("frecuencia", valorCardiaco);
                        mLocalizacion.put("niveloxigeno", valorSpo2);

                        BaseDeDatos.collection("emergencias").document(idEmergencia)
                                .collection("signosvitales").document(idEmergencia)
                                .set(mLocalizacion).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("LOG", "Signos vitales actualizados");
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("LOG", "Error al actualizar signos vitales: ", e);
                                    }
                                });
                    }
                }
            }
        };

        return START_STICKY;
    }
}

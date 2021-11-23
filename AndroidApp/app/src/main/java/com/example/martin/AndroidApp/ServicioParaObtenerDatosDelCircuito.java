package com.example.martin.AndroidApp;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class ServicioParaObtenerDatosDelCircuito extends Service{
    private boolean TERMINADA = false;
    private FirebaseFirestore BaseDeDatos;
    private String idEmergencia;
    private BroadcastReceiver actualizacionesEnConexion;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;

    public ServicioParaObtenerDatosDelCircuito() {
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
                        .setContentTitle("Dispositivo conectado")
                        .setContentText("Tu localización y signos vitales se están actualizando.")
                        .setStyle(new NotificationCompat.BigTextStyle())
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        startForeground(3, builder.build());


        return START_STICKY;
    }
}

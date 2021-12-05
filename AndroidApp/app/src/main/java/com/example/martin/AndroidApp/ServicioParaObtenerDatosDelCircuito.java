package com.example.martin.AndroidApp;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Binder;
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
    private boolean enEmergencia;
    private String idEmergencia;
    private Context context;
    private HiloParaActualizarLocalizacionEnEmergencia hiloParaActualizarDatosEnEmergencia;
    private HiloParaGuardarMediciones hiloParaGuardarMediciones;
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        ServicioParaObtenerDatosDelCircuito getService() {
            // Return this instance of LocalService so clients can call public methods
            return ServicioParaObtenerDatosDelCircuito.this;
        }
    }
    private boolean tieneConexionAInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);

        return connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private class HiloParaGuardarMediciones extends  Thread{
        private long ultimaVezQueActualizoSignosVitales;
        private long ultimaVezQueSePicaronLosBotones;
        private Context context;
        private int valorCardiaco;
        private int valorSpo2;
        private BroadcastReceiver actualizacionesEnConexion;
        private int frecuenciaCardiacaMinima;
        private int frecuenciaCardiacaMaxima;
        private FirebaseFirestore BaseDeDatos;
        private Usuario usuario;
        private ManejadorBaseDeDatosLocal mManejadorBaseDeDatosLocal;
        private Map<String, Object> signosVitales;
        private int valorBoton1;
        private int valorBoton2;

        HiloParaGuardarMediciones( Context context , ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal ,
                                   ManejadorBaseDeDatosNube manejadorBaseDeDatosNube ){
            this.ultimaVezQueActualizoSignosVitales = System.currentTimeMillis();
            ultimaVezQueSePicaronLosBotones = System.currentTimeMillis();
            this.context = context;
            this.BaseDeDatos = FirebaseFirestore.getInstance();
            this.mManejadorBaseDeDatosLocal = manejadorBaseDeDatosLocal;
            this.usuario = manejadorBaseDeDatosLocal.obtenerUsuario(
                    manejadorBaseDeDatosNube.obtenerIdUsuario());
            this.signosVitales = new HashMap<>();

            this.actualizacionesEnConexion = new BroadcastReceiver() {

                @Override
                public void onReceive(Context arg0, Intent intent) {
                    String mensajeSucio = intent.getStringExtra("MENSAJE");

                    //El mensaje viene en forma de un string con corchetes y comas, asi que lo vamos
                    // a limpiar dejando solo el string identico a como se mando del arduino
                    String mensaje = "";
                    for (int i = 0; i < mensajeSucio.length(); i++) {
                        if (mensajeSucio.charAt(i) >= '0' && mensajeSucio.charAt(i) <= '9') {
                            mensaje += mensajeSucio.charAt(i);
                        }
                    }

                    if (mensaje.length() == 12) {
                        int valorECG =
                                (mensaje.charAt(0) - '0') * 1000 + (mensaje.charAt(1) - '0') * 100 +
                                        (mensaje.charAt(2) - '0') * 10 + (mensaje.charAt(3) - '0');
                        valorSpo2 =
                                (mensaje.charAt(4) - '0') * 100 + (mensaje.charAt(5) - '0') * 10 +
                                        (mensaje.charAt(6) - '0');
                        valorCardiaco =
                                (mensaje.charAt(7) - '0') * 100 + (mensaje.charAt(8) - '0') * 10 +
                                        (mensaje.charAt(9) - '0');
                        valorBoton1 = (mensaje.charAt(10) - '0');
                        valorBoton2 = (mensaje.charAt(11) - '0');

                        //Revisar si los valores son normales
                        if ( !enEmergencia && (valorCardiaco > frecuenciaCardiacaMaxima ||
                                valorCardiaco < frecuenciaCardiacaMinima || valorSpo2 < 94)){
                            enEmergencia = true;
                            Intent intentCountDown = new Intent(context, Countdown.class);
                            intentCountDown.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intentCountDown);
                        }

                        if(valorBoton1 == 1 && valorBoton2 == 1 && System.currentTimeMillis() - ultimaVezQueSePicaronLosBotones > 1000) {
                            ultimaVezQueSePicaronLosBotones = System.currentTimeMillis();
                            Intent intentCountDown = new Intent(context, Countdown.class);
                            intentCountDown.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intentCountDown);
                        }

                        mManejadorBaseDeDatosLocal.agregarDatosAMedicion(valorECG, valorCardiaco, valorSpo2,
                                        System.currentTimeMillis(),
                                        usuario.getIdUsuario());

                        if ( enEmergencia && idEmergencia != null && (System.currentTimeMillis() -
                                ultimaVezQueActualizoSignosVitales) > 5000 &&
                                tieneConexionAInternet(getApplicationContext())) {
                            ultimaVezQueActualizoSignosVitales = System.currentTimeMillis();

                            signosVitales.put("frecuencia", valorCardiaco);
                            signosVitales.put("niveloxigeno", valorSpo2);

                            BaseDeDatos.collection("emergencias").document(idEmergencia)
                                    .collection("signosvitales").document(idEmergencia)
                                    .set(signosVitales).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        }

        public void run(){

            Log.d( "LOG", "HiloParaGuardarMediciones");

            frecuenciaCardiacaMaxima = usuario.getFrecuenciaCardiacaMaxima() > 0 ?
                    usuario.getFrecuenciaCardiacaMaxima() : 100;
            frecuenciaCardiacaMinima = usuario.getFrecuenciaCardiacaMinima() > 0 ?
                    usuario.getFrecuenciaCardiacaMinima() : 60;

            LocalBroadcastManager.getInstance(context).registerReceiver(
                    actualizacionesEnConexion,
                    new IntentFilter("INTENT_MENSAJE"));
        }
    }

    private class HiloParaActualizarLocalizacionEnEmergencia extends  Thread{
        private LocationCallback locationCallback;
        private LocationRequest locationRequest;
        private FusedLocationProviderClient fusedLocationProviderClient;
        private FirebaseFirestore BaseDeDatos;

        HiloParaActualizarLocalizacionEnEmergencia(){
            this.BaseDeDatos = FirebaseFirestore.getInstance();


            this.locationCallback = new LocationCallback() {
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
                    if (tieneConexionAInternet( context )){
                        Log.d("LOG", "Se van a subir los datos");

                        //Subir localización
                        Map<String, Object> mLocalizacion = new HashMap<>();
                        mLocalizacion.put("longitud", Double.toString(longitude));
                        mLocalizacion.put("latitud", Double.toString(latitude));

                        BaseDeDatos.collection("emergencias").document(idEmergencia)
                                .collection("localizacion").document(idEmergencia)
                                .set(mLocalizacion, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
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

            this.locationRequest = new LocationRequest();
            this.locationRequest.setInterval(10000);
            this.locationRequest.setFastestInterval(5000);
            this.locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            if (ActivityCompat.checkSelfPermission( context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED && ActivityCompat
                    .checkSelfPermission( context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED && ActivityCompat
                    .checkSelfPermission( context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
                        context );
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback,
                        Looper.getMainLooper());
            }
        }

        public void run(){

            //Revisar si ya terminó para cerrar el hilo
            BaseDeDatos.collection("emergencias").document(idEmergencia).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {
                                    boolean terminada = (boolean) documentSnapshot.get("terminada");

                                    if (terminada){
                                        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                        enEmergencia = false;
                                        idEmergencia = null;
                                        Log.d("LOG", "Emergencia terminada");
                                        return;
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
                                    boolean terminada = (boolean) documentSnapshot.get("terminada");

                                    if (terminada){
                                        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                        enEmergencia = false;
                                        idEmergencia = null;
                                        Log.d("LOG", "Emergencia terminada");
                                        return;
                                    }
                                } else {
                                    Log.d("LOG" ,
                                            "ServicioParaActualizarDatosEnLaEmergencia: " +
                                                    "No se encontró la emergencia.");
                                }
                            }
                        }
                    });

        }
    }

    public ServicioParaObtenerDatosDelCircuito() {
    }

    private void actualizarDatosEnEmergencia(){
        hiloParaActualizarDatosEnEmergencia = new HiloParaActualizarLocalizacionEnEmergencia();
        hiloParaActualizarDatosEnEmergencia.start();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    //Métodos para el cliente (Countdown)

    protected void activarEmergencia(String idEmergencia){
        enEmergencia = true;
        this.idEmergencia = idEmergencia;
        actualizarDatosEnEmergencia();
    }

    protected void desactivarEmergencia(){
        enEmergencia = false;
        this.idEmergencia = null;
    }

    public void onCreate (){
        Log.d("LOG", "ServicioParaObtenerDatosDelCircuito creado");
        context = getApplicationContext();
        enEmergencia = false;
        hiloParaGuardarMediciones = new HiloParaGuardarMediciones( getApplicationContext() ,
                new ManejadorBaseDeDatosLocal(getApplicationContext(), null) ,
                new ManejadorBaseDeDatosNube());
    }

    public int onStartCommand (Intent intent,
                               int flags,
                               int startId){

        Log.d("LOG", "onStartCommand");
        hiloParaGuardarMediciones.start();

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), "Servicio")
                        .setSmallIcon(R.drawable.ic_ipn)
                        .setContentTitle("TT 2020-B065")
                        .setContentText("La aplicación etá ejecutando en segundo plano.")
                        .setStyle(new NotificationCompat.BigTextStyle())
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        startForeground(3, builder.build());

        return START_STICKY;
    }
}

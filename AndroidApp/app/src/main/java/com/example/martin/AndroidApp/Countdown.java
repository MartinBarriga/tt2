package com.example.martin.AndroidApp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Countdown extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    CountDownTimer cdt;
    private String location;
    private ManejadorBaseDeDatosLocal mManejadorBaseDeDatosLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mManejadorBaseDeDatosLocal = new ManejadorBaseDeDatosLocal(getApplicationContext(), null);

        cdt = new CountDownTimer(11000, 1000) {
            TextView text = findViewById(R.id.segundos);
            public void onTick(long millisUntilFinished) {
                text.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                Toast.makeText(getApplicationContext(), "Enviando alertas...", Toast.LENGTH_LONG)
                        .show();
                sendNotification();
                sendSMS(getCurrentFocus(), getApplicationContext());
            }
        };
        cdt.start();
    }

    public void skipCountdown(View view) {
        cdt.cancel();
        Toast.makeText(getApplicationContext(), "Enviando alertas...", Toast.LENGTH_LONG).show();
        sendNotification();
        sendSMS(getCurrentFocus(), getApplicationContext());
    }

    public void cancelAlert(View view) {
        cdt.cancel();
        System.exit(0);
    }

    public void sendNotification() {
        getLocation();
        //timer para que de chance de obtener la localización sin problemas
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject datosDelUsuario = mManejadorBaseDeDatosLocal
                            .obtenerDatosDelUsuarioEnFormatoJsonParaEnvioDeNotificaciones(
                                    user.getUid(), location);
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
                                            "key=AAAAOPf1nos" +
                                                    ":APA91bHaLkuq7MtUFj7DOqr5Mwg9PTMAaydunHq" +
                                                    "-p6394Z6q3uE_vdtKbtkPkD2ee1Q8BBfZpmIsVNYpBpy52oR6sfAOaREegqb4CGRt04Wr-MDf7WEK8hFiYMYwD0k5F4fDH8Ld5pQ_");
                                    return header;
                                }
                            };

                    myrequest.add(request);
                    Log.d("LOG", "Request añadida.");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, 5000);
    }

    public void sendSMS(View view, Context context) {

        getLocation();
        //timer para que de chance de obtener la localización sin problemas
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Pair<String, ArrayList<String>> mensajeYNumerosDeTelefonos =
                        mManejadorBaseDeDatosLocal
                                .obtenerMensajeYNumerosDeTelefonosParaEnvioDeSMS(user.getUid(),
                                        location);
                for (String telefono : mensajeYNumerosDeTelefonos.second) {
                    SmsManager sms = SmsManager.getDefault();
                    ArrayList<String> mensajeEnPartes = null;
                    try {
                        mensajeEnPartes = sms.divideMessage(mensajeYNumerosDeTelefonos.first);
                    } catch (Exception e) {
                        Log.d("LOG", "Excepcion:" + e);
                    }
                    sms.sendMultipartTextMessage(telefono, null, mensajeEnPartes, null, null);
                    (new Handler()).postDelayed(new Runnable() {
                        public void run() {
                            System.exit(0);
                        }
                    }, 5000);
                    Log.d("LOG", "Se envió un mensaje a: " + telefono);
                }

            }
        }, 5000);
    }

    private void getLocation() {
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
                            location =
                                    "\nMi ubicación es: https://www.google.com/maps/search/?api=1&query=" +
                                            Double.toString(latitude) + "," +
                                            Double.toString(longitude);
                        }
                    }
                }, Looper.getMainLooper());
    }
}

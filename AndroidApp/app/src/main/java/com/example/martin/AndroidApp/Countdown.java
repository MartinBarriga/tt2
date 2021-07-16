package com.example.martin.AndroidApp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

import androidx.appcompat.app.AppCompatActivity;

public class Countdown extends AppCompatActivity {

    private String location;
    private ConexionSQLiteHelper mConectionSQLiteHelper;
    FirebaseAuth mAuth;
    FirebaseUser user;

    CountDownTimer cdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mConectionSQLiteHelper = new ConexionSQLiteHelper(getApplicationContext(), "lifeguard", null, 2);

        cdt = new CountDownTimer(11000, 1000) {
            TextView text = findViewById(R.id.segundos);
            public void onTick(long millisUntilFinished) {
                text.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                Toast.makeText(getApplicationContext(), "Enviando alertas...",Toast.LENGTH_LONG).show();
                sendNotification();
                sendSMS(getCurrentFocus(), getApplicationContext());
            }
        };
        cdt.start();
    }

    public void skipCountdown(View view){
        cdt.cancel();
        Toast.makeText(getApplicationContext(), "Enviando alertas...",Toast.LENGTH_LONG).show();
        sendNotification();
        sendSMS(getCurrentFocus(), getApplicationContext());
    }

    public void cancelAlert(View view){
        cdt.cancel();
        System.exit(0);
    }

    public void sendNotification(){
        getLocation();
        //timer para que de chance de obtener la localización sin problemas
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String mensaje = "";
                String name = "";
                SQLiteDatabase readingDatabase = mConectionSQLiteHelper.getReadableDatabase();
                Cursor cursor = readingDatabase.rawQuery("SELECT * FROM usuario WHERE Uid = ? ", new String[]{user.getUid()});
                while(cursor.moveToNext()){
                    mensaje = cursor.getString(5)+"\n"+location+"\n\nNombre: " + cursor.getString(1)
                            +"\nEdad: " + cursor.getInt(4)+"\nNúmero de seguridad Social: "+cursor.getLong(6)
                            +"\nMedicación: "+cursor.getString(7)+"\nEnfermedades crónicas: "+cursor.getString(8)
                            +"\nToxicomanías: "+cursor.getString(9) +"\nTipo de sangre: "+cursor.getString(10)
                            +"\nAlergias: "+cursor.getString(11)+"\nReligión: "+cursor.getString(12);

                    name = cursor.getString(1);
                }
                Log.d("LOG", "Mensaje: "+ mensaje);

                cursor = readingDatabase.rawQuery("SELECT * FROM contact WHERE userID = ? AND isNotificationSelected = ?", new String[]{user.getUid(), "1"});
                String phone;
                String condition = "";
                while(cursor.moveToNext()){

                    if (cursor.getPosition()>0) condition += " || ";

                    phone = cursor.getString(1).replaceAll(" ", "");
                    condition += "'"+phone+"' in topics";
                }
                Log.d("LOG", "Condition: "+ condition);

                RequestQueue myrequest = Volley.newRequestQueue(getApplicationContext());
                JSONObject json = new JSONObject();

                try {
                    //Para enviarte una notificación a ti mismo cambia la siguiente línea por json.put("to", "/topics/tuPropioNúmeroA10Dígitos");
                    json.put("condition", condition);
                    JSONObject notification = new JSONObject();
                    notification.put("nombre", name);
                    notification.put("mensaje", mensaje);

                    json.put("data", notification);

                    String URL = "https://fcm.googleapis.com/fcm/send";

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, json, null, null){
                        @Override
                        public Map<String, String> getHeaders(){
                            Map<String, String> header = new HashMap<>();

                            header.put("content-type", "application/json");
                            header.put("authorization", "key=AAAAOPf1nos:APA91bHaLkuq7MtUFj7DOqr5Mwg9PTMAaydunHq-p6394Z6q3uE_vdtKbtkPkD2ee1Q8BBfZpmIsVNYpBpy52oR6sfAOaREegqb4CGRt04Wr-MDf7WEK8hFiYMYwD0k5F4fDH8Ld5pQ_");
                            return header;
                        }
                    };

                    myrequest.add(request);
                    Log.d("LOG", "Request añadida.");
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, 5000);
    }

    public void sendSMS(View view, Context context){

        getLocation();
        //timer para que de chance de obtener la localización sin problemas
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String mensaje = "";
                SQLiteDatabase readingDatabase = mConectionSQLiteHelper.getReadableDatabase();
                Cursor cursor = readingDatabase.rawQuery("SELECT * FROM usuario WHERE Uid = ? ", new String[]{user.getUid()});
                while(cursor.moveToNext()){
                    mensaje = cursor.getString(5)+"\n"+location+"\n\nNombre: " + cursor.getString(1)
                            +"\nEdad: " + cursor.getInt(4)+"\nNúmero de seguridad Social: "+cursor.getLong(6)
                            +"\nMedicación: "+cursor.getString(7)+"\nEnfermedades crónicas: "+cursor.getString(8)
                            +"\nToxicomanías: "+cursor.getString(9) +"\nTipo de sangre: "+cursor.getString(10)
                            +"\nAlergias: "+cursor.getString(11)+"\nReligión: "+cursor.getString(12);
                }
                Log.d("LOG", "Mensaje: "+ mensaje);

                cursor = readingDatabase.rawQuery("SELECT * FROM contact WHERE userID = ? AND isMessageSelected = ?", new String[]{user.getUid(), "1"});
                String phone;
                while(cursor.moveToNext()){
                    phone = cursor.getString(1).replaceAll(" ", "");
                    Log.d("LOG", "Se envió un mensaje a: "+phone);
                    SmsManager sms=SmsManager.getDefault();
                    ArrayList<String> parts=null;
                    try{
                        parts = sms.divideMessage(mensaje);
                    }catch (Exception e){
                        Log.d("LOG", "Excepcion:"+e);
                    }

                    sms.sendMultipartTextMessage(phone, null, parts, null, null);
                    (new Handler()).postDelayed(new Runnable() {
                        public void run() {
                            System.exit(0);
                        }
                    }, 5000);
                }

            }
        }, 5000);
    }

    private void getLocation() {
        location =  "";
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(30000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.getFusedLocationProviderClient(getApplicationContext()).requestLocationUpdates(locationRequest, new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(getApplicationContext()).removeLocationUpdates(this);
                if(locationResult != null && locationResult.getLocations().size() > 0){
                    int latestLocationIndex = locationResult.getLocations().size() - 1;
                    double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                    double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                    location = "\nMi ubicación es: https://www.google.com/maps/search/?api=1&query="+ Double.toString( latitude) + "," + Double.toString(longitude);
                }
            }
        }, Looper.getMainLooper());
    }
}

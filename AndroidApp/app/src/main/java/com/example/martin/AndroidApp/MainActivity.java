package com.example.martin.AndroidApp;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.martin.AndroidApp.ui.mediciones.Hilo;
import com.example.martin.AndroidApp.ui.mediciones.DispositivosVinculados;
import com.example.martin.AndroidApp.ui.usuario.Respaldo;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    // Identificador unico de servicio - SPP UUID
    private static final UUID BTMODULEUUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static int handlerState = 0;
    public static Handler bluetoothIn;
    public BluetoothAdapter bluetoothAdapter;
    public Hilo MyConexionBT;
    NavController navController;
    private BluetoothSocket btSocket = null;
    private ManejadorBaseDeDatosNube mManejadorBaseDeDatosNube;

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String address = intent.getStringExtra(DispositivosVinculados.DIRECCION_DEL_DISPOSITIVO);
        if (address != null) {
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
            intent.removeExtra(DispositivosVinculados.DIRECCION_DEL_DISPOSITIVO);
            try {
                btSocket = createBluetoothSocket(device);
            } catch (IOException e) {
                //No se pudo crear el socket
                Toast.makeText(getApplicationContext(), "No se pudo crear el socket",
                        Toast.LENGTH_LONG).show();

            }
            // Establece la conexi??n con el socket Bluetooth.
            try {
                btSocket.connect();
                MyConexionBT = new Hilo(btSocket, bluetoothIn);
                MyConexionBT.start();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "No se pudo conectar", Toast.LENGTH_LONG)
                        .show();
                try {
                    btSocket.close();
                } catch (IOException e2) {
                }
            }
            //Toast.makeText(getApplicationContext(), "Conexion establecida", Toast.LENGTH_LONG)
            // .show();
            navController.navigate(R.id.navigation_visualizacion_datos_medidos);
        }
    }

    private boolean servicioEnEjecucion(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //getSupportFragmentManager()
        //        .findFragmentById(R.id.navigation_visualizacion_datos_medidos);
        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {

                    //Interacci??n con los datos de ingreso
                    String mensaje = (String) msg.obj;
                    /*long current = now.getEpochSecond();
                    if(current != lastSecond) {
                        lastSecond = current;
                        values = 0;
                    }
                    values++;*/

                    Long tiempo = System.nanoTime();
                    Intent intentMensaje = new Intent("INTENT_MENSAJE");
                    intentMensaje.putExtra("MENSAJE", mensaje);
                    intentMensaje.putExtra("TIEMPO", tiempo);
                    LocalBroadcastManager.getInstance(getApplicationContext())
                            .sendBroadcast(intentMensaje);
                }
            }
        };

        if ( !servicioEnEjecucion(ServicioParaObtenerDatosDelCircuito.class) ) {
            Intent intent = new Intent(getApplicationContext(), ServicioParaObtenerDatosDelCircuito.class);
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            cw.startService(intent);
        }

        mManejadorBaseDeDatosNube = new ManejadorBaseDeDatosNube();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_contacts, R.id.navigation_dashboard, R.id.navigation_usuario)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        Bundle bundle = getIntent().getExtras();
        if (bundle.getBoolean("firstLaunch")) {
            Toast.makeText(getApplicationContext(), "Aqu?? puede completar o editar sus datos.",
                    Toast.LENGTH_LONG).show();
            navController.navigate(R.id.navigation_usuario);
        }
        if (bundle.getBoolean("nuevaAlerta")) {
            Toast.makeText(getApplicationContext(), "Nueva alerta", Toast.LENGTH_LONG).show();
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            ManejadorBaseDeDatosLocal mConnectionSQLiteHelper =
                    new ManejadorBaseDeDatosLocal(MainActivity.this, null);

            SQLiteDatabase escritura = mConnectionSQLiteHelper.getWritableDatabase();

            Log.d("LOG",
                    "Table update: " + "idNotificacion = " +
                            String.valueOf(bundle.getLong("idNotificacion")) +
                            " AND idUsuario LIKE '" +
                            mManejadorBaseDeDatosNube.obtenerIdUsuario() + "'");
            Boolean esPropia = bundle.getBoolean("esPropia");
            Notificacion notificacion =
                    new Notificacion(bundle.getLong("idNotificacion"),
                            mManejadorBaseDeDatosNube.obtenerIdUsuario(), bundle.getString("idEmergencia"),
                            bundle.getString("titulo"), 0, bundle.getString("fecha"), true,
                            esPropia,
                            false);

            int r = escritura.update("notificacion", mConnectionSQLiteHelper
                            .generarFormatoDeNotificacionParaIntroducirEnBD(notificacion),
                    "idNotificacion = ? AND idUsuario LIKE '" + notificacion.getIdUsuario() + "'",
                    new String[]{String.valueOf(notificacion.getIdNotificacion())});
            escritura.close();
            navController.navigate(R.id.navigation_dashboard, bundle);
        }

    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        //crea un conexion de salida segura para el dispositivo usando el servicio UUID
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }
}

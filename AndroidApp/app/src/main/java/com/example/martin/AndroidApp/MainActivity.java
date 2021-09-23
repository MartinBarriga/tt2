package com.example.martin.AndroidApp;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.martin.AndroidApp.ui.VisualizacionDatosMedidos.ConnectedThread;
import com.example.martin.AndroidApp.ui.VisualizacionDatosMedidos.DispositivosVinculados;
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
    public ConnectedThread MyConexionBT;
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
            // Establece la conexión con el socket Bluetooth.
            try {
                btSocket.connect();
                MyConexionBT = new ConnectedThread(btSocket, bluetoothIn);
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

                    //Interacción con los datos de ingreso
                    String mensaje = (String) msg.obj;
                    /*long current = now.getEpochSecond();
                    if(current != lastSecond) {
                        lastSecond = current;
                        values = 0;
                    }
                    values++;*/

                    Long tiempo = System.nanoTime();
                    System.out.println("Mensaje: " + mensaje + " " + tiempo);
                    Intent intentMensaje = new Intent("INTENT_MENSAJE");
                    intentMensaje.putExtra("MENSAJE", mensaje);
                    intentMensaje.putExtra("TIEMPO", tiempo);
                    LocalBroadcastManager.getInstance(getApplicationContext())
                            .sendBroadcast(intentMensaje);
                }
            }
        };

        mManejadorBaseDeDatosNube = new ManejadorBaseDeDatosNube();

        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_contacts, R.id.navigation_dashboard, R.id.navigation_usuario)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //startService(new Intent(this, WearListenerService.class));

        Bundle bundle = getIntent().getExtras();
        if (bundle.getBoolean("firstLaunch")) {
            Toast.makeText(getApplicationContext(), "Aquí puede completar o editar sus datos.",
                    Toast.LENGTH_LONG).show();
            navController.navigate(R.id.navigation_usuario);
        }
        if (bundle.getBoolean("nuevaAlerta")) {
            Toast.makeText(getApplicationContext(), "Nueva alerta", Toast.LENGTH_LONG).show();
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            ManejadorBaseDeDatosLocal mConnectionSQLiteHelper =
                    new ManejadorBaseDeDatosLocal(MainActivity.this, null);
            ;
            SQLiteDatabase writingDatabase = mConnectionSQLiteHelper.getWritableDatabase();

            Log.d("LOG",
                    "Table update: " + "id = " + bundle.getString("idS") + " AND userID LIKE '" +
                            bundle.getString("userID") + "'");
            NotificationInfo notificacion =
                    new NotificationInfo(bundle.getLong("id"), bundle.getString("fecha"),
                            bundle.getString("nombre"), bundle.getString("mensaje"), true,
                            bundle.getString("userID"));

            mManejadorBaseDeDatosNube.actualizarNotificacion(notificacion);

            int r = writingDatabase.update("notificacion", getContactValue(notificacion),
                    "id = ? AND userID LIKE '" + bundle.getString("userID") + "'",
                    new String[]{bundle.getString("idS")});

            alertDialog.setTitle("Nueva alerta de " + bundle.getString("nombre"));
            final SpannableString s = new SpannableString(bundle.getString("mensaje"));
            Linkify.addLinks(s, Linkify.WEB_URLS);
            alertDialog.setMessage(s);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            writingDatabase.close();
            ((TextView) alertDialog.findViewById(android.R.id.message))
                    .setMovementMethod(LinkMovementMethod.getInstance());
        }

    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        //crea un conexion de salida segura para el dispositivo usando el servicio UUID
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    public ContentValues getContactValue(NotificationInfo notification) {
        ContentValues contactValues = new ContentValues();
        contactValues.put("fecha", notification.getFecha());
        contactValues.put("nombre", notification.getNombre());
        contactValues.put("mensaje", notification.getMensaje());

        if (notification.getLeido()) {
            contactValues.put("leido", 1);
        } else {
            contactValues.put("leido", 0);
        }
        contactValues.put("userID", notification.getUserID());
        return contactValues;
    }

}

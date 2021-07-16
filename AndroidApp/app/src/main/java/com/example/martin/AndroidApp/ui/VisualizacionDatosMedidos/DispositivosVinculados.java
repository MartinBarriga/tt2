package com.example.martin.AndroidApp.ui.VisualizacionDatosMedidos;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.martin.AndroidApp.MainActivity;
import com.example.martin.AndroidApp.R;

import java.util.Set;

public class DispositivosVinculados extends AppCompatActivity {

    // Declaracion de ListView
    ListView IdLista;
    // String que se enviara a la actividad principal, mainactivity
    public static String DIRECCION_DEL_DISPOSITIVO = "direccion_dispositivo";

    // Declaracion de campos
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter mPairedDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispositivos_vinculados);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // Inicializa la array que contendra la lista de los dispositivos bluetooth vinculados
        mPairedDevicesArrayAdapter = new ArrayAdapter(this, R.layout.dispositivos);
        // Presenta los dispositivos vinculados en el ListView
        IdLista = (ListView) findViewById(R.id.listViewDispositivos);
        IdLista.setAdapter(mPairedDevicesArrayAdapter);
        IdLista.setOnItemClickListener(mDeviceClickListener);
        // Obtiene el adaptador local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        // Adiciona un dispositivos emparejado al array
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());

        }
        //---------------------------------------------------------------------
    }

    // Configura un (on-click) para la lista
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {

            // Obtener la dirección MAC del dispositivo, que son los últimos 17 caracteres en la vista
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            finishAffinity();

            // Realiza un intent para iniciar la siguiente actividad
            // mientras toma un EXTRA_DEVICE_ADDRESS que es la dirección MAC.
            Intent intent = new Intent(DispositivosVinculados.this, MainActivity.class);
            intent.putExtra(DIRECCION_DEL_DISPOSITIVO, address);
            startActivity(intent);
            /*
            Bundle bundle = new Bundle();
            bundle.putString(DIRECCION_DEL_DISPOSITIVO, address);
            // set Fragmentclass Arguments
            VisualizacionDatosMedidosFragment visualizacionDatosMedidosFragment = new VisualizacionDatosMedidosFragment();
            visualizacionDatosMedidosFragment.setArguments(bundle);

             */
        }
    };
}
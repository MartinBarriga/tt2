package com.example.martin.AndroidApp.ui.VisualizacionDatosMedidos;

import static android.app.Activity.RESULT_OK;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.martin.AndroidApp.MainActivity;
import com.example.martin.AndroidApp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;


public class VisualizacionDatosMedidosFragment extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final int REQUEST_ENABLE_BT = 1; //Mientras sea un valor mayor a 0, la constante pedirá que se habilite el bluetooth
    public MainActivity mainActivity;
    private Button botonBluetooth;
    private BroadcastReceiver connectionUpdates;
    private LineChart grafica;

    private void conectarDispositivo() {
        botonBluetooth.setVisibility(View.INVISIBLE);

        if (mainActivity.bluetoothAdapter == null) {
            // TODO: Imprimir que el dispositivo no soporta bluetooth
        }
        else {

            if (!mainActivity.bluetoothAdapter.isEnabled()) {
                //Pedir prender el bluetooth
                botonBluetooth.setText(R.string.textoEncenderBluetooth);
                botonBluetooth.setVisibility(View.VISIBLE);
                botonBluetooth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                });
            }
            else {
                //Preguntar si ya estamos conectados al dispositivo y el hilo está corriendo
                if(mainActivity.MyConexionBT != null) {
                    botonBluetooth.setText("Desconectar");
                    botonBluetooth.setVisibility(View.VISIBLE);
                    botonBluetooth.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mainActivity.bluetoothAdapter.disable();
                            mainActivity.MyConexionBT = null;
                            botonBluetooth.setText(R.string.textoEncenderBluetooth);
                            botonBluetooth.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                                }
                            });
                        }
                    });
                    if(mainActivity.MyConexionBT.isAlive()) {
                        Toast.makeText(getActivity(), "Fragment detecta hilo abierto", Toast.LENGTH_LONG).show();


                    }
                }
                else {
                    Toast.makeText(getActivity(), "Fragment detecta hilo cerrado", Toast.LENGTH_LONG).show();
                    //Pedir conectarse al dispositivo
                    botonBluetooth.setText(R.string.textoVincularSensor);
                    botonBluetooth.setVisibility(View.VISIBLE);
                    botonBluetooth.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!mainActivity.bluetoothAdapter.isEnabled()) {
                                conectarDispositivo();
                            } else {
                                Intent vinculacionDeDispositivoIntent = new Intent(getActivity(), DispositivosVinculados.class);
                                startActivity(vinculacionDeDispositivoIntent);
                            }
                        }
                    });
                }

            }
        }

    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.MAGENTA);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }
    int  n = 50;
    @Override
    public void onResume(){
        super.onResume();
        connectionUpdates = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String mensaje = intent.getStringExtra("MENSAJE");
                Long tiempo = intent.getLongExtra("TIEMPO", 0);
                LineData informacion = grafica.getData();
                int valorECG = (mensaje.charAt(4) -'0') * 1000 + (mensaje.charAt(7)-'0') * 100 + (mensaje.charAt(10) -'0') * 10 + (mensaje.charAt(13) -'0');
                n++;
                if(informacion != null) {
                    LineDataSet set = (LineDataSet) informacion.getDataSetByIndex(0);
                    if(set == null) {
                        set = createSet();
                        informacion.addDataSet(set);
                    }
                    informacion.addEntry(new Entry(set.getEntryCount(), valorECG), 0);
                    informacion.notifyDataChanged();
                    grafica.setMaxVisibleValueCount(100);

                    grafica.notifyDataSetChanged();
                    grafica.moveViewToX(informacion.getEntryCount());
                    System.out.println("NULL");
                }
                System.out.println("Mensaje2: " + mensaje + " " + tiempo + " " + valorECG);
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                connectionUpdates ,
                new IntentFilter("INTENT_MENSAJE"));

    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(connectionUpdates);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onStart() {
        super.onStart();
        conectarDispositivo();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_visualizacion_datos_medidos, container, false);
        botonBluetooth = root.findViewById(R.id.botonBluetooth);
        grafica = (LineChart) root.findViewById(R.id.graficaECG);
        grafica.getDescription().setEnabled(true);
        grafica.getDescription().setText("Valores ECG en tiempo real");
        grafica.setTouchEnabled(true);
        grafica.setDragEnabled(true);
        grafica.setScaleEnabled(false);
        grafica.setDrawGridBackground(false);
        grafica.setPinchZoom(false);
        grafica.setBackgroundColor(Color.WHITE);

        LineData informacion = new LineData();
        informacion.setValueTextColor(Color.WHITE);
        grafica.setData(informacion);
        /*// get the legend (only possible after setting data)
        Legend l = grafica.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = grafica.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = grafica.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum(10f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = grafica.getAxisRight();
        rightAxis.setEnabled(false);*/

        grafica.getAxisLeft().setDrawGridLines(false);
        grafica.getXAxis().setDrawGridLines(false);
        grafica.setDrawBorders(false);
        mainActivity = (MainActivity) getActivity();

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    conectarDispositivo();
                }
                break;
        }
    }
}
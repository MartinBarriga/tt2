package com.example.martin.AndroidApp.ui.mediciones;

import static android.app.Activity.RESULT_OK;

import android.app.PendingIntent;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.martin.AndroidApp.Countdown;
import com.example.martin.AndroidApp.Instructivo;
import com.example.martin.AndroidApp.MainActivity;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosLocal;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosNube;
import com.example.martin.AndroidApp.R;
import com.example.martin.AndroidApp.ui.usuario.Respaldo;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class VisualizacionDatosMedidosFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final int REQUEST_ENABLE_BT = 1;
    private final int indiceSetParaECG = 1;
    private final int indiceSetParaFrecuenciaCardiaca = 2;
    private final int indiceSetParaSpo2 = 3;
    //Mientras sea un valor mayor a 0, la constante pedirá que se habilite el bluetooth
    public MainActivity mainActivity;

    private Button botonBluetooth;
    private BroadcastReceiver actualizacionesEnConexion;
    private LineChart graficaECG;
    private LineChart graficaFrecuenciaCardiacaSpo2;
    private TextView textViewValorECG;
    private TextView textViewValorCardiaco;
    private TextView textViewValorSpo2;
    private CardView verHistorialMediciones;
    private Boolean seEstaReproduciendoGraficaECG;
    private ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal;
    private ManejadorBaseDeDatosNube manejadorBaseDeDatosNube;
    private Long ultimaVezQueSePicaronLosBotones;

    private void conectarDispositivo() {
        botonBluetooth.setVisibility(View.INVISIBLE);

        if (mainActivity.bluetoothAdapter == null) {
            // TODO: Imprimir que el dispositivo no soporta bluetooth
        } else {

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
            } else {
                //Preguntar si ya estamos conectados al dispositivo y el hilo está corriendo
                if (mainActivity.MyConexionBT != null) {
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
                                    Intent enableBtIntent =
                                            new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                                }
                            });
                        }
                    });
                    if (mainActivity.MyConexionBT.isAlive()) {
                        Toast.makeText(getActivity(), "Fragment detecta hilo abierto",
                                Toast.LENGTH_LONG).show();


                    }
                } else {
                    Toast.makeText(getActivity(), "Fragment detecta hilo cerrado",
                            Toast.LENGTH_LONG).show();
                    //Pedir conectarse al dispositivo
                    botonBluetooth.setText(R.string.textoVincularSensor);
                    botonBluetooth.setVisibility(View.VISIBLE);
                    botonBluetooth.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!mainActivity.bluetoothAdapter.isEnabled()) {
                                conectarDispositivo();
                            } else {
                                Intent vinculacionDeDispositivoIntent =
                                        new Intent(getActivity(), DispositivosVinculados.class);
                                startActivity(vinculacionDeDispositivoIntent);
                            }
                        }
                    });
                }

            }
        }

    }

    private LineDataSet crearSetECG() {
        LineDataSet set = new LineDataSet(null, "Valores ECG");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.BLUE);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    private LineDataSet crearSetFrecuenciaCardiaca() {
        LineDataSet set = new LineDataSet(null, "Valores Cardiacos");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.RED);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    private LineDataSet crearSetSpo2() {
        LineDataSet set = new LineDataSet(null, "Valores Spo2");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.YELLOW);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }


    @Override
    public void onResume() {
        super.onResume();
        actualizacionesEnConexion = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String mensaje = intent.getStringExtra("MENSAJE");
                Long tiempo = intent.getLongExtra("TIEMPO", 0);


                int valorECG =
                        (mensaje.charAt(0) - '0') * 1000 + (mensaje.charAt(1) - '0') * 100 +
                                (mensaje.charAt(2) - '0') * 10 + (mensaje.charAt(3) - '0');
                int valorSpo2 =
                        (mensaje.charAt(4) - '0') * 100 + (mensaje.charAt(5) - '0') * 10 +
                                (mensaje.charAt(6) - '0');
                int valorCardiaco =
                        (mensaje.charAt(7) - '0') * 100 + (mensaje.charAt(8) - '0') * 10 +
                                (mensaje.charAt(9) - '0');

                textViewValorECG.setText("Valor ECG: " + Integer.toString(valorECG) + " mv");
                textViewValorCardiaco
                        .setText(
                                "Frecuencia Cardiaca: " + Integer.toString(valorCardiaco) +
                                        " ppm");
                textViewValorSpo2.setText("Spo2: " + Integer.toString(valorSpo2) + "%");
                /*manejadorBaseDeDatosLocal
                        .agregarDatosAMedicion(valorECG, valorCardiaco, valorSpo2,
                                System.currentTimeMillis(),
                                manejadorBaseDeDatosNube.obtenerIdUsuario());*/

                LineData informacionECG = graficaECG.getData();
                if (informacionECG != null) {
                    LineDataSet setECG =
                            (LineDataSet) informacionECG
                                    .getDataSetByIndex(indiceSetParaECG);
                    if (setECG == null) {
                        setECG = crearSetECG();
                        informacionECG.addDataSet(setECG);

                    }

                    informacionECG.addEntry(new Entry(tiempo, valorECG), indiceSetParaECG);
                    if (setECG.getEntryCount() > 200) {
                        setECG.removeFirst();
                    }
                    informacionECG.notifyDataChanged();
                    graficaECG.notifyDataSetChanged();
                    graficaECG.moveViewToX(informacionECG.getEntryCount());
                }

                LineData informacionCardiacaSpo2 = graficaFrecuenciaCardiacaSpo2.getData();
                if (informacionCardiacaSpo2 != null) {
                    LineDataSet setFrecuenciaCardiaca =
                            (LineDataSet) informacionCardiacaSpo2
                                    .getDataSetByIndex(indiceSetParaFrecuenciaCardiaca);
                    if (setFrecuenciaCardiaca == null) {
                        setFrecuenciaCardiaca = crearSetFrecuenciaCardiaca();
                        informacionCardiacaSpo2.addDataSet(setFrecuenciaCardiaca);
                    }

                    informacionCardiacaSpo2.addEntry(new Entry(tiempo, valorCardiaco),
                            indiceSetParaFrecuenciaCardiaca);
                    if (setFrecuenciaCardiaca.getEntryCount() > 200) {
                        setFrecuenciaCardiaca.removeFirst();
                    }

                    LineDataSet setSpo2 =
                            (LineDataSet) informacionCardiacaSpo2
                                    .getDataSetByIndex(indiceSetParaSpo2);
                    if (setSpo2 == null) {
                        setSpo2 = crearSetSpo2();
                        informacionCardiacaSpo2.addDataSet(setSpo2);
                    }

                    informacionCardiacaSpo2.addEntry(new Entry(tiempo, valorSpo2),
                            indiceSetParaSpo2);
                    if (setSpo2.getEntryCount() > 200) {
                        setSpo2.removeFirst();
                    }


                    informacionCardiacaSpo2.notifyDataChanged();
                    graficaFrecuenciaCardiacaSpo2.notifyDataSetChanged();
                    graficaFrecuenciaCardiacaSpo2
                            .moveViewToX(informacionCardiacaSpo2.getEntryCount());
                }
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                actualizacionesEnConexion,
                new IntentFilter("INTENT_MENSAJE"));

    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
                actualizacionesEnConexion);
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

    void mostrarGraficaECG() {
        seEstaReproduciendoGraficaECG = true;
        graficaECG.setVisibility(View.VISIBLE);
        graficaFrecuenciaCardiacaSpo2.setVisibility(View.INVISIBLE);
    }

    void mostrarGraficaRitmoCardiacoSpo2() {
        seEstaReproduciendoGraficaECG = false;
        graficaECG.setVisibility(View.INVISIBLE);
        graficaFrecuenciaCardiacaSpo2.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =
                inflater.inflate(R.layout.fragment_visualizacion_datos_medidos, container, false);
        botonBluetooth = root.findViewById(R.id.botonBluetooth);
        textViewValorECG = root.findViewById(R.id.valorECG);
        textViewValorCardiaco = root.findViewById(R.id.valorCardiaco);
        textViewValorSpo2 = root.findViewById(R.id.valorSpo2);
        verHistorialMediciones = root.findViewById(R.id.verMedicionesCardView);
        verHistorialMediciones.setCardBackgroundColor(Color.TRANSPARENT);
        verHistorialMediciones.setCardElevation(0);
        FloatingActionButton botonEmergencia = root.findViewById(R.id.boton_emergencia);
        ultimaVezQueSePicaronLosBotones = System.currentTimeMillis();
        botonEmergencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Countdown.class);
                startActivity(intent);
            }
        });
        FloatingActionButton botonInstructivo = root.findViewById(R.id.boton_instructivo_flotante);
        botonInstructivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Instructivo.class);
                intent.putExtra("pantalla", Instructivo.PANTALLA_MEDICIONES_TIEMPO_REAL);
                startActivity(intent);
            }
        });

        textViewValorECG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!seEstaReproduciendoGraficaECG) mostrarGraficaECG();
                Toast.makeText(getContext(), "PRESIONASTE ECG", Toast.LENGTH_SHORT).show();
            }
        });
        textViewValorCardiaco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (seEstaReproduciendoGraficaECG) mostrarGraficaRitmoCardiacoSpo2();
                Toast.makeText(getContext(), "PRESIONASTE RITMO CARDIACO", Toast.LENGTH_SHORT)
                        .show();
            }
        });
        textViewValorSpo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (seEstaReproduciendoGraficaECG) mostrarGraficaRitmoCardiacoSpo2();
                Toast.makeText(getContext(), "PRESIONASTE SPO2", Toast.LENGTH_SHORT).show();
            }
        });
        verHistorialMediciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), HistorialDeMediciones.class);
                intent.putExtra("idUsuario", manejadorBaseDeDatosNube.obtenerIdUsuario());
                startActivity(intent);
            }
        });

        graficaECG = (LineChart) root.findViewById(R.id.graficaECG);
        graficaECG.getDescription().setEnabled(true);
        graficaECG.getDescription().setText("Valores ECG en tiempo real");
        graficaECG.setTouchEnabled(false);
        graficaECG.setDragEnabled(false);
        graficaECG.setScaleEnabled(false);
        graficaECG.setDrawGridBackground(false);
        graficaECG.setPinchZoom(false);
        graficaECG.setBackgroundColor(Color.WHITE);
        LineData informacionECG = new LineData();
        informacionECG.setValueTextColor(Color.WHITE);
        graficaECG.setData(informacionECG);
        graficaECG.getAxisLeft().setDrawGridLines(false);
        graficaECG.getXAxis().setDrawGridLines(false);
        graficaECG.getXAxis().setTextColor(0);
        graficaECG.getAxisLeft().setAxisMaxValue(500);
        graficaECG.getAxisLeft().setAxisMinValue(300);
        graficaECG.getAxisRight().setAxisMaxValue(500);
        graficaECG.getAxisRight().setAxisMinValue(300);
        graficaECG.setDrawBorders(false);
        graficaECG.invalidate();

        graficaFrecuenciaCardiacaSpo2 =
                (LineChart) root.findViewById(R.id.graficaFrecuenciaCardiacaSpo2);
        graficaFrecuenciaCardiacaSpo2.getDescription().setEnabled(true);
        graficaFrecuenciaCardiacaSpo2.getDescription()
                .setText("Valores de Frecuencia cardiaca y Spo2 en tiempo real");
        graficaFrecuenciaCardiacaSpo2.setTouchEnabled(false);
        graficaFrecuenciaCardiacaSpo2.setDragEnabled(false);
        graficaFrecuenciaCardiacaSpo2.setScaleEnabled(false);
        graficaFrecuenciaCardiacaSpo2.setDrawGridBackground(false);
        graficaFrecuenciaCardiacaSpo2.setPinchZoom(false);
        graficaFrecuenciaCardiacaSpo2.setBackgroundColor(Color.WHITE);
        LineData informacionFrecuenciaSpo2 = new LineData();
        informacionFrecuenciaSpo2.setValueTextColor(Color.WHITE);
        graficaFrecuenciaCardiacaSpo2.setData(informacionFrecuenciaSpo2);
        graficaFrecuenciaCardiacaSpo2.getAxisLeft().setDrawGridLines(false);
        graficaFrecuenciaCardiacaSpo2.getXAxis().setDrawGridLines(false);
        graficaFrecuenciaCardiacaSpo2.getXAxis().setTextColor(0);
        graficaFrecuenciaCardiacaSpo2.getAxisLeft().setAxisMaxValue(100);
        graficaFrecuenciaCardiacaSpo2.getAxisLeft().setAxisMinValue(0);
        graficaFrecuenciaCardiacaSpo2.getAxisRight().setAxisMaxValue(100);
        graficaFrecuenciaCardiacaSpo2.getAxisRight().setAxisMinValue(0);
        graficaFrecuenciaCardiacaSpo2.setDrawBorders(false);
        graficaFrecuenciaCardiacaSpo2.invalidate();

        mostrarGraficaECG();
        manejadorBaseDeDatosLocal =
                new ManejadorBaseDeDatosLocal(getActivity().getApplicationContext(), null);
        manejadorBaseDeDatosNube = new ManejadorBaseDeDatosNube();
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
package com.example.martin.AndroidApp.ui.mediciones;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.martin.AndroidApp.Countdown;
import com.example.martin.AndroidApp.Dato;
import com.example.martin.AndroidApp.Instructivo;
import com.example.martin.AndroidApp.MainActivity;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosLocal;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosNube;
import com.example.martin.AndroidApp.Medicion;
import com.example.martin.AndroidApp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.renderer.DataRenderer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HistorialDeMediciones extends AppCompatActivity {


    private final int indiceSetParaECG = 1;
    private final int indiceSetParaFrecuenciaCardiaca = 2;
    private final int indiceSetParaSpo2 = 3;
    private LineChart graficaECG;
    private LineChart graficaFrecuenciaCardiacaSpo2;
    private ManejadorBaseDeDatosLocal manejadorBaseDeDatosLocal;

    private LineDataSet crearSetECG() {
        LineDataSet set = new LineDataSet(null, "Valores ECG");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(1f);
        set.setColor(Color.BLUE);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        set.setDrawCircles(false);
        return set;
    }

    private LineDataSet crearSetFrecuenciaCardiaca() {
        LineDataSet set = new LineDataSet(null, "Valores Cardiacos");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.RED);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        set.setDrawCircles(false);
        return set;
    }

    private LineDataSet crearSetSpo2() {
        LineDataSet set = new LineDataSet(null, "Valores Spo2");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.YELLOW);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        set.setDrawCircles(false);
        return set;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String idUsuario = getIntent().getExtras().getString("idUsuario");
        setContentView(R.layout.activity_historial_de_mediciones);
        TextView textViewFechaInicio = findViewById(R.id.textViewFechaInicio);
        TextView textViewHoraInicio = findViewById(R.id.textViewHoraInicio);
        TextView textViewHoraFin = findViewById(R.id.textViewHoraFin);
        Button botonBuscar = findViewById(R.id.botonBuscar);
        FloatingActionButton botonEmergencia = findViewById(R.id.boton_emergencia);
        Calendar calendario = Calendar.getInstance();
        final String[] fechaSeleccionada = {""};
        final String[] horaInicioSeleccionada = {""};
        final String[] horaFinSeleccionada = {""};


        manejadorBaseDeDatosLocal = new ManejadorBaseDeDatosLocal(getApplicationContext(), null);
        graficaECG = (LineChart) findViewById(R.id.graficaECGHistorial);
        graficaFrecuenciaCardiacaSpo2 =
                (LineChart) findViewById(R.id.graficaFrecuenciaCardiacaSpo2Historial);

        graficaECG.getDescription().setEnabled(true);
        graficaECG.getDescription().setText("Valores ECG en tiempo real");
        graficaECG.setTouchEnabled(true);
        graficaECG.setDragEnabled(true);
        graficaECG.setScaleEnabled(true);
        graficaECG.setScaleYEnabled(false);
        graficaECG.setDrawGridBackground(false);
        graficaECG.setPinchZoom(true);
        graficaECG.setBackgroundColor(Color.WHITE);
        LineData informacionECG = new LineData();
        informacionECG.setValueTextColor(Color.WHITE);
        graficaECG.setData(informacionECG);
        graficaECG.getAxisLeft().setDrawGridLines(false);
        graficaECG.getXAxis().setDrawGridLines(false);
        graficaECG.getAxisLeft().setAxisMaxValue(900);
        graficaECG.getAxisLeft().setAxisMinValue(200);
        graficaECG.getAxisRight().setAxisMaxValue(900);
        graficaECG.getAxisRight().setAxisMinValue(200);
        graficaECG.setDrawBorders(false);
        XAxis xAxisGraficaECG = graficaECG.getXAxis();
        xAxisGraficaECG.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisGraficaECG.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                SimpleDateFormat formatoParaHoras = new SimpleDateFormat("HH:mm:ss.SSS");
                return formatoParaHoras.format(new Date(new Float(value).longValue()));
            }
        });
        graficaECG.invalidate();


        graficaFrecuenciaCardiacaSpo2.getDescription().setEnabled(true);
        graficaFrecuenciaCardiacaSpo2.getDescription()
                .setText("Valores de Frecuencia cardiaca y Spo2 en tiempo real");
        graficaFrecuenciaCardiacaSpo2.setTouchEnabled(true);
        graficaFrecuenciaCardiacaSpo2.setDragEnabled(true);
        graficaFrecuenciaCardiacaSpo2.setScaleEnabled(true);
        graficaFrecuenciaCardiacaSpo2.setScaleYEnabled(false);
        graficaFrecuenciaCardiacaSpo2.setDrawGridBackground(false);
        graficaFrecuenciaCardiacaSpo2.setPinchZoom(true);
        graficaFrecuenciaCardiacaSpo2.setBackgroundColor(Color.WHITE);
        LineData informacionFrecuenciaSpo2 = new LineData();
        informacionFrecuenciaSpo2.setValueTextColor(Color.WHITE);
        graficaFrecuenciaCardiacaSpo2.setData(informacionFrecuenciaSpo2);
        graficaFrecuenciaCardiacaSpo2.getAxisLeft().setDrawGridLines(false);
        graficaFrecuenciaCardiacaSpo2.getXAxis().setDrawGridLines(false);
        graficaFrecuenciaCardiacaSpo2.getAxisLeft().setAxisMaxValue(200);
        graficaFrecuenciaCardiacaSpo2.getAxisLeft().setAxisMinValue(0);
        graficaFrecuenciaCardiacaSpo2.getAxisRight().setAxisMaxValue(200);
        graficaFrecuenciaCardiacaSpo2.getAxisRight().setAxisMinValue(0);
        graficaFrecuenciaCardiacaSpo2.setDrawBorders(false);
        XAxis xAxisFrecuenciaCardiacaSpo2 = graficaFrecuenciaCardiacaSpo2.getXAxis();
        xAxisFrecuenciaCardiacaSpo2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisFrecuenciaCardiacaSpo2.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                SimpleDateFormat formatoParaHoras = new SimpleDateFormat("HH:mm:ss.SSS");
                return formatoParaHoras.format(new Date(new Float(value).longValue()));
            }
        });
        graficaFrecuenciaCardiacaSpo2.invalidate();

        textViewFechaInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialogoFecha = new DatePickerDialog(HistorialDeMediciones.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int anio, int mes,
                                                  int dia) {
                                mes = mes + 1;
                                String fecha = anio + "/" + mes + "/" + dia;
                                try {
                                    Date fechaTipoDate =
                                            new SimpleDateFormat("yyyy/MM/dd").parse(fecha);
                                    fecha = new SimpleDateFormat("yyyy/MM/dd")
                                            .format(fechaTipoDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                fechaSeleccionada[0] = fecha;
                                textViewFechaInicio.setText("Fecha Inicio: " + fecha);
                            }
                        }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH));
                dialogoFecha.show();
            }
        });

        textViewHoraInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog dialogoHora = new TimePickerDialog(HistorialDeMediciones.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hora, int minuto) {
                                String horayMinuto = hora + ":" + minuto + ":00.000";
                                try {
                                    Date horayMinutoTipoDate =
                                            new SimpleDateFormat("HH:mm:ss.SSS").parse(horayMinuto);
                                    horayMinuto = new SimpleDateFormat("HH:mm:ss.SSS")
                                            .format(horayMinutoTipoDate);
                                    ;
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                horaInicioSeleccionada[0] = horayMinuto;
                                textViewHoraInicio.setText("Hora Inicio: " + horayMinuto);
                            }
                        }, 12, 0, false);
                dialogoHora.show();
            }
        });

        textViewHoraFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog dialogoHora = new TimePickerDialog(HistorialDeMediciones.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hora, int minuto) {
                                String horayMinuto = hora + ":" + minuto + ":00.000";
                                try {
                                    Date horayMinutoTipoDate =
                                            new SimpleDateFormat("HH:mm:ss.SSS").parse(horayMinuto);
                                    horayMinuto = new SimpleDateFormat("HH:mm:ss.SSS")
                                            .format(horayMinutoTipoDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                horaFinSeleccionada[0] = horayMinuto;
                                textViewHoraFin.setText("Hora Fin: " + horayMinuto);
                            }
                        }, 12, 0, false);
                dialogoHora.show();
            }
        });

        botonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fechaSeleccionada[0].length() == 0 || horaInicioSeleccionada[0].length() == 0 ||
                        horaFinSeleccionada[0]
                                .length() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Selecciona una fecha, así como una hora de inicio y fin",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(), "Cargando datos...", Toast.LENGTH_LONG).show();
                    ArrayList<Dato> datosMedidos = manejadorBaseDeDatosLocal
                            .obtenerDatosMedidosDeUnRangoEspecificado(idUsuario,
                                    fechaSeleccionada[0],
                                    horaInicioSeleccionada[0], horaFinSeleccionada[0],
                                    getApplicationContext());
                    if (datosMedidos.isEmpty()) {
                        Toast.makeText(getApplicationContext(),
                                "No se encontraron datos para el rango de tiempo seleccionado",
                                Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        llenarLasGraficasConLosDatosObtenidos(datosMedidos);
                    }

                }
            }
        });
        botonEmergencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Countdown.class);
                startActivity(intent);
            }
        });
        FloatingActionButton botonInstructivo = findViewById(R.id.boton_instructivo_flotante);
        botonInstructivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Instructivo.class);
                intent.putExtra("pantalla",Instructivo.PANTALLA_HISTORIAL_MEDICIONES);
                startActivity(intent);
            }
        });
    }

    private void llenarLasGraficasConLosDatosObtenidos(ArrayList<Dato> datosMedidos) {
        graficaECG.getData().clearValues();
        graficaFrecuenciaCardiacaSpo2.getData().clearValues();
        for (Dato dato : datosMedidos) {
            String horaSinFiltrar = dato.getHora();

            SimpleDateFormat formatoParaHoras = new SimpleDateFormat("HH:mm:ss.SSS");
            Date hora = null;
            try {
                hora = formatoParaHoras.parse(horaSinFiltrar);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            LineData informacionECG = graficaECG.getData();
            if (informacionECG != null) {
                LineDataSet setECG =
                        (LineDataSet) informacionECG
                                .getDataSetByIndex(indiceSetParaECG);
                if (setECG == null) {
                    setECG = crearSetECG();
                    informacionECG.addDataSet(setECG);
                }

                informacionECG.addEntry(new Entry(new Long(hora.getTime()).floatValue(), dato.getEcg()), indiceSetParaECG);

            }
            informacionECG.notifyDataChanged();

            LineData informacionCardiacaSpo2 = graficaFrecuenciaCardiacaSpo2.getData();
            if (informacionCardiacaSpo2 != null) {
                LineDataSet setFrecuenciaCardiaca =
                        (LineDataSet) informacionCardiacaSpo2
                                .getDataSetByIndex(indiceSetParaFrecuenciaCardiaca);
                if (setFrecuenciaCardiaca == null) {
                    setFrecuenciaCardiaca = crearSetFrecuenciaCardiaca();
                    informacionCardiacaSpo2.addDataSet(setFrecuenciaCardiaca);
                }
                informacionCardiacaSpo2.addEntry(new Entry(new Long(hora.getTime()).floatValue(), dato.getFrecuenciaCardiaca()),
                        indiceSetParaFrecuenciaCardiaca);

                LineDataSet setSpo2 =
                        (LineDataSet) informacionCardiacaSpo2
                                .getDataSetByIndex(indiceSetParaSpo2);
                if (setSpo2 == null) {
                    setSpo2 = crearSetSpo2();
                    informacionCardiacaSpo2.addDataSet(setSpo2);
                }
                informacionCardiacaSpo2.addEntry(new Entry(new Long(hora.getTime()).floatValue(), dato.getSpo2()),
                        indiceSetParaSpo2);


                informacionCardiacaSpo2.notifyDataChanged();

            }
        }

        graficaECG.notifyDataSetChanged();
        graficaECG.moveViewToX(1);
        graficaFrecuenciaCardiacaSpo2.notifyDataSetChanged();
        graficaFrecuenciaCardiacaSpo2
                .moveViewToX(1);
    }
}

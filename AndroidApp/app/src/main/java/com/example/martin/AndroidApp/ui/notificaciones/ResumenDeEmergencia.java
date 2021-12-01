package com.example.martin.AndroidApp.ui.notificaciones;

import androidx.appcompat.app.AppCompatActivity;

import com.example.martin.AndroidApp.Dato;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosLocal;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosNube;
import com.example.martin.AndroidApp.R;
import com.example.martin.AndroidApp.Resumen;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ResumenDeEmergencia extends AppCompatActivity {
    private ManejadorBaseDeDatosLocal mManejadorBaseDeDatosLocal;
    private ManejadorBaseDeDatosNube mManejadorBaseDeDatosNube;
    private final int indiceSetParaECG = 1;
    private final int indiceSetParaFrecuenciaCardiaca = 2;
    private final int indiceSetParaSpo2 = 3;
    private LineChart graficaECG;
    private LineChart graficaFrecuenciaCardiacaSpo2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_de_emergencia);

        mManejadorBaseDeDatosLocal = new ManejadorBaseDeDatosLocal(getApplicationContext(), null);
        mManejadorBaseDeDatosNube =  new ManejadorBaseDeDatosNube();
        Bundle bundle = getIntent().getExtras();
        Long idNotificacion = bundle.getLong("idNotificacion");
        String idEmergencia = bundle.getString("idEmergencia");
        int estado = bundle.getInt("estado");
        if (!mManejadorBaseDeDatosLocal.existeElResumen(idNotificacion, mManejadorBaseDeDatosNube.obtenerIdUsuario())){
            Toast.makeText(getApplicationContext(), "La emergencia fue terminada",
                    Toast.LENGTH_LONG).show();
            mManejadorBaseDeDatosNube.iniciarDescargaDeResumen(mManejadorBaseDeDatosLocal,idEmergencia,idNotificacion);
        }
        Resumen resumen = mManejadorBaseDeDatosLocal.obtenerResumen(idNotificacion, mManejadorBaseDeDatosNube.obtenerIdUsuario());
        TextView textNombreResumen = findViewById(R.id.nombreResumen);
        textNombreResumen.setText("Nombre: "+resumen.getNombre());
        TextView textCantidadDePersonasEnviadoResumen = findViewById(R.id.cantidadDePersonasEnviadoResumen);
        textCantidadDePersonasEnviadoResumen.setText("Se envió alerta a "+resumen.getCantidadDePersonasEnviado()+" personas");
        TextView textDuracionResumen = findViewById(R.id.duracionResumen);
        textDuracionResumen.setText(resumen.getDuracion());
        TextView textDesenlaceResumen = findViewById(R.id.desenlaceResumen);
        String desenlace = resumen.getDesenlace();
        textDesenlaceResumen.setText(desenlace);
        Log.d("LOG", "Detalle: "+resumen.getDetalles());
        if (desenlace.matches("Persona trasladada a un hospital")){
            TextView textDetalleResumenLabel = findViewById(R.id.detalleResumenLabel);
            textDetalleResumenLabel.setText("Nombre del hospital al que fue trasladado el afectado:");
            TextView textDetalleResumen = findViewById(R.id.detalleResumen);
            textDetalleResumen.setText(resumen.getDetalles());
        } else if (desenlace.matches("Persona atendida por un familiar")){
            TextView textDetalleResumenLabel = findViewById(R.id.detalleResumenLabel);
            textDetalleResumenLabel.setText("Nombre del familiar que atendió al afectado:");
            TextView textDetalleResumen = findViewById(R.id.detalleResumen);
            textDetalleResumen.setText(resumen.getDetalles());
        } else {
            TextView textDetalleResumenLabel = findViewById(R.id.detalleResumenLabel);
            textDetalleResumenLabel.setVisibility(View.GONE);
            TextView textDetalleResumen = findViewById(R.id.detalleResumen);
            textDetalleResumen.setVisibility(View.GONE);
        }
        TextView textComentariosResumen = findViewById(R.id.comentariosResumen);
        textComentariosResumen.setText(resumen.getComentario());

         graficaECG = (LineChart) findViewById(R.id.graficaECGEmergenciaFinalizada);
         graficaFrecuenciaCardiacaSpo2 =
                (LineChart) findViewById(R.id.graficaFrecuenciaCardiacaSpo2EmergenciaFinalizada);
        TextView fechaTextView = (TextView) findViewById(R.id.fechaEmergenciaFinalizada);
        TextView informacionEmergenciaTextView =
                (TextView) findViewById(R.id.informacionEmergenciaFinalizada);
        // Si la emergencia fue propia, mostramos graficas con los datos, sino ocultamos las gráficas
        if(estado > 1) {
            String fecha = resumen.getInicio().substring(0, resumen.getInicio().indexOf(' '));
            fecha = fecha.replace('-', '/');
            String horaInicio =
                    resumen.getInicio().substring(resumen.getInicio().indexOf(' ') + 1) + ".000";
            String horaFin =
                    resumen.getFin().substring(resumen.getFin().indexOf(' ') + 1) + ".000";

            fechaTextView.setText(fecha);
            if (estado == 2) {
                informacionEmergenciaTextView.setText(
                        "Detectamos una anomalía en tus mediciones\nEl sistema mandó las alertas " +
                                "correspondientes");
            } else if (estado == 3) {
                informacionEmergenciaTextView.setText(
                        "Detectamos una anomalía en tus mediciones\nEl envío de alertas fue " +
                                "cancelado manualmente");
            }
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
            graficaECG.getAxisLeft().setAxisMaxValue(500);
            graficaECG.getAxisLeft().setAxisMinValue(300);
            graficaECG.getAxisRight().setAxisMaxValue(500);
            graficaECG.getAxisRight().setAxisMinValue(300);
            graficaECG.setDrawBorders(false);
            XAxis xAxisGraficaECG = graficaECG.getXAxis();
            xAxisGraficaECG.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxisGraficaECG.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float horaFloat) {
                    String horaString = "";
                    int hora = (int) horaFloat;
                    String horaStringSinFormato = Integer.toString(hora);
                    while (horaStringSinFormato.length() < 9) {
                        horaStringSinFormato = "0" + horaStringSinFormato;
                    }
                    int decimalesmovidos = 0;
                    for (int i = horaStringSinFormato.length() - 1; i >= 0; i--) {
                        horaString = horaStringSinFormato.charAt(i) + horaString;
                        decimalesmovidos++;
                        if (decimalesmovidos == 3) {
                            horaString = "." + horaString;
                        } else if (decimalesmovidos == 5 || decimalesmovidos == 7) {
                            horaString = "." + horaString;
                        }
                    }
                    return horaString;
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
            graficaFrecuenciaCardiacaSpo2.getAxisLeft().setAxisMaxValue(100);
            graficaFrecuenciaCardiacaSpo2.getAxisLeft().setAxisMinValue(0);
            graficaFrecuenciaCardiacaSpo2.getAxisRight().setAxisMaxValue(100);
            graficaFrecuenciaCardiacaSpo2.getAxisRight().setAxisMinValue(0);
            graficaFrecuenciaCardiacaSpo2.setDrawBorders(false);
            XAxis xAxisFrecuenciaCardiacaSpo2 = graficaFrecuenciaCardiacaSpo2.getXAxis();
            xAxisFrecuenciaCardiacaSpo2.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxisFrecuenciaCardiacaSpo2.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float horaFloat) {
                    String horaString = "";
                    int hora = (int) horaFloat;
                    String horaStringSinFormato = Integer.toString(hora);
                    while (horaStringSinFormato.length() < 9) {
                        horaStringSinFormato = "0" + horaStringSinFormato;
                    }
                    int decimalesmovidos = 0;
                    for (int i = horaStringSinFormato.length() - 1; i >= 0; i--) {
                        horaString = horaStringSinFormato.charAt(i) + horaString;
                        decimalesmovidos++;
                        if (decimalesmovidos == 3) {
                            horaString = "." + horaString;
                        } else if (decimalesmovidos == 5 || decimalesmovidos == 7) {
                            horaString = "." + horaString;
                        }
                    }
                    return horaString;
                }
            });
            graficaFrecuenciaCardiacaSpo2.invalidate();

            ArrayList<Dato> datosMedidos = mManejadorBaseDeDatosLocal
                    .obtenerDatosMedidosDeUnRangoEspecificado(mManejadorBaseDeDatosNube.obtenerIdUsuario(),
                            fecha, horaInicio, horaFin, getApplicationContext());
            if (datosMedidos.isEmpty()) {
                Toast.makeText(getApplicationContext(),
                        "No se encontraron datos para el rango de tiempo seleccionado",
                        Toast.LENGTH_SHORT)
                        .show();
            } else {
                llenarLasGraficasConLosDatosObtenidos(datosMedidos);
            }
        } else {
            graficaECG.setVisibility(View.INVISIBLE);
            graficaFrecuenciaCardiacaSpo2.setVisibility(View.INVISIBLE);
            fechaTextView.setVisibility(View.INVISIBLE);
            informacionEmergenciaTextView.setVisibility(View.INVISIBLE);
        }

    }

    private void llenarLasGraficasConLosDatosObtenidos(ArrayList<Dato> datosMedidos) {
        for (Dato dato : datosMedidos) {
            String horaSinFiltrar = dato.getHora();
            String horaFiltrada = "";
            for (int i = 0; i < horaSinFiltrar.length(); i++) {
                if (horaSinFiltrar.charAt(i) >= '0' && horaSinFiltrar.charAt(i) <= '9') {
                    horaFiltrada += horaSinFiltrar.charAt(i);
                }
            }
            Long hora = Long.parseLong(horaFiltrada);

            LineData informacionECG = graficaECG.getData();
            if (informacionECG != null) {
                LineDataSet setECG =
                        (LineDataSet) informacionECG
                                .getDataSetByIndex(indiceSetParaECG);
                if (setECG == null) {
                    setECG = crearSetECG();
                    informacionECG.addDataSet(setECG);
                }

                informacionECG.addEntry(new Entry(hora, dato.getEcg()), indiceSetParaECG);

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
                informacionCardiacaSpo2.addEntry(new Entry(hora, dato.getFrecuenciaCardiaca()),
                        indiceSetParaFrecuenciaCardiaca);

                LineDataSet setSpo2 =
                        (LineDataSet) informacionCardiacaSpo2
                                .getDataSetByIndex(indiceSetParaSpo2);
                if (setSpo2 == null) {
                    setSpo2 = crearSetSpo2();
                    informacionCardiacaSpo2.addDataSet(setSpo2);
                }
                informacionCardiacaSpo2.addEntry(new Entry(hora, dato.getSpo2()),
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
}
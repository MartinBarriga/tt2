package com.example.martin.AndroidApp.ui.notificaciones;

import androidx.appcompat.app.AppCompatActivity;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosLocal;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosNube;
import com.example.martin.AndroidApp.R;
import com.example.martin.AndroidApp.Resumen;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class ResumenDeEmergencia extends AppCompatActivity {
    private ManejadorBaseDeDatosLocal mManejadorBaseDeDatosLocal;
    private ManejadorBaseDeDatosNube mManejadorBaseDeDatosNube;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_de_emergencia);

        mManejadorBaseDeDatosLocal = new ManejadorBaseDeDatosLocal(getApplicationContext(), null);
        mManejadorBaseDeDatosNube =  new ManejadorBaseDeDatosNube();
        Bundle bundle = getIntent().getExtras();
        Long idNotificacion = bundle.getLong("idNotificacion");
        String idEmergencia = bundle.getString("idEmergencia");
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

    }

}
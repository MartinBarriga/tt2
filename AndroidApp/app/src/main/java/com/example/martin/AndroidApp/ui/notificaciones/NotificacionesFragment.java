package com.example.martin.AndroidApp.ui.notificaciones;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.martin.AndroidApp.Countdown;
import com.example.martin.AndroidApp.Instructivo;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosLocal;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosNube;
import com.example.martin.AndroidApp.Notificacion;
import com.example.martin.AndroidApp.R;
import com.example.martin.AndroidApp.SeguimientoDeAlerta;
import com.example.martin.AndroidApp.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

/*PARA AGREGAR UNA NUEVA NOTIFICACION SE DEBE DE EJECUTAR LA SIGUIENTE LINEA:
 !!!!!!!!!!!!!!!!!!!!!!!!!!
 Y CAMBIAR LOS STRINGS QUE CONTIENEN LA PALABRA "OBTENIDA/OBTENIDO", NO MOVER EL NULL, EL FALSE Y
  EL USER.GETUID
 mNotificationsManager.addNewNotification(new Notificacion(null, "FECHA OBTENIDA", "NOMBRE
 OBTENIDO", "MENSAJE OBTENIDO", false, user.getUid()));
 !!!!!!!!!!!!!!!!!!!!!!!!
 !!!!!!!!!!!!!!!!!!!!!!!!
 */
public class NotificacionesFragment extends Fragment
        implements NotificacionesRecyclerAdapter.OnNotificacionListener {

    private static final int PERMISSION_SEND_SMS = 123;
    private final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private ManejadorBaseDeDatosLocal mManejadorBaseDeDatosLocal;
    private ManejadorBaseDeDatosNube mManejadorBaseDeDatosNube;
    private ManejadorNotificaciones mNotificacionesManager;
    private NotificacionesRecyclerAdapter mNotificacionesRecyclerAdapter;
    private View root;
    private ArrayList<Notificacion> mNotificaciones;
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
            new ItemTouchHelper.SimpleCallback(0,
                    ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView,
                                      @NonNull RecyclerView.ViewHolder viewHolder,
                                      @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    int posicion = viewHolder.getAdapterPosition();
                    mNotificacionesManager.eliminarNotificacion(posicion);
                    mNotificaciones = mNotificacionesManager.getArrayNotifications();
                    mNotificacionesRecyclerAdapter.notifyItemRemoved(posicion);

                }
            };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mManejadorBaseDeDatosNube = new ManejadorBaseDeDatosNube();
        mNotificacionesManager = new ManejadorNotificaciones(getContext());
        if (ContextCompat
                .checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED || ContextCompat
                .checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) !=
                PackageManager.PERMISSION_GRANTED || ContextCompat
                .checkSelfPermission(this.getContext(), Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED || ContextCompat
                .checkSelfPermission(this.getContext(), Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED ) {
            final AlertDialog.Builder mensajeDePermiso = new AlertDialog.Builder(getContext());
            mensajeDePermiso.setTitle("Permiso para enviar SMS.");
            mensajeDePermiso.setMessage(
                    "Debido a que la función de esta aplicación es enviar mensajes de emergencia," +
                            " a continuación se le pedirá permiso para enviar mensajes desde su " +
                            "celular y acceder a su localización.");
            mensajeDePermiso.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.SEND_SMS,
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.READ_PHONE_STATE}, PERMISSION_SEND_SMS);
                }
            });

            mensajeDePermiso.setNegativeButton(getString(R.string.newNameDialogCancelingButton),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // InputMethodManager imm = (InputMethodManager)getSystemService
                            // (Context.INPUT_METHOD_SERVICE);
                            //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            dialog.cancel();
                        }
                    });
            mensajeDePermiso.create().show();
        }

        if ( !Settings.canDrawOverlays(getContext()) ){
            final AlertDialog.Builder mensajeDePermiso = new AlertDialog.Builder(getContext());
            mensajeDePermiso.setTitle("Permiso para mostrar sobre otras aplicaciones.");
            mensajeDePermiso.setMessage(
                    "Adicionalmente te pedimos actives la función para mostrar sobre otras aplicaciones.");
            mensajeDePermiso.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    startActivity(myIntent);
                }
            });

            mensajeDePermiso.setNegativeButton(getString(R.string.newNameDialogCancelingButton),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // InputMethodManager imm = (InputMethodManager)getSystemService
                            // (Context.INPUT_METHOD_SERVICE);
                            //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            dialog.cancel();
                        }
                    });
            mensajeDePermiso.create().show();
        }

        mManejadorBaseDeDatosLocal = new ManejadorBaseDeDatosLocal(
                getActivity().getApplicationContext(), null);
        Usuario usuario = mManejadorBaseDeDatosLocal
                .obtenerUsuario(mManejadorBaseDeDatosNube.obtenerIdUsuario());
        FirebaseMessaging.getInstance().subscribeToTopic(Long.toString(usuario.getTelefono()))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Suscrito";
                        if (!task.isSuccessful()) {
                            msg = "No suscrito";
                        }
                        Log.d("LOG", msg);
                    }
                });

        if ( usuario.getRecibeAlertasDeUsuariosCercanos() ){
            FirebaseMessaging.getInstance().subscribeToTopic("UsuariosCercanos")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = "Suscrito a usuarios cercanos";
                            if (!task.isSuccessful()) {
                                msg = "No se pudo suscribir a usuarios cercanos";
                            }
                            Log.d("LOG", msg);
                        }
                    });
        }

        FloatingActionButton botonEmergencia = root.findViewById(R.id.boton_emergencia);
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
                intent.putExtra("pantalla",Instructivo.PANTALLA_NOTIFICACIONES);
                startActivity(intent);
            }
        });

        mostrarNotificaciones();

        Bundle bundle = getArguments();
        if (bundle!=null)
            if (bundle.getBoolean("nuevaAlerta")){
                onNotificacionClick(mNotificacionesManager.obtenerPosicionDeUnaNotificacion(
                        bundle.getLong("idNotificacion")));
            }
        return root;
    }

    private void mostrarNotificaciones() {
        final RecyclerView notificacionesRecyclerView =
                (RecyclerView) root.findViewById(R.id.notificationsRecyclerView);
        final LinearLayoutManager notifiacacionesLayoutManager =
                new LinearLayoutManager(getContext());
        notificacionesRecyclerView.setLayoutManager(notifiacacionesLayoutManager);
        mNotificaciones = mNotificacionesManager.getArrayNotifications();
        mNotificacionesRecyclerAdapter =
                new NotificacionesRecyclerAdapter(getContext(), mNotificaciones, this);
        new ItemTouchHelper(itemTouchHelperCallback)
                .attachToRecyclerView(notificacionesRecyclerView);
        notificacionesRecyclerView.setAdapter(mNotificacionesRecyclerAdapter);
    }

    private boolean tieneConexionAInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(
                Context.CONNECTIVITY_SERVICE);

        return connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_SEND_SMS: {

                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("LOG", "se concedieron permisos de enviar sms");
                } else {
                    Log.d("LOG", "no nos dieron persmiso :c");
                }
                return;
            }
            case REQUEST_CODE_LOCATION_PERMISSION: {
                if (grantResults.length > 0) {
                    Log.d("LOG", "se concedieron permisos de consultar localización");
                } else {
                    Log.d("LOG", "no nos dieron persmiso para localizacion:c");
                }
                return;
            }

        }
    }

    @Override
    public void onNotificacionClick(final int position) {
        String idEmergencia = mNotificaciones.get(position).getIdEmergencia();
        if (mNotificaciones.get(position).getEstado()==0){
            HiloParaRevisarSiLaEmergenciaFueTerminada hiloParaRevisarSiLaEmergenciaFueTerminada =
                    new HiloParaRevisarSiLaEmergenciaFueTerminada(idEmergencia, mNotificacionesManager,
                            position);
            hiloParaRevisarSiLaEmergenciaFueTerminada.start();
            try {
                hiloParaRevisarSiLaEmergenciaFueTerminada.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            (new Handler()).postDelayed(new Runnable() {
                public void run() {
                    mNotificacionesRecyclerAdapter.notifyItemChanged(position);
                }
            }, 200);
            //emergencia aún no terminada
            if (mNotificaciones.get(position).getEstado()==0){
                if (tieneConexionAInternet()) {
                    String nombre;
                    if (mNotificaciones.get(position).getEsPropia()) {
                        nombre = "Emergencia";
                    } else {
                        Usuario usuario = mManejadorBaseDeDatosLocal
                                .obtenerUsuario(mManejadorBaseDeDatosNube.obtenerIdUsuario());
                        nombre = usuario.getNombre().replace(" ", "_");
                    }
                    String parametroUbicacion = "";
                    Bundle bundle = getArguments();
                    if (bundle != null)
                        parametroUbicacion = "&ubicacion=" + bundle.getString("localizacion");
                    String enlace = "https://seguimiento-de-alerta.firebaseapp.com/?id=" + idEmergencia
                            + "&nombre=" + nombre + parametroUbicacion;
                    Intent intent = new Intent(getContext(), SeguimientoDeAlerta.class);
                    intent.putExtra("enlace", enlace);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(),
                            "No hay conexión a internet, no se puede hacer el seguimiento de la emergencia.",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
        //emergencia terminada y de alguein mas
        if (mNotificaciones.get(position).getEstado()==1){
            //Abrir Activity del resumen
            Intent intent = new Intent(getContext(), ResumenDeEmergencia.class);
            intent.putExtra("idNotificacion", mNotificaciones.get(position).getIdNotificacion());
            intent.putExtra("idEmergencia", mNotificaciones.get(position).getIdEmergencia());
            startActivity(intent);
        }else if (mNotificaciones.get(position).getEstado()==2){
            //Abrir Activity del resumen de una emergencia propia que sí envió alertas
            Intent intent = new Intent(getContext(), EmergenciaFinalizada.class);
            intent.putExtra("idNotificacion", mNotificaciones.get(position).getIdNotificacion());
            intent.putExtra("idEmergencia", mNotificaciones.get(position).getIdEmergencia());
            intent.putExtra("estado", mNotificaciones.get(position).getIdEmergencia());
            startActivity(intent);
        }else if (mNotificaciones.get(position).getEstado()==3){
            //Abrir lo correspondiente a una emergencia cancelada manualmente
            Intent intent = new Intent(getContext(), EmergenciaFinalizada.class);
            intent.putExtra("idNotificacion", mNotificaciones.get(position).getIdNotificacion());
            intent.putExtra("idEmergencia", mNotificaciones.get(position).getIdEmergencia());
            intent.putExtra("estado", mNotificaciones.get(position).getIdEmergencia());
            startActivity(intent);
        }

        mNotificacionesManager.actualizarCampoLeido(position);
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                mNotificacionesRecyclerAdapter.notifyItemChanged(position);
            }
        }, 500);
    }

    class HiloParaRevisarSiLaEmergenciaFueTerminada extends Thread{
        ManejadorBaseDeDatosNube manejadorBaseDeDatosNube;
        String idEmergencia;
        int posicion;
        ManejadorNotificaciones manejadorNotificaciones;
        HiloParaRevisarSiLaEmergenciaFueTerminada(String idEmergencia,
                                                  ManejadorNotificaciones manejadorNotificaciones,
                                                  int posicion){
            this.idEmergencia = idEmergencia;
            this.manejadorNotificaciones = manejadorNotificaciones;
            this.posicion = posicion;
            this.manejadorBaseDeDatosNube = new ManejadorBaseDeDatosNube();
        }

        public void run(){
            boolean terminada = manejadorBaseDeDatosNube.revisarSiUnaEmergenciaFueTerminada(idEmergencia);
            if (terminada){
                Log.d("LOG", "Emergencia terminada. Actualizando estado.");
                manejadorNotificaciones.actualizarEstado(posicion, 1);
                manejadorNotificaciones.actualizarTitulo(posicion);
            }
        }

    }
}

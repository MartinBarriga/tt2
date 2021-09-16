package com.example.martin.AndroidApp.ui.dashboard;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.martin.AndroidApp.Countdown;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosLocal;
import com.example.martin.AndroidApp.NotificationInfo;
import com.example.martin.AndroidApp.R;
import com.example.martin.AndroidApp.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

/*PARA AGREGAR UNA NUEVA NOTIFICACION SE DEBE DE EJECUTAR LA SIGUIENTE LINEA:
 !!!!!!!!!!!!!!!!!!!!!!!!!!
 Y CAMBIAR LOS STRINGS QUE CONTIENEN LA PALABRA "OBTENIDA/OBTENIDO", NO MOVER EL NULL, EL FALSE Y
  EL USER.GETUID
 mNotificationsManager.addNewNotification(new NotificationInfo(null, "FECHA OBTENIDA", "NOMBRE
 OBTENIDO", "MENSAJE OBTENIDO", false, user.getUid()));
 !!!!!!!!!!!!!!!!!!!!!!!!
 !!!!!!!!!!!!!!!!!!!!!!!!
 */
public class DashboardFragment extends Fragment
        implements NotificationRecyclerAdapter.OnNotificationListener {

    private static final int PERMISSION_SEND_SMS = 123;
    private final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    private ManejadorBaseDeDatosLocal mManejadorBaseDeDatosLocal;
    private DashboardViewModel dashboardViewModel;
    private NotificationsManager mNotificationsManager;
    private NotificationRecyclerAdapter mNotificationsRecyclerAdapter;
    private View root;
    private ArrayList<NotificationInfo> mNotifications;
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView,
                                      @NonNull RecyclerView.ViewHolder viewHolder,
                                      @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    int position = viewHolder.getAdapterPosition();
                    mNotificationsManager.deleteNotification(position);
                    mNotifications = mNotificationsManager.getArrayNotifications();
                    mNotificationsRecyclerAdapter.notifyItemRemoved(position);

                }
            };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        mNotificationsManager = new NotificationsManager(getContext());
        if (ContextCompat
                .checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED || ContextCompat
                .checkSelfPermission(this.getContext(), Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED || ContextCompat
                .checkSelfPermission(this.getContext(), Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder newNameDialog = new AlertDialog.Builder(getContext());
            newNameDialog.setTitle("Permiso para enviar SMS.");
            newNameDialog.setMessage(
                    "Debido a que la función de esta aplicación es enviar mensajes de emergencia," +
                            " a continuación se le pedirá permiso para enviar mensajes desde su " +
                            "celular y acceder a su localización.");
            newNameDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.SEND_SMS,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.READ_PHONE_STATE}, PERMISSION_SEND_SMS);
                }
            });
            Log.d("LOG", "no hay permisos de enviar sms");

            newNameDialog.setNegativeButton(getString(R.string.newNameDialogCancelingButton),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // InputMethodManager imm = (InputMethodManager)getSystemService
                            // (Context.INPUT_METHOD_SERVICE);
                            //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            dialog.cancel();
                        }
                    });


            final AlertDialog sameNameDialog = newNameDialog.create();
            sameNameDialog.show();
        } else {
            Log.d("LOG", "sí hay permisos de enviar sms");
        }

        mManejadorBaseDeDatosLocal = new ManejadorBaseDeDatosLocal(getContext(), null);
        UserInfo usuario = mManejadorBaseDeDatosLocal.obtenerUsuario(user.getUid());


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

        db.collection("contact").whereEqualTo("phoneNumber", Long.toString(usuario.getTelefono()))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String usuarios = "Soy contacto de los usuarios:";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("LOG", document.getId() + " => " + document.getData());
                                usuarios += " " + document.getData().get("userID");
                                Log.d("LOG", "String usuarios: " + usuarios);
                            }
                        } else {
                            Toast.makeText(getContext(), "No se pudo buscar el número.",
                                    Toast.LENGTH_LONG).show();

                            Log.d("LOG", "Error getting documents: ", task.getException());
                        }
                    }
                });

        Button sendSMS = root.findViewById(R.id.sendSMS);
        sendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS(v, getContext());
            }
        });

        displayNotifications();

        return root;
    }

    private void displayNotifications() {
        final RecyclerView notificationsRecyclerView =
                (RecyclerView) root.findViewById(R.id.notificationsRecyclerView);
        final LinearLayoutManager notifiactionsLayoutManager =
                new LinearLayoutManager(getContext());
        notificationsRecyclerView.setLayoutManager(notifiactionsLayoutManager);
        mNotifications = mNotificationsManager.getArrayNotifications();
        mNotificationsRecyclerAdapter =
                new NotificationRecyclerAdapter(getContext(), mNotifications, this);
        new ItemTouchHelper(itemTouchHelperCallback)
                .attachToRecyclerView(notificationsRecyclerView);
        notificationsRecyclerView.setAdapter(mNotificationsRecyclerAdapter);
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

    public void sendSMS(View view, Context context) {

        Intent intent = new Intent(getContext(), Countdown.class);
        startActivity(intent);
    }

    @Override
    public void onViewClick(final int position) {
        final AlertDialog.Builder newNameDialog = new AlertDialog.Builder(getContext());
        newNameDialog.setTitle("Información del mensaje");
        final SpannableString s = new SpannableString(mNotifications.get(position).getMensaje());
        Linkify.addLinks(s, Linkify.WEB_URLS);
        newNameDialog.setMessage(s);
        newNameDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog sameNameDialog = newNameDialog.create();
        sameNameDialog.show();
        ((TextView) sameNameDialog.findViewById(android.R.id.message))
                .setMovementMethod(LinkMovementMethod.getInstance());
        mNotificationsManager.changeLeido(position);
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                mNotificationsRecyclerAdapter.notifyItemChanged(position);
            }
        }, 500);
    }
}

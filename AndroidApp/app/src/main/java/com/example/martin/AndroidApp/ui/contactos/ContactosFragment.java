package com.example.martin.AndroidApp.ui.contactos;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.martin.AndroidApp.Contacto;
import com.example.martin.AndroidApp.Countdown;
import com.example.martin.AndroidApp.Instructivo;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosLocal;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosNube;
import com.example.martin.AndroidApp.R;
import com.example.martin.AndroidApp.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class ContactosFragment extends Fragment implements  ContactosRecyclerAdapter.OnContactoListener{

    private static final int PICK_CONTACT = 1;
    private ManejadorContactos mManejadorContactos;
    private ContactosRecyclerAdapter mContactosRecyclerAdapter;
    private View root;
    private ArrayList<Contacto> mContactos;
    private ManejadorBaseDeDatosNube mManejadorBaseDeDatosNube;
    private ManejadorBaseDeDatosLocal mManejadorBaseDeDatosLocal;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_contacts, container, false);

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
                intent.putExtra("pantalla",Instructivo.PANTALLA_CONTACTOS);
                startActivity(intent);
            }
        });

        mManejadorContactos = new ManejadorContactos(getContext());
        mManejadorBaseDeDatosNube = new ManejadorBaseDeDatosNube();
        mManejadorBaseDeDatosLocal = new ManejadorBaseDeDatosLocal(this.getContext(), null);
        mostrarContactos();

        Usuario usuario = mManejadorBaseDeDatosLocal.obtenerUsuario(mManejadorBaseDeDatosNube.obtenerIdUsuario());
        Switch enviarAlertasAUsuariosCercanos = (Switch) root.findViewById(R.id.EnviarAlertasAUsuariosCercanosSwitch);
        enviarAlertasAUsuariosCercanos.setChecked(usuario.getEnviaAlertasAUsuariosCercanos());

        Switch recibirAlertasDeUsuariosCercanos = (Switch) root.findViewById(R.id.recibirAlertasDeUsuariosCercanosSwitch);
        recibirAlertasDeUsuariosCercanos.setChecked(usuario.getRecibeAlertasDeUsuariosCercanos());

        enviarAlertasAUsuariosCercanos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Usuario usuario = mManejadorBaseDeDatosLocal.obtenerUsuario(mManejadorBaseDeDatosNube.obtenerIdUsuario());
                usuario.setEnviaAlertasAUsuariosCercanos(isChecked);
                mManejadorBaseDeDatosLocal.actualizarUsuario(mManejadorBaseDeDatosLocal.generarFormatoDeUsuarioParaIntroducirEnBD(usuario));
            }
        });

        recibirAlertasDeUsuariosCercanos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Usuario usuario = mManejadorBaseDeDatosLocal.obtenerUsuario(mManejadorBaseDeDatosNube.obtenerIdUsuario());
                usuario.setRecibeAlertasDeUsuariosCercanos(isChecked);
                mManejadorBaseDeDatosLocal.actualizarUsuario(mManejadorBaseDeDatosLocal.generarFormatoDeUsuarioParaIntroducirEnBD(usuario));
                if (isChecked)
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
                else
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("UsuariosCercanos")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    String msg = "Desuscrito a usuarios cercanos";
                                    if (!task.isSuccessful()) {
                                        msg = "No se pudo desuscribir a usuarios cercanos";
                                    }
                                    Log.d("LOG", msg);
                                }
                            });
            }
        });

        return root;
    }

    private void mostrarContactos() {
        final RecyclerView contactsRecyclerView = (RecyclerView) root.findViewById(R.id.contactsRecyclerView);
        final LinearLayoutManager contactosLayoutManager = new LinearLayoutManager(getContext());
        contactsRecyclerView.setLayoutManager(contactosLayoutManager);
        //Aqui tengo duda si debo de agregar a la función de llenado del array desde la base de datos del contactManager;
        mContactos = mManejadorContactos.obtenerArregloContactos();
        mContactosRecyclerAdapter = new ContactosRecyclerAdapter(getContext(), mContactos, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(contactsRecyclerView);
        contactsRecyclerView.setAdapter(mContactosRecyclerAdapter);

        FloatingActionButton botonAgregarContacto = root.findViewById(R.id.button_add);
        botonAgregarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarContacto(v, getContext());
            }
        });
    }


    private void agregarContacto(View view, Context context){
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, PICK_CONTACT);
            Log.d("LOG", "no hay permisos");
        }else{
            Log.d("LOG", "sí hay permisos");
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(intent, PICK_CONTACT);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case (PICK_CONTACT):
                Cursor cursor = null;
                try {


                    Uri uri = data.getData();
                    cursor = getContext().getContentResolver().query(uri, null, null, null, null);
                    cursor.moveToFirst();
                    int indiceTelefono = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    Long telefono = limpiarNumeroDeTelefono(cursor.getString(indiceTelefono).replace(" ", ""));

                    cursor = getContext().getContentResolver().query(uri, null, null, null, null);
                    cursor.moveToFirst();
                    int indiceNombre = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    String nombre = cursor.getString(indiceNombre);

                    Toast.makeText(getContext(), "Contacto Agregado!", Toast.LENGTH_SHORT).show();
                    mManejadorContactos
                            .agregarNuevoContacto(new Contacto(null, telefono, nombre , false, false, false, mManejadorBaseDeDatosNube.obtenerIdUsuario(), false));


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        mostrarContactos();
    }

    public Long limpiarNumeroDeTelefono(String telefono) {
        String telefonoLimpio = "";
        for(int i = telefono.length()-1; i >= 0; i--) {
            if(telefonoLimpio.length() == 10) break;
            if(telefono.charAt(i) >= '0' && telefono.charAt(i) <= '9') {
                telefonoLimpio = telefono.charAt(i) + telefonoLimpio;
            }
        }
        return Long.valueOf(telefonoLimpio);
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT){

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int posicion = viewHolder.getAdapterPosition();
            mManejadorContactos.eliminarContacto(posicion);
            mContactos = mManejadorContactos.obtenerArregloContactos();
            mContactosRecyclerAdapter.notifyItemRemoved(posicion);

        }
    };


    @Override
    public void onNombreClick(final int position) {
        final AlertDialog.Builder nuevoNombreDialog = new AlertDialog.Builder(getContext());
        nuevoNombreDialog.setTitle(R.string.newNameDialogTitle);
        final EditText nuevoNombreEditText = new EditText(getContext());
        nuevoNombreEditText.setText(mContactos.get(position).getNombre());
        nuevoNombreEditText.setGravity(EditText.TEXT_ALIGNMENT_CENTER);
        nuevoNombreEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        nuevoNombreEditText.setSelectAllOnFocus(true);
        nuevoNombreEditText.selectAll();
        nuevoNombreDialog.setView(nuevoNombreEditText);
        nuevoNombreDialog.setPositiveButton( getString(R.string.newNameDialogAcceptingButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        nuevoNombreDialog.setNegativeButton(getString(R.string.newNameDialogCancelingButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams. SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                dialog.cancel();
            }
        });
        final AlertDialog resultadoPeticionCambioDeNombreDialog = nuevoNombreDialog.create();
        resultadoPeticionCambioDeNombreDialog.show();
        resultadoPeticionCambioDeNombreDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nuevoNombre = nuevoNombreEditText.getText().toString();
                String nombreActual = mContactos.get(position).getNombre();
                if(nuevoNombre.isEmpty() || nuevoNombre.equals(nombreActual)){
                    Toast.makeText(getContext(), "El nombre no puede estar vacío.", Toast.LENGTH_LONG).show();
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams. SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }
                else{
                    mManejadorContactos.cambiarNombreContacto(position, nuevoNombre);
                    Toast.makeText(getContext(),  "Nombre cambiado", Toast.LENGTH_LONG).show();
                    getActivity().getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                    );
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams. SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                    resultadoPeticionCambioDeNombreDialog.dismiss();
                    mContactosRecyclerAdapter.notifyItemChanged(position);
                }
            }
        });

    }

    @Override
    public void onMensajeClick(int position) {
        mManejadorContactos.cambiarEstadoEnvioDeMensajesSeleccionado(position);
        mContactosRecyclerAdapter.notifyItemChanged(position);

    }

    @Override
    public void onNotificacionClick(final int position) {
        mManejadorContactos.cambiarEstadoEnvioDeNotificacionesSeleccionado(position);
         (new Handler()).postDelayed(new Runnable() {
            public void run() {
                mContactosRecyclerAdapter.notifyItemChanged(position);
            }
        }, 500);

    }
}

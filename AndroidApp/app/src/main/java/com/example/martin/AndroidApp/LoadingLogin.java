package com.example.martin.AndroidApp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.martin.AndroidApp.recuperarContrasena;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoadingLogin extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Usuario nuevoUsuario;
    private ManejadorBaseDeDatosLocal mManejadorBaseDeDatosLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mAuth = FirebaseAuth.getInstance();

        mManejadorBaseDeDatosLocal =
                new ManejadorBaseDeDatosLocal(getApplicationContext(), null);
        SQLiteDatabase db = mManejadorBaseDeDatosLocal.getWritableDatabase();
        mManejadorBaseDeDatosLocal.onCreate(db);
        db.close();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Alerta";
            String description = "Alertas generadas al recibir un mensaje de emergencia.";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel =
                    new NotificationChannel("Alerta", name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{300,700,300,700,300,700,300,700,300,700});
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            channel.setSound(alarmSound,new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).setUsage(AudioAttributes.USAGE_NOTIFICATION).build());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                channel.setAllowBubbles(true);
            }
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                if (currentUser != null) {
                    Log.d("LOG", "Usuario logueado: " + currentUser.getEmail() + " verificado: " +
                            currentUser.isEmailVerified());
                    if (!currentUser.isEmailVerified()) {
                        currentUser.reload();
                        (new Handler()).postDelayed(new Runnable() {
                            public void run() {
                                setContentView(R.layout.verify_email);
                            }
                        }, 2000);
                    } else {
                        if (!mManejadorBaseDeDatosLocal.existeElUsuario(currentUser.getUid())) {
                            mAuth.signOut();
                            setContentView(R.layout.activity_login);
                        } else {

                            //¿De qué sirve esto aquí? El current user no es null y ya está en la
                            // BD.

                            /*SQLiteDatabase escritura = mConectionSQLiteHelper
                            .getWritableDatabase();
                            ContentValues userValues = new ContentValues();
                            userValues.put("Uid", currentUser.getUid());
                            userValues.put("nombre", "");
                            userValues.put("telefono", "");
                            userValues.put("correo", "");
                            userValues.put("edad", "");
                            userValues.put("mensaje", "Me encuentro en una emergencia. A
                            continuación se muestra mi ubicación actual y algunos datos
                            personales.");
                            userValues.put("nss", 0);
                            userValues.put("medicacion", "");
                            userValues.put("enfermedades", "");
                            userValues.put("toxicomanias", "");
                            userValues.put("tiposangre", "");
                            userValues.put("alergias", "");
                            userValues.put("religion", "");

                            Long idRes = escritura.insert("usuario", null, userValues);
                            escritura.close();
                            Toast.makeText(getApplicationContext(), "Usuario agregado.", Toast
                            .LENGTH_LONG).show();*/
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("firstLaunch", false);
                            startActivity(intent);
                        }
                    }
                } else {
                    setContentView(R.layout.activity_login);
                }
            }
        }, 2000);
    }

    public void signUp(View view) {
        setContentView(R.layout.sign_up_form);
    }

    public void verifyEmail(View view) {
        final FirebaseUser user = mAuth.getCurrentUser();
        user.reload();
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                if (!user.isEmailVerified()) {
                    Toast.makeText(getApplicationContext(),
                            "La dirección de correo no ha sido verificada aún, intente de nuevo.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "La dirección ha sido verificada.",
                            Toast.LENGTH_LONG);
                    FirebaseFirestore fdb = FirebaseFirestore.getInstance();
                    ManejadorBaseDeDatosNube manejadorBaseDeDatosNube =
                            new ManejadorBaseDeDatosNube();
                    manejadorBaseDeDatosNube.agregarUsuario(nuevoUsuario);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("firstLaunch", true);
                    startActivity(intent);
                }
            }
        }, 2000);
    }

    public void reenviarCorreo(View view) {
        FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification();
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

    public void createAccount(View view) {

        EditText field = findViewById(R.id.emailCA);
        String emailCA = field.getText().toString();
        field = findViewById(R.id.passwordCA);
        String passwordCA = field.getText().toString();
        final EditText txt = findViewById(R.id.nameCA);
        final EditText phone = findViewById(R.id.phoneCA);

        if (!emailCA.matches("") && !passwordCA.matches("") &&
                !txt.getText().toString().matches("") && !phone.getText().toString().matches(""))
            mAuth.createUserWithEmailAndPassword(emailCA, passwordCA)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("LOG", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                nuevoUsuario = new Usuario(user.getUid(), txt.getText().toString(),
                                        limpiarNumeroDeTelefono(phone.getText().toString()
                                                .replace(" ", "")),
                                        0,
                                        Long.valueOf(0), "", "",
                                        "", "", "", true,
                                        "Sin respaldo previo", "Cada dia",
                                        -1, -1,
                                        false, false);

                                mManejadorBaseDeDatosLocal.agregarUsuario(mManejadorBaseDeDatosLocal
                                        .generarFormatoDeUsuarioParaIntroducirEnBD(nuevoUsuario));

                                Toast.makeText(getApplicationContext(), "Usuario agregado.",
                                        Toast.LENGTH_LONG).show();

                                if (!user.isEmailVerified()) {
                                    user.sendEmailVerification();
                                    setContentView(R.layout.verify_email);
                                } else {
                                    Intent intent =
                                            new Intent(getApplicationContext(), MainActivity.class);
                                    intent.putExtra("firstLaunch", true);
                                    startActivity(intent);
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("LOG", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(),
                                        "Error al crear usuario, intente de nuevo.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        else Toast.makeText(getApplicationContext(), "Ingrese todos los datos, por favor.",
                Toast.LENGTH_LONG).show();
    }

    public void signIn(View view) {

        EditText field = findViewById(R.id.email);
        String email = field.getText().toString();
        field = findViewById(R.id.password);
        String password = field.getText().toString();

        if (!email.matches("") && !password.matches(""))
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("LOG", "signInWithEmail:success");
                                EditText field = findViewById(R.id.email);
                                field.setText(null);
                                field = findViewById(R.id.password);
                                field.setText(null);
                                FirebaseUser user = mAuth.getCurrentUser();

                                if (!mManejadorBaseDeDatosLocal.existeElUsuario(user.getUid())) {
                                    ManejadorBaseDeDatosNube manejadorBaseDeDatosNube =
                                            new ManejadorBaseDeDatosNube();
                                    manejadorBaseDeDatosNube
                                            .descargarRespaldo(mManejadorBaseDeDatosLocal);
                                }

                                if (!user.isEmailVerified()) {
                                    user.sendEmailVerification();
                                    setContentView(R.layout.verify_email);
                                } else {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.putExtra("firstLaunch", false);
                                    startActivity(intent);
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("LOG", "signInWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(),
                                        "Error al ingresar, intente de nuevo.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        else Toast.makeText(getApplicationContext(), "Ingrese todos los datos, por favor.",
                Toast.LENGTH_LONG).show();
    }

    public void recuperarContrasenaButtonOnClick(View view){
        EditText field = findViewById(R.id.email);
        String email = field.getText().toString();

        Intent intent = new Intent(getApplicationContext(), recuperarContrasena.class);
        if (email!=null && !email.matches("")){
            intent.putExtra("email", email);
        }
        startActivity(intent);
    }

}

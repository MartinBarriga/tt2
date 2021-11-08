package com.example.martin.AndroidApp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class RecuperarContrasena extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasena);
        mAuth = FirebaseAuth.getInstance();
        Bundle bundle = getIntent().getExtras();

        if (bundle != null){
            TextView emailTextview = findViewById(R.id.recuperarContrasenaEmailTextview);
            String email = "";
            email = bundle.getString("email");
            emailTextview.setText(email);
        }
    }

    public void solicitarCambioDeContrasenaButtonOnClick(View view){
        TextView emailTextview = findViewById(R.id.recuperarContrasenaEmailTextview);
        String email = emailTextview.getText().toString();
        if (email != null && !email.matches("")){
            mAuth.sendPasswordResetEmail(email);
            Toast.makeText(getApplicationContext(), "Correo enviado", Toast.LENGTH_LONG);
        } else {
            Toast.makeText(getApplicationContext(), "Ingresa tu correo electrónico", Toast.LENGTH_LONG);
        }
    }

}
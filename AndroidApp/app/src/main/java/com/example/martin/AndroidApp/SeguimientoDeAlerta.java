package com.example.martin.AndroidApp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class SeguimientoDeAlerta extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguimiento_de_alerta);

        WebView webView;
        webView = (WebView)findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });
        webView.loadUrl("https://seguimiento-de-alerta.firebaseapp.com/?id=PSdO2ED0Cd88Dl9zzKSE&nombre=Manuel&ubicacion=-99.143736,19.493818");
    }
}
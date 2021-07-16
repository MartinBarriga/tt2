package com.example.martin.AndroidApp;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class WearListenerService extends WearableListenerService {

    private final String HR_NULL_MESSAGE_PATH = "/alerta";
    private boolean HEMOS_VISTO_UN_PULSO_MAYOR_A_DIEZ=false;

    public WearListenerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("ServiceLOG", "Created");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent)  {
       if (messageEvent.getPath().equals(HR_NULL_MESSAGE_PATH)) {
            Intent startIntent = new Intent(getApplicationContext(), Countdown.class); //Aquí podría ser otra actividad
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //startIntent.putExtra("messageData", messageEvent.getData());
            //startActivity(startIntent);
            String pulso=new String(messageEvent.getData());
            int pulso_int=(int)Float.parseFloat(pulso);
            if(pulso_int>10){
                this.HEMOS_VISTO_UN_PULSO_MAYOR_A_DIEZ=true;
                Toast.makeText(getApplicationContext(), "Ritmo cardíaco: "+pulso, Toast.LENGTH_LONG).show();
            }

            if(this.HEMOS_VISTO_UN_PULSO_MAYOR_A_DIEZ==true&&pulso_int<10){
                this.HEMOS_VISTO_UN_PULSO_MAYOR_A_DIEZ=false;
                Toast.makeText(getApplicationContext(), "Ritmo cardíaco: "+pulso+"\nSe esta muriendo...", Toast.LENGTH_LONG).show();
                startActivity(startIntent);
            }
        }
    }

}

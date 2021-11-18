package com.example.martin.AndroidApp.ui.mediciones;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.example.martin.AndroidApp.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

//Crea la clase que permite crear el evento de conexion
public class Hilo extends Thread {
    private final InputStream streamEntrada;
    private final OutputStream streamSalida;
    private Handler bluetoothIn;

    public Hilo(BluetoothSocket socket, Handler bluetoothIn) {
        InputStream streamEntradaAux = null;
        OutputStream streamSalidaAux = null;
        try {
            streamEntradaAux = socket.getInputStream();
            streamSalidaAux = socket.getOutputStream();
        } catch (IOException e) {
            //No se pudieron crear los streams
        }

        streamEntrada = streamEntradaAux;
        streamSalida = streamSalidaAux;
        this.bluetoothIn = bluetoothIn;
    }

    public void run() {
        //byte[] byte_in = new byte[1];
        int tamDelMensaje = 13;
        char[] mensajeRecibido = new char[tamDelMensaje];
        char caracterRecibido;
        // Se mantiene en modo escucha para determinar el ingreso de datos
        while (true) {
            try {
                //mmInStream.read(byte_in);
                //char ch = (char) byte_in[0];
                do {
                    caracterRecibido = (char) streamEntrada.read();
                } while (caracterRecibido != '+' && caracterRecibido != '-' &&
                        caracterRecibido != -1);

                mensajeRecibido[0] = caracterRecibido;
                for (int indice = 1; indice < tamDelMensaje; indice++) {
                    caracterRecibido = (char) streamEntrada.read();
                    if (caracterRecibido == -1) {
                        break;
                    }
                    mensajeRecibido[indice] = caracterRecibido;
                }
                if (caracterRecibido != -1) {
                    bluetoothIn.obtainMessage(MainActivity.handlerState,
                            Arrays.toString(mensajeRecibido)).sendToTarget();
                }
            } catch (IOException e) {
                break;
            }
        }
    }

    //Envio de trama
    public void write(String input) {
        try {
            streamSalida.write(input.getBytes());
        } catch (IOException e) {
            //si no es posible enviar datos se cierra la conexión
            //getActivity().onBackPressed();
        }
    }
}

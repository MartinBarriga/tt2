package com.example.martin.AndroidApp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConexionSQLiteHelper extends SQLiteOpenHelper {


    final String CREAR_TABLA_USUARIO="CREATE TABLE IF NOT EXISTS usuario (Uid TEXT PRIMARY KEY, nombre TEXT, telefono INTEGER, correo TEXT, edad INTEGER, mensaje TEXT, nss INTEGER, medicacion TEXT, enfermedades TEXT, toxicomanias TEXT, tiposangre TEXT, alergias TEXT, religion TEXT)";
    final String CREAR_TABLA_CONTACTO="CREATE TABLE IF NOT EXISTS contact (id INTEGER PRIMARY KEY AUTOINCREMENT, phoneNumber TEXT, name TEXT, isMessageSelected INTEGER, isNotificationSelected INTEGER, isUser INTEGER, userID TEXT)";
    final String CREAR_TABLA_NOTIFICACION="CREATE TABLE IF NOT EXISTS notificacion (id INTEGER PRIMARY KEY AUTOINCREMENT, fecha TEXT, nombre TEXT, mensaje TEXT, leido TEXT, userID TEXT)";
    public ConexionSQLiteHelper( Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAR_TABLA_USUARIO);
        db.execSQL(CREAR_TABLA_CONTACTO);
        db.execSQL(CREAR_TABLA_NOTIFICACION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuario");
        db.execSQL("DROP TABLE IF EXISTS contact");
        db.execSQL("DROP TABLE IF EXISTS notificacion");
        onCreate(db);
    }
}

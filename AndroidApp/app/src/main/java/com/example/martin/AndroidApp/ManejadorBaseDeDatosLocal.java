package com.example.martin.AndroidApp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ManejadorBaseDeDatosLocal extends SQLiteOpenHelper {

    // Información de la base de datos
    private static final String NOMRE_BASE_DE_DATOS = "lifeguard";
    private static final int VERSION_DE_BASE_DE_DATOS = 2;
    private static final String NOMBRE_TABLA_USUARIO = "usuario";
    private static final String NOMBRE_TABLA_CONTACTO = "contact";
    private static final String NOMBRE_TABLA_NOTIFICACION = "notificacion";

    // Instrucciones para la creación de la base de datos
    private static final String CREAR_TABLA_USUARIO = "CREATE TABLE IF NOT EXISTS " +
            NOMBRE_TABLA_USUARIO + " (Uid TEXT PRIMARY KEY, nombre TEXT, telefono INTEGER, " +
            "correo TEXT, edad INTEGER, mensaje TEXT, nss INTEGER, medicacion TEXT, " +
            "enfermedades TEXT, toxicomanias TEXT, tiposangre TEXT, alergias TEXT, religion TEXT)";
    private static final String CREAR_TABLA_CONTACTO = "CREATE TABLE IF NOT EXISTS " +
            NOMBRE_TABLA_CONTACTO + " (id INTEGER PRIMARY KEY AUTOINCREMENT, phoneNumber TEXT, " +
            "name TEXT, isMessageSelected INTEGER, isNotificationSelected INTEGER, isUser " +
            "INTEGER, " +
            "userID TEXT)";
    private static final String CREAR_TABLA_NOTIFICACION = "CREATE TABLE IF NOT EXISTS " +
            NOMBRE_TABLA_NOTIFICACION + " (id INTEGER PRIMARY KEY AUTOINCREMENT, fecha TEXT, " +
            "nombre TEXT, mensaje TEXT, leido TEXT, userID TEXT)";

    public ManejadorBaseDeDatosLocal(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, NOMRE_BASE_DE_DATOS, factory, VERSION_DE_BASE_DE_DATOS);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAR_TABLA_USUARIO);
        db.execSQL(CREAR_TABLA_CONTACTO);
        db.execSQL(CREAR_TABLA_NOTIFICACION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA_USUARIO);
            db.execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA_CONTACTO);
            db.execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA_NOTIFICACION);
            onCreate(db);
        }
    }

    public JSONObject obtenerDatosDelUsuarioEnFormatoJsonParaEnvioDeNotificaciones(String idUsuario,
                                                                                   String localizacion)
            throws JSONException {
        String nombre = "";
        String mensaje = "";
        SQLiteDatabase readingDatabase = getReadableDatabase();
        Cursor informacionUsuario = readingDatabase
                .rawQuery("SELECT * FROM usuario WHERE Uid = ? ", new String[]{idUsuario});
        while (informacionUsuario.moveToNext()) {
            mensaje = informacionUsuario.getString(5) + "\n" + localizacion + "\n\nNombre: " +
                    informacionUsuario.getString(1)
                    + "\nEdad: " + informacionUsuario.getInt(4) + "\nNúmero de seguridad Social: " +
                    informacionUsuario.getLong(6)
                    + "\nMedicación: " + informacionUsuario.getString(7) +
                    "\nEnfermedades crónicas: " + informacionUsuario.getString(8)
                    + "\nToxicomanías: " + informacionUsuario.getString(9) + "\nTipo de sangre: " +
                    informacionUsuario.getString(10)
                    + "\nAlergias: " + informacionUsuario.getString(11) + "\nReligión: " +
                    informacionUsuario.getString(12);

            nombre = informacionUsuario.getString(1);
        }
        Log.d("LOG", "Mensaje: " + mensaje);

        Cursor contactosConNotificacionSeleccionada = readingDatabase
                .rawQuery("SELECT * FROM contact WHERE userID = ? AND isNotificationSelected = ?",
                        new String[]{idUsuario, "1"});
        String telefono;
        String condicion = "";
        while (contactosConNotificacionSeleccionada.moveToNext()) {
            if (contactosConNotificacionSeleccionada.getPosition() > 0) condicion += " || ";
            telefono = contactosConNotificacionSeleccionada.getString(1).replaceAll(" ", "");
            condicion += "'" + telefono + "' in topics";
        }
        Log.d("LOG", "Condition: " + condicion);

        JSONObject nombreYMensajeAEnviar = new JSONObject();
        nombreYMensajeAEnviar.put("nombre", nombre);
        nombreYMensajeAEnviar.put("mensaje", mensaje);

        JSONObject datosDelUsuarioEnFormatoJson = new JSONObject();
        //Para enviarte una notificación a ti mismo cambia la siguiente línea por json.put("to",
        // "/topics/   ");
        datosDelUsuarioEnFormatoJson.put("condition", condicion);
        datosDelUsuarioEnFormatoJson.put("data", nombreYMensajeAEnviar);

        return datosDelUsuarioEnFormatoJson;
    }

    public Pair<String, ArrayList<String>> obtenerMensajeYNumerosDeTelefonosParaEnvioDeSMS(
            String idUsuario, String localizacion) {
        String mensaje = "";
        SQLiteDatabase readingDatabase = getReadableDatabase();
        Cursor informacionUsuario = readingDatabase
                .rawQuery("SELECT * FROM usuario WHERE Uid = ? ", new String[]{idUsuario});
        while (informacionUsuario.moveToNext()) {
            mensaje = informacionUsuario.getString(5) + "\n" + localizacion + "\n\nNombre: " +
                    informacionUsuario.getString(1)
                    + "\nEdad: " + informacionUsuario.getInt(4) + "\nNúmero de seguridad Social: " +
                    informacionUsuario.getLong(6)
                    + "\nMedicación: " + informacionUsuario.getString(7) +
                    "\nEnfermedades crónicas: " + informacionUsuario.getString(8)
                    + "\nToxicomanías: " + informacionUsuario.getString(9) + "\nTipo de sangre: " +
                    informacionUsuario.getString(10)
                    + "\nAlergias: " + informacionUsuario.getString(11) + "\nReligión: " +
                    informacionUsuario.getString(12);
        }
        Log.d("LOG", "Mensaje: " + mensaje);

        Cursor contactosConMensajeSeleccionado = readingDatabase
                .rawQuery("SELECT * FROM contact WHERE userID = ? AND isMessageSelected = ?",
                        new String[]{idUsuario, "1"});

        ArrayList<String> telefonos = new ArrayList<String>();
        while (contactosConMensajeSeleccionado.moveToNext()) {
            telefonos.add(contactosConMensajeSeleccionado.getString(1).replaceAll(" ", ""));
        }
        return new Pair<String, ArrayList<String>>(mensaje, telefonos);
    }

    public long agregarNotificacion(RemoteMessage notificacion, String idUsuario, String fecha) {
        SQLiteDatabase writingDatabase = getWritableDatabase();
        ContentValues userValues = new ContentValues();
        userValues.put("fecha", fecha);
        userValues.put("nombre", notificacion.getData().get("nombre"));
        userValues.put("mensaje", notificacion.getData().get("mensaje"));
        userValues.put("leido", 0);
        userValues.put("userID", idUsuario);
        long idNotificacion = writingDatabase.insert("notificacion", "id", userValues);
        Log.d("LOG", "Notificación agregada. ID: " + idNotificacion);
        writingDatabase.close();
        return idNotificacion;
    }

    public ArrayList<ContactsInfo> obtenerContactos(String idUsuario) {
        SQLiteDatabase readingDatabase = getReadableDatabase();
        Cursor cursor = readingDatabase
                .rawQuery("SELECT * FROM contact WHERE userID LIKE '" + idUsuario + "'", null);
        ArrayList<ContactsInfo> contactos = new ArrayList<ContactsInfo>();

        while (cursor.moveToNext()) {
            Long id = cursor.getLong(0);
            String telefono = cursor.getString(1);
            String nombre = cursor.getString(2);
            Boolean estaSeleccionadoMensaje = false;
            Boolean estaSeleccionadaNotificacion = false;
            Boolean esUsuarioDeLaApp = false;
            if (cursor.getInt(3) == 1) estaSeleccionadoMensaje = true;
            if (cursor.getInt(4) == 1) estaSeleccionadaNotificacion = true;
            if (cursor.getInt(5) == 1) esUsuarioDeLaApp = true;
            String userID = cursor.getString(6);
            ContactsInfo contacto = new ContactsInfo(id, telefono, nombre, estaSeleccionadoMensaje,
                    estaSeleccionadaNotificacion, esUsuarioDeLaApp, userID);
            contactos.add(contacto);
        }
        readingDatabase.close();
        return contactos;
    }

    public Long agregarNuevoContacto(ContentValues contacto) {
        SQLiteDatabase writingDatabase = getWritableDatabase();
        Long idContacto = writingDatabase.insert("contact", "id", contacto);
        writingDatabase.close();
        return idContacto;
    }

    public void actualizarContacto(String idContacto, ContentValues contacto) {
        SQLiteDatabase writingDatabase = getWritableDatabase();
        writingDatabase.update("contact", contacto, "id = ? AND userID = ? ",
                new String[]{idContacto,
                        (String) contacto.get("userID")});
        writingDatabase.close();
    }

    public void eliminarContacto(String idContacto, ContentValues contacto) {
        SQLiteDatabase writingDatabase = getWritableDatabase();
        writingDatabase.delete("contact", "id = ? AND userID = ? ",
                new String[]{idContacto,
                        (String) contacto.get("userID")});
        writingDatabase.close();
    }

    public UserInfo obtenerUsuario(String idUsuario) {
        SQLiteDatabase readingDatabase = getReadableDatabase();
        Cursor cursor = readingDatabase
                .rawQuery("SELECT * FROM usuario WHERE Uid LIKE '" + idUsuario + "'", null);
        cursor.moveToNext();
        UserInfo usuario =
                new UserInfo(idUsuario, cursor.getString(1), cursor.getLong(2), cursor.getString(3),
                        cursor.getInt(4), cursor.getString(5), cursor.getLong(6), cursor.getString(7),
                        cursor.getString(8), cursor.getString(9), cursor.getString(10),
                        cursor.getString(11), cursor.getString(12));
        return usuario;
    }

    public Boolean existeElUsuario(String idUsuario) {
        SQLiteDatabase readingDatabase = getReadableDatabase();
        Cursor cursor = readingDatabase
                .rawQuery("SELECT * FROM usuario WHERE Uid LIKE '" + idUsuario + "'", null);
        if(cursor.getCount() == 0) return false;
        return true;
    }

    public void actualizarUsuario(String idUsuario, ContentValues usuario) {
        SQLiteDatabase writingDatabase = getWritableDatabase();
        writingDatabase.update("usuario", usuario, "Uid LIKE '" + idUsuario + "'", null);
        writingDatabase.close();
    }

    public void agregarUsuario(ContentValues usuario) {
        SQLiteDatabase writingDatabase = getWritableDatabase();
        writingDatabase.insert("usuario", null, usuario);
    }

    public ArrayList<NotificationInfo> obtenerNotificaciones(String idUsuario) {
        SQLiteDatabase readingDatabase = getReadableDatabase();
        ArrayList<NotificationInfo> notificaciones = new ArrayList<NotificationInfo>();
        Cursor cursor = readingDatabase.rawQuery(
                "SELECT * FROM notificacion WHERE userID LIKE '" + idUsuario +
                        "' ORDER BY id DESC", null);
        while (cursor.moveToNext()) {
            Long id = cursor.getLong(0);
            String fecha = cursor.getString(1);
            String nombre = cursor.getString(2);
            String mensaje = cursor.getString(3);
            Boolean leido = false;
            if (cursor.getInt(4) == 1) leido = true;
            String userID = cursor.getString(5);
            notificaciones.add(new NotificationInfo(id, fecha, nombre, mensaje, leido, userID));
        }
        readingDatabase.close();
        return notificaciones;
    }

    public void eliminarNotificacion(String idUsuario, String idNotificacion) {
        SQLiteDatabase writingDatabase = getWritableDatabase();
        writingDatabase.delete("notificacion", "id = ? AND userID = ? ",
                new String[]{idNotificacion,
                        idUsuario});
        writingDatabase.close();
    }

    public void actualizarNotificacion(String idUsuario, String idNotificacion, ContentValues notificacion) {
        SQLiteDatabase writingDatabase = getWritableDatabase();
        writingDatabase.update("notificacion", notificacion, "id = ? AND userID = ? ",
                new String[]{idNotificacion,
                        idUsuario});
        writingDatabase.close();
    }

    public Long agregarNotificacion(ContentValues notificacion) {
        SQLiteDatabase writingDatabase = getWritableDatabase();
        Long idNotificacion = writingDatabase.insert("notificacion", "id", notificacion);
        writingDatabase.close();
        return idNotificacion;
    }
}

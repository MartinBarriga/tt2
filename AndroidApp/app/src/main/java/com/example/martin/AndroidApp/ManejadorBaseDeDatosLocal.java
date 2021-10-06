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
    private static final int VERSION_DE_BASE_DE_DATOS = 15;
    private static final String NOMBRE_TABLA_USUARIO = "usuario";
    private static final String NOMBRE_TABLA_CONTACTO = "contacto";
    private static final String NOMBRE_TABLA_NOTIFICACION = "notificacion";

    // Instrucciones para la creación de la base de datos
    private static final String CREAR_TABLA_USUARIO = "CREATE TABLE IF NOT EXISTS " +
            NOMBRE_TABLA_USUARIO + " (idUsuario TEXT PRIMARY KEY, nombre TEXT, telefono INTEGER, " +
            "edad INTEGER, nss INTEGER, medicacion TEXT, " +
            "enfermedades TEXT, toxicomanias TEXT, tiposangre TEXT, alergias TEXT, religion TEXT," +
            " enNube INTEGER, fechaUltimoRespaldo TEXT, frecuenciaRespaldo TEXT, " +
            "frecuenciaCardiacaMinima INTEGER, frecuenciaCardiacaMaxima INTEGER)";
    private static final String CREAR_TABLA_CONTACTO = "CREATE TABLE IF NOT EXISTS " +
            NOMBRE_TABLA_CONTACTO + " (idContacto INTEGER PRIMARY KEY AUTOINCREMENT, telefono INTEGER, " +
            "nombre TEXT, recibeSMS INTEGER, recibeNotificaciones INTEGER, esUsuario " +
            "INTEGER, idUsuario TEXT, enNube INTEGER)";
    private static final String CREAR_TABLA_NOTIFICACION = "CREATE TABLE IF NOT EXISTS " +
            NOMBRE_TABLA_NOTIFICACION + " (idNotificacion INTEGER PRIMARY KEY AUTOINCREMENT, fecha TEXT, " +
            "nombre TEXT, mensaje TEXT, leido TEXT, idUsuario TEXT, enNube INTEGER)";

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
        SQLiteDatabase lectura = getReadableDatabase();
        Cursor informacionUsuario = lectura
                .rawQuery("SELECT * FROM usuario WHERE idUsuario = ? ", new String[]{idUsuario});
        while (informacionUsuario.moveToNext()) {
            mensaje = "Me encuentro en  una emergencia. A continuacion se muestra mi ubicacion " +
                    "actual y algunos datos personales" + "\n" + localizacion + "\n\nNombre: " +
                    informacionUsuario.getString(1)
                    + "\nEdad: " + informacionUsuario.getInt(3) + "\nNúmero de seguridad Social: " +
                    informacionUsuario.getLong(4)
                    + "\nMedicación: " + informacionUsuario.getString(5) +
                    "\nEnfermedades crónicas: " + informacionUsuario.getString(6)
                    + "\nToxicomanías: " + informacionUsuario.getString(7) + "\nTipo de sangre: " +
                    informacionUsuario.getString(8)
                    + "\nAlergias: " + informacionUsuario.getString(9) + "\nReligión: " +
                    informacionUsuario.getString(10);

            nombre = informacionUsuario.getString(1);
        }
        Log.d("LOG", "Mensaje: " + mensaje);

        Cursor contactosConNotificacionSeleccionada = lectura
                .rawQuery("SELECT * FROM contacto WHERE idUsuario = ? AND recibeNotificaciones = ?",
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
        // "/topics/tunumerodetelefono");
        datosDelUsuarioEnFormatoJson.put("condition", condicion);
        datosDelUsuarioEnFormatoJson.put("data", nombreYMensajeAEnviar);
        lectura.close();
        return datosDelUsuarioEnFormatoJson;
    }

    public Pair<String, ArrayList<String>> obtenerMensajeYNumerosDeTelefonosParaEnvioDeSMS(
            String idUsuario, String localizacion) {
        String mensaje = "";
        SQLiteDatabase lectura = getReadableDatabase();
        Cursor informacionUsuario = lectura
                .rawQuery("SELECT * FROM usuario WHERE idUsuario = ? ", new String[]{idUsuario});
        while (informacionUsuario.moveToNext()) {
            mensaje =
                    "Me encuentro en  una emergencia. A continuacion se muestra mi ubicacion " +
                            "actual y algunos datos personales" +
                            "\n" + localizacion + "\n\nNombre: " +
                            informacionUsuario.getString(1)
                            + "\nEdad: " + informacionUsuario.getInt(3) +
                            "\nNúmero de seguridad Social: " +
                            informacionUsuario.getLong(4)
                            + "\nMedicación: " + informacionUsuario.getString(5) +
                            "\nEnfermedades crónicas: " + informacionUsuario.getString(6)
                            + "\nToxicomanías: " + informacionUsuario.getString(7) +
                            "\nTipo de sangre: " +
                            informacionUsuario.getString(8)
                            + "\nAlergias: " + informacionUsuario.getString(9) + "\nReligión: " +
                            informacionUsuario.getString(10);
        }
        Log.d("LOG", "Mensaje: " + mensaje);

        Cursor contactosConMensajeSeleccionado = lectura
                .rawQuery("SELECT * FROM contacto WHERE idUsuario = ? AND recibeSMS = ?",
                        new String[]{idUsuario, "1"});

        ArrayList<String> telefonos = new ArrayList<String>();
        while (contactosConMensajeSeleccionado.moveToNext()) {
            telefonos.add(contactosConMensajeSeleccionado.getString(1).replaceAll(" ", ""));
        }
        lectura.close();
        return new Pair<String, ArrayList<String>>(mensaje, telefonos);
    }

    public long agregarNotificacion(RemoteMessage notificacion, String idUsuario, String fecha) {
        SQLiteDatabase escritura = getWritableDatabase();
        ContentValues userValues = new ContentValues();
        userValues.put("fecha", fecha);
        userValues.put("nombre", notificacion.getData().get("nombre"));
        userValues.put("mensaje", notificacion.getData().get("mensaje"));
        userValues.put("leido", 0);
        userValues.put("idUsuario", idUsuario);
        userValues.put("enNube", 0);
        long idNotificacion = escritura.insert("notificacion", "idNotificacion", userValues);
        Log.d("LOG", "Notificación agregada. ID: " + idNotificacion);
        escritura.close();
        return idNotificacion;
    }

    public ArrayList<Contacto> obtenerContactos(String idUsuario) {
        SQLiteDatabase lectura = getReadableDatabase();
        Cursor cursor = lectura
                .rawQuery("SELECT * FROM contacto WHERE idUsuario LIKE '" + idUsuario + "'", null);
        ArrayList<Contacto> contactos = new ArrayList<Contacto>();

        while (cursor.moveToNext()) {
            Long id = cursor.getLong(0);
            Long telefono = cursor.getLong(1);
            String nombre = cursor.getString(2);
            Boolean estaSeleccionadoMensaje = false;
            Boolean estaSeleccionadaNotificacion = false;
            Boolean esUsuarioDeLaApp = false;
            Boolean estaEnLaNube = false;
            if (cursor.getInt(3) == 1) estaSeleccionadoMensaje = true;
            if (cursor.getInt(4) == 1) estaSeleccionadaNotificacion = true;
            if (cursor.getInt(5) == 1) esUsuarioDeLaApp = true;
            String userID = cursor.getString(6);
            if (cursor.getInt(7) == 1) estaEnLaNube = true;
            Contacto contacto = new Contacto(id, telefono, nombre, estaSeleccionadoMensaje,
                    estaSeleccionadaNotificacion, esUsuarioDeLaApp, userID, estaEnLaNube);
            contactos.add(contacto);
        }
        lectura.close();
        return contactos;
    }

    public Long agregarNuevoContacto(ContentValues contacto) {
        SQLiteDatabase escritura = getWritableDatabase();
        Long idContacto = escritura.insert("contacto", "idContacto", contacto);
        escritura.close();
        return idContacto;
    }

    public void actualizarContacto(String idContacto, ContentValues contacto) {
        SQLiteDatabase escritura = getWritableDatabase();
        escritura.update("contacto", contacto, "idContacto = ? AND idUsuario = ? ",
                new String[]{idContacto,
                        (String) contacto.get("idUsuario")});
        escritura.close();
    }

    public void eliminarContacto(String idContacto, ContentValues contacto) {
        SQLiteDatabase escritura = getWritableDatabase();
        escritura.delete("contacto", "idContacto = ? AND idUsuario = ? ",
                new String[]{idContacto,
                        (String) contacto.get("idUsuario")});
        escritura.close();
    }

    public Usuario obtenerUsuario(String idUsuario) {
        SQLiteDatabase lectura = getReadableDatabase();
        Cursor cursor = lectura
                .rawQuery("SELECT * FROM usuario WHERE idUsuario LIKE '" + idUsuario + "'", null);
        if (cursor.moveToNext()) {
            Boolean enNube = cursor.getInt(11) == 1 ? true : false;
            Usuario usuario =
                    new Usuario(idUsuario, cursor.getString(1), cursor.getLong(2),
                            cursor.getInt(3),
                            cursor.getLong(4), cursor.getString(5),
                            cursor.getString(6),
                            cursor.getString(7), cursor.getString(8), cursor.getString(9),
                            cursor.getString(10), enNube, cursor.getString(12),
                            cursor.getString(13), cursor.getInt(14), cursor.getInt(15));
            lectura.close();
            return usuario;
        }
        lectura.close();
        return null;
    }

    public Boolean existeElUsuario(String idUsuario) {
        SQLiteDatabase lectura = getReadableDatabase();
        Cursor cursor = lectura
                .rawQuery("SELECT * FROM usuario WHERE idUsuario LIKE '" + idUsuario + "'", null);

        if (cursor.getCount() == 0) {
            lectura.close();
            return false;
        }
        lectura.close();
        return true;
    }

    public void actualizarUsuario(ContentValues usuario) {
        SQLiteDatabase escritura = getWritableDatabase();
        escritura.update("usuario", usuario, "idUsuario LIKE '" + usuario.get("idUsuario") + "'", null);
        escritura.close();
    }

    public void agregarUsuario(ContentValues usuario) {
        SQLiteDatabase escritura = getWritableDatabase();
        escritura.insert("usuario", null, usuario);
        escritura.close();
    }

    public ArrayList<Notificacion> obtenerNotificaciones(String idUsuario) {
        SQLiteDatabase lectura = getReadableDatabase();
        ArrayList<Notificacion> notificaciones = new ArrayList<Notificacion>();
        Cursor cursor = lectura.rawQuery(
                "SELECT * FROM notificacion WHERE idUsuario LIKE '" + idUsuario +
                        "' ORDER BY idNotificacion DESC", null);
        while (cursor.moveToNext()) {
            Long idNotificacion = cursor.getLong(0);
            String fecha = cursor.getString(1);
            String nombre = cursor.getString(2);
            String mensaje = cursor.getString(3);
            Boolean leido = cursor.getInt(4) == 1 ? true : false;
            Boolean enNube = cursor.getInt(6) == 1 ? true : false;
            notificaciones
                    .add(new Notificacion(idNotificacion, fecha, nombre, mensaje, leido, idUsuario, enNube));
        }
        lectura.close();
        return notificaciones;
    }

    public void eliminarNotificacion(String idUsuario, String idNotificacion) {
        SQLiteDatabase escritura = getWritableDatabase();
        escritura.delete("notificacion", "idNotificacion = ? AND idUsuario = ? ",
                new String[]{idNotificacion,
                        idUsuario});
        escritura.close();
    }

    public void actualizarNotificacion(String idUsuario,
                                       ContentValues notificacion) {
        SQLiteDatabase escritura = getWritableDatabase();
        escritura.update("notificacion", notificacion, "idNotificacion = ? AND idUsuario = ? ",
                new String[]{Long.toString((Long) notificacion.get("idNotificacion")),
                        idUsuario});
        escritura.close();
    }

    public Long agregarNotificacion(ContentValues notificacion) {
        SQLiteDatabase escritura = getWritableDatabase();
        Long idNotificacion = escritura.insert("notificacion", "idNotificacion", notificacion);
        escritura.close();
        return idNotificacion;
    }

    public ContentValues generarFormatoDeUsuarioParaIntroducirEnBD(Usuario usuario) {
        ContentValues contentUsuario = new ContentValues();
        contentUsuario.put("idUsuario", usuario.getIdUsuario());
        contentUsuario.put("nombre", usuario.getNombre());
        contentUsuario.put("telefono", usuario.getTelefono());
        contentUsuario.put("edad", usuario.getEdad());
        contentUsuario.put("nss", usuario.getNss());
        contentUsuario.put("medicacion", usuario.getMedicacion());
        contentUsuario.put("enfermedades", usuario.getEnfermedades());
        contentUsuario.put("toxicomanias", usuario.getToxicomanias());
        contentUsuario.put("tiposangre", usuario.getTipoSangre());
        contentUsuario.put("alergias", usuario.getAlergias());
        contentUsuario.put("religion", usuario.getReligion());
        if (usuario.getEnNube()) {
            contentUsuario.put("enNube", 1);
        } else {
            contentUsuario.put("enNube", 0);
        }
        contentUsuario.put("fechaUltimoRespaldo", usuario.getFechaUltimoRespaldo());
        contentUsuario.put("frecuenciaRespaldo", usuario.getFrecuenciaRespaldo());
        contentUsuario.put("frecuenciaCardiacaMinima", usuario.getFrecuenciaCardiacaMinima());
        contentUsuario.put("frecuenciaCardiacaMaxima", usuario.getFrecuenciaCardiacaMaxima());
        return contentUsuario;
    }

    public ContentValues generarFormatoDeContactoParaIntroducirEnBD(Contacto contacto) {
        ContentValues contentContacto = new ContentValues();
        contentContacto.put("idContacto", contacto.getIdContacto());
        contentContacto.put("telefono", contacto.getTelefono());
        contentContacto.put("nombre", contacto.getNombre());
        if (contacto.getRecibeSMS()) {
            contentContacto.put("recibeSMS", 1);
        } else {
            contentContacto.put("recibeSMS", 0);
        }
        if (contacto.getRecibeNotificaciones()) {
            contentContacto.put("recibeNotificaciones", 1);
        } else {
            contentContacto.put("recibeNotificaciones", 0);
        }
        if (contacto.getEsUsuario()) {
            contentContacto.put("esUsuario", 1);
        } else {
            contentContacto.put("esUsuario", 0);
        }
        contentContacto.put("idUsuario", contacto.getIdUsuario());
        if (contacto.getEnNube()) {
            contentContacto.put("enNube", 1);
        } else {
            contentContacto.put("enNube", 0);
        }
        return contentContacto;
    }

    public ContentValues generarFormatoDeNotificacionParaIntroducirEnBD(
            Notificacion notificacion) {
        ContentValues contentNotificacion = new ContentValues();
        contentNotificacion.put("idNotificacion", notificacion.getIdNotificacion());
        contentNotificacion.put("fecha", notificacion.getFecha());
        contentNotificacion.put("nombre", notificacion.getNombre());
        contentNotificacion.put("mensaje", notificacion.getMensaje());

        if (notificacion.getLeido()) {
            contentNotificacion.put("leido", 1);
        } else {
            contentNotificacion.put("leido", 0);
        }
        if (notificacion.getEnNube()) {
            contentNotificacion.put("enNube", 1);
        } else {
            contentNotificacion.put("enNube", 0);
        }
        contentNotificacion.put("idUsuario", notificacion.getIdUsuario());
        return contentNotificacion;
    }
}
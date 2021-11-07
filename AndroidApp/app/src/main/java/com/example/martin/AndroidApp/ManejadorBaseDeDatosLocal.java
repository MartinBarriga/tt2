package com.example.martin.AndroidApp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ManejadorBaseDeDatosLocal extends SQLiteOpenHelper {

    // Información de la base de datos
    private static final String NOMRE_BASE_DE_DATOS = "TT2020B065";
    private static final int VERSION_DE_BASE_DE_DATOS = 1;
    private static final String NOMBRE_TABLA_USUARIO = "usuario";
    private static final String NOMBRE_TABLA_CONTACTO = "contacto";
    private static final String NOMBRE_TABLA_NOTIFICACION = "notificacion";
    private static final String NOMBRE_TABLA_RESUMEN = "resumen";
    private static final String NOMBRE_TABLA_MEDICION = "medicion";
    private static final String NOMBRE_TABLA_DATO = "dato";
    private static final String NOMBRE_TABLA_ENFERMEDAD = "enfermedad";
    private static final String NOMBRE_TABLA_ENFERMEDADESYUSUARIOS = "enfermedadesyusuarios";

    // Instrucciones para la creación de la base de datos
    private static final String CREAR_TABLA_USUARIO = "CREATE TABLE IF NOT EXISTS " +
            NOMBRE_TABLA_USUARIO + " (idUsuario TEXT PRIMARY KEY, nombre TEXT, telefono INTEGER, " +
            "edad INTEGER, nss INTEGER, medicacion TEXT, " +
            "toxicomanias TEXT, tiposangre TEXT, alergias TEXT, religion TEXT," +
            " enNube INTEGER, fechaUltimoRespaldo TEXT, frecuenciaRespaldo TEXT, " +
            "frecuenciaCardiacaMinima INTEGER, frecuenciaCardiacaMaxima INTEGER, " +
            "enviaAlertasAUsuariosCercanos INTEGER, recibeAlertasDeUsuariosCercanos INTEGER)";
    private static final String CREAR_TABLA_CONTACTO = "CREATE TABLE IF NOT EXISTS " +
            NOMBRE_TABLA_CONTACTO +
            " (idContacto INTEGER PRIMARY KEY AUTOINCREMENT, telefono INTEGER, " +
            "nombre TEXT, recibeSMS INTEGER, recibeNotificaciones INTEGER, esUsuario " +
            "INTEGER, idUsuario TEXT NOT NULL REFERENCES " + NOMBRE_TABLA_USUARIO +
            " (idUsuario), enNube INTEGER)";
    private static final String CREAR_TABLA_NOTIFICACION = "CREATE TABLE IF NOT EXISTS " +
            NOMBRE_TABLA_NOTIFICACION +
            " (idNotificacion INTEGER PRIMARY KEY AUTOINCREMENT, fecha TEXT, titulo TEXT, leido " +
            "INTEGER, idUsuario TEXT NOT NULL REFERENCES " + NOMBRE_TABLA_USUARIO +
            " (idUsuario), enNube INTEGER, idEmergencia TEXT, esPropia INTEGER, estado INTEGER)";
    private static final String CREAR_TABLA_RESUMEN = "CREATE TABLE IF NOT EXISTS " +
            NOMBRE_TABLA_RESUMEN +
            " (idResumen INTEGER PRIMARY KEY AUTOINCREMENT, idNotificacion INTEGER NOT NULL " +
            "REFERENCES " + NOMBRE_TABLA_NOTIFICACION +
            " (idNotificacion) ON UPDATE CASCADE ON DELETE CASCADE, " +
            "nombre TEXT, comentario TEXT, " + "desenlace TEXT, detalles TEXT, duracion TEXT, " +
            "cantidadDePersonasEnviado INTEGER, " + "seguidores INTEGER, enNube INTEGER)";
    private static final String CREAR_TABLA_MEDICION = "CREATE TABLE IF NOT EXISTS " +
            NOMBRE_TABLA_MEDICION +
            " (idMedicion INTEGER PRIMARY KEY AUTOINCREMENT, idUsuario TEXT NOT NULL REFERENCES " +
            NOMBRE_TABLA_USUARIO + " (idUsuario), fecha TEXT, enNube INTEGER)";
    private static final String CREAR_TABLA_DATO = "CREATE TABLE IF NOT EXISTS " +
            NOMBRE_TABLA_DATO +
            " (idDato INTEGER PRIMARY KEY AUTOINCREMENT, idMedicion INTEGER NOT NULL REFERENCES " +
            NOMBRE_TABLA_MEDICION +
            " (idMedicion) ON UPDATE CASCADE ON DELETE CASCADE, frecuenciaCardiaca " +
            "INTEGER, ecg INTEGER, spo2 INTEGER, hora TEXT, enNube INTEGER)";
    private static final String CREAR_TABLA_ENFERMEDAD = "CREATE TABLE IF NOT EXISTS " +
            NOMBRE_TABLA_ENFERMEDAD +
            " (idEnfermedad INTEGER PRIMARY KEY AUTOINCREMENT, enfermedad TEXT NOT NULL)";
    private static final String CREAR_TABLA_ENFERMEDADESYUSUARIOS = "CREATE TABLE IF NOT EXISTS " +
            NOMBRE_TABLA_ENFERMEDADESYUSUARIOS +
            " (idEnfermedad INTEGER NOT NULL REFERENCES "+ NOMBRE_TABLA_ENFERMEDAD+" (idEnfermedad) " +
            ", idUsuario TEXT NOT NULL REFERENCES " + NOMBRE_TABLA_USUARIO + " (idUsuario), " +
            "PRIMARY KEY (idEnfermedad, idUsuario))";

    private static final String INSERTAR_ENFERMEDADES_INICIALES = "INSERT OR IGNORE INTO " + NOMBRE_TABLA_ENFERMEDAD +
            " (idEnfermedad, enfermedad) VALUES " +
            "(1, 'Asma'), " +
            "(2, 'Cancer'), " +
            "(3, 'VIH / SIDA'), " +
            "(4, 'Diabetes tipo 1'), " +
            "(5, 'Diabetes tipo 2'), " +
            "(6, 'Diabetes gestacional'), " +
            "(7, 'Hipertensión arterial'), " +
            "(8, 'Bronquitis crónica'), " +
            "(9, 'Fibrosis quística'), " +
            "(10, 'Cardiopatía coronaria / Arteriopatía coronaria'), " +
            "(11, 'Cardiopatía congénita'), " +
            "(12, 'Esclerosis múltiple'), " +
            "(13, 'Parkinson'), " +
            "(14, 'Insuficiencia renal crónica'), " +
            "(15, 'Hemofilia'), " +
            "(16, 'Artritis degenerativa / Osteoartritis'), " +
            "(17, 'Artritis reumatoide'), " +
            "(18, 'Lupus'), " +
            "(19, 'Hipotiroidismo'), " +
            "(20, 'Hipertiroidismo'), " +
            "(21, 'Gastritis crónica'), " +
            "(22, 'Demencia'), " +
            "(23, 'Apnea del sueño'), " +
            "(24, 'Hepatitis A'), " +
            "(25, 'Hepatitis B'), " +
            "(26, 'Hepatitis C'), " +
            "(27, 'Hepatitis D'), " +
            "(28, 'Hepatitis E'), " +
            "(29, 'Hepatitis alcohólica'), " +
            "(30, 'Hígado graso'), " +
            "(31, 'Enfermedad de Crohn'), " +
            "(32, 'Insuficiencia renal'), " +
            "(33, 'Insuficiencia cardíaca'), " +
            "(34, 'Linfangitis'), " +
            "(35, 'Angina de pecho'), " +
            "(36, 'Leucemia'), " +
            "(37, 'Cirrosis'), " +
            "(38, 'Cardiomegalia / Corazón dilatado'); ";

    public ManejadorBaseDeDatosLocal(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, NOMRE_BASE_DE_DATOS, factory, VERSION_DE_BASE_DE_DATOS);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAR_TABLA_USUARIO);
        db.execSQL(CREAR_TABLA_CONTACTO);
        db.execSQL(CREAR_TABLA_NOTIFICACION);
        db.execSQL(CREAR_TABLA_RESUMEN);
        db.execSQL(CREAR_TABLA_MEDICION);
        db.execSQL(CREAR_TABLA_DATO);
        db.execSQL(CREAR_TABLA_ENFERMEDAD);
        db.execSQL(CREAR_TABLA_ENFERMEDADESYUSUARIOS);
        db.execSQL(INSERTAR_ENFERMEDADES_INICIALES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA_USUARIO);
            db.execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA_CONTACTO);
            db.execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA_NOTIFICACION);
            db.execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA_RESUMEN);
            db.execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA_MEDICION);
            db.execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA_DATO);
            db.execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA_ENFERMEDAD);
            db.execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA_ENFERMEDADESYUSUARIOS);
            onCreate(db);
        }
    }

    public JSONObject obtenerDatosDelUsuarioEnFormatoJsonParaEnvioDeNotificaciones(String idUsuario,
                                                                                   String idEmergencia,
                                                                                   String fecha,
                                                                                   String localizacion)
            throws JSONException {
        String titulo = "";
        SQLiteDatabase lectura = getReadableDatabase();
        Cursor informacionUsuario = lectura
                .rawQuery("SELECT * FROM " + NOMBRE_TABLA_USUARIO + " WHERE idUsuario = ? ",
                        new String[]{idUsuario});
        while (informacionUsuario.moveToNext()) {
            titulo = informacionUsuario.getString(1) + " tiene una emergencia";
        }

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

        JSONObject informacionBaseDeNotificacionAEnviar = new JSONObject();
        informacionBaseDeNotificacionAEnviar.put("titulo", titulo);
        informacionBaseDeNotificacionAEnviar.put("fecha", fecha);
        informacionBaseDeNotificacionAEnviar.put("idEmergencia", idEmergencia);
        informacionBaseDeNotificacionAEnviar.put("localizacion", localizacion);

        JSONObject datosDelUsuarioEnFormatoJson = new JSONObject();
        //Para enviarte una notificación a ti mismo cambia la siguiente línea por json.put("to",
        // "/topics/tunumerodetelefono");
        datosDelUsuarioEnFormatoJson.put("condition", condicion);
        datosDelUsuarioEnFormatoJson.put("data", informacionBaseDeNotificacionAEnviar);
        lectura.close();
        return datosDelUsuarioEnFormatoJson;
    }

    public ArrayList<Pair<String, String>> obtenerMensajeYNumerosDeTelefonosParaEnvioDeSMS(
            String idUsuario, String localizacion, String idEmergencia) {
        SQLiteDatabase lectura = getReadableDatabase();

        Cursor contactosConMensajeSeleccionado = lectura
                .rawQuery("SELECT * FROM contacto WHERE idUsuario = ? AND recibeSMS = ?",
                        new String[]{idUsuario, "1"});

        ArrayList<Pair<String, String>> mensajesYNumeros = new ArrayList<Pair<String, String>>();
        while (contactosConMensajeSeleccionado.moveToNext()) {
            String telefono = contactosConMensajeSeleccionado.getString(1).replaceAll(" ", "");
            String nombre =
                    contactosConMensajeSeleccionado.getString(2).replaceAll(" ", "_") + "-" +
                            contactosConMensajeSeleccionado.getInt(0);
            String mensaje =
                    "https://seguimiento-de-alerta.firebaseapp.com/?" +
                            "id=" + idEmergencia + "&nombre=" + nombre + "&ubicacion=" +
                            localizacion + "\n\n" +
                            "Me encuentro en una emergencia. Puedes hacer el seguimiento de esta " +
                            "emergencia" +
                            " en el enlace de arriba.";
            Log.d("LOG", "Mensaje: " + mensaje);
            mensajesYNumeros.add(new Pair<String, String>(mensaje, telefono));
        }
        lectura.close();
        return mensajesYNumeros;
    }

    public long agregarNotificacion(RemoteMessage notificacion, String idUsuario, int esPropia) {
        String titulo = "";

        SQLiteDatabase escritura = getWritableDatabase();
        ContentValues userValues = new ContentValues();
        userValues.put("idUsuario", idUsuario);
        userValues.put("idEmergencia", notificacion.getData().get("idEmergencia"));
        titulo = notificacion.getFrom().matches("/topics/UsuariosCercanos") ?
                "Un usuario cercano tiene una emergencia" : notificacion.getData().get("titulo");
        userValues.put("titulo", titulo);
        userValues.put("estado", 0);
        userValues.put("fecha", notificacion.getData().get("fecha"));
        userValues.put("leido", 0);
        userValues.put("esPropia", esPropia);
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

    public int obtenerCantidadDeContactos(String idUsuario) {
        SQLiteDatabase lectura = getReadableDatabase();
        Cursor cursor = lectura
                .rawQuery("SELECT * FROM contacto WHERE idUsuario LIKE '" + idUsuario + "'", null);
        int cantidad = cursor.getCount();
        lectura.close();
        return cantidad;
    }

    public Long agregarNuevoContacto(ContentValues contacto) {
        SQLiteDatabase escritura = getWritableDatabase();
        Long idContacto = escritura.insert(NOMBRE_TABLA_CONTACTO, "idContacto", contacto);
        escritura.close();
        return idContacto;
    }

    public void actualizarContacto(String idContacto, ContentValues contacto) {
        SQLiteDatabase escritura = getWritableDatabase();
        escritura.update(NOMBRE_TABLA_CONTACTO, contacto, "idContacto = ? AND idUsuario = ? ",
                new String[]{idContacto,
                        (String) contacto.get("idUsuario")});
        escritura.close();
    }

    public void eliminarContacto(String idContacto, ContentValues contacto) {
        SQLiteDatabase escritura = getWritableDatabase();
        escritura.delete(NOMBRE_TABLA_CONTACTO, "idContacto = ? AND idUsuario = ? ",
                new String[]{idContacto,
                        (String) contacto.get("idUsuario")});
        escritura.close();
    }

    public Usuario obtenerUsuario(String idUsuario) {
        SQLiteDatabase lectura = getReadableDatabase();
        Cursor cursor = lectura
                .rawQuery("SELECT * FROM " + NOMBRE_TABLA_USUARIO + " WHERE idUsuario LIKE '" +
                        idUsuario + "'", null);
        if (cursor.moveToNext()) {
            Boolean enNube = cursor.getInt(10) == 1;
            Boolean enviaAlertasAUsuariosCercanos = cursor.getInt(15) == 1;
            Boolean recibeAlertasDeUsuariosCercanos = cursor.getInt(16) == 1;
            Usuario usuario =
                    new Usuario(idUsuario, cursor.getString(1), cursor.getLong(2),
                            cursor.getInt(3),
                            cursor.getLong(4), cursor.getString(5),
                            cursor.getString(6),
                            cursor.getString(7), cursor.getString(8), cursor.getString(9),
                            enNube, cursor.getString(11),
                            cursor.getString(12), cursor.getInt(13), cursor.getInt(14),
                            enviaAlertasAUsuariosCercanos, recibeAlertasDeUsuariosCercanos);
            lectura.close();
            return usuario;
        }
        lectura.close();
        return null;
    }

    public Boolean existeElUsuario(String idUsuario) {
        SQLiteDatabase lectura = getReadableDatabase();
        Cursor cursor = lectura
                .rawQuery("SELECT * FROM " + NOMBRE_TABLA_USUARIO + " WHERE idUsuario LIKE '" +
                        idUsuario + "'", null);

        if (cursor.getCount() == 0) {
            lectura.close();
            return false;
        }
        lectura.close();
        return true;
    }

    public void actualizarUsuario(ContentValues usuario) {
        SQLiteDatabase escritura = getWritableDatabase();
        escritura.update(NOMBRE_TABLA_USUARIO, usuario,
                "idUsuario LIKE '" + usuario.get("idUsuario") + "'",
                null);
        escritura.close();
    }

    public void agregarUsuario(ContentValues usuario) {
        SQLiteDatabase escritura = getWritableDatabase();
        escritura.insert(NOMBRE_TABLA_USUARIO, null, usuario);
        escritura.close();
    }

    public ArrayList<Notificacion> obtenerNotificaciones(String idUsuario) {
        SQLiteDatabase lectura = getReadableDatabase();
        ArrayList<Notificacion> notificaciones = new ArrayList<Notificacion>();
        Cursor cursor = lectura.rawQuery(
                "SELECT * FROM " + NOMBRE_TABLA_NOTIFICACION + "  WHERE idUsuario LIKE '" +
                        idUsuario +
                        "' ORDER BY idNotificacion DESC", null);
        while (cursor.moveToNext()) {
            Long idNotificacion = cursor.getLong(0);
            String fecha = cursor.getString(1);
            String titulo = cursor.getString(2);
            Boolean leido = cursor.getInt(3) == 1 ? true : false;
            Boolean enNube = cursor.getInt(5) == 1 ? true : false;
            String idEmergencia = cursor.getString(6);
            Boolean esPropia = cursor.getInt(7) == 1 ? true : false;
            int estado = cursor.getInt(8);
            notificaciones
                    .add(new Notificacion(idNotificacion, idUsuario, idEmergencia, titulo, estado,
                            fecha, leido, esPropia, enNube));
        }
        lectura.close();
        return notificaciones;
    }

    public void eliminarNotificacion(String idUsuario, String idNotificacion) {
        SQLiteDatabase escritura = getWritableDatabase();
        escritura.delete(NOMBRE_TABLA_NOTIFICACION, "idNotificacion = ? AND idUsuario = ? ",
                new String[]{idNotificacion,
                        idUsuario});
        escritura.close();
    }

    public void actualizarNotificacion(String idUsuario,
                                       ContentValues notificacion) {
        SQLiteDatabase escritura = getWritableDatabase();
        escritura.update(NOMBRE_TABLA_NOTIFICACION, notificacion,
                "idNotificacion = ? AND idUsuario = ? ",
                new String[]{Long.toString((Long) notificacion.get("idNotificacion")),
                        idUsuario});
        escritura.close();
    }

    public Long agregarNotificacion(ContentValues notificacion) {
        SQLiteDatabase escritura = getWritableDatabase();
        Long idNotificacion =
                escritura.insert(NOMBRE_TABLA_NOTIFICACION, "idNotificacion", notificacion);
        escritura.close();
        return idNotificacion;
    }

    public Boolean existeLaEmergencia(String idEmergencia, String idUsuario) {
        SQLiteDatabase lectura = getReadableDatabase();
        Cursor cursor = lectura
                .rawQuery(
                        "SELECT * FROM " + NOMBRE_TABLA_NOTIFICACION + " WHERE idEmergencia LIKE '"
                                + idEmergencia + "' AND idUsuario LIKE '" + idUsuario + "'", null);

        if (cursor.getCount() == 0) {
            lectura.close();
            return false;
        }
        lectura.close();
        return true;
    }

    public Boolean existeElResumen(Long idNotificacion, String idUsuario) {
        SQLiteDatabase lectura = getReadableDatabase();
        Cursor cursor = lectura
                .rawQuery("SELECT * FROM " + NOMBRE_TABLA_RESUMEN + " INNER JOIN " +
                                NOMBRE_TABLA_NOTIFICACION +
                                " USING(idNotificacion) WHERE " + NOMBRE_TABLA_NOTIFICACION +
                                ".idNotificacion = " + idNotificacion +
                                " AND " + NOMBRE_TABLA_NOTIFICACION + ".idUsuario LIKE '" + idUsuario + "'",
                        null);

        if (cursor.getCount() == 0) {
            lectura.close();
            return false;
        }
        lectura.close();
        return true;
    }

    public Resumen obtenerResumen(Long idNotificacion, String idUsuario) {
        SQLiteDatabase lectura = getReadableDatabase();
        Cursor cursor = lectura
                .rawQuery("SELECT * FROM " + NOMBRE_TABLA_RESUMEN + " INNER JOIN " +
                                NOMBRE_TABLA_NOTIFICACION +
                                " USING(idNotificacion) WHERE " + NOMBRE_TABLA_NOTIFICACION +
                                ".idNotificacion = " + idNotificacion +
                                " AND " + NOMBRE_TABLA_NOTIFICACION + ".idUsuario LIKE '" + idUsuario + "'",
                        null);
        if (cursor.moveToNext()) {
            Boolean enNube = cursor.getInt(9) == 1;
            Resumen resumen = new Resumen(cursor.getLong(0), cursor.getLong(1), cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getInt(7),
                    cursor.getInt(8), enNube);
            lectura.close();
            return resumen;
        }
        lectura.close();
        return null;
    }

    public Long agregarResumen(ContentValues resumen) {
        SQLiteDatabase escritura = getWritableDatabase();
        Long idResumen =
                escritura.insert(NOMBRE_TABLA_RESUMEN, "idResumen", resumen);
        escritura.close();
        return idResumen;
    }

    public ContentValues generarFormatoDeUsuarioParaIntroducirEnBD(Usuario usuario) {
        ContentValues contentUsuario = new ContentValues();
        contentUsuario.put("idUsuario", usuario.getIdUsuario());
        contentUsuario.put("nombre", usuario.getNombre());
        contentUsuario.put("telefono", usuario.getTelefono());
        contentUsuario.put("edad", usuario.getEdad());
        contentUsuario.put("nss", usuario.getNss());
        contentUsuario.put("medicacion", usuario.getMedicacion());
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
        if (usuario.getEnviaAlertasAUsuariosCercanos()) {
            contentUsuario.put("enviaAlertasAUsuariosCercanos", 1);
        } else {
            contentUsuario.put("enviaAlertasAUsuariosCercanos", 0);
        }
        if (usuario.getRecibeAlertasDeUsuariosCercanos()) {
            contentUsuario.put("recibeAlertasDeUsuariosCercanos", 1);
        } else {
            contentUsuario.put("recibeAlertasDeUsuariosCercanos", 0);
        }
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
        contentNotificacion.put("titulo", notificacion.getTitulo());

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
        contentNotificacion.put("idEmergencia", notificacion.getIdEmergencia());
        if (notificacion.getEsPropia()) {
            contentNotificacion.put("esPropia", 1);
        } else {
            contentNotificacion.put("esPropia", 0);
        }
        contentNotificacion.put("estado", notificacion.getEstado());
        return contentNotificacion;
    }

    public ContentValues generarFormatoDeResumenParaIntroducirEnBD(Resumen resumen) {
        ContentValues contentResumen = new ContentValues();
        contentResumen.put("idResumen", resumen.getIdResumen());
        contentResumen.put("idNotificacion", resumen.getIdNotificacion());
        contentResumen.put("nombre", resumen.getNombre());
        contentResumen.put("comentario", resumen.getComentario());
        contentResumen.put("desenlace", resumen.getDesenlace());
        contentResumen.put("detalles", resumen.getDetalles());
        contentResumen.put("duracion", resumen.getDuracion());
        contentResumen.put("cantidadDePersonasEnviado", resumen.getCantidadDePersonasEnviado());
        contentResumen.put("seguidores", resumen.getSeguidores());
        contentResumen.put("enNube", resumen.isEnNube() ? 1 : 0);

        return contentResumen;
    }

    private Long agregarMedicion(ContentValues medicion) {

        SQLiteDatabase escritura = getWritableDatabase();
        Long idMedicion = escritura.insert(NOMBRE_TABLA_MEDICION, "idMedicion", medicion);
        escritura.close();
        return idMedicion;
    }

    private Boolean existeMedicionConMismaFecha(Medicion medicion) {
        SQLiteDatabase lectura = getReadableDatabase();
        Cursor cursor = lectura
                .rawQuery("SELECT * FROM " + NOMBRE_TABLA_MEDICION + " WHERE idUsuario LIKE '" +
                                medicion.getIdUsuario() + "' AND fecha LIKE '" + medicion.getFecha() + "'",
                        null);

        if (cursor.getCount() == 0) {
            lectura.close();
            return false;
        }
        cursor.moveToNext();
        medicion.setIdMedicion(cursor.getLong(0));
        lectura.close();
        return true;
    }

    private ContentValues generarFormatoDeMedicionParaIntroducirEnBD(Medicion medicion) {
        ContentValues contentMedicion = new ContentValues();
        contentMedicion.put("idMedicion", medicion.getIdMedicion());
        contentMedicion.put("idUsuario", medicion.getIdUsuario());
        contentMedicion.put("fecha", medicion.getFecha());
        contentMedicion.put("enNube", medicion.getEnNube() ? 1 : 0);
        return contentMedicion;
    }

    private ContentValues generarFormadoDeDatoParaIntroducirEnBD(Dato dato) {
        ContentValues contentDato = new ContentValues();
        contentDato.put("idDato", dato.getIdDato());
        contentDato.put("idMedicion", dato.getIdMedicion());
        contentDato.put("frecuenciaCardiaca", dato.getFrecuenciaCardiaca());
        contentDato.put("ecg", dato.getEcg());
        contentDato.put("spo2", dato.getSpo2());
        contentDato.put("hora", dato.getHora());
        contentDato.put("enNube", dato.getEnNube() ? 1 : 0);
        return contentDato;
    }

    private Long agregarDato(ContentValues dato) {

        SQLiteDatabase escritura = getWritableDatabase();
        Long IdDato = escritura.insert(NOMBRE_TABLA_DATO, "idDato", dato);
        escritura.close();
        return IdDato;
    }

    public void agregarDatosAMedicion(int ecg, int frecuenciaCardiaca, int spo2, Long tiempo,
                                      String idUsuario) {
        SimpleDateFormat formatoParaFechas = new SimpleDateFormat("yyyy/MM/dd");
        String fecha = formatoParaFechas.format(new Date(tiempo));
        Medicion medicion = new Medicion(null, idUsuario, fecha, false);
        if (!existeMedicionConMismaFecha(medicion)) {
            Long idMedicion = agregarMedicion(generarFormatoDeMedicionParaIntroducirEnBD(medicion));
            medicion.setIdMedicion(idMedicion);
        }
        SimpleDateFormat formatoParaHoras = new SimpleDateFormat("HH:mm:ss.SSS");
        String hora = formatoParaHoras.format(new Date(tiempo));
        Dato dato = new Dato(null, medicion.getIdMedicion(), frecuenciaCardiaca, ecg, spo2, hora,
                false);
        Long idDato = agregarDato(generarFormadoDeDatoParaIntroducirEnBD(dato));
        dato.setIdDato(idDato);
    }

    private Boolean elDatoEstaDentroDeLasHorasSolicitadas(String horaDato,
                                                          String horaInicioSeleccionada,
                                                          String horaFinSeleccionada) {
        Date horaDatoTipoDate = new Date();
        Date horaInicioSeleccionadaTipoDate = new Date();
        Date horaFinSeleccionadaTipoDate = new Date();
        try {
            horaDatoTipoDate =
                    new SimpleDateFormat("HH:mm:ss.SSS").parse(horaDato);
            horaInicioSeleccionadaTipoDate =
                    new SimpleDateFormat("HH:mm:ss.SSS").parse(horaInicioSeleccionada);
            horaFinSeleccionadaTipoDate =
                    new SimpleDateFormat("HH:mm:ss.SSS").parse(horaFinSeleccionada);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (horaDatoTipoDate.after(horaInicioSeleccionadaTipoDate) &&
                horaDatoTipoDate.before(horaFinSeleccionadaTipoDate)) {
            return true;
        }
        return false;
    }

    public ArrayList<Dato> obtenerDatosMedidosDeUnRangoEspecificado(String idUsuario,
                                                                    String fechaSeleccionada,
                                                                    String horaInicioSeleccionada,
                                                                    String horaFinSeleccionada,
                                                                    Context contexto) {
        ArrayList<Dato> datosMedidos = new ArrayList<>();
        SQLiteDatabase lectura = getReadableDatabase();
        Cursor cursorMedicion = lectura.rawQuery(
                "SELECT * FROM " + NOMBRE_TABLA_MEDICION + "  WHERE idUsuario LIKE '" +
                        idUsuario +
                        "' AND fecha LIKE '" + fechaSeleccionada + "'", null);

        if (cursorMedicion.getCount() > 0) {
            cursorMedicion.moveToNext();
            Long idMedicion = cursorMedicion.getLong(0);
            Cursor cursorDato =
                    lectura.rawQuery("SELECT * FROM " + NOMBRE_TABLA_DATO + " WHERE idMedicion = ?",
                            new String[]{Long.toString(idMedicion)});

            while (cursorDato.moveToNext()) {
                Dato dato =
                        new Dato(cursorDato.getLong(0), cursorDato.getLong(1), cursorDato.getInt(2),
                                cursorDato.getInt(3), cursorDato.getInt(4), cursorDato.getString(5),
                                cursorDato.getInt(5) == 1 ? true : false);
                if (elDatoEstaDentroDeLasHorasSolicitadas(dato.getHora(), horaInicioSeleccionada,
                        horaFinSeleccionada)) {
                    datosMedidos.add(dato);
                }
            }
        }
        lectura.close();
        return datosMedidos;
    }

    public ArrayList<String> obtenerEnfermedades(){
        SQLiteDatabase lectura = getReadableDatabase();
        ArrayList<String> enfermedades = new ArrayList<>();
        Cursor cursor = lectura.rawQuery(
                "SELECT * FROM " + NOMBRE_TABLA_ENFERMEDAD,
                null);
        while (cursor.moveToNext()) {
            enfermedades.add(cursor.getString(1));
        }
        lectura.close();
        return enfermedades;
    }

    public ArrayList<String> obtenerEnfermedadesDeUnUsuario(String idUsuario){
        SQLiteDatabase lectura = getReadableDatabase();
        ArrayList<String> enfermedades = new ArrayList<>();
        Cursor cursor = lectura.rawQuery(
                "SELECT * FROM " + NOMBRE_TABLA_ENFERMEDAD + " INNER JOIN " +
                        NOMBRE_TABLA_ENFERMEDADESYUSUARIOS +
                        " USING(idEnfermedad) WHERE " + NOMBRE_TABLA_ENFERMEDADESYUSUARIOS
                        + ".idUsuario LIKE '" + idUsuario + "'",
                null);
        while (cursor.moveToNext()) {
            enfermedades.add(cursor.getString(1));
        }
        lectura.close();
        return enfermedades;
    }

    public Long agregarEnfermadadAUsuario(String idUsuario, Long idEnfermedad){
        SQLiteDatabase escritura = getWritableDatabase();
        ContentValues enfermedadYUsuario = new ContentValues();
        enfermedadYUsuario.put("idEnfermedad", idEnfermedad);
        enfermedadYUsuario.put("idUsuario", idUsuario);
        Long id = escritura.insert(NOMBRE_TABLA_ENFERMEDADESYUSUARIOS,null, enfermedadYUsuario);
        escritura.close();
        return id;
    }

    public void eliminarEnfermedadDeUsuario(String idUsuario, Long idEnfermedad){
        SQLiteDatabase escritura = getWritableDatabase();
        escritura.execSQL("DELETE FROM " + NOMBRE_TABLA_ENFERMEDADESYUSUARIOS + " WHERE " +
                "idEnfermedad = " + idEnfermedad + " AND idUsuario LIKE '" + idUsuario + "'");
        escritura.close();
    }

    public Long agregarEnfermedad(String enfermedad){
        SQLiteDatabase escritura = getWritableDatabase();
        ContentValues nuevaEnfermedad = new ContentValues();
        nuevaEnfermedad.put("enfermedad", enfermedad);
        Long id = escritura.insert(NOMBRE_TABLA_ENFERMEDAD, "idEnfermedad", nuevaEnfermedad);
        escritura.close();
        return id;
    }
}
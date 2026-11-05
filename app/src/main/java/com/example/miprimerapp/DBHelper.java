package com.example.miprimerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "personas.db", null, 1);
    }

    // Se ejecuta UNA sola vez, la primera vez que se usa la base de datos
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE personas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "apellido TEXT NOT NULL, " +
                "edad INTEGER, " +
                "telefono TEXT, " +
                "correo TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS personas");
        onCreate(db);
    }

    // Convierte una Persona en los valores que entiende la base de datos
    private ContentValues valores(Persona p) {
        ContentValues v = new ContentValues();
        v.put("nombre", p.getNombre());
        v.put("apellido", p.getApellido());
        v.put("edad", p.getEdad());
        v.put("telefono", p.getTelefono());
        v.put("correo", p.getCorreo());
        return v;
    }

    // C - CREATE (crear)
    public long insertar(Persona p) {
        return getWritableDatabase().insert("personas", null, valores(p));
    }

    // R - READ (leer)
    public List<Persona> listar() {
        List<Persona> lista = new ArrayList<>();
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT id, nombre, apellido, edad, telefono, correo FROM personas ORDER BY apellido, nombre",
                null)) {
            while (c.moveToNext()) {
                lista.add(new Persona(
                        c.getInt(0),
                        c.getString(1),
                        c.getString(2),
                        c.getInt(3),
                        c.getString(4),
                        c.getString(5)));
            }
        }
        return lista;
    }

    // U - UPDATE (actualizar)
    public int actualizar(Persona p) {
        return getWritableDatabase().update("personas", valores(p),
                "id = ?", new String[]{String.valueOf(p.getId())});
    }

    // D - DELETE (eliminar)
    public int eliminar(int id) {
        return getWritableDatabase().delete("personas",
                "id = ?", new String[]{String.valueOf(id)});
    }
}
package com.example.miprimerapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Listados extends AppCompatActivity {

    private final String[] cabecera = {"Id", "Nombre", "Apellido"};

    private final ArrayList<String[]> datos = new ArrayList<>();

    private FeedReaderDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listados);
        dbHelper = new FeedReaderDbHelper(this);
        TableLayout tblistado = findViewById(R.id.tblistado);
        TextView txtvacio = findViewById(R.id.txtvacio);
        TraerDatos();
        if (datos.isEmpty()) {
            txtvacio.setVisibility(View.VISIBLE);
        } else {
            DynamicTable creaTabla = new DynamicTable(tblistado,this);
            creaTabla.setCabecera(cabecera);
            creaTabla.setDatos(datos);
            creaTabla.crearCabecera();
            creaTabla.crearFilas();
        }
    }
    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    private void TraerDatos() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.FeedEntry.column1,
                FeedReaderContract.FeedEntry.column2
        };
        String sortOrder = FeedReaderContract.FeedEntry.column2 + "ASC";
        try (Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.nameTable,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        )) {
            while (cursor.moveToNext()) {
                String[] fila = new String[3];
                fila[0] = String.valueOf(cursor.getLong(
                        cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID)));
                fila[1] = cursor.getString(
                        cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.column1));
                fila[2] = cursor.getString(
                        cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.column2));
                datos.add(fila);
            }
        }
    }

    public void Regresar(View vista) {
        finish();
    }
}
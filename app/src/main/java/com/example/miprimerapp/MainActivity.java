package com.example.miprimerapp;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DBHelper db;
    private EditText etNombre, etApellido, etEdad, etTelefono, etCorreo;
    private Button btnGuardar, btnActualizar, btnEliminar, btnLimpiar;
    private ListView lvPersonas;

    private List<Persona> personas;
    private int idSeleccionado = -1;   // -1 significa "ninguno seleccionado"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Abrimos la base de datos
        db = new DBHelper(this);

        // 2. Buscamos cada elemento de la pantalla por su id
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etEdad = findViewById(R.id.etEdad);
        etTelefono = findViewById(R.id.etTelefono);
        etCorreo = findViewById(R.id.etCorreo);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        lvPersonas = findViewById(R.id.lvPersonas);

        // 3. Decimos qué hace cada botón
        btnGuardar.setOnClickListener(v -> guardar());
        btnActualizar.setOnClickListener(v -> actualizar());
        btnEliminar.setOnClickListener(v -> eliminar());
        btnLimpiar.setOnClickListener(v -> limpiar());

        // 4. Al tocar una persona de la lista, se carga en el formulario
        lvPersonas.setOnItemClickListener((parent, view, position, id) ->
                seleccionar(personas.get(position)));

        limpiar();
        cargarLista();
    }

    // Lee lo escrito en el formulario y revisa que esté bien
    private Persona leerFormulario(int id) {
        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String edadTexto = etEdad.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();

        if (nombre.isEmpty()) {
            etNombre.setError("Ingrese el nombre");
            return null;
        }
        if (apellido.isEmpty()) {
            etApellido.setError("Ingrese el apellido");
            return null;
        }
        int edad;
        try {
            edad = Integer.parseInt(edadTexto);
        } catch (NumberFormatException e) {
            etEdad.setError("Ingrese una edad válida");
            return null;
        }
        if (edad > 120) {
            etEdad.setError("Edad no válida");
            return null;
        }
        if (!correo.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.setError("Correo no válido");
            return null;
        }
        return new Persona(id, nombre, apellido, edad, telefono, correo);
    }

    // C - CREATE
    private void guardar() {
        Persona p = leerFormulario(0);
        if (p == null) return;

        if (db.insertar(p) != -1) {
            mensaje("Registro guardado");
            limpiar();
            cargarLista();
        } else {
            mensaje("Error al guardar");
        }
    }

    // R - READ
    private void cargarLista() {
        personas = db.listar();
        ArrayAdapter<Persona> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, personas);
        lvPersonas.setAdapter(adapter);
    }

    // U - UPDATE
    private void actualizar() {
        if (idSeleccionado == -1) {
            mensaje("Seleccione un registro de la lista");
            return;
        }
        Persona p = leerFormulario(idSeleccionado);
        if (p == null) return;

        if (db.actualizar(p) > 0) {
            mensaje("Registro actualizado");
            limpiar();
            cargarLista();
        } else {
            mensaje("No se pudo actualizar");
        }
    }

    // D - DELETE
    private void eliminar() {
        if (idSeleccionado == -1) {
            mensaje("Seleccione un registro de la lista");
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Eliminar")
                .setMessage("¿Seguro que desea eliminar este registro?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    if (db.eliminar(idSeleccionado) > 0) {
                        mensaje("Registro eliminado");
                        limpiar();
                        cargarLista();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    // Pasa los datos de una persona al formulario para editarla
    private void seleccionar(Persona p) {
        idSeleccionado = p.getId();
        etNombre.setText(p.getNombre());
        etApellido.setText(p.getApellido());
        etEdad.setText(String.valueOf(p.getEdad()));
        etTelefono.setText(p.getTelefono());
        etCorreo.setText(p.getCorreo());

        btnGuardar.setEnabled(false);
        btnActualizar.setEnabled(true);
        btnEliminar.setEnabled(true);
    }

    // Deja el formulario vacío, listo para un registro nuevo
    private void limpiar() {
        idSeleccionado = -1;
        EditText[] campos = {etNombre, etApellido, etEdad, etTelefono, etCorreo};
        for (EditText campo : campos) {
            campo.setText("");
            campo.setError(null);
        }
        btnGuardar.setEnabled(true);
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
        etNombre.requestFocus();
    }

    private void mensaje(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
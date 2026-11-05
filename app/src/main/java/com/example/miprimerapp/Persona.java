package com.example.miprimerapp;

public class Persona {

    private int id;
    private String nombre;
    private String apellido;
    private int edad;
    private String telefono;
    private String correo;

    public Persona(int id, String nombre, String apellido, int edad, String telefono, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.telefono = telefono;
        this.correo = correo;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public int getEdad() { return edad; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }

    // Así se verá cada persona en la lista de la pantalla
    @Override
    public String toString() {
        return nombre + " " + apellido + " (" + edad + " años)\n" + telefono + "   " + correo;
    }
}

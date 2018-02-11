package com.puzzlesmentales.igu;

import android.graphics.drawable.Drawable;

import com.puzzlesmentales.R;

/**
 * @author  Antonio Paya Gonzalez
 */

public class ItemLista {
    protected long id;
    protected Drawable imagen;
    protected String nombre;
    protected String tipo;
    protected long tiempo;


    public ItemLista(long id, String nombre, String tipo, long tiempo, Drawable rutaImagen) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.tiempo = tiempo;
        this.imagen = rutaImagen;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Drawable getRutaImagen() {
        return imagen;
    }

    public void setRutaImagen(Drawable rutaImagen) {
        this.imagen = rutaImagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public long getTiempo() {
        return tiempo;
    }

    public void setTiempo(long tiempo) {
        this.tiempo = tiempo;
    }
}

package com.puzzlesmentales.util;

import android.os.AsyncTask;

import com.puzzlesmentales.igu.NuevoJuegoActivity;

/**
 * Created by PabloD on 12/01/2018.
 */

public class AsyncTaskNuevaPartida extends AsyncTask<Void, Void, Void>
{
    NuevoJuegoActivity nuevoJuego;

    public AsyncTaskNuevaPartida(NuevoJuegoActivity nj)
    {
        this.nuevoJuego = nj;
    }

    @Override
    protected Void doInBackground(Void... voids)
    {
        nuevoJuego.cargaImagenes();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        nuevoJuego.carga();
    }
}
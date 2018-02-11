package com.puzzlesmentales.util;

import android.os.AsyncTask;

import com.puzzlesmentales.igu.sopa.LeerSopaConfigFragment;

/**
 * Created by PabloD on 08/01/2018.
 */

public class AsyncTaskLeerSopa extends AsyncTask<Void, Void, Void>
{
    LeerSopaConfigFragment sopa;

    public AsyncTaskLeerSopa(LeerSopaConfigFragment sopa){
        this.sopa = sopa;
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        sopa.enciendeDialogoDeCarga();

        Thread hilo;
        hilo = new Thread(new Runnable() {
            @Override
            public void run()
            {
                sopa.procesarImagen( sopa.getImagen() );
            }
        });

        hilo.start();
        try
        {
            hilo.join( 22000 );
        } catch (InterruptedException e) {
            //
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        sopa.cargaTextoLeido();

        sopa.apagaDialogoDeCarga();
    }
}

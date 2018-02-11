package com.puzzlesmentales.igu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.puzzlesmentales.R;
import com.puzzlesmentales.igu.opciones.OpcionesActivity;
import com.puzzlesmentales.util.AnimacionButton;

public class MainActivity extends AppCompatActivity
{

    private Button botonOpciones;
    private Button botonSalir;
    private Button botonNuevoJuego;
    private Button botonCargarPartida;
    public static final String PREFERENCIAS = "pref";
    public static final String PUNTOS = "puntos";
    private SharedPreferences sharedpreferences;
    private Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SI NO TIENE PUNTOS GUARDADOS PONERLOS A 0
        sharedpreferences = getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        if (!sharedpreferences.contains(PUNTOS)) {
            editor.putInt(PUNTOS, 0);
            editor.apply();
        }

        botonNuevoJuego = (Button) findViewById(R.id.btNuevaPartida);
        botonNuevoJuego.setOnClickListener(new View.OnClickListener() // al pulsar Opciones
        {
            @Override
            public void onClick(View view)
            {
                final Animation myAnim = AnimationUtils.loadAnimation(activity, R.anim.bounce_animacion);
                AnimacionButton interpolator = new AnimacionButton(0.8, 10);
                myAnim.setInterpolator(interpolator);
                botonNuevoJuego.startAnimation(myAnim);
                Intent nuevoJuego = new Intent (getApplicationContext(), NuevoJuegoActivity.class);
                startActivity(nuevoJuego);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        botonOpciones = (Button) findViewById(R.id.btOpciones);
        botonOpciones.setOnClickListener(new View.OnClickListener() // al pulsar Opciones
        {
            @Override
            public void onClick(View view)
            {
                final Animation myAnim = AnimationUtils.loadAnimation(activity, R.anim.bounce_animacion);
                AnimacionButton interpolator = new AnimacionButton(0.8, 10);
                myAnim.setInterpolator(interpolator);
                botonOpciones.startAnimation(myAnim);
                Intent opciones = new Intent (getApplicationContext(), OpcionesActivity.class);
                startActivity(opciones);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.slide_out_right);
            }
        });

        botonCargarPartida = (Button) findViewById(R.id.btCargarPartida);
        botonCargarPartida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation myAnim = AnimationUtils.loadAnimation(activity, R.anim.bounce_animacion);
                AnimacionButton interpolator = new AnimacionButton(0.8, 10);
                myAnim.setInterpolator(interpolator);
                botonCargarPartida.startAnimation(myAnim);
                Intent cargar = new Intent (getApplicationContext(), CargarPartidaActivity.class);
                startActivity(cargar);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.slide_out_right);
            }
        });
        botonSalir = (Button) findViewById(R.id.btSalir);
        botonSalir.setOnClickListener(new View.OnClickListener() // al pulsar Salir
        {
            @Override
            public void onClick(View view)
            {
                finish();

            }
        });
    }


    public void verAcercaDe(View boton) {
        Button botonVerAcercaDe = findViewById(R.id.btAcercaDe);
        final Animation myAnim = AnimationUtils.loadAnimation(activity, R.anim.bounce_animacion);
        AnimacionButton interpolator = new AnimacionButton(0.8, 10);
        myAnim.setInterpolator(interpolator);
        botonVerAcercaDe.startAnimation(myAnim);
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Aplicación desarrollada por");
        alertDialog.setMessage("Pablo Díaz\nAntonio Payá\nFernando Sánchez\n\nAsignatura: Software para Dispositivos Móviles.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        SharedPreferences gameSettings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean preguntarSalir = gameSettings.getBoolean("peguntar_salir", true);
        if(preguntarSalir)
            new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Saliendo de la app")
                .setMessage("¿Estás seguro de que quieres salir")
                .setPositiveButton("Si", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
        else
            finish();
    }
}

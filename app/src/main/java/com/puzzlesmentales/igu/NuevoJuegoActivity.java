package com.puzzlesmentales.igu;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.puzzlesmentales.R;
import com.puzzlesmentales.igu.opciones.SudokuConfigActivity;
import com.puzzlesmentales.igu.opciones.SopaLetrasConfigActivity;
import com.puzzlesmentales.util.AsyncTaskNuevaPartida;

public class NuevoJuegoActivity extends AppCompatActivity
{
    private ImageView imgSudoku;
    private ImageView imgSopa;
    private ImageButton btContinuar;
    private LinearLayout layoutSudoku;
    private LinearLayout layoutSopa;
    private TextView txContinuar;

    private final static int SUDOKU = 0;
    private final static int SOPA = 1;

    private int juegoSeleccionado = SUDOKU; // 0 -> SUDOKU
    // 1 -> SOPA DE LETRAS

    private Drawable sudokuIMG;
    private Drawable sopaIMG;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_juego);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        imgSudoku = (ImageView) findViewById(R.id.imagenSudoku);
        layoutSudoku = (LinearLayout) findViewById(R.id.layoutSudoku);

        imgSopa = (ImageView) findViewById(R.id.imagenSopa);
        layoutSopa = (LinearLayout) findViewById(R.id.layoutSopa);

        txContinuar = (TextView) findViewById(R.id.txContinuarONo);
        txContinuar.setText(R.string.txNuevoJuego_Continuar_Defecto);

        btContinuar = (ImageButton) findViewById(R.id.btContinuar);
        btContinuar.setEnabled( false );

        cargarDescripciones();

        AsyncTaskNuevaPartida np = new AsyncTaskNuevaPartida( this );
        np.execute();
    }

    public void carga()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                if (sudokuIMG == null)
                    Log.d("Holaa", "ghhhdda");
                imgSudoku.setImageDrawable( sudokuIMG );
                imgSopa.setImageDrawable( sopaIMG );
            }
        });
    }

    public void cargaImagenes()
    {
        sudokuIMG = resizeImage( R.drawable.sudoku );
        sopaIMG = resizeImage( R.drawable.sopa_letras );
    }

    private void cargarDescripciones()
    {
        TextView txDescSudoku = (TextView) findViewById(R.id.txDescripcionSudoku);
        TextView txDescSopa = (TextView) findViewById(R.id.txDescripcionSopa);

        txDescSudoku.setText( R.string.txNuevoJuego_sudoku );

        txDescSopa.setText( R.string.txNuevoJuego_sopa );
    }

    public void juegoSeleccionado(View view)
    {
        if ( !btContinuar.isEnabled() )
        {
            txContinuar.setText(R.string.txContinuar_NuevoJuego);
            txContinuar.setTextColor(Color.parseColor("#06EF33"));

            btContinuar.setVisibility(View.VISIBLE);
            btContinuar.setEnabled(true);
        }

        if ( view.getId() == R.id.layoutSudoku || view.getId() == R.id.txDescripcionSudoku )
        {
            sudokuSeleccionado();
        }

        else if ( view.getId() == R.id.layoutSopa || view.getId() == R.id.txDescripcionSopa )
        {
            sopaSeleccionada();
        }
    }

    private void sudokuSeleccionado()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) // si la API es 21 o mayor
        {
            layoutSudoku.setBackground(getDrawable(R.drawable.borde_nuevojuego));
            layoutSopa.setBackground(getDrawable(R.drawable.sinborde_nuevojuego));
        }

        else
        {
            txContinuar.setText(R.string.txNuevoJuego_SudokuSeleccionado);
        }

        juegoSeleccionado = SUDOKU;
    }

    private void sopaSeleccionada()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) // si la API es 21 o mayor
        {
            layoutSudoku.setBackground(getDrawable(R.drawable.sinborde_nuevojuego));
            layoutSopa.setBackground(getDrawable(R.drawable.borde_nuevojuego));
        }

        else
        {
            txContinuar.setText(R.string.txNuevoJuego_SopaSeleccionada);
        }

        juegoSeleccionado = SOPA;
    }

    public void onClickJuegoSeleccionado(View view)
    {
        Intent nuevoJuego = null;

        switch (juegoSeleccionado)
        {
            case SUDOKU:
                nuevoJuego = new Intent (getApplicationContext(), SudokuConfigActivity.class);
                break;
            case SOPA:
                nuevoJuego = new Intent( getApplicationContext(), SopaLetrasConfigActivity.class);
                break;
        }

        startActivity(nuevoJuego);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }



    /* Estos métodos de abajo son para mejorar el rendimiento al cargar imágenes */
    /* -------------------------------------------------------------- */

    public Drawable resizeImage(int imageResource)
    {
        Display display = getWindowManager().getDefaultDisplay();
        double deviceWidth = display.getWidth();

        BitmapDrawable bd = (BitmapDrawable) this.getResources().getDrawable(
                imageResource);
        double imageHeight = bd.getBitmap().getHeight();
        double imageWidth = bd.getBitmap().getWidth();

        double ratio = deviceWidth / imageWidth;
        int newImageHeight = (int) (imageHeight * ratio);

        Bitmap bMap = BitmapFactory.decodeResource(getResources(), imageResource);
        Drawable drawable = new BitmapDrawable(this.getResources(),
                getResizedBitmap(bMap, newImageHeight, (int) deviceWidth));

        return drawable;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth)
    {

        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // create a matrix for the manipulation
        Matrix matrix = new Matrix();

        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);

        return resizedBitmap;

    }
}
package com.puzzlesmentales.igu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.puzzlesmentales.R;
import com.puzzlesmentales.db.GestorDB;
import com.puzzlesmentales.igu.sopa.SopaActivity;
import com.puzzlesmentales.igu.sudoku.SudokuActivity;
import com.puzzlesmentales.logic.sopa.SopaLetras;
import com.puzzlesmentales.logic.sudoku.SudokuGame;
import com.puzzlesmentales.util.FormatoTiempoJuego;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class CargarPartidaActivity extends AppCompatActivity {
    private Map<String,SudokuGame> sudokuGames;
    private Map<String,SopaLetras> sopaGames;
    private ItemListaAdapter adaptador;
    private Activity activity = this;
    private ListView lista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_cargar_partida);
        lista = findViewById(R.id.listaPartidas);
        try {
            GestorDB.startConexion(getPackageName(),this);
            sudokuGames = GestorDB.getAllSudokus();
            sopaGames = GestorDB.getAllSopas();
            ArrayList<ItemLista> items = obtenerItems();
            adaptador = new ItemListaAdapter(this, items);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            GestorDB.cerrarConexion();
        }
        if(adaptador.getCount() == 0) crearDialogoJugar();
        lista.setAdapter(adaptador);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                String name = ((ItemLista) adaptador.getItem(position)).getNombre();
                if(((ItemLista) adaptador.getItem(position)).getTipo().equals("Sopa")){
                    Intent intent = new Intent(activity, SopaActivity.class);
                    intent.putExtra("cargada", true);
                    intent.putExtra("game",name);
                    intent.putExtra("tiempo", true);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(activity, SudokuActivity.class);
                    intent.putExtra("cargada", true);
                    intent.putExtra("game",name);
                    intent.putExtra("tiempo", true);
                    startActivity(intent);
                }
            }
        });

        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int pos, long id) {
                final CharSequence colors[] = new CharSequence[] {"Jugar", "Cambiar nombre", "Borrar"};
                final String name = ((ItemLista) adaptador.getItem(pos)).getNombre();
                final boolean sopa = ((ItemLista) adaptador.getItem(pos)).getTipo().equals("Sopa");
                AlertDialog.Builder builder = new AlertDialog.Builder(activity,AlertDialog.THEME_HOLO_DARK);
                builder.setTitle("Elige una opción");
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            if(sopa){
                                Intent intent = new Intent(activity, SopaActivity.class);
                                intent.putExtra("cargada", true);
                                intent.putExtra("game",name);
                                intent.putExtra("tiempo", true);
                                startActivity(intent);
                            }else{
                                Intent intent = new Intent(activity, SudokuActivity.class);
                                intent.putExtra("cargada", true);
                                intent.putExtra("game",name);
                                intent.putExtra("tiempo", true);
                                startActivity(intent);
                            }
                        }
                        else if(which == 1){
                            cambiarNombreJuego(name,sopa);
                        }
                        else{
                            AlertDialog.Builder alert = new AlertDialog.Builder(activity,AlertDialog.THEME_HOLO_DARK);
                            alert.setTitle("ATENCIÓN!!");
                            alert.setMessage("¿Estás seguro de que quieres borrar la partida?");
                            alert.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(sopa){
                                        try {
                                            GestorDB.startConexion(getPackageName(),activity);
                                            SopaLetras sop = GestorDB.getSopaFromNombre(name);
                                            GestorDB.deleteSopa(name);
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        } finally {
                                            GestorDB.cerrarConexion();
                                        }
                                    }
                                    else{
                                        try {
                                            GestorDB.startConexion(getPackageName(),activity);
                                            SudokuGame sop = GestorDB.getSudokuFromNombre(name);
                                            GestorDB.deleteSudoku(name);
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        } finally {
                                            GestorDB.cerrarConexion();
                                        }
                                    }
                                    activity.finish();
                                    startActivity(activity.getIntent());
                                }
                            });
                            alert.setNegativeButton("NO",null);
                            alert.show();
                        }
                    }
                });
                try {
                    GestorDB.startConexion(getPackageName(),activity);
                    sudokuGames = GestorDB.getAllSudokus();
                    sopaGames = GestorDB.getAllSopas();
                    ArrayList<ItemLista> items = obtenerItems();
                    adaptador.clear();
                    adaptador.setItems(items);
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    GestorDB.cerrarConexion();
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        adaptador.notifyDataSetChanged();
                    }
                });
                builder.show();
                return true;
            }
        });
    }

    private void crearDialogoJugar() {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity,AlertDialog.THEME_HOLO_DARK);
        alert.setTitle("Sin partidas guardadas");
        alert.setMessage("No tienes ninguna partida guardada, ¿quieres empezar un nuevo juego?");
        alert.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent nuevoJuego = new Intent (getApplicationContext(), NuevoJuegoActivity.class);
                startActivity(nuevoJuego);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                activity.finish();
            }
        });
        alert.setNegativeButton("NO",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });
        alert.show();
    }

    private void cambiarNombreJuego(final String name, final boolean sopa) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nombre para la partida guardada");

        final EditText inputTxt = new EditText(this);

        inputTxt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        builder.setView(inputTxt);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name2 = inputTxt.getText().toString();
                if(sopa){
                    try {
                        GestorDB.startConexion(getPackageName(),activity);
                        SopaLetras sop = GestorDB.getSopaFromNombre(name);
                        GestorDB.deleteSopa(name);
                        GestorDB.guardarTableroSopa(sop,getPackageName(),name2);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        GestorDB.cerrarConexion();
                    }
                }
                else{
                    try {
                        GestorDB.startConexion(getPackageName(),activity);
                        SudokuGame sop = GestorDB.getSudokuFromNombre(name);
                        GestorDB.deleteSudoku(name);
                        GestorDB.guardarTableroSudoku(sop,sop.getCeldas(),getPackageName(),name2);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        GestorDB.cerrarConexion();
                    }
                }
                activity.finish();
                startActivity(activity.getIntent());
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private ArrayList<ItemLista> obtenerItems() {
        ArrayList<ItemLista> items = new ArrayList<>();
        int cont = 0;
        for (Map.Entry<String,SudokuGame> entry : sudokuGames.entrySet()) {
            int ruta = R.drawable.sudoku;
            items.add(new ItemLista(cont++,entry.getKey(),"Sudoku", entry.getValue().getTiempo(),
                    resizeImage(ruta) ));
        }
        for (Map.Entry<String,SopaLetras> entry : sopaGames.entrySet()) {
            int ruta = R.drawable.sopa_letras;
            items.add(new ItemLista(cont++,entry.getKey(),"Sopa", entry.getValue().getTiempo(),
                    resizeImage(ruta) ));
        }
        return  items;
    }

    /* Estos métodos de abajo son para mejorar el rendimiento al cargar imágenes */
    /* -------------------------------------------------------------- */

    public Drawable resizeImage(int imageResource)
    {
        // Get device dimensions
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

class ItemListaAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<ItemLista> items;
    private FormatoTiempoJuego formatoTiempoJuego;

    public ItemListaAdapter(Activity activity, ArrayList<ItemLista> items) {
        super();
        this.formatoTiempoJuego = new FormatoTiempoJuego();
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getId();
    }

    public void clear(){items.clear();}

    @Override
    public View getView(int position, View contentView, ViewGroup parent) {
        View vi=contentView;

        if(contentView == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater != null ? inflater.inflate(R.layout.list_partidas_guardadas, null) : null;
        }

        ItemLista item = items.get(position);

        ImageView image = vi.findViewById(R.id.imagen);
        image.setImageDrawable(item.getRutaImagen());

        TextView nombre = vi.findViewById(R.id.nombre);
        nombre.setText(item.getNombre());

        TextView tipo = vi.findViewById(R.id.tipo);
        tipo.setText(item.getTipo());

        return vi;
    }

    public void setItems(ArrayList<ItemLista> items) {
        this.items = items;
    }
}

package com.puzzlesmentales.igu.sudoku;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.puzzlesmentales.R;
import com.puzzlesmentales.db.GestorDB;
import com.puzzlesmentales.igu.MainActivity;
import com.puzzlesmentales.igu.sudoku.input.PadNumerico;
import com.puzzlesmentales.igu.sudoku.input.PanelControl;
import com.puzzlesmentales.igu.sudoku.input.VentanaTeclado;
import com.puzzlesmentales.logic.sudoku.GeneradorSudoku;
import com.puzzlesmentales.logic.sudoku.ResuelveSudoku;
import com.puzzlesmentales.logic.sudoku.SudokuGame;
import com.puzzlesmentales.logic.sudoku.Tablero;
import com.puzzlesmentales.util.AndroidUtils;
import com.puzzlesmentales.util.FormatoTiempoJuego;
import com.puzzlesmentales.util.Temporizador;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Activity que permite jugar al sudoku
 * @author  Antonio Paya Gonzalez
 */
public class SudokuActivity extends Activity {
    private ViewGroup rootLayout;
    private TableroSudokuView tableroView;
    private HintsQueue hintsQueue;
    private SudokuGame game;
    private PanelControl panel;
    private VentanaTeclado ventanaTeclado;
    private PadNumerico padNumerico;
    private GeneradorSudoku generador;
    private int[][] sudoku;
    private TextView lbTiempo;
    private boolean mostrarTiempo;
    private GameTimer gameTimer;
    private FormatoTiempoJuego formatoTiempoJuego;
    private Activity activity;
    private int puntos;
    private boolean tiempoLimite;
    private Tablero tableroOriginal;
    private boolean partidaGuardada;
    private int numHuecos;
    private MediaPlayer player;
    private boolean reproduciendo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        AndroidUtils.setThemeFromPreferences(this);
        setContentView(R.layout.sudoku_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.mostrarTiempo = true;
        formatoTiempoJuego = new FormatoTiempoJuego();
        rootLayout = (ViewGroup) findViewById(R.id.root_layout);
        tableroView = (TableroSudokuView) findViewById(R.id.tablero);
        lbTiempo = (TextView) findViewById(R.id.labelTiempo);
        hintsQueue = new HintsQueue(this);
        gameTimer = new GameTimer();
        tiempoLimite = getIntent().getExtras().getBoolean("tiempo");
        partidaGuardada = false;

        if(!getIntent().getExtras().getBoolean("cargada")){
            numHuecos = Integer.parseInt(getIntent().getExtras().getString("numHuecos"));
            generador = new GeneradorSudoku();
            sudoku = generador.generar(numHuecos);
            Tablero tablero = Tablero.createGameFromArray(sudoku);
            tableroOriginal = tablero;
            game = SudokuGame.crearSudokuConTablero(tablero);
        }
        else{
            String name = getIntent().getExtras().getString("game");
            numHuecos = 30;
            try {
                GestorDB.startConexion(getPackageName(),this);
                game = GestorDB.getSudokuFromNombre(name);
                tableroOriginal = game.getCeldas();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                GestorDB.cerrarConexion();
            }
            tableroOriginal = game.getCeldas();
            String str = (game.getCeldas() == null ? "Null" : "No es null");
            Log.i("Hola: ","GetCeldas-----------------"+str );
        }

        game.start();
        tableroView.setGame(game);
        game.setOnSudokuResueltoListener(onSolvedListener);

        hintsQueue.showOneTimeHint("welcome", R.string.bienvenido, R.string.primeraVezRun);
        panel = (PanelControl) findViewById(R.id.metodosEntrada);
        panel.initialize(tableroView, game, hintsQueue);
        ventanaTeclado = panel.getInputMethod(PanelControl.INPUT_METHOD_POPUP);
        padNumerico = panel.getInputMethod(PanelControl.INPUT_METHOD_NUMPAD);
        reproduciendo = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sudoku_activty,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            resolverSudokuSiTienePuntos();
            return true;
        }
        else if(id == R.id.guardar_sudoku){
            showInputTextDialog();
        }
        else if(id == R.id.puntos){
            SharedPreferences prefs = activity.getSharedPreferences(MainActivity.PREFERENCIAS, MODE_PRIVATE);
            int anterioresPuntos = prefs.getInt(MainActivity.PUNTOS,0);
            Toast.makeText(activity, "Ahora tienes "+anterioresPuntos+" puntos", Toast.LENGTH_SHORT).show();
        }else if(id == R.id.iniciarMusica){
            if(player == null){
                try {
                    AssetFileDescriptor afd = getAssets().openFd("music.mp3");
                    player = new MediaPlayer();
                    player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
                    player.prepare();
                    player.start();
                    player.setLooping(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(reproduciendo) {player.pause();reproduciendo = false;}
            else {player.start();player.setLooping(true);reproduciendo=true;}
        }

        return super.onOptionsItemSelected(item);
    }

    private void resolverSudokuSiTienePuntos() {
        SharedPreferences prefs = activity.getSharedPreferences(MainActivity.PREFERENCIAS, MODE_PRIVATE);
        int anterioresPuntos = prefs.getInt(MainActivity.PUNTOS,0);
        if(anterioresPuntos < 500){
            Toast.makeText(activity, "Se necesitan 500 puntos. Te faltan "+(500-anterioresPuntos), Toast.LENGTH_LONG).show();
        }else{
            new ResuelveSudoku(sudoku);
            Tablero tablero = Tablero.createGameFromArray(sudoku);
            game = SudokuGame.crearSudokuConTablero(tablero);
            game.setEstado(SudokuGame.GAME_STATE_COMPLETED);
            tableroView.setGame(game);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(MainActivity.PUNTOS,anterioresPuntos-500);
            editor.apply();
        }
    }

    @Override
    public void onBackPressed() {
        if(!partidaGuardada)
            new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_DARK)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Cerrando juego")
                    .setMessage("¿Estás seguro de que quieres salir sin guardar?")
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
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences gameSettings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int screenPadding = gameSettings.getInt("screen_border_size", 0);
        rootLayout.setPadding(screenPadding, screenPadding, screenPadding, screenPadding);
        tableroView.setColoresErroneos(gameSettings.getBoolean("highlight_wrong_values", true));
        tableroView.setColoresPulsados(gameSettings.getBoolean("highlight_touched_cell", true));

        ventanaTeclado.setEnabled(gameSettings.getBoolean("popupNum", true));
        padNumerico.setEnabled(gameSettings.getBoolean("padNumerico", true));
        padNumerico.setMoveCeldaSeleccionadaOnPress(gameSettings.getBoolean("im_numpad_move_right", false));
        ventanaTeclado.setColorearValoresCompletados(gameSettings.getBoolean("highlight_completed_values", true));
        ventanaTeclado.setMostrarTotalNumeros(gameSettings.getBoolean("show_number_totals", false));
        padNumerico.setColorearValoresCompletados(gameSettings.getBoolean("highlight_completed_values", true));
        padNumerico.setMostrarTotalNumeros(gameSettings.getBoolean("show_number_totals", false));

        panel.activarPrimerInputMethod();
        mostrarTiempo = gameSettings.getBoolean("show_time", true) && tiempoLimite;
        if (game.getEstado() == SudokuGame.GAME_STATE_PLAYING) {
            game.resume();

            if (mostrarTiempo) {
                gameTimer.start();
            }
        }
        updateTime();
        boolean musica = gameSettings.getBoolean("activar_musica", true);
        if(musica && !reproduciendo && player == null){
            try {
                AssetFileDescriptor afd = getAssets().openFd("music.mp3");
                player = new MediaPlayer();
                player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
                player.prepare();
                player.start();
                player.setLooping(true);
                reproduciendo = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onRestart(){
        super.onRestart();
        if(reproduciendo) {player.start();player.setLooping(true);reproduciendo=true;}
    }
    @Override
    protected void onPause() {
        super.onPause();
        gameTimer.stop();
        if(player != null){player.pause();}
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        gameTimer.stop();

        if (game.getEstado() == SudokuGame.GAME_STATE_PLAYING) {
            game.pause();
        }

        game.guardarEstado(outState);
        gameTimer.saveState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) player.release();
    }

    private void showInputTextDialog(){
        final boolean valido = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_DARK);
        builder.setTitle("Nombre para la partida guardada");

        final EditText inputTxt = new EditText(this);

        inputTxt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        builder.setView(inputTxt);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = inputTxt.getText().toString();
                List<String> nombres = new ArrayList<>();
                try {
                    GestorDB.startConexion(getPackageName(),activity);
                    Map<String,SudokuGame> sudokuGames = GestorDB.getAllSudokus();
                    for (Map.Entry<String,SudokuGame> entry : sudokuGames.entrySet()) {
                        nombres.add(entry.getKey());
                    }
                }catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    GestorDB.cerrarConexion();
                }
                if(!nombres.contains(name)){
                    try {
                        GestorDB.startConexion(getPackageName(),activity);
                        GestorDB.guardarTableroSudoku(game, tableroOriginal, getPackageName(), name);
                        partidaGuardada = true;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        GestorDB.cerrarConexion();
                    }
                }else{
                    Toast.makeText(activity, "ERROR: Ya hay una partida con ese nombre", Toast.LENGTH_LONG).show();
                    dialog.cancel();
                }
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

    /**
     * Ocurre cuando se resuelve el sudoku
     */
    private SudokuGame.OnSudokuResueltoListener onSolvedListener = new SudokuGame.OnSudokuResueltoListener() {
        @Override
        public void onSudokuResuelto() {
            if(numHuecos == 30)
                puntos = 30;
            else if(numHuecos == 40)
                puntos = 40;
            else
                puntos = 50;
            int minutos = Integer.parseInt(formatoTiempoJuego.format(game.getTiempo()).split(":")[0]);
            puntos = puntos - minutos;
            puntos = puntos < 0 ? 0 : puntos;
            tableroView.setSoloLectura(true);
            crearDialogoResuelto().show();
            SharedPreferences prefs = getSharedPreferences(MainActivity.PREFERENCIAS, MODE_PRIVATE);
            int anterioresPuntos = prefs.getInt(MainActivity.PUNTOS,0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(MainActivity.PUNTOS,anterioresPuntos+puntos);
            editor.apply();
        }

    };


    /**
     * Crea un diálogo de alerta sencillo
     * @return Nuevo diálogo
     */
    public AlertDialog crearDialogoResuelto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity,AlertDialog.THEME_HOLO_DARK);
        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFERENCIAS, MODE_PRIVATE);
        final int anterioresPuntos = prefs.getInt(MainActivity.PUNTOS,0);
        builder.setTitle("Felicidades")
                .setMessage("Has resuelto el sudoku\n Como recompensa has ganado "+puntos+" puntos")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(activity, "Se han guardado los puntos, ahora tienes: "+(anterioresPuntos+puntos)
                                        , Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });

        return builder.create();
    }

    /**
     * Implementa una clase que hace de temporizador
     */
    private final class GameTimer extends Temporizador {

        GameTimer() {
            super(1000);
        }

        @Override
        protected boolean step(int count, long time) {
            updateTime();
            return false;
        }

    }

    /**
     * Actualiza el tiempo del temporizador
     */
    void updateTime() {
        if (mostrarTiempo) {
            setTitle(formatoTiempoJuego.format(game.getTiempo()));
            lbTiempo.setText(formatoTiempoJuego.format(game.getTiempo()));
        } else {
            setTitle(R.string.app_name);
        }

    }
}

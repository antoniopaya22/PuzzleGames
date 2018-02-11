package com.puzzlesmentales.igu.sopa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.puzzlesmentales.R;
import com.puzzlesmentales.db.GestorDB;
import com.puzzlesmentales.igu.MainActivity;
import com.puzzlesmentales.logic.sopa.SopaLetras;
import com.puzzlesmentales.util.AndroidUtils;
import com.puzzlesmentales.util.FormatoTiempoJuego;
import com.puzzlesmentales.util.Temporizador;

import java.io.IOException;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SopaActivity extends Activity {

    private boolean partidaGuardada;
    private static List<String> variados = new ArrayList<>(Arrays.asList(new String[]{"cielo", "familia", "amor", "viejo", "joven", "mujer", "vida", "mundo",
            "mano", "carta", "ojo", "muerte", "noche", "río", "número", "rey", "año", "frente", "fuerza", "mente", "cosa", "rostro", "corazón", "sol", "cabeza",
            "dado", "bonito"}));

    private static List<String> comidas = new ArrayList<>(Arrays.asList(new String[]{"pollo", "sopa", "ramen", "manzana", "pera", "kiwi", "platano", "filete",
            "merluza", "yogurt", "tomate", "arroz", "macarrones", "pizza", "palomitas", "haburguesa", "calamar", "patata", "galleta", "sal", "huevos", "coco", "yuca", "maiz", "pan",
            "pasas", "limon"}));

    private static List<String> deporte = new ArrayList<>(Arrays.asList(new String[]{"tenis", "remo", "vela", "polo", "judo", "baloncesto", "curling", "golf",
            "futbol", "dardos", "atletismo", "boxeo", "beisbol", "triatlon", "natacion", "patinaje", "salto", "snowboard", "pinpon", "ciclismo"}));


    private static List<String> paises = new ArrayList<>(Arrays.asList(new String[]{ "Afganistán", "Akrotiri", "Albania", "Alemania", "Andorra", "Angola", "Anguila", "Antártida",
            "Argelia", "Argentina", "Armenia", "Aruba","Australia", "Austria", "Azerbaiyán",
            "Bahamas",  "Bangladesh", "Barbados", "Bélgica", "Belice", "Benín", "Bermudas", "Bielorrusia", "Bolivia","Botswuana", "Brasil", "Brunéi",
            "Bulgaria",  "Burundi", "Bután","Camboya", "Camerún", "Canadá", "Chad", "Chile", "China", "Chipre", "Colombia",  "Congo",
            "Croacia", "Cuba", "Dinamarca", "Dominica", "Ecuador", "Egipto",
            "Eslovaquia", "Eslovenia", "España", "EstadosUnidos", "Estonia", "Etiopía", "Filipinas", "Finlandia", "Francia", "Gabón",
            "Georgia", "Ghana", "Gibraltar", "Granada", "Grecia", "Groenlandia",  "Guatemala", "Guinea",
            "Honduras", "HongKong", "Hungría", "India","Indonesia", "Iran", "Iraq", "Irlanda","Islandia",
            "Israel", "Italia", "Jamaica", "Japón",
            "Jordania", "Kazajistán", "Kenia","Letonia",  "Lituania", "Luxemburgo",
            "Macao", "Macedonia", "Madagascar", "Malasia", "Marruecos", "Mauricio", "Mauritania", "México", "Micronesia",
            "Moldavia", "Monaco", "Mongolia", "Montserrat",   "Nepal", "Nicaragua", "Níger", "Nigeria",  "Noruega",
            "Paraguay", "Peru", "Polonia",
            "Portugal", "Qatar", "ReinoUnido","Rumania", "Rusia",

            "Senegal",  "Singapur", "Siria", "Somalia", "Sudafrica", "Sudan", "Suecia", "Suiza",
            "Tailandia", "Togo",
            "Túnez",  "Turquía",  "Ucrania", "Uganda",  "Uruguay","Venezuela", "Vietnam",
            "Yemen", "Zambia", "Zimbabue"}));


    private static List<String> animales = new ArrayList<>(Arrays.asList(new String[]{"cerdo", "pato", "leon", "tigre", "gallina", "oso", "panda", "perro",
            "gato", "raton", "hormiga", "paloma", "serpiente", "zorro", "mono", "toro", "topo", "foca", "camello", "cangrejo", "burro", "caballo", "cebra", "caracol", "avispa",
            "cisne", "loro"}));

    private final static float TEXT_SIZE_MULT = 0.65f;
    private final static int PADDING = 10;

    private Activity activity;
    private String cantidad,temas;
    private GameTimer gameTimer;
    private boolean tiempo;
    private FormatoTiempoJuego formatoTiempoJuego;
    private TextView lbTiempo;
    private SopaLetras sop;
    private boolean cargada;
    private MediaPlayer player;
    private boolean reproduciendo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        partidaGuardada = false;
        AndroidUtils.setThemeFromPreferences(this);
        setContentView(R.layout.sopa_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        gameTimer = new GameTimer();
        formatoTiempoJuego = new FormatoTiempoJuego();
        lbTiempo = (TextView) findViewById(R.id.labelTiempo);
        TableroSopaView tablero = findViewById(R.id.tablero);
        cantidad = getIntent().getExtras().getString("numPalabras","Medias");
        temas = getIntent().getExtras().getString("tema","Variados");
        tiempo = getIntent().getExtras().getBoolean("tiempo",true);
        final SopaLetras sopa;
        final FlowLayout f = findViewById(R.id.board);
        cargada = getIntent().getExtras().getBoolean("cargada");
        if(cargada){
            String name = getIntent().getExtras().getString("game");
            try {
                GestorDB.startConexion(getPackageName(),this);
                sop = GestorDB.getSopaFromNombre(name);
                tablero.init(sop,false);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                GestorDB.cerrarConexion();
            }
        }else{
            tablero.init(sopa = creaSopaLetras());
            sop = sopa;
        }

        List<String> adv = sop.getAdivinadas();
        for(String palabra : sop.getColocadas()) {
            TextView text;
            f.addView(text = new TextView(activity));
            text.setText(palabra);
            text.setTextSize(text.getTextSize()*TEXT_SIZE_MULT);
            text.setPadding(PADDING+5,PADDING,PADDING+5,PADDING);
            if(adv.contains(palabra)) {
                text.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }
        tablero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                f.removeAllViews();
                List<String> adv = sop.getAdivinadas();
                for(String palabra : sop.getColocadas()) {
                    TextView text;
                    f.addView(text = new TextView(activity));
                    text.setTextSize(text.getTextSize()*TEXT_SIZE_MULT);
                    text.setText(palabra);
                    text.setPadding(PADDING +5,PADDING,PADDING +5,PADDING);
                    if(adv.contains(palabra)) {
                        text.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                }
            }
        });

        gameTimer.start();

        updateTime();
        reproduciendo = false;
    }



    private SopaLetras creaSopaLetras()
    {
        SopaLetras sopaLetras = null;

        char[][] sopa = (char[][]) getIntent().getExtras().getSerializable("sopa");
        if(sopa != null) {
            List<String> palabras_a_buscar = (ArrayList<String>) getIntent()
                    .getExtras()
                    .getSerializable("palabras_encontradas");
            sopaLetras = new SopaLetras(sopa, palabras_a_buscar);
        }

        else
        {
            int alto =  10;
            int ancho = 10;
            int colocar = 12;

            if(cantidad.equalsIgnoreCase("Pocas")){
                alto =  10;
                ancho = 8;
                colocar = 7;
            }else if(cantidad.equalsIgnoreCase("Muchas")){
                alto =  15;
                ancho = 12;
                colocar = 17;
            }

            List<String> palabras = variados;
            if(temas.equalsIgnoreCase("Comidas")){
                palabras = comidas;
            }else if(temas.equalsIgnoreCase("Deportes")){
                palabras = deporte;
            }else if(temas.equalsIgnoreCase("Paises")){
                palabras = new ArrayList<>();
                Random r = new Random();
                for(String p : paises){
                    if(r.nextInt(100) < 25){
                        String string = Normalizer.normalize(p.toLowerCase(), Normalizer.Form.NFD);
                        string = string.replaceAll("[^\\p{ASCII}]", "");
                        palabras.add(string);
                    }
                }

            }else if(temas.equalsIgnoreCase("Animales")){
                palabras = animales;
            }else{
                Random r = new Random();
                palabras = new ArrayList<>();
                palabras.addAll(variados);
                palabras.addAll(comidas);
                palabras.addAll(deporte);
                palabras.addAll(animales);
                for(int i = 0; i < 15; i++){
                    String p = paises.get(r.nextInt(paises.size()));
                    if(!palabras.contains(p)){
                        String string = Normalizer.normalize(p.toLowerCase(), Normalizer.Form.NFD);
                        string = string.replaceAll("[^\\p{ASCII}]", "");
                        palabras.add(string);
                    }
                }
            }

            sopaLetras = new SopaLetras(alto,ancho,colocar,palabras);
        }

        return sopaLetras;
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
    public void onDestroy() {
        super.onDestroy();
        if (player != null) player.release();
    }

    @Override
    protected void onResume() {
        super.onResume();

        sop.resume();

        if (tiempo) {
            gameTimer.start();
        }

        updateTime();
        SharedPreferences gameSettings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        sop.pause();
        gameTimer.stop();
        gameTimer.saveState(outState);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sopa_activty,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.guardar_sopa){
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
                    Map<String,SopaLetras> sopas = GestorDB.getAllSopas();
                    for (Map.Entry<String,SopaLetras> entry : sopas.entrySet()) {
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
                        GestorDB.guardarTableroSopa(sop, getPackageName(), name);
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
    void updateTime() {
        if (tiempo) {
            setTitle(formatoTiempoJuego.format(sop.getTiempo()));
            lbTiempo.setText(formatoTiempoJuego.format(sop.getTiempo()));
        } else {
            setTitle(R.string.app_name);
        }

    }
}

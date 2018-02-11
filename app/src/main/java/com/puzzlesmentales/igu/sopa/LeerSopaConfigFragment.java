package com.puzzlesmentales.igu.sopa;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.puzzlesmentales.R;
import com.puzzlesmentales.igu.sopa.fragments_pasos.Fragment_Pasos_Adaptador;
import com.puzzlesmentales.util.AsyncTaskLeerSopa;
import com.puzzlesmentales.util.Tesseract;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class LeerSopaConfigFragment extends Fragment {

    private View rootView;

    private Button btLeerSopa;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap imagen = null;
    private Tesseract tess;

    private Fragment_Pasos_Adaptador adaptadorPestanias;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private ListView listViewPalabras;
    private ArrayList<String> palabras_buscadas = new ArrayList<>(); // Lista de palabras_buscadas seleccionadas (NO
    // se puede usar el tipo List; ArrayAdapter no lo permite.)
    ArrayAdapter<String> adaptadorPalabras; // Manejador de la lista de palabras_buscadas del ListView
    private EditText etAniadePalabra;
    private Button btAniadePalabra;
    private View resultado;
    private ImageView imgResultado;

    private TextView txResultado;

    private TextView txPalabrasBuscadasEncontradas;
    private TextView txSeHanEncontrado;
    private int palabrasEncontradas = 0;

    private char[][] sopa;
    private ArrayList<String> palabras_encontradas = new ArrayList<>();

    private Button btRecargarPalabrasBuscadas;

    private String textoLeido;
    AsyncTaskLeerSopa lectorSopa; // lee la sopa
    // en una tarea asíncrona
    private ProgressDialog progreso;


    public LeerSopaConfigFragment()
    {

    }

    public static LeerSopaConfigFragment newInstance()
    {
        LeerSopaConfigFragment fragment = new LeerSopaConfigFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_leer_sopa_config, container,
                false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        adaptadorPestanias = new Fragment_Pasos_Adaptador(getFragmentManager(), rootView.getContext());
        viewPager.setAdapter(adaptadorPestanias);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.BLUE);
        aniadeListenerAlTabLayout();
        seleccionarPestaniaDesdePreferencias( rootView.getContext() );

        btAniadePalabra = (Button) rootView.findViewById(R.id.btAniadirPalabra);
        aniadeListenerAlBotonAniadirPalabra();

        btLeerSopa = (Button) rootView.findViewById(R.id.btLeerSopa);
        aniadeListenerAlBotonLeerSopa();

        etAniadePalabra = (EditText) rootView.findViewById(R.id.editTextAniadePalabra);
        restringeEditText();
        listViewPalabras = (ListView) rootView.findViewById(R.id.listViewPalabras);
        adaptadorPalabras=new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,
                palabras_buscadas)
        { /* Consiguiendo la vista del ListView, se le puede cambiar el tamaño a los items de éste */
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                // View del listView
                View view = super.getView(position, convertView, parent);
                TextView texto = (TextView) view.findViewById(android.R.id.text1);
                texto.setTextSize(TypedValue.COMPLEX_UNIT_DIP,12);

                // Return the view
                return view;
            }
        };

        listViewPalabras.setAdapter( adaptadorPalabras );
        aniadeListenerListView();

        txSeHanEncontrado = (TextView) rootView.findViewById(R.id.txSeHanEncontrado);

        btRecargarPalabrasBuscadas = (Button) rootView.findViewById(R.id.btRecargarPalabrasBuscadas);
        aniadeListenerBotonPalabrasBuscadas();

        return rootView;
    }


    public Bitmap getImagen()
    {
        return imagen;
    }


    /* ======================== Métodos para guardar/cargar el PASO
                                por defecto al visualizar la ventana
                                de configuración de leer Sopa.
                                Una persona que ya se ha visto todos
                                los pasos querrá ir rápido a la
                                pestaña de Resultado.               ==================== */


    /*
    * Selecciona la pestaña de los pasos a partir de SharedPreferences.
    */
    private void seleccionarPestaniaDesdeIndice( int indice )
    {
        tabLayout.setScrollPosition(indice,0f,true);
        viewPager.setCurrentItem( indice );
    }

    /*
    * Selecciona la pestaña de los pasos a partir de SharedPreferences.
    */
    private void seleccionarPestaniaDesdePreferencias(Context contexto)
    {
        int indice = cargarPasoDesdePreferencias();
        tabLayout.setScrollPosition(indice,0f,true);
        viewPager.setCurrentItem( indice );
    }

    private void guardarPaso(int paso)
    {
        SharedPreferences preferencias;
        preferencias = getActivity().getPreferences(Context.MODE_PRIVATE);

        SharedPreferences.Editor editor;
        editor = preferencias.edit();

        editor.putInt("paso", paso);
        editor.apply();
    }

    private int cargarPasoDesdePreferencias()
    {
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        return  preferences.getInt("paso", 0); // el 0 es el valor por defecto
    }

    private void aniadeListenerAlTabLayout()
    {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                guardarPaso( tab.getPosition() );
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void aniadeListenerAlBotonAniadirPalabra()
    {
        btAniadePalabra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                aniadePalabra();
            }
        });
    }

    private void aniadeListenerAlBotonLeerSopa()
    {
        btLeerSopa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(getContext(), "El procesamiento de la imagen puede tomar hasta 20 segundos.\n" +
                        "Por favor, ten paciencia.", Toast.LENGTH_LONG).show();
                leerSopaDesdeGaleria();
            }
        });
    }

    private void aniadeListenerBotonPalabrasBuscadas()
    {
        btRecargarPalabrasBuscadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (imagen != null)
                    recargaPalabrasBuscadas();
            }
        });
    }

    private void aniadeListenerListView()
    {
        listViewPalabras.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int posicion, long l)
            {
                String palabra = (String) adapterView.getItemAtPosition( posicion );
                borrarDeListaPalabras( palabra );
            }

        });
    }

    /* Restringe la barra espaciadora al escribir la palabra en el EditText */
    private void restringeEditText()
    {
        InputFilter filter = new InputFilter()
        {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend)
            {
                for (int i = start; i < end; i++) {
                    if (Character.isWhitespace(source.charAt(i)))
                    {
                        return "";
                    }
                }
                return null;
            }

        };
        etAniadePalabra.setFilters( new InputFilter[] { filter } );
    }


    /* Cuando el jugador da click en el botón "Añadir >", se invocará este método
    *
    * */
    private void aniadePalabra()
    {
        String palabra = etAniadePalabra.getText().toString();

        if ( !palabra.isEmpty() && !palabras_buscadas.contains( palabra ) )
        {
            palabras_buscadas.add( palabra );
            adaptadorPalabras.notifyDataSetChanged();

            /*
            btLeerSopa.setVisibility( View.VISIBLE ); // con al menos una palabra, ya se puede leer
            // una sopa.
            */
        }

        etAniadePalabra.setText( "" );
    }

    /* Borra de la lista de palabras el Item que se ha pulsado en el ListView */
    private void borrarDeListaPalabras(String palabra)
    {
        palabras_buscadas.remove( palabra );
        adaptadorPalabras.notifyDataSetChanged();

        /*
        if ( palabras_buscadas.isEmpty() )
            btLeerSopa.setVisibility( View.INVISIBLE );
        */
    }

    /* ======================= MÉTODOS DE COMPROBACIÓN DE LA SOPA ======================== */
    /*
    * Método que comprueba si una palabra está contenida en una sopa de letras.
    * Para ello, comprueba fila por fila (izq->dcha y dcha->izq), y columna por columna
    * (arriba->abajo y abajo->arriba)
    * */
    public boolean palabraEstaEnSopa( char[][] sopa, String palabra )
    {
        return (
                compruebaFilas( sopa, palabra ) ||
                        compruebaColumnas( sopa, palabra )
        );
    }

    private boolean compruebaFilas( char[][] sopa, String palabra )
    {
        String cadenaFila = "";

        for(int fila = 0; fila < sopa.length; fila++)
        {
            cadenaFila = new String( sopa[fila] );

            if( cadenaFila.contains( palabra ) || invertirCadena(cadenaFila).contains( palabra ))
            {
                return true;
            }
        }
        return false;
    }

    private boolean compruebaColumnas( char[][] sopa, String palabra )
    {
        String cadenaColumna = "";

        for (int columna=0; columna< sopa[0].length; columna++) // recorro todas las columnas
        {
            for (int fila=0; fila< sopa.length; fila++)
            {
                cadenaColumna += sopa[fila][columna];
            }

            if (cadenaColumna.contains( palabra ) || invertirCadena(cadenaColumna).contains( palabra ))
                return true;

            cadenaColumna = "";
        }

        return false;
    }

    /* Método muy básico para invertir una cadena. P. ej., ABC -> CBA
    * */
    private String invertirCadena( String cadena )
    {
        String cadenaInvertida = "";

        for (int x=cadena.length()-1 ; x>=0 ; x--)
        {
            cadenaInvertida = cadenaInvertida + cadena.charAt(x);
        }

        return cadenaInvertida;
    }


    /* Carga el texto de la imagen leída en la pestaña de "RESULTADO"
    * */
    public boolean cargarSopaLeida()
    {
        txResultado.setText( textoLeido );

        String salto_linea = System.getProperty("line.separator");
        if ( textoLeido.contains(salto_linea) ) // si el texto contiene algún salto de línea
        {
            String[] filas = textoLeido.split("\n"); // filas de la sopa en un String[]
            int longitud_fila = 0;

            for (int i=0; i< filas.length; i++) // se comprueba si la longitud de cada fila de la sopa
            {                                   // es distinta de las demás (si es así, ¡NO es una sopa!).
                // Return false en ese caso.
                if ( i==0 ) //primera línea leída de la sopa
                    longitud_fila = filas[i].length();

                else
                {
                    if ( filas[i].length() != longitud_fila )
                    {
                        if ( (filas[i].length()-longitud_fila) == 1 ) // recorto una letra
                        {
                            filas[i] = filas[i].substring(0, filas[i].length()-1);
                        }

                        else if ( (filas[i].length()-longitud_fila) == -1 ) // añado una letra
                        {
                            filas[i] = filas[i] + filas[i].charAt(
                                                        new Random()
                                                                .nextInt( filas[i].length() ));
                        }

                        else
                        {
                            modificaTextoPalabrasEncontradasYBuscadas("Lo sentimos, pero hay demasiadas " +
                                    "discrepancias con respecto al número de caracteres" +
                                    " leídos por nuestro procesador de imágenes" +
                                    " en las filas de la sopa de letras. Vuelva" +
                                    " a intentarlo, por favor.");

                            return false;
                        }
                    }
                }
            }


            int filas_sopa = filas.length;
            int columnas_sopa = filas[0].length();
            sopa = new char[ filas_sopa ][ columnas_sopa ]; // si la sopa está bien formada,
            // vamos creándola

            for (int i=0; i< filas_sopa; i++) // relleno el array
            {
                for (int j=0; j< columnas_sopa; j++)
                {
                    sopa[i][j] = filas[i].charAt( j );
                }
            }

            for(String palabra : palabras_buscadas) // se buscan las palabras_buscadas que se han
            {                                           // encontrado en la sopa
                if ( !(palabras_encontradas.contains(palabra)) && palabraEstaEnSopa(sopa, palabra) ) {
                    palabras_encontradas.add(palabra);
                }
            }
        }

        else // si el texto leído solo tiene una línea, va por aquí
        {
            sopa = new char[1][ textoLeido.length() ];

            for (int j=0; j< textoLeido.length(); j++) // relleno el array
            {
                sopa[0][ j ] = textoLeido.charAt(j);
            }

            for (String palabra : palabras_buscadas) // busca las palabras_buscadas del
            {                                           // ListView en el char[][] sopa
                if ( !(palabras_encontradas.contains(palabra)) && palabraEstaEnSopa( sopa, palabra ))
                {
                    palabras_encontradas.add( palabra );
                }
            }
        }

        escribePalabrasEncontradas();

        if ( palabras_encontradas.isEmpty() ) // si no se ha encontrado ninguna...
            return false;
        else
            this.palabrasEncontradas = palabras_encontradas.size();

        return true;
    }

    private void modificaTextoPalabrasEncontradasYBuscadas(String text)
    {
        txPalabrasBuscadasEncontradas.setText(text);
    }

    private void escribePalabrasEncontradas( )
    {
        modificaTextoPalabrasEncontradasYBuscadas("");
        String buscadas = "Palabras buscadas: ", encontradas = "Palabras encontradas: Ninguna.";

        for (String palabra_buscada : palabras_buscadas)
        {
            if ( palabras_buscadas.get( palabras_buscadas.size()-1 ) == palabra_buscada ) // si es
            {                                                           // la última palabra,
                // se le quita la coma (',')
                buscadas += palabra_buscada; // última palabra buscada
            }

            else
                buscadas += palabra_buscada + ", "; // estas son las palabras_buscadas buscadas
        }                                           // por el jugador, separadas por comas

        for (String palabra_encontrada : palabras_encontradas)
        {
            if ( palabras_encontradas.get(0) == palabra_encontrada ) // primera palabra de la lista
            {
                encontradas = "Palabras encontradas: " + palabra_encontrada;
            }

            else // las de por medio
                encontradas +=  ", " + palabra_encontrada;
        }

        String palabras_buscadas_encontradas = buscadas + "\n" + encontradas;

        modificaTextoPalabrasEncontradasYBuscadas(palabras_buscadas_encontradas);
    }

    /* Método invocado cuando hay al menos una palabra con la que jugar */
    public void intentaEmpezarJuego()
    {
        etAniadePalabra.setEnabled( false );
        listViewPalabras.setEnabled( false );
        txSeHanEncontrado.setText( "Se han encontrado " + palabrasEncontradas + " palabras " +
                "en la imagen." );

        btAniadePalabra.setText( "Cancelar todo" );
        btAniadePalabra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                restauraEstadoAnterior();
            }
        });

        btLeerSopa.setText("¡Empezar partida!");
        btLeerSopa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                iniciarPartida();
            }
        });
    }

    private void restauraEstadoAnterior()
    {
        etAniadePalabra.setEnabled( true );
        listViewPalabras.setEnabled( true );
        btRecargarPalabrasBuscadas.setVisibility( View.VISIBLE );
        txSeHanEncontrado.setText( "" );
        btAniadePalabra.setText( "Añadir >" );
        btLeerSopa.setText("Leer sopa");
        aniadeListenerAlBotonAniadirPalabra();
        aniadeListenerAlBotonLeerSopa();
    }

    private void iniciarPartida()
    {
        Intent intent = new Intent(getActivity(), SopaActivity.class);
        intent.putExtra("sopa", sopa);
        intent.putStringArrayListExtra("palabras_encontradas", palabras_encontradas);
        startActivity(intent);
    }

    private void recargaPalabrasBuscadas()
    {
        lectorSopa = new AsyncTaskLeerSopa( this );
        lectorSopa.execute();
    }

    /* ==================================================================================
        ================================ MÉTODOS PARA TESSERACT ============================ */

    private void leerSopaDesdeGaleria()
    {
        seleccionarPestaniaDesdeIndice( 5 ); // pestaña 'RESULTADO'
        resultado = viewPager.getRootView(); // cogemos la Vista de 'RESULTADO'
        cargaComponentesPasoResultado();

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult
                (
                        Intent.createChooser(intent, "Seleccione una imagen"),
                        REQUEST_IMAGE_CAPTURE
                );
    }

    private void cargaComponentesPasoResultado()
    {
        imgResultado = (ImageView) resultado.findViewById(R.id.imgResultado);
        txResultado = (TextView) resultado.findViewById(R.id.txDescripcionPRes);
        txPalabrasBuscadasEncontradas = (TextView) resultado.findViewById(R.id.txPalabrasBuscadasEncontradas);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent)
    {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Uri selectedImage;

        switch (requestCode)
        {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == Activity.RESULT_OK) // Si se ha escogido una imagen
                {
                    selectedImage = imageReturnedIntent.getData();
                    String selectedPath=selectedImage.getPath();

                    if (selectedPath != null)
                    {
                        InputStream imageStream = null;
                        try
                        {
                            imageStream = getActivity().getContentResolver().openInputStream(
                                    selectedImage);
                        }
                        catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }

                        // Transformamos la URI de la imagen a inputStream y este a un Bitmap
                        imagen = BitmapFactory.decodeStream(imageStream);
                        // Se coloca la imagen en la pestaña 'RESULTADO'
                        imgResultado.setImageBitmap( imagen );

                        lectorSopa = new AsyncTaskLeerSopa( this );
                        lectorSopa.execute();
                    }
                }
                break;
        }
    }

    public void procesarImagen(final Bitmap imagen)
    {
        tess = new Tesseract(getActivity().getApplicationContext());
        tess.restringeTesseractASoloLetras();

        textoLeido = null;
        palabras_encontradas = new ArrayList<>();

        try
        {
            if (imagen != null)
                textoLeido = tess.getResultado( imagen );
        } catch (RuntimeException re)
        {
            //
        } finally {
            tess.onDestroy();
        }

    }


    public void cargaTextoLeido()
    {
        if (textoLeido != null)
        {
            textoLeido = textoLeido.toUpperCase()
                    .replace(" ", "")
                    .replace("\n\n", "\n");

            if (cargarSopaLeida())
            {
                intentaEmpezarJuego();
            }

            if (imagen != null && btRecargarPalabrasBuscadas.getVisibility() == View.INVISIBLE)
                btRecargarPalabrasBuscadas.setVisibility( View.VISIBLE );
        }

        else
        {
            Toast.makeText(getContext(), "No ha sido posible procesar la imagen.",
                    Toast.LENGTH_SHORT).show();
            escribePalabrasEncontradas();
            txResultado.setText( "" );
        }
    }

    public void enciendeDialogoDeCarga()
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                progreso = new ProgressDialog(getContext(), AlertDialog.THEME_HOLO_DARK);
                progreso.setCancelable(false);
                progreso.setProgressStyle( android.R.style.Widget_ProgressBar_Horizontal );
                progreso.setTitle( "Procesando la imagen" );
                progreso.setMessage( "Espere unos segundos, por favor" );
                progreso.show();
                /*progreso = ProgressDialog.show(getContext(), "Procesando la imagen",
                                                "Espere unos segundos, por favor");
                */
            }
        });
    }
    public void apagaDialogoDeCarga()
    {
        getActivity().runOnUiThread(new Runnable() { // invoca al hilo principal
            @Override
            public void run()
            {
                progreso.dismiss();
            }
        });
    }
}

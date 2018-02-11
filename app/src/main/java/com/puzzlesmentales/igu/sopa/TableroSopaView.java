package com.puzzlesmentales.igu.sopa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.puzzlesmentales.R;
import com.puzzlesmentales.igu.MainActivity;
import com.puzzlesmentales.logic.sopa.SopaLetras;
import com.puzzlesmentales.util.FormatoTiempoJuego;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by Fer on 30/12/2017.
 */

public class TableroSopaView extends View {

    private SopaLetras sopa;

    private int numCelVertical, numCelHorizontal;
    private int posIC,posIF,posFC,posFF = -1;

    private float anchoCelda;
    private float altoCelda;

    private Paint lineaPaint;

    private int puntos;
    private Paint valorCeldaPaint;

    private Paint backgroundColorPulsado;

    private int numeroIzq;
    private int numeroArriba;
    public static final int TABLERO_SIZE_DEFAULT = 100;

    private Point initPalabra,endPalabra;

    private List<Line> lineas;

    private boolean crearLineas;



    public TableroSopaView(Context context) {
        super(context);
    }

    public TableroSopaView(Context context, AttributeSet attrs){
        super(context,attrs);
        lineas = new ArrayList<>();
        setFocusable(true);
        setFocusableInTouchMode(true);
        lineaPaint = new Paint();
        valorCeldaPaint = new Paint();
        backgroundColorPulsado = new Paint();

        valorCeldaPaint.setAntiAlias(true);


        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TableroSudokuView
        );

        setColorLinea(a.getColor(R.styleable.TableroSudokuView_lineColor, Color.BLACK));
        setColorTexto(a.getColor(R.styleable.TableroSudokuView_textColor, Color.BLACK));
        setBackgroundColor(a.getColor(R.styleable.TableroSudokuView_backgroundColor, Color.WHITE));
        setBackgroundColorTouched(a.getColor(R.styleable.TableroSudokuView_backgroundColorTouched, Color.rgb(50, 50, 255)));
        a.recycle();
    }

    public void init(SopaLetras sup){
        init(sup,true);
    }

    public void init(SopaLetras sup,boolean init){
        sopa = sup;
        if(init) {
            sopa.init();
        }else{
            crearLineas = true;
        }
        numCelVertical = sopa.getSopa().length;
        numCelHorizontal = sopa.getSopa()[0].length;
        Log.d("filas:"+numCelVertical, "col:"+numCelHorizontal);
    }

    private void cargarLineas() {
        List<String> added = new ArrayList<>();
        for (int i = 0; i < sopa.getSopa().length; i++) {
            for (int j = 0; j < sopa.getSopa()[i].length; j++) {
                for (int c = 0; c < sopa.getAdivinadas().size(); c++) {
                    String pa = sopa.getAdivinadas().get(c);
                    if (added.contains(pa) || pa.isEmpty()) {
                        continue;
                    }
                    List<SopaLetras.Direction> ds = sopa.disponible(i, j, sopa.getAdivinadas().get(c));
                    if (ds != null && !ds.isEmpty()) {
                        added.add(pa);
                        SopaLetras.Direction d = ds.get(0);
                        PointF init;
                        PointF fin;
                        if (d == SopaLetras.Direction.BOTTOM || d == SopaLetras.Direction.RIGHT) {
                            init = new PointF(j * anchoCelda, i * altoCelda);
                            if(d == SopaLetras.Direction.BOTTOM) {
                                fin = new PointF(j * anchoCelda + anchoCelda,(i+pa.length()) *altoCelda  );
                            }else {
                                fin = new PointF( (j + pa.length()) * anchoCelda , i * altoCelda + altoCelda);
                            }
                        }else{
                            if(d == SopaLetras.Direction.LEFT){
                                init = new PointF((j - pa.length()) * anchoCelda + anchoCelda, i * altoCelda);
                            }else{
                                init = new PointF(j * anchoCelda, (i - pa.length()) * altoCelda+ altoCelda);
                            }
                            fin = new PointF(j * anchoCelda + anchoCelda, i * altoCelda + altoCelda);
                        }
                        lineas.add(new Line(init, fin,backgroundColorPulsado));
                        System.out.println(lineas.get(lineas.size() - 1).toString());
                        backgroundColorPulsado = new Paint();
                        Random r = new Random();
                        backgroundColorPulsado.setARGB(100,r.nextInt(255), r.nextInt(255),r.nextInt(255));
                    }
                }
            }
        }
    }


    public int getPuntos(){return puntos;}
    public void setColorLinea(int color) {
        lineaPaint.setColor(color);
    }
    public void setColorTexto(int color) {
        valorCeldaPaint.setColor(color);
    }
    public void setBackgroundColorTouched(int color) {
        backgroundColorPulsado.setColor(color);
        backgroundColorPulsado.setAlpha(100);
    }


    //  private String ant = "";

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(sopa == null){
            return;
        }

        int ancho = getWidth() - getPaddingRight();
        int alto = getHeight() - getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        int celdaLeft, celdaTop;
        float numeroAscent = valorCeldaPaint.ascent();
        for (int row = 0; row < numCelVertical; row++) {
            for (int col = 0; col < numCelHorizontal; col++) {
                celdaLeft = Math.round((col * anchoCelda) + paddingLeft);
                celdaTop = Math.round((row * altoCelda) + paddingTop);
                canvas.drawText(Character.toString(sopa.getSopa()[row][col]), celdaLeft + numeroIzq,
                        celdaTop + numeroArriba - numeroAscent, valorCeldaPaint);
            }
        }
        // Dibujar lineas verticales
        for (int c = 0; c <= numCelHorizontal; c++) {
            float x = (c * anchoCelda) + paddingLeft;
            canvas.drawLine(x, paddingTop, x, alto, lineaPaint);
        }
        // Dibujar lineas horizontales
        for (int r = 0; r <= numCelVertical; r++) {
            float y = r * altoCelda + paddingTop;
            canvas.drawLine(paddingLeft, y, ancho, y, lineaPaint);
        }
        for(Line l : lineas){
            canvas.drawRect(l.getPrimero().x,l.getPrimero().y,l.getUltimo().x,l.getUltimo().y,l.getPaint());
        }
        if(posIC >= 0 && posIF >= 0 && posFC >= 0 && posFF >= 0){
            Point pos = getCeldaSituadaEn(posIC,posIF);
            Point posf = getCeldaSituadaEn(posFC,posFF);
            if(pos != null && posf != null) {
                Point o1 = new Point(pos.x,posf.y);
                Point o2 = new Point(posf.x,pos.y);

                /*Point ai;
                Point bd;
                if(pos.x <= posf.x && pos.x <= o1.x && pos.x <= o2.x && pos.y <= posf.y && pos.y <= o1.y && pos.y <= o2.y){
                    ai = pos;
                    bd = posf;
                }else if(posf.x <= pos.x && posf.x <= o1.x && posf.x <= o2.x && posf.y <= pos.y && posf.y <= o1.y && posf.y <= o2.y){
                    ai = posf;
                    bd = pos;
                }else if(o1.x <= pos.x && o1.x <= posf.x && o1.x <= o2.x && o1.y <= pos.y && o1.y <= posf.y && o1.y <= o2.y){
                    ai = o1;
                    bd = o2;
                }else{
                    ai = o2;
                    bd = o1;
                }*/
                //Punto opuesto mas cercano
                Point oc;
                double do1 = Math.sqrt(Math.pow(Math.abs(o1.x - posf.x), 2) + Math.pow(Math.abs(o1.y - posf.y), 2));
                double do2 = Math.sqrt(Math.pow(Math.abs(o2.x - posf.x), 2) + Math.pow(Math.abs(o2.y - posf.y), 2));
                if(do1 <  do2){
                    oc = o1;
                }else{
                    oc = o2;
                }
                if(posIC <= posFC && posIF <= posFF) {
                    canvas.drawRect(pos.y * anchoCelda, pos.x * altoCelda, oc.y * anchoCelda + anchoCelda, oc.x * altoCelda + altoCelda, backgroundColorPulsado);
                }else if(posIC >= posFC && posIF <= posFF){
                    //Abajo izquierda
                    canvas.drawRect(oc.y * anchoCelda, pos.x * altoCelda, pos.y * anchoCelda + anchoCelda, oc.x * altoCelda + altoCelda, backgroundColorPulsado);
                }else if(posIC <= posFC && posIF >= posFF){
                    //Arriba derecha
                    canvas.drawRect(pos.y * anchoCelda, oc.x * altoCelda, oc.y * anchoCelda + anchoCelda, pos.x * altoCelda + altoCelda, backgroundColorPulsado);
                }else{
                    //Arriba izquierda
                    canvas.drawRect(oc.y * anchoCelda, oc.x * altoCelda, pos.y * anchoCelda + anchoCelda, pos.x * altoCelda + altoCelda, backgroundColorPulsado);
                }


            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(sopa == null){
            super.onMeasure(widthMeasureSpec,heightMeasureSpec);
            return;
        }
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int ancho = -1, alto = -1;
        if (widthMode == MeasureSpec.EXACTLY) {
            ancho = widthSize;
        } else {
            ancho = TABLERO_SIZE_DEFAULT;
            if (widthMode == MeasureSpec.AT_MOST && ancho > widthSize) {
                ancho = widthSize;
            }
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            alto = heightSize;
        } else {
            alto = TABLERO_SIZE_DEFAULT;
            if (heightMode == MeasureSpec.AT_MOST && alto > heightSize) {
                alto = heightSize;
            }
        }
        if (widthMode != MeasureSpec.EXACTLY) ancho = alto;
        if (heightMode != MeasureSpec.EXACTLY) alto = ancho;
        if (widthMode == MeasureSpec.AT_MOST && ancho > widthSize) ancho = widthSize;
        if (heightMode == MeasureSpec.AT_MOST && alto > heightSize) alto = heightSize;

        anchoCelda = (ancho - getPaddingLeft() - getPaddingRight()) / numCelHorizontal;
        altoCelda = (alto - getPaddingTop() - getPaddingBottom()) / numCelVertical;

        setMeasuredDimension(ancho, alto);

        float cellTextSize = altoCelda * 0.75f;
        valorCeldaPaint.setTextSize(cellTextSize);
        // calcular los offsets en cada celda para centrar el número renderizado
        numeroIzq = (int) ((anchoCelda - valorCeldaPaint.measureText("9")) / 2);
        numeroArriba = (int) ((altoCelda - valorCeldaPaint.getTextSize()) / 2);
        if(crearLineas){
            cargarLineas();
            crearLineas = false;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                posIC = x;
                posIF = y;
                posFC = x;
                posFF = y;
                initPalabra = getCeldaSituadaEn(x,y);
                break;
            case MotionEvent.ACTION_MOVE:
                posFC = x;
                posFF = y;
                break;
            case MotionEvent.ACTION_UP:
                endPalabra = getCeldaSituadaEn(posFC,posFF);
                if(initPalabra != null && endPalabra != null){
                    Point o1 = new Point(initPalabra.x,endPalabra.y);
                    Point o2 = new Point(endPalabra.x,initPalabra.y);
                    Point oc;
                    double do1 = Math.sqrt(Math.pow(Math.abs(o1.x - endPalabra.x), 2) + Math.pow(Math.abs(o1.y - endPalabra.y), 2));
                    double do2 = Math.sqrt(Math.pow(Math.abs(o2.x - endPalabra.x), 2) + Math.pow(Math.abs(o2.y - endPalabra.y), 2));
                    if(do1 <  do2){
                        oc = o1;
                    }else{
                        oc = o2;
                    }
                    PointF init,fin;
                    if(posIC <= posFC && posIF <= posFF) {
                        init = new PointF(initPalabra.y * anchoCelda, initPalabra.x * altoCelda);
                        fin = new PointF( oc.y * anchoCelda + anchoCelda,oc.x * altoCelda + altoCelda);
                    }else if(posIC >= posFC && posIF <= posFF){
                        //Abajo izquierda
                        init = new PointF(oc.y * anchoCelda, initPalabra.x * altoCelda);
                        fin = new PointF( initPalabra.y * anchoCelda + anchoCelda, oc.x * altoCelda + altoCelda);
                    }else if(posIC <= posFC && posIF >= posFF){
                        //Arriba derecha
                        init =  new PointF(initPalabra.y * anchoCelda, oc.x * altoCelda);
                        fin = new PointF( oc.y * anchoCelda + anchoCelda,initPalabra.x * altoCelda + altoCelda);
                    }else{
                        //Arriba izquierda
                        init =  new PointF(oc.y * anchoCelda, oc.x * altoCelda);
                        fin = new PointF(initPalabra.y * anchoCelda + anchoCelda,initPalabra.x * altoCelda + altoCelda);
                    }
                    boolean ver = false;
                    if(init.x == (fin.x- anchoCelda)){
                        ver = true;
                    }
                    boolean atras = false;
                    if(ver){
                        if(posIF > posFF){
                            atras = true;
                        }
                    }else{
                        if(posIC > posFC){
                            atras = true;
                        }
                    }
                    // System.out.println(ver + " " + atras + " " + init.toString() + " " + fin.toString() + " " + anchoCelda);
                    int len = ver ?  Math.round(Math.abs(init.y - fin.y)/altoCelda) : Math.round(Math.abs(init.x - fin.x)/anchoCelda);
                    // System.out.println(len);
                    String s = "";
                    for(int i = 0; i < len;i++){
                        if(atras){
                            i = i * -1;
                        }
                        int inx = Math.round(initPalabra.x);
                        int iny = Math.round(initPalabra.y);
                        //System.out.println(inx + " " + iny);
                        if(ver){
                            s += sopa.getSopa()[inx + i * 1][iny + i * 0];
                        }else{
                            s += sopa.getSopa()[inx + i * 0][iny + i * 1];
                        }
                        if(atras){
                            i = i * -1;
                        }
                    }
                    //System.out.println(s);
                    if(sopa.addAdivinar(s)) {
                        lineas.add(new Line(init,fin,backgroundColorPulsado));
                        if(sopa.acabado()){
                            puntos = 30;
                            FormatoTiempoJuego f = new FormatoTiempoJuego();
                            int min = Integer.parseInt(f.format(sopa.getTiempo()).split(":")[0]);
                            puntos = puntos - min;
                            puntos += sopa.getAdivinadas().size() * 2;
                            puntos = puntos < 0 ? 0 : puntos;
                            SharedPreferences prefs = getContext().getSharedPreferences(MainActivity.PREFERENCIAS, MODE_PRIVATE);
                            int anterioresPuntos = prefs.getInt(MainActivity.PUNTOS,0);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt(MainActivity.PUNTOS,anterioresPuntos+puntos);
                            editor.apply();
                            crearDialogoResuelto().show();
                        }
                        backgroundColorPulsado = new Paint();
                        Random r = new Random();
                        backgroundColorPulsado.setARGB(100,r.nextInt(255), r.nextInt(255),r.nextInt(255));
                    }
                }
                posIC = posIF = posFC = posFF = -1;
                endPalabra = initPalabra = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                posIC = posIF = posFC = posFF = -1;
                endPalabra = initPalabra = null;
                break;
        }
        postInvalidate();
        super.onTouchEvent(event);
        return true;
    }

    @Nullable
    private Point getCeldaSituadaEn(int x, int y) {
        int lx = x - getPaddingLeft();
        int ly = y - getPaddingTop();

        int row = (int) (ly / altoCelda);
        int col = (int) (lx / anchoCelda);

        if (col >= 0 && col < numCelHorizontal
                && row >= 0 && row < numCelVertical) {
            return new Point(row,col);
        } else {
            return null;
        }
    }

    private class Line {
        private PointF pri, ult;
        private Paint paint;

        public Line(PointF pri, PointF ult,Paint paint) {
            this.pri = pri;
            this.ult = ult;
            this.paint = paint;
        }

        public Paint getPaint() {
            return paint;
        }

        public PointF getPrimero() {
            return pri;
        }

        public PointF getUltimo() {
            return ult;
        }

        @Override
        public String toString() {
            return pri.toString() + " " + ult.toString();
        }
    }

    public AlertDialog crearDialogoResuelto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),AlertDialog.THEME_HOLO_DARK);
        SharedPreferences prefs = getContext().getSharedPreferences(MainActivity.PREFERENCIAS, MODE_PRIVATE);
        final int anterioresPuntos = prefs.getInt(MainActivity.PUNTOS,0);
        builder.setTitle("Felicidades")
                .setMessage("Has resuelto la sopa y has ganado " + puntos  + " puntos")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(), "Se han guardado los puntos, ahora tienes: "+anterioresPuntos,Toast.LENGTH_SHORT).show();
                                ((Activity)TableroSopaView.this.getContext()).finish();
                            }
                        });

        return builder.create();
    }

}

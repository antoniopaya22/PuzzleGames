package com.puzzlesmentales.igu.sudoku;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.puzzlesmentales.R;
import com.puzzlesmentales.logic.sudoku.Celda;
import com.puzzlesmentales.logic.sudoku.NotaCelda;
import com.puzzlesmentales.logic.sudoku.SudokuGame;
import com.puzzlesmentales.logic.sudoku.Tablero;

import java.util.Collection;

public class TableroSudokuView extends View {

    private Paint lineaPaint;
    private Paint lineaSectorPaint;
    private Paint valorCeldaPaint;
    private Paint valorCeldaSoloLecturaPaint;
    private Paint notaCeldaPaint;
    private Paint valorCeldaInvalidoPaint;

    private Paint backgroundColorSecondary;
    private Paint backgroundColorLectura;
    private Paint backgroundColorPulsado;
    private Paint backgroundColorSeleccionado;

    private float anchoCelda;
    private float altoCelda;

    private Celda celdaPulsada;
    private Celda celdaSeleccionada;
    private boolean soloLectura = false;
    private boolean colorearErroneos = true;
    private boolean colorearPulsados = true;
    private boolean ocultarPulsados = true;

    public static final int TABLERO_SIZE_DEFAULT = 100;
    private static final int NO_COLOR = 0;

    private SudokuGame game;
    private Tablero tablero;

    private OnTocarCeldaListener onTocarCeldaListener;
    private OnCeldaSeleccionadaListener onCeldaSeleccionadaListener;

    private int numeroIzq;
    private int numeroArriba;
    private float notaArriba;
    private int anchoLineaSector;

    /**
     * Constructor basico de la vista
     * @param context
     */
    public TableroSudokuView(Context context) {
        this(context, null);
    }


    /**
     * Constructor con attributeSet
     * @param context
     * @param attrs
     */
    public TableroSudokuView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setFocusable(true);
        setFocusableInTouchMode(true);

        lineaPaint = new Paint();
        lineaSectorPaint = new Paint();
        valorCeldaPaint = new Paint();
        valorCeldaSoloLecturaPaint = new Paint();
        valorCeldaInvalidoPaint = new Paint();
        notaCeldaPaint = new Paint();
        backgroundColorSecondary = new Paint();
        backgroundColorLectura = new Paint();
        backgroundColorPulsado = new Paint();
        backgroundColorSeleccionado = new Paint();

        valorCeldaPaint.setAntiAlias(true);
        valorCeldaSoloLecturaPaint.setAntiAlias(true);
        valorCeldaInvalidoPaint.setAntiAlias(true);
        notaCeldaPaint.setAntiAlias(true);
        valorCeldaInvalidoPaint.setColor(Color.RED);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TableroSudokuView
        );

        setColorLinea(a.getColor(R.styleable.TableroSudokuView_lineColor, Color.BLACK));
        setColorLineaSector(a.getColor(R.styleable.TableroSudokuView_sectorLineColor, Color.BLACK));
        setColorTexto(a.getColor(R.styleable.TableroSudokuView_textColor, Color.BLACK));
        setColorTextoSoloLectura(a.getColor(R.styleable.TableroSudokuView_textColorReadOnly, Color.BLACK));
        setColorTextoNota(a.getColor(R.styleable.TableroSudokuView_textColorNote, Color.BLACK));
        setBackgroundColor(a.getColor(R.styleable.TableroSudokuView_backgroundColor, Color.WHITE));
        setBackgroundColorSecondary(a.getColor(R.styleable.TableroSudokuView_backgroundColorSecondary, NO_COLOR));
        setBackgroundColorReadOnly(a.getColor(R.styleable.TableroSudokuView_backgroundColorReadOnly, NO_COLOR));
        setBackgroundColorTouched(a.getColor(R.styleable.TableroSudokuView_backgroundColorTouched, Color.rgb(50, 50, 255)));
        setBackgroundColorSelected(a.getColor(R.styleable.TableroSudokuView_backgroundColorSelected, Color.YELLOW));

        a.recycle();
    }

    //Getters
    public int getColorLinea() {
        return this.lineaPaint.getColor();
    }
    public int getColorLineaSector() {
        return this.lineaSectorPaint.getColor();
    }
    public int getColorTexto() {
        return this.valorCeldaPaint.getColor();
    }
    public int getColorTextoSoloLectura() {
        return this.valorCeldaSoloLecturaPaint.getColor();
    }
    public int getColorTextoNota() {
        return this.notaCeldaPaint.getColor();
    }
    public int getBackgroundColorSecondary() {
        return this.backgroundColorSecondary.getColor();
    }
    public int getBackgroundColorReadOnly() {
        return this.backgroundColorLectura.getColor();
    }
    public int getBackgroundColorTouched() {
        return this.backgroundColorPulsado.getColor();
    }
    public int getBackgroundColorSelected() {
        return this.backgroundColorSeleccionado.getColor();
    }
    public Tablero getTablero() {
        return this.tablero;
    }
    public Celda getCeldaSeleccionada() {
        return this.celdaSeleccionada;
    }
    public boolean isReadOnly() {
        return this.soloLectura;
    }
    public boolean getColorearErroneos() {
        return this.colorearErroneos;
    }
    public boolean getColorearPulsados() {
        return this.colorearPulsados;
    }
    public boolean getAutoHideTouchedCellHint() {
        return this.ocultarPulsados;
    }

    //Setters
    public void setColorLinea(int color) {
        lineaPaint.setColor(color);
    }
    public void setColorLineaSector(int color) {
        lineaSectorPaint.setColor(color);
    }
    public void setColorTexto(int color) {
        valorCeldaPaint.setColor(color);
    }
    public void setColorTextoSoloLectura(int color) {
        valorCeldaSoloLecturaPaint.setColor(color);
    }
    public void setColorTextoNota(int color) {
        notaCeldaPaint.setColor(color);
    }
    public void setBackgroundColorSecondary(int color) {
        backgroundColorSecondary.setColor(color);
    }
    public void setBackgroundColorReadOnly(int color) {
        backgroundColorLectura.setColor(color);
    }
    public void setBackgroundColorTouched(int color) {
        backgroundColorPulsado.setColor(color);
        backgroundColorPulsado.setAlpha(100);
    }
    public void setBackgroundColorSelected(int color) {
        backgroundColorSeleccionado.setColor(color);
        backgroundColorSeleccionado.setAlpha(100);
    }
    public void setGame(SudokuGame game) {
        this.game = game;
        setTablero(game.getCeldas());
    }
    public void setTablero(Tablero celdas) {
        tablero = celdas;
        if (tablero != null) {
            if (!soloLectura) {
                celdaSeleccionada = tablero.getCelda(0, 0);
                onCeldaSeleccionada(celdaSeleccionada);
            }
            tablero.addOnChangeListener(new Tablero.OnChangeListener() {
                @Override
                public void onChange() {
                    postInvalidate();
                }
            });
        }
        postInvalidate();
    }
    public void setSoloLectura(boolean readonly) {
        soloLectura = readonly;
        postInvalidate();
    }
    public void setColoresErroneos(boolean a) {
        colorearErroneos = a;
        postInvalidate();
    }
    public void setColoresPulsados(boolean a) {
        colorearPulsados = a;
    }
    public void setAutoHideTouchedCellHint(boolean a) {
        ocultarPulsados = a;
    }
    public void setOnTocarCeldaListener(OnTocarCeldaListener l) {
        onTocarCeldaListener = l;
    }
    public void setOnCeldaSeleccionadaListener(OnCeldaSeleccionadaListener l) {
        onCeldaSeleccionadaListener = l;
    }
    private void setValorCelda(Celda celda, int valor) {
        if (celda.isEditable()) {
            if (game != null) {
                game.setValorCelda(celda, valor);
            } else {
                celda.setValor(valor);
            }
        }
    }
    private void setNotaCelda(Celda celda, NotaCelda nota) {
        if (celda.isEditable()) {
            if (game != null) {
                game.setNotaCelda(celda, nota);
            } else {
                celda.setNota(nota);
            }
        }
    }

    //Metodos

    @Override
    protected void onDraw(Canvas canvas) {
        // Drawable tiene su propio draw() que toma un lienzo como argumento
        super.onDraw(canvas);

        int ancho = getWidth() - getPaddingRight();
        int alto = getHeight() - getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        // Dibujar secondary background
        if (backgroundColorSecondary.getColor() != NO_COLOR) {
            canvas.drawRect(3 * anchoCelda, 0, 6 * anchoCelda, 3 * anchoCelda, backgroundColorSecondary);
            canvas.drawRect(0, 3 * anchoCelda, 3 * anchoCelda, 6 * anchoCelda, backgroundColorSecondary);
            canvas.drawRect(6 * anchoCelda, 3 * anchoCelda, 9 * anchoCelda, 6 * anchoCelda, backgroundColorSecondary);
            canvas.drawRect(3 * anchoCelda, 6 * anchoCelda, 6 * anchoCelda, 9 * anchoCelda, backgroundColorSecondary);
        }

        // Dibujar celdas
        int celdaLeft, celdaTop;
        if (tablero != null) {
            boolean flagColorLecturaBackground = backgroundColorLectura.getColor() != NO_COLOR;
            float numeroAscent = valorCeldaPaint.ascent();
            float notaAscent = notaCeldaPaint.ascent();
            float anchoNota = anchoCelda / 3f;
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    Celda celda = tablero.getCelda(row, col);
                    celdaLeft = Math.round((col * anchoCelda) + paddingLeft);
                    celdaTop = Math.round((row * altoCelda) + paddingTop);
                    //Dibujar en modo solo lectura
                    if (!celda.isEditable() && flagColorLecturaBackground) {
                        if (backgroundColorLectura.getColor() != NO_COLOR) {
                            canvas.drawRect(celdaLeft, celdaTop, celdaLeft + anchoCelda, celdaTop + altoCelda,
                                    backgroundColorLectura);
                        }
                    }
                    // Dibujar Texto Celda
                    int valorCelda = celda.getValor();
                    if (valorCelda != 0) {
                        Paint valCeldaPaint = celda.isEditable() ? valorCeldaPaint : valorCeldaSoloLecturaPaint;
                        if (colorearErroneos && !celda.isValido()) {
                            valCeldaPaint = valorCeldaInvalidoPaint;
                        }
                        canvas.drawText(Integer.toString(valorCelda), celdaLeft + numeroIzq,
                                celdaTop + numeroArriba - numeroAscent, valCeldaPaint);
                    } else {
                        if (!celda.getNota().isEmpty()) {
                            Collection<Integer> numeros = celda.getNota().getNumeros();
                            for (Integer num : numeros) {
                                int n = num - 1;
                                int c = n % 3;
                                int r = n / 3;
                                canvas.drawText(Integer.toString(num), celdaLeft + c * anchoNota + 2, celdaTop + notaArriba - notaAscent + r * anchoNota - 1, notaCeldaPaint);
                            }
                        }
                    }
                }
            }

            // Colorear celda seleccionada
            if (!soloLectura && celdaSeleccionada != null) {
                celdaLeft = Math.round(celdaSeleccionada.getColumnIndex() * anchoCelda) + paddingLeft;
                celdaTop = Math.round(celdaSeleccionada.getRowIndex() * altoCelda) + paddingTop;
                canvas.drawRect(celdaLeft, celdaTop, celdaLeft + anchoCelda, celdaTop + altoCelda, backgroundColorSeleccionado);
            }
            // Resaltar visualmente la célula debajo del dedo
            if (colorearPulsados && celdaPulsada != null) {
                celdaLeft = Math.round(celdaPulsada.getColumnIndex() * anchoCelda) + paddingLeft;
                celdaTop = Math.round(celdaPulsada.getRowIndex() * altoCelda) + paddingTop;
                canvas.drawRect(celdaLeft, paddingTop, celdaLeft + anchoCelda, alto, backgroundColorPulsado);
                canvas.drawRect(paddingLeft, celdaTop, ancho, celdaTop + altoCelda, backgroundColorPulsado);
            }

        }

        // Dibujar lineas verticales
        for (int c = 0; c <= 9; c++) {
            float x = (c * anchoCelda) + paddingLeft;
            canvas.drawLine(x, paddingTop, x, alto, lineaPaint);
        }
        // Dibujar lineas horizontales
        for (int r = 0; r <= 9; r++) {
            float y = r * altoCelda + paddingTop;
            canvas.drawLine(paddingLeft, y, ancho, y, lineaPaint);
        }

        int anchoLineaSector1 = anchoLineaSector / 2;
        int anchoLineaSector2 = anchoLineaSector1 + (anchoLineaSector % 2);

        // Dibujar sectores
        for (int c = 0; c <= 9; c = c + 3) {
            float x = (c * anchoCelda) + paddingLeft;
            canvas.drawRect(x - anchoLineaSector1, paddingTop, x + anchoLineaSector2, alto, lineaSectorPaint);
        }
        for (int r = 0; r <= 9; r = r + 3) {
            float y = r * altoCelda + paddingTop;
            canvas.drawRect(paddingLeft, y - anchoLineaSector1, ancho, y + anchoLineaSector2, lineaSectorPaint);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
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

        anchoCelda = (ancho - getPaddingLeft() - getPaddingRight()) / 9.0f;
        altoCelda = (alto - getPaddingTop() - getPaddingBottom()) / 9.0f;

        setMeasuredDimension(ancho, alto);

        float cellTextSize = altoCelda * 0.75f;
        valorCeldaPaint.setTextSize(cellTextSize);
        valorCeldaSoloLecturaPaint.setTextSize(cellTextSize);
        valorCeldaInvalidoPaint.setTextSize(cellTextSize);
        notaCeldaPaint.setTextSize(altoCelda / 3.0f);
        // calcular los offsets en cada celda para centrar el número renderizado
        numeroIzq = (int) ((anchoCelda - valorCeldaPaint.measureText("9")) / 2);
        numeroArriba = (int) ((altoCelda - valorCeldaPaint.getTextSize()) / 2);
        // añadir alguna compensación porque en algunas resoluciones las notas están cortadas en la parte superior
        notaArriba = altoCelda / 50.0f;
        calcularAnchoLineaSector(ancho, alto);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!soloLectura) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    celdaPulsada = getCeldaSituadaEn(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    celdaSeleccionada = getCeldaSituadaEn(x, y);
                    invalidate();
                    if (celdaSeleccionada != null) {
                        onTocarCelda(celdaSeleccionada);
                        onCeldaSeleccionada(celdaSeleccionada);
                    }
                    if (ocultarPulsados) {
                        celdaPulsada = null;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    celdaPulsada = null;
                    break;
            }
            postInvalidate();
        }

        return !soloLectura;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!soloLectura) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    return moverCeldaSeleccionada(0, -1);
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    return moverCeldaSeleccionada(1, 0);
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    return moverCeldaSeleccionada(0, 1);
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    return moverCeldaSeleccionada(-1, 0);
                case KeyEvent.KEYCODE_0:
                case KeyEvent.KEYCODE_SPACE:
                case KeyEvent.KEYCODE_DEL:
                    // Limpiar el valor de la celda seleccionada
                    if (celdaSeleccionada != null) {
                        if (event.isShiftPressed() || event.isAltPressed()) {
                            setNotaCelda(celdaSeleccionada, NotaCelda.VACIA);
                        } else {
                            setValorCelda(celdaSeleccionada, 0);
                            moverCeldaSeleccionadaADerecha();
                        }
                    }
                    return true;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    if (celdaSeleccionada != null) {
                        onTocarCelda(celdaSeleccionada);
                    }
                    return true;
            }

            if (keyCode >= KeyEvent.KEYCODE_1 && keyCode <= KeyEvent.KEYCODE_9) {
                int numSel = keyCode - KeyEvent.KEYCODE_0;
                Celda cell = celdaSeleccionada;

                if (event.isShiftPressed() || event.isAltPressed()) {
                    // Añadir o eliminar numeros en las notas de las celdas
                    setNotaCelda(cell, cell.getNota().anotarNumero(numSel));
                } else {
                    // Introducir un numero en una celda
                    setValorCelda(cell, numSel);
                    moverCeldaSeleccionadaADerecha();
                }
                return true;
            }
        }
        return false;
    }

    protected void onTocarCelda(Celda celda) {
        if (onTocarCeldaListener != null)
            onTocarCeldaListener.onCellTapped(celda);
    }
    public void ocultarCeldaTocada() {
        celdaPulsada = null;
        postInvalidate();
    }
    protected void onCeldaSeleccionada(Celda celda) {
        if (onCeldaSeleccionadaListener != null)
            onCeldaSeleccionadaListener.onCellSelected(celda);
    }

    private void calcularAnchoLineaSector(int pxAncho, int pxAlto) {
        int pxSize = pxAncho < pxAlto ? pxAncho : pxAlto;
        float escala = getContext().getResources().getDisplayMetrics().density;
        float size = pxSize / escala;
        float anchoLineaSectorEscala = 2.0f;

        if (size > 150) {
            anchoLineaSectorEscala = 3.0f;
        }
        anchoLineaSector = (int) (anchoLineaSectorEscala * escala);
    }

    /**
     * Mueve la celda seleccionada por una celda a la derecha.
     * Si se alcanza el borde, la selección salta al comienzo de otra línea.
     */
    public void moverCeldaSeleccionadaADerecha() {
        if (!moverCeldaSeleccionada(1, 0)) {
            int selRow = celdaSeleccionada.getRowIndex();
            selRow++;
            if (!moverCeldaSeleccionadaA(selRow, 0)) {
                moverCeldaSeleccionadaA(0, 0);
            }
        }
        postInvalidate();
    }
    /**
     * Movimientos seleccionados por las celdas vx hacia la derecha y hacia abajo.
     * vx y vy pueden ser negativos.
     * Devuelve true, si se selecciona una nueva celda.
     *
     * @param vx Horizontal desplazamiento
     * @param vy Vertical desplazamiento
     */
    private boolean moverCeldaSeleccionada(int vx, int vy) {
        int newRow = 0;
        int newCol = 0;
        if (celdaSeleccionada != null) {
            newRow = celdaSeleccionada.getRowIndex() + vy;
            newCol = celdaSeleccionada.getColumnIndex() + vx;
        }
        return moverCeldaSeleccionadaA(newRow, newCol);
    }


    /**
     * Mueve la celda seleccionada dada por fila y columna
     *
     * @param row fila de la celda que se va a saleccionar
     * @param col columna de la celda que se va a seleccionar
     * @return True si se selecciona correctamente
     */
    private boolean moverCeldaSeleccionadaA(int row, int col) {
        if (col >= 0 && col < Tablero.SUDOKU_SIZE && row >= 0 && row < Tablero.SUDOKU_SIZE) {
            celdaSeleccionada = tablero.getCelda(row, col);
            onCeldaSeleccionada(celdaSeleccionada);
            postInvalidate();
            return true;
        }
        return false;
    }

    //Listeners
    public interface OnTocarCeldaListener {
        void onCellTapped(Celda celda);
    }
    public interface OnCeldaSeleccionadaListener {
        void onCellSelected(Celda celda);
    }

    /**
     * Devuelve la celda de unas cordenadas dadas por parametro, null en caso contrario
     * @param x
     * @param y
     * @return
     */
    @Nullable
    private Celda getCeldaSituadaEn(int x, int y) {
        int lx = x - getPaddingLeft();
        int ly = y - getPaddingTop();

        int row = (int) (ly / altoCelda);
        int col = (int) (lx / anchoCelda);

        if (col >= 0 && col < Tablero.SUDOKU_SIZE
                && row >= 0 && row < Tablero.SUDOKU_SIZE) {
            return tablero.getCelda(row, col);
        } else {
            return null;
        }
    }

}

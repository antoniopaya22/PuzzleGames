package com.puzzlesmentales.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.puzzlesmentales.logic.sopa.SopaLetras;
import com.puzzlesmentales.logic.sudoku.SudokuGame;
import com.puzzlesmentales.logic.sudoku.Tablero;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase que facilita los accesos a la base de datos mediante JDBC
 * @author  Antonio Paya Gonzalez
 */

public class GestorDB {

    private static BaseDatos connection;
    private static SQLiteDatabase db;

    public static boolean guardarTableroSudoku(SudokuGame game, Tablero tableroOrgininal, String ruta, String nombre)
            throws SQLException{
        String sql = "INSERT INTO SUDOKUS(Fila1,Fila2,Fila3,Fila4,Fila5,Fila6,Fila7,Fila8,Fila9,Tiempo,Nombre) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        SQLiteStatement addSudoku = db.compileStatement(sql);
        addSudoku.bindString(1,getStringFromFila(game,0,tableroOrgininal));
        addSudoku.bindString(2,getStringFromFila(game,1,tableroOrgininal));
        addSudoku.bindString(3,getStringFromFila(game,2,tableroOrgininal));
        addSudoku.bindString(4,getStringFromFila(game,3,tableroOrgininal));
        addSudoku.bindString(5,getStringFromFila(game,4,tableroOrgininal));
        addSudoku.bindString(6,getStringFromFila(game,5,tableroOrgininal));
        addSudoku.bindString(7,getStringFromFila(game,6,tableroOrgininal));
        addSudoku.bindString(8,getStringFromFila(game,7,tableroOrgininal));
        addSudoku.bindString(9,getStringFromFila(game,8,tableroOrgininal));
        addSudoku.bindLong(10,game.getTiempo());
        addSudoku.bindString(11,nombre);
        addSudoku.execute();
        addSudoku.close();
        return true;
    }

    public static boolean guardarTableroSopa(SopaLetras sop, String packageName, String name) throws SQLException{
        String sql = "INSERT INTO SOPAS(Sopa,Palabras,Adivinadas,Tiempo,Nombre) VALUES (?,?,?,?,?)";
        SQLiteStatement addSudoku = db.compileStatement(sql);
        addSudoku.bindString(1,getStringFromSopa(sop));
        addSudoku.bindString(2,getStringFromPalabrasSopa(sop,true));
        addSudoku.bindString(3,getStringFromPalabrasSopa(sop,false));
        addSudoku.bindLong(4,sop.getTiempo());
        addSudoku.bindString(5,name);
        addSudoku.execute();
        addSudoku.close();
        return true;
    }

    public static void deleteSudoku(String name){
        String sql = "DELETE FROM Sudokus WHERE Nombre = ?";
        SQLiteStatement addSudoku = db.compileStatement(sql);
        addSudoku.bindString(1,name);
        addSudoku.execute();
        addSudoku.close();
    }

    public static void deleteSopa(String name){
        String sql = "DELETE FROM Sopas WHERE Nombre = ?";
        SQLiteStatement addSudoku = db.compileStatement(sql);
        addSudoku.bindString(1,name);
        addSudoku.execute();
        addSudoku.close();
    }

    private static String getStringFromPalabrasSopa(SopaLetras sop,boolean adiv) {
        String cadena = "";
        if(adiv){
            for (int i = 0; i< sop.getColocadas().size(); i++){
                cadena+=sop.getColocadas().get(i)+",";
            }
            return cadena;
        }
        else{
            for (int i = 0; i< sop.getAdivinadas().size(); i++){
                cadena+=sop.getAdivinadas().get(i)+",";
            }
            return cadena;
        }
    }

    private static String getStringFromSopa(SopaLetras sop) {
        String cadena = "";
        for (int i = 0; i< sop.getSopa().length; i++){
            for (int j=0; j<sop.getSopa()[i].length; j++){
                cadena+= sop.getSopa()[i][j];
            }
            cadena+=",";
        }
        return cadena;
    }

    public static Map<String,SudokuGame> getAllSudokus() throws SQLException {
        Map<String,SudokuGame> games = new HashMap<String, SudokuGame>();
        String sql = "SELECT * FROM Sudokus";
        Cursor rs = db.rawQuery(sql,null);
        if (rs.moveToFirst()) {
            do {
                String[] columnNames = rs.getColumnNames();
                String nombre = rs.getString(11);
                games.put(nombre,getSudokuFromNombre(nombre));
            } while(rs.moveToNext());
        }
        rs.close();
        return games;
    }

    public static Map<String,SopaLetras> getAllSopas() throws SQLException{
        Map<String,SopaLetras> games = new HashMap<String, SopaLetras>();
        String sql = "SELECT * FROM Sopas";
        Cursor rs = db.rawQuery(sql,null);
        if (rs.moveToFirst()) {
            do {
                String[] columnNames = rs.getColumnNames();
                String nombre = rs.getString(5);
                games.put(nombre,getSopaFromNombre(nombre));
            } while(rs.moveToNext());
        }
        rs.close();
        return games;
    }

    public static SopaLetras getSopaFromNombre(String nombre) throws SQLException{
        SopaLetras sopaLetras = null;
        char[][] sopaTabl;
        String sql = "SELECT * FROM Sopas WHERE nombre = '"+nombre+"'";
        Cursor rs = db.rawQuery(sql,null);
        if (rs.moveToFirst()) {
            do {
                String[] tablero = rs.getString(1).split(",");
                    sopaTabl = new char[tablero.length][tablero[0].length()];
                for (int i = 0; i< tablero.length; i++){
                    for(int j = 0; j<tablero[i].length(); j++){
                        sopaTabl[i][j] = tablero[i].charAt(j);
                    }
                }
                List<String> palabras = new ArrayList<>();
                String[] pal = rs.getString(2).split(",");
                for (int i = 0; i< pal.length; i++){
                    palabras.add(pal[i]);
                }
                sopaLetras = new SopaLetras(sopaTabl,palabras);
                sopaLetras.setColocadas(palabras);
                sopaLetras.setTiempo(rs.getLong(4));
                palabras = new ArrayList<>();
                pal = rs.getString(3).split(",");
                for (int i = 0; i< pal.length; i++){
                    if(!pal[i].isEmpty()) {
                        palabras.add(pal[i]);
                    }
                }
                sopaLetras.setAdivinadas(palabras);
            } while(rs.moveToNext());
        }
        rs.close();
        return sopaLetras;
    }

    public static SudokuGame getSudokuFromNombre(String nombre) throws SQLException {
        int[][] tablero = new int[9][9];
        SudokuGame game = null;
        String sql = "SELECT * FROM Sudokus WHERE nombre = '"+nombre+"'";
        Cursor rs = db.rawQuery(sql,null);
        if (rs.moveToFirst()) {
            do {
                for (int i = 0; i< tablero.length; i++)
                    for (int j = 0; j< tablero.length; j++){
                        if(! Character.isDigit(rs.getString(i+1).toCharArray()[j]))
                            tablero[i][j] = getNumeroFromString(rs.getString(i+1).toCharArray()[j]);
                    }
                Tablero t = Tablero.createGameFromArray(tablero);
                game = SudokuGame.crearSudokuConTablero(t);
                for (int i = 0; i< tablero.length; i++)
                    for (int j = 0; j< tablero.length; j++){
                        if(Character.isDigit(rs.getString(i+1).toCharArray()[j])){
                            game.setValorCelda(game.getCeldas().getCelda(i,j)
                                    ,getNumeroFromString(rs.getString(i+1).toCharArray()[j]));
                            game.getCeldas().getCelda(i,j).setEditable(true);
                        }
                    }
                game.setTiempo(rs.getLong(10));
            } while(rs.moveToNext());
        }
        rs.close();
        return game;
    }

    private static int getNumeroFromString(char f) {
        if(Character.isDigit(f))
            return  Character.getNumericValue(f);
        else {
            switch (f) {
                case 'A':
                    return 1;
                case 'B':
                    return 2;
                case 'C':
                    return 3;
                case 'D':
                    return 4;
                case 'E':
                    return 5;
                case 'F':
                    return 6;
                case 'G':
                    return 7;
                case 'H':
                    return 8;
                case 'I':
                    return 9;
            }
            return 0;
        }
    }

    private static String getStringFromFila(SudokuGame game,int fila,Tablero tableroOriginal){
        String cadena = "";
        for (int i = 0; i < 9; i++){
            if(game.getCeldas().getCelda(fila,i).getValor() == 0) cadena+="-";
            else if(tableroOriginal.getCelda(fila,i).getValor() == 0)
                cadena += game.getCeldas().getCelda(fila,i).getValor();
            else
                cadena += getLetraParaNum(game.getCeldas().getCelda(fila,i).getValor());
        }
        return cadena;
    }

    private static String getLetraParaNum(int num){
        switch (num){
            case 1:
                return "A";
            case 2:
                return "B";
            case 3:
                return "C";
            case 4:
                return "D";
            case 5:
                return "E";
            case 6:
                return "F";
            case 7:
                return "G";
            case 8:
                return "H";
            case 9:
                return "I";
        }
        return "";
    }

    public static void startConexion(String ruta, Context context){
        connection = new BaseDatos(context, "database", null,1);
        db = connection.getReadableDatabase();
    }

    public static void cerrarConexion(){
        connection.close();
    }

}

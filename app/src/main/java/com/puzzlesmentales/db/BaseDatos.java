package com.puzzlesmentales.db;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author  Antonio Paya Gonzalez
 */
public class BaseDatos extends SQLiteOpenHelper {

    String sqlCreateSudokus = "CREATE TABLE Sudokus (" +
            "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
            "Fila1 TEXT," +
            "Fila2 TEXT," +
            "Fila3 TEXT," +
            "Fila4 TEXT," +
            "Fila5 TEXT," +
            "Fila6 TEXT," +
            "Fila7 TEXT," +
            "Fila8 TEXT," +
            "Fila9 TEXT," +
            "Tiempo INTEGER," +
            "Nombre TEXT" +
            ");";
    String sqlCreateSopas = "CREATE TABLE Sopas ("+
            "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
            "Sopa TEXT," +
            "Palabras TEXT," +
            "Adivinadas TEXT," +
            "Tiempo INTEGER," +
            "Nombre TEXT" +
            ");";

    public BaseDatos(Context contexto, String nombre,
                                CursorFactory factory, int version) {
        super(contexto, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreateSudokus);
        db.execSQL(sqlCreateSopas);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        db.execSQL("DROP TABLE IF EXISTS Sudokus");
        db.execSQL("DROP TABLE IF EXISTS Sopas");
        db.execSQL(sqlCreateSudokus);
        db.execSQL(sqlCreateSopas);
    }

}
package com.puzzlesmentales.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Tesseract
{
    private final TessBaseAPI tess;
    private String datapath;
    private final static String LENGUAJE = "spa";

    public Tesseract(Context context)
    {
        tess = new TessBaseAPI();
        datapath = context.getFilesDir().getAbsolutePath() + "/tesseract/";

        checkFile( context, new File(datapath + "tessdata/") );

        tess.init(datapath, LENGUAJE);
    }

    public void restringeTesseractASoloLetras()
    {
        /*
        //tess.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);
        tess.setVariable("tessedit_char_whitelist", "123456789");
        tess.setVariable("tessedit_create_hocr", "1");
        */
        tess.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
    }

    public String getResultado(Bitmap imagen) throws RuntimeException
    {
        tess.setImage( imagen );
        return tess.getUTF8Text();
    }
    public void onDestroy()
    {
        if (tess != null)
            tess.end();
    }

    private void checkFile(Context context, File dir) {
        //directorio no existe, pues se crea
        if (!dir.exists() && dir.mkdirs())
        {
            copiarArchivos( context );
        }

        // directorio existe pero no tiene el spa.traineddata
        if(dir.exists())
        {
            String datafilepath = datapath+ "/tessdata/spa.traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists())
            {
                copiarArchivos( context );
            }
        }
    }

    private void copiarArchivos(Context context )
    {
        AssetManager assetManager = context.getAssets();
        String filepath = datapath + "/tessdata/spa.traineddata";

        try
        {
            InputStream in = assetManager.open("spa.traineddata"); // abrimos el spa.traineddata de la carpeta Assets
            OutputStream out = new FileOutputStream( filepath ); // abrimos la ruta donde queremos meter
            // el spa.traineddata
            byte[] buffer = new byte[1024];
            int read = in.read(buffer);
            while (read != -1) { // copiamos el archivo binario bit a bit
                out.write(buffer, 0, read);
                read = in.read(buffer);
            }

            out.close();
            in.close();
        } catch (Exception e) {
            e.getMessage();
        }
    }

}

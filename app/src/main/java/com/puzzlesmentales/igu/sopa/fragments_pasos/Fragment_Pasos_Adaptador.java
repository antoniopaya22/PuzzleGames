package com.puzzlesmentales.igu.sopa.fragments_pasos;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class Fragment_Pasos_Adaptador extends FragmentPagerAdapter {
    final int PAGE_COUNT = 6;
    private String tabTitles[] = new String[]{"Paso 1", "Paso 2", "Consejo", "Paso 3", "Paso 4", "Resultado"};
    private Context context;
    private int pestania_seleccionada = 0;

    public Fragment_Pasos_Adaptador(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    public int getPestania_seleccionada()
    {
        return pestania_seleccionada;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch ( position )
        {
            case 0:
                fragment = new Fragment_Paso_1();
                pestania_seleccionada = 0;
                break;

            case 1:
                fragment = new Fragment_Paso_2();
                pestania_seleccionada = 1;
                break;

            case 2:
                fragment = new Fragment_Paso_Consejo();
                pestania_seleccionada = 2;
                break;

            case 3:
                fragment = new Fragment_Paso_3();
                pestania_seleccionada = 3;
                break;

            case 4:
                fragment = new Fragment_Paso_4();
                pestania_seleccionada = 4;
                break;

            case 5:
                fragment = new Fragment_Paso_Resultado();
                pestania_seleccionada = 5;
                break;
        }

        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return tabTitles[position];
    }
}
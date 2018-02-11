package com.puzzlesmentales.igu.sopa;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.puzzlesmentales.R;
import com.puzzlesmentales.igu.MainActivity;

import static android.content.Context.MODE_PRIVATE;

public class JugarSopaConfigFragment extends Fragment {

    Spinner spDificultad;
    Switch swPersonalizar;
    Spinner numHuecos;
    Switch swTiempoLimite;
    Button btJugar;
    TextView txtNumHuecos,txtDificultad;
    Spinner temaSpin;
    TextView txtTam;

    public JugarSopaConfigFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_jugar_sopa_config, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        spDificultad =  (Spinner) rootView.findViewById(R.id.spDificultad);
        swPersonalizar = (Switch) rootView.findViewById(R.id.switchDificultad);
        numHuecos = (Spinner) rootView.findViewById(R.id.spNumHuecos);
        temaSpin = (Spinner) rootView.findViewById(R.id.spTamano);
        swTiempoLimite = (Switch) rootView.findViewById(R.id.switchTiempoLimite);
        btJugar = (Button) rootView.findViewById(R.id.btJugar);
        txtNumHuecos = (TextView)rootView.findViewById(R.id.txtNumHuecos);
        txtDificultad = (TextView)rootView.findViewById(R.id.txtDificultad);
        txtTam = (TextView)rootView.findViewById(R.id.txtTamano);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.sp_dificultad, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDificultad.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(),
                R.array.cantidad_palabras, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numHuecos.setAdapter(adapter2);


        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(getActivity(),
                R.array.tamano_sopa, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        temaSpin.setAdapter(adapter3);

        txtNumHuecos.setVisibility(View.INVISIBLE);
        numHuecos.setVisibility(View.INVISIBLE);
        swTiempoLimite.setVisibility(View.INVISIBLE);
        txtTam.setVisibility(View.INVISIBLE);
        temaSpin.setVisibility(View.INVISIBLE);

        swPersonalizar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    spDificultad.setEnabled(false);
                    txtDificultad.setEnabled(false);
                    txtNumHuecos.setVisibility(View.VISIBLE);
                    numHuecos.setVisibility(View.VISIBLE);
                    swTiempoLimite.setVisibility(View.VISIBLE);
                    temaSpin.setVisibility(View.VISIBLE);
                    txtTam.setVisibility(View.VISIBLE);
                }else{
                    spDificultad.setEnabled(true);
                    txtDificultad.setEnabled(true);
                    txtTam.setVisibility(View.INVISIBLE);
                    txtNumHuecos.setVisibility(View.INVISIBLE);
                    numHuecos.setVisibility(View.INVISIBLE);
                    swTiempoLimite.setVisibility(View.INVISIBLE);
                    temaSpin.setVisibility(View.INVISIBLE);
                }
            }
        });

        btJugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(swPersonalizar.isChecked()){
                    Intent intent = new Intent(getActivity(), SopaActivity.class);
                    intent.putExtra("numPalabras", (String) numHuecos.getSelectedItem());
                    intent.putExtra("tiempo", swTiempoLimite.isChecked());
                    intent.putExtra("tema",(String) temaSpin.getSelectedItem());
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getActivity(), SopaActivity.class);
                    switch ((String) spDificultad.getSelectedItem()){
                        case "Fácil":
                            intent.putExtra("numPalabras", "Pocas");

                            break;
                        case "Media":
                            intent.putExtra("numPalabras","Medias");
                            break;
                        case "Dificil":
                            intent.putExtra("numPalabras", "Muchas");
                            break;
                    }
                    intent.putExtra("tema", "Variados");
                    intent.putExtra("tiempo", true);
                    startActivity(intent);
                }
            }
        });
        return rootView;
    }




}

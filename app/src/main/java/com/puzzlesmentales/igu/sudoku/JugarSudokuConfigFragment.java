package com.puzzlesmentales.igu.sudoku;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.puzzlesmentales.R;
import com.puzzlesmentales.igu.MainActivity;

import static android.content.Context.MODE_PRIVATE;

public class JugarSudokuConfigFragment extends Fragment {

    Spinner spDificultad;
    Switch swPersonalizar;
    Spinner numHuecos;
    Switch swTiempoLimite;
    Button btJugar;
    TextView txtNumHuecos,txtDificultad;

    public JugarSudokuConfigFragment() {
    }

    public static JugarSudokuConfigFragment newInstance() {
        JugarSudokuConfigFragment fragment = new JugarSudokuConfigFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_jugar_sudoku_config, container, false);
        spDificultad = (Spinner) rootView.findViewById(R.id.spDificultad);
        swPersonalizar = (Switch) rootView.findViewById(R.id.switchDificultad);
        numHuecos = (Spinner) rootView.findViewById(R.id.spNumHuecos);
        swTiempoLimite = (Switch) rootView.findViewById(R.id.switchTiempoLimite);
        btJugar = (Button) rootView.findViewById(R.id.btJugar);
        txtNumHuecos = (TextView) rootView.findViewById(R.id.txtNumHuecos);
        txtDificultad = (TextView) rootView.findViewById(R.id.txtDificultad);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.sp_dificultad, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDificultad.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(),
                R.array.sp_huecos, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numHuecos.setAdapter(adapter2);

        txtNumHuecos.setVisibility(View.INVISIBLE);
        numHuecos.setVisibility(View.INVISIBLE);
        swTiempoLimite.setVisibility(View.INVISIBLE);

        swPersonalizar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    spDificultad.setEnabled(false);
                    txtDificultad.setEnabled(false);
                    txtNumHuecos.setVisibility(View.VISIBLE);
                    numHuecos.setVisibility(View.VISIBLE);
                    swTiempoLimite.setVisibility(View.VISIBLE);
                } else {
                    spDificultad.setEnabled(true);
                    txtDificultad.setEnabled(true);
                    txtNumHuecos.setVisibility(View.INVISIBLE);
                    numHuecos.setVisibility(View.INVISIBLE);
                    swTiempoLimite.setVisibility(View.INVISIBLE);
                }
            }
        });

        btJugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swPersonalizar.isChecked()) {
                    Intent intent = new Intent(getActivity(), SudokuActivity.class);
                    intent.putExtra("cargada", false);
                    intent.putExtra("numHuecos", (String) numHuecos.getSelectedItem());
                    intent.putExtra("tiempo", swTiempoLimite.isChecked());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), SudokuActivity.class);
                    intent.putExtra("cargada", false);
                    switch ((String) spDificultad.getSelectedItem()) {
                        case "Fácil":
                            intent.putExtra("numHuecos", "30");
                            break;
                        case "Media":
                            intent.putExtra("numHuecos", "40");
                            break;
                        case "Dificil":
                            intent.putExtra("numHuecos", "50");
                            break;
                    }

                    intent.putExtra("tiempo", true);
                    startActivity(intent);
                }
            }
        });
        return rootView;
    }


}

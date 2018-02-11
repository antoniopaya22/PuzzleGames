package com.puzzlesmentales.igu.sopa.fragments_pasos;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.puzzlesmentales.R;


public class Fragment_Paso_2 extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private TextView txDescP2;

    //private OnFragmentInteractionListener mListener;

    public Fragment_Paso_2() {
    }

    public static Fragment_Paso_2 newInstance(String param1, String param2) {
        Fragment_Paso_2 fragment = new Fragment_Paso_2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_paso_2, container,
                false);

        txDescP2 = (TextView) rootView.findViewById( R.id.txDescripcionP2 );
        hacerScrollable();

        return rootView;
    }

    private void hacerScrollable()
    {
        txDescP2.setMovementMethod(new ScrollingMovementMethod());
    }
}

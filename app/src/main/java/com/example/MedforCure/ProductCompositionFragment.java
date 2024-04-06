package com.example.MedforCure;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



public class ProductCompositionFragment extends Fragment {

    private TextView compositionBody;
    public String ProductComposition;


    public ProductCompositionFragment() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_product_composition, container, false);
        compositionBody=view.findViewById(R.id.tv_product_composition);
        compositionBody.setText(ProductComposition);
        return view;
    }
}
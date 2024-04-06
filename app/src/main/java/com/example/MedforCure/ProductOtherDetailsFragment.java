package com.example.MedforCure;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.api.Distribution;

import java.util.ArrayList;
import java.util.List;


public class  ProductOtherDetailsFragment extends Fragment {



    public ProductOtherDetailsFragment() {
        // Required empty public constructor
    }

    private RecyclerView productOtherDetailsRecyclerView;
    public List<Product_Other_Details_Model> product_other_details_modelList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_product_other_details, container, false);
        productOtherDetailsRecyclerView = view.findViewById(R.id.product_other_details_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        productOtherDetailsRecyclerView.setLayoutManager(linearLayoutManager);

        /*
        product_other_details_modelList.add(new Product_Other_Details_Model(0,"Brand Details"));
        product_other_details_modelList.add(new Product_Other_Details_Model(1,"Brand","Cipla"));
        product_other_details_modelList.add(new Product_Other_Details_Model(1,"Brand","Cipla"));
        product_other_details_modelList.add(new Product_Other_Details_Model(1,"Brand","Cipla"));
        product_other_details_modelList.add(new Product_Other_Details_Model(1,"Brand","Cipla"));
        product_other_details_modelList.add(new Product_Other_Details_Model(0,"Not For Sale only CSD"));
        product_other_details_modelList.add(new Product_Other_Details_Model(1,"Brand","Gama"));
        product_other_details_modelList.add(new Product_Other_Details_Model(1,"Brand","Gama"));
        product_other_details_modelList.add(new Product_Other_Details_Model(1,"Brand","Gama"));
        product_other_details_modelList.add(new Product_Other_Details_Model(1,"Brand","Gama"));
        product_other_details_modelList.add(new Product_Other_Details_Model(1,"Brand","Gama"));
        product_other_details_modelList.add(new Product_Other_Details_Model(1,"Brand","Gama"));
        product_other_details_modelList.add(new Product_Other_Details_Model(0,"Brand Details"));
        product_other_details_modelList.add(new Product_Other_Details_Model(1,"Brand","Cipla"));
        product_other_details_modelList.add(new Product_Other_Details_Model(1,"Brand","Cipla"));
        product_other_details_modelList.add(new Product_Other_Details_Model(1,"Brand","Cipla"));
        product_other_details_modelList.add(new Product_Other_Details_Model(1,"Brand","Cipla"));
        product_other_details_modelList.add(new Product_Other_Details_Model(0,"Not For Sale only CSD"));
        product_other_details_modelList.add(new Product_Other_Details_Model(1,"Brand","Gama"));
        product_other_details_modelList.add(new Product_Other_Details_Model(1,"Brand","Gama"));
        product_other_details_modelList.add(new Product_Other_Details_Model(1,"Brand","Gama"));
        product_other_details_modelList.add(new Product_Other_Details_Model(1,"Brand","Gama"));
        product_other_details_modelList.add(new Product_Other_Details_Model(1,"Brand","Gama"));
        product_other_details_modelList.add(new Product_Other_Details_Model(1,"Brand","Gama"));
*/

        Product_Other_Details_Adapter product_other_details_adapter = new Product_Other_Details_Adapter(product_other_details_modelList);
        productOtherDetailsRecyclerView.setAdapter(product_other_details_adapter);
        product_other_details_adapter.notifyDataSetChanged();
        return view;
    }
}
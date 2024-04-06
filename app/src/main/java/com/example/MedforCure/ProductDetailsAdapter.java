package com.example.MedforCure;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class ProductDetailsAdapter extends FragmentPagerAdapter {
    private int totalTabs;

    private String productComposition;
    private List<Product_Other_Details_Model> product_other_details_modelList;

    public ProductDetailsAdapter(@NonNull FragmentManager fm, int totalTabs, String productComposition,List<Product_Other_Details_Model> product_other_details_modelList) {
        super(fm);
        this.productComposition = productComposition;
        this.product_other_details_modelList = product_other_details_modelList;
        this.totalTabs=totalTabs;
    }


    @NonNull
    @Override
    public Fragment getItem(int i) {
        switch (i)
        {
            case 0:
                ProductCompositionFragment productCompositionFragment = new ProductCompositionFragment();
                productCompositionFragment.ProductComposition=productComposition;
                return productCompositionFragment;
            case 1:
                ProductOtherDetailsFragment productOtherDetailsFragment = new ProductOtherDetailsFragment();
                productOtherDetailsFragment.product_other_details_modelList=product_other_details_modelList;
                return productOtherDetailsFragment;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}

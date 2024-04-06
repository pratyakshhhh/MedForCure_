package com.example.MedforCure;

import java.util.List;

public class HomePageModel {
    private int type;
    private String backGroundColor;
    public static final int BANNER_SLIDER =0;
    public static final int STRIP_AD_BANNER =1;
    public static final int HORIZONTAL_PRODUCT_VIEW =2;
    public static final int GRID_PRODUCT_VIEW =3;



    //////////Banner Slider
    private List<SliderModel> SliderModelList;
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<SliderModel> getSliderModelList() {
        return SliderModelList;
    }

    public void setSliderModelList(List<SliderModel> sliderModelList) {
        SliderModelList = sliderModelList;
    }

    public HomePageModel(int type, List<SliderModel> sliderModelList) {
        this.type = type;
        SliderModelList = sliderModelList;
    }
    //////////Banner Slider

    //////////Strip Ad
    private String resource;

    public HomePageModel(int type, String resource,String backGroundColor) {
        this.type = type;
        this.backGroundColor = backGroundColor;
        this.resource = resource;
    }

    public String getBackGroundColor() {
        return backGroundColor;
    }

    public void setBackGroundColor(String backGroundColor) {
        this.backGroundColor = backGroundColor;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    //////////Strip Ad

    private String title;
    private List<Horizontal_Product_Scroll_Model> horizontalProductScrollModelList;

    //////////Grid Product Layout

    public HomePageModel(int type, String title,String backGroundColor, List<Horizontal_Product_Scroll_Model> horizontalProductScrollModelList) {
        this.type = type;
        this.backGroundColor = backGroundColor;
        this.title = title;
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    //////////Grid Product Layout

    //////////Horizontal Product Layout
    private List<WishlistModel> viewAllProductList;

    public HomePageModel(int type, String title,String backGroundColor, List<Horizontal_Product_Scroll_Model> horizontalProductScrollModelList,List<WishlistModel> viewAllProductList) {
        this.type = type;
        this.backGroundColor = backGroundColor;
        this.title = title;
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
        this.viewAllProductList = viewAllProductList;
    }


    public List<WishlistModel> getViewAllProductList() {
        return viewAllProductList;
    }

    public void setViewAllProductList(List<WishlistModel> viewAllProductList) {
        this.viewAllProductList = viewAllProductList;
    }

    //////////Horizontal Product Layout





    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Horizontal_Product_Scroll_Model> getHorizontalProductScrollModelList() {
        return horizontalProductScrollModelList;
    }

    public void setHorizontalProductScrollModelList(List<Horizontal_Product_Scroll_Model> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

}

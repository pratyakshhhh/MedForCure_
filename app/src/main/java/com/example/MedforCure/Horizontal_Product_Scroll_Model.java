package com.example.MedforCure;

public class Horizontal_Product_Scroll_Model {
    private String productID;
    private String product_image;
    private String product_title;
    private String product_brand;
    private String product_price;

    public String  getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_title() {
        return product_title;
    }

    public void setProduct_title(String product_title) {
        this.product_title = product_title;
    }

    public String getProduct_brand() {
        return product_brand;
    }

    public void setProduct_brand(String product_brand) {
        this.product_brand = product_brand;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public Horizontal_Product_Scroll_Model(String productID,String product_image, String product_title, String product_brand, String product_price) {
        this.productID = productID;
        this.product_image = product_image;
        this.product_title = product_title;
        this.product_brand = product_brand;
        this.product_price = product_price;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }
}

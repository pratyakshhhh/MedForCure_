package com.example.MedforCure;

import java.util.ArrayList;
import java.util.List;

public class Cart_Item_Model {
    public static final int CART_ITEM = 0;
    public static final int TOTAL_AMOUNT = 1;

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /////cart item
    private String productId;
    private String productImage;
    private Long freeCoupons;
    private Long productQuantity;
    private Long maxQuanity;
    private Long stockQuanity;
    private Long OffersApplied;
    private Long  CouponsApplied;
    private String productTitle;
    private String productPrice;
    private String cuttedPrice;
    private boolean inStock;
    private List<String> qtyIDs;
    private boolean qtyError;
    private String selectedCouponId;
    private String discountedPrice;
    private boolean COD;


    public boolean isCOD() {
        return COD;
    }

    public void setCOD(boolean COD) {
        this.COD = COD;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public Long getStockQuanity() {
        return stockQuanity;
    }

    public void setStockQuanity(Long stockQuanity) {
        this.stockQuanity = stockQuanity;
    }

    public Cart_Item_Model(boolean COD,int type, String productId, String productImage, String productTitle, Long freeCoupons, Long productQuantity, Long offersApplied, Long couponsApplied, String productPrice, String cuttedPrice, boolean inStock, Long maxQuanity, Long stockQuanity) {
        this.type = type;
        this.productId=productId;
        this.productImage = productImage;
        this.freeCoupons = freeCoupons;
        this.productQuantity = productQuantity;
        OffersApplied = offersApplied;
        CouponsApplied = couponsApplied;
        this.productTitle = productTitle;
        this.productPrice = productPrice;
        this.stockQuanity = stockQuanity;
        this.cuttedPrice = cuttedPrice;
        this.inStock = inStock;
        this.maxQuanity = maxQuanity;
        qtyIDs = new ArrayList<>();
        qtyError = false;
        this.COD = COD;
    }

    public String getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(String discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public boolean isQtyError() {
        return qtyError;
    }

    public void setQtyError(boolean qtyError) {
        this.qtyError = qtyError;
    }

    public List<String> getQtyIDs() {
        return qtyIDs;
    }

    public void setQtyIDs(List<String> qtyIDs) {
        this.qtyIDs = qtyIDs;
    }

    public Long getMaxQuanity() {
        return maxQuanity;
    }

    public String getSelectedCouponId() {
        return selectedCouponId;
    }

    public void setSelectedCouponId(String selectedCouponId) {
        this.selectedCouponId = selectedCouponId;
    }

    public void setMaxQuanity(Long maxQuanity) {
        this.maxQuanity = maxQuanity;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public Long getFreeCoupons() {
        return freeCoupons;
    }

    public void setFreeCoupons(Long freeCoupons) {
        this.freeCoupons = freeCoupons;
    }

    public Long getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Long productQuantity) {
        this.productQuantity = productQuantity;
    }

    public Long getOffersApplied() {
        return OffersApplied;
    }

    public void setOffersApplied(Long offersApplied) {
        OffersApplied = offersApplied;
    }

    public Long getCouponsApplied() {
        return CouponsApplied;
    }

    public void setCouponsApplied(Long couponsApplied) {
        CouponsApplied = couponsApplied;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getCuttedPrice() {
        return cuttedPrice;
    }

    public void setCuttedPrice(String cuttedPrice) {
        this.cuttedPrice = cuttedPrice;
    }
    /////cart item

    /////cart total

    private int totalItems,totalitemsPrice,totalAmount,savedAmount;
    private String deliveryPrice;

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalitemsPrice() {
        return totalitemsPrice;
    }

    public void setTotalitemsPrice(int totalitemsPrice) {
        this.totalitemsPrice = totalitemsPrice;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getSavedAmount() {
        return savedAmount;
    }

    public void setSavedAmount(int savedAmount) {
        this.savedAmount = savedAmount;
    }

    public String getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(String deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public Cart_Item_Model(int type) {
        this.type = type;
    }

    /////cart total



}

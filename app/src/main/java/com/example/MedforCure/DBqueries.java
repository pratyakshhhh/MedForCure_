package com.example.MedforCure;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DBqueries {

    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static String email, fullName, profile;

    public static List<CategoryModel> categoryModelList = new ArrayList<>();

    public static List<List<HomePageModel>> lists = new ArrayList<>();
    public static List<String> loadedCategoriesNames = new ArrayList<>();

    public static List<String> wishList = new ArrayList<>();
    public static List<WishlistModel> wishlistModelList = new ArrayList<>();

    public static List<String> myRatedIds = new ArrayList<>();
    public static List<Long> myRating = new ArrayList<>();

    public static List<String> cartList = new ArrayList<>();
    public static List<Cart_Item_Model> cartItemModelList = new ArrayList<>();
    public static int selectedAddress = -1;
    public static List<AddressesModel> addressesModelList = new ArrayList<>();

    public static List<RewardModel> rewardModelList = new ArrayList<>();

    public static List<MyOrderItemModel> myOrderItemModelList = new ArrayList<>();

    public static void loadCategories(final RecyclerView categoryRecyclerView, final Context context) {
        categoryModelList.clear();
        firebaseFirestore.collection("CATEGORIES").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                categoryModelList.add(new CategoryModel(documentSnapshot.get("icon").toString(), documentSnapshot.get("categoryName").toString()));
                            }
                            CategoryAdapter categoryAdapter = new CategoryAdapter(categoryModelList);
                            categoryRecyclerView.setAdapter(categoryAdapter);
                            categoryAdapter.notifyDataSetChanged();// ye refresh karta hai list ko

                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void loadFragmentData(final RecyclerView homePageRecyclerView, final Context context, final int index, String categoryName) {
        firebaseFirestore.collection("CATEGORIES").document(categoryName.toUpperCase())
                .collection("TOP_DEALS")
                .orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                if ((long) documentSnapshot.get("view_type") == 0) {
                                    List<SliderModel> sliderModelList = new ArrayList<>();
                                    long no_of_banners = (long) documentSnapshot.get("no_of_banners");
                                    for (long x = 1; x <= no_of_banners; x++) {
                                        sliderModelList.add(new SliderModel(documentSnapshot.get("banner_" + x).toString(), documentSnapshot.get("banner_" + x + "_background").toString()));
                                    }
                                    lists.get(index).add(new HomePageModel(0, sliderModelList));
                                } else if ((long) documentSnapshot.get("view_type") == 1) {
                                    lists.get(index).add(new HomePageModel(1, documentSnapshot.get("strip_ad_banner").toString(), documentSnapshot.get("background").toString()));
                                } else if ((long) documentSnapshot.get("view_type") == 2) {
                                    List<WishlistModel> viewAllProductList = new ArrayList<>();
                                    List<Horizontal_Product_Scroll_Model> horizontalProductScrollModelList = new ArrayList<>();
                                    long no_of_products = (long) documentSnapshot.get("no_of_products");
                                    for (long x = 1; x <= no_of_products; x++) {
                                        horizontalProductScrollModelList.add(new Horizontal_Product_Scroll_Model((String) documentSnapshot.get("product_ID_" + x),
                                                (String) documentSnapshot.get("product_image_" + x),
                                                (String) documentSnapshot.get("product_title_" + x),
                                                (String) documentSnapshot.get("product_brand_" + x),
                                                (String) documentSnapshot.get("product_price_" + x)));
                                        if (documentSnapshot.get("free_coupons_" + x) != null && documentSnapshot.get("total_ratings_" + x) != null && documentSnapshot.get("product_image_" + x) != null && documentSnapshot.get("product_full_title_" + x) != null && documentSnapshot.get("average_rating_" + x) != null && documentSnapshot.get("product_price_" + x) != null && documentSnapshot.get("cutted_price_" + x) != null && documentSnapshot.get("COD_" + x) != null) {
                                            viewAllProductList.add(new WishlistModel((String) documentSnapshot.get("product_ID_" + x),
                                                    (String) documentSnapshot.get("product_image_" + x),
                                                    (String) documentSnapshot.get("product_full_title_" + x),
                                                    (Long) documentSnapshot.get("free_coupons_" + x),
                                                    (String) documentSnapshot.get("average_rating_" + x),
                                                    (Long) documentSnapshot.get("total_ratings_" + x),
                                                    (String) documentSnapshot.get("product_price_" + x),
                                                    (String) documentSnapshot.get("cutted_price_" + x),
                                                    (Boolean) documentSnapshot.get("COD_" + x),
                                                    (boolean) documentSnapshot.get("in_stock_" + x)));
                                        }
                                    }
                                    lists.get(index).add(new HomePageModel(2, (String) documentSnapshot.get("layout_title"), (String) documentSnapshot.get("layout_background"), horizontalProductScrollModelList, viewAllProductList));
                                } else if ((Long) documentSnapshot.get("view_type") == 3) {
                                    List<Horizontal_Product_Scroll_Model> GridLayoutModelList = new ArrayList<>();
                                    Long no_of_products = (Long) documentSnapshot.get("no_of_products");
                                    for (long x = 1; x <= no_of_products; x++) {
                                        GridLayoutModelList.add(new Horizontal_Product_Scroll_Model((String) documentSnapshot.get("product_ID_" + x), (String) documentSnapshot.get("product_image_" + x), (String) documentSnapshot.get("product_title_" + x), (String) documentSnapshot.get("product_brand_" + x),
                                                (String) documentSnapshot.get("product_price_" + x)));
                                    }
                                    lists.get(index).add(new HomePageModel(3, (String) documentSnapshot.get("layout_title"), (String) documentSnapshot.get("layout_background"), GridLayoutModelList));

                                }

                            }
                            HomePageAdapter homePageAdapter = new HomePageAdapter(lists.get(index));
                            homePageRecyclerView.setAdapter(homePageAdapter);
                            homePageAdapter.notifyDataSetChanged();
                            HomeFragment.swipeRefreshLayout.setRefreshing(false);
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void loadWishlist(final Context context, final Dialog dialog, final boolean loadProductData) {
        wishList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("MY_WISHLIST")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                        wishList.add((String) task.getResult().get("product_id_" + x));
                        if (DBqueries.wishList.contains(ProductDetailsActivity.productId)) {
                            ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = true;
                            if (ProductDetailsActivity.addtoWishlistbutton != null) {
                                ProductDetailsActivity.addtoWishlistbutton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));
                            }
                        } else {
                            ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = false;
                            if (ProductDetailsActivity.addtoWishlistbutton != null) {
                                ProductDetailsActivity.addtoWishlistbutton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                            }
                        }
                        if (loadProductData) {
                            wishlistModelList.clear();
                            final String productId = (String) task.getResult().get("product_id_" + x);
                            firebaseFirestore.collection("PRODUCTS").document(productId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        final DocumentSnapshot documentSnapshot = task.getResult();
                                        FirebaseFirestore.getInstance().collection("PRODUCTS").document(productId).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            if (task.getResult().getDocuments().size() < (long) documentSnapshot.get("stock_quantity")) {
                                                                wishlistModelList.add(new WishlistModel(productId, (String) documentSnapshot.get("product_image_1"),
                                                                        (String) documentSnapshot.get("product_title"),
                                                                        (Long) documentSnapshot.get("free_coupons"),
                                                                        (String) documentSnapshot.get("average_rating"),
                                                                        (Long) documentSnapshot.get("total_ratings"),
                                                                        (String) documentSnapshot.get("product_price"),
                                                                        (String) documentSnapshot.get("cutted_price"),
                                                                        (Boolean) documentSnapshot.get("COD"),
                                                                        true));
                                                            } else {
                                                                wishlistModelList.add(new WishlistModel(productId, (String) documentSnapshot.get("product_image_1"),
                                                                        (String) documentSnapshot.get("product_title"),
                                                                        (Long) documentSnapshot.get("free_coupons"),
                                                                        (String) documentSnapshot.get("average_rating"),
                                                                        (Long) documentSnapshot.get("total_ratings"),
                                                                        (String) documentSnapshot.get("product_price"),
                                                                        (String) documentSnapshot.get("cutted_price"),
                                                                        (Boolean) documentSnapshot.get("COD"),
                                                                        false));

                                                            }
                                                            MyWishlistFragment.wishlistAdapter.notifyDataSetChanged();
                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        }
                    }

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

                }
                dialog.dismiss();
            }
        });
    }

    public static void removeFromWishlist(final int index, final Context context) {
        final String removedProductId = wishList.get(index);
        wishList.remove(index);
        Map<String, Object> updateWishlist = new HashMap<>();

        for (int x = 0; x < wishList.size(); x++) {
            updateWishlist.put("product_ID" + x, wishList.get(x));
        }
        updateWishlist.put("list_size", (long) wishList.size());
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                .document("MY_WISHLIST").set(updateWishlist).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (wishlistModelList.size() != 0) {
                        wishlistModelList.remove(index);
                        MyWishlistFragment.wishlistAdapter.notifyDataSetChanged();
                    }
                    ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = false;
                    Toast.makeText(context, "Product removed successfully", Toast.LENGTH_SHORT).show();
                } else {
                    if (ProductDetailsActivity.addtoWishlistbutton != null) {
                        ProductDetailsActivity.addtoWishlistbutton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));
                    }
                    wishList.add(index, removedProductId);
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }

                ProductDetailsActivity.running_wishlist_query = false;
            }
        });
    }

    public static void loadRatingList(final Context context) {
        if (!ProductDetailsActivity.running_rating_query) {
            ProductDetailsActivity.running_rating_query = true;
            myRatedIds.clear();
            myRating.clear();
            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_RATINGS")
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        List<String> orderProductIds = new ArrayList<>();
                        for (int x = 0; x < myOrderItemModelList.size(); x++) {
                            orderProductIds.add(myOrderItemModelList.get(x).getProductId());
                        }

                        for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                            myRatedIds.add(task.getResult().get("product_ID_" + x).toString());
                            myRating.add((long) task.getResult().get("rating_" + x));

                            if (task.getResult().get("product_ID_" + x).toString().equals(ProductDetailsActivity.productId)) {
                                ProductDetailsActivity.initialRating = Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x))) - 1;
                                if (ProductDetailsActivity.rateNowContainer != null) {
                                    ProductDetailsActivity.setRating(ProductDetailsActivity.initialRating);
                                }
                            }

                            if (orderProductIds.contains(task.getResult().get("product_ID_" + x).toString())) {
                                myOrderItemModelList.get(orderProductIds.indexOf(task.getResult().get("product_ID_" + x).toString())).setRating(Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x))) - 1);
                            }
                        }

                        if (MyOrdersFragment.myOrderAdapter != null) {
                            MyOrdersFragment.myOrderAdapter.notifyDataSetChanged();
                        }
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                    ProductDetailsActivity.running_rating_query = false;
                }
            });
        }
    }

    public static void loadCartList(final Context context, final Dialog dialog, final boolean loadProductData, final TextView badgeCount, final TextView cartTotalAmount) {
        cartList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("MY_CART")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                        cartList.add((String) task.getResult().get("product_id_" + x));
                        if (DBqueries.cartList.contains(ProductDetailsActivity.productId)) {
                            ProductDetailsActivity.ALREADY_ADDED_TO_CART = true;
                        } else {
                            ProductDetailsActivity.ALREADY_ADDED_TO_CART = false;
                        }

                        if (loadProductData) {
                            cartItemModelList.clear();
                            final String productId = (String) task.getResult().get("product_id_" + x);
                            firebaseFirestore.collection("PRODUCTS").document(productId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        final DocumentSnapshot documentSnapshot = task.getResult();
                                        FirebaseFirestore.getInstance().collection("PRODUCTS").document(productId).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            int index = 0;
                                                            if (cartList.size() >= 2) {
                                                                index = cartList.size() - 2;
                                                            }
                                                            if (task.getResult().getDocuments().size() < (long) documentSnapshot.get("stock_quantity")) {
                                                                cartItemModelList.add(index, new Cart_Item_Model(documentSnapshot.getBoolean("COD"),
                                                                        Cart_Item_Model.CART_ITEM,
                                                                        productId,
                                                                        (String) documentSnapshot.get("product_image_1"),
                                                                        (String) documentSnapshot.get("product_title"),
                                                                        (Long) documentSnapshot.get("free_coupons"),
                                                                        (long) 1,
                                                                        (long) documentSnapshot.get("offers_applied"),
                                                                        (long) 0,
                                                                        (String) documentSnapshot.get("product_price"),
                                                                        (String) documentSnapshot.get("cutted_price"),
                                                                        true,
                                                                        (long) documentSnapshot.get("max-quantity"),
                                                                        (long) documentSnapshot.get("stock_quantity")));
                                                            } else {
                                                                cartItemModelList.add(index, new Cart_Item_Model(documentSnapshot.getBoolean("COD"),
                                                                        Cart_Item_Model.CART_ITEM,
                                                                        productId,
                                                                        (String) documentSnapshot.get("product_image_1"),
                                                                        (String) documentSnapshot.get("product_title"),
                                                                        (Long) documentSnapshot.get("free_coupons"),
                                                                        (long) 1,
                                                                        (long) documentSnapshot.get("offers_applied"),
                                                                        (long) 0,
                                                                        (String) documentSnapshot.get("product_price"),
                                                                        (String) documentSnapshot.get("cutted_price"),
                                                                        false,
                                                                        (long) documentSnapshot.get("max-quantity"),
                                                                        (long) documentSnapshot.get("stock_quantity")));

                                                            }
                                                            if (cartList.size() == 1) {
                                                                cartItemModelList.add(new Cart_Item_Model(Cart_Item_Model.TOTAL_AMOUNT));
                                                                LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
                                                                parent.setVisibility(View.VISIBLE);

                                                            }
                                                            if (cartList.size() == 0) {
                                                                cartItemModelList.clear();
                                                            }
                                                            MyCartFragment.cartAdapter.notifyDataSetChanged();
                                                        } else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        }
                    }

                    if (cartList.size() != 0) {
                        badgeCount.setVisibility(View.VISIBLE);
                    } else {
                        badgeCount.setVisibility(View.INVISIBLE);
                    }
                    if (DBqueries.cartList.size() < 99) {
                        badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
                    } else {
                        badgeCount.setText("99+");
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

                }
                dialog.dismiss();
            }
        });
    }

    public static void removeFromCart(final int index, final Context context, final TextView cartTotalAmount) {
        final String removedProductId = cartList.get(index);
        cartList.remove(index);
        Map<String, Object> updateCartlist = new HashMap<>();

        for (int x = 0; x < cartList.size(); x++) {
            updateCartlist.put("product_ID" + x, cartList.get(x));
        }
        updateCartlist.put("list_size", (long) cartList.size());
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                .document("MY_CART").set(updateCartlist).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (cartItemModelList.size() != 0) {
                        cartItemModelList.remove(index);
                        MyCartFragment.cartAdapter.notifyDataSetChanged();
                    }
                    if (cartList.size() == 0) {
                        LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
                        parent.setVisibility(View.GONE);
                        cartItemModelList.clear();
                    }
                    Toast.makeText(context, "Product removed successfully", Toast.LENGTH_SHORT).show();
                } else {
                    cartList.add(index, removedProductId);
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }

                ProductDetailsActivity.running_cart_query = false;
            }
        });
    }

    public static void loadAddresses(final Context context, final Dialog loadingDialog, final boolean gotoDeliveryActivity) {
        addressesModelList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                .document("MY_ADDRESSES").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Intent deliveryIntent = null;
                    if ((long) task.getResult().get("list_size") == 0) {
                        deliveryIntent = new Intent(context, AddAddressActivity.class);
                        deliveryIntent.putExtra("INTENT", "deliveryIntent");
                    } else {
                        for (long x = 1; x <= (long) task.getResult().get("list_size"); x++) {
                            addressesModelList.add(new AddressesModel(task.getResult().getBoolean("selected_" + x),
                                    task.getResult().getString("city_" + x),
                                    task.getResult().getString("locality_" + x),
                                    task.getResult().getString("flat_no_" + +x),
                                    task.getResult().getString("pincode_" + +x),
                                    task.getResult().getString("landmark_" + x),
                                    task.getResult().getString("name_" + x),
                                    task.getResult().getString("mobile_no_" + x),
                                    task.getResult().getString("alternate_mobile_no_" + x),
                                    task.getResult().getString("state_" + x)));

                            if ((Boolean) task.getResult().get("selected_" + x)) {
                                selectedAddress = Integer.parseInt(String.valueOf(x - 1));
                            }
                        }
                        if (gotoDeliveryActivity) {
                            deliveryIntent = new Intent(context, DeliveryActivity.class);
                        }
                    }
                    if (gotoDeliveryActivity) {
                        context.startActivity(deliveryIntent);
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }
        });
    }


    public static void loadRewards(final Context context, final Dialog loadingDialog, final Boolean onRewardFragment) {
        rewardModelList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final Date lastSeenDAte = task.getResult().getTimestamp("Last_seen").toDate();

                    firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                            .collection("USER_REWARDS").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    if (documentSnapshot.get("type").toString().equals("Discount") && lastSeenDAte.before(documentSnapshot.getTimestamp("validity").toDate())) {
                                        rewardModelList.add(new RewardModel(documentSnapshot.getId(), (String) documentSnapshot.get("type"),
                                                (String) documentSnapshot.get("lower_limit"),
                                                (String) documentSnapshot.get("upper_limit"),
                                                (String) documentSnapshot.get("percentage"),
                                                documentSnapshot.getTimestamp("validity").toDate(),
                                                (String) documentSnapshot.get("body"),
                                                (Boolean) documentSnapshot.get("already_used")

                                        ));
                                    } else if (documentSnapshot.get("type").toString().equals("Flat Rs.* OFF") && lastSeenDAte.before(documentSnapshot.getTimestamp("validity").toDate())) {
                                        rewardModelList.add(new RewardModel(documentSnapshot.getId(), (String) documentSnapshot.get("type"),
                                                (String) documentSnapshot.get("lower_limit"),
                                                (String) documentSnapshot.get("upper_limit"),
                                                (String) documentSnapshot.get("amount"),
                                                documentSnapshot.getTimestamp("validity").toDate(),
                                                (String) documentSnapshot.get("body"),
                                                (Boolean) documentSnapshot.get("already_used")
                                        ));
                                    }
                                }

                                if (onRewardFragment) {
                                    MyRewardsFragment.rewardAdapter.notifyDataSetChanged();
                                }

                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                            }
                            loadingDialog.dismiss();
                        }
                    });
                } else {
                    loadingDialog.dismiss();
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    public static void loadOrders(final Context context, @Nullable final MyOrderAdapter myOrderAdapter, final Dialog loadingDialog) {
        myOrderItemModelList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_ORDERS").orderBy("time", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                firebaseFirestore.collection("ORDERS").document(documentSnapshot.getString("order_id"))
                                        .collection("OrderItems").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {

                                            for (DocumentSnapshot orderItems : task.getResult().getDocuments()) {

                                                final MyOrderItemModel myOrderItemModel = new MyOrderItemModel(orderItems.getString("PRODUCT ID"), orderItems.getString("ORDER STATUS"), orderItems.getString("ADDRESS"), orderItems.getString("COUPON ID"), orderItems.getString("CUTTED PRICE"), orderItems.getDate("ORDERED DATE"),
                                                        orderItems.getDate("PACKED DATE"), orderItems.getDate("SHIPPED DATE"), orderItems.getDate("DELIVERED DATE"), orderItems.getDate("CANCELLED DATE"),
                                                        orderItems.getString("DISCOUNTED PRICE"), orderItems.getLong("FREE COUPONS"), orderItems.getString("FULL NAME"),
                                                        orderItems.getString("ORDER ID"), orderItems.getString("PAYMENT METHOD"), orderItems.getString("PINCODE"), orderItems.getString("PRODUCT PRICE"),
                                                        orderItems.getLong("PRODUCT QUANTITY"), orderItems.getString("USER ID"), orderItems.getString("PRODUCT IMAGE"), orderItems.getString("PRODUCT TITLE"), orderItems.getString("DELIVERY PRICE"), orderItems.getBoolean("CANCELLATION REQUESTED"));

                                                myOrderItemModelList.add(myOrderItemModel);

                                            }
                                            loadRatingList(context);
                                            if (myOrderAdapter != null) {
                                                myOrderAdapter.notifyDataSetChanged();
                                            }
                                        } else {
                                            String error = task.getException().getMessage();
                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                        }
                                        loadingDialog.dismiss();
                                    }
                                });
                            }
                        } else {
                            loadingDialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void clearData() {
        categoryModelList.clear();
        lists.clear();
        loadedCategoriesNames.clear();
        wishList.clear();
        wishlistModelList.clear();
        cartList.clear();
        cartItemModelList.clear();
        myRatedIds.clear();
        myRating.clear();
        addressesModelList.clear();
        rewardModelList.clear();
        myOrderItemModelList.clear();
    }
}


package com.example.MedforCure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static com.example.MedforCure.MainActivity.showCart;
import static com.example.MedforCure.RegisterActivity.setSignUpFragment;

public class ProductDetailsActivity extends AppCompatActivity {

    public static boolean running_wishlist_query = false;
    public static boolean running_rating_query = false;
    public static boolean running_cart_query = false;
    public static Activity productDetailsActivity;


    private ViewPager ProductImagesViewPager;
    private TextView productTitle;
    private TextView averageRatingminiView;
    private TextView totalRatingsminiView;
    private TextView productPrice;
    private String productOriginalPrice;
    private TextView cuttedPrice;
    private ImageView codIndicator;
    private TextView tvcodIndicator;
    private TextView rewardTitle;
    private TextView rewardBody;


    private TabLayout ViewPagerIndicator;
    private LinearLayout couponRedemptionLayout;
    private Button couponRedeemBtn;
    public static FloatingActionButton addtoWishlistbutton;
    public static boolean ALREADY_ADDED_TO_WISHLIST = false;

    //product description
    private ConstraintLayout ProductDetailsOnlyContainer;
    private ConstraintLayout ProductDetailsTabsContainer;
    private ViewPager ProductDetailsViewPager;
    private TabLayout ProductDetailsTabLayout;
    private TextView productOnlyDetailsBody;
    private List<Product_Other_Details_Model> product_other_details_modelList = new ArrayList<>();
    private String productComposition;
    //product description


    private Button buynowbtn;
    private LinearLayout addtoCartbtn;
    public static MenuItem cartItem;
    public static boolean ALREADY_ADDED_TO_CART = false;


    private FirebaseFirestore firebaseFirestore;

    /////rating layout
    public static LinearLayout rateNowContainer;
    private TextView totalRatings;
    private LinearLayout ratingsNoContainer;
    private TextView totalRatingsfigure;
    private LinearLayout ratingsProgressBarContainer;
    private TextView averagerating;
    public static int initialRating;

    /////rating layout


    ///Coupon Dialog
    private TextView couponTitle;
    private TextView couponBody;
    private TextView couponExpiryDate;
    private RecyclerView couponsRecyclerView;
    private LinearLayout selectedCoupon;
    private TextView discountedPrice;
    private TextView originalPrice;
    ///Coupon Dialog

    private Dialog signInDialog;
    private FirebaseUser currentUser;
    public static String productId;
    private Dialog loadingDialog;
    private TextView badgeCount;

    private boolean inStock = false;
    private DocumentSnapshot documentSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        buynowbtn = findViewById(R.id.buy_now_button);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ProductImagesViewPager = findViewById(R.id.product_images_viewpager);
        ViewPagerIndicator = findViewById(R.id.viewpager_indicator);
        addtoWishlistbutton = findViewById(R.id.add_to_wishlish_button);
        ProductDetailsViewPager = findViewById(R.id.product_details_view_pager);
        ProductDetailsTabLayout = findViewById(R.id.product_details_tab_layout);
        couponRedeemBtn = findViewById(R.id.coupon_redemption_button);
        productTitle = findViewById(R.id.product_title);
        averageRatingminiView = findViewById(R.id.tv_product_rating_miniview);
        totalRatingsminiView = findViewById(R.id.total_ratings_miniview);
        productPrice = findViewById(R.id.product_price);
        cuttedPrice = findViewById(R.id.cutted_price);
        tvcodIndicator = findViewById(R.id.tv_cod_indicator);
        codIndicator = findViewById(R.id.cod_indicator_image_view);
        rewardTitle = findViewById(R.id.reward_title);
        rewardBody = findViewById(R.id.reward_body);

        ProductDetailsTabsContainer = findViewById(R.id.product_details_tabs_container);
        ProductDetailsOnlyContainer = findViewById(R.id.product_details_container);
        productOnlyDetailsBody = findViewById(R.id.product_details_body);

        totalRatings = findViewById(R.id.total_ratings);
        ratingsNoContainer = findViewById(R.id.ratings_numbers_container);
        totalRatingsfigure = findViewById(R.id.total_ratings_figure);
        ratingsProgressBarContainer = findViewById(R.id.ratings_progress_bar_container);
        averagerating = findViewById(R.id.average_rating);
        addtoCartbtn = findViewById(R.id.add_to_cart_button);
        couponRedemptionLayout = findViewById(R.id.coupon_redemption_layout);

        initialRating = -1;
        // Loading Dialog
        loadingDialog = new Dialog(ProductDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false); // Agar user dialog ke bahar click karega toh dialog khud bandd ho jayega
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        // Loading Dialog

        ////Coupon Dialog
        final Dialog checkCouponPriceDialog = new Dialog(ProductDetailsActivity.this);
        checkCouponPriceDialog.setContentView(R.layout.coupon_redeem_dialog);
        checkCouponPriceDialog.setCancelable(true);
        checkCouponPriceDialog.getWindow().
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageView toggleRecyclerView = checkCouponPriceDialog.findViewById(R.id.toggle_recyclerview);
        couponsRecyclerView = checkCouponPriceDialog.findViewById(R.id.coupons_recyclerview);
        selectedCoupon = checkCouponPriceDialog.findViewById(R.id.selected_coupon);

        couponTitle = checkCouponPriceDialog.findViewById(R.id.coupon_title);
        couponExpiryDate = checkCouponPriceDialog.findViewById(R.id.coupon_validity);
        couponBody = checkCouponPriceDialog.findViewById(R.id.coupon_body);

         originalPrice = checkCouponPriceDialog.findViewById(R.id.original_price);
         discountedPrice = checkCouponPriceDialog.findViewById(R.id.discounted_price);

        LinearLayoutManager layoutManager = new LinearLayoutManager(ProductDetailsActivity.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        couponsRecyclerView.setLayoutManager(layoutManager);

        toggleRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogRecyclerView();
            }
        });
        ////Coupon Dialog

        firebaseFirestore = FirebaseFirestore.getInstance();

        final List<String> productImages = new ArrayList<>();
        productId = getIntent().getStringExtra("PRODUCT_ID");
        firebaseFirestore.collection("PRODUCTS").document(productId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    documentSnapshot = task.getResult();
                    firebaseFirestore.collection("PRODUCTS").document(productId).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (long x = 1; x < (long) documentSnapshot.get("no_of_product_images") + 1; x++) {
                                            productImages.add(documentSnapshot.get("product_image_" + x).toString());
                                        }

                                        ProductImagesAdapter productImagesAdapter = new ProductImagesAdapter(productImages);
                                        ProductImagesViewPager.setAdapter(productImagesAdapter);

                                        productTitle.setText((String) documentSnapshot.get("product_title"));
                                        averageRatingminiView.setText((String) documentSnapshot.get("average_rating"));
                                        totalRatingsminiView.setText("(" + (long) documentSnapshot.get("total_ratings") + ") ratings");
                                        productPrice.setText("Rs. " + (String) documentSnapshot.get("product_price"));

                                        //for coupon dialog
                                        productOriginalPrice = (String) documentSnapshot.get("product_price");
                                        originalPrice.setText(productPrice.getText());
                                        RewardAdapter rewardAdapter = new RewardAdapter(DBqueries.rewardModelList, true,couponsRecyclerView,selectedCoupon,productOriginalPrice,couponTitle,couponExpiryDate,couponBody,discountedPrice);
                                        couponsRecyclerView.setAdapter(rewardAdapter);
                                        rewardAdapter.notifyDataSetChanged();
                                        //for coupon dialog

                                        cuttedPrice.setText("Rs. " + (String) documentSnapshot.get("cutted_price"));

                                        if ((boolean) documentSnapshot.get("COD")) {
                                            codIndicator.setVisibility(View.VISIBLE);
                                            tvcodIndicator.setVisibility(View.VISIBLE);
                                        } else {
                                            codIndicator.setVisibility(View.INVISIBLE);
                                            tvcodIndicator.setVisibility(View.INVISIBLE);
                                        }

                                        rewardTitle.setText((long) documentSnapshot.get("free_coupons") + (String) documentSnapshot.get("free_coupon_title"));
                                        rewardBody.setText((String) documentSnapshot.get("free_coupon_body"));

                                        if ((boolean) documentSnapshot.get("use_tab_layout")) {
                                            ProductDetailsTabsContainer.setVisibility(View.VISIBLE);
                                            ProductDetailsOnlyContainer.setVisibility(View.GONE);
                                            productComposition = (String) documentSnapshot.get("product_composition");


                                            for (long x = 1; x < (long) documentSnapshot.get("total_product_other_details_titles") + 1; x++) {
                                                product_other_details_modelList.add(new Product_Other_Details_Model(1, (String) documentSnapshot.get("product_other_details_" + x + "_name"), (String) documentSnapshot.get("product_other_details_" + x + "_value")));
                                            }
                                        } else {

                                            ProductDetailsTabsContainer.setVisibility(View.GONE);
                                            ProductDetailsOnlyContainer.setVisibility(View.VISIBLE);
                                            productOnlyDetailsBody.setText((String) documentSnapshot.get("product_details_only"));
                                        }

                                        totalRatings.setText((long) documentSnapshot.get("total_ratings") + " ratings");
                                        int maxProgress = Integer.parseInt(String.valueOf((long) documentSnapshot.get("total_ratings")));
                                        for (int x = 0; x < 5; x++) {
                                            TextView rating = (TextView) ratingsNoContainer.getChildAt(x);
                                            rating.setText(String.valueOf((long) documentSnapshot.get((5 - x) + "_star"))); //important
                                            ProgressBar progressBar = (ProgressBar) ratingsProgressBarContainer.getChildAt(x);
                                            progressBar.setMax(maxProgress);
                                            progressBar.setProgress(Integer.parseInt(String.valueOf((long) documentSnapshot.get((5 - x) + "_star"))));
                                        }

                                        totalRatingsfigure.setText(String.valueOf((long) documentSnapshot.get("total_ratings")));
                                        averagerating.setText((String) documentSnapshot.get("average_rating"));
                                        averageRatingminiView.setText((String) documentSnapshot.get("average_rating"));
                                        ProductDetailsViewPager.setAdapter(new ProductDetailsAdapter(getSupportFragmentManager(), ProductDetailsTabLayout.getTabCount(), productComposition, product_other_details_modelList));

                                        if (currentUser != null) {
                                            if (DBqueries.myRating.size() == 0) {
                                                DBqueries.loadRatingList(ProductDetailsActivity.this);
                                            }
                                            if (DBqueries.cartList.size() == 0) {
                                                DBqueries.loadCartList(ProductDetailsActivity.this, loadingDialog, false, badgeCount, new TextView(ProductDetailsActivity.this));
                                            }

                                            if (DBqueries.wishList.size() == 0) {
                                                DBqueries.loadWishlist(ProductDetailsActivity.this, loadingDialog, false);
                                            }
                                            if (DBqueries.rewardModelList.size() == 0) {
                                                DBqueries.loadRewards(ProductDetailsActivity.this, loadingDialog,false);
                                            }
                                            if (DBqueries.cartList.size() != 0 && DBqueries.rewardModelList.size() != 0 && DBqueries.wishList.size() != 0) {
                                                loadingDialog.dismiss();
                                            }
                                        } else {
                                            loadingDialog.dismiss();
                                        }
                                        if (DBqueries.myRatedIds.contains(productId)) {
                                            int index = DBqueries.myRatedIds.indexOf(productId);
                                            initialRating = Integer.parseInt(String.valueOf(DBqueries.myRating.get(index))) - 1;
                                            setRating(initialRating);
                                        }
                                        if (DBqueries.cartList.contains(productId)) {
                                            ALREADY_ADDED_TO_CART = true;
                                            addtoWishlistbutton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));
                                        } else {
                                            ALREADY_ADDED_TO_CART = false;
                                            addtoWishlistbutton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                                        }
                                        if (DBqueries.wishList.contains(productId)) {
                                            ALREADY_ADDED_TO_WISHLIST = true;
                                            addtoWishlistbutton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));
                                        } else {
                                            ALREADY_ADDED_TO_WISHLIST = false;
                                            addtoWishlistbutton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                                        }


                                        if (task.getResult().getDocuments().size() < (long) documentSnapshot.get("stock_quantity")) {
                                            inStock = true;
                                            buynowbtn.setVisibility(View.VISIBLE);
                                            addtoCartbtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (currentUser == null) {
                                                        signInDialog.show();
                                                    } else {
                                                        if (!running_cart_query) {
                                                            running_cart_query = true;
                                                            if (ALREADY_ADDED_TO_CART) {
                                                                running_cart_query = false;
                                                                Toast.makeText(ProductDetailsActivity.this, "Already added to cart!", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Map<String, Object> addProduct = new HashMap<>();
                                                                addProduct.put("product_id_" + DBqueries.cartList.size(), productId);
                                                                addProduct.put("list_size", (long) (DBqueries.cartList.size() + 1));

                                                                firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_CART")
                                                                        .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            if (DBqueries.cartItemModelList.size() != 0) {
                                                                                DBqueries.cartItemModelList.add(0, new Cart_Item_Model(documentSnapshot.getBoolean("COD"),
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
                                                                                        inStock,
                                                                                        (long) documentSnapshot.get("max-quantity"),
                                                                                        (long) documentSnapshot.get("stock_quantity")));
                                                                            }
                                                                            ALREADY_ADDED_TO_CART = true;
                                                                            DBqueries.cartList.add(productId);
                                                                            Toast.makeText(ProductDetailsActivity.this, "Added to Cart successfully", Toast.LENGTH_SHORT).show();
                                                                            invalidateOptionsMenu();
                                                                            running_cart_query = false;
                                                                        } else {
                                                                            running_cart_query = false;
                                                                            String error = task.getException().getMessage();
                                                                            Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }

                                                }
                                            });
                                        } else {
                                            inStock = false;
                                            buynowbtn.setVisibility(View.GONE);
                                            TextView outOfStock = (TextView) addtoCartbtn.getChildAt(0);
                                            outOfStock.setText("Out of Stock");
                                            outOfStock.setTextColor(getResources().getColor(R.color.colorPrimary));
                                            outOfStock.setCompoundDrawables(null, null, null, null);

                                        }
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                } else {
                    loadingDialog.dismiss();
                    String error = task.getException().getMessage();
                    Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });


        ViewPagerIndicator.setupWithViewPager(ProductImagesViewPager, true);

        addtoWishlistbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    signInDialog.show();
                } else {
                    if (!running_wishlist_query) {
                        running_wishlist_query = true;
                        // addtoWishlistbutton.setEnabled(false);
                        if (ALREADY_ADDED_TO_WISHLIST) {
                            int index = DBqueries.wishList.indexOf(productId);
                            DBqueries.removeFromWishlist(index, ProductDetailsActivity.this);
                            addtoWishlistbutton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));

                        } else {
                            addtoWishlistbutton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));
                            Map<String, Object> addProduct = new HashMap<>();
                            addProduct.put("product_id_" + DBqueries.wishList.size(), productId);
                            addProduct.put("list_size", (long) (DBqueries.wishList.size() + 1));

                            firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_WISHLIST")
                                    .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        if (DBqueries.wishlistModelList.size() != 0) {
                                            DBqueries.wishlistModelList.add(new WishlistModel(productId, (String) documentSnapshot.get("product_image_1"),
                                                    (String) documentSnapshot.get("product_full_title"),
                                                    (Long) documentSnapshot.get("free_coupons"),
                                                    (String) documentSnapshot.get("average_rating"),
                                                    (Long) documentSnapshot.get("total_ratings"),
                                                    (String) documentSnapshot.get("product_price"),
                                                    (String) documentSnapshot.get("cutted_price"),
                                                    (Boolean) documentSnapshot.get("COD"),
                                                    inStock));
                                        }
                                        ALREADY_ADDED_TO_WISHLIST = true;
                                        addtoWishlistbutton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));
                                        DBqueries.wishList.add(productId);
                                        Toast.makeText(ProductDetailsActivity.this, "Added to wishlist successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        addtoWishlistbutton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                                        String error = task.getException().getMessage();
                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();

                                    }
                                    running_wishlist_query = false;
                                }
                            });
                        }
                    }
                }
            }
        });

        ProductDetailsViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(ProductDetailsTabLayout));
        ProductDetailsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ProductDetailsViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        /////rating layout

        rateNowContainer = findViewById(R.id.rate_now_container);
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            final int starPosition = x;
            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentUser == null) {
                        signInDialog.show();
                    } else {
                        if (starPosition != initialRating) {
                            if (!running_rating_query) {
                                running_rating_query = true;
                                setRating(starPosition);
                                Map<String, Object> updateRating = new HashMap<>();
                                if (DBqueries.myRatedIds.contains(productId)) {  //for updating the current rating
                                    TextView oldrating = (TextView) ratingsNoContainer.getChildAt(5 - initialRating - 1);
                                    TextView finalrating = (TextView) ratingsNoContainer.getChildAt(5 - starPosition - 1);
                                    updateRating.put(initialRating + 1 + "_star", Long.parseLong((String) oldrating.getText()) - 1);
                                    updateRating.put(starPosition + 1 + "_star", Long.parseLong((String) finalrating.getText()) + 1);
                                    updateRating.put("average_rating", calculateAverageRating((long) starPosition + initialRating, true));

                                } else {//for first time rating

                                    updateRating.put(starPosition + 1 + "_star", (long) documentSnapshot.get(starPosition + 1 + "_star") + 1);
                                    updateRating.put("average_rating", calculateAverageRating(starPosition + 1, false));
                                    updateRating.put("total_ratings", (long) documentSnapshot.get("total_ratings") + 1);

                                }
                                firebaseFirestore.collection("PRODUCTS").document(productId)
                                        .update(updateRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Map<String, Object> myrating = new HashMap<>();
                                            if (DBqueries.myRatedIds.contains(productId)) {
                                                myrating.put("rating_" + DBqueries.myRatedIds.indexOf(productId), (long) starPosition + 1);
                                            } else {
                                                myrating.put("list_size", (long) DBqueries.myRatedIds.size() + 1);
                                                myrating.put("product_ID_" + DBqueries.myRatedIds.size(), productId);
                                                myrating.put("rating_" + DBqueries.myRatedIds.size(), (long) starPosition + 1);
                                            }
                                            firebaseFirestore.collection("USERS").document(currentUser.getUid())
                                                    .collection("USER_DATA").document("MY_RATINGS")
                                                    .update(myrating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        if (DBqueries.myRatedIds.contains(productId)) {
                                                            DBqueries.myRating.set(DBqueries.myRatedIds.indexOf(productId), (long) starPosition + 1);

                                                            TextView oldrating = (TextView) ratingsNoContainer.getChildAt(5 - initialRating - 1);
                                                            TextView finalrating = (TextView) ratingsNoContainer.getChildAt(5 - starPosition - 1);

                                                            oldrating.setText(String.valueOf(Integer.parseInt((String) oldrating.getText()) - 1));
                                                            finalrating.setText(String.valueOf(Integer.parseInt((String) finalrating.getText()) + 1));
                                                        } else {
                                                            DBqueries.myRatedIds.add(productId);
                                                            DBqueries.myRating.add((long) starPosition + 1);

                                                            TextView rating = (TextView) ratingsNoContainer.getChildAt(5 - starPosition - 1);
                                                            rating.setText(String.valueOf(Integer.parseInt((String) rating.getText()) + 1));

                                                            totalRatingsminiView.setText("(" + (long) documentSnapshot.get("total_ratings") + 1 + ") ratings");
                                                            totalRatings.setText((long) documentSnapshot.get("total_ratings") + 1 + " ratings");
                                                            totalRatingsfigure.setText(String.valueOf((long) documentSnapshot.get("total_ratings") + 1));

                                                            Toast.makeText(ProductDetailsActivity.this, "Thank you for rating", Toast.LENGTH_SHORT).show();
                                                        }
                                                        int maxProgress = Integer.parseInt((String) totalRatingsfigure.getText());
                                                        for (int x = 0; x < 5; x++) {
                                                            TextView ratingfigures = (TextView) ratingsNoContainer.getChildAt(x);
                                                            ProgressBar progressBar = (ProgressBar) ratingsProgressBarContainer.getChildAt(x);
                                                            progressBar.setMax(maxProgress);
                                                            progressBar.setProgress(Integer.parseInt((String) ratingfigures.getText()));
                                                        }
                                                        initialRating = starPosition;
                                                        averagerating.setText(calculateAverageRating(0, true));
                                                        averageRatingminiView.setText(calculateAverageRating(0, true));

                                                        if (DBqueries.wishList.contains(productId) && DBqueries.wishlistModelList.size() != 0) {
                                                            int index = DBqueries.wishList.indexOf(productId);
                                                            DBqueries.wishlistModelList.get(index).setRating(averagerating.getText().toString());
                                                            DBqueries.wishlistModelList.get(index).setTotalRatings(Long.valueOf((String) totalRatingsfigure.getText()));
                                                        }
                                                    } else {
                                                        setRating(initialRating);
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                    running_rating_query = false;
                                                }
                                            });
                                        } else {
                                            running_rating_query = false;
                                            setRating(initialRating);
                                            String error = task.getException().getMessage();
                                            Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            }
                        }
                    }
                }

            });
        }
        /////rating layout

        buynowbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    signInDialog.show();
                } else {
                    DeliveryActivity.fromCart = false;
                    loadingDialog.show();
                    productDetailsActivity = ProductDetailsActivity.this;
                    DeliveryActivity.cartItemModelList = new ArrayList<>();
                    DeliveryActivity.cartItemModelList.add(new Cart_Item_Model(documentSnapshot.getBoolean("COD"),
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
                            inStock,
                            (long) documentSnapshot.get("max-quantity"),
                            (long) documentSnapshot.get("stock_quantity")));
                    DeliveryActivity.cartItemModelList.add(new Cart_Item_Model(Cart_Item_Model.TOTAL_AMOUNT));
                    if (DBqueries.addressesModelList.size() == 0) {
                        DBqueries.loadAddresses(ProductDetailsActivity.this, loadingDialog,true);
                    } else {
                        loadingDialog.dismiss();
                        Intent deliveryIntent = new Intent(ProductDetailsActivity.this, DeliveryActivity.class);
                        startActivity(deliveryIntent);
                    }
                }
            }
        });




        couponRedeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCouponPriceDialog.show();
            }
        });

        //sign in dialog
        signInDialog = new Dialog(ProductDetailsActivity.this);
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.setCancelable(true); // Agar user dialog ke bahar click karega toh dialog khud bandd ho jayega
        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button dialogSignInBtn = signInDialog.findViewById(R.id.sign_in_btn);
        Button dialogSignUpBtn = signInDialog.findViewById(R.id.sign_up_btn);

        final Intent registerIntent = new Intent(ProductDetailsActivity.this, RegisterActivity.class);

        dialogSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SigninFragment.disableclosebutton = true;
                SignupFragment.disableclosebutton = true;
                signInDialog.dismiss();
                setSignUpFragment = false;
                startActivity(registerIntent);

            }
        });

        dialogSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SigninFragment.disableclosebutton = true;
                SignupFragment.disableclosebutton = true;
                signInDialog.dismiss();
                setSignUpFragment = true;
                startActivity(registerIntent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            couponRedemptionLayout.setVisibility(View.GONE);
        } else {
            couponRedemptionLayout.setVisibility(View.VISIBLE);
        }
        if (currentUser != null) {
            if (DBqueries.myRating.size() == 0) {
                DBqueries.loadRatingList(ProductDetailsActivity.this);
            }
            if (DBqueries.wishList.size() == 0) {
                DBqueries.loadWishlist(ProductDetailsActivity.this, loadingDialog, false);
            }
            if (DBqueries.rewardModelList.size() == 0) {
                DBqueries.loadRewards(ProductDetailsActivity.this, loadingDialog,false);
            }
            if (DBqueries.cartList.size() != 0 && DBqueries.rewardModelList.size() != 0 && DBqueries.wishList.size() != 0) {
                loadingDialog.dismiss();
            }
        } else {
            loadingDialog.dismiss();
        }
        if (DBqueries.myRatedIds.contains(productId)) {
            int index = DBqueries.myRatedIds.indexOf(productId);
            initialRating = Integer.parseInt(String.valueOf(DBqueries.myRating.get(index))) - 1;
            setRating(initialRating);
        }

        if (DBqueries.cartList.contains(productId)) {
            ALREADY_ADDED_TO_CART = true;
            addtoWishlistbutton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));
        } else {
            ALREADY_ADDED_TO_CART = false;
            addtoWishlistbutton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
        }

        if (DBqueries.wishList.contains(productId)) {
            ALREADY_ADDED_TO_WISHLIST = true;
            addtoWishlistbutton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));
        } else {
            ALREADY_ADDED_TO_WISHLIST = false;
            addtoWishlistbutton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
        }
        invalidateOptionsMenu();

    }

    private void showDialogRecyclerView() {
        if (couponsRecyclerView.getVisibility() == View.GONE) {
            couponsRecyclerView.setVisibility(View.VISIBLE);
            selectedCoupon.setVisibility(View.GONE);
        } else {
            couponsRecyclerView.setVisibility(View.GONE);
            selectedCoupon.setVisibility(View.VISIBLE);

        }
    }

    public static void setRating(int starPosition) {
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            ImageView starBtn = (ImageView) rateNowContainer.getChildAt(x);
            starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));
            if (x <= starPosition) {
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFB350")));
            }
        }
    }


    private String calculateAverageRating(long currentUserRating, boolean update) {
        Double totalStars = Double.valueOf(0);
        for (int x = 1; x < 6; x++) {
            TextView ratingNo = (TextView) ratingsNoContainer.getChildAt(5 - x);
            totalStars = totalStars + (Long.parseLong((String) ratingNo.getText())) * x;
        }
        totalStars = totalStars + currentUserRating;
        if (update) {
            return String.valueOf((totalStars / Long.parseLong((String) totalRatingsfigure.getText()))).substring(0, 3);

        } else {

            return String.valueOf((totalStars / Long.parseLong((String) totalRatingsfigure.getText()) + 1)).substring(0, 3);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_and_cart_icon, menu);
        cartItem = menu.findItem(R.id.main_cart_icon);
        cartItem.setActionView(R.layout.badge_layout);
        ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
        badgeIcon.setImageResource(R.drawable.cart_small);
        badgeIcon.setImageTintList((ColorStateList.valueOf(Color.parseColor("#ffffff"))));
        badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);
        if (currentUser != null) {
            if (DBqueries.cartList.size() == 0) {
                DBqueries.loadCartList(ProductDetailsActivity.this, loadingDialog, false, badgeCount, new TextView(ProductDetailsActivity.this));
            } else {
                badgeCount.setVisibility(View.VISIBLE);
                if (DBqueries.cartList.size() < 99) {
                    badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
                } else {
                    badgeCount.setText("99+");
                }
            }
        }
        cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    signInDialog.show();
                } else {
                    Intent cartIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                    showCart = true;
                    startActivity(cartIntent);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();


        if (id == android.R.id.home) {
            productDetailsActivity = null;
            finish();
            return true;
        } else if (id == R.id.main_search_icon) {
            return true;
        } else if (id == R.id.main_cart_icon) {
            if (currentUser == null) {
                signInDialog.show();
            } else {
                Intent cartIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                showCart = true;
                startActivity(cartIntent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        productDetailsActivity = null;
        super.onBackPressed();
    }
}
package com.example.MedforCure;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Distribution;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Cart_Item_Adapter extends RecyclerView.Adapter {

    private List<Cart_Item_Model> cart_item_modelList;
    private int lastPosition = -1;
    private final TextView cartTotalAmount;
    private boolean showDeleteBtn;

    public Cart_Item_Adapter(List<Cart_Item_Model> cart_item_modelList, TextView cartTotalAmount, boolean showDeleteBtn) {
        this.cart_item_modelList = cart_item_modelList;
        this.cartTotalAmount = cartTotalAmount;
        this.showDeleteBtn = showDeleteBtn;
    }

    @Override
    public int getItemViewType(int position) {
        switch (cart_item_modelList.get(position).getType()) {
            case 0:
                return Cart_Item_Model.CART_ITEM;
            case 1:
                return Cart_Item_Model.TOTAL_AMOUNT;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case Cart_Item_Model.CART_ITEM:
                View cartItemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_item_layout, viewGroup, false);
                return new CartItemViewHolder(cartItemView);
            case Cart_Item_Model.TOTAL_AMOUNT:
                View cartTotalView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_total_amount_layout, viewGroup, false);
                return new CartTotalAmountViewHolder(cartTotalView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (cart_item_modelList.get(position).getType()) {
            case Cart_Item_Model.CART_ITEM:
                String productID = cart_item_modelList.get(position).getProductId();
                String resource = cart_item_modelList.get(position).getProductImage();
                String title = cart_item_modelList.get(position).getProductTitle();
                Long freeCoupons = cart_item_modelList.get(position).getFreeCoupons();
                String product_price = cart_item_modelList.get(position).getProductPrice();
                String cutted_price = cart_item_modelList.get(position).getCuttedPrice();
                Long offers_applied = cart_item_modelList.get(position).getOffersApplied();
                boolean inStock = cart_item_modelList.get(position).isInStock();
                Long productQuantity = cart_item_modelList.get(position).getProductQuantity();
                Long maxQuantity = cart_item_modelList.get(position).getMaxQuanity();
                boolean qtyError = cart_item_modelList.get(position).isQtyError();
                List<String> qtyIds = cart_item_modelList.get(position).getQtyIDs();
                long stockQty = cart_item_modelList.get(position).getStockQuanity();
                boolean COD = cart_item_modelList.get(position).isCOD();

                ((CartItemViewHolder) viewHolder).setItemDetails(productID, resource, title, freeCoupons, product_price, cutted_price, offers_applied, position, inStock, String.valueOf(productQuantity), maxQuantity, qtyError, qtyIds, stockQty,COD);
                break;
            case Cart_Item_Model.TOTAL_AMOUNT:
                int totalItems = 0;
                int totalItemPrice = 0;
                String deliveryPrice;
                int totalAmount;
                int savedAmount = 0;

                for (int x = 0; x < cart_item_modelList.size(); x++) {

                    if (cart_item_modelList.get(x).getType() == Cart_Item_Model.CART_ITEM && cart_item_modelList.get(x).isInStock()) {
                        int quantity = Integer.parseInt(String.valueOf(cart_item_modelList.get(x).getProductQuantity()));
                        totalItems = totalItems + quantity;
                        if (TextUtils.isEmpty(cart_item_modelList.get(x).getSelectedCouponId())) {
                            totalItemPrice = totalItemPrice + Integer.parseInt(cart_item_modelList.get(x).getProductPrice()) * quantity;
                        } else {
                            totalItemPrice = totalItemPrice + Integer.parseInt(cart_item_modelList.get(x).getDiscountedPrice()) * quantity;
                        }

                        if (!TextUtils.isEmpty(cart_item_modelList.get(x).getCuttedPrice())) {
                            savedAmount = savedAmount + (Integer.parseInt(cart_item_modelList.get(x).getCuttedPrice()) - Integer.parseInt(cart_item_modelList.get(x).getProductPrice())) * quantity;
                            if (!TextUtils.isEmpty(cart_item_modelList.get(x).getSelectedCouponId())) {
                                savedAmount = savedAmount + (Integer.parseInt(cart_item_modelList.get(x).getProductPrice()) - Integer.parseInt(cart_item_modelList.get(x).getDiscountedPrice())) * quantity;
                            }
                        } else {
                            if (TextUtils.isEmpty(cart_item_modelList.get(x).getSelectedCouponId())) {
                                savedAmount = savedAmount + (Integer.parseInt(cart_item_modelList.get(x).getProductPrice()) - Integer.parseInt(cart_item_modelList.get(x).getDiscountedPrice())) * quantity;
                            }
                        }
                    }
                }
                if (totalItemPrice > 500) {
                    deliveryPrice = "FREE";
                    totalAmount = totalItemPrice;
                } else {
                    deliveryPrice = "60";
                    totalAmount = totalItemPrice + 60;
                }

                cart_item_modelList.get(position).setTotalItems(totalItems);
                cart_item_modelList.get(position).setTotalitemsPrice(totalItemPrice);
                cart_item_modelList.get(position).setDeliveryPrice(deliveryPrice);
                cart_item_modelList.get(position).setTotalAmount(totalAmount);
                cart_item_modelList.get(position).setSavedAmount(savedAmount);
                ((CartTotalAmountViewHolder) viewHolder).setTotalAmount(totalItems, totalItemPrice, deliveryPrice, totalAmount, savedAmount);
                break;
            default:
                return;
        }
        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(viewHolder.itemView.getContext(), R.anim.fade_in);
            viewHolder.itemView.setAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return cart_item_modelList.size();
    }

    class CartItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productTitle;
        private TextView freeCoupons;
        private TextView productPrice;
        private TextView cuttedPrice;
        private TextView offersApplied;
        private TextView couponsApplied;
        private TextView productQuantity;
        private ImageView freeCouponIcon;
        private LinearLayout deleteBtn;
        private LinearLayout couponRedemptionLayout;
        private Button redeemBtn;
        private TextView couponRedemptionBody;
        private ImageView codIndicator;


        ///Coupon Dialog
        private TextView couponTitle;
        private TextView couponBody;
        private TextView couponExpiryDate;
        private RecyclerView couponsRecyclerView;
        private LinearLayout selectedCoupon;
        private TextView discountedPrice;
        private TextView originalPrice;
        private LinearLayout applyORremoveBtnContainer;
        private TextView footerText;
        private Button removeCouponBtn, applyCouponBtn;
        private String productOriginalPrice;
        ///Coupon Dialog

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            freeCouponIcon = itemView.findViewById(R.id.free_coupon_icon);
            freeCoupons = itemView.findViewById(R.id.tv_free_coupon);
            productPrice = itemView.findViewById(R.id.product_price);
            cuttedPrice = itemView.findViewById(R.id.cutted_price);
            offersApplied = itemView.findViewById(R.id.offers_applied);
            couponsApplied = itemView.findViewById(R.id.coupons_applied);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            deleteBtn = itemView.findViewById(R.id.remove_item_button);
            redeemBtn = itemView.findViewById(R.id.coupon_redemption_button);
            couponRedemptionLayout = itemView.findViewById(R.id.coupon_redemption_layout);
            couponRedemptionBody = itemView.findViewById(R.id.tv_coupon_redemption);
            codIndicator = itemView.findViewById(R.id.cod_indicator);
        }

        private void setItemDetails(final String productID, String resource, String title, Long freecouponsnumber, final String productPriceText, String cuttedPricetext, Long offersAppliedNumber, final int position, boolean inStock, final String quantity, final Long maxQuanitity, boolean qtyError, final List<String> qtyIds, final long stockQty, boolean COD) {

            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.icon_categories)).into(productImage);
            productTitle.setText(title);

            final Dialog checkCouponPriceDialog = new Dialog(itemView.getContext());
            checkCouponPriceDialog.setContentView(R.layout.coupon_redeem_dialog);
            checkCouponPriceDialog.setCancelable(false);
            checkCouponPriceDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            if (COD) {
                codIndicator.setVisibility(View.VISIBLE);
            } else {
                codIndicator.setVisibility(View.INVISIBLE);
            }

            if (inStock) {
                if (freecouponsnumber > 0) {
                    freeCouponIcon.setVisibility(View.VISIBLE);
                    freeCoupons.setVisibility(View.VISIBLE);
                    if (freecouponsnumber == 1) {
                        freeCoupons.setText("Free " + freecouponsnumber + " Coupon");
                    } else {
                        freeCoupons.setText("Free " + freecouponsnumber + " Coupons");
                    }
                } else {
                    freeCouponIcon.setVisibility(View.INVISIBLE);
                    freeCoupons.setVisibility(View.INVISIBLE);
                }

                productPrice.setText("Rs. " + productPriceText + "/-");
                productPrice.setTextColor(Color.parseColor("#000000"));
                cuttedPrice.setText("Rs. " + cuttedPricetext + "/-");
                couponRedemptionLayout.setVisibility(View.VISIBLE);


                ////Coupon Dialog

                ImageView toggleRecyclerView = checkCouponPriceDialog.findViewById(R.id.toggle_recyclerview);
                couponsRecyclerView = checkCouponPriceDialog.findViewById(R.id.coupons_recyclerview);
                selectedCoupon = checkCouponPriceDialog.findViewById(R.id.selected_coupon);

                couponTitle = checkCouponPriceDialog.findViewById(R.id.coupon_title);
                couponExpiryDate = checkCouponPriceDialog.findViewById(R.id.coupon_validity);
                couponBody = checkCouponPriceDialog.findViewById(R.id.coupon_body);
                footerText = checkCouponPriceDialog.findViewById(R.id.footer_text);
                applyORremoveBtnContainer = checkCouponPriceDialog.findViewById(R.id.apply_remove_buttons_container);
                removeCouponBtn = checkCouponPriceDialog.findViewById(R.id.removeBtn);
                applyCouponBtn = checkCouponPriceDialog.findViewById(R.id.applyBtn);

                footerText.setVisibility(View.GONE);
                applyORremoveBtnContainer.setVisibility(View.VISIBLE);

                originalPrice = checkCouponPriceDialog.findViewById(R.id.original_price);
                discountedPrice = checkCouponPriceDialog.findViewById(R.id.discounted_price);

                LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
                layoutManager.setOrientation(RecyclerView.VERTICAL);
                couponsRecyclerView.setLayoutManager(layoutManager);

                productOriginalPrice = productPriceText;
                originalPrice.setText(productPrice.getText());
                final RewardAdapter rewardAdapter = new RewardAdapter(position, DBqueries.rewardModelList, true, couponsRecyclerView, selectedCoupon, productOriginalPrice, couponTitle, couponExpiryDate, couponBody, discountedPrice, cart_item_modelList);
                couponsRecyclerView.setAdapter(rewardAdapter);
                rewardAdapter.notifyDataSetChanged();

                applyCouponBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(cart_item_modelList.get(position).getSelectedCouponId())) {
                            for (RewardModel rewardModel : DBqueries.rewardModelList) {
                                if (rewardModel.getCouponId().equals(cart_item_modelList.get(position).getSelectedCouponId())) {
                                    rewardModel.setAlreadyUsed(true);
                                    couponRedemptionLayout.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.coupon_gradient_selected_background));
                                    couponRedemptionBody.setText(rewardModel.getCouponBody());
                                    redeemBtn.setText("Change Coupon");
                                }
                            }
                            couponsApplied.setVisibility(View.VISIBLE);
                            cart_item_modelList.get(position).setDiscountedPrice(discountedPrice.getText().toString().substring(4, discountedPrice.getText().length() - 2));
                            productPrice.setText(discountedPrice.getText());
                            String offerDiscountedAmt = String.valueOf(Long.valueOf(productPriceText) - Long.valueOf(discountedPrice.getText().toString().substring(4, discountedPrice.getText().length() - 2)));
                            couponsApplied.setText("Coupon applied:  - Rs." + offerDiscountedAmt + "/-");
                            notifyItemChanged(cart_item_modelList.size() - 1);
                            checkCouponPriceDialog.dismiss();
                        }
                    }
                });

                removeCouponBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (RewardModel rewardModel : DBqueries.rewardModelList) {
                            if (rewardModel.getCouponId().equals(cart_item_modelList.get(position).getSelectedCouponId())) {
                                rewardModel.setAlreadyUsed(false);
                            }
                        }
                        couponTitle.setText("Coupon");
                        couponExpiryDate.setText("Coupon Validity");
                        couponBody.setText("Tap the icon on the top right corner to select your coupon");
                        couponsApplied.setVisibility(View.INVISIBLE);
                        couponRedemptionLayout.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.coupon_gradient_background));
                        couponRedemptionBody.setText("Apply your coupon here.");
                        redeemBtn.setText("Redeem");
                        cart_item_modelList.get(position).setSelectedCouponId(null);
                        productPrice.setText("Rs. " + productPriceText + "/-");
                        notifyItemChanged(cart_item_modelList.size() - 1);
                        checkCouponPriceDialog.dismiss();

                    }
                });

                toggleRecyclerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialogRecyclerView();
                    }
                });

                if (!TextUtils.isEmpty(cart_item_modelList.get(position).getSelectedCouponId())) {
                    for (RewardModel rewardModel : DBqueries.rewardModelList) {
                        if (rewardModel.getCouponId().equals(cart_item_modelList.get(position).getSelectedCouponId())) {
                            couponRedemptionLayout.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.coupon_gradient_selected_background));
                            couponRedemptionBody.setText(rewardModel.getCouponBody());
                            redeemBtn.setText("Change Coupon");


                            couponBody.setText(rewardModel.getCouponBody());
                            if (rewardModel.getType().equals("Discount")) {
                                couponTitle.setText(rewardModel.getType());
                            } else {
                                couponTitle.setText("Flat Rs. " + rewardModel.getDiscORamt() + " OFF");
                            }

                            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM YYYY");
                            couponExpiryDate.setText("till " + simpleDateFormat.format(rewardModel.getTimestamp()));

                        }
                    }
                    discountedPrice.setText("Rs. " + cart_item_modelList.get(position).getDiscountedPrice() + "/-");
                    couponsApplied.setVisibility(View.VISIBLE);
                    productPrice.setText("Rs. " + cart_item_modelList.get(position).getDiscountedPrice() + "/-");
                    String offerDiscountedAmt = String.valueOf(Long.valueOf(productPriceText) - Long.valueOf(cart_item_modelList.get(position).getDiscountedPrice()));
                    couponsApplied.setText("Coupon applied:  - Rs." + offerDiscountedAmt + "/-");
                } else {
                    couponsApplied.setVisibility(View.INVISIBLE);
                    couponRedemptionLayout.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.coupon_gradient_background));
                    couponRedemptionBody.setText("Apply your coupon here.");
                    redeemBtn.setText("Redeem");
                }

                ////Coupon Dialog


                productQuantity.setText("Qty: " + quantity);
                if (!showDeleteBtn) {
                    if (qtyError) {
                        productQuantity.setTextColor(itemView.getContext().getResources().getColor(R.color.failurered));
                        productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.failurered)));
                    } else {
                        productQuantity.setTextColor(itemView.getContext().getResources().getColor(android.R.color.black));
                        productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(android.R.color.black)));
                    }
                }


                productQuantity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog quantityDialog = new Dialog(itemView.getContext());
                        quantityDialog.setContentView(R.layout.quantity_dialog);
                        quantityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        quantityDialog.setCancelable(false);
                        final EditText quantityNo = quantityDialog.findViewById(R.id.quantity_number);
                        Button cancelBtn = quantityDialog.findViewById(R.id.cancel_btn);
                        Button okBtn = quantityDialog.findViewById(R.id.okay_btn);
                        quantityNo.setHint("Max: " + String.valueOf(maxQuanitity));
                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                quantityDialog.dismiss();
                            }
                        });

                        okBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!TextUtils.isEmpty(quantityNo.getText())) {
                                    if (Long.valueOf(quantityNo.getText().toString()) <= maxQuanitity && Long.valueOf(quantityNo.getText().toString()) != 0) {
                                        if (itemView.getContext() instanceof MainActivity) {
                                            cart_item_modelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                        } else {
                                            if (DeliveryActivity.fromCart) {
                                                cart_item_modelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                            } else {
                                                DeliveryActivity.cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                            }
                                        }
                                        productQuantity.setText("Qty: " + quantityNo.getText());
                                        notifyItemChanged(cart_item_modelList.size() - 1);
                                        if (!showDeleteBtn) {       //agar delivery activity me hai
                                            DeliveryActivity.loadingDialog.show();
                                            DeliveryActivity.cartItemModelList.get(position).setQtyError(false);
                                            final int initialQty = Integer.parseInt(quantity);
                                            final int finalQty = Integer.parseInt(quantityNo.getText().toString());
                                            final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                                            if (finalQty > initialQty) {

                                                for (int y = 0; y < finalQty - initialQty; y++) {
                                                    final String quantityDocumentName = UUID.randomUUID().toString().substring(0, 20);

                                                    Map<String, Object> timestamp = new HashMap<>();
                                                    timestamp.put("time", FieldValue.serverTimestamp());
                                                    final int finalY = y;
                                                    firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY").document(quantityDocumentName).set(timestamp)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    qtyIds.add(quantityDocumentName);

                                                                    if (finalY + 1 == finalQty - initialQty) {

                                                                        firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).limit(stockQty).get()
                                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            List<String> serverQuantity = new ArrayList<>();

                                                                                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                                                                serverQuantity.add(queryDocumentSnapshot.getId());
                                                                                            }

                                                                                            long availableQty = 0;

                                                                                            for (String qtyId : qtyIds) {
                                                                                                if (!serverQuantity.contains(qtyId)) {
                                                                                                    DeliveryActivity.cartItemModelList.get(position).setQtyError(true);
                                                                                                    DeliveryActivity.cartItemModelList.get(position).setMaxQuanity(availableQty);
                                                                                                    Toast.makeText(itemView.getContext(), "Sorry! All products may not be avaiable in required quantity", Toast.LENGTH_SHORT).show();
                                                                                                } else {
                                                                                                    availableQty++;

                                                                                                }
                                                                                            }
                                                                                            DeliveryActivity.cart_item_adapter.notifyDataSetChanged();
                                                                                        } else {
                                                                                            String error = task.getException().getMessage();
                                                                                            Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                        DeliveryActivity.loadingDialog.dismiss();
                                                                                    }
                                                                                });

                                                                    }
                                                                }
                                                            });
                                                }
                                            } else if (initialQty > finalQty) {
                                                for (int x = 0; x < initialQty - finalQty; x++) {
                                                    final String qtyId = qtyIds.get(qtyIds.size() - 1 - x);
                                                    final int finalX = x;
                                                    firebaseFirestore.collection("PRODUCTS").document(productID)
                                                            .collection("QUANTITY").document(qtyId).delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    qtyIds.remove(qtyId);
                                                                    DeliveryActivity.cart_item_adapter.notifyDataSetChanged();
                                                                    if (finalX + 1 == initialQty - finalQty) {
                                                                        DeliveryActivity.loadingDialog.dismiss();
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        }
                                    } else {
                                        Toast.makeText(itemView.getContext(), "Enter Quantity between 0 and " + maxQuanitity.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                quantityDialog.dismiss();
                            }
                        });
                        quantityDialog.show();
                    }
                });

                if (offersAppliedNumber > 0) {
                    offersApplied.setVisibility(View.VISIBLE);
                    String offerDiscountedAmt = String.valueOf(Long.valueOf(cuttedPricetext) - Long.valueOf(productPriceText));
                    offersApplied.setText("Offer applied:  -Rs." + offerDiscountedAmt + "/-");
                } else {
                    offersApplied.setVisibility(View.INVISIBLE);
                }

            } else {
                productPrice.setText("Out of Stock");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
                cuttedPrice.setText("");
                couponRedemptionLayout.setVisibility(View.GONE);
                freeCoupons.setVisibility(View.INVISIBLE);
                productQuantity.setVisibility(View.INVISIBLE);
                couponsApplied.setVisibility(View.GONE);
                offersApplied.setVisibility(View.GONE);
                freeCouponIcon.setVisibility(View.INVISIBLE);

            }

            if (showDeleteBtn) { // agar cart me hai
                deleteBtn.setVisibility(View.VISIBLE);
            } else {
                //agar delivery activity me hai
                deleteBtn.setVisibility(View.GONE);
            }


            redeemBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (RewardModel rewardModel : DBqueries.rewardModelList) {
                        if (rewardModel.getCouponId().equals(cart_item_modelList.get(position).getSelectedCouponId())) {
                            rewardModel.setAlreadyUsed(false);
                        }
                    }
                    checkCouponPriceDialog.show();
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(cart_item_modelList.get(position).getSelectedCouponId())) {
                        for (RewardModel rewardModel : DBqueries.rewardModelList) {
                            if (rewardModel.getCouponId().equals(cart_item_modelList.get(position).getSelectedCouponId())) {
                                rewardModel.setAlreadyUsed(false);
                            }
                        }
                    }
                    if (!ProductDetailsActivity.running_cart_query) {
                        ProductDetailsActivity.running_cart_query = true;
                        DBqueries.removeFromCart(position, itemView.getContext(), cartTotalAmount);
                    }
                }
            });
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
    }

    class CartTotalAmountViewHolder extends RecyclerView.ViewHolder {

        private TextView total_items;
        private TextView total_item_price;
        private TextView delivery_price;
        private TextView total_amount;
        private TextView saved_amount;


        public CartTotalAmountViewHolder(@NonNull View itemView) {

            super(itemView);
            total_items = itemView.findViewById(R.id.total_items);
            total_item_price = itemView.findViewById(R.id.total_items_price);
            delivery_price = itemView.findViewById(R.id.delivery_charge);
            total_amount = itemView.findViewById(R.id.total_price);
            saved_amount = itemView.findViewById(R.id.saved_amount);
        }

        private void setTotalAmount(int totalItemText, int totalItemPriceText, String deliveryPriceText, int totalAmountText, int savedAmounttext) {
            total_items.setText("Price(" + totalItemText + " items)");
            total_item_price.setText("Rs." + totalItemPriceText + "/-");
            if (deliveryPriceText.equals("FREE")) {
                delivery_price.setText(deliveryPriceText);
            } else {
                delivery_price.setText("Rs." + deliveryPriceText + "/-");
            }
            total_amount.setText("Rs." + totalAmountText + "/-");
            cartTotalAmount.setText("Rs." + totalAmountText + "/-");
            saved_amount.setText("You saved Rs. " + savedAmounttext + "/- on this order");

            LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
            if (totalItemPriceText == 0) {
                if (DeliveryActivity.fromCart) {
                    cart_item_modelList.remove(cart_item_modelList.size() - 1);
                    DeliveryActivity.cartItemModelList.remove(DeliveryActivity.cartItemModelList.size() - 1);
                }
                if (showDeleteBtn) {
                    cart_item_modelList.remove(cart_item_modelList.size() - 1);
                }
                parent.setVisibility(View.GONE);
            } else {
                parent.setVisibility(View.VISIBLE);
            }
        }
    }
}

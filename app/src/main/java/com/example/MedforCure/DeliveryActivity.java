package com.example.MedforCure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.easypay.utils.EasypayLoaderService;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.example.MedforCure.DBqueries.firebaseFirestore;

public class DeliveryActivity extends AppCompatActivity {

    public static List<Cart_Item_Model> cartItemModelList;
    private RecyclerView deliveryRecyclerView;
    public static Cart_Item_Adapter cart_item_adapter;
    private Button ChangeORaddNewAddressButton;
    public static final int SELECT_ADDRESS = 0;
    private TextView totalAmount;
    private TextView fullName;
    private String name, mobileNo;
    private TextView fullAddress;
    private TextView pincode;
    public static Dialog loadingDialog;
    private Dialog paymentMethodDialog;
    private Button continueBtn;
    private FirebaseFirestore firebaseFirestore;
    private TextView codTitle;
    private View divider;
    private ImageButton paytm, COD;
    private ConstraintLayout orderConfirmationLayout;
    private ImageButton continueShoppingBtn;
    private TextView orderId;
    private String paymentMethod = "PAYTM";
    private String order_id;
    public static boolean codOrderConfirm = false;


    private boolean successResponse = false;
    public static boolean fromCart;

    public static boolean getQtyIDs = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Delivery");
        deliveryRecyclerView = findViewById(R.id.delivery_recycler_view);
        ChangeORaddNewAddressButton = findViewById(R.id.change_or_add_address_button);
        totalAmount = findViewById(R.id.total_cart_amount);
        fullName = findViewById(R.id.address_full_name);
        fullAddress = findViewById(R.id.address);
        pincode = findViewById(R.id.address_pincode);
        continueBtn = findViewById(R.id.cart_continue_button);
        orderConfirmationLayout = findViewById(R.id.order_confirmation_layout);
        continueShoppingBtn = findViewById(R.id.continue_shopping_btn);
        orderId = findViewById(R.id.order_id);
        firebaseFirestore = FirebaseFirestore.getInstance();
        // Loading Dialog
        loadingDialog = new Dialog(DeliveryActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false); // Agar user dialog ke bahar click karega toh dialog  bandd nahi hoga
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // Loading Dialog

        // Payment Method Dialog
        paymentMethodDialog = new Dialog(DeliveryActivity.this);
        paymentMethodDialog.setContentView(R.layout.payment_method);
        paymentMethodDialog.setCancelable(true); // Agar user dialog ke bahar click karega toh dialog khud bandd ho jayega
        paymentMethodDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        paymentMethodDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paytm = paymentMethodDialog.findViewById(R.id.paytmBtn);
        COD = paymentMethodDialog.findViewById(R.id.codBtn);
        codTitle = paymentMethodDialog.findViewById(R.id.cod_btn_title);
        divider = paymentMethodDialog.findViewById(R.id.paymentmethoddivider);
        // Payment Method Dialog


        firebaseFirestore = FirebaseFirestore.getInstance();
        getQtyIDs = true;

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);  //Jab activity me hote hai toh this pass karte hai jab fragment me hote hai toh getcontext() pass karte hai
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        deliveryRecyclerView.setLayoutManager(layoutManager);

        cart_item_adapter = new Cart_Item_Adapter(cartItemModelList, totalAmount, false);
        deliveryRecyclerView.setAdapter(cart_item_adapter);


        cart_item_adapter.notifyDataSetChanged();

        ChangeORaddNewAddressButton.setVisibility(View.VISIBLE);
        ChangeORaddNewAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getQtyIDs = false;
                Intent myAddressesIntent = new Intent(DeliveryActivity.this, MyAddressesActivity.class);
                myAddressesIntent.putExtra("MODE", SELECT_ADDRESS); //Intent ke sang agar koi extra value bhejni hai uske lie hai
                startActivity(myAddressesIntent);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean allProductsAvaiable = true;
                for (Cart_Item_Model cartItemModel : cartItemModelList) {
                    if (cartItemModel.isQtyError()) {
                        allProductsAvaiable = false;
                        break;
                    }
                    if (cartItemModel.getType() == Cart_Item_Model.CART_ITEM) {
                        if (!cartItemModel.isCOD()) {
                            COD.setEnabled(false);
                            COD.setAlpha(0.5f);
                            codTitle.setAlpha(0.5f);
                            break;
                        } else {
                            COD.setEnabled(true);
                            COD.setAlpha(1f);
                            codTitle.setAlpha(1f);
                        }
                    }
                }
                if (allProductsAvaiable) {
                    paymentMethodDialog.show();
                }
            }
        });

        COD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethod = "COD";
                placeOrderDetails();
            }
        });


        paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethod = "PAYTM";
                placeOrderDetails();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        ///accessing quantity
        if (getQtyIDs) {
            loadingDialog.show();
            for (int x = 0; x < cartItemModelList.size() - 1; x++) { // for products

                for (int y = 0; y < cartItemModelList.get(x).getProductQuantity(); y++) { // for quantity of each product
                    final String quantityDocumentName = UUID.randomUUID().toString().substring(0, 20);

                    Map<String, Object> timestamp = new HashMap<>();
                    timestamp.put("time", FieldValue.serverTimestamp());
                    final int finalX = x;
                    final int finalY = y;
                    firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductId()).collection("QUANTITY").document(quantityDocumentName).set(timestamp)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        cartItemModelList.get(finalX).getQtyIDs().add(quantityDocumentName);

                                        if (finalY + 1 == cartItemModelList.get(finalX).getProductQuantity()) {

                                            firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(finalX).getProductId()).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).limit(cartItemModelList.get(finalX).getStockQuanity()).get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                List<String> serverQuantity = new ArrayList<>();

                                                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                                    serverQuantity.add(queryDocumentSnapshot.getId());
                                                                }

                                                                long availableQty = 0;
                                                                boolean noLongerAvailable = true;

                                                                for (String qtyId : cartItemModelList.get(finalX).getQtyIDs()) {
                                                                    cartItemModelList.get(finalX).setQtyError(true);

                                                                    if (!serverQuantity.contains(qtyId)) {
                                                                        if (noLongerAvailable) {
                                                                            cartItemModelList.get(finalX).setInStock(false);
                                                                        } else {
                                                                            cartItemModelList.get(finalX).setQtyError(true);
                                                                            cartItemModelList.get(finalX).setMaxQuanity(availableQty);
                                                                            Toast.makeText(DeliveryActivity.this, "Sorry! All products may not be avaiable in required quantity", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    } else {
                                                                        availableQty++;
                                                                        noLongerAvailable = false;
                                                                    }
                                                                }
                                                                cart_item_adapter.notifyDataSetChanged();
                                                            } else {
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                                            }
                                                            loadingDialog.dismiss();
                                                        }
                                                    });

                                        }
                                    } else {
                                        loadingDialog.dismiss();
                                        String error = task.getException().getMessage();
                                        Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        } else {
            getQtyIDs = true;
        }
        ///accessing quantity

        name = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getName();
        mobileNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getMobileNo();
        if (DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternateMobileNo().equals("")) {
            fullName.setText(name + " - " + mobileNo);
        } else {
            fullName.setText(name + " - " + mobileNo + " or " + DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternateMobileNo());
        }
        String flatNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getFlatno();
        String locality = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLocality();
        String landMark = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLandMark();
        String city = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getCity();
        String state = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getState();

        if (landMark.equals("")) {
            fullAddress.setText(flatNo + ", " + locality + ", " + city + ", " + state);

        } else {
            fullAddress.setText(flatNo + ", " + locality + ", " + landMark + ", " + city + ", " + state);

        }
        pincode.setText(DBqueries.addressesModelList.get(DBqueries.selectedAddress).getPincode());
        if (codOrderConfirm) {
            showConfirmationLayout();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        loadingDialog.dismiss();

        if (getQtyIDs) {
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {
                if (!successResponse) {
                    for (final String qtyID : cartItemModelList.get(x).getQtyIDs()) {
                        final int finalX = x;
                        firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductId())
                                .collection("QUANTITY").document(qtyID).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (qtyID.equals(cartItemModelList.get(finalX).getQtyIDs().get(cartItemModelList.get(finalX).getQtyIDs().size() - 1))) {
                                            cartItemModelList.get(finalX).getQtyIDs().clear();
                                        }
                                    }
                                });
                    }
                } else {
                    cartItemModelList.get(x).getQtyIDs().clear();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (successResponse) {
            finish();
            return;
        }
        super.onBackPressed();
    }

    private void showConfirmationLayout() {
        successResponse = true;
        codOrderConfirm = false;
        getQtyIDs = false;

        for (int x = 0; x < cartItemModelList.size() - 1; x++) {

            for (String qtyID : cartItemModelList.get(x).getQtyIDs()) {

                firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductId())
                        .collection("QUANTITY").document(qtyID).update("user_ID", FirebaseAuth.getInstance().getUid());

            }


        }

        if (MainActivity.mainActivity != null) {
            MainActivity.mainActivity.finish();
            MainActivity.mainActivity = null;
            MainActivity.showCart = false;
        } else {
            MainActivity.resetMainActivity = true;
        }
        if (ProductDetailsActivity.productDetailsActivity != null) {
            ProductDetailsActivity.productDetailsActivity.finish();
            ProductDetailsActivity.productDetailsActivity = null;

        }
        ///send confirmation sms
        String SMS_API = "https://www.fast2sms.com/dev/bulkV2";
        final String message = "Thanks for shopping with us! Your order is confirmed and will be shipped shortly. Your Order Id is " + String.valueOf(order_id);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SMS_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//nothing
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //nothing

            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("authorization", "h6l52x9AdynYM1QfTpXvr8sGRucEJUjqFePbKtzWCHaZSo4D7Lmio1cDBgI0G2fj7KO5Yar9JL4tHZkW");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> body = new HashMap<>();
                body.put("message", message);
                body.put("language", "english");
                body.put("route", "q");
                body.put("numbers", mobileNo);
                body.put("flash", "0");
                return body;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(DeliveryActivity.this);
        requestQueue.add(stringRequest);
        ///send confirmation sms

        if (fromCart) {
            loadingDialog.show();
            Map<String, Object> updateCartlist = new HashMap<>();
            long cartListSize = 0;
            final List<Integer> indexList = new ArrayList<>();
            for (int x = 0; x < DBqueries.cartList.size(); x++) {
                if (!cartItemModelList.get(x).isInStock()) {
                    updateCartlist.put("product_ID" + cartListSize, cartItemModelList.get(x).getProductId());
                    cartListSize++;
                } else {
                    indexList.add(x);
                }
            }

            updateCartlist.put("list_size", cartListSize);

            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                    .document("MY_CART").set(updateCartlist).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        for (int x = 0; x < indexList.size(); x++) {
                            DBqueries.cartList.remove(indexList.get(x).intValue());
                            DBqueries.cartItemModelList.remove(indexList.get(x).intValue());
                            DBqueries.cartItemModelList.remove(DBqueries.cartItemModelList.size() - 1);

                        }

                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.dismiss();
                }
            });
        }

        continueBtn.setEnabled(false);
        ChangeORaddNewAddressButton.setEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        orderId.setText("Order Id:" + order_id);
        orderConfirmationLayout.setVisibility(View.VISIBLE);
        continueShoppingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void placeOrderDetails() {
        loadingDialog.show();
        order_id = UUID.randomUUID().toString().substring(0, 28);
        String userID = FirebaseAuth.getInstance().getUid();
        for (Cart_Item_Model cartItemModel : cartItemModelList) {
            if (cartItemModel.getType() == Cart_Item_Model.CART_ITEM) {
                Map<String, Object> orderDetails = new HashMap<>();
                orderDetails.put("ORDER ID", order_id);
                orderDetails.put("PRODUCT ID", cartItemModel.getProductId());
                orderDetails.put("PRODUCT IMAGE", cartItemModel.getProductImage());
                orderDetails.put("PRODUCT TITLE", cartItemModel.getProductTitle());

                orderDetails.put("USER ID", userID);
                orderDetails.put("PRODUCT QUANTITY", cartItemModel.getProductQuantity());
                if (cartItemModel.getCuttedPrice() != null) {
                    orderDetails.put("CUTTED PRICE", cartItemModel.getCuttedPrice());
                } else {
                    orderDetails.put("CUTTED PRICE", "");
                }
                orderDetails.put("PRODUCT PRICE", cartItemModel.getProductPrice());
                if (cartItemModel.getSelectedCouponId() != null) {
                    orderDetails.put("COUPON ID", cartItemModel.getSelectedCouponId());
                } else {
                    orderDetails.put("COUPON ID", "");

                }
                if (cartItemModel.getDiscountedPrice() != null) {
                    orderDetails.put("DISCOUNTED PRICE", cartItemModel.getDiscountedPrice());
                } else {
                    orderDetails.put("DISCOUNTED PRICE", "");

                }
                orderDetails.put("ORDERED DATE", FieldValue.serverTimestamp());
                orderDetails.put("PACKED DATE", FieldValue.serverTimestamp());
                orderDetails.put("SHIPPED DATE", FieldValue.serverTimestamp());
                orderDetails.put("DELIVERED DATE", FieldValue.serverTimestamp());
                orderDetails.put("CANCELLED DATE", FieldValue.serverTimestamp());
                orderDetails.put("ORDER STATUS", "Ordered");
                orderDetails.put("PAYMENT METHOD", paymentMethod);
                orderDetails.put("ADDRESS", fullAddress.getText());
                orderDetails.put("FULL NAME", fullName.getText());
                orderDetails.put("PINCODE", pincode.getText());
                if (cartItemModel.getFreeCoupons() != null) {
                    orderDetails.put("FREE COUPONS", cartItemModel.getFreeCoupons());
                } else {
                    orderDetails.put("FREE COUPONS", "");
                }
                orderDetails.put("DELIVERY PRICE", cartItemModelList.get(cartItemModelList.size() - 1).getDeliveryPrice());
                orderDetails.put("CANCELLATION REQUESTED", false);


                firebaseFirestore.collection("ORDERS").document(order_id).collection("OrderItems")
                        .document(cartItemModel.getProductId()).set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            String error = task.getException().getMessage();
                            Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Map<String, Object> orderDetails = new HashMap<>();
                orderDetails.put("TOTAL ITEMS", cartItemModel.getTotalItems());
                orderDetails.put("TOTAL ITEMS PRICE", cartItemModel.getTotalitemsPrice());
                orderDetails.put("DELIVERY PRICE", cartItemModel.getDeliveryPrice());
                orderDetails.put("TOTAL AMOUNT", cartItemModel.getTotalAmount());
                orderDetails.put("SAVED AMOUNT", cartItemModel.getSavedAmount());
                orderDetails.put("PAYMENT STATUS", "Not Paid");
                orderDetails.put("ORDER STATUS", "Cancelled");
                firebaseFirestore.collection("ORDERS").document(order_id)
                        .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (paymentMethod.equals("PAYTM")) {
                                paytm();
                            } else {
                                cod();
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private void paytm() {
        getQtyIDs = false;
        paymentMethodDialog.dismiss();
        loadingDialog.show();
//                Toast.makeText(DeliveryActivity.this, "Paytm Payment Gateway not Integrated", Toast.LENGTH_SHORT).show();
//                loadingDialog.dismiss();

////                //comment from here

        if (ContextCompat.checkSelfPermission(DeliveryActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DeliveryActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }

        final String M_id = "LmuQHS54079637602585";
        final String customer_id = FirebaseAuth.getInstance().getUid();
        String url = "https://medforcure.000webhostapp.com/paytm/sample.php";
        final String callBackUrl = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=" + order_id;
        RequestQueue requestQueue = Volley.newRequestQueue(DeliveryActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("CHECKSUMHASH")) {
                        String CHECKSUMHASH = jsonObject.getString("CHECKSUMHASH");
                        PaytmPGService paytmPGService = PaytmPGService.getStagingService(null);
                        HashMap<String, String> paytmParams = new HashMap<String, String>();
                        paytmParams.put("MID", M_id);
                        paytmParams.put("ORDER_ID", order_id); //yaha order id pass karni hoti hai
                        paytmParams.put("CUST_ID", customer_id);
                        paytmParams.put("CHANNEL_ID", "WAP");
                        paytmParams.put("TXN_AMOUNT", totalAmount.getText().toString().substring(3, totalAmount.getText().length() - 2));
                        paytmParams.put("WEBSITE", "WEBSTAGING");
                        paytmParams.put("INDUSTRY_TYPE_ID", "Retail");
                        paytmParams.put("CALLBACK_URL", callBackUrl);
                        paytmParams.put("CHECKSUMHASH", CHECKSUMHASH);

                        PaytmOrder order = new PaytmOrder(paytmParams);

                        paytmPGService.initialize(order, null);
                        paytmPGService.startPaymentTransaction(DeliveryActivity.this, true, true, new PaytmPaymentTransactionCallback() {
                            @Override
                            public void onTransactionResponse(Bundle inResponse) {
//                                        Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
                                if (inResponse.getString("STATUS").equals("TXN_SUCCESS")) {
                                    Map<String, Object> updateStatus = new HashMap<>();
                                    updateStatus.put("PAYMENT STATUS", "Paid");
                                    updateStatus.put("ORDER STATUS", "Ordered");
                                    firebaseFirestore.collection("ORDERS").document(order_id)
                                            .update(updateStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Map<String, Object> userOrder = new HashMap<>();
                                                userOrder.put("order_id", order_id);
                                                userOrder.put("time", FieldValue.serverTimestamp());
                                                firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                                                        .collection("USER_ORDERS").document(order_id).set(userOrder)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    showConfirmationLayout();
                                                                } else {
                                                                    Toast.makeText(DeliveryActivity.this, "Failed to update User's Order list", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });

                                            } else {
                                                Toast.makeText(DeliveryActivity.this, "ORDER CANCELLED", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                }

                            }

                            @Override
                            public void networkNotAvailable() {
                                Toast.makeText(getApplicationContext(), "Network connection error: Check your internet connectivity", Toast.LENGTH_LONG).show();
                            }


                            @Override
                            public void clientAuthenticationFailed(String inErrorMessage) {
                                Toast.makeText(getApplicationContext(), "Authentication failed: Server error" + inErrorMessage.toString(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void someUIErrorOccurred(String inErrorMessage) {
                                Toast.makeText(getApplicationContext(), "UI Error " + inErrorMessage, Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {

                                Toast.makeText(getApplicationContext(), "Unable to load webpage " + inErrorMessage.toString(), Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void onBackPressedCancelTransaction() {
                                Toast.makeText(getApplicationContext(), "Transaction cancelled", Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                                Toast.makeText(getApplicationContext(), "Transaction cancelled" + inResponse.toString(), Toast.LENGTH_LONG).show();

                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
                Toast.makeText(DeliveryActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> paytmParams = new HashMap<String, String>();
                paytmParams.put("MID", M_id);
                paytmParams.put("ORDER_ID", order_id);
                paytmParams.put("CUST_ID", customer_id);
                paytmParams.put("CHANNEL_ID", "WAP");
                paytmParams.put("TXN_AMOUNT", totalAmount.getText().toString().substring(3, totalAmount.getText().length() - 2));
                paytmParams.put("WEBSITE", "WEBSTAGING");
                paytmParams.put("INDUSTRY_TYPE_ID", "Retail");
                paytmParams.put("CALLBACK_URL", callBackUrl);
                return paytmParams;
            }
        };

        requestQueue.add(stringRequest);

        //comment till here
    }

    private void cod() {
        getQtyIDs = false;
        paymentMethodDialog.dismiss();
        Intent otpIntent = new Intent(DeliveryActivity.this, OTPverificationActivity.class);
        otpIntent.putExtra("mobile_no", mobileNo.substring(0, 10));
        otpIntent.putExtra("order_id", order_id);
        startActivity(otpIntent);
    }
}
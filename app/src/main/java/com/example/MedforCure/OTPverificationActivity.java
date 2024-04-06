package com.example.MedforCure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OTPverificationActivity extends AppCompatActivity {

    private TextView phoneNo;
    private EditText otp;
    private Button verifyBtn;
    private String userMobileNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_pverification);

        phoneNo = findViewById(R.id.phone_no);
        otp = findViewById(R.id.otp);
        verifyBtn = findViewById(R.id.verifyBtn);
        userMobileNo = getIntent().getStringExtra("mobile_no");
        phoneNo.setText("Verification code has been sent to +91 *******" + userMobileNo.substring(7, 10));

        Random random = new Random();
        final int OTPnumber = random.nextInt(999999 - 111111) + 111111;
        Toast.makeText(this, String.valueOf(OTPnumber), Toast.LENGTH_SHORT).show();
        String SMS_API = "https://www.fast2sms.com/dev/bulkV2";
        final String message = "Dear Customer, your OTP verification code is: " + String.valueOf(OTPnumber);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SMS_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                verifyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (otp.getText().toString().equals(String.valueOf(OTPnumber))) {

                            Map<String, Object> updateStatus = new HashMap<>();
                            updateStatus.put("ORDER STATUS", "Ordered");

                            final String order_id = getIntent().getStringExtra("order_id");
                            FirebaseFirestore.getInstance().collection("ORDERS").document(order_id)
                                    .update(updateStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Map<String, Object> userOrder = new HashMap<>();
                                        userOrder.put("order_id", order_id);
                                        userOrder.put("time", FieldValue.serverTimestamp());

                                        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                                                .collection("USER_ORDERS").document(order_id).set(userOrder)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            DeliveryActivity.codOrderConfirm = true;
                                                            finish();
                                                        } else {
                                                            Toast.makeText(OTPverificationActivity.this, "Failed to update User's Order list", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });


                                    } else {
                                        Toast.makeText(OTPverificationActivity.this, "ORDER CANCELLED", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(OTPverificationActivity.this, "Incorrect OTP!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                finish();
                Toast.makeText(OTPverificationActivity.this, "Failed to send the OTP verification code", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("authorization", "your_own_key");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> body = new HashMap<>();
                body.put("message", message);
                body.put("language", "english");
                body.put("route", "q");
                body.put("numbers", userMobileNo);
                body.put("flash", "0");
                return body;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(OTPverificationActivity.this);
        requestQueue.add(stringRequest);

    }

}
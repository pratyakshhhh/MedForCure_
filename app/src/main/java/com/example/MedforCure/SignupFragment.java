package com.example.MedforCure;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.graphics.Color.argb;
import static com.example.MedforCure.R.*;


public class SignupFragment extends Fragment {

    private TextView alreadyHaveAnAccount;
    private FrameLayout parentFrameLayout;
    private EditText full_name;
    private EditText email;
    private EditText password;
    private EditText confirm_password;
    private ImageButton close_button;
    private Button sign_up_button;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    public static boolean disableclosebutton = false;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SigninFragment());
            }
        });

        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainIntent();

            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        full_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirm_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailandPassword();
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(anim.slide_from_left, anim.slideout_from_right);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(layout.fragment_signup, container, false);
        alreadyHaveAnAccount = view.findViewById(id.already_have_an_account);
        parentFrameLayout = getActivity().findViewById((id.register_framelayout));
        email = view.findViewById(id.sign_up_email);
        full_name = view.findViewById(id.sign_up_full_name);
        password = view.findViewById(id.sign_up_password);
        confirm_password = view.findViewById(id.sign_up_rpassword);

        close_button = view.findViewById(id.sign_up_close);
        sign_up_button = view.findViewById(id.sign_up_button);
        progressBar = view.findViewById(id.sign_up_progress_bar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (disableclosebutton) {
            close_button.setVisibility(View.GONE);
        } else {
            close_button.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @SuppressLint("ResourceAsColor")
    private void checkInputs() {
        if (!TextUtils.isEmpty(full_name.getText())) {
            if (!TextUtils.isEmpty(email.getText())) {
                if (!TextUtils.isEmpty(password.getText()) && password.length() >= 8) {
                    if (!TextUtils.isEmpty(confirm_password.getText())) {
                        sign_up_button.setTextColor(Color.parseColor("#ffffff"));
                        sign_up_button.setEnabled(true);

                    } else {
                        sign_up_button.setTextColor(Color.parseColor("#808080"));
                        sign_up_button.setEnabled(false);

                    }
                } else {
                    sign_up_button.setTextColor(Color.parseColor("#808080"));
                    sign_up_button.setEnabled(false);

                }
            } else {
                sign_up_button.setTextColor(Color.parseColor("#808080"));
                sign_up_button.setEnabled(false);


            }
        } else {
            sign_up_button.setTextColor(Color.parseColor("#808080"));
            sign_up_button.setEnabled(false);
        }
    }

    private void checkEmailandPassword() {
        if (email.getText().toString().matches(emailPattern)) {
            if (password.getText().toString().equals(confirm_password.getText().toString())) {
                progressBar.setVisibility(View.VISIBLE);
                sign_up_button.setEnabled(false);
                sign_up_button.setTextColor(Color.parseColor("#808080"));
                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @SuppressLint("ResourceAsColor")
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("Full Name", full_name.getText().toString());
                                    user.put("Email", email.getText().toString());
                                    user.put("Password", password.getText().toString());
                                    user.put("Profile","");


                                    firebaseFirestore.collection("USERS").document(firebaseAuth.getUid())
                                            .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                CollectionReference userDataReference = firebaseFirestore.collection("USERS").document(firebaseAuth.getUid()).collection("USER_DATA");

                                                ///Maps
                                                Map<String, Object> wishlistMap = new HashMap<>();
                                                wishlistMap.put("list_size", (long) 0);

                                                Map<String, Object> ratingsMap = new HashMap<>();
                                                ratingsMap.put("list_size", (long) 0);

                                                Map<String, Object> cartMap = new HashMap<>();
                                                cartMap.put("list_size", (long) 0);

                                                Map<String, Object> myAddresesMap = new HashMap<>();
                                                myAddresesMap.put("list_size", (long) 0);
                                                ///Maps
                                                final List<String> documentNames = new ArrayList<>();
                                                documentNames.add("MY_WISHLIST");
                                                documentNames.add("MY_RATINGS");
                                                documentNames.add("MY_CART");
                                                documentNames.add("MY_ADDRESSES");

                                                List<Map<String, Object>> documentFields = new ArrayList<>();
                                                documentFields.add(wishlistMap);
                                                documentFields.add(ratingsMap);
                                                documentFields.add(cartMap);
                                                documentFields.add(myAddresesMap);

                                                for (int x = 0; x < documentNames.size(); x++) {
                                                    final int finalX = x;
                                                    userDataReference.document(documentNames.get(x))
                                                            .set(documentFields.get(x)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                if (finalX == documentNames.size() - 1) {
                                                                    mainIntent();

                                                                }
                                                            } else {
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                sign_up_button.setEnabled(true);
                                                                sign_up_button.setTextColor(Color.parseColor("#ffffff"));
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                    });
                                                }

                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });


                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    sign_up_button.setEnabled(true);
                                    sign_up_button.setTextColor(Color.parseColor("#ffffff"));
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                confirm_password.setError("Password doesn't match!");

            }
        } else {
            email.setError("Invalid Email!");
        }

    }

    private void mainIntent() {
        if (disableclosebutton) {
            disableclosebutton = false;
        } else {
            Intent mainIntent = new Intent(getActivity(), MainActivity.class);
            startActivity(mainIntent);
        }
        getActivity().finish();
    }

}

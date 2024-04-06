package com.example.MedforCure;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResetPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResetPasswordFragment extends Fragment {
    private EditText registered_Email;
    private Button reset_password_button;
    private TextView goBack;
    private FrameLayout parentFrameLayout;
    private FirebaseAuth firebaseAuth;
    private ViewGroup emailContainer;
    private TextView emailtext;
    private ProgressBar progressBar;
    private ImageView greenicon;
    private ImageView redicon;


    public ResetPasswordFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_reset_password, container, false);
        registered_Email=view.findViewById(R.id.forgot_password_email);
        reset_password_button=view.findViewById(R.id.reset_password_button);
        goBack=view.findViewById(R.id.forgot_password_go_back);
        parentFrameLayout =  getActivity().findViewById(R.id.register_framelayout);
        firebaseAuth=FirebaseAuth.getInstance();
        emailContainer=view.findViewById(R.id.forgot_password_container);
        emailtext=view.findViewById(R.id.forgot_password_email_text);
        progressBar=view.findViewById(R.id.forgot_password_progress_bar);
        greenicon=view.findViewById(R.id.greenmsgicon);
        redicon=view.findViewById(R.id.redmsgicon);;
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registered_Email.addTextChangedListener(new TextWatcher() {
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

        reset_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                reset_password_button.setEnabled(false);
                reset_password_button.setTextColor(Color.parseColor("#808080"));


                firebaseAuth.sendPasswordResetEmail(registered_Email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {   TransitionManager.beginDelayedTransition(emailContainer);
                        progressBar.setVisibility(View.GONE);
                        emailtext.setText("Recovery email sent successfully! Please check Inbox");
                        emailtext.setTextColor(Color.parseColor("#33691E"));
                        emailContainer.setVisibility(View.VISIBLE);
                        greenicon.setVisibility(View.VISIBLE);
                        redicon.setVisibility(View.GONE);
                        reset_password_button.setEnabled(false);
                        reset_password_button.setTextColor(Color.parseColor("#808080"));

                    }
                    else
                    {   TransitionManager.beginDelayedTransition(emailContainer);
                        String error= task.getException().getMessage();
                        progressBar.setVisibility(View.GONE);
                        emailtext.setText(error);
                        emailtext.setTextColor(Color.parseColor("#FF0000"));
                        emailContainer.setVisibility(View.VISIBLE);
                        greenicon.setVisibility(View.GONE);
                        redicon.setVisibility(View.VISIBLE);
                        reset_password_button.setEnabled(true);
                        reset_password_button.setTextColor(Color.parseColor("#ffffff"));
                    }

                    }
                });
            }
        });
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SigninFragment());


            }
        });
    }

    private void checkInputs() {
        if (TextUtils.isEmpty(registered_Email.getText())) {
            reset_password_button.setEnabled(false);
            reset_password_button.setTextColor(Color.parseColor("#808080"));
        }
        else {
            reset_password_button.setEnabled(true);
            reset_password_button.setTextColor(Color.parseColor("#ffffff"));

        }
    }

    private void setFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slideout_from_right);
        fragmentTransaction.replace(parentFrameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }
}
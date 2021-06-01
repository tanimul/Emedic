package com.example.emadic.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.emadic.KEYS;
import com.example.emadic.R;
import com.example.emadic.Tools;
import com.example.emadic.databinding.ActivityRegisteredLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class ActivityRegisteredLogin extends AppCompatActivity {
    private static final String TAG = "ActivityRegisteredLogin";
    private ActivityRegisteredLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisteredLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(Tools.getPrefBoolean(KEYS.IS_LOGGED_IN,false)){
            Intent intent = new Intent(ActivityRegisteredLogin.this, ActivityHomePage.class);
            startActivity(intent);
            finish();
        }

        binding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.tvPhoneNumber.length() != 11 || binding.tvPhoneNumber.equals("")) {
                    Toast.makeText(ActivityRegisteredLogin.this, "Invalid Phone Number.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(ActivityRegisteredLogin.this, ActivityRegisteredVerificationCode.class);
                    intent.putExtra("phone_NO", "" + binding.tvPhoneNumber.getText().toString());
                    startActivity(intent);finish();
                }

            }
        });
    }

}

//Confirm
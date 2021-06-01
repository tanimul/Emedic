package com.example.emadic.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.emadic.KEYS;
import com.example.emadic.R;
import com.example.emadic.Tools;
import com.example.emadic.databinding.ActivityRegisteredVerificationCodeBinding;
import com.example.emadic.modelclass.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class ActivityRegisteredVerificationCode extends AppCompatActivity {
    private static final String TAG = "Act_Reg_ver_code";
    private ActivityRegisteredVerificationCodeBinding binding;
    public PhoneAuthProvider.ForceResendingToken resendToken;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private FirebaseUser firebaseUser;
    String code_from_system;
    private String phone_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisteredVerificationCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            phone_no = "+88" + extras.getString("phone_NO");
            Log.d(TAG, phone_no);
        }

        sendverificationcode(phone_no);
        binding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.pinOTP.getText().toString().isEmpty()) {
                    verifycode(binding.pinOTP.getText().toString());
                }
            }
        });

        binding.newProfileResentOtpClickingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityRegisteredVerificationCode.this, "Resend Code sent Successfully.please check your phone", Toast.LENGTH_SHORT).show();
                sendverificationcode(phone_no);
            }
        });
    }

    private void sendverificationcode(String phoneNo) {
        Log.d(TAG, "number: " + phoneNo);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNo)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            Log.d(TAG, "onVerificationCompleted:" + credential);
            String code = credential.getSmsCode();
            if (code != null) {
                binding.pinOTP.setText(code);
                verifycode(code);
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed: ", e);

            Toast.makeText(ActivityRegisteredVerificationCode.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            // Show a message and update the UI
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {

            Log.d(TAG, "onCodeSent:" + verificationId);

            // Save verification ID and resending token so we can use them later
            code_from_system = verificationId;
            resendToken = token;
        }
    };

    private void verifycode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(code_from_system, code);

        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(ActivityRegisteredVerificationCode.this, "verification complete", Toast.LENGTH_SHORT).show();
                            checkUserStatus(task.getResult().getUser().getUid());
                        } else {

                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(ActivityRegisteredVerificationCode.this, "Task is not complete", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }

    private void checkUserStatus(String uid) {
        if (uid != null) Log.d(TAG, "UID: " + uid);
        else Log.d(TAG, "UID: null");

        ProgressDialog Dialog = new ProgressDialog(this);
        Dialog.setMessage("Please wait ...");
        Dialog.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User_Registration").child(uid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    UserInfo user = dataSnapshot.getValue(UserInfo.class);

                    Tools.savePrefBoolean(KEYS.IS_LOGGED_IN,true);
                    Tools.savePref(KEYS.USER_NAME,user.getName());
                    Tools.savePref(KEYS.USER_NUMBER,user.getContact_number());
                    Tools.savePref(KEYS.USER_PIC_URL,user.getImage_url());

                    Intent intent2=new Intent(ActivityRegisteredVerificationCode.this, ActivityHomePage.class);
                    intent2.putExtra("U_id", "" + uid);
                    Dialog.dismiss();
                    startActivity(intent2);
                    finish();
                }else {
                    Intent intent = new Intent(ActivityRegisteredVerificationCode.this, ActivityNewProfile.class);
                    intent.putExtra("phone_Number", "" + phone_no);
                    intent.putExtra("U_id", "" + uid);
                    intent.putExtra("edit_profile","no");
                    Dialog.dismiss();
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Dialog.dismiss();
                Toast.makeText(ActivityRegisteredVerificationCode.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onCancelled: database error" + databaseError.getMessage());
            }
        });
    }
}

//confirm
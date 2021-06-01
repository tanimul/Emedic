package com.example.emadic.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.emadic.KEYS;
import com.example.emadic.R;
import com.example.emadic.Tools;
import com.example.emadic.databinding.ActivityEditProfileBinding;
import com.example.emadic.modelclass.UserInfo;
import com.example.emadic.modelclass.UserUpdateInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class Activity_Edit_profile extends AppCompatActivity {
    private static final String TAG = "Activity_Edit_profile";
    private ActivityEditProfileBinding binding;
    private String editProfile_id, profile_key;
    private int image_rec_code = 1;
    private Uri filepath_uri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private String name, blood_grp, medical_condition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (!extras.getString("edit_profile_id").equals("null")) {
                editProfile_id = extras.getString("edit_profile_id");
                updateprofile(editProfile_id);
            }
        }

        Log.d(TAG, "onCreate: " + FirebaseAuth.getInstance().getCurrentUser().getUid());

        binding.btnUpdateRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userUpdateValidation()) {
                    Log.d(TAG, "Validation sucessfull");
                    updateregistration(profile_key);
                }
            }
        });

    }

    private boolean userUpdateValidation() {
        name = binding.editTextNewProfileName.getText().toString();
        blood_grp = binding.editTextYourBloodGourpName.getText().toString().trim();
        medical_condition = binding.editTextMedicalCondition.getText().toString();
        if (name.isEmpty()) {
            binding.editTextNewProfileName.setError("Enter Your Name please");
            binding.editTextNewProfileName.requestFocus();
            return false;
        }
        if (blood_grp.isEmpty()) {

            binding.editTextYourBloodGourpName.setError("Enter Your Blood group please");
            binding.editTextYourBloodGourpName.requestFocus();
            return false;
        }

        if (medical_condition.isEmpty()) {

            binding.editTextMedicalCondition.setError("Enter Your Medical Condition please");
            binding.editTextMedicalCondition.requestFocus();
            return false;

        } else {
            return true;
        }
    }

    private void updateprofile(String u_id) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User_Registration").child(u_id);
        databaseReference.keepSynced(true);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserInfo users = dataSnapshot.getValue(UserInfo.class);
                binding.editTextNewProfileName.setText(users.getName());
                binding.editTextYourBloodGourpName.setText(users.getBlood_grp());
                binding.editTextMedicalCondition.setText(users.getMedical_condition());
                profile_key = dataSnapshot.getKey();
                Picasso.get().load(users.getImage_url())
                        .placeholder(R.drawable.user_images)
                        .fit()
                        .centerCrop()
                        .into(binding.editProfileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateregistration(String profileKey) {
        final ProgressDialog Dialog = new ProgressDialog(Activity_Edit_profile.this);
        Dialog.setMessage("Please wait ...");
        Dialog.show();
        databaseReference = FirebaseDatabase.getInstance().getReference("User_Registration").child(profileKey);
        Log.d(TAG, "Update processing..");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Dialog.dismiss();
                Toast.makeText(Activity_Edit_profile.this, "Successfully updated", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "done");
                Map<String, Object> hasMap = new HashMap<>();
                hasMap.put("blood_grp", blood_grp);
                hasMap.put("medical_condition", medical_condition);
                hasMap.put("name", name);

                databaseReference.updateChildren(hasMap);
                onBackPressed();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Dialog.dismiss();
                Toast.makeText(Activity_Edit_profile.this, "Something went error", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error.");

            }
        });

    }
}
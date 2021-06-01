package com.example.emadic.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.emadic.R;
import com.example.emadic.databinding.ActivityProfileBinding;
import com.example.emadic.databinding.ActivityRegisteredLoginBinding;
import com.example.emadic.modelclass.UserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ActivityProfile extends AppCompatActivity {
    private static final String TAG = "ActivityProfile";
    private ActivityProfileBinding binding;
    private String u_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (!extras.getString("user_id").equals("null")) {
                u_id = extras.getString("user_id");
                updateprofile(u_id);
                Log.d("dddddd", "Ddddd" + u_id);
            }
        }

        binding.profileEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityProfile.this, Activity_Edit_profile.class);
                intent.putExtra("edit_profile_id", u_id);
                startActivity(intent);
                finish();
            }
        });
    }

    private void updateprofile(String u_id) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User_Registration").child(u_id);
        databaseReference.keepSynced(true);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserInfo users = dataSnapshot.getValue(UserInfo.class);
                binding.profileTextName.setText(users.getName());
                binding.profileBloodGroupText.setText(users.getBlood_grp());
                binding.profileContactText.setText(users.getContact_number());
                binding.profileMedicalConditionText.setText(users.getMedical_condition());
                Picasso.get().load(users.getImage_url())
                        .placeholder(R.drawable.user_images)
                        .fit()
                        .centerCrop()
                        .into(binding.profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
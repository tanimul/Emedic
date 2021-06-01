package com.example.emadic.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.example.emadic.R;
import com.example.emadic.adapter.AdapterAmbulanceList;
import com.example.emadic.adapter.AdapterHospitalList;
import com.example.emadic.databinding.ActivityCallAmbulanceBinding;
import com.example.emadic.databinding.ActivityHomePageBinding;
import com.example.emadic.modelclass.Ambulance_info;
import com.example.emadic.modelclass.Hospital_info;
import com.example.emadic.modelclass.ModelAmbulance;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ActivityCallAmbulance extends AppCompatActivity {

    private static final String TAG = "ActivityAmbulancePage";
    private ActivityCallAmbulanceBinding binding;
    private ArrayList<Ambulance_info> ambulance_info = new ArrayList<Ambulance_info>();
    public static AdapterAmbulanceList adapterAmbulanceList;
    int number = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallAmbulanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.callAmbulanceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Request patient by Call
                Log.d(TAG, "ambulance number:");
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "" + number));
                startActivity(intent);
            }

        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.ambulanceSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchambulance(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        RecyclerView recyclerView = findViewById(R.id.recyc_ambulance_list);
        recyclerView.setFitsSystemWindows(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapterAmbulanceList = new AdapterAmbulanceList(ambulance_info, ActivityCallAmbulance.this);
        recyclerView.setAdapter(adapterAmbulanceList);

        readambulanece();
    }

    private void searchambulance(String toLowerCase) {
        Log.d(TAG, "case: " + toLowerCase.toLowerCase());
        Query query = FirebaseDatabase.getInstance().getReference("Ambulance_List")
                .orderByChild("area")
                .startAt(toLowerCase.toLowerCase())
                .endAt(toLowerCase.toLowerCase() + "\uf8ff");
        Log.d(TAG, "" + query.toString().toLowerCase());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ambulance_info.clear();
                Log.d(TAG, "Total hospital: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String key = dataSnapshot1.getKey();
                    Ambulance_info ambulance_infos = dataSnapshot1.getValue(Ambulance_info.class);
                    ambulance_info.add(ambulance_infos);
                    Log.d(TAG, "Key:" + key + ">>hospital name:" + ambulance_infos.getName());

                }
                adapterAmbulanceList.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readambulanece() {
        Log.d(TAG, "read");
        final ProgressDialog Dialog = new ProgressDialog(ActivityCallAmbulance.this);
        Dialog.setMessage("Please wait ...");
        Dialog.show();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Ambulance_List");
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ambulance_info.clear();
                Log.d(TAG, "Total hospital: " + dataSnapshot.getChildrenCount());
                Dialog.dismiss();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String key = dataSnapshot1.getKey();
                    Ambulance_info ambulance_infos = dataSnapshot1.getValue(Ambulance_info.class);
                    ambulance_info.add(ambulance_infos);
                    Log.d(TAG, "Key:" + key + ">>hospital name:" + ambulance_infos.getName());

                }
                adapterAmbulanceList.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "something is wrong:");
            }
        });
    }
}
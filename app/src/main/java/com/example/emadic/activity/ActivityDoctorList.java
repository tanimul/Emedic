
package com.example.emadic.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.emadic.R;
import com.example.emadic.adapter.DoctorListAdapter;
import com.example.emadic.databinding.ActivityDoctorListBinding;
import com.example.emadic.modelclass.Doctor_info;
import com.example.emadic.modelclass.ModelDoctor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ActivityDoctorList extends AppCompatActivity {

    private static final String TAG = "Act_doctor_list";
    private ActivityDoctorListBinding binding;


    private ArrayList<Doctor_info> doctorlist = new ArrayList<Doctor_info>();
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    DoctorListAdapter doctorListAdapter;
    private String hospital_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            hospital_id = intent.getString("hospital_id");
            Log.d(TAG, "Hospital id: " + hospital_id);
            readDoctor(hospital_id);
        }

        binding.recycDoctorlist.setFitsSystemWindows(true);
        binding.recycDoctorlist.setLayoutManager(new LinearLayoutManager(this));
        binding.recycDoctorlist.setHasFixedSize(true);
        doctorListAdapter = new DoctorListAdapter(doctorlist, ActivityDoctorList.this);
        binding.recycDoctorlist.setAdapter(doctorListAdapter);

        binding.myappointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityDoctorList.this, ActivityMyAppointment.class));
            }
        });


    }

    private void readDoctor(String hospital_id) {
        Log.d(TAG, "read" + hospital_id);
        final ProgressDialog Dialog = new ProgressDialog(ActivityDoctorList.this);
        Dialog.setMessage("Please wait ...");
        Dialog.show();
        databaseReference = FirebaseDatabase.getInstance().getReference("DoctorList").child(hospital_id);
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                doctorlist.clear();
                Log.d(TAG, "Total childern: " + dataSnapshot.getChildrenCount());
                Dialog.dismiss();
                if (dataSnapshot.getChildrenCount() == 0) {
                    binding.textDoctorlistEmpty.setVisibility(View.VISIBLE);
                } else {
                    binding.textDoctorlistEmpty.setVisibility(View.GONE);
                }
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String key = dataSnapshot1.getKey();
                    Doctor_info doctor_info = dataSnapshot1.getValue(Doctor_info.class);
                    doctorlist.add(doctor_info);
                    Log.d(TAG, "Key:" + key + "Doctor name:" + doctor_info.getDoctor_name());

                }
                doctorListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "something is wrong:");
            }
        });
    }
}
package com.example.emadic.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.emadic.R;
import com.example.emadic.adapter.AdapterAppointmentList;
import com.example.emadic.modelclass.Appointment_info;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ActivityMyAppointment extends AppCompatActivity {
    private static final String TAG = "Act_my_appointment";
    private com.example.emadic.databinding.ActivityMyAppointmentBinding binding;

    private ArrayList<Appointment_info> my_appointment_list = new ArrayList<Appointment_info>();
    private DatabaseReference databaseReference;
    AdapterAppointmentList appointmentListAdapter;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.example.emadic.databinding.ActivityMyAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("My Appointment");
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recy_my_appoint);
        recyclerView.setFitsSystemWindows(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        appointmentListAdapter = new AdapterAppointmentList(my_appointment_list, ActivityMyAppointment.this);
        recyclerView.setAdapter(appointmentListAdapter);

        if (!user.getUid().isEmpty()) {
            Log.d(TAG, "Not Empty");
            //  readAppointment(user.getUid());
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = (cal.get(Calendar.MONTH) + 1);
            int date = cal.get(Calendar.DAY_OF_MONTH);
            filterappointmentupcoming(year, month, date);
        }

        binding.appointmentUpcoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = (cal.get(Calendar.MONTH) + 1);
                int date = cal.get(Calendar.DAY_OF_MONTH);
                filterappointmentupcoming(year, month, date);
                binding.appointmentUpcoming.setBackgroundResource(R.drawable.bt_background_4);
                binding.appointmentPrevious.setBackgroundResource(R.drawable.bt_background_3);
                binding.appointmentPrevious.setTextColor(Color.parseColor("#FF000000"));
            }
        });

        binding.appointmentPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = (cal.get(Calendar.MONTH) + 1);
                int date = cal.get(Calendar.DAY_OF_MONTH);
                filterappointmentprevious(year, month, date);
                binding.appointmentPrevious.setBackgroundResource(R.drawable.bt_background_4);
                binding.appointmentUpcoming.setBackgroundResource(R.drawable.bt_background_3);
                binding.appointmentUpcoming.setTextColor(Color.parseColor("#FF000000"));
            }
        });

    }

    private void filterappointmentupcoming(int year, int month, int date) {
        String currentdate = year + "-" + month + "-" + date;
        Log.d(TAG, "read filtering" + currentdate);
        final ProgressDialog Dialog = new ProgressDialog(ActivityMyAppointment.this);
        Dialog.setMessage("Please wait ...");
        Dialog.show();
        databaseReference = FirebaseDatabase.getInstance().getReference("Appointment");
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.d(TAG, "Total childern: " + dataSnapshot.getChildrenCount());
                Dialog.dismiss();
                my_appointment_list.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    //Log.d(TAG, ">" + dataSnapshot1.getKey());
                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                        //Log.d(TAG, ">>" + dataSnapshot2.getKey());
                        for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {

                            for (DataSnapshot dataSnapshot4 : dataSnapshot3.getChildren()) {
                                //Log.d(TAG, ">>>>" + dataSnapshot4.getKey());
                                //Log.d(TAG, ">>>>" + user.getUid());
                                if (user.getUid().equals(dataSnapshot4.getKey())) {
                                    binding.appointmentEmpty.setVisibility(View.GONE);
                                    Appointment_info appointment_info = dataSnapshot4.getValue(Appointment_info.class);

                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                                    try {
                                        Date strDate = sdf.parse(currentdate);
                                        Date strDate2 = sdf2.parse(appointment_info.getDate());
                                        if (!strDate.after(strDate2)) {
                                            my_appointment_list.add(appointment_info);
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }
                    }

                }
                appointmentListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "something is wrong:");
            }
        });
    }

    private void filterappointmentprevious(int year, int month, int date) {
        String currentdate = year + "-" + month + "-" + date;
        Log.d(TAG, "read filtering" + currentdate);
        final ProgressDialog Dialog = new ProgressDialog(ActivityMyAppointment.this);
        Dialog.setMessage("Please wait ...");
        Dialog.show();
        databaseReference = FirebaseDatabase.getInstance().getReference("Appointment");
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Total childern: " + dataSnapshot.getChildrenCount());
                Dialog.dismiss();
                my_appointment_list.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Log.d(TAG, ">" + dataSnapshot1.getKey());
                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                        Log.d(TAG, ">>" + dataSnapshot2.getKey());
                        for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {
                            for (DataSnapshot dataSnapshot4 : dataSnapshot3.getChildren()) {
                                Log.d(TAG, ">>>>" + dataSnapshot4.getKey());
                                Log.d(TAG, ">>>>" + user.getUid());
                                if (user.getUid().equals(dataSnapshot4.getKey())) {
                                    Appointment_info appointment_info = dataSnapshot4.getValue(Appointment_info.class);

                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                                    try {
                                        Date strDate = sdf.parse(currentdate);
                                        Date strDate2 = sdf2.parse(appointment_info.getDate());
                                        if (strDate.after(strDate2)) {
                                            my_appointment_list.add(appointment_info);
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }

                        }
                    }

                }
                appointmentListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "something is wrong:");
            }
        });
    }
}
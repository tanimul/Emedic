package com.example.emadic.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.emadic.KEYS;
import com.example.emadic.R;
import com.example.emadic.Tools;
import com.example.emadic.databinding.ActivityBookAppoinmentBinding;
import com.example.emadic.modelclass.Appointment_info;
import com.example.emadic.modelclass.Doctor_info;
import com.github.badoualy.datepicker.DatePickerTimeline;
import com.github.badoualy.datepicker.MonthView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class ActivityBookAppoinment extends AppCompatActivity {

    private static final String TAG = "Act_Book_appointment";
    private ActivityBookAppoinmentBinding binding;
    private String doctor_key, userid, doctorname, hospital_id, hospital_name;
    private DatabaseReference databaseReference;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private BottomSheetBehavior mBottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookAppoinmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mBottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet2);

        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            doctor_key = intent.getString("doctor_id");
            hospital_id = intent.getString("hospital_id");
            //Log.d(TAG, hospital_id + " :::Hospital id >>><<<< Doctor id: " + doctor_key);
        }

        if (user != null) {
            //Log.d(TAG, "Firebase user: " + user.getUid());
            userid = user.getUid();
        } else {
            //Log.d(TAG, "Not Firebase user");
        }

        getdoctor_information(doctor_key);


        binding.bookAppoinment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "Appiontment_booking Processing");
                if (!binding.bookAppoitmnetTellsometing.getText().toString().isEmpty()) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    Toast.makeText(ActivityBookAppoinment.this, "Please Describe your problem", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.bookMakePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.bookAppoitmnetAccountNumber.length()==11){
                    Toast.makeText(ActivityBookAppoinment.this, "Payment Successfully", Toast.LENGTH_SHORT).show();
                    int month = binding.datePickerTimeline.getSelectedMonth() + 1;
                    String date = binding.datePickerTimeline.getSelectedYear() + "-" + month
                            + "-" + binding.datePickerTimeline.getSelectedDay();
                    //Log.d(TAG, "" + date);
                    bookappointment(date);
                }else {
                    Toast.makeText(ActivityBookAppoinment.this, "Enter valid number", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void bookappointment(String date) {
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Appointment").child(hospital_id);
        String patientName=Tools.getPref(KEYS.USER_NAME,"");
        String patietContactInfo=Tools.getPref(KEYS.USER_NUMBER,"");

        if(!patientName.equals("") && !patietContactInfo.equals("")){
            Appointment_info appointment_info = new Appointment_info(date, doctor_key, binding.bookAppoitmnetTellsometing.getText().toString(),
                    userid, doctorname, hospital_id, hospital_name, patientName,patietContactInfo);
            databaseReference2.child(date).child(doctor_key).child(userid).setValue(appointment_info).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ActivityBookAppoinment.this, "Appointment Added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else Toast.makeText(ActivityBookAppoinment.this, "Something went worng!!", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ActivityBookAppoinment.this, "Something went worng!!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onFailure: error:"+e.getMessage());
                }
            });
        }


    }

    private void getdoctor_information(String doctorkey) {
        databaseReference = FirebaseDatabase.getInstance().getReference("DoctorList");
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                        Doctor_info doctor_info = dataSnapshot2.getValue(Doctor_info.class);
                        Log.d(TAG, "Doctor name:" + doctor_info.getDoctor_name()+" hospital name:"+doctor_info.getHospital_name());
                        if (doctor_info.getDoctor_key().equals(doctorkey)) {
                            binding.bookAppointmnetDoctorname.setText("Hospital name: " + doctor_info.getDoctor_name());
                            binding.bookAppoinmentDctorDetails.setText("Doctor details: " + doctor_info.getQualification());
                            binding.bookAppointmentDoctorVisiting.setText("visiting charge: " + doctor_info.getVisiting_charge() + " Tk");
                            doctorname = doctor_info.getDoctor_name();
                            hospital_name = doctor_info.getHospital_name();
                        }
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "something is wrong:");
            }
        });
    }
}

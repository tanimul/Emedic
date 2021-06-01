package com.example.emadic.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.example.emadic.databinding.ActivityHospitalListBinding;

public class Activity_hospital_list extends AppCompatActivity {
    private static final String TAG = "Activity_hospital_list";
    private ActivityHospitalListBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityHospitalListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
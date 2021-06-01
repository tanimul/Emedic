package com.example.emadic.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emadic.R;
import com.example.emadic.activity.ActivityBookAppoinment;
import com.example.emadic.modelclass.Doctor_info;

import java.util.ArrayList;

public class DoctorListAdapter extends RecyclerView.Adapter<DoctorListAdapter.DoctorListholder> {
    private static final String TAG = "Doctor_List_Adapter";
    private ArrayList<Doctor_info> doctor = new ArrayList<>();
    Context context;

    public DoctorListAdapter(ArrayList<Doctor_info> doctor, Context context) {
        this.doctor = doctor;
        this.context = context;
    }

    @NonNull
    @Override
    public DoctorListAdapter.DoctorListholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_doctorlist, parent, false);
        return new DoctorListAdapter.DoctorListholder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorListholder holder, int position) {
        final Doctor_info doctor_info = doctor.get(position);

        holder.name.setText("Name: " + doctor_info.getDoctor_name());
        holder.qualification.setText("Qualification: " + doctor_info.getQualification());
        holder.visitcharge.setText("Visiting Charge: " + doctor_info.getVisiting_charge()+" TAKA");
        holder.schedule.setText("Visiting Schedule: " + doctor_info.getVisiting_schedule());

        holder.book_appoinment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Log.d(TAG, "doctor key: " + doctor_info.getDoctor_key());
                Intent intent = new Intent(context, ActivityBookAppoinment.class);
                intent.putExtra("doctor_id", doctor_info.getDoctor_key());
                intent.putExtra("hospital_id", doctor_info.getHospital_id());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctor.size();
    }

    public class DoctorListholder extends RecyclerView.ViewHolder {

        TextView name, qualification, visitcharge, schedule;
        Button book_appoinment;


        public DoctorListholder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.doctor_list_doctorName);
            qualification = itemView.findViewById(R.id.doctor_list_qualification);
            visitcharge = itemView.findViewById(R.id.doctor_list_visiting_charge);
            schedule = itemView.findViewById(R.id.doctor_list_visiting_schedule);
            book_appoinment = itemView.findViewById(R.id.doctor_list_button);
        }
    }
}

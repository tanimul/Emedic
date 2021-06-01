package com.example.emadic.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emadic.R;
import com.example.emadic.modelclass.Appointment_info;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import static android.view.View.GONE;

public class AdapterAppointmentList extends RecyclerView.Adapter<AdapterAppointmentList.ViewHolderAAAL> {
    private static final String TAG = "My_appoint_List_Adapter";
    private ArrayList<Appointment_info> appointment_info = new ArrayList<>();
    Context context;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public AdapterAppointmentList(ArrayList<Appointment_info> appointment_info, Context context) {
        this.appointment_info = appointment_info;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderAAAL onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.layout_my_appointment, parent, false);
        ViewHolderAAAL viewHolderAAAL = new ViewHolderAAAL(view);
        return viewHolderAAAL;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderAAAL holder, int position) {

        Log.d(TAG, "Bind.... "+appointment_info.get(position).getHospital_name());
        Log.d(TAG, "" + appointment_info.get(position).getUser_id());
        Log.d(TAG, "" + user.getUid());
        //  if (appointment_info.get(position).getUser_id().equals(user.getUid()) ) {
        holder.name.setText("Doctor Name: " + appointment_info.get(position).getDoctor_name());
        holder.appointmentDate.setText("Appointment Date: " + appointment_info.get(position).getDate());
        holder.hospitalname.setText("Hospital Name: " + appointment_info.get(position).getHospital_name());
//        }

    }

    @Override
    public int getItemCount() {
        return appointment_info.size();
    }

    class ViewHolderAAAL extends RecyclerView.ViewHolder {

        TextView name, appointmentDate, hospitalname;


        public ViewHolderAAAL(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.myAppointment_doctorName);
            appointmentDate = itemView.findViewById(R.id.myAppoinment_date);
            hospitalname = itemView.findViewById(R.id.myAppointment_hosp_name);


        }
    }
}

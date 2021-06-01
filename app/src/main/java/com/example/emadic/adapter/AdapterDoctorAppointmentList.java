package com.example.emadic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emadic.R;
import com.example.emadic.modelclass.ModelDoctorAppoinment;

import java.util.List;

public class AdapterDoctorAppointmentList extends  RecyclerView.Adapter<AdapterDoctorAppointmentList.ViewHolderADA>{

    List<ModelDoctorAppoinment>list;
    Context context;

    public AdapterDoctorAppointmentList(List<ModelDoctorAppoinment> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderADA onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_doctor_appointment,parent,false);
        ViewHolderADA viewHolderADAObject = new ViewHolderADA(view);
        return viewHolderADAObject;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderADA holder, int position) {

        holder.name.setText(list.get(position).getName());
        /*holder.hospital_doctorAppointment_cllimage.setImageResource(list.get(position).getImage());*/
        holder.problem.setText(list.get(position).getProblemText());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class  ViewHolderADA extends RecyclerView.ViewHolder {

        /*ImageView hospital_doctorAppointment_cllimage;*/
        TextView name, problem;
        Button reschedule, delete;


        public ViewHolderADA(@NonNull View itemView) {
            super(itemView);

           /* hospital_doctorAppointment_cllimage = itemView.findViewById(R.id.hospital_doctorAppointment_callImages);*/
            name = itemView.findViewById(R.id.hospital_doctorAppointment_doctorName);
            problem = itemView.findViewById(R.id.hospital_doctorAppointment_prbolemText);
            reschedule = itemView.findViewById(R.id.hospital_doctorAppointment_rescheduleBtn);
            delete = itemView.findViewById(R.id.hospital_doctorAppointment_deleteBtn);

        }
    }
}

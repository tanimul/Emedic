package com.example.emadic.adapter.dummy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emadic.R;
import com.example.emadic.modelclass.ModelHospHospital;

import java.util.List;

public class AdapterHospHospitalList extends  RecyclerView.Adapter<AdapterHospHospitalList.ViewHolderAHHL> {

    List<ModelHospHospital> list;
    Context context;

    public AdapterHospHospitalList(List<ModelHospHospital> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderAHHL onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_hosp_hospital_list,parent,false);
        ViewHolderAHHL viewHolderAHHLObject = new ViewHolderAHHL(view);
        return viewHolderAHHLObject;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderAHHL holder, int position) {

        holder.name.setText(list.get(position).getHospital_name());
        holder.hospHospital.setImageResource(list.get(position).getImage());
        holder.openStatus.setText(list.get(position).getOpenStatus());
        holder.contact.setText(list.get(position).getContactNumber());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class  ViewHolderAHHL extends RecyclerView.ViewHolder {

        ImageView hospHospital;
        TextView name , contact, openStatus;

        public ViewHolderAHHL(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.hosp_hospitalName);
            contact = itemView.findViewById(R.id.hosp_hospital_contractNumber);
            openStatus = itemView.findViewById(R.id.hosp_hospital_openStatus);
            hospHospital = itemView.findViewById(R.id.hosp_hospital_imageView);
        }
    }
}

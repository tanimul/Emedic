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
import com.example.emadic.modelclass.ModelDerictionHospital;

import java.util.List;

public class AdapterMapHospitalList extends  RecyclerView.Adapter<AdapterMapHospitalList.ViewHolderAMPL> {

    List<ModelDerictionHospital> list;
    Context context;

    public AdapterMapHospitalList(List<ModelDerictionHospital> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderAMPL onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_deriction_hospital_list,parent,false);
        ViewHolderAMPL viewHolderAMPLObject = new ViewHolderAMPL(view);
        return viewHolderAMPLObject;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderAMPL holder, int position) {

        holder.name.setText(list.get(position).getName());
        holder.distance.setText(list.get(position).getDistance());
        holder.timeDistance.setText(list.get(position).getTimeDistance());
        holder.openinghour.setText(list.get(position).getOpeningHour());
        holder.deriction_hospitalimage.setImageResource(list.get(position).getImage());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class  ViewHolderAMPL extends RecyclerView.ViewHolder {

        ImageView deriction_hospitalimage;
        TextView name, distance, timeDistance, openinghour;


        public ViewHolderAMPL(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.deriction_hospitalName);
            distance = itemView.findViewById(R.id.deriction_hospital_distance);
            timeDistance = itemView.findViewById(R.id.deriction_hospital_timeDistance);
            openinghour = itemView.findViewById(R.id.deriction_hospital_openingHour);
            deriction_hospitalimage = itemView.findViewById(R.id.deriction_hospitalImage);
        }
    }

}

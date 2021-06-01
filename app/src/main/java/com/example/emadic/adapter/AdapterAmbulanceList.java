package com.example.emadic.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emadic.R;
import com.example.emadic.modelclass.Ambulance_info;

import java.util.ArrayList;

public class AdapterAmbulanceList extends RecyclerView.Adapter<AdapterAmbulanceList.ViewHolderAAL> {
    private static final String TAG = "Ambulance_List_Adapter";
    String number;
    private ArrayList<Ambulance_info> ambulance_info = new ArrayList<>();
    Context context;

    public AdapterAmbulanceList(ArrayList<Ambulance_info> ambulance_info, Context context) {
        this.ambulance_info = ambulance_info;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderAAL onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_ambulance_list, parent, false);
        ViewHolderAAL viewHolderAALObject = new ViewHolderAAL(view);
        return viewHolderAALObject;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderAAL holder, int position) {
        final Ambulance_info ambulance_infos = ambulance_info.get(position);
        holder.name.setText("Driver's Name: " + ambulance_info.get(position).getName());
        holder.ambulance_contact_number.setText("Contract Number: " + ambulance_info.get(position).getAmbulance_contact_number());
        holder.layout_ambulancelist_area.setText("Area: " + ambulance_info.get(position).getArea());

        holder.ambulanceList_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number = ambulance_infos.getAmbulance_contact_number();
                Log.d(TAG, "Bind. " + number);
                Context context = v.getContext();
                requestambulance(context, number);
            }
        });
    }

    //Request patient by Call
    private void requestambulance(Context context, String number) {
        Log.d(TAG, "ambulance number:" + number);
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "" + number));
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return ambulance_info.size();
    }

    class ViewHolderAAL extends RecyclerView.ViewHolder {

        ImageView ambulanceList_button;
        TextView name, ambulance_contact_number, layout_ambulancelist_area;

        public ViewHolderAAL(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.layout_ambulancelist_text_name);
            ambulance_contact_number = itemView.findViewById(R.id.layout_ambulancelist_contact_number);
            layout_ambulancelist_area = itemView.findViewById(R.id.layout_ambulancelist_area);
            ambulanceList_button = itemView.findViewById(R.id.layout_ambulancelist_button);


        }
    }

}

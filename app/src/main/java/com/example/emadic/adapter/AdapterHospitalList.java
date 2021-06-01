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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emadic.R;
import com.example.emadic.activity.ActivityDoctorList;
import com.example.emadic.interfaces.HospitalListClick;
import com.example.emadic.modelclass.ArroundHosAddress_info;
import com.example.emadic.modelclass.Hospital_info;

import java.util.ArrayList;
import java.util.List;

//confirm
public class AdapterHospitalList extends RecyclerView.Adapter<AdapterHospitalList.ViewHolderAHL> {

    private static final String TAG = "AdapterHospitalList";
    private List<ArroundHosAddress_info> hospital;
    private List<Hospital_info> hospitalsToCompareWith;
    Context context;
    HospitalListClick callback;


    public AdapterHospitalList(ArrayList<ArroundHosAddress_info> hospital, List<Hospital_info> hospitalsToCompareWith, Context context, HospitalListClick callBack) {
        this.hospital = hospital;
        this.hospitalsToCompareWith = hospitalsToCompareWith;
        this.context = context;
        this.callback = callBack;
    }

    @NonNull
    @Override
    public ViewHolderAHL onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.layout_hospital_list, parent, false);

        ViewHolderAHL viewHolderAHLObject = new ViewHolderAHL(view);
        return viewHolderAHLObject;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderAHL holder, int position) {
        final ArroundHosAddress_info arroundHosAddress_info = hospital.get(position);
        //  final Hospital_info hospital_info = hospitalsToCompareWith.get(position);

        holder.name.setText(hospital.get(position).getAddress());
        holder.time.setText("time: " + hospital.get(position).getTime());
        holder.distance.setText("distance: " + hospital.get(position).getDistance());

        for (Hospital_info comareWith : hospitalsToCompareWith) {
            Log.d(TAG, "onDataChange: compare  lat=" + comareWith.getLatitude() + " lang=" + comareWith.getLongitude());
            Log.d(TAG, "onDataChange: with = lat" + hospital.get(position).getLatitude() + " lang=" + hospital.get(position).getLongitude());
            if (Double.compare(comareWith.getLatitude(), hospital.get(position).getLatitude()) == 0 &&
                    Double.compare(comareWith.getLongitude(), hospital.get(position).getLongitude()) == 0) {
                Log.d(TAG, "Registered Hospital>>>" + hospital.get(position).getAddress());
                holder.appointbtn.setVisibility(View.VISIBLE);
                holder.start_background.setVisibility(View.VISIBLE);
                holder.homepage_list_call_buttn.setVisibility(View.VISIBLE);

                holder.start_background.setBackgroundColor(context.getResources().getColor(R.color.green, null));
                break;
            } else {
                Log.d(TAG, "UnRegistered Hospital>>" + hospital.get(position).getAddress());
                holder.appointbtn.setVisibility(View.GONE);
                holder.homepage_list_call_buttn.setVisibility(View.INVISIBLE);
                holder.start_background.setVisibility(View.VISIBLE);
                holder.start_background.setBackgroundColor(context.getResources().getColor(R.color.red, null));
            }
        }


        holder.appointbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, ActivityDoctorList.class);
                for (Hospital_info comareWith : hospitalsToCompareWith) {
                    if (Double.compare(comareWith.getLatitude(), hospital.get(position).getLatitude()) == 0 &&
                            Double.compare(comareWith.getLongitude(), hospital.get(position).getLongitude()) == 0) {
                        intent.putExtra("hospital_id", "" + comareWith.getHospital_unique_id());
                    }

                }

                context.startActivity(intent);
            }
        });

        holder.homepage_list_call_buttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Hospital_info comareWith : hospitalsToCompareWith) {
                    if (Double.compare(comareWith.getLatitude(), hospital.get(position).getLatitude()) == 0 &&
                            Double.compare(comareWith.getLongitude(), hospital.get(position).getLongitude()) == 0) {
                        Log.d(TAG, "Contact number: " + comareWith.getContact_number());
                        callNumber(context, comareWith.getContact_number());
                        break;
                    }
                }
            }

        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Hospital_info comareWith : hospitalsToCompareWith) {
                    if (Double.compare(comareWith.getLatitude(), hospital.get(position).getLatitude()) == 0 &&
                            Double.compare(comareWith.getLongitude(), hospital.get(position).getLongitude()) == 0) {
                        Log.d(TAG, "Registerd Hospital: ");
                        callback.arroundHosAddress_info(arroundHosAddress_info, comareWith.getHospital_unique_id());
                        break;
                    }
                    if (Double.compare(comareWith.getLatitude(), hospital.get(position).getLatitude()) == 1 &&
                            Double.compare(comareWith.getLongitude(), hospital.get(position).getLongitude()) == 1
                    ) {
                        Log.d(TAG, "UnRegisterd Hospital: ");
                        callback.arroundHosAddress_info(arroundHosAddress_info, "null");
                        break;
                    }
                }


            }
        });

    }

    private void callNumber(Context context, String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "" + number));
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return hospital.size();
    }

    class ViewHolderAHL extends RecyclerView.ViewHolder {

        ImageView homepage_list_call_buttn, appointbtn, start_background;
        TextView name, distance, time;

        public ViewHolderAHL(@NonNull View itemView) {
            super(itemView);

            homepage_list_call_buttn = itemView.findViewById(R.id.homepagelist_call_button);
            appointbtn = itemView.findViewById(R.id.homepage_appoint_buttn);
            name = itemView.findViewById(R.id.hompagelist_hospital_name);
            distance = itemView.findViewById(R.id.homepagelist_distance_text);
            time = itemView.findViewById(R.id.homepagelist_time_text);
            start_background = itemView.findViewById(R.id.hospital_images);
        }
    }
}

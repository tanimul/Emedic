package com.example.emadic.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.emadic.R;
import com.example.emadic.ViewmodelDirectionApi;
import com.example.emadic.databinding.ActivityMapWithDerictionBinding;
import com.example.emadic.model_direction.DirectionResponse;
import com.example.emadic.model_direction.OverviewPolyline;
import com.example.emadic.retrofit.ApiClient;
import com.example.emadic.retrofit.ApiInterface;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.PolyUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityMapWithDeriction extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "Activity_Map_with_dir";
    private ActivityMapWithDerictionBinding binding;
    private String hospitalName, hospitalDistance, hospitalTime;
    private Double hospitalLatitude, hospitalLongitude;
    // private ViewmodelDirectionApi viewmodelDirectionApi;

    private Marker startmarker, destinationmarker;
    private List<LatLng> polylineLatLngList;
    private Polyline polyline;

    GoogleMap map;
    int PROXIMITY_RADIUS = 10000;
    private static final int REQUEST_CODE = 101;
    private static final float DEFAULT_ZOOM = 17f;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationManager locationManager;
    private LocationCallback mLocationCallback;
    private Location mLastLocation;
    Polyline cur_polyline;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapWithDerictionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //  viewmodelDirectionApi = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(ViewmodelDirectionApi.class);


        final Bundle hospitalinfo = getIntent().getExtras();
        if (hospitalinfo != null) {
            Log.d(TAG, " Data.");
            hospitalName = hospitalinfo.getString("hospital_name");
            hospitalDistance = hospitalinfo.getString("hospital_distance");
            hospitalTime = hospitalinfo.getString("hospital_time");
            hospitalLatitude = hospitalinfo.getDouble("hospital_lat");
            hospitalLongitude = hospitalinfo.getDouble("hospital_lng");

            if (!hospitalinfo.getString("hospital_id").equals("null")) {
                binding.derictionHospitalMakeAnAppointment.setVisibility(View.VISIBLE);
            } else {
                binding.derictionHospitalMakeAnAppointment.setVisibility(View.INVISIBLE);
            }

            binding.derictionHospitalName.setText(binding.derictionHospitalName.getText().toString() + "" + hospitalName);
            binding.derictionHospitalDistance.setText(binding.derictionHospitalDistance.getText().toString() + "" + hospitalDistance);
            binding.derictionHospitalTimeDistance.setText(binding.derictionHospitalTimeDistance.getText().toString() + "" + hospitalTime);

        } else {
            Log.d(TAG, "No Data.");
        }

        binding.iconBackFromMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.derictionHospitalMakeAnAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityMapWithDeriction.this, ActivityDoctorList.class);
                if (!hospitalinfo.getString("hospital_id").equals("null")) {
                    intent.putExtra("hospital_id", "" + hospitalinfo.getString("hospital_id"));
                    startActivity(intent);
                }
            }
        });


        init();
        fetchlastlocation();
    }

    private void getDirectionResponse(Marker startmarker, Marker destinationmarker) {

        Log.d(TAG, "getDirectionResponse: called");


        Map<String, String> mapQuery = new HashMap<>();
        mapQuery.put("key", "AIzaSyCdP8QSuapjIn5DZEfWXG5EH6EIiYb6uuY");
        mapQuery.put("origin", startmarker.getPosition().latitude + "," + startmarker.getPosition().longitude);
        mapQuery.put("destination", destinationmarker.getPosition().latitude + "," + destinationmarker.getPosition().longitude);

        ApiInterface client = ApiClient.getApiInterface();

        Call<DirectionResponse> call = client.getDirectionResponses(mapQuery);
        call.enqueue(new Callback<DirectionResponse>() {
            @Override
            public void onResponse(Call<DirectionResponse> call, Response<DirectionResponse> response) {

                if (response != null) {
                    Log.d("ddddd", "onChanged: direction response status: " + response.body().getStatus());

                    if (response.body().getStatus().equals("OK")) {
                        OverviewPolyline overviewPolyline = response.body().getRoutes().get(0).getOverviewPolyline();
                        drawPolyLine(overviewPolyline);
                    }
                } else {
                    Log.d("ddd", "null response");
                }


            }

            @Override
            public void onFailure(Call<DirectionResponse> call, Throwable t) {
                Log.d("ddd", "failure");
            }
        });


//        if (origin != null && destination != null) {
//            Log.d("ddddd", "getDirectionResponse: inside if");
//            try {
//                Map<String, String> mapQuery = new HashMap<>();
//                mapQuery.put("key", "AIzaSyCdP8QSuapjIn5DZEfWXG5EH6EIiYb6uuY");
//                mapQuery.put("origin", lat1 + "," + long1);
//                mapQuery.put("destination",lat2 + "," + long2);
//                //mapQuery.put("waypoints","place_id:EjzgprjgpoLgprjgpqYg4Kat4Kas4KaoIOCmj-CmreCmv-CmqOCmv-CmiSwgRGhha2EsIEJhbmdsYWRlc2giLiosChQKEgl5t4sRqLhVNxFuzi8hz2Bs_RIUChIJgWsCh7C4VTcRwgRZ3btjpY8|place_id:EiBNYW5payBNaWEgQXZlLCBEaGFrYSwgQmFuZ2xhZGVzaCIuKiwKFAoSCQ0daOiruFU3EX3sX5-U4v_rEhQKEgmBawKHsLhVNxHCBFndu2Oljw");
//
//                viewmodelDirectionApi.getDirectionResponse(mapQuery).observe(this, new Observer<DirectionResponse>() {
//                    @Override
//                    public void onChanged(DirectionResponse directionResponse) {
//                        Log.d("ddddd", "onChanged: direction response status: " + directionResponse.getStatus());
//                        if (directionResponse.getStatus().equals("OK")) {
//                            OverviewPolyline overviewPolyline = directionResponse.getRoutes().get(0).getOverviewPolyline();
//                            drawPolyLine(overviewPolyline);
//                        }
//                    }
//                });
//            } catch (Exception E) {
//                Log.d("ddddd", "getDirectionResponse: error" + E.getMessage());
//            }
//        } else {
//            Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
//            Log.d("ddddd", "getDirectionResponse: something went wrong");
//        }

    }

    private void drawPolyLine(OverviewPolyline overviewPolyline) {

        if (overviewPolyline != null) {
            Log.d(TAG, "drawPolyLine: overviewPolyline..notNull");
            polylineLatLngList = PolyUtil.decode(overviewPolyline.getPoints());
            for (int k = 0; k < polylineLatLngList.size() - 1; k++) {
                LatLng origin = polylineLatLngList.get(k);
                LatLng destination = polylineLatLngList.get(k + 1);
                Log.d(TAG, origin + ">>><<<" + destination);
                map.addPolyline(new PolylineOptions().color(R.color.pilyline).geodesic(true).add(
                        new LatLng(origin.latitude, origin.longitude),
                        new LatLng(destination.latitude, destination.longitude)).width(12));
            }
            fitMapForAllMArkers(startmarker,destinationmarker);

        } else {
            Log.d(TAG, "drawPolyLine: overviewPolyline=null");
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(TAG, "onMapReady: called");
        map = googleMap;
        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        LatLng latLng2 = new LatLng(hospitalLatitude, hospitalLongitude);
        moveCamera(latLng, latLng2);

    }

    void init() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    private void fetchlastlocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mLastLocation = location;
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.direction_map);
                    mapFragment.getMapAsync(ActivityMapWithDeriction.this);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchlastlocation();
                }
                break;
        }
    }

    public void moveCamera(LatLng latLng, LatLng latLng2) {

        startmarker = map.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(getBitmapDescriptor(getResources().getDrawable(R.drawable.ic_current, null)))
                .title("Current Location"));
        destinationmarker = map.addMarker(new MarkerOptions()
                .position(latLng2)
                .icon(getBitmapDescriptor(getResources().getDrawable(R.drawable.ic_destination, null)))
                .title("" + hospitalName));

        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

        getDirectionResponse(startmarker, destinationmarker);
    }
    private BitmapDescriptor getBitmapDescriptor(Drawable vectorDrawable) {
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bm = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bm);
    }

    private void fitMapForAllMArkers(Marker s, Marker b) {
        LatLngBounds.Builder latlangBoundBuilder = new LatLngBounds.Builder();
        latlangBoundBuilder.include(s.getPosition());
        latlangBoundBuilder.include(b.getPosition());
        LatLngBounds bounds = latlangBoundBuilder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
        map.animateCamera(cu);
    }


}
package com.example.emadic.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emadic.KEYS;
import com.example.emadic.R;
import com.example.emadic.Tools;
import com.example.emadic.ViewModelEmedic;
import com.example.emadic.ViewmodelDirectionApi;
import com.example.emadic.adapter.AdapterHospitalList;
import com.example.emadic.databinding.ActivityHomePageBinding;
import com.example.emadic.interfaces.HospitalListClick;
import com.example.emadic.model_distancematrix.DistanceResponse;
import com.example.emadic.model_distancematrix.Element;
import com.example.emadic.modelclass.ArroundHosAddress_info;
import com.example.emadic.modelclass.Hospital_info;
import com.example.emadic.modelclass.UserInfo;
import com.example.emadic.retrofit.ApiClient;
import com.example.emadic.retrofit.ApiInterface;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityHomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, HospitalListClick {
    private static final String TAG = "ActivityHomePage";
    private ActivityHomePageBinding binding;
    private final float DEFAULT_ZOOM = 15f;
    private final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 101;
    private static final int LOCATION_SETTINGS_REQUEST_CODE = 22;
    private BottomSheetBehavior bottomSheetBehavior;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    Intent intent;

    private GoogleMap mMap;
    int PROXIMITY_RADIUS = 10000;
    Double latitude, longitude;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationManager locationManager;
    private LocationCallback mLocationCallback;
    private Location mLastLocation;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    //private Place place;
    //private String location_name;
    //private ViewmodelDirectionApi viewmodelDirectionApi;
    //private ArrayList<Hospital_info> hospitalList = new ArrayList<Hospital_info>();

    private ArrayList<ArroundHosAddress_info> nearByHospitals;
    List<Hospital_info> hospitalsToCompareWith;
    private AdapterHospitalList adapterHospitalList;
    private ViewModelEmedic viewModelEmedic;

    private ImageView userImage;

    private Dialog dialogRating;
    private TextView ratingCancel,ratingSubmit;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        init();
        initNavAndToolbar();
        setupRatigDialog();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(ActivityHomePage.this);
        initLocationCallBack();

        if (isPermissionGranted()) {
            if (isLocationEnabled()) {
                getLastLocation();
            } else {
                showGPSTurnOnOption();
            }
        } else {
            requestLocationPermission();
        }

        binding.icCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveCamera(mLastLocation);
            }
        });

        getImage(Tools.getPref(KEYS.USER_PIC_URL,""));

        ratingSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogRating.dismiss();
                Toast.makeText(ActivityHomePage.this, "Thank you for your feedback.", Toast.LENGTH_SHORT).show();
            }
        });

        ratingCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogRating.dismiss();
            }
        });

        nearByHospitals = new ArrayList<ArroundHosAddress_info>();
        hospitalsToCompareWith = new ArrayList<>();
        adapterHospitalList = new AdapterHospitalList(nearByHospitals, hospitalsToCompareWith, ActivityHomePage.this, ActivityHomePage.this);
        binding.bottomsheet.bottomSheetRecycleView.setAdapter(adapterHospitalList);
        binding.bottomsheet.bottomSheetRecycleView.setLayoutManager(new LinearLayoutManager(ActivityHomePage.this));
    }


    void init() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        View bottomsheet = findViewById(R.id.bottomsheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet);

        viewModelEmedic = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(ViewModelEmedic.class);
    }
    void initNavAndToolbar() {
        toolbar = findViewById(R.id.main_home_page_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.user_nav_view);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.openNavDrawer, R.string.closeNavDrawer);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getColor(R.color.black));
        navigationView.setNavigationItemSelectedListener(this);
    }
    private void setupRatigDialog(){
        dialogRating=new Dialog(this);
        dialogRating.setContentView(R.layout.layout_rating_dialog);
        ratingSubmit=dialogRating.findViewById(R.id.ratingSubmit);
        ratingCancel=dialogRating.findViewById(R.id.ratingCancel);
        ratingBar=dialogRating.findViewById(R.id.ratingBar);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: called");
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(false);
    }

    private void requestLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION_REQUEST_CODE);
    }

    private boolean isPermissionGranted() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return true;
        else return false;
    }

    private void initLocationCallBack() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mLastLocation = locationResult.getLastLocation();
                if (mLastLocation != null) {
                    Log.d(TAG, "initLocationCallBack: got location");
                    mMap.addMarker(
                            new MarkerOptions().
                                    position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                                    .title("User Location")
                                    .icon(getBitmapDescriptor(getResources().getDrawable(R.drawable.ic_user_location, null))));
                    moveCameraAndShowNearbyHospital(mLastLocation, 25f);
                    fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
                }

            }
        };
    }

    private BitmapDescriptor getBitmapDescriptor(Drawable vectorDrawable) {

        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bm = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bm);
    }

    public void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Don't have location permission", Toast.LENGTH_SHORT).show();
            return;
        } else {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(
                    task -> {
                        mLastLocation = task.getResult();
                        if (mLastLocation == null) {
                            requestNewLocationData();
                        } else {
                            Log.d(TAG, "getLastLocation: got location");
                            moveCameraAndShowNearbyHospital(mLastLocation, 25f);
                            latitude=mLastLocation.getLatitude();
                            longitude=mLastLocation.getLongitude();
                            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
                        }
                    }
            );
        }
    }

    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Don't have location permission", Toast.LENGTH_SHORT).show();
            return;
        } else
            fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private boolean isLocationEnabled() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER)) return true;
        else return false;
    }

    private void showGPSTurnOnOption() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        LocationServices
                .getSettingsClient(this)
                .checkLocationSettings(builder.build())
                .addOnSuccessListener(this, (LocationSettingsResponse response) -> {
                    Log.d(TAG, "showLocationSettingOption: location is on now");
                    getLastLocation();
                })
                .addOnFailureListener(this, ex -> {
                    if (ex instanceof ResolvableApiException) {
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) ex;
                            resolvable.startResolutionForResult(this, LOCATION_SETTINGS_REQUEST_CODE);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // handle later
                        }
                    }
                });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: size=" + grantResults.length);
        if (requestCode == FINE_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isLocationEnabled()) {
                    getLastLocation();
                } else {
                    showGPSTurnOnOption();
                }
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Log.d(TAG, "Clicked");
        switch (item.getItemId()) {
            case R.id.nav_appointment:
                intent = new Intent(ActivityHomePage.this, ActivityMyAppointment.class);
                startActivity(intent);
                break;
            case R.id.home_nav_profile:
                intent = new Intent(ActivityHomePage.this, ActivityProfile.class);
                intent.putExtra("user_id", firebaseUser != null ? firebaseUser.getUid() : "null");
                startActivity(intent);
                break;

            case R.id.nav_ambulance:
                intent = new Intent(ActivityHomePage.this, ActivityCallAmbulance.class);
                startActivity(intent);
                break;

            case R.id.home_nav_logout:
                Tools.savePrefBoolean(KEYS.IS_LOGGED_IN, false);
                firebaseAuth.getInstance().signOut();
                intent = new Intent(ActivityHomePage.this, ActivityRegisteredLogin.class);
                startActivity(intent);
                finish();


                //todo why??
                //intent.putExtra("finish", true);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                break;


            case R.id.home_nav_rateApp:
                ratingBar.setRating(0);
                binding.drawerLayout.closeDrawer(GravityCompat.START, false);
                dialogRating.show();
                break;

            case R.id.hone_nav_aboutus:
                startActivity(new Intent(ActivityHomePage.this,ActivityAbout.class));
                binding.drawerLayout.closeDrawer(GravityCompat.START, false);
                break;
        }
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void moveCamera(Location location) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM));
    }
    public void moveCamera(Location location, float zoomlevel) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoomlevel));
    }
    public void moveCamera(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        mMap.addMarker(markerOptions);
    }
    public void moveCamera(LatLng latLng, String caller) {
        Log.d(TAG, "moveCamera: called by " + caller);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
    }


    public void moveCameraAndShowNearbyHospital(Location location, float zoomlevel) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoomlevel));
        getNearbyHospital(location.getLatitude(), location.getLongitude());
    }

    private void getNearbyHospital(double latitude, double longitude) {
        viewModelEmedic.getNearbyPlaces(latitude, longitude, "hospital").observe(ActivityHomePage.this,
                new Observer<List<HashMap<String, String>>>() {
                    @Override
                    public void onChanged(List<HashMap<String, String>> hashMaps) {
                        if (hashMaps != null) {
                            m1(hashMaps);
                        } else
                            Toast.makeText(ActivityHomePage.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
        /*Object dataTransfer[] = new Object[2];
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        //testing purpose
        String hospital = "hospital";
        Log.d(TAG, "hospital");

        String url = getUrl(latitude, longitude, hospital);
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        getNearbyPlacesData.execute(dataTransfer);
        Toast.makeText(ActivityHomePage.this, "Showing Nearby hospital", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Arround Location:" + GetNearbyPlacesData.arround_hospital_list.size());*/
    }

    private void showNearbyHospital(List<HashMap<String, String>> nearbyPlaceList) {
        nearByHospitals.clear();
        for (int i = 0; i < nearbyPlaceList.size(); i++) {
            HashMap<String, String> googlePlace = nearbyPlaceList.get(i);

            String placeName = googlePlace.get("place_name");
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            //String vicinity = googlePlace.get("vicinity");
            //String category = googlePlace.get("category");
            Log.d(TAG, "PLace name:>>>>>>" + placeName);
            Log.d(TAG, "latitude:>>>>>>" + lat);
            Log.d(TAG, "Longitude:>>>>>>" + lng);

            ArroundHosAddress_info arroundHosAddress_info = new ArroundHosAddress_info(placeName, lat, lng, "", "");
            nearByHospitals.add(i, arroundHosAddress_info);

            LatLng latLng = new LatLng(lat, lng);
            getDistanceInformation(latLng, mLastLocation, i);


            MarkerOptions markerOptions = new MarkerOptions()
                    .title(placeName)
                    .position(new LatLng(lat, lng))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mMap.addMarker(markerOptions);
            moveCamera(mLastLocation);//todo recheck


        }
        Log.d(TAG, "showNearbyPlaces: size=" + nearByHospitals.size());
        adapterHospitalList.notifyDataSetChanged();
    }

    @Override
    public void arroundHosAddress_info(ArroundHosAddress_info arroundHosAddress_info, String hospital_id) {
        Log.d(TAG, "" + arroundHosAddress_info.getAddress());
        Log.d(TAG, "" + hospital_id);

        Intent intent = new Intent(ActivityHomePage.this, ActivityMapWithDeriction.class);
        intent.putExtra("hospital_name", arroundHosAddress_info.getAddress());
        intent.putExtra("hospital_id", hospital_id);
        intent.putExtra("hospital_distance", arroundHosAddress_info.getDistance());
        intent.putExtra("hospital_time", arroundHosAddress_info.getTime());
        intent.putExtra("hospital_lat", arroundHosAddress_info.getLatitude());
        intent.putExtra("hospital_lng", arroundHosAddress_info.getLongitude());
        startActivity(intent);
    }

    void m1(List<HashMap<String, String>> hashMaps) {
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference("Hospital_Registration");
        databaseReference.keepSynced(true);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    hospitalsToCompareWith.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Hospital_info hospital_info = dataSnapshot1.getValue(Hospital_info.class);
                        hospitalsToCompareWith.add(hospital_info);
                    }
                    showNearbyHospital(hashMaps);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void getDistanceInformation(LatLng destinationLatLang, Location OriginLocation, final int position) {
//        Log.d(TAG, "position: " + position + "> destination: " + destinationLatLang + "> originlocation:" + OriginLocation);
//        try {
//            String lat2 = String.valueOf(destinationLatLang.latitude);
//            String long2 = String.valueOf(destinationLatLang.longitude);
//
//            String lat1 = String.valueOf(OriginLocation.getLatitude());
//            String long1 = String.valueOf(OriginLocation.getLongitude());
//
//            Map<String, String> mapQuery = new HashMap<>();
//            mapQuery.put("units", "imperial");
//            mapQuery.put("origins", lat1 + "," + long1);
//            mapQuery.put("destinations", lat2 + "," + long2);
//            mapQuery.put("key", "AIzaSyCWzXk-SGxgedHpO8pnTA0h6aYzEiJL_ss");
//            viewmodelDirectionApi.getDistanceResponse(mapQuery).observe(this, new Observer<DistanceResponse>() {
//                @Override
//                public void onChanged(DistanceResponse distanceResponse) {
//                    if (distanceResponse != null) {
//                        final Element element = distanceResponse.getRows().get(0).getElements().get(0);
//                        nearByHospitals.get(position).setTime(element.getDuration().getText());
//                        nearByHospitals.get(position).setDistance(element.getDistance().getText());
//                        Log.d(TAG,"Distance: "+element.getDistance().getText());
//                        Log.d(TAG,"Duration: "+element.getDuration().getText());
//                        adapterHospitalList.notifyDataSetChanged();
//
//                        Log.d(TAG, "getDistanceResponse: " + element.getDuration().getText() + "   &   " + element.getDistance().getValue() + "");
//                    } else {
//                        Log.d(TAG, "getDistanceResponse: null");
//                    }
//                }
//            });
//        } catch (Exception e) {
//            Log.d("dddddd", "getDistanceInformation: exception:" + e.getMessage());
//        }
        try {

            String lat2 = String.valueOf(destinationLatLang.latitude);
            String long2 = String.valueOf(destinationLatLang.longitude);

            String lat1 = String.valueOf(OriginLocation.getLatitude());
            String long1 = String.valueOf(OriginLocation.getLongitude());
            Log.d(TAG, "Current Location: " + lat1 + " >><< " + long1);
            Log.d(TAG, "Destination Location: " + lat2 + " >><< " + long2);

            Map<String, String> mapQuery = new HashMap<>();
            mapQuery.put("units", "imperial");
            mapQuery.put("origins", lat1 + "," + long1);
            mapQuery.put("destinations", lat2 + "," + long2);
            mapQuery.put("key", "AIzaSyCWzXk-SGxgedHpO8pnTA0h6aYzEiJL_ss");

            ApiInterface client = ApiClient.getApiInterface();

            Call<DistanceResponse> call = client.getDistanceInfos(mapQuery);
            call.enqueue(new Callback<DistanceResponse>() {
                @Override
                public void onResponse(Call<DistanceResponse> call, Response<DistanceResponse> response) {

                    if (response != null) {

                        Element element = response.body().getRows().get(0).getElements().get(0);
                        Log.d(TAG, "response distance: " + element.getDistance().getText());
                        Log.d(TAG, "response diruation: " + element.getDuration().getText());

                        nearByHospitals.get(position).setTime(element.getDuration().getText());
                        nearByHospitals.get(position).setDistance(element.getDistance().getText());
                        adapterHospitalList.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "null response");
                    }


                }

                @Override
                public void onFailure(Call<DistanceResponse> call, Throwable t) {
                    Log.d(TAG, "failure");
                }
            });
        } catch (Exception e) {
            Log.d("ddd", "getDistanceInformation: exception:" + e.getMessage());
        }


    }

    private void getImage(String picUrl) {
        View header = navigationView.getHeaderView(0);
        userImage = header.findViewById(R.id.homepage_nav_image);
        Picasso.get().load(picUrl)
                .placeholder(R.drawable.user_images)
                .fit()
                .centerCrop()
                .into(userImage);
    }


    @Override
    protected void onResume() {
        super.onResume();
        binding.drawerLayout.closeDrawer(GravityCompat.START, false);
    }
}



/*    private void initPlacesAPI() {
        Places.initialize(getApplicationContext(), "AIzaSyCWzXk-SGxgedHpO8pnTA0h6aYzEiJL_ss");
    }

    private void initAutoComplete() {

        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG,
                Place.Field.TYPES);

        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .setCountry("bd")
                .setHint("Search for Places")
                .build(ActivityHomePage.this);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_SETTINGS_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    getLastLocation();
                    break;
                case RESULT_CANCELED:
                    new AlertDialog.Builder(this)
                            .setTitle("Enable Location Services")
                            .setMessage("You need to enable Location Services to Get Nearby Hospital Details")
                            .setPositiveButton("Enable", new
                                    DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                            showGPSTurnOnOption();
                                        }
                                    })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(ActivityHomePage.this, "Unable to detect location!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
            }
        }

        if (requestCode == 100 && resultCode == RESULT_OK) {
            place = Autocomplete.getPlaceFromIntent(data);
            Log.d(TAG, "place address: " + place.getAddress());
            location_name = place.getName();
            //todo changed my tanvir
            //binding.hospitalSearchEditText.setText("" + location_name);
            LatLng latLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
            moveCamera(latLng);
        }
    }*/




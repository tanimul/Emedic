package com.example.emadic.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.emadic.KEYS;
import com.example.emadic.R;
import com.example.emadic.Tools;
import com.example.emadic.databinding.ActivityNewProfileBinding;
import com.example.emadic.databinding.ActivityRegisteredLoginBinding;
import com.example.emadic.modelclass.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ActivityNewProfile extends AppCompatActivity {
    private static final String TAG = "ActivityNewProfile";
    private ActivityNewProfileBinding binding;
    public static UserInfo userInfo = new UserInfo("", "", "", "", "","");
    private String phone_no, u_id;
    private int image_rec_code = 1;
    private Uri filepath_uri;
    private StorageReference storageReference;
    private long lastclicktime = 0;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference;
    private String image_url, name, blood_grp, contact_number, medical_condition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        spinner_bloodtype();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            phone_no = extras.getString("phone_Number");
            u_id = extras.getString("U_id");
            Log.d(TAG, phone_no);
            Log.d(TAG, u_id);
        }

        binding.newProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - lastclicktime < 1000) {
                    return;
                }
                lastclicktime = SystemClock.elapsedRealtime();

                openGallery();
            }
        });

        binding.btnCompleteRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - lastclicktime < 1000) {
                    return;
                }
                lastclicktime = SystemClock.elapsedRealtime();

                if (userRegistrationValidation()) {
                    Log.d(TAG, "Validation sucessfull");
                    userRegistration();
                } else {
                    Toast.makeText(ActivityNewProfile.this, "Please fill the all Informations", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    //check all Validation
    public boolean userRegistrationValidation() {
        name = binding.editTextNewProfileName.getText().toString();
        blood_grp = binding.editTextYourBloodGourpName.getSelectedItem().toString();
        contact_number = phone_no;
        medical_condition = binding.editTextMedicalCondition.getText().toString();
        if (name.isEmpty()) {
            binding.editTextNewProfileName.setError("Enter Your Name please");
            binding.editTextNewProfileName.requestFocus();
            return false;
        }

        if (blood_grp.equals("Blood Group")) {
            binding.editTextYourBloodGourpName.requestFocus();
            binding.editTextYourBloodGourpName.setFocusable(true);
            return false;

        }

        if (medical_condition.isEmpty()) {

            binding.editTextMedicalCondition.setError("Enter Your Medical Condition please");
            binding.editTextMedicalCondition.requestFocus();
            return false;

        }

        if (filepath_uri == null) {
            Toast.makeText(this, "Please fill the all field", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }

    }

    public void userRegistration() {
        final ProgressDialog Dialog = new ProgressDialog(ActivityNewProfile.this);
        Dialog.setMessage("Please wait ...");
        Dialog.show();
        databaseReference = FirebaseDatabase.getInstance().getReference("User_Registration");
        storageReference = FirebaseStorage.getInstance().getReference("User_Image");
        userInfo.setName(name);
        userInfo.setContact_number(phone_no);
        userInfo.setMedical_condition(medical_condition);
        userInfo.setBlood_grp(blood_grp);
        userInfo.setUserid(u_id);
        Log.d(TAG, "registration processing");
        if (filepath_uri != null) {

            final StorageReference storageReference2 = storageReference.child(System.currentTimeMillis() + "." + GetFileExtention(filepath_uri));
            Log.d(TAG, "Image uplaode");
            storageReference2.putFile(filepath_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            storageReference2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();
                                    Dialog.dismiss();
                                    Toast.makeText(ActivityNewProfile.this, "Successfully added", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "done");

                                    userInfo.setImage_url(url);
                                    databaseReference.child(u_id).setValue(userInfo);
                                    Log.d(TAG, "done" + url);


                                    Tools.savePrefBoolean(KEYS.IS_LOGGED_IN,true);
                                    Tools.savePref(KEYS.USER_NAME,userInfo.getName());
                                    Tools.savePref(KEYS.USER_NUMBER,userInfo.getContact_number());
                                    Tools.savePref(KEYS.USER_PIC_URL,userInfo.getImage_url());


                                    Intent intent = new Intent(ActivityNewProfile.this, ActivityHomePage.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "" + e.getMessage());
                    Dialog.dismiss();
                    Toast.makeText(ActivityNewProfile.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Dialog.dismiss();
            Log.d(TAG, "Image null");
        }

    }

    //Galary open for place picture
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, image_rec_code);
    }

    //Get image extention
    public String GetFileExtention(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == image_rec_code && resultCode == RESULT_OK && data != null) {
            filepath_uri = data.getData();
            Picasso.get().load(filepath_uri).into(binding.newProfileImage);
        }

    }
    private void spinner_bloodtype() {
        ArrayAdapter<String> blood_typeadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.blood_group));
        blood_typeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.editTextYourBloodGourpName.setAdapter(blood_typeadapter);
    }
}

//confirm
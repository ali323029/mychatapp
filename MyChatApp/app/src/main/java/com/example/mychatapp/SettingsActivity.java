package com.example.mychatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private CircleImageView mDisplayImage;
    private TextView mname;
    private TextView mStatus;
    private Button mstatusButton,mImageButtton;
    private ProgressDialog mprogressdialog;

    private static final int gallerypick = 1;

    private StorageReference mImageStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mDisplayImage = (CircleImageView)findViewById(R.id.setting_image);
        mname = (TextView)findViewById(R.id.settings_display_Name);
        mStatus = (TextView)findViewById(R.id.setting_status_tv);

        mstatusButton = (Button)findViewById(R.id.setting_status_btn);
        mImageButtton = (Button)findViewById(R.id.setting_changeimage_btn);

        mImageStorage = FirebaseStorage.getInstance().getReference();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mCurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        mUserDatabase.keepSynced(true);  // For offline capability

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                final String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                mname.setText(name);
                mStatus.setText(status);
                if(!image.equals("default")) {
//                    Picasso.get().load(thumb_image).placeholder(R.drawable.defaultdpimage).into(mDisplayImage);

                    Picasso.get().load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.defaultdpimage).into(mDisplayImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {

                            Picasso.get().load(thumb_image).placeholder(R.drawable.defaultdpimage).into(mDisplayImage);

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mstatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status_value = mStatus.getText().toString();
                Intent i = new Intent(SettingsActivity.this,StatusActivity.class);
                i.putExtra("status_value",status_value);
                startActivity(i);
            }
        });

// for selecting 1 image from the gallery
        mImageButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),gallerypick);
            }
        });


    }

// getting selected image data

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(requestCode == gallerypick && resultCode == RESULT_OK)
        {


            //getting sellected image uri
            Uri imageUri = data.getData();

            // cropping selected image
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            // getting cropped image data
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mprogressdialog = new ProgressDialog(this);
                mprogressdialog.setTitle("Uploading Image...");
                mprogressdialog.setMessage("Please wait while we upload and process the image.");
                mprogressdialog.setCanceledOnTouchOutside(false);
                mprogressdialog.show();

//getting cropped image uri
               final Uri resultUri = result.getUri();

                final String current_user_id = mCurrentUser.getUid();




                final StorageReference imagepath = mImageStorage.child("profile_images").child(current_user_id+".jpg");


                imagepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful())
                        {                                            
                                    imagepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                        //for_thumbimage
                                            final File thumb_filepath = new File(resultUri.getPath());
                                            try {

                                                Bitmap tumb_bitmap = new Compressor(getApplicationContext())
                                                        .setMaxWidth(200)
                                                        .setMaxHeight(200)
                                                        .setQuality(75)
                                                        .compressToBitmap(thumb_filepath);

                                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                tumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                                final byte[] thumb_byte = baos.toByteArray();
                                                final StorageReference thumb_imagepath = mImageStorage.child("profile_images").child("thumb").child(current_user_id+".jpg");

                                                final UploadTask uploadTask = thumb_imagepath.putBytes(thumb_byte) ;



                                                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                    @Override
                                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                        if (!task.isSuccessful()) {
                                                            throw task.getException();
                                                        }
                                                        // Continue with the task to get the download URL
                                                        return thumb_imagepath.getDownloadUrl();
                                                    }
                                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Uri> task) {
                                                        if (task.isSuccessful()) {
                                                            Uri downloadUri = task.getResult();
                                                            if (downloadUri != null) {

                                                                String photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                                                                Toast.makeText(SettingsActivity.this,photoStringLink, Toast.LENGTH_SHORT).show();
                                                                mUserDatabase.child("thumb_image").setValue(photoStringLink);
                                                                Toast.makeText(SettingsActivity.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                                                            }

                                                        } else {
                                                            Toast.makeText(SettingsActivity.this, "Failed to Upload", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

//
//                                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
//
//                                                       // String thumb_download_uri = thumb_task.getResult().getMetadata().getReference().toString();
//                                                         // String thumb_download_uri = thumb_task.getResult().getMetadata().getReference().getDownloadUrl().toString();
//
//                                                          String thumb_download_uri = .toString();
//
//                                                        Toast.makeText(SettingsActivity.this,thumb_download_uri,Toast.LENGTH_LONG).show();
//
//
//                                                        mUserDatabase.child("thumb_image").setValue(thumb_download_uri);
//
//
//                                                        // String thumb_download_uri = thumb_imagepath.getDownloadUrl().toString();
//
//
//                                                    }
//                                                });


                                            } catch (IOException e) {
                                                Toast.makeText(SettingsActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                            }
                                            //thumbimage_ends




                                            String downloadimage_url = uri.toString();

                                            mUserDatabase.child("image").setValue(downloadimage_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    mprogressdialog.hide();
                                                    Toast.makeText(SettingsActivity.this,"image uploaded",Toast.LENGTH_LONG).show();

                                                }
                                            });

                                             }
                                    });
                        }else
                        {
                            mprogressdialog.dismiss();
                            Toast.makeText(SettingsActivity.this,"something is wrong",Toast.LENGTH_LONG).show();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }



}

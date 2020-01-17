package com.example.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {


    private TextView mprofile_name;
    private TextView mprofile_status;
    private TextView mprofile_total_friends;
    private Button mrequestbtn;
    private ImageView mprofile_image;

    private DatabaseReference mUserDataBase;
    private DatabaseReference mFriendRequestDB;
    private DatabaseReference mFriendsDB;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mNotificationDB;


    private ProgressDialog mprogProgressDialog;

    private String mcurrent_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mprogProgressDialog = new ProgressDialog(this);
        mprogProgressDialog.setTitle("Loading User Data");
        mprogProgressDialog.setMessage("Please wait while we load user data");
        mprogProgressDialog.setCanceledOnTouchOutside(false);
        mprogProgressDialog.show();

        final String user_id = getIntent().getStringExtra("user_id");

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserDataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendRequestDB = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");
        mFriendsDB = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDB = FirebaseDatabase.getInstance().getReference().child("Notifications");


        mcurrent_state = "not_friend";

        mprofile_image = (ImageView)findViewById(R.id.profile_image);
        mprofile_name = (TextView)findViewById(R.id.profile_name);
        mprofile_status = (TextView)findViewById(R.id.profile_status);
        mprofile_total_friends  = (TextView)findViewById(R.id.profile_total_friends);

        mrequestbtn = (Button)findViewById(R.id.request_button);


        mUserDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String profileName = dataSnapshot.child("name").getValue().toString();
                String profilestatus = dataSnapshot.child("status").getValue().toString();
                String profileImage = dataSnapshot.child("image").getValue().toString();

                mprofile_name.setText(profileName);
                mprofile_status.setText(profilestatus);

                Picasso.get().load(profileImage).placeholder(R.drawable.defaultdpimage).into(mprofile_image);


                mFriendRequestDB.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_id))
                        {
                            String request_type = dataSnapshot.child(user_id).child("Request_Type").getValue().toString();

                            if(request_type.equals("received")){
                                mcurrent_state = "request_received";
                                mrequestbtn.setText("Accept Request");
                                mprogProgressDialog.dismiss();


                            }else if(request_type.equals("sent"))
                            {
                                mcurrent_state = "request_sent";
                                mrequestbtn.setText("Cancel Request");
                                mprogProgressDialog.dismiss();

                            }
                        }
                        mprogProgressDialog.dismiss();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mprogProgressDialog.dismiss();




        mFriendsDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(mCurrentUser.getUid()))
                {
                    if(dataSnapshot.child(mCurrentUser.getUid()).hasChild(user_id))
                    {
                        mcurrent_state = "friends";
                        mrequestbtn.setText("Unfriend");
                        mprogProgressDialog.dismiss();

                    }
                }
                mprogProgressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        /*-------------------Not Friend State-------------*/
        mrequestbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mrequestbtn.setEnabled(false);

                if(mcurrent_state.equals("not_friend"))
                {
                    mFriendRequestDB.child(mCurrentUser.getUid()).child(user_id).child("Request_Type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                mFriendRequestDB.child(user_id).child(mCurrentUser.getUid()).child("Request_Type")
                                        .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        HashMap<String,String> notificationData = new HashMap<>();
                                        notificationData.put("from",mCurrentUser.getUid());
                                        notificationData.put("type","request");
                                        mNotificationDB.child(user_id).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mcurrent_state = "request_sent";
                                                mrequestbtn.setText("Cancel Request");

                                            }
                                        });

                                        mcurrent_state = "request_sent";
                                        mrequestbtn.setText("Cancel Request");



                                        Toast.makeText(ProfileActivity.this,"Request Sent",Toast.LENGTH_LONG).show();

                                    }
                                });

                            }else
                            {
                                Toast.makeText(ProfileActivity.this,"Failing in Sending request",Toast.LENGTH_LONG).show();
                            }
                            mrequestbtn.setEnabled(true);


                        }
                    });

                }
                /*-------------------Cancel request State-------------*/
                if(mcurrent_state.equals("request_sent"))
                {
                    mFriendRequestDB.child(mCurrentUser.getUid()).child(user_id).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendRequestDB.child(user_id).child(mCurrentUser.getUid()).removeValue();

                            mrequestbtn.setEnabled(true);
                            mcurrent_state = "not_friend";
                            mrequestbtn.setText("Send Request");
                        }
                    });
                }


                /*-------------------request received-------------*/
                if(mcurrent_state.equals("request_received"))
                {
                    final String mcurrentData = DateFormat.getDateTimeInstance().format(new Date());
                    mFriendsDB.child(mCurrentUser.getUid()).child(user_id).setValue(mcurrentData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendsDB.child(user_id).child(mCurrentUser.getUid()).child("date").setValue(mcurrentData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //Now removing requests

                                    mFriendRequestDB.child(mCurrentUser.getUid()).child(user_id).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    mFriendRequestDB.child(user_id).child(mCurrentUser.getUid()).removeValue();

                                                    mrequestbtn.setEnabled(true);
                                                    mcurrent_state = "friends";
                                                    mrequestbtn.setText("Unfriend");
                                                }
                                            });

                                }
                            });

                        }
                    });

                }

                // unfriend

                if(mcurrent_state.equals("friends"))
                {

                    mFriendsDB.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendsDB.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                                    mrequestbtn.setEnabled(true);
                                                    mcurrent_state = "not_friend";
                                                    mrequestbtn.setText("Send Request");
                                }
                            });

                        }
                    });



                }



            }
        });






    }
}

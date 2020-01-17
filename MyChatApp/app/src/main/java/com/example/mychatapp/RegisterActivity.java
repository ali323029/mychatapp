package com.example.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import androidx.appcompat.widget.Toolbar;

import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mpassword;
    private Button regbtn;
    private Toolbar mtoolbar;
    private ProgressDialog mRegprogress;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mtoolbar = (Toolbar)findViewById(R.id.register_tool_bar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRegprogress = new ProgressDialog(this);

        mDisplayName = (TextInputLayout)findViewById(R.id.reg_display_name);
        mEmail = (TextInputLayout)findViewById(R.id.reg_email);
        mpassword = (TextInputLayout)findViewById(R.id.reg_password);


        regbtn = (Button)findViewById(R.id.reg_create_btn);

        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mdisplayname = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mpassword.getEditText().getText().toString();
                if(password.length()<8)
                {
                    Toast.makeText(RegisterActivity.this,"The Password Length should be greater and equal to Eight",Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(password))
                {
                    Toast.makeText(RegisterActivity.this,"Please fill the fileds",Toast.LENGTH_SHORT).show();

                }
                if(TextUtils.isEmpty(mdisplayname))
                {
                    Toast.makeText(RegisterActivity.this,"Please fill the fileds",Toast.LENGTH_SHORT).show();

                }
                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(RegisterActivity.this,"Please fill the fileds",Toast.LENGTH_SHORT).show();
                }

                if(!TextUtils.isEmpty(mdisplayname) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

                   mRegprogress.setTitle("Registering User");
                   mRegprogress.setMessage("Please wait while we create your account !");
                   mRegprogress.setCanceledOnTouchOutside(false);
                   mRegprogress.show();
                    register_User(mdisplayname,email,password);
                    Toast.makeText(RegisterActivity.this,"Account Created Successfully",Toast.LENGTH_SHORT).show();
                }else
                {
                    Toast.makeText(RegisterActivity.this,"Please fill the fileds",Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    private void register_User(final String mdisplayname, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = current_user.getUid();

                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                            HashMap<String,String> usermap = new HashMap<>();
                            usermap.put("name",mdisplayname);
                            usermap.put("status","Hi there! I am using MyChatApp");
                            usermap.put("image","default");
                            usermap.put("thumb_image","default");
                            mDatabase.setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        String current_user_id = mAuth.getCurrentUser().getUid();
                                        String token  = FirebaseInstanceId.getInstance().getToken();
                                        mDatabase.child("device_token").setValue(token).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mRegprogress.dismiss();
                                                Intent i = new Intent(RegisterActivity.this,MainActivity.class);
                                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(i);
                                                finish();
                                            }
                                        });

                                    }

                                }
                            });



                        } else {
                            mRegprogress.hide();
                            Toast.makeText(RegisterActivity.this,"Cannot Sign in. Please check the form and try again",Toast.LENGTH_SHORT);

                        }

                    }
                });
    }

}

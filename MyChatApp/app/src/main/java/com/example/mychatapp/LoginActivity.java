package com.example.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout mEmail;
    private TextInputLayout mpassword;
    private Button signinbtn;
    private Toolbar mtoolbar;
    private ProgressDialog msignprogress;

    private FirebaseAuth mAuth;
    private DatabaseReference mLoginDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mtoolbar = (Toolbar)findViewById(R.id.login_tool_bar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        msignprogress = new ProgressDialog(this);

        mEmail = (TextInputLayout)findViewById(R.id.signin_email);
        mpassword = (TextInputLayout)findViewById(R.id.signin_password);

        mLoginDB = FirebaseDatabase.getInstance().getReference().child("Users");


        signinbtn = (Button)findViewById(R.id.signin_btn);


        signinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getEditText().getText().toString();
                String password = mpassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){

                    msignprogress.setTitle("Logging In");
                    msignprogress.setMessage("Please wait while check your credentials.");
                    msignprogress.setCanceledOnTouchOutside(false);
                    msignprogress.show();
                    Sign_User(email,password);
                }
            }
        });
    }

    private void Sign_User(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            msignprogress.dismiss();

                            String current_user_id = mAuth.getCurrentUser().getUid();
                            String token  = FirebaseInstanceId.getInstance().getToken();

                            mLoginDB.child(current_user_id).child("device_token").setValue(token).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    sendToMain();
                                }
                            });



                        } else {
                            msignprogress.hide();
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    private void sendToMain() {
        Intent i = new Intent(LoginActivity.this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

}

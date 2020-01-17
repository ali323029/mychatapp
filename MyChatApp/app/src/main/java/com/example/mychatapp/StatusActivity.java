package com.example.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private TextInputLayout mstatus;
    private Button msave;
    private Toolbar mtoolbar;
    private ProgressDialog mstatusRegprogress;

    private DatabaseReference mstatusDataBase;
    private FirebaseAuth mCurrentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mtoolbar = (Toolbar)findViewById(R.id.status_app_bar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String status_value = getIntent().getStringExtra("status_value");

        mstatus = (TextInputLayout)findViewById(R.id.status_input);
        msave = (Button)findViewById(R.id.save_change_btn);

        mstatus.getEditText().setText(status_value);
        String uid = mCurrentUser.getInstance().getCurrentUser().getUid();
        mstatusDataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        msave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mstatusRegprogress = new ProgressDialog(StatusActivity.this);
                mstatusRegprogress.setTitle("Saving Changes");
                mstatusRegprogress.setMessage("Please wait while we save the changes");
                mstatusRegprogress.setCanceledOnTouchOutside(false);
                mstatusRegprogress.show();
                String status = mstatus.getEditText().getText().toString();
                mstatusDataBase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            mstatusRegprogress.dismiss();
                        }else
                        {
                            Toast.makeText(StatusActivity.this,"Error in Status",Toast.LENGTH_LONG);
                        }
                    }
                });

            }
        });

    }
}

package com.example.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private RecyclerView mRecyclerView;
    private DatabaseReference mDataBase;
    FBRecyclerAdopter fbRecyclerAdopter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mtoolbar = findViewById(R.id.user_AppBar);
        setSupportActionBar(mtoolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("userlist");

        mDataBase = FirebaseDatabase.getInstance().getReference().child("Users");

        mRecyclerView = findViewById(R.id.users_List);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(UsersActivity.this));
        //mRecyclerView.setHasFixedSize(true);


        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(mDataBase, Users.class)
                        .build();
         fbRecyclerAdopter = new FBRecyclerAdopter(options);

        mRecyclerView.setAdapter(fbRecyclerAdopter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        fbRecyclerAdopter.startListening();
    }


    @Override
    protected void onStop() {
        super.onStop();
       // fbRecyclerAdopter.stopListening();
    }


}

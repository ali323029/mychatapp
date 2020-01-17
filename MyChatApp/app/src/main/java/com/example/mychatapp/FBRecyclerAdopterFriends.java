package com.example.mychatapp;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;


import static androidx.core.content.ContextCompat.createDeviceProtectedStorageContext;
import static androidx.core.content.ContextCompat.startActivity;


public class FBRecyclerAdopterFriends extends FirebaseRecyclerAdapter<Friends,FBRecyclerAdopterFriends.FreindsViewHolder>{

    public FBRecyclerAdopterFriends(@NonNull FirebaseRecyclerOptions options) {
        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull FBRecyclerAdopterFriends.FreindsViewHolder holder, int position, @NonNull Friends model) {

        holder.date.setText(model.getDate());

    }

    @NonNull
    @Override
    public FBRecyclerAdopterFriends.FreindsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View mview = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_friends,parent,false);


        return new FreindsViewHolder(mview);
    }


    class FreindsViewHolder extends RecyclerView.ViewHolder
    {
        public TextView date;

        public FreindsViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.profile_status);
        }
    }
}

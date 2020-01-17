package com.example.mychatapp;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.core.content.ContextCompat.startActivity;

public class FBRecyclerAdopter extends FirebaseRecyclerAdapter<Users,FBRecyclerAdopter.UserViewHolder> {



    public FBRecyclerAdopter(@NonNull FirebaseRecyclerOptions<Users> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Users model) {

        holder.name.setText(model.getName());
        holder.status.setText(model.getStatus());
     //   holder.user_image.setText(model.getThumb_image());
        Picasso.get().load(model.getThumb_image()).placeholder(R.drawable.defaultdpimage).into(holder.user_image);

        final String user_id = getRef(position).getKey();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent user_profile = new Intent(v.getContext(),ProfileActivity.class);
                user_profile.putExtra("user_id",user_id);
                startActivity(v.getContext(),user_profile,null);
            }
        });

    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_single_layout,parent,false);

        return new UserViewHolder(view);
    }

    class UserViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;
        TextView status;
        CircleImageView user_image;


        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.single_username);
            status = itemView.findViewById(R.id.single_userstatus);
            user_image = (CircleImageView)itemView.findViewById(R.id.single_userimage);
        }
    }
}

package com.example.mychatapp;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private View mview;
    private Context mcontext;
    private RecyclerView mRecyclerView;
    private DatabaseReference mDataBase;
    FBRecyclerAdopterFriends fbRecyclerAdopterFriends;
    String mCurrentUser;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDataBase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrentUser);
        Toast.makeText(getContext(),mCurrentUser,Toast.LENGTH_LONG).show();
        mview = inflater.inflate(R.layout.fragment_friends, container, false);
        mRecyclerView = (RecyclerView)mview.findViewById(R.id.friends_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.hasFixedSize();


        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(mDataBase,Friends.class).build();

        mcontext = getContext();
        fbRecyclerAdopterFriends = new FBRecyclerAdopterFriends(options);
        mRecyclerView.setAdapter(fbRecyclerAdopterFriends);



        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        fbRecyclerAdopterFriends.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        fbRecyclerAdopterFriends.stopListening();
    }
}

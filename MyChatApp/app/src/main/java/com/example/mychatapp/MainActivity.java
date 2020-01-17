package com.example.mychatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private FirebaseAuth mAuth;
    private ViewPager mviewpager;
    private SectionPagerAdopter mSecionPagerAdopter;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mtoolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("ChatAPP");


        mviewpager = (ViewPager)findViewById(R.id.main_tabpager);
        mSecionPagerAdopter = new SectionPagerAdopter(getSupportFragmentManager());
        mviewpager.setAdapter(mSecionPagerAdopter);

        mTabLayout = (TabLayout)findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mviewpager);


    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null)
        {
            sendToStart();
        }
    }

    private void sendToStart() {
        Intent i = new Intent(this,StartActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.main_menu,menu);
         return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);

         if(item.getItemId() == R.id.main_logout_btn)
         {
             FirebaseAuth.getInstance().signOut();
             sendToStart();
         }
         if(item.getItemId() == R.id.main_setting_btn){
             Intent i = new Intent(this,SettingsActivity.class);
             startActivity(i);
         }
         if(item.getItemId() == R.id.main_all_btn)
         {
             Intent i = new Intent(this,UsersActivity.class);
             startActivity(i);
         }

        return true;
    }



}

package com.chanu.childtask;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Home extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private Toolbar toolbar;
    public static TabLayout tabLayout;
    private ViewPager viewPager;

    public boolean isParent;

    DatabaseReference Data;
    SharedVariable sharedVariable;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ImageButton addBtn = findViewById(R.id.openAddButton);
        FirebaseApp.initializeApp(Home.this);
        Data = FirebaseDatabase.getInstance().getReference();

        sharedVariable = new SharedVariable(this);
        isParent = sharedVariable.getIsParent();
        setCommon();
        common.isParent=isParent;

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");

        tabLayout = findViewById(R.id.tablayout);
        if (!isParent) {
            addBtn.setVisibility(View.VISIBLE);
            tabLayout.addTab(tabLayout.newTab().setText("My Schedule"));
            tabLayout.addTab(tabLayout.newTab().setText("Who Guide Me"));
            tabLayout.addTab(tabLayout.newTab().setText("Notification"));
        } else if (isParent) {
            addBtn.setVisibility(View.GONE);
            tabLayout.addTab(tabLayout.newTab().setText("View Child"));
            tabLayout.addTab(tabLayout.newTab().setText("Add Child"));
            tabLayout.addTab(tabLayout.newTab().setText("Notification"));
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);

        viewPager = findViewById(R.id.viewpager);
        PageAdapter adapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), isParent);
        viewPager.setAdapter(adapter);
        tabLayout.setOnTabSelectedListener(this);

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> finishAffinity())
                .setNegativeButton("No", null)
                .show();
    }

    public void setCommon() {
//        common.rName = sharedVariable.get;
//        common.rEmail = sharedVariable.get;
//        common.rPassword = sharedVariable.get();
        common.rDBEmail = sharedVariable.getDBEmail();
        common.isGoogleLogin = sharedVariable.getIsGoogle();
        common.isLogin = true;
    }

    public void option(View view) {
        startActivity(new Intent(this, Option.class));
    }

    public void addTask(View view) {
        startActivity(new Intent(this, AddTask.class));
    }
}
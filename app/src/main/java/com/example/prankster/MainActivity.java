package com.example.prankster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postList;
    private Toolbar mToolbar;

    private CircleImageView navProfileImage;
    private TextView navProfileUsername;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        userRef = FirebaseDatabase.getInstance("https://prankster-dee14-default-rtdb.firebaseio.com/").getReference().child("Users");

        mToolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.navigation_view);
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        navProfileImage = (CircleImageView)navView.findViewById(R.id.nav_profile_image);
        navProfileUsername = (TextView)navView.findViewById(R.id.nav_user_full_name);

        if(mAuth.getCurrentUser() != null) {
            currentUserID = mAuth.getCurrentUser().getUid();
            userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.hasChild("fullname")) {
                            String fullname = snapshot.child("fullname").getValue().toString();
                            navProfileUsername.setText(fullname);
                        } else {
                            Toast.makeText(MainActivity.this, "Profile name does not exist", Toast.LENGTH_SHORT).show();
                        }
                        if (snapshot.hasChild("profileimage")) {
                            String image = snapshot.child("profileimage").getValue().toString();
                            Picasso.with(MainActivity.this).load(image).placeholder(R.drawable.profile).into(navProfileImage);
                        } else {
                            Toast.makeText(MainActivity.this, "Profile image does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                userMenuSelector(item);
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            Log.i("Main Activity", "onStart(): current user == null");
            sendUserToLoginActivity();
        }else{
            Log.i("Main Activity", "onStart(): current user != null");
            checkUserExistence();
        }
    }

    private void checkUserExistence() {
        final String current_user_id = mAuth.getCurrentUser().getUid();
        Log.i("MainActivity"," checkUserExistence(): currentUserID: " + current_user_id );

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(current_user_id).exists()){
                    Log.i("checkUserExistence()", "dataSnapshot.child("+ current_user_id+ ").exists()");
                }else{
                    Log.i("checkUserExistence()", "! dataSnapshot.child("+ current_user_id+ ").exists()");
                }
                if(!dataSnapshot.hasChild(current_user_id)){
                    Log.i("MainActivity", "checkUserExistence(): child not found" );
                    Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(setupIntent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void userMenuSelector(MenuItem item){
        switch (item.getItemId()){
            case R.id.nav_profile:

                break;
            case R.id.nav_home:

                break;
            case R.id.nav_friends:

                break;
            case R.id.nav_findFriends:

                break;
            case R.id.nav_messages:

                break;
            case R.id.nav_settings:

                break;
            case R.id.nav_logout:
                sendUserToLoginActivity();
                Log.i("MainActivity", "Logging out");
                break;

        }
    }

    private void sendUserToLoginActivity(){
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
}
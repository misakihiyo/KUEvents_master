package com.example.eforezan.kuevents;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class HomePage extends AppCompatActivity   implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mEventList;


    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUsers;

    private DatabaseReference sDatabase;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mCurrentUser;

    private boolean mProcessGoing = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle;
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        if(mCurrentUser == null){
            Intent loginIntent = new Intent(HomePage.this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Events");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        mDatabase.keepSynced(true);
        mDatabaseUsers.keepSynced(true);

        mEventList = (RecyclerView) findViewById(R.id.event_list);
        mEventList.setHasFixedSize(true);
        mEventList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        TextView going = (TextView) findViewById(R.id.going_status);

        FirebaseRecyclerAdapter<Event, EventViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(
                Event.class,
                R.layout.event_row,
                EventViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(EventViewHolder viewHolder, Event model, int position) {

                final String post_key = getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setStart_Date(model.getStart_date());
                viewHolder.setgoingbtn(post_key);
                //viewHolder.setLatitude(model.getLatitude());
                //viewHolder.setLongitude(model.getLongitude());
                // This is used in second.java
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(HomePage.this, post_key, Toast.LENGTH_LONG).show();
                        Intent singleEventIntent = new Intent(HomePage.this, second.class);
                        singleEventIntent.putExtra("event_id", post_key);
                        startActivity(singleEventIntent);
                    }
                });

                viewHolder.mGoingbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mProcessGoing = true;
                        mDatabase.child( "Events" ).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (mProcessGoing){
                                    if (dataSnapshot.child(post_key).child("Attendee").hasChild(mAuth.getCurrentUser().getUid())){
                                        mDatabase.child(post_key).child("Attendee").child(mAuth.getCurrentUser().getUid()).removeValue();
                                        mProcessGoing = false;


                                    }else{
                                        mDatabase.child(post_key).child("Attendee").child(mAuth.getCurrentUser().getUid()).setValue("Random");
                                        mProcessGoing = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });


            }
            //};
            //}
        };
        mEventList.setAdapter(firebaseRecyclerAdapter);


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen( GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.profile) {
            // setFragment(new profile());
        }
        else if (id == R.id.events) {
            //startActivity( new Intent(MainActivity.this, HomePage.class ) );
        }
        else if (id == R.id.location) {
            Intent i = new Intent(HomePage.this, MapsActivity.class);
            startActivity(i);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder{

        View mView;
        ImageButton mGoingbtn;
        DatabaseReference mDatabase;
        FirebaseAuth mAuth;
        TextView going;

        public EventViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mGoingbtn = (ImageButton) mView.findViewById(R.id.going_btn);
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Events");
            mAuth = FirebaseAuth.getInstance();
            mDatabase.keepSynced(true);
            going = mView.findViewById(R.id.going_status);

        }

        public void setgoingbtn(final String post_key){
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(post_key).child("Attendee").hasChild(mAuth.getCurrentUser().getUid())){
                        mGoingbtn.setImageResource(R.mipmap.ic_bookmark_black_24dp);
                        going.setText("Going");

                    }else{
                        mGoingbtn.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                        going.setText("Are you going?");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void setTitle(String title){
            TextView post_title = (TextView) mView.findViewById(R.id.post_title);
            post_title.setText(title);

        }




        public void setImage(Context ctx, String image){
            ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);

        }

        public void setStart_Date(String start_date){
            TextView post_date=(TextView) mView.findViewById(R.id.post_date);
            post_date.setText(start_date);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        sDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        switch (item.getItemId()) {
            case R.id.action_logout:

                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));

                break;

            case R.id.action_add:

                String uid = mCurrentUser.getUid().toString().trim();

                sDatabase.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String role = (String) dataSnapshot.child("role").getValue();

                        if (role.equals("Admin")){
                            Toast.makeText(HomePage.this, "You are admin", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(HomePage.this, PostActivity.class));

                        }

                        if (role.equals("Normal"))
                        {
                            View b = findViewById(R.id.action_add);
                            b.setVisibility(View.GONE);
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



                // Toast.makeText(HomePage.this, uid, Toast.LENGTH_LONG).show();





                break;

            case R.id.map_button:
                startActivity( new Intent( HomePage.this, MapsActivity.class ) );
        }

        return super.onOptionsItemSelected(item);
    }


}

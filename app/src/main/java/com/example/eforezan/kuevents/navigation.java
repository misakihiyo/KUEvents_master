package com.example.eforezan.kuevents;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class navigation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mEventList;


    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mCurrentUser;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    private DatabaseReference uDatabase;



    private boolean mProcessGoing = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_navigation );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );


        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle( this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( this );

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        if (mCurrentUser == null) {
            Intent loginIntent = new Intent( navigation.this, LoginActivity.class );
            loginIntent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
            startActivity( loginIntent );
        }


        mDatabase = FirebaseDatabase.getInstance().getReference().child( "Events" );
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child( "Users" );

        firebaseUser = mAuth.getCurrentUser();


        mDatabase.keepSynced( true );
        mDatabaseUsers.keepSynced( true );

        mEventList = (RecyclerView) findViewById( R.id.event_list );
        mEventList.setHasFixedSize( true );
        mEventList.setLayoutManager( new LinearLayoutManager( this ) );

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity( new Intent( navigation.this, LoginActivity.class ) );
                }
            }
        };

        final TextView mName = (TextView) navigationView.getHeaderView( 0 ).findViewById( R.id.headername );
        final ImageView mPic = (ImageView) navigationView.getHeaderView( 0 ).findViewById( R.id.headerimageView );

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mName.setText(dataSnapshot.child("name").getValue().toString());

                String link =dataSnapshot.child("image").getValue().toString();
                Picasso.with(getBaseContext()).load(link).into(mPic);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }





        });




    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        if (drawer.isDrawerOpen( GravityCompat.START )) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        TextView going = (TextView) findViewById(R.id.going_status);

        FirebaseRecyclerAdapter<Event, HomePage.EventViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Event, HomePage.EventViewHolder>(
                Event.class,
                R.layout.event_row,
                HomePage.EventViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(HomePage.EventViewHolder viewHolder, Event model, int position) {

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
                        Intent singleEventIntent = new Intent(navigation.this, second.class);
                        singleEventIntent.putExtra("event_id", post_key);
                        startActivity(singleEventIntent);
                    }
                });

                viewHolder.mGoingbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mProcessGoing = true;
                        mDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (mProcessGoing){
                                    if (dataSnapshot.child(post_key).child("Attendee").hasChild(mAuth.getCurrentUser().getUid())){
                                        mDatabase.child(post_key).child("Attendee").child(mAuth.getCurrentUser().getUid()).removeValue();
                                        mDatabaseUsers.child(mAuth.getCurrentUser().getUid()).child( "GoingEvents" ).child(post_key).removeValue();
                                        mProcessGoing = false;


                                    }else{



                                        String uid = (String) mAuth.getCurrentUser().getUid().toString();

                                        uDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

                                        uDatabase.child(uid).addValueEventListener( new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                String name = (String) dataSnapshot.child("name").getValue();

                                                mDatabase.child(post_key).child("Attendee").child(mAuth.getCurrentUser().getUid()).setValue(name);
                                                mDatabaseUsers.child( mAuth.getCurrentUser().getUid()).child( "GoingEvents" ).child( post_key ).setValue( dataSnapshot.child(post_key).child( "title" ).getValue() );
                                                mProcessGoing = false;
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        } );

                                        //mDatabase.child(post_key).child("Attendee").child(mAuth.getCurrentUser().getUid()).setValue(uid);
                                        //mDatabaseUsers.child( mAuth.getCurrentUser().getUid()).child( "GoingEvents" ).child( post_key ).setValue( dataSnapshot.child(post_key).child( "title" ).getValue() );
                                        //mProcessGoing = false;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.main_menu, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_logout:

                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));

                break;

            case R.id.action_add:

                String uid = mCurrentUser.getUid().toString().trim();

                mDatabaseUsers.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String role = (String) dataSnapshot.child("role").getValue();

                        if (role.equals("Admin")){
                            Toast.makeText(navigation.this, "You are admin", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(navigation.this, PostActivity.class));

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
                startActivity( new Intent( navigation.this, MapsActivity.class ) );
        }


        return super.onOptionsItemSelected( item );
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profile) {
            setFragment( new profile() );
        }  else if (id == R.id.events) {
            startActivity( new Intent( navigation.this, navigation.class ) );

        } else if (id == R.id.location) {
            setFragment( new location() );
        }
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }

    public void setFragment(Fragment fragment){
        if(fragment!=null){
            FragmentTransaction ft= getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.layout_layout,fragment).commit();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
}

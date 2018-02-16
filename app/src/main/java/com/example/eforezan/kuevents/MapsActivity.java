package com.example.eforezan.kuevents;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public ArrayList<Double> lati;
    public ArrayList<Double> longi;
    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    Marker marker;
    List<Event> mEventList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_maps );
       mDatabase = FirebaseDatabase.getInstance().getReference().child("Events");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );
        mDatabase.push().setValue(marker);
    }


    @Override
    public void onMapReady( GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

         mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    String lat = child.child("Latitude").getValue().toString();
                    String longi = child.child("Longitude").getValue().toString();
                    String title = child.child( "title" ).getValue().toString();

                    double location_left = Double.parseDouble(lat);
                    double location_right = Double.parseDouble(longi);
                    LatLng cod = new LatLng(location_left, location_right);
                    mMap.addMarker(new MarkerOptions().position(cod).title(title));
                    float zoomLevel = 16.0f; //This goes up to 21
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cod, zoomLevel));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


}
}


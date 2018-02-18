package com.example.eforezan.kuevents;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class tab3 extends Fragment{

    private static final String TAG ="Attended";
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ListView mEventList;
    private ArrayList<String> mEventnames = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_tab3, container, false );


        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child( "Users" ).child( mAuth.getCurrentUser().getUid() ).child( "GoingEvents" );
        mEventList =(ListView) view.findViewById( R.id.attended_list );



        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>( getContext(), android.R.layout.simple_expandable_list_item_1, mEventnames );
        mEventList.setAdapter( arrayAdapter );

        mDatabase.addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String value = dataSnapshot.getValue().toString();
                arrayAdapter.add( value);

                Toast.makeText(getContext(),"Attended list",Toast.LENGTH_LONG  ).show();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );


        return super.onCreateView(inflater, container, savedInstanceState);

    }



}
package com.example.eforezan.kuevents;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class second extends AppCompatActivity {

   String event_title;

    private DatabaseReference mDatabase;

    private ImageView mEventSingleImage;
    private TextView mEventSingleTitle;
    private TextView mEventSingleDesc;

    private EditText name;
    private RadioGroup radio_g;
    private RadioButton radio_b;
    private Button button_sbm;
    private Button button_showdb;

    //int attempt_counter = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Events");

        final String mPost_key = getIntent().getExtras().getString("event_id");

        mEventSingleImage = (ImageView) findViewById(R.id.image_event);
        mEventSingleTitle = (TextView) findViewById(R.id.title_view);
        mEventSingleDesc = (TextView) findViewById(R.id.desc_view);


        //Toast.makeText(second.this, mPost_key, Toast.LENGTH_LONG).show();

        //button_sbm = (Button) findViewById(R.id.button);
        button_showdb = (Button) findViewById(R.id.button_showdb);



        mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String event_title= (String) dataSnapshot.child("title").getValue();
                String event_desc = (String) dataSnapshot.child("desc").getValue();
                String event_image = (String) dataSnapshot.child("image").getValue();

                final String post_key= (String) dataSnapshot.getKey();

               // final String post_key= mDatabase.child("title").push().getKey();

                //added
                //final String post_key = (String) dataSnapshot.getChildren().iterator().next().getKey();

                //String uid = mDatabase.child("Events").push().getKey();
                //Log.i("uid", uid);



                mEventSingleTitle.setText(event_title);
                mEventSingleDesc.setText(event_desc);

                Picasso.with(second.this).load(event_image).into(mEventSingleImage);

                    button_showdb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view2) {

                           // Toast.makeText(second.this, post_key, Toast.LENGTH_SHORT).show();
                            Intent intent2 = new Intent(second.this, display_data.class);
                            intent2.putExtra("event_id", post_key);
                            startActivity(intent2);
                        }
                    });




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //radio_g = (RadioGroup) findViewById(R.id.radio_group);
        // int selected_id = radio_g.getCheckedRadioButtonId();
        //radio_b = (RadioButton) findViewById(selected_id);




        /*button_showdb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                Toast.makeText(second.this, event_title, Toast.LENGTH_SHORT).show();
                //Intent intent2 = new Intent(second.this, display_data.this);
                //intent2.putExtra("event_id", post_key);
                //startActivity(intent2);
            }
        });
        */



        // AddData();
        //showdb();



    }



    /*public void AddData() {
        button_sbm.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (attempt_counter != 0) {
                            int selected_id = radio_g.getCheckedRadioButtonId();
                            radio_b = (RadioButton) findViewById(selected_id);
                            String value = name.getText().toString().trim();
                            String status = radio_b.getText().toString();
                            Toast.makeText(second.this, value + " is " + status, Toast.LENGTH_SHORT).show();
                            if (status.equals("Going")) {
                                mDatabase.push().setValue(value).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(second.this, "Data inserted", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(second.this, "Error", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(second.this, "not inserted", Toast.LENGTH_SHORT).show();
                            }
                            attempt_counter--;
                        } else {
                            button_sbm.setEnabled(false);
                            Toast.makeText(second.this, "you can only poll once", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }*/








}

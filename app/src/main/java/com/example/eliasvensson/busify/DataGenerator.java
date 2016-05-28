/**
 * @author Sara Kinell and Annie Söderström
 * @version 1.0, 2016-05-27
 * @since 1.0
 *
 * Class to query data from firebase database.
 * If changes are made to firebase log messages will be sent
 */

package com.example.eliasvensson.busify;

import android.util.Log;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DataGenerator {

    private DatabaseReference ref;
    private FirebaseDatabase database;

    public DataGenerator(){
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
    }

    // Method to test Firebase
    public void getBusValues(String date) {
        ref.orderByChild("2016-05-18");

        // Add value event listener to the database reference
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("Changed bus type to ", dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Cancelled ", databaseError.getMessage(), databaseError.toException());
            }
        });

        // Add child event listener to the database reference
        ref.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("Child added ", dataSnapshot.getKey().toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("Child changed ", dataSnapshot.getKey().toString());

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("Child removed ", dataSnapshot.getKey().toString());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d("Child moved ", dataSnapshot.getKey().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Cancelled ", databaseError.getMessage(), databaseError.toException());
            }
        });


    }

   }





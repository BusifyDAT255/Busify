/**
 * @author Sara Kinell and Annie Söderström
 * @version 1.0, 2016-05-27
 * @since 1.0
 *
 *
 */

package com.example.eliasvensson.busify;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


/**
 *
 */
public class DataGenerator {

    // Method to test Firebase
    public void testFirebase() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        HashMap<String, String> busMap = new HashMap<>();
        ref.child("Bus").child("100020").child("Bus type").setValue("Fake bus type");


        // Add value event listener to the database reference
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot busSnapshot: dataSnapshot.getChildren()) {
                    String busType = (String) busSnapshot.child("Bus type").getValue();
                    //String date = (String) busSnapshot.child("Date").getValue();
                    Log.e("Changed bus type to: ", busType);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Add child event listener to the database reference
        ref.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

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
        });


    }





   }





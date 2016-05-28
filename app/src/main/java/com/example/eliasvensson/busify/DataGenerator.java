/**
 * @author Sara Kinell and Annie Söderström
 * @version 1.0, 2016-05-27
 * @since 1.0
 *
 *
 */

package com.example.eliasvensson.busify;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


/**
 *
 */
public class DataGenerator {

    private DatabaseReference ref;

    // Method to test Firebase
    public void testFirebase() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        HashMap<String, String> busMap = new HashMap<>();
        ref.orderByChild("2016-05-18");

        // Add value event listener to the database reference
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String busType ="";
                for (DataSnapshot busSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot busDetails : busSnapshot.getChildren()) {
                        busType = busType + busDetails.getValue().toString() + "\n";
                    }
                }
                Log.e("Changed bus type to ", busType);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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





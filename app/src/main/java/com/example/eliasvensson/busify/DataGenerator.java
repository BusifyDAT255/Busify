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
    private FirebaseDatabase database;
    private String chosenDate;

    public DataGenerator(){
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
    }

    // Gets bus values for specified date
    public void getBusValues(String date) {
        this.chosenDate = date;
        // Add value event listener to the database reference
        ref.child(date).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("Bus values for " + chosenDate, dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Cancelled ", databaseError.getMessage(), databaseError.toException());
            }
        });



    }

   }





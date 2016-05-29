/**
 * @author Sara Kinell
 * @author Annie Söderström
 * @version 2.0, 2016-05-28
 * @since 1.0, 2016-05-27
 *
 * Information for buses from Firebase.
 * Error message will be shown if the ValueEventListener fails to
 * access the server or is removed because of Firebase settings.
 *
 */

package com.example.eliasvensson.busify;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DataGenerator {

    /**
     * Defines variables handling the reference to the Firebase database
     * and the String containing the date for which bus information is to be shown.
     */
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private String chosenDate;
    private final Activity mainActivity;
    private String busdata = "";

    /**
     * Constructor for the DataGenerator class.
     */
    public DataGenerator(Activity act) {
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        this.mainActivity = act;
    }

    /**
     * Gets bus information (bus-ID, driving distance (km), electric energy
     * consumption (kWh) and bus type) for specified date.
     *
     * @param date the date to get information for
     * @return the bus info for the specified date, as a String
     */
    public String getBusInformation(String date) {
        this.chosenDate = date;

        // Adds mainActivity value event listener to the database reference
        ref.child(chosenDate).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Creates mainActivity csv file with mainActivity snapshot of the data for the chosen date
                busdata = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Displays an error message if the listener fails or is removed
                Toast.makeText(mainActivity, "Can´t generate data", Toast.LENGTH_SHORT).show();
                throw new InternalError(databaseError.getMessage());
            }
        });

        busFields(busdata);
        return busdata;

    }

    public String[][] busFields(String data) {
        String[][] csvFormat = new String[11][4];
        data = data.replace("{", ",").replace("}", ",").replace("=", ",");
        String[] divided = data.split(",");
        for (int i = 0; i < divided.length; i++) {
            Log.e("Splitted data:", divided[i]);
        }

        return csvFormat;

    }

}


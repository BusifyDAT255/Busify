/**
 * @author Sara Kinell
 * @author Annie Söderström
 * @version 3.0, 2016-05-29
 * @since 1.0, 2016-05-27
 * <p/>
 * Information for buses from Firebase is combined with calculated values.
 * Error message will be shown if the ValueEventListener fails to
 * access the server or is removed because of Firebase settings.
 * <p/>
 * TODO: The method busFields is under construction
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
    private String[][] csvFormat;


    /**
     * Constructor for the DataGenerator class.
     */
    public DataGenerator(Activity act, int row, int col) {
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        mainActivity = act;
        csvFormat = new String[row][col];
    }

    /**
     * Gets bus information (bus-ID, driving distance (km), electric energy
     * consumption (kWh) and bus type) for specified date.
     *
     * @param date the date to get information for
     * @return the bus info for the specified date, as a String
     */
    public String[][] getBusInformation(String date) {
        this.chosenDate = date;

        // Adds mainActivity value event listener to the database reference
        ref.child(chosenDate).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Gets a snapshot of the data in the database as a String
                busdata = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Displays an error message if the listener fails or is removed
                Toast.makeText(mainActivity, "Cannot generate data", Toast.LENGTH_SHORT).show();
                throw new InternalError(databaseError.getMessage());
            }
        });

        return busFields(busdata);

    }

    /**
     *
     * @param data
     * @return
     */
    public String[][] busFields(String data) {
        data = data.replace("{", ",").replace("}", ",").replace("=", ",").replace(", ", ",").replace("Driving distance (km)", "").replace("Electric energy consumption (kWh)", "").replace("Bus type", "");
        String[] splittedBusInfo = data.split(",");
        splittedBusInfo = fixIndex(splittedBusInfo, 40);
        addTitles(0);
        int index = 0;
        for (int j = 1; j < csvFormat.length; j++) {
            for (int k = 0; k < csvFormat[j].length; k++) {
                if (splittedBusInfo[index] != null) {
                    csvFormat[j][k] = splittedBusInfo[index];
                    Log.e("Number ", "" + index + " csvFormat [" + j + "]" + "[" + k + "] " + csvFormat[j][k]);
                    index++;
                }

            }
        }
        return csvFormat;
    }

    /**
     * Adds titles to the first row
     * @param firstRow the first row number
     */
    private void addTitles(int firstRow) {
        csvFormat[firstRow][0] = "Bus ID";
        csvFormat[firstRow][1] = "Driving distance (km)";
        csvFormat[firstRow][2] = "Electric energy consumption (kWh)";
        csvFormat[firstRow][3] = "Bus type";
        //csvFormat[firstRow][4] = "Electricity per km (kWh/km)";
    }

    /**
     * Fixes the indices of a split array
     * @param splitted field with empty content
     * @param size the size of the new field
     * @return field of strings with non-empty content
     */
    private String[] fixIndex(String[] splitted, int size) {
        String[] trimmed = new String[size];
        int index = 0;
        for (int i = 0; i < splitted.length; i++) {
            if (!splitted[i].isEmpty()) {
                trimmed[index] = splitted[i];
                Log.e("right [" + index + "] " + trimmed[index], " wrong [" + i + "] " + splitted[i]);
                index++;
            }
        }
        return trimmed;
    }

}


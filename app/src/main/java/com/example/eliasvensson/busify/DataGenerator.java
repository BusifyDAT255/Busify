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

import java.util.ArrayList;


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

    // Could make csvFormat an instance variable
    public String[][] busFields(String data) {
        csvFormat = new String[11][4]; // Should be [11][5]
        data = data.replace("{", ",").replace("}", ",").replace("=", ",").replace(", ", ",").replace("Driving distance (km)", "").replace("Electric energy consumption (kWh)", "").replace("Bus type", "");
        String[] splitBusInfo = data.split(",");
        ArrayList<String> busValues = trimArray(splitBusInfo);
        addTitles(0, csvFormat);
        Log.e("busValues position 0 ", busValues.get(0));

        int dataNumber = 0;
        for (int j = 1; j < csvFormat.length; j++) {
            //Log.e("j", "" + j);
            for (int k = 0; k < csvFormat[j].length; k++) {
                //Log.e("k", "" + k);
                //csvFormat[j][k] = busValues.get(dataNumber);
                Log.e("Number ", "" + dataNumber + " csvFormat [" + j + "]" + "[" + k + "] " + csvFormat[j][k]);
                dataNumber++;
            }
        }
        return csvFormat;
    }

    private void addTitles(int firstRow, String[][] csvFormat) {
        csvFormat[firstRow][0] = "Bus ID";
        csvFormat[firstRow][1] = "Driving distance (km)";
        csvFormat[firstRow][2] = "Electric energy consumption (kWh)";
        csvFormat[firstRow][3] = "Bus type";
        //csvFormat[firstRow][4] = "Electricity per km (kWh/km)";
    }


    private ArrayList<String> trimArray(String[] splitted) {
        ArrayList<String> trimmed = new ArrayList<>();
        for (int i = 0; i < splitted.length; i++) {
            if (!splitted[i].isEmpty()) {
                trimmed.add(splitted[i]);
                Log.e("Trimmed " + i, " " + trimmed.get(i));
            }
        }
        return trimmed;
    }

}


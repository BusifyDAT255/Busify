/**
 * @author Sara Kinell
 * @author Annie Söderström
 * @version 4.0, 2016-05-30
 * @since 1.0, 2016-05-27
 *
 * Information for buses from Firebase is combined with calculated values.
 * Error message will be shown if the ValueEventListener fails to
 * access the server or is removed because of Firebase settings.
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
    private Activity mainActivity;
    private String busdata = "";
    private String[][] csvFormat;


    /**
     * Constructor for the DataGenerator class.
     *
     * @param act the MainActivity view
     * @param row number of rows in the final .csv-file
     * @param col number of columns in the final .csv-file
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
     * Creates content for .csv-file
     *
     * @param data bus information from Firebase
     * @return a two dimensional field of bus data
     */
    public String[][] busFields(String data) {
        //Replaces tokens with comma for easier splitting of the String
        data = data.replace("{", ",").replace("}", ",").replace("=", ",").replace(", ", ",").replace("Driving distance (km)", "").replace("Electric energy consumption (kWh)", "").replace("Bus type", "");
        //Splits String into a field
        String[] splittedBusInfo = data.split(",");
        //Fixes correct indices and add titles to csvFormate
        splittedBusInfo = fixIndex(splittedBusInfo, (csvFormat.length - 1) * (csvFormat[0].length));
        addTitles(0);

        int index = 0;
        for (int j = 1; j < csvFormat.length; j++) {
            for (int k = 0; k < csvFormat[j].length; k++) {
                //Fills a two dimensional field with values from a one dimensional field representing Firebase data
                if (splittedBusInfo[index] != null) {
                    csvFormat[j][k] = splittedBusInfo[index];
                    index++;
                }
            }
        }
        return csvFormat;
    }

    /**
     * Adds titles to the first row
     *
     * @param firstRow the first row number
     */
    private void addTitles(int firstRow) {
        csvFormat[firstRow][0] = "Bus ID";
        csvFormat[firstRow][1] = "Driving distance (km)";
        csvFormat[firstRow][2] = "Electric energy consumption (kWh)";
        csvFormat[firstRow][3] = "Bus type";
    }

    /**
     * Fixes the indices of a split array
     *
     * @param splitted field with empty content
     * @param size     the size of the new field
     * @return field of strings with non-empty content
     */
    private String[] fixIndex(String[] splitted, int size) {
        String[] trimmed = new String[size];
        int index = 0;
        for (int i = 0; i < splitted.length; i++) {
            if (!splitted[i].isEmpty()) {
                trimmed[index] = splitted[i];
                index++;
            }
        }
        return trimmed;
    }


}


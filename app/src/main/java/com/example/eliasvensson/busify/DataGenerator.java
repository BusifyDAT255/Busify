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

    public String[][] busFields(String data) {
        String[][] csvFormat = new String[11][4]; // Should be [11][5]
        data = data.replace("{", ",").replace("}", ",").replace("=", ",").replace(", ", ",").replace("Driving distance (km)", "").replace("Electric energy consumption (kWh)", "").replace("Bus type", "");
        String[] divided = data.split(",");
        divided = fixIndex(divided, 40);

        int dataNumber = 0;
        for (int j = 0; j < csvFormat.length; j++) {
            //Log.e("j", "" + j);
            for (int k = 0; k < csvFormat[j].length; k++) {
                //Log.e("k", "" + k);
                if (j == 0) {
                    addTitles(j, csvFormat);
                } else {
                        if (divided[dataNumber] != null) {
                            csvFormat[j][k] = divided[dataNumber];
                            Log.e("Number ", "" + dataNumber + " csvFormat [" + j + "]" + "[" + k + "] " + csvFormat[j][k]);
                        }
                    dataNumber++;

                }
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


    private String[] fixIndex(String[] wrong, int size) {
        String[] right = new String[size];
        int index = 0;
        for (int i = 0; i < wrong.length; i++) {
            if (!wrong[i].isEmpty()) {
                right[index] = wrong[i];
                Log.e("right [" + index + "] " +  right[index], " wrong [" + i + "] " +  wrong[i]);
                index++;
            }
        }
        return right;
    }

}


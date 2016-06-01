/**
 * @author Sara Kinell
 * @author Annie Söderström
 * @version 6.0, 2016-06-01
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

import java.math.BigDecimal;
import java.math.MathContext;


public class DataGenerator {
    /**
     * Defines variables handling the reference to the Firebase database,
     * the String containing the date for which bus information is to be shown,
     * a MainActivity reference and the csvFormat to be used when creating a .csv-file.
     * The busdata variable is used to contain data from Firebase.
     */
    private FirebaseDatabase database;
    protected DatabaseReference ref;
    private String chosenDate;
    private Activity mainActivity;
    private String busdata = "";
    private String[][] csvFormat;

    /**
     * Constructor for the DataGenerator class.
     *
     * @param act MainActivity view
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

        //Adds mainActivity value event listener to the database reference
        ref.child(chosenDate).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Gets a snapshot of the data in Firebase for chosenDate
                busdata = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Displays an error message if the listener fails or is removed
                Toast.makeText(mainActivity, "Cannot generate data", Toast.LENGTH_SHORT).show();
                throw new InternalError(databaseError.getMessage());
            }
        });

        return busFields(busdata);
    }

    /**
     * Creates content for a .csv-file.
     *
     * @param data bus information from Firebase
     * @return two dimensional field of bus data
     */
    public String[][] busFields(String data) {
        //Replaces tokens with comma for easier splitting of the String
        data = data.replace("{", ",").replace("}", ",").replace("=", ",").replace(", ", ",")
                .replace("Driving distance (km)", "").replace("Electric energy consumption (kWh)", "")
                .replace("Bus type", "");
        //Splits String into a field
        String[] splittedBusInfo = data.split(",");
        //Fixes correct indices and add titles to csvFormat
        splittedBusInfo = fixIndex(splittedBusInfo, (csvFormat.length - 1) *
                (csvFormat[0].length - 1));
        addTitles(0);

        //Fills a two dimensional field with values from a one dimensional field representing Firebase data
        int index = 0;
        for (int j = 1; j < csvFormat.length; j++) {
            for (int k = 0; k < csvFormat[j].length; k++) {
                // If on column 5: electricity/km
                if (k == 4) {
                    //Adds a fifth column with calculated electricity per km
                    if (csvFormat[j][2] != null && csvFormat[j][1] != null) {
                        csvFormat[j][k] = stringDivision(csvFormat[j][2], csvFormat[j][1]);
                    }
                } else if (splittedBusInfo[index] != null) {
                    //Adds values from the database into csvFormat
                    csvFormat[j][k] = splittedBusInfo[index];

                    // Increments index for the one-dimensional array, iff values for column
                    // 1-4 was changed
                    index++;
                }
            }
        }
        return csvFormat;
    }

    /**
     * Fixes the indices in a split array.
     *
     * @param splitted field with empty content
     * @param size the size of the new field
     * @return field of strings with non-empty content
     */
    private String[] fixIndex(String[] splitted, int size) {
        String[] trimmed = new String[size];
        int index = 0;
        //Adds values from the split array to the trimmed array and ignores empty Strings
        for (int i = 0; i < splitted.length; i++) {
            if (!splitted[i].isEmpty()) {
                trimmed[index] = splitted[i];
                index++;
            }
        }
        return trimmed;
    }

    /**
     * Adds titles to the first row, which represents the first row of the .csv-file.
     *
     * @param firstRow the first row number
     */
    private void addTitles(int firstRow) {
        csvFormat[firstRow][0] = "Bus ID";
        csvFormat[firstRow][1] = "Driving distance (km)";
        csvFormat[firstRow][2] = "Electric energy consumption (kWh)";
        csvFormat[firstRow][3] = "Bus type";
        csvFormat[firstRow][4] = "Electricity per km (kWh/km)";
    }

    /**
     * Calculates the fraction of two Strings and rounds the
     * resulting value.
     *
     * @param numerator top number of the fraction
     * @param denominator bottom number of the fraction
     * @return rounded String representation of the fraction
     */
    private String stringDivision(String numerator, String denominator) {
        double fraction = 0.0;
        try {
            //Creates a fraction of the incoming values represented as Strings
            fraction = (Double.parseDouble(numerator)) / (Double.parseDouble(denominator));
        } catch (NumberFormatException e) {
            // If Strings may not be converted to doubles, an empty String is returned
            Log.e("Incorrect numbers", "String values may not be parsed to doubles");
            return "#";
        }
        //Rounds the double to three significant figures
        BigDecimal bd = new BigDecimal(fraction);
        bd = bd.round(new MathContext(3));
        double rounded = bd.doubleValue();

        //Returns the rounded double into the correct field, as a String
        return String.valueOf(rounded);
    }

}

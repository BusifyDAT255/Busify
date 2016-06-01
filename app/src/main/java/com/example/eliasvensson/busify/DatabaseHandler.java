/**
 * @author Elias Svensson
 * @author David Genelöv
 * @author Annie Söderström
 * @author Melinda Fulöp
 * @author Sara Kinell
 * @author Jonathan Fager
 * @version 6.0, 2016-06-01
 * @since 1.0, 2016-05-27
 * <p/>
 * Manages the interaction between MainActivity and Firebase database.
 * This is where the data is queried, and structured in a suitable format for making .csv-files
 * (i.e. a two dimensional array of Strings)
 */

package com.example.eliasvensson.busify;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DatabaseHandler {
    /**
     * Defines variables handling the reference to the Firebase database,
     * the String containing the date for which bus information is to be shown
     * and the csvFormat to be used when creating a .csv-file.
     * The busdata variable is used to contain data from Firebase.
     */
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String busdata = "";

    /**
     * Constructor for the DatabaseHandler class.
     */
    public DatabaseHandler() {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
    }

    protected void initiateDatabase(final String reportDate, final CsvHandler csvHandler){
        // Creates a thread to handle time delay in database access
        Thread databaseTimer = new Thread() {
            public void run() {
                try {
                    // Makes a call to the database to get access
                    getBusInformation(reportDate, csvHandler);
                    // Waits to get access to the database
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        // Starts the timer
        databaseTimer.start();
    }

    /**
     * Gets bus information (bus-ID, driving distance (km), electric energy
     * consumption (kWh) and bus type) for specified date.
     *
     * @param date the date to get information for
     * @return the bus info for the specified date, as a String
     */
    public String[][] getBusInformation(String date, CsvHandler csvHandler) {

        //Adds activity value event listener to the database reference
        databaseReference.child(date).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Gets a snapshot of the data in Firebase for chosenDate
                busdata = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Shuts down the app if access to database is denied
                throw new InternalError(databaseError.getMessage());
            }
        });

        return csvHandler.queryTo2DArray(busdata).clone();
    }
}

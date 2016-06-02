/**
 * @author Elias Svensson
 * @author David Genelöv
 * @author Annie Söderström
 * @author Melinda Fulöp
 * @author Sara Kinell
 * @author Jonathan Fager
 * @version 2.0, 2016-05-31
 * @since 1.0
 * <p/>
 * Manages all handling of .csv-files needed for the Busify app,
 * i.e. converting a String from a Firebase data query to a two dimensional array.
 * When the array is created, labels are added and necessary calculations are done.
 * Also provides methods for creating a .csv-file from the two dimensional array.
 *
 */

package com.example.eliasvensson.busify;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.charset.Charset;

public class CsvHandler {

    // Initiates the mainActivity this class will talk with
    private Activity activity;

    // Initiates the array for storing values from database query
    private String[][] dataToCsv;

    //Creates a FileOutputStream for writing the file to internal storage
    FileOutputStream outputStream;
    
    public CsvHandler(Activity activity, int row, int col) {
        this.activity = activity;
        dataToCsv = new String[row][col];
    }

    /**
     * Creates content for a .csv-file.
     *
     * @param data bus information from Firebase
     * @return two dimensional field of bus data
     */
    public String[][] queryTo2DArray(String data) {
        // Replaces tokens with comma for easier splitting of the String
        data = data.replace("{", ",").replace("}", ",").replace("=", ",").replace(", ", ",")
                .replace("Driving distance (km)", "").replace("Electric energy consumption (kWh)", "")
                .replace("Bus type", "");

        // Splits String into a field
        String[] splittedBusInfo = data.split(",");

        // Fixes correct indices and add titles to csvFormat
        splittedBusInfo = fixIndex(splittedBusInfo, (dataToCsv.length - 1) *
                (dataToCsv[0].length - 1));
        addTitles(0);

        // Fills a two dimensional field with values from a one dimensional field representing Firebase data
        int index = 0;
        for (int j = 1; j < dataToCsv.length; j++) {
            for (int k = 0; k < dataToCsv[j].length; k++) {
                // If on column 5: electricity/km
                if (k == 4) {
                    // Adds a fifth column with calculated electricity per km
                    if (dataToCsv[j][2] != null && dataToCsv[j][1] != null) {
                        if (dataToCsv[j][1] != "0"){
                            dataToCsv[j][k] = stringDivision(dataToCsv[j][2], dataToCsv[j][1]);
                        }
                        else{
                            dataToCsv[j][k] = "-";
                        }
                    }
                //Adds values from the database into csvFormat
                } else if (splittedBusInfo[index] != null) {
                    dataToCsv[j][k] = splittedBusInfo[index];

                    // Increments index for the one-dimensional array, if values for column
                    // 1-4 was changed
                    index++;
                }
            }
        }
        // Returns a clone, for safety purposes
        return dataToCsv.clone();
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

        // Adds values from the split array to the trimmed array and ignores empty Strings
        for (int i = 0; i < splitted.length; i++) {
            if (!splitted[i].isEmpty()) {
                trimmed[index] = splitted[i];
                index++;
            }
        }
        return trimmed;
    }

    /**
     * Adds titles to the first row, which represents the first row of the .csv-file
     * @param firstRow the first row number
     */
    private void addTitles(int firstRow) {
        dataToCsv[firstRow][0] = "Bus ID";
        dataToCsv[firstRow][1] = "Driving distance (km)";
        dataToCsv[firstRow][2] = "Electric energy consumption (kWh)";
        dataToCsv[firstRow][3] = "Bus type";
        dataToCsv[firstRow][4] = "Electricity per km (kWh/km)";
    }

    /**
     * Calculates the fraction of two Strings and rounds the resulting value.
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
            // If Strings may not be converted to doubles, a hashtag is returned
            return "#NAN#";
        }
        //Rounds the double to three significant figures
        BigDecimal bd = new BigDecimal(fraction);
        bd = bd.round(new MathContext(3));
        double rounded = bd.doubleValue();

        //Returns the rounded double into the correct field, as a String
        return String.valueOf(rounded);
    }

    /**
     * Method that writes a two-dimensional array with string fields,
     * to a string on .csv-format with a specified date as the filename.
     * The file gets stored on the internal memory on the phone.
     * @param dataArray array to write to a .csv
     * @param reportDate  specified date that gets passed to the filename
     */
    public void writeFileFromArray(String reportDate, String[][] dataArray) {
        String filename = reportDate + ".csv";

        //Creates the String which will make up the text for the .csv
        StringBuffer csvBuffer = new StringBuffer();

        // Parses 2D array to string in .csv format
        for (int i = 0; i < dataArray.length; i++) {
            for (int j = 0; j < dataArray[0].length; j++) {
                // Adds semi-colon to indicate new column in .csv file
                csvBuffer.append(dataArray[i][j] + ";");
            }
            // Adds new line to indicate new row in .csv file 
            csvBuffer.append("\n");
        }

        // Creates String from buffer 
        String csvText = csvBuffer.toString();

        try {
            /* Opens a FileOutputStream to a file with the specified filename
               Creates file if it doesn't exist. */
            outputStream = activity.openFileOutput(filename, Context.MODE_PRIVATE);
            /* Writes the string to the specified file.
               Uses UTF-8 encoding because it is always supported and can encode any character. */
            outputStream.write(csvText.getBytes(Charset.forName("UTF-8")));

            //Closes the FileOutputStream to produce a file
            outputStream.close();

        } catch (FileNotFoundException e) {
            Toast.makeText(activity, "Internal Error: No such file found", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(activity, "Internal Error: IOException", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method to extract a filePath for a specified date.
     *
     * @param reportDate a String with the date to return a filepath for
     * @return the filepath for the specified date
     */
    public String getFilePath(String reportDate) {
        String filePath = activity.getFilesDir().getAbsolutePath() + "/" + reportDate + ".csv";
        return filePath;
    }


}



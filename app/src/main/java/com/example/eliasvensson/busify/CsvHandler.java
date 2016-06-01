/**
 * @author Elias Svensson
 * @author David Genelöv
 * @author Melinda Fulöp
 * @version 2.0, 2016-05-31
 * @since 1.0
 *
 * Manages all handling of .csv-files needed for the Busify app
 *
 */

package com.example.eliasvensson.busify;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.CancellableTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CsvHandler {

    // Initiates the mainActivity this class will talk with
    private MainActivity mainActivity;

    //Creates a FileOutputStream for writing the file to internal storage
    FileOutputStream outputStream;
    
    public CsvHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
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
        String csvText = "";
        // Parses 2D array to string in .csv format
        for (int i = 0; i < dataArray.length; i++) {
            for (int j = 0; j < dataArray[0].length; j++) {
                csvText += dataArray[i][j] + ";";
            }
            csvText += "\n";
        }

        try {
            //Opens a FileOutputStream to a file with the specified filename
            //Creates file if it doesn't exist.
            outputStream = mainActivity.openFileOutput(filename, Context.MODE_PRIVATE);
            //Writes the string to the specified file
            outputStream.write(csvText.getBytes());
            //Closes the FileOutputStream to produce a file
            outputStream.close();

        } catch (FileNotFoundException e) {
            Toast.makeText(mainActivity, "Internal Error: No such file found", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(mainActivity, "Internal Error: IOException", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method to extract a filePath for a specified date.
     *
     * @param reportDate a String with the date to return a filepath for
     * @return the filepath for the specified date
     */
    public String getFilePath(String reportDate) {
        String filePath = mainActivity.getFilesDir().getAbsolutePath() + "/" + reportDate + ".csv";
        Log.e("LOG", "Output from getFilePath " + filePath);
        return filePath;
    }

    /**
     * Takes filePath as a String, finds the Uri, reserve place at "/reports/date.csv"
     * and builds metadata . Initiates a CancellableTask uploadTask and uses .putFile to upload file
     * and calls sendEmail().
     *
     * TODO: refactor the sendEmail after checking execution order.
     * TODO: Look through comments for this code
     * TODO: Fix what happends if Failure.
     * TODO: Fix the progress in MainActivity to use "onProgress"?
     *
     * @param filePath file path ending with [date].csv
     */
    public void csvUploader(String filePath) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        Uri file = Uri.fromFile(new File(filePath));
        Log.e("csvUploader Uri File:", filePath.toString());

        // Creates the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("text/csv").build();
        Log.e("LOG","Metadata: " + metadata.toString());

        // Uploads file and metadata to the path 'reports/date.csv'
        CancellableTask uploadTask = storageReference.child("reports/" + file.getLastPathSegment()).putFile(file, metadata);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                //double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //mainActivity.setProgress((int) progress);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handles unsuccessful uploads
                Log.e("LOG", "Unsucessfull in CSVUPLOADER");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handles successful uploads on complete
                Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                String downloadLink = downloadUrl.toString();
                Log.e("LOG", "Successfull CSVUPLOADER");
                Log.e("LOG", taskSnapshot.getMetadata().getPath());
                // Sets link to be downloaded and sends an email
                mainActivity.getEmailHandler().sendEmail("Your ElectriCity report for " + mainActivity.getReportDate()
                        , "Please click the link to download report:\n\n" + downloadLink, mainActivity.getProgress());
            }
        });
    }
}



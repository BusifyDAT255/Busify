/**
 * @author Elias Svensson
 * @author David Genelöv
 * @author Melinda Fulöp
 * @version 1.0, 2016-05-30
 * @since 1.0
 *
 * Manages all handling of .csv-files needed for the Busify app

 * Note: The class is under construction. There is still a test-method for reading csv-files
 * and the writeFileFromArray method will need to be adjusted to Sara and Annies last version of
 * the DataGenerators array with database info
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class CsvHandler {

    private MainActivity mainActivity;

    public CsvHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    /**
     * Method that writes a two-dimensional array with string fields,
     * to a string on .csv-format with a specified date as the filename.
     *
     * @param dataArray array to write to a .csv
     * @param callDate  specified date that gets passed to the filename
     */
    public void writeFileFromArray(String callDate, String[][] dataArray) {
        String filename = callDate + ".csv";
        //Creates the String which will make up the text for the .csv
        String csvText = "";
        // Parses 2D array to string in .csv format
        for (int i = 0; i < dataArray.length; i++) {
            for (int j = 0; j < dataArray[0].length; j++) {
                csvText += dataArray[i][j] + ";";
            }
            csvText += "\n";
        }

        //Creates a FileOutputStream for writing the file to internal storage
        FileOutputStream outputStream;
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
     * TESTMETOD
     * TODO: Ta bort innan merge med master. Låt stå till develop
     */
    public void readCsvFile(String callDate) {
        try {
            String Message;
            FileInputStream fileInputStream = mainActivity.openFileInput(callDate + ".csv");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            while ((Message = bufferedReader.readLine()) != null) {
                stringBuffer.append(Message + "\n");
            }
            Toast.makeText(mainActivity, stringBuffer.toString(), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to extract a filePath for a specified date.
     *
     * @param callDate a String with the date to return a filepath for
     * @return the filepath for the specified date
     */
    public String getFilePath(String callDate) {
        String filePath = mainActivity.getFilesDir().getAbsolutePath() + "/" + callDate + ".csv";
        Log.e("LOG", "Output from getFilePath " + filePath);
        return filePath;
    }


    /**
     * Takes filePath as a String, finds the Uri, reserve place at "/reports/date.csv"
     * Build metadata.
     * Initiate a CancellableTask uploadTask and uses .putFile to upload file.
     *
     * Calls sendEmail().
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

        // Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("text/csv").build();
        Log.e("LOG","Metadata: " + metadata.toString());

        // Upload file and metadata to the path 'reports/date.csv'
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
                Log.e("LOG", "Successfull CSVUPLOADER");
                Log.e("LOG", taskSnapshot.getMetadata().getPath());
                // Sets link to be downloaded and sends an email
                mainActivity.setDownloadLink(downloadUrl);
                mainActivity.sendEmail();
            }
        });
    }
}



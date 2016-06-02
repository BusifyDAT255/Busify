/**
 * @author Elias Svensson
 * @author David Genelöv
 * @author Annie Söderström
 * @author Melinda Fulöp
 * @author Sara Kinell
 * @author Jonathan Fager
 * @version 1.0, 2016-06-01
 * @since 1.0
 *
 * Provides the methods the app need for all contact with Firebase storage,
 * i.e uploading files and getting their URL-references
 */

package com.example.eliasvensson.busify;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.CancellableTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class StorageHandler {

    // Variable for saving the reference to Firebase storage
    protected StorageReference storageReference;

    // Variable for saving the date-specific reference to Firebase storage
    protected StorageReference dateStorageReference;

    // Initiates a reference to the Firebase root storage
    public StorageHandler(){
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    /**
     * Takes filePath as a String & reserve place at "/reports/date.csv"
     * and builds metadata . Initiates a CancellableTask uploadTask and uses .putFile to upload file
     * and calls sendEmail().
     *
     * @param filePath file path ending with [date].csv
     */
    public void uploadFile(String filePath) {

        // Gets the URI for specified file from internal storage
        Uri uriToFile = Uri.fromFile(new File(filePath));

        // Creates the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("text/csv").build();

        // Uploads file and metadata to the path 'reports/date.csv'
        CancellableTask<UploadTask.TaskSnapshot> uploadTask = storageReference.child("reports/" + uriToFile.getLastPathSegment()).putFile(uriToFile, metadata);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Shuts down app if upload is unsuccessful
                throw new InternalError(exception.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {}
        });
    }

    /**
     * Sets the DateStorageReference to the specified path.
     * @param path: The path to Firebase storage for a specific date
     */
    public void setDateStorageReference(String path){
        dateStorageReference = storageReference.child(path);
    }

    /**
     * Gets the current DateStorageReference
     * @return the DateStorageReference for the current date
     */
    public StorageReference getDateStorageReference(){
        return dateStorageReference;
    }
}

package com.example.eliasvensson.busify;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.CancellableTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class StorageHandler {

    protected StorageReference storageReference;
    protected StorageReference dateStorageReference;

    public StorageHandler(){
        // Initiates a storage reference to the root reference
        storageReference = FirebaseStorage.getInstance().getReference();
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
    public void uploadFile(String filePath) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        Uri file = Uri.fromFile(new File(filePath));
        Log.e("uploadFile Uri File:", filePath.toString());

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
                //Do nothing(!)
            }
        });
    }

    public void setStorageReference(String path){
        dateStorageReference = storageReference.child(path);
    }

    public StorageReference getStorageReference(){
        return dateStorageReference;
    }
}

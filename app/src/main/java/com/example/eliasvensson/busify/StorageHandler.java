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

/**
 * Created by jonathanfager on 16-05-28.
 */
public class StorageHandler {

    //Declare variables
    StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();


    public void uploadCSV(String date) {

        Uri file = Uri.fromFile(new File("reports/" + date + ".csv"));

// Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("text/csv").build();

// Upload file and metadata to the path 'reports/"date".csv'
        CancellableTask uploadTask = mStorageReference.child("reports/" + file.getLastPathSegment()).putFile(file, metadata);


        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //Use this later to make some nice progress window
                System.out.println("Upload is " + progress + "% done");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e("upload", "Upload error");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                MainActivity.setDownloadLink(downloadUrl);
            }
        });


    }
}

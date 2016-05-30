/**
 * @author Jonathan Fager
 * @version 2.0, 2016-05-30
 * @since 1.0, 2016-05-29
 *
 * Uploads files to Firebase Storage.
 */

package com.example.eliasvensson.busify;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.CancellableTask;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class StorageHandler {


    public void uploadCsv(String date, StorageReference mStorageReference) {

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
                Log.e("Upload is ", progress + "% done");
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

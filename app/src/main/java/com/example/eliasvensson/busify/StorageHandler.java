package com.example.eliasvensson.busify;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StorageHandler {

    protected StorageReference storageReference;
    protected StorageReference dateStorageReference;

    public StorageHandler(){

        // Initiates a storage reference to the root reference
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public void setStorageReference(String path){
        dateStorageReference = storageReference.child(path);
    }

    public StorageReference getStorageReference(){
        return dateStorageReference;
    }
}

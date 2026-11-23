package com.example.zonktacticsalfa1;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FBRef {
    public static FirebaseStorage storage = FirebaseStorage.getInstance();
    public static StorageReference refStorage = storage.getReference();
    public static StorageReference refGallery = refStorage.child("Gallery");
}

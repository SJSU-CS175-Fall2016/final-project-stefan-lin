package com.stefan.jeremy.clubsoda.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stefan.jeremy.clubsoda.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by stefanlin on 8/19/16.
 */

public class ImageManagement {
  /* Components */
  private static final long FIVETWELVE_KB = 1024*512;
  private static ImageManagement mImageManagement;
  private FirebaseAuth           mFirebaseAuth;
  private FirebaseDatabase       mFirebaseDatabase;
  private FirebaseStorage        mFirebaseStorage;
  private StorageReference       mStorageRef;
  private Bitmap                 mBitmap;

  private ImageManagement(){
    mFirebaseAuth     = FirebaseAuth.getInstance();
    mFirebaseDatabase = FirebaseDatabase.getInstance();
    mFirebaseStorage  = FirebaseStorage.getInstance();
    mStorageRef       = mFirebaseStorage.getReference();
    mStorageRef       = mStorageRef.child("profile-pics");
  } // END CONSTRUCTOR

  public static ImageManagement getInstance(){
    if(mImageManagement == null){
      mImageManagement = new ImageManagement();
    }
    return mImageManagement;
  } // END METHOD getInstatnce

  public void storeImage(WeakReference<Activity> activityRef,
                         String uid, byte[] imgBytes) throws Exception {
    /* SAVE TO INTERNAL STORAGE */
    FileOutputStream fileOutputStream = activityRef.get().openFileOutput(
      uid, Context.MODE_PRIVATE
    );
    fileOutputStream.write(imgBytes);
    fileOutputStream.close();
    /* SAVE TO INTERNAL STORAGE END */
  } // END METHOD storeImage

  public void populateCircularImageView(final WeakReference<Activity> activityRef,
                                        String uid, final CircularImageView circularImageView)
                           throws FileNotFoundException, IOException {
    final String usrId = uid;
    byte[] imgByteArray = null;
    File checkFile = activityRef.get().getBaseContext().getFileStreamPath(uid);
    if(!checkFile.exists()){
      /* CURRENT USER DOESN'T HAVE THE REQUESTING IMAGE IN INTERNAL STORAGE */
      /* TODO: Request avatar photo from Firebase */
      mBitmap = BitmapFactory.decodeResource(
        activityRef.get().getResources(),
        R.drawable.ic_face_grey_500_48dp
      );
      StorageReference islandRef = mStorageRef.child(uid).child("profile.jpg");
      islandRef.getBytes(FIVETWELVE_KB).addOnSuccessListener(
          new OnSuccessListener<byte[]>() {
        @Override
        public void onSuccess(byte[] bytes) {
          mBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
          try {
            storeImage(activityRef, usrId, bytes);
          }
          catch (Exception e){
            Log.e("storageImage()", "Exception catched");
            e.printStackTrace();
          }
        } // END METHOD onSuccess
      }); // END METHOD CALL addOnSuccessListener
      islandRef.getBytes(FIVETWELVE_KB).addOnCompleteListener(new OnCompleteListener<byte[]>() {
        @Override
        public void onComplete(@NonNull Task<byte[]> task) {
          activityRef.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
              circularImageView.setImageBitmap(mBitmap);
            }
          });
          //circularImageView.setImageBitmap(mBitmap);
        } // END METHOD onComplete
      }); // END METHOD CALL addOnCompleteListener
    }
    else{ /* Image exists in internal storage */
      FileInputStream fileInputStream = activityRef.get().openFileInput(uid);
      imgByteArray = new byte[fileInputStream.available()];
      while(fileInputStream.read(imgByteArray) != -1){ /* Empty */ }
      circularImageView.setImageBitmap(
        BitmapFactory.decodeByteArray(imgByteArray, 0, imgByteArray.length)
      );
    }
  } // END hasImage

}

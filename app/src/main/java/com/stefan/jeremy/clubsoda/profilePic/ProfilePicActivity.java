package com.stefan.jeremy.clubsoda.profilePic;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stefan.jeremy.clubsoda.App;
import com.stefan.jeremy.clubsoda.R;
import com.stefan.jeremy.clubsoda.data.User;
import com.stefan.jeremy.clubsoda.utils.Constants;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilePicActivity extends AppCompatActivity {
  static final int REQUEST_IMAGE_CAPTURE = 1;
  Activity appAct;
  private FirebaseAuth mAuth;
  private FirebaseStorage mStorage;
  private DatabaseReference mDatabase;
  private StorageReference mStorageRef;
  private Button mUploadButton;
  private final int SELECT_PHOTO = 1;
  private User mCurrUser;
  private CircleImageView mImageView;
  private TextView mNameView;
  private TextView mEmailView;
  private Button mBackButton;
  private TextView mTitle;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile_pic);
    appAct = this;
    /* Firebase */
    mStorage = FirebaseStorage.getInstance();
    mDatabase = FirebaseDatabase.getInstance().getReference();
    mAuth = FirebaseAuth.getInstance();

    mStorageRef = mStorage.getReference();
    mStorageRef = mStorageRef.child("profile-pics");
    /* Get user data */
    mCurrUser = (User) getIntent().getExtras().getSerializable("user");

    /* Instantiate Components */
    mImageView = (CircleImageView) findViewById(R.id.profile_avatar);
    mEmailView = (TextView) findViewById(R.id.profile_emailtext);
    mNameView = (TextView) findViewById(R.id.profile_nametext);
    mBackButton = (Button) findViewById(R.id.post_button);
    mBackButton.setText(Constants.BACK_BUTTON);
    mBackButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });
    mTitle = (TextView) findViewById(R.id.title_bar_text);
    mTitle.setText(Constants.USER_PROFILE_TITLE);

    /* Set data */
    mEmailView.setText(mCurrUser.email);
    String fullName = mCurrUser.firstName + " " + mCurrUser.lastName;
    mNameView.setText(fullName);

    /* Set listeners */
    mUploadButton = (Button) findViewById(R.id.profile_newpic_button);
    mUploadButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        //Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        //photoPickerIntent.setType("image/*");
        //startActivityForResult(photoPickerIntent, SELECT_PHOTO);

        ActivityCompat.requestPermissions(appAct,
                new String[]{Manifest.permission.CAMERA},
                1);
      }
    });

    /* Get profile pic */
    getProfilePic();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[], int[] grantResults) {
    switch (requestCode) {
      case 1: {

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
          if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
          }
        } else {
          // permission denied, boo! Disable the
          // functionality that depends on this permission.
          Toast.makeText(appAct, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
        }
        return;
      }
      // other 'case' lines to check for other
      // permissions this app might request
    }
  }

  private void getProfilePic() {
    final String uid = mAuth.getCurrentUser().getUid();
    Bitmap storedImg = getStoredImage(uid);
    if (storedImg == null) {
      StorageReference islandRef = mStorageRef.child(uid).child("profile.jpg");
      final long FIVETWELVE_KB = 1024 * 512;
      islandRef.getBytes(FIVETWELVE_KB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
        @Override
        public void onSuccess(byte[] bytes) {
          Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
          mImageView.setImageBitmap(bmp);
          //bitmapLruCache.put(uid, bmp);
          storeImage(bytes); // Internal storage
        }
        // On fail, do nothing (keep default profile pic)
      });
    } else {
      mImageView.setImageBitmap(storedImg);
    }
  }

  private void storeImage(byte[] imgBytes) {
    String uid = mAuth.getCurrentUser().getUid();
    FileOutputStream outputStream;
    try {
      outputStream = openFileOutput(uid, Context.MODE_PRIVATE);
      outputStream.write(imgBytes);
      outputStream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  private Bitmap getStoredImage(String uid) {
    byte[] imgByteArray = null;
    File checkFile = getBaseContext().getFileStreamPath(uid);
    if (!checkFile.exists()) {
      return null;
    }
    try {
      FileInputStream inputStream = openFileInput(uid);
      imgByteArray = new byte[inputStream.available()];
      while (inputStream.read(imgByteArray) != -1) { }

    } catch (FileNotFoundException e) {
      Log.e("EXCEPTION STORED IMG", "NOT FOUND?");
    } catch (IOException e) {
      Log.e("IOEXCEPTION STORED IMG", "IOE");
    }
    Bitmap bmp = BitmapFactory.decodeByteArray(imgByteArray, 0, imgByteArray.length);
    return bmp;
  }

  /* Upload image */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
    super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
    if (resultCode == RESULT_OK) {
      Bundle extras = imageReturnedIntent.getExtras();
      Bitmap selectedImage = (Bitmap) extras.get("data");
      uploadToDatabase(selectedImage);
      /*try {
        final Uri imageUri = imageReturnedIntent.getData();
        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
        uploadToDatabase(selectedImage);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } */
    }
  }
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    return true;
  }

  /* This is pretty slow! Any alternatives?
     Maybe make a progress dialog while it's working. */
  private byte[] compressBitmap(Bitmap bmp) {
    int max_size = 1024 * 500; // 512 kb
    int streamLength = max_size;
    int compressQuality = 100;
    ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
    int length = 0;
    while (streamLength >= max_size && compressQuality > 20) {
      try {
        bmpStream.flush();
        bmpStream.reset();
      } catch (IOException e) {
        e.printStackTrace();
      }
      compressQuality -= 20;
      bmp.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
      byte[] bmpPicByteArray = bmpStream.toByteArray();
      streamLength = bmpPicByteArray.length;
    }
    return bmpStream.toByteArray();
  }

  private void uploadToDatabase(Bitmap selectedImage) {
    selectedImage = bitmapResize(selectedImage);
    byte[] imgBytes = compressBitmap(selectedImage);
    Bitmap bmp = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
    Log.e("BMP SIZE", "BMP SIZE IS " + bmp.getByteCount());
    mImageView.setImageBitmap(bmp);
    storeImage(imgBytes);
    String uid = mAuth.getCurrentUser().getUid();
    /* Push to Cache */
    App.imageLoader.putBitmap(uid, selectedImage);
    StorageReference userRef = mStorageRef.child(uid).child("profile.jpg");
    UploadTask uploadTask = userRef.putBytes(imgBytes);
  }

  private void pushProfilePicPathToDatabase(Uri downloadUrl) {
    String uid = mAuth.getCurrentUser().getUid();
  }

  private Bitmap bitmapResize(Bitmap bmp) {
    int width = bmp.getWidth();
    int height = bmp.getHeight();
    float scaleWidth = ((float) Constants.PROFILE_PIC_WIDTH) / width;
    float scaleHeight = ((float) Constants.PROFILE_PIC_HEIGHT) / height;
    /*Matrix matrix = new Matrix();
    matrix.postScale(scaleWidth, scaleHeight);
    Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, false);*/
    float aspectRatio = bmp.getWidth() / (float) bmp.getHeight();
    Bitmap resizedBitmap = Bitmap.createScaledBitmap(bmp, 50, Math.round(50 / aspectRatio), false);
    return resizedBitmap;
  }

}

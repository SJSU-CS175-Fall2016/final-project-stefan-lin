package com.stefan.jeremy.clubsoda.utils;

import android.app.ActivityManager;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.LruCache;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stefan.jeremy.clubsoda.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.os.Handler;

/**
 * Created by stefanlin on 8/21/16.
 */

/**
 * Class ImageLoader
 *
 * The responsibilities of ImageLoader class:
 *   (1) contain a cache, inner class, to store loaded Bitmap objects
 *   (2) contain a inner class that distributes the task that is setting bitmap
 *       for each circularImageView by creating corresponding threads
 *   (3) memory management, to trim memory of cache, when system requesting
 *
 * Note: The initializations of any connection to the Firebase would not be
 *       placed in slave-threads. (It would cause errors.)
 */
public class ImageLoader{
  public final String TAG = ImageLoader.class.getSimpleName();
  /* Components */
  private LruBitmapCache mCache;              /* LRU Cache */
  private Context mContext;                   /* Context from App Class */
  private FirebaseStorage firebaseStorage;    /* Firebase Storage Reference */
  private StorageReference storageReference;  /* Storage Reference */

  /**
   * Public Method: ImageLoader
   *
   * Constructor method for ImageLoader class. It would initialize the LRU Cache
   * with 1/8 of the total available memory (RAM).
   *
   * @param context: (Context) Context reference from App class,
   *                 MultidexApplication.
   */
  public ImageLoader(Context context){
    this.mContext = context;
    ActivityManager activityManager = (ActivityManager)mContext
                                        .getSystemService(
                                          Context.ACTIVITY_SERVICE
                                        );
    int maxKb = activityManager.getMemoryClass() * 1024;
    int limitKb = maxKb / 8; /* 1/8 of total available RAM */
    mCache = new LruBitmapCache(limitKb);

    /* Initialize Firebase components */
    //firebaseStorage  = FirebaseStorage.getInstance();
    //storageReference = firebaseStorage.getReference();
    //storageReference = storageReference.child("profile-pics");
    Log.e(TAG, "End of imageLoader constructor");
  } // END CONSTRUCTOR

  /**
   * Public Method putBitmap
   *
   * Simply encapsulates the real put() method of LruCache class.
   *
   * @param usrId: (String) The user ID assigned by Firebase Auth.
   * @param bmp: (Bitmap) The Bitmap object indicates the user avatar picture.
   */
  public void putBitmap(String usrId, Bitmap bmp){this.mCache.put(usrId, bmp);}

  /**
   * Public Method cleanCache
   *
   * Simply encapsulates the evictAll() method of LruCache class. Every time
   * when this method is called, the LruCache object will release all memory.
   */
  public void cleanCache(){
    mCache.evictAll();
  }

  /**
   * Public Method trimCacheSizeToHalf
   *
   * Simply encapsulates the trimToSize() method of LruCache class. When this
   * method is called, the LruCache memory size will be cut in half.
   */
  public void trimCacheSizeToHalf(){
    mCache.trimToSize(mCache.size()/2);
  }

  /**
   * Public Method display
   *
   * This method is the method that would be called by AnnouncementFragment_v2
   * object many times. It would simply attach all requested users' avatar
   * pictures to RecyclerView, which is a list of announcements.
   *
   * Important: This method will first look into cache to see if the requesting
   *            bitmap is in the cache or not. If it does, take the bitmap from
   *            cache and use it right away. If it does not exist in cache, then
   *            create a new SetImageTask and run the task in slave-thread. The
   *            SetImageTask will look into internal storage and firebase to
   *            check the avatar picture.
   *
   * @param usrId: (String) user ID which is assigned by Firebase Auth
   * @param circularImageView: (CircularImageView) the image view for avatar
   */
  public void display(String usrId, CircularImageView circularImageView) {
    Log.e(TAG, "ImageLoader.display() called");
    Bitmap bitmap = this.mCache.get(usrId);
    if(bitmap != null){
      Log.e(TAG, "ImageLoader.display() image in cache");
      circularImageView.setImageBitmap(bitmap);
      return;
    }
    Log.e(TAG, "ImageLoader.display() image not in cache");
    firebaseStorage  = FirebaseStorage.getInstance();
    storageReference = (firebaseStorage.getReference()).child("profile-pics");
    Handler refresh = new Handler(Looper.getMainLooper());
    refresh.post(new SetImageTask(circularImageView, usrId));
  }

  //@Override
  //public void onTrimMemory(int level) {
  //  if (level >= TRIM_MEMORY_MODERATE) {
  //    mCache.evictAll();
  //  }
  //  else if (level >= TRIM_MEMORY_BACKGROUND) {
  //    mCache.trimToSize(mCache.size() / 2);
  //  }
  //} // END METHOD onTrimMemory

  //@Override
  //public void onConfigurationChanged(Configuration configuration) {
  //} // END METHOD onConfigurationChanged

  //@Override
  //public void onLowMemory() {
  //} // END METHOD onLowMemory

  /**
   * Private Inner Class LruBitmapCache
   *
   * This class is extends from LruCache from library because we need to
   * override sizeOf() method for better performance.
   */
  private class LruBitmapCache extends LruCache<String, Bitmap>{

    /**
     * Public Method LruBitmapCache
     *
     * The constructor method for class LruBitmapCache.
     *
     * @param maxSize: (int) the max size of cache memory.
     */
    public LruBitmapCache(int maxSize){
      super(maxSize);
    } // END CONSTRUCTOR

    //public void insert(String key, Bitmap value){
    //  this.put(key, value);
    //}

    /**
     * Protected Method sizeOf
     *
     * This method is provide system a way to get the size of specific entry,
     * which in our case is the specific bitmap size, so that the system can
     * manage cache memory in better precision.
     *
     * @param key: (String) the key indicates the user ID.
     * @param value: (Bitmap) the value indicates the avatar picture.
     * @return (int) the size of requesting bitmap.
     */
    @Override
    protected int sizeOf(String key, Bitmap value){
      return value.getByteCount() / 1024;
    } // END METHOD sizeOf
  } // END CLASS LruBimapCache

  //private class SetImageTask extends AsyncTask<String, Void, Integer> {

  /**
   * Private Class SetImageTask
   *
   * This class is built for distributing set-image-tasks into slave-threads.
   * Here, if we're loading and setting bitmap to circular image view in one
   * thread(UI thread), the system will not able to handle that many processes
   * once. Thus, the user will have a horrible experience. The multithreading
   * solution is necessary here.
   *
   * Note: Even though Android-way of achieving this task is by using AsyncTask,
   *       from my testing, the Runnable is out performed the AsyncTask.
   */
  private class SetImageTask implements Runnable{
    //private Semaphore semaphore = new Semaphore(0);
    private final String TAG = SetImageTask.class.getSimpleName();
    private final long FIVETWELVE_KB = 1024*512;
    private CircularImageView circularImageView;
    private String usrId;
    private Bitmap bitmap;

    /**
     * Public Method SetImageTask
     *
     * The constructor method for class SetImageTask. In constructor, it will
     * initialize the two necessary fields for SetImageTask - usrId and circular
     * image view.
     *
     * @param circularImageView: (CircularImageView) a reference to the view
     * @param uId: (String) user ID that is generated by Firebase
     */
    public SetImageTask(CircularImageView circularImageView, String uId){
      Log.e(TAG, "SetImageTask constructor");
      this.circularImageView = circularImageView;
      this.usrId = uId;
    } // END CONSTRUCTOR

    /**
     * Public Method run
     *
     * This method will be executed in separated thread when the start() is
     * called.
     */
    @Override
    public void run(){
      setBitmap();
    }

    /**
     * Private Method bitmapResize
     *
     * Resizing the bitmap to the smaller size to reduce memory work loads.
     *
     * @param bmp: (Bitmap) a reference to bitmap
     * @return: (Bitmap) a reduced size bitmap
     */
    private Bitmap bitmapResize(Bitmap bmp){
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

    //@Override
    //protected Integer doInBackground(String... strings) {
    //  Log.e(TAG, "SetImageTask.doInBackground()");
    //  String usrId = strings[0];
    //  try {
    //    /* Requested Image is not in cache */
    //    setBitmap(usrId); /* setBitmap: check internal storage, then firebase */
    //    return 1;
    //  } catch (Exception e) {
    //    e.printStackTrace();
    //    return 0;
    //  }
    //} // END METHOD doInBackground

    //@Override
    //protected void onPostExecute(Integer result) {
    //  Log.e(TAG, "SetImageTask.onPostExecute()");
    //  if (result == 1) {
    //    //this.circularImageView.setImageBitmap(this.bitmap);
    //    //try {
    //    //  this.semaphore.acquire();
    //    //  this.circularImageView.setImageBitmap(this.bitmap);
    //    //} catch (InterruptedException e) {
    //    //  e.printStackTrace();
    //    //}
    //  }
    //  super.onPostExecute(result);
    //} // END METHOD onPostExecute

    /**
     * Private Method setBitmap
     *
     * The setBitmap method would request Firebase Storage to fetch back the
     * avatar picture based upon user ID. After receiving the avatar picture
     * from Firebase Storage, the method will resize the bitmap into specific
     * size, set picture to circularImageView, and put it into cache.
     *
     */
    private void setBitmap() {
      Log.e(TAG, "SetImageTask.setBitmap()");
      Log.e(TAG, "SetImageTask usrId = " + usrId);
      final String uId = usrId;

      //File checkFile = mContext.getFileStreamPath(uId);
      //if(checkFile.exists()){ /* Image exists in internal storage */
      //  Log.e(TAG, "SetImageTask image exists in internal storage");
      //  this.bitmap = readFromInternalStorage(uId);
      //  bitmap = bitmapResize(bitmap);
      //  mCache.put(uId, this.bitmap);
      //  //semaphore.release();
      //}
      //else{ /* Image does not exist in internal storage */
        Log.e(TAG, "SetImageTask image does not exist in internal storage");
        /* Read from Firebase Storage */
        //firebaseStorage  = FirebaseStorage.getInstance();
        //storageReference = (firebaseStorage.getReference()).child("profile-pics");
        StorageReference islandReference = storageReference
                                             .child(usrId)
                                             .child("profile.jpg");
        islandReference.getBytes(this.FIVETWELVE_KB)
                       .addOnSuccessListener(new OnSuccessListener<byte[]>() {
          @Override
          public void onSuccess(byte[] bytes) {
            Log.e(TAG, "SetImageTask - StorageReference onSuccess");
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            bitmap = bitmapResize(bitmap);
            //circularImageView.setImageBitmap(bitmap);
            mCache.put(uId, bitmap);
            //semaphore.release();
          }
        }) // END METHOD CALL addOnSuccessListener
           /* Fail, then read default */
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            Log.e(TAG, "SetImageTask - StorageReference onFailure");
            //bitmap = decodeBitmapWithGiveSizeFromResource(
            bitmap = BitmapFactory.decodeResource(
              mContext.getResources(), R.drawable.ic_face_grey_500_48dp
            );
            bitmap = bitmapResize(bitmap);
            //circularImageView.setImageBitmap(bitmap);
            mCache.put(uId, bitmap);
            //semaphore.release();
          }
        }) // END METHOD CALL addOnFailureListener
        .addOnCompleteListener(new OnCompleteListener<byte[]>() {
          @Override
          public void onComplete(@NonNull Task<byte[]> task) {
            circularImageView.setImageBitmap(
              mCache.get(uId)
            );
          }
        });
      //}
    } // END METHOD getBitmap

    //private Bitmap decodeBitmapWithGiveSizeFromResource(Resources res, int resId) {
    //  // First decode with inJustDecodeBounds=true to check dimensions
    //  BitmapFactory.Options options = new BitmapFactory.Options();
    //  options.inJustDecodeBounds = true;
    //  BitmapFactory.decodeResource(res, resId, options);
    //  // Calculate inSampleSize
    //  options.inSampleSize = getSampleSize(options);
    //  // Decode bitmap with inSampleSize set
    //  options.inJustDecodeBounds = false;
    //  return BitmapFactory.decodeResource(res, resId, options);
    //  //return BitmapFactory.decodeResource(res, resId);
    //}

    //private int getSampleSize(BitmapFactory.Options opt){
    //  int imageHeight = opt.outHeight;
    //  int imageWidth = opt.outWidth;
    //  int inSampleSize = 1;
    //  int reqHeight = circularImageView.getHeight();
    //  int reqWidth = circularImageView.getWidth();
    //  if (imageHeight > reqHeight || imageWidth > reqWidth) {
    //    final int halfHeight = imageHeight / 2;
    //    final int halfWidth = imageWidth / 2;
    //    // Calculate the largest inSampleSize value that is a power of 2 and keeps both
    //    // height and width larger than the requested height and width.
    //    while ((halfHeight / inSampleSize) > reqHeight
    //      && (halfWidth / inSampleSize) > reqWidth) {
    //      inSampleSize *= 2;
    //    }
    //  }
    //  return inSampleSize;
    //} // END METHOD getSampleSize

    /**
     * Private Method readFromInternalStorage
     *
     * Read the file from internal storage and resize, then return the bitmap.
     *
     * @param uId: (String) user ID assigned by Firebase Auth
     * @return: (Bitmap) resized bitmap
     */
    private Bitmap readFromInternalStorage(String uId){
      FileInputStream fileInputStream;
      BitmapFactory.Options opt;
      try {
        fileInputStream = mContext.openFileInput(uId);
        //opt = new BitmapFactory.Options();
        //opt.inJustDecodeBounds = true;
        //BitmapFactory.decodeStream(fileInputStream, null, opt);
        //opt.inSampleSize = getSampleSize(opt);
        //opt.inJustDecodeBounds = false;
        //fileInputStream = mContext.openFileInput(uId);
        byte[] imgByteArray = new byte[fileInputStream.available()];
        while (fileInputStream.read(imgByteArray) != -1) { /* Empty */ }
        return bitmapResize(BitmapFactory.decodeByteArray(
                 //imgByteArray, 0, imgByteArray.length, opt
                 imgByteArray, 0, imgByteArray.length
               ));
      }
      catch (FileNotFoundException ffe){
        ffe.printStackTrace();
      }
      catch (IOException ioe){
        ioe.printStackTrace();
      }
      return null;
    } // END METHOD readFromInternalStorage
  } // END CLASS SetImageTask
} // END CLASS ImageLoader

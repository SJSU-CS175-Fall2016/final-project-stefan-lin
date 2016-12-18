package com.stefan.jeremy.clubsoda;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.util.LruCache;

import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.stefan.jeremy.clubsoda.utils.ImageLoader;

import java.util.HashMap;

/**
 * Background App Application
 *
 * This class will run as soon as the app had been initiated.
 */
public class App extends MultiDexApplication implements ComponentCallbacks2{
  public static ImageLoader imageLoader;
  //private static LruCache<String, Bitmap> bitmapLruCache = new LruCache<>(((int)Runtime.getRuntime().maxMemory()/1024)/8);
  //private static LruCache<String, Bitmap> bitmapLruCache =
  //  new LruCache<String, Bitmap>(((int)Runtime.getRuntime().maxMemory()/1024)/8){
  //    @Override
  //    protected int sizeOf(String key, Bitmap value){
  //      return value.getByteCount() / 1024;
  //    } // END METHOD sizeOf
  //  };
  //public static HashMap<String, Bitmap> imageMap = new HashMap<>();

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(newBase);
    MultiDex.install(this);
  }

  @Override
  public void onCreate(){
    Firebase.setAndroidContext(getApplicationContext());
    if (!FirebaseApp.getApps(this).isEmpty()) {
      FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
    Log.e("OnCreate() in App:", "About to create bitmaplru");
    imageLoader = new ImageLoader(getApplicationContext());
    if(imageLoader != null) {
      Log.e("App", "imageLoader is not null - " + imageLoader.TAG);
    }
    else{
      Log.e("App", "imageLoader is null XXXXXXXXXXXXXXXXXXXX");
    }
    super.onCreate();
  }

  //public static LruCache<String, Bitmap> getBitmapLruCache() {
  //  return bitmapLruCache;
  //}


  @Override
  public void onTrimMemory(int level) {
    if (level >= TRIM_MEMORY_MODERATE) {
      imageLoader.cleanCache();
    }
    else if (level >= TRIM_MEMORY_BACKGROUND) {
      imageLoader.trimCacheSizeToHalf();
    }
  } // END METHOD onTrimMemory

  @Override
  public void onConfigurationChanged(Configuration configuration) {

  } // END METHOD onConfigurationChanged

  @Override
  public void onLowMemory() {

  } // END METHOD onLowMemory
}

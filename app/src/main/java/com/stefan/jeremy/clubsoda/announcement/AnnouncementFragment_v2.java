package com.stefan.jeremy.clubsoda.announcement;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//import com.andexert.library.RippleView;
import com.andexert.library.RippleView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stefan.jeremy.clubsoda.App;
import com.stefan.jeremy.clubsoda.DetailActivity;
import com.stefan.jeremy.clubsoda.R;
import com.stefan.jeremy.clubsoda.data.AnnouncementRecord;
import com.stefan.jeremy.clubsoda.data.User;
import com.stefan.jeremy.clubsoda.utils.Constants;
import com.stefan.jeremy.clubsoda.utils.DateConverter;

import java.lang.ref.WeakReference;

/**
 * Created by stefanlin on 8/19/16.
 */

public class AnnouncementFragment_v2 extends Fragment {
  /* Components */
  private static final long FIVETWELVE_KB = 1024*512;
  private OnFragmentInteractionListener mListener;
  private User                    mCurrUser;
  private FloatingActionButton    mFab;
  private DatabaseReference       mDatabase;
  private FirebaseStorage         mFirebaseStorage;
  private StorageReference        mStorageRef;
  private RecyclerView            mRecyclerView;
  private WeakReference<Activity> mCurrentActivity;
  private Bitmap                  mBitmap;

  /* Private Helper Methods */
  private void initFAB(){
    mFab = (FloatingActionButton) getActivity()
      .findViewById(R.id.announcement_FAB);
    if(mFab != null) {
      mFab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          openNewRecordActivity();
        }
      });
    }
    else{
      //TODO: Handle mFab is null (maybe dynamically create a FAB)
    }
  } // END METHOD initFAB

  private void openNewRecordActivity(){
    Intent intent = new Intent(
      getActivity(),
      NewRecordActivity.class
    );
    intent.putExtra(Constants.INTENT_FRAGMENT_TYPE, Constants.ANNOUNCEMENT_TYPE);
    intent.putExtra(Constants.INTENT_PASS_USER, mCurrUser);
    startActivity(intent);
    getActivity().overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
  } // END METHOD openNewAnnouncementRecordActivity

  /* Control Bitmap Size */
  //private Bitmap decodeBitmapWithGiveSizeFromResource(Resources res, int resId,
  //                                                           int reqWidth, int reqHeight) {
  //  // First decode with inJustDecodeBounds=true to check dimensions
  //  final BitmapFactory.Options options = new BitmapFactory.Options();
  //  options.inJustDecodeBounds = true;
  //  BitmapFactory.decodeResource(res, resId, options);
  //  // Calculate inSampleSize
  //  options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
  //  // Decode bitmap with inSampleSize set
  //  options.inJustDecodeBounds = false;
  //  return BitmapFactory.decodeResource(res, resId, options);
  //  //return BitmapFactory.decodeResource(res, resId);
  //}

  //public static int calculateInSampleSize(
  //  BitmapFactory.Options options, int reqWidth, int reqHeight) {
  //  final int height = options.outHeight;
  //  final int width = options.outWidth;
  //  int inSampleSize = 1;
  //  if (height > reqHeight || width > reqWidth) {
  //    final int halfHeight = height / 2;
  //    final int halfWidth = width / 2;
  //    while ((halfHeight / inSampleSize) > reqHeight
  //      && (halfWidth / inSampleSize) > reqWidth) {
  //      inSampleSize *= 2;
  //    }
  //  }
  //  return inSampleSize;
  //}
  /* End Control Bitmap Size */

  //private Bitmap bitmapResize(Bitmap bmp){
  //  int width = bmp.getWidth();
  //  int height = bmp.getHeight();
  //  float scaleWidth = ((float) Constants.PROFILE_PIC_WIDTH) / width;
  //  float scaleHeight = ((float) Constants.PROFILE_PIC_HEIGHT) / height;
  //  /*Matrix matrix = new Matrix();
  //  matrix.postScale(scaleWidth, scaleHeight);
  //  Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, false);*/
  //  float aspectRatio = bmp.getWidth() / (float) bmp.getHeight();
  //  Bitmap resizedBitmap = Bitmap.createScaledBitmap(bmp, 50, Math.round(50 / aspectRatio), false);
  //  return resizedBitmap;
  //}

  //private void populateCircularImageView(final CircularImageView circularImageView, String uid){
  //  final String usrId = uid;
  //  byte[] imgByteArray = null;
  //  Bitmap temp;
  //  File checkFile = mCurrentActivity.get().getBaseContext().getFileStreamPath(usrId);
  //  if(checkFile.exists()){ /* Avatar image exists */
  //    try {
  //      Bitmap t = App.getBitmapLruCache().get(uid);
  //      if(t != null){
  //        circularImageView.setImageBitmap(
  //          App.getBitmapLruCache().get(uid)
  //        );
  //      }
  //      else {
  //        FileInputStream fileInputStream = mCurrentActivity.get().openFileInput(uid);
  //        //BitmapFactory.Options opt = new BitmapFactory.Options();
  //        //opt.inJustDecodeBounds = true;
  //        //BitmapFactory.decodeStream(fileInputStream, null, opt);
  //        //int imageHeight = opt.outHeight;
  //        //int imageWidth = opt.outWidth;
  //        //String imageType = opt.outMimeType;
  //        //int inSampleSize = 1;
  //        //int reqHeight = circularImageView.getHeight();
  //        //int reqWidth = circularImageView.getWidth();
  //        //if (imageHeight > reqHeight || imageWidth > reqWidth) {
  //        //  final int halfHeight = imageHeight / 2;
  //        //  final int halfWidth = imageWidth / 2;
  //        //  // Calculate the largest inSampleSize value that is a power of 2 and keeps both
  //        //  // height and width larger than the requested height and width.
  //        //  while ((halfHeight / inSampleSize) > reqHeight
  //        //    && (halfWidth / inSampleSize) > reqWidth) {
  //        //    inSampleSize *= 2;
  //        //  }
  //        //}
  //        //opt.inSampleSize = inSampleSize;
  //        //opt.inJustDecodeBounds = false;
  //        //fileInputStream = mCurrentActivity.get().openFileInput(uid);
  //        imgByteArray = new byte[fileInputStream.available()];
  //        while (fileInputStream.read(imgByteArray) != -1) { /* Empty */ }
  //        temp = bitmapResize(BitmapFactory.decodeByteArray(imgByteArray, 0, imgByteArray.length));
  //        Log.i("0bitmap aloc memory", String.valueOf(temp.getAllocationByteCount()));
  //        Log.i("0bitmap memory", String.valueOf(temp.getByteCount()));
  //        Log.i("0bitmap raw", String.valueOf(temp.getRowBytes()));
  //        circularImageView.setImageBitmap(
  //          //BitmapFactory.decodeByteArray(imgByteArray, 0, imgByteArray.length)
  //          temp
  //        );
  //        App.getBitmapLruCache().put(uid, temp);
  //      }
  //    }
  //    catch (IOException ioe){
  //      ioe .printStackTrace();
  //    }
  //  } // END IF STATEMENT
  //  else{ /* Avatar image does not exists */
  //    //mBitmap = BitmapFactory.decodeResource(
  //    //  mCurrentActivity.get().getResources(),
  //    //  R.drawable.ic_face_grey_500_48dp
  //    //);
  //    mBitmap = bitmapResize(decodeBitmapWithGiveSizeFromResource(
  //      getResources(),
  //      R.drawable.ic_face_grey_500_48dp,
  //      circularImageView.getWidth(),
  //      circularImageView.getHeight()
  //    ));
  //    /* Load from Firebase Storage */
  //    StorageReference islandRef = mStorageRef.child(usrId).child("profile.jpg");
  //    islandRef.getBytes(FIVETWELVE_KB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
  //      @Override
  //      public void onSuccess(byte[] bytes) {
  //        mBitmap = bitmapResize(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
  //        /* Save image to internal storage */
  //        FileOutputStream fileOutputStream = null;
  //        try {
  //          fileOutputStream = mCurrentActivity.get().openFileOutput(
  //            usrId, Context.MODE_PRIVATE
  //          );
  //          fileOutputStream.write(bytes);
  //          fileOutputStream.close();
  //        }
  //        catch (FileNotFoundException e) {
  //          e.printStackTrace();
  //        }
  //        catch (Exception e){
  //          e.printStackTrace();
  //        }
  //        /* End saving image to internal storage */
  //      }
  //    }); // END METHOD CALL addonSuccessListener
  //    islandRef.getBytes(FIVETWELVE_KB).addOnCompleteListener(new OnCompleteListener<byte[]>() {
  //      @Override
  //      public void onComplete(@NonNull Task<byte[]> task) {
  //        circularImageView.setImageBitmap(mBitmap);
  //      } // END METHOD onComplete
  //    }); // END METHOD CALL addOnCompleteListener
  //  } // END ELSE STATEMENT
  //} // END METHOD populateCircularImageView

  private void populateRecyclerView(){
    DatabaseReference mRef = mDatabase
      .child(Constants.FIREBASE_ANNOUNCEMENT_TABLE)
      .child(mCurrUser.currClub);
    //.child(Constants.FIREBASE_TABLE_RECORDS);
    FirebaseRecyclerAdapter<AnnouncementRecord, MessageViewHolder> adapter =
      new FirebaseRecyclerAdapter<AnnouncementRecord, MessageViewHolder>(
        AnnouncementRecord.class,
        R.layout.single_announcement_record_v2,
        //R.layout.single_finance_record,
        MessageViewHolder.class,
        mRef){
        @Override
        protected void populateViewHolder(MessageViewHolder viewHolder,
                                          AnnouncementRecord model,
                                          int position) {
          final MessageViewHolder vh = viewHolder;
          final AnnouncementRecord ar = model;
          viewHolder.cAuthorTime.setText(DateConverter.convertMillisecondToDate(
            model.author, model.getTimestamp()
          ));
          viewHolder.cUsrId = model.uid;
          viewHolder.cTitle.setText(model.title);
          viewHolder.cContent.setText(model.description);
          App.imageLoader.display(model.uid, viewHolder.cCircularImage);
          //Handler refresh = new Handler(Looper.getMainLooper());
          //refresh.post(new Runnable() {
          //  public void run() {
          //    populateCircularImageView(vh.cCircularImage, ar.uid);
          //  }
          //});
          /******** Important *********/
            /*
              initRippleView() provide ripple effect and in its onComplete()
              it will redirect activity to detail view activity
             */
          viewHolder.initRippleView(mCurrentActivity.get());
        }
      }; // END adapter ASSIGNMENT
    this.mRecyclerView.setAdapter(adapter);/* Binds RecyclerView with Adapter */
  } // END METHOD populateRecyclerView

  private void initRecyclerView(){
    mRecyclerView = (RecyclerView) getActivity().findViewById(
      R.id.announcement_recycler
    );
    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
    layoutManager.setReverseLayout(true);
    layoutManager.setStackFromEnd(true);
    mRecyclerView.setLayoutManager(layoutManager);
    //RecyclerViewHeader header = (RecyclerViewHeader)getActivity().findViewById(R.id.announcementHeader);
    //header.attachTo(this.mRecyclerView);
  } // END METHOD initRecyclerView

  public AnnouncementFragment_v2(){ /* Empty Constructor */ }

  public static AnnouncementFragment_v2 newInstance(User _mCurrUser){
    AnnouncementFragment_v2 fragment = new AnnouncementFragment_v2();
    Bundle args = new Bundle();
    args.putSerializable(Constants.BUNDLE_USER, _mCurrUser);
    fragment.setArguments(args);

    return fragment;
  } // END METHOD newInstance

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState){
    /* Initialize componenets */
    mDatabase = FirebaseDatabase.getInstance().getReference();
    this.mCurrentActivity = new WeakReference<Activity>(getActivity());
    this.mCurrUser = (User) ((savedInstanceState != null)?
      savedInstanceState.getSerializable(Constants.BUNDLE_USER):
      getArguments().getSerializable(Constants.BUNDLE_USER));
    mFirebaseStorage  = FirebaseStorage.getInstance();
    mStorageRef       = (mFirebaseStorage.getReference()).child("profile-pics");

    this.initFAB();              /* Set up floating action button */
    this.initRecyclerView();     /* Set up RecyclerView */
    this.populateRecyclerView(); /* Populate RecyclerView with Firebase */
  } // END METHOD onViewCreated

  @Override
  public void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_announcement, container, false);
  }

  @TargetApi(23)
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.onAttachToContext(context);
  }

  /*
   * Deprecated on API 23
   * Use onAttachToContext instead
   */
  @SuppressWarnings("deprecation")
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      onAttachToContext(activity);
    }
  }

  /*
   * Called when the fragment attaches to the context
   */
  protected void onAttachToContext(Context context) {
    if (context instanceof OnFragmentInteractionListener) {
      mListener = (OnFragmentInteractionListener) context;
    }
    else {
      throw new RuntimeException(context.toString()
        + " must implement OnFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach(){
    super.onDetach();
    mListener = null;
  }

  public static class MessageViewHolder extends RecyclerView.ViewHolder{
    /* Components */
    //protected ImageView cPhoto;
    public TextView          cTitle;
    public TextView          cAuthorTime;
    public TextView          cContent;
    public CircularImageView cCircularImage;
    public String            cUsrId;

    public MessageViewHolder(View itemView){
      super(itemView);
      this.cContent    = (TextView)itemView.findViewById(R.id.record_content);
      this.cAuthorTime = (TextView)itemView.findViewById(R.id.record_author_and_time);
      this.cTitle      = (TextView)itemView.findViewById(R.id.record_title);
      this.cCircularImage = (CircularImageView)itemView.findViewById(R.id.record_avatar);
      this.cUsrId      = "-1";
    } // END CONSTRUCTOR

    public void initRippleView(final Activity activity){
      RippleView rippleView = (RippleView) itemView
        .findViewById(R.id.record_ripple_effect);
      rippleView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Intent intent = new Intent(
            activity, DetailActivity.class
          );
          /* {title, authorTime, content} */
          String[] strs = {
            cTitle.getText().toString(),
            cAuthorTime.getText().toString(),
            cContent.getText().toString(),
            cUsrId
          };
          intent.putExtra(Constants.INTENT_FRAGMENT_VALUE, strs);
          intent.putExtra(Constants.INTENT_AVATAR_SRC_ID, (Integer)cCircularImage.getTag());
          //TODO: CHECK USER SDK VERSION HERE
          if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
              //activity, cTitle, cTitle.getTransitionName()
              activity, cCircularImage, cCircularImage.getTransitionName()
            );
            activity.startActivity(intent, options.toBundle());
          }
          else{
            activity.startActivity(intent);
          }
        }
      });
      //rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
      //  @Override
      //  public void onComplete(RippleView rippleView) {
      //    Intent intent = new Intent(
      //      activity, DetailActivity.class
      //    );
      //    /* {title, authorTime, content} */
      //    String[] strs = {
      //      cTitle.getText().toString(),
      //      cAuthorTime.getText().toString(),
      //      cContent.getText().toString(),
      //      cUsrId
      //    };
      //    intent.putExtra(Constants.INTENT_FRAGMENT_VALUE, strs);
      //    intent.putExtra(Constants.INTENT_AVATAR_SRC_ID, (Integer)cCircularImage.getTag());
      //    //TODO: CHECK USER SDK VERSION HERE
      //    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      //      ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
      //        //activity, cTitle, cTitle.getTransitionName()
      //        activity, cCircularImage, cCircularImage.getTransitionName()
      //      );
      //      activity.startActivity(intent, options.toBundle());
      //    }
      //    else{
      //      activity.startActivity(intent);
      //    }
      //  }
      //}); // END METHOD CALL setOnRippleCompleteListener
    } // END METHOD initRippleView
  } // END CLASS MessageViewHolder

  public interface OnFragmentInteractionListener {
    // TODO: Update argument type and name
    void onAnnouncementFragmentInteraction();
  } // END INTERFACE OnFragmentInsteractionListener
} // END CLASS AnnouncementFragment

package com.stefan.jeremy.clubsoda.announcement;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//import com.andexert.library.RippleView;
import com.andexert.library.RippleView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stefan.jeremy.clubsoda.DetailActivity;
import com.stefan.jeremy.clubsoda.R;
import com.stefan.jeremy.clubsoda.data.AnnouncementRecord;
import com.stefan.jeremy.clubsoda.data.User;
import com.stefan.jeremy.clubsoda.utils.Constants;

import java.lang.ref.WeakReference;

/**
 * Created by stefanlin on 8/16/16.
 */

public class AnnouncementFragment_temp extends Fragment {
  /* Components */
  private OnFragmentInteractionListener mListener;
  private User                    mCurrUser;
  private FloatingActionButton    mFab;
  private DatabaseReference       mDatabase;
  private RecyclerView            mRecyclerView;
  private WeakReference<Activity> mCurrentActivity;


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
    //TODO: transition b/w activities
  } // END METHOD openNewAnnouncementRecordActivity

  private void populateRecyclerView(){
    DatabaseReference mRef = mDatabase
                               .child(Constants.FIREBASE_ANNOUNCEMENT_TABLE)
                               .child(mCurrUser.currClub);
                               //.child(Constants.FIREBASE_TABLE_RECORDS);
    FirebaseRecyclerAdapter<AnnouncementRecord, AnnouncementFragment_temp.MessageViewHolder> adapter =
      new FirebaseRecyclerAdapter<AnnouncementRecord, AnnouncementFragment_temp.MessageViewHolder>(
        AnnouncementRecord.class,
        R.layout.single_announcement_record_temp,
        //R.layout.single_finance_record,
        AnnouncementFragment_temp.MessageViewHolder.class,
        mRef){
          @Override
          protected void populateViewHolder(AnnouncementFragment_temp.MessageViewHolder viewHolder,
                                            AnnouncementRecord model,
                                            int position) {
            viewHolder.cAuthor.setText(model.author);
            viewHolder.cTitle.setText(model.title);
            viewHolder.cContent.setText(model.description);
            viewHolder.cTime.setText(
              //DateConverter.convertMillisecondToDate(model.getTimestamp())  /// TIMESTAMP HERE /////////// TODO: 8/17/16
              "12345"
            );
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
    mRecyclerView = (RecyclerView) getActivity()
                                     .findViewById(R.id.announcement_recycler);
    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
    layoutManager.setReverseLayout(true);
    mRecyclerView.setLayoutManager(layoutManager);
  } // END METHOD initRecyclerView

  public AnnouncementFragment_temp(){ /* Empty Constructor */ }

  public static AnnouncementFragment_temp newInstance(User _mCurrUser){
    AnnouncementFragment_temp fragment = new AnnouncementFragment_temp();
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

  /* This class has to be static */
  public static class MessageViewHolder extends RecyclerView.ViewHolder{
    /* Components */
    //protected ImageView cPhoto;
    public TextView   cTitle;
    public TextView   cTime;
    public TextView   cAuthor;
    public TextView   cContent;

    public MessageViewHolder(View itemView){
      super(itemView);
      Log.e("TEST TEST TEST", "in MessageViewHolder constructor");
      this.cTime    = (TextView) itemView.findViewById(R.id.card_time);
      this.cContent = (TextView) itemView.findViewById(R.id.card_content);
      this.cAuthor  = (TextView) itemView.findViewById(R.id.card_author);
      this.cTitle   = (TextView) itemView.findViewById(R.id.card_title);
    } // END CONSTRUCTOR

    public void initRippleView(final Activity activity){
      RippleView rippleView = (RippleView) itemView
                                             .findViewById(R.id.card_ripple);
      rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
        @Override
        public void onComplete(RippleView rippleView) {
          TextView t = (TextView) rippleView.findViewById(R.id.card_time);
          TextView c = (TextView) rippleView.findViewById(R.id.card_content);
          TextView a = (TextView) rippleView.findViewById(R.id.card_author);
          TextView l = (TextView) rippleView.findViewById(R.id.card_title);

          Intent intent = new Intent(
            activity, DetailActivity.class
          );
          String[] strs = {
            t.getText().toString(),
            c.getText().toString(),
            a.getText().toString(),
            l.getText().toString()
          };
          intent.putExtra(Constants.INTENT_FRAGMENT_VALUE, strs);

          //TODO: CHECK USER SDK VERSION HERE
          if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //intent.putExtra(DetailActivity.EXTRA_CONTACT, contact);
            //Pair<View, String> p1 = Pair.create((View) t, t.getTransitionName());
            //Pair<View, String> p2 = Pair.create((View) c, c.getTransitionName());
            //Pair<View, String> p3 = Pair.create((View) a, a.getTransitionName());
            //Pair<View, String> p4 = Pair.create((View) l, l.getTransitionName());
            //ActivityOptionsCompat options = ActivityOptionsCompat.
            //  makeSceneTransitionAnimation(context.get(), p1, p2, p3, p4);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
              activity, (View)l, l.getTransitionName()
            );
            activity.startActivity(intent, options.toBundle());
          }
          else{
            activity.startActivity(intent);
          }
        }
      }); // END METHOD CALL setOnRippleCompleteListener
    } // END METHOD initRippleView
  } // END CLASS MessageViewHolder

  public interface OnFragmentInteractionListener {
    // TODO: Update argument type and name
    void onAnnouncementFragmentInteraction();
  } // END INTERFACE OnFragmentInsteractionListener
}

package com.stefan.jeremy.clubsoda.clubManagement;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stefan.jeremy.clubsoda.ClubActivity;
import com.stefan.jeremy.clubsoda.R;
import com.stefan.jeremy.clubsoda.finances.FinancesFragment;
import com.stefan.jeremy.clubsoda.utils.Constants;
import com.stefan.jeremy.clubsoda.utils.globalMethods;

public class ClubManagementFragment extends Fragment {

  // TODO: Rename and change types of parameters

  private OnFragmentInteractionListener mListener;

  private Button mAddButton;
  private EditText mEmailText;
  private ProgressDialog mPd;
  private FirebaseAuth mAuth;
  private DatabaseReference mDatabase;
  private String mCurrClubName;


  public static ClubManagementFragment newInstance() {
    ClubManagementFragment fragment = new ClubManagementFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }
  public static ClubManagementFragment newInstance(String mCurrClubName) {
    ClubManagementFragment fragment = new ClubManagementFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable("mCurrClubName", mCurrClubName);
    fragment.setArguments(bundle);
    return fragment;
  }

  public ClubManagementFragment() { }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    mAuth = FirebaseAuth.getInstance();
    mDatabase = FirebaseDatabase.getInstance().getReference();

    mCurrClubName = (String)getArguments().getSerializable("mCurrClubName");
    mAddButton = (Button) getActivity().findViewById(R.id.clubMng_addButton);
    mEmailText = (EditText) getActivity().findViewById(R.id.clubMng_addUser);
    mPd = new ProgressDialog(this.getContext());

    mAddButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String email = mEmailText.getText().toString();
        addUserToClub(email);
      }
    });

  }

  private void addUserToClub(String email) {
    // add user to club
    email = globalMethods.emailToDatabaseConverter(email);
    String uid;
    mDatabase.child("email-to-uid").child(email).addListenerForSingleValueEvent(
            new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                  Toast.makeText(getActivity().getApplicationContext(),
                          "Email does not exist in our database!", Toast.LENGTH_LONG);
                } else {
                  String uid = (String) dataSnapshot.getValue();
                  mDatabase.child("user-club-index").child(uid).child("clubs")
                           .child(mCurrClubName).setValue(true);
                  getActivity().finish();
                }
              }
              @Override
              public void onCancelled(DatabaseError databaseError) { }
            }
    );
  }
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    Bundle bundle = getArguments();
    mCurrClubName = bundle.getString("mCurrClubName");
    return inflater.inflate(R.layout.fragment_club_management, container, false);
  }

  @TargetApi(23)
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnFragmentInteractionListener) {
      mListener = (OnFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString()
              + " must implement OnFragmentInteractionListener");
    }
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
    } else {
      throw new RuntimeException(context.toString()
        + " must implement OnFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  @Override
  public void onResume() {
    super.onResume();
    /*
    ((ClubActivity) getActivity()).setTextTitlebar(Constants.CLUB_MANAGEMENT_TITLE);*/
  }

  public interface OnFragmentInteractionListener {
    // TODO: Update argument type and name
    void onClubManagementInteraction();
  }
}

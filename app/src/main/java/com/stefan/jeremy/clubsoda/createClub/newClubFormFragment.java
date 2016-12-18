package com.stefan.jeremy.clubsoda.createClub;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.stefan.jeremy.clubsoda.R;
import com.stefan.jeremy.clubsoda.clubManagement.ClubManagementFragment;

public class newClubFormFragment extends Fragment {
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  // TODO: Rename and change types of parameters
  private String mParam1;
  private String mParam2;

  private OnFragmentInteractionListener mListener;
  private Typeface mHelveticaTF;
  private EditText mClubNameET;
  private EditText mSchoolET;

  private Button createClubButton;

  private FirebaseAuth mAuth;

  public newClubFormFragment() { }
  public static newClubFormFragment newInstance() {
    newClubFormFragment fragment = new newClubFormFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }


  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    mHelveticaTF = Typeface.createFromAsset(getActivity().getAssets(),
                                            "fonts/Helvetica.otf");
    /* Set Typeface */
    mClubNameET = (EditText) getView().findViewById(R.id.clubCreate_clubName);
    mSchoolET = (EditText) getView().findViewById(R.id.clubCreate_clubSchool);
    mClubNameET.setTypeface(mHelveticaTF);
    mSchoolET.setTypeface(mHelveticaTF);

    mAuth = FirebaseAuth.getInstance();
    createClubButton = (Button) getActivity().findViewById(R.id.clubCreate_clubCreate);
    createClubButton.setTypeface(mHelveticaTF);


    addActionListeners();
  }

  private void addActionListeners() {
    createClubButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        String clubName = mClubNameET.getText().toString();
        String school = mSchoolET.getText().toString();
        String uid = mAuth.getCurrentUser().getUid();
        mListener.onCreateClubFragment(clubName, school, uid);
      }
    });

  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mParam1 = getArguments().getString(ARG_PARAM1);
      mParam2 = getArguments().getString(ARG_PARAM2);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_new_club_form, container, false);
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

  /**
   * This interface must be implemented by activities that contain this
   * fragment to allow an interaction in this fragment to be communicated
   * to the activity and potentially other fragments contained in that
   * activity.
   * <p>
   * See the Android Training lesson <a href=
   * "http://developer.android.com/training/basics/fragments/communicating.html"
   * >Communicating with Other Fragments</a> for more information.
   */
  public interface OnFragmentInteractionListener {
    // TODO: Update argument type and name
    void onCreateClubFragment(String clubName, String school, String uid);
  }
}

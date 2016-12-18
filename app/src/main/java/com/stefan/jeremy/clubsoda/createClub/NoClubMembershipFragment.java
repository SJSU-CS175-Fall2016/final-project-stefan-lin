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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.stefan.jeremy.clubsoda.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NoClubMembershipFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NoClubMembershipFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoClubMembershipFragment extends Fragment {
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  // TODO: Rename and change types of parameters
  private String mParam1;
  private String mParam2;

  private OnFragmentInteractionListener mListener;
  private Typeface mHelveticaTF;
  private TextView oopsTV, descriptionTV;
  private Button createClubButton;
  private Button signOutButton;

  public NoClubMembershipFragment() {
    // Required empty public constructor
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {

    /* Set font */
    mHelveticaTF = Typeface.createFromAsset(getActivity().getAssets(),
                                              "fonts/Helvetica.otf");
    oopsTV = (TextView) view.findViewById(R.id.noClub_oops);
    descriptionTV = (TextView) view.findViewById(R.id.noClub_description);
     oopsTV.setTypeface(mHelveticaTF);
    //descriptionTV.setTypeface(mHelveticaTF);

    /* Switch fragments */
    createClubButton = (Button) view.findViewById(R.id.noClub_create);
    createClubButton.setTypeface(mHelveticaTF);

    signOutButton = (Button) getActivity().findViewById(R.id.noClub_signOut);
    signOutButton.setTypeface(mHelveticaTF);

    createClubButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        mListener.onNoClubMembershipFragment();
      }
    });

    signOutButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        mListener.onSignOut();
      }
    });

  }


  // TODO: Rename and change types and number of parameters
  public static NoClubMembershipFragment newInstance() {
    NoClubMembershipFragment fragment = new NoClubMembershipFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View v = inflater.inflate(R.layout.fragment_no_club_membership, container, false);
    return v;
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

  public interface OnFragmentInteractionListener {
    // TODO: Update argument type and name
    void onNoClubMembershipFragment();
    void onSignOut();
  }
}

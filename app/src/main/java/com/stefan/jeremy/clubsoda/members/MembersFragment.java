package com.stefan.jeremy.clubsoda.members;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.stefan.jeremy.clubsoda.R;
import com.stefan.jeremy.clubsoda.data.User;
import com.stefan.jeremy.clubsoda.finances.NewFinancesRecordActivity;
import com.stefan.jeremy.clubsoda.settings.SettingsActivity;

public class MembersFragment extends Fragment {
  private OnFragmentInteractionListener mListener;

  private User mCurrUser;
  private ImageView mSettingsIV;

  public MembersFragment() {
  }

  public static MembersFragment newInstance(User mCurrUser) {
    MembersFragment fragment = new MembersFragment();
    Bundle args = new Bundle();
    args.putSerializable("user", mCurrUser);
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
    return inflater.inflate(R.layout.fragment_members, container, false);

  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    // Get arguments
    mCurrUser = (User) getArguments().getSerializable("user");
    // Settings Image
    mSettingsIV = (ImageView) getActivity().findViewById(R.id.settings_button);
    mSettingsIV.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        intent.putExtra("user", mCurrUser);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
      }
    });


  }

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
   * <p/>
   * See the Android Training lesson <a href=
   * "http://developer.android.com/training/basics/fragments/communicating.html"
   * >Communicating with Other Fragments</a> for more information.
   */
  public interface OnFragmentInteractionListener {
    // TODO: Update argument type and name
    void onMembersFragmentInteraction();
  }
}

package com.stefan.jeremy.clubsoda;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/* Placeholder fragment for ClubActivity
 * Delete after member's list is finished (no need) */

public class BlankFragment extends Fragment {
  public BlankFragment() { /*Required empty public constructor */ }
  public static BlankFragment newInstance() {
    BlankFragment fragment = new BlankFragment();
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
    return inflater.inflate(R.layout.fragment_blank, container, false);
  }
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
  }
  @Override
  public void onDetach() {
    super.onDetach();
  }
}

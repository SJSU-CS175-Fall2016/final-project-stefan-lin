package com.stefan.jeremy.clubsoda.clubManagement;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.stefan.jeremy.clubsoda.R;

public class ClubManagementActivity extends AppCompatActivity
                                    implements ClubManagementFragment.OnFragmentInteractionListener{
  private String mCurrClubName;
  public ClubManagementActivity() {

  }
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_club_management);
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    Bundle b = getIntent().getExtras();
    mCurrClubName = (String) b.getSerializable("mCurrClubName");
    ft.replace(R.id.cmfHolder, ClubManagementFragment.newInstance(mCurrClubName)).commit();
  }

  @Override
  public void onClubManagementInteraction() {

  }
}

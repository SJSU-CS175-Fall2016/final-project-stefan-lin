package com.stefan.jeremy.clubsoda;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stefan.jeremy.clubsoda.announcement.AnnouncementFragment_v2;
import com.stefan.jeremy.clubsoda.clubManagement.ClubManagementActivity;
import com.stefan.jeremy.clubsoda.clubManagement.ClubManagementFragment;
import com.stefan.jeremy.clubsoda.data.User;
import com.stefan.jeremy.clubsoda.events.EventsFragment;
import com.stefan.jeremy.clubsoda.finances.FinancesFragment;
import com.stefan.jeremy.clubsoda.members.MembersFragment;
import com.stefan.jeremy.clubsoda.utils.Constants;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ClubActivity extends AppCompatActivity
                          implements ClubManagementFragment.OnFragmentInteractionListener,
                                     FinancesFragment.OnFragmentInteractionListener,
                                     AnnouncementFragment_v2.OnFragmentInteractionListener,
                                     EventsFragment.OnFragmentInteractionListener,
                                     MembersFragment.OnFragmentInteractionListener{

  //private Toolbar mToolbar;
  private ViewPager mViewPager;
  private TabLayout mTabLayout;
  private FirebaseAuth mAuth;
  private DatabaseReference mDatabase;

  private User mCurrUser;

  private TextView email_tv, name_tv;

  private String mCurrClubName;

  private TextView mTitlebarText;

  private ReentrantLock lock;
  private Condition foundCurrentClub;

  private int[] tabIcons = {
          R.drawable.ic_announcement_white_36dp,
          R.drawable.ic_event_white_36dp,
          R.drawable.ic_account_balance_wallet_white_36dp,
          R.drawable.ic_people_white_36dp
  };

  private ArrayList<String> clubNameList;

  public ClubActivity() {
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_club);

    /* Status bar color */
    Window window = getWindow();
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    window.setStatusBarColor(getResources().getColor(R.color.CyanDark));

    /* Firebase */
    mAuth = FirebaseAuth.getInstance();
    mDatabase = FirebaseDatabase.getInstance().getReference();

    /* Get all user data */
    Bundle b = getIntent().getExtras();
    mCurrUser = (User) b.getSerializable("mCurrUser");
    clubNameList = (ArrayList<String>) b.getSerializable("clubNameList");
    mCurrClubName = (String) b.getSerializable("mCurrClubName");

    /* Title bar */
    //mTitlebarText = (TextView) findViewById(R.id.title_bar_text);

    //mToolbar = (Toolbar) findViewById(R.id.toolbar);
    //mToolbar.inflateMenu(R.menu.toolbar_spinner_menu);

    setupToolbar();

    /* GUI Setup */
    createTabView();

    /* Club Setup */
    setupClub();
  }
  @Override
  protected void onSaveInstanceState(Bundle savedInstanceState) {
    savedInstanceState.putSerializable("mCurrUser", mCurrUser);
    savedInstanceState.putSerializable("mCurrClubName", mCurrClubName);
    savedInstanceState.putSerializable("clubNameList", clubNameList);
    super.onSaveInstanceState(savedInstanceState);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    mCurrUser = (User) savedInstanceState.getSerializable("mCurrUser");
    clubNameList = (ArrayList<String>) savedInstanceState.getSerializable("clubNameList");
    mCurrClubName = (String) savedInstanceState.getSerializable("mCurrClubName");
  }


  private void setupToolbar() {
    /*mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
          switch (item.getItemId()) {
            case R.id.add_user_menu:
              openClubManagement();
              return true;
            case R.id.logout_menu:
              navLogout();
              return true;
          }
          return true;
        }
    });*/
  }

  private void openClubManagement() {
    Intent intent = new Intent(this, ClubManagementActivity.class);
    Bundle b = new Bundle();
    b.putSerializable("mCurrClubName", mCurrClubName);
    intent.putExtras(b);
    startActivity(intent);
    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
  }

  /* Club setup helper functions */
  private void setupClub() {
    /* Fetch announcements data for clubName
       and display announcements fragment */
  }

  /* Check if last visited club is in last visited,
     if not, get a random club in club list and make that last visited.
   */
  private void getLastVisitedClub() {
    final String uid = mAuth.getCurrentUser().getUid();

    mDatabase.child("user-club-index").child(uid).child("last-visited").addListenerForSingleValueEvent(
            new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                lock.lock();
                if (dataSnapshot.exists()) {
                  mCurrClubName = (String) dataSnapshot.getValue();
                } else {
                  mCurrClubName = clubNameList.get(0);
                  mDatabase.child("user-club-index").child(uid).child("last-visited").setValue(mCurrClubName);
                }
                lock.unlock();
                // Can only setup toolbar AFTER this data is received.
                getUserInformation();
              }

              @Override
              public void onCancelled(DatabaseError databaseError) {
              }
            }
    );
  }

    private void getUserInformation() {
      String uid = mAuth.getCurrentUser().getUid();
      mDatabase.child("users").child(uid).addListenerForSingleValueEvent(
              new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                  String email = null;
                  String firstName= null;
                  String lastName = null;
                  for (DataSnapshot child : dataSnapshot.getChildren()) {
                    switch(dataSnapshot.getKey()) {
                      case "email":
                        email = (String) dataSnapshot.getValue();
                        break;
                      case "firstName":
                        firstName = (String) dataSnapshot.getValue();
                        break;
                      case "lastName":
                        lastName = (String) dataSnapshot.getValue();
                        break;
                    }
                  }
                  mCurrUser = new User(email, firstName, lastName, mCurrClubName);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
              }
      );
  }

  public User getCurrUser() {
    return mCurrUser;
  }
  private void getListOfClubs() {
    final String uid = mAuth.getCurrentUser().getUid();
    mDatabase.child("user-club-index").child(uid).child("clubs").addListenerForSingleValueEvent(
            new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                lock.lock();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                  String clubName = child.getKey();
                  clubNameList.add(clubName);
                }
                lock.unlock();
              }
              @Override
              public void onCancelled(DatabaseError databaseError) { }
            }
    );
  }

  private String[] page_titles = {
          Constants.ANNOUNCEMENTS_TITLE,
          Constants.EVENTS_TITLE,
          Constants.FINANCE_TITLE,
          Constants.MEMBERS_TITLE
  };

  private void createTabView() {
    mViewPager = (ViewPager) findViewById(R.id.viewpager);
    mViewPager.setAdapter(
            new ClubFragmentPagerAdapter(getSupportFragmentManager(), ClubActivity.this, mCurrUser));

    mTabLayout = (TabLayout) findViewById(R.id.tabs);
    mTabLayout.setupWithViewPager(mViewPager);


    mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override
      public void onPageSelected(int position) {
        TextView _title = (TextView) findViewById(R.id.title_bar_text);
        //_title.setText(page_titles[position]);
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });

    setupTabIcons();
  }

  private void setupTabIcons() {
    for (int i = 0; i < 4; i++) {
      mTabLayout.getTabAt(i).setIcon(tabIcons[i]);
    }
  }


  /* Nav helper functions */

  private void navLogout() {
    mAuth.signOut();
    Intent _intent = new Intent(this, LoginActivity.class);
    _intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(_intent);
    finish();
  }

  private void announcements() {
    //mTitlebarText.setText(Constants.ANNOUNCEMENTS_TITLE);
  }

  private void finances() {
    //mTitlebarText.setText(Constants.FINANCE_TITLE);
  }

  private void members() {
    //mTitlebarText.setText(Constants.MEMBERS_TITLE);
  }

  private void clubManagement() {
    mTitlebarText.setText(Constants.CLUB_MANAGEMENT_TITLE);
    FragmentManager manager = getSupportFragmentManager();
    FragmentTransaction ft = manager.beginTransaction();
    ClubManagementFragment cmf = (ClubManagementFragment) manager.findFragmentByTag("clubmanagement_fragment");
    if (cmf != null) {
      //ft.replace(R.id.body, cmf, "clubmanagement_fragment");
    } else {
      //ft.replace(R.id.body, ClubManagementFragment.newInstance(), "clubmanagement_fragment");
    }
    ft.addToBackStack("clubmangement_fragment");
    ft.commit();
  }

  private void settings() {
    mTitlebarText.setText(Constants.SETTINGS_TITLE);
  }

  private void events() {
    mTitlebarText.setText(Constants.EVENTS_TITLE);
  }



  /* Fragment Interaction Methods */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    return true;
  }
  @Override
  public void onFinancesFragmentInteraction() {
  }
  @Override
  public void onAnnouncementFragmentInteraction(){
  }
  @Override
  public void onEventsFragmentInteraction() {
  }
  public void onClubManagementInteraction() {
  }
  @Override
  public void onMembersFragmentInteraction() {
  }
}


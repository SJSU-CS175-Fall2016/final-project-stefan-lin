package com.stefan.jeremy.clubsoda;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.Toolbar;

import com.stefan.jeremy.clubsoda.announcement.AnnouncementFragment_temp;
import com.stefan.jeremy.clubsoda.announcement.AnnouncementFragment_v2;
import com.stefan.jeremy.clubsoda.clubManagement.ClubManagementFragment;
import com.stefan.jeremy.clubsoda.data.User;
import com.stefan.jeremy.clubsoda.events.EventsFragment;
import com.stefan.jeremy.clubsoda.finances.FinancesFragment;
import com.stefan.jeremy.clubsoda.members.MembersFragment;

public class ClubFragmentPagerAdapter extends FragmentPagerAdapter {
  final int PAGE_COUNT = 4;
  private Context context;
  private Toolbar mToolbar;
  private User mCurrUser;


  public ClubFragmentPagerAdapter(FragmentManager fm, Context context, User mCurrUser) {
    super(fm);
    this.mCurrUser = mCurrUser;
    this.context = context;
  }
  @Override
  public Fragment getItem(int position) {
    switch(position) {
      /* TODO: Firebase is not yet set up for Announcement */
      case 0:
        return AnnouncementFragment_v2.newInstance(mCurrUser);
      case 1:
        return EventsFragment.newInstance(mCurrUser);
      case 2:
        return FinancesFragment.newInstance(mCurrUser);
      case 3:
        return MembersFragment.newInstance(mCurrUser);
      default:
        return BlankFragment.newInstance();

    }
  }

  @Override
  public int getCount() {
    return PAGE_COUNT;
  }

  @Override
  public CharSequence getPageTitle(int position) {
    // Only want icons
    return null;
  }
}

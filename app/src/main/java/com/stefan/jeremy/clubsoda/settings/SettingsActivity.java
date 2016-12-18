package com.stefan.jeremy.clubsoda.settings;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.stefan.jeremy.clubsoda.LoginActivity;
import com.stefan.jeremy.clubsoda.R;
import com.stefan.jeremy.clubsoda.clubManagement.ClubManagementActivity;
import com.stefan.jeremy.clubsoda.data.User;
import com.stefan.jeremy.clubsoda.profilePic.ProfilePicActivity;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
  private ArrayList<String> mMenuItems;
  private TextView mTitle;
  private FirebaseAuth mAuth;
  private User mCurrUser;
  private Button mBackButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);
    mAuth = FirebaseAuth.getInstance();

    mMenuItems = new ArrayList<>();
    mMenuItems.add("Add user to club");
    mMenuItems.add("Add user avatar");
    mMenuItems.add("Logout");

    mCurrUser = (User) getIntent().getExtras().getSerializable("user");
    mBackButton = (Button) findViewById(R.id.post_button);
    mBackButton.setText("BACK");
    mBackButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });

    mTitle = (TextView) findViewById(R.id.title_bar_text);
    mTitle.setText("Settings");

    ListView mSettingsMenu = (ListView) findViewById(R.id.settings_listview);
    mSettingsMenu.setAdapter(new SettingsAdapter(this, mMenuItems));

    mSettingsMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        String itemName = (String) adapterView.getAdapter().getItem(position);
        switch (itemName) {
          case "Add user to club":
            startAddUserActivity();
            break;
          case "Add user avatar":
            startProfilePicActivity();
            break;
          case "Logout":
            logout();
            break;
          default:
            break;
        }
      }
    });
  }

  public class SettingsAdapter extends ArrayAdapter<String> {
    public SettingsAdapter(Context context, ArrayList<String> menuItems) {
      super(context, 0, menuItems);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      String _menuItem = getItem(position);
      if (convertView == null) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.settings_menu, parent, false);
      }
      TextView _menuTextView;
      _menuTextView = (TextView) convertView.findViewById(R.id.settings_section_title);
      _menuTextView.setText(_menuItem);
      return convertView;
    }
  }

  /* Menu methods */
  private void logout() {
    mAuth.signOut();
    Intent loginIntent = new Intent(this, LoginActivity.class);
    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(loginIntent);
  }
  private void startAddUserActivity() {
    Intent userIntent = new Intent(this, ClubManagementActivity.class);
    userIntent.putExtra("mCurrClubName", mCurrUser.currClub);
    startActivity(userIntent);
    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

  }
  private void startProfilePicActivity() {
    Intent profileIntent = new Intent(this, ProfilePicActivity.class);
    profileIntent.putExtra("user", mCurrUser);
    startActivity(profileIntent);
    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
  }
}

package com.stefan.jeremy.clubsoda.createClub;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stefan.jeremy.clubsoda.ClubActivity;
import com.stefan.jeremy.clubsoda.LoginActivity;
import com.stefan.jeremy.clubsoda.R;

public class CreateClubActivity extends AppCompatActivity
                                implements NoClubMembershipFragment.OnFragmentInteractionListener,
                                           newClubFormFragment.OnFragmentInteractionListener{

  private FirebaseAuth mAuth;
  private DatabaseReference mDatabase;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_club);
    mAuth = FirebaseAuth.getInstance();
    mDatabase = FirebaseDatabase.getInstance().getReference();


    // Inflate fragment that notifies user that he/she is not part of a club yet

    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.CreateClubHolder, NoClubMembershipFragment.newInstance());
    ft.commit();
  }

  @Override
  public void onNoClubMembershipFragment() {
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.add(R.id.CreateClubHolder, newClubFormFragment.newInstance());
    ft.commit();
  }

  @Override
  public void onCreateClubFragment(final String clubName, final String school, final String uid) {
    mDatabase.child("owners").child(clubName).addListenerForSingleValueEvent(
            new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                  Toast.makeText(getApplicationContext(),
                          "Club name already exists. Try another.",
                          Toast.LENGTH_LONG)
                          .show();
                } else {
                  // Create Club
                  createClub(clubName, school, uid);
                  switchToClubActivity(clubName);
                }
              }
              @Override
              public void onCancelled(DatabaseError databaseError) {}

            });



  }
  /* Helper functions */
  private void createClub(final String clubName, final String school, final String uid) {
    // Update user-club index
    mDatabase.child("user-club-index").child(uid).child("clubs").child(clubName).setValue(true);
    // Update last visited
    mDatabase.child("user-club-index").child(uid).child("last-visited").addListenerForSingleValueEvent(
            new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                  dataSnapshot.getRef().removeValue();
                }
                // Update with new last visited club
                mDatabase.child("user-club-index").child(uid).child("last-visited").setValue(clubName);
              }
              @Override
              public void onCancelled(DatabaseError databaseError) { }
            }
    );
    // Update owners
    mDatabase.child("owners").child(clubName).child(uid).setValue(true);
    // Update clubs
    mDatabase.child("clubs").child(clubName).child("school").setValue(school);
    // Create finance
    Double zero = 0.0;
    mDatabase.child("finances").child(clubName).child("current-balance").setValue(zero);
  }

  private void switchToClubActivity(String clubName) {
    Intent newIntent = new Intent(this, ClubActivity.class);
    newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
    newIntent.putExtra("clubName", clubName);
    startActivity(newIntent);
    overridePendingTransition(0,0);
    finish();
  }


  public void onSignOut() {
    mAuth.signOut();
    Intent clubIntent = new Intent(this, LoginActivity.class);
    clubIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
    startActivity(clubIntent);
    overridePendingTransition(0,0);
    finish();
  }
}

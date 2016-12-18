package com.stefan.jeremy.clubsoda;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.stefan.jeremy.clubsoda.utils.Constants;

/**
 * Created by stefanlin on 8/17/16.
 */

public class DetailActivity extends AppCompatActivity {
  /* Components */
  private int mParentFragmentType;

  @Override
  protected void onCreate(Bundle savedInstanceState){
    this.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
    super.onCreate(savedInstanceState);

    /* Retrieve extras from intent that passed in */
    Bundle extras = getIntent().getExtras();
    this.mParentFragmentType = extras.getInt(Constants.INTENT_FRAGMENT_TYPE);

    /* Determine which XML layout should be loaded */
    switch (mParentFragmentType){
      case Constants.ANNOUNCEMENT_TYPE:
        this.loadAnnouncement();
        break;
      case Constants.FINANCE_TYPE:
        setContentView(R.layout.activity_detail_finance);
        break;
    } // END SWITCH


  } // END METHOD onCreate

  private void loadAnnouncement(){
    setContentView(R.layout.activity_detail_announcement_v2);

    Bundle extras = getIntent().getExtras();
    /* {title, authorTime, content} */
    String[] strs = extras.getStringArray(Constants.INTENT_FRAGMENT_VALUE);
    //int  imgId = extras.getInt(Constants.INTENT_AVATAR_SRC_ID);
    CircularImageView circularImageView = (CircularImageView) findViewById(R.id.detail_avatar2);
    TextView title      = (TextView) findViewById(R.id.detail_title2);
    TextView content    = (TextView) findViewById(R.id.detail_content2);
    TextView authorTime = (TextView) findViewById(R.id.detail_author_and_time2);

    title.setText(strs[0]);
    authorTime.setText(strs[1]);
    content.setText(strs[2]);
    App.imageLoader.display(strs[3], circularImageView);
  } // END METHOD loadAnnouncement
}

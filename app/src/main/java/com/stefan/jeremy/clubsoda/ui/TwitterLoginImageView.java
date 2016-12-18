package com.stefan.jeremy.clubsoda.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import java.lang.ref.WeakReference;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.common.CommonUtils;

/**
 * Created by stefanlin on 8/13/16.
 */

public class TwitterLoginImageView extends ImageView {
  private static final String _TAG = TwitterLoginImageView.class.getSimpleName();

  private WeakReference<Activity>    _activityRef;
  private volatile TwitterAuthClient _authClient;
  private OnClickListener            _onClickListener;
  private Callback<TwitterSession>   _callback;

  public TwitterLoginImageView(Context context) {
    this(context, null);
  }

  public TwitterLoginImageView(Context context, AttributeSet attrs){
    this(context, attrs, 0);
  }

  public TwitterLoginImageView(Context context, AttributeSet attrs, int defStyle) {
    this(context, attrs, defStyle, null);
  }

  TwitterLoginImageView(Context context, AttributeSet attrs, int defStyle,
                        TwitterAuthClient authClient) {
    super(context, attrs, defStyle);
    this._activityRef = new WeakReference<>(getActivity());
    this._authClient = authClient;

    super.setOnClickListener(new TwitterLoginImageViewClickListener());

    checkTwitterCoreAndEnable();
  }

  protected Activity getActivity() {
    if (getContext() instanceof Activity) {
      return (Activity) getContext();
    }
    else if (isInEditMode()) {
      return null;
    }
    else {
      throw new IllegalStateException("[Error] No activity.");
    }
  }

  /**
   * @return the current {@link com.twitter.sdk.android.core.Callback}
   */
  public Callback<TwitterSession> getCallback() {
    return this._callback;
  }

  /**
   * Call this method when {@link android.app.Activity#onActivityResult(int, int, Intent)}
   * is called to complete the authorization flow.
   *
   * @param requestCode the request code used for SSO
   * @param resultCode the result code returned by the SSO activity
   * @param data the result data returned by the SSO activity
   */
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == getTwitterAuthClient().getRequestCode()) {
      getTwitterAuthClient().onActivityResult(requestCode, resultCode, data);
    }
  }

  @Override
  public void setOnClickListener(OnClickListener onClickListener) {
    this._onClickListener = onClickListener;
  }

  TwitterAuthClient getTwitterAuthClient() {
    if (this._authClient == null) {
      synchronized (TwitterLoginImageView.class) {
        if (this._authClient == null) {
          this._authClient = new TwitterAuthClient();
        }
      }
    }
    return _authClient;
  }

  private void checkTwitterCoreAndEnable() {
    //Default (Enabled) in edit mode
    if (isInEditMode()) {
      return;
    }
    try {
      TwitterCore.getInstance();
    }
    catch (IllegalStateException ex) {
      //Disable if TwitterCore hasn't started
      Fabric.getLogger().e(_TAG, ex.getMessage());
      setEnabled(false);
    }
  }


  private class TwitterLoginImageViewClickListener implements OnClickListener {
    @Override
    public void onClick(View view) {
      checkCallback(_callback);
      checkActivity(_activityRef.get());

      getTwitterAuthClient().authorize(_activityRef.get(), _callback);

      if (_onClickListener != null) {
        _onClickListener.onClick(view);
      }
    }

    private void checkCallback(Callback callback) {
      if (callback == null) {
        CommonUtils.logOrThrowIllegalStateException(TwitterCore.TAG,
          "[Error] Callback must not be null");
      }
    }

    private void checkActivity(Activity activity) {
      if (activity == null || activity.isFinishing()) {
        CommonUtils.logOrThrowIllegalStateException(TwitterCore.TAG,
          "[Error] No activity");
      }
    }
  } // END INNER CLASS
} // END CLASS

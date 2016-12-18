package com.stefan.jeremy.clubsoda.login;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.widget.Toast;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.TwitterAuthProvider;
import com.stefan.jeremy.clubsoda.R;
import com.stefan.jeremy.clubsoda.utils.Constants;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Target;

import io.fabric.sdk.android.Fabric;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class LoginFragment extends Fragment {
  private OnFragmentInteractionListener mListener;
  /* Components */
  private Button    mLoginButton;
  private EditText  mUsernameText;
  private EditText  mPasswordText;
  private TextView  mSignUpTextView;
  private ImageView mFacebookButton;
  private ImageView mTwitterButton;
  private Context   mContext;

  /* Twitter */
  private TwitterAuthClient twtAuthClient;

  /* Facebook Callbacks */
  private CallbackManager callbackManager;
  private FacebookCallback<LoginResult> mFacebookCallback;
  private LoginManager mLoginMgr;

  /* FirebaseAuth Ref */
  private FirebaseAuth mFirebaseAuth;

  public static LoginFragment newInstance() {
    LoginFragment fragment = new LoginFragment();
    return fragment;
  }
  public LoginFragment() { /* Empty constructor req for fragment */ }

  /* GUI */
  @Override
  public void onViewCreated(View view, Bundle savedInstanceState){
    /* Facebook */
    // Initialize the SDK before executing any other operations
    // MUST BE ABOVE setContentView() and instantiatComponents()
    FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
    //AppEventsLogger.activateApp(this);  // deprecated

    //intialize twitter sdk with fabric
    TwitterAuthConfig authConfig = new TwitterAuthConfig(
      Constants.TWITTER_KEY,
      Constants.TWITTER_SECRET
    );
    Fabric.with(getApplicationContext(), new Twitter(authConfig));

    instantiateComponents();
    createGUI();
    addActionListeners();
  }
  private void instantiateComponents(){
    mLoginButton    = (Button)    getView().findViewById(R.id.button1);
    mUsernameText   = (EditText)  getView().findViewById(R.id.editText1);
    mPasswordText   = (EditText)  getView().findViewById(R.id.editText2);
    mSignUpTextView = (TextView)  getView().findViewById(R.id.signup);
    mContext        = getActivity().getApplicationContext();

    mFacebookButton = (ImageView) getView().findViewById(R.id.facebookbutton);
    mFacebookButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        fb_login();
      }
    });

    mTwitterButton = (ImageView) getView().findViewById(R.id.twitterbutton);
    mTwitterButton.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View view){
        twt_login();
      }
    });

    /* Firebase */
    mFirebaseAuth = FirebaseAuth.getInstance();
  }
  private void createGUI(){ }

  private void addActionListeners(){
    /* Login Button */
    /* TODO: Login functionality, firebase stuff */
    mLoginButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        String email = mUsernameText.getText().toString();
        String password = mPasswordText.getText().toString();
        mListener.onLogin(email, password);
      }
    });

    mSignUpTextView.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.fragmentholder, SignUpFragment.newInstance());
        ft.addToBackStack(null).commit();
      }
    });
  }

  /* Fragment Methods */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_login, container, false);
  }

  @TargetApi(23)
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnFragmentInteractionListener) {
      mListener = (OnFragmentInteractionListener) context;
    }
    else {
      throw new RuntimeException(
        context.toString() + " must implement OnFragmentInteractionListener"
      );
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
    }
    else {
      throw new RuntimeException(
        context.toString() + " must implement OnFragmentInteractionListener"
      );
    }
  }


  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  /* Fragment-Activity interaction */
  public interface OnFragmentInteractionListener {
    public void onLogin(String email, String password);
  }


  /* Facebook */
  private void fb_login(){
    callbackManager = CallbackManager.Factory.create();
    //mFacebookButton.setBackgroundResource(R.drawable.fb);
    mLoginMgr = LoginManager.getInstance();

    // init FacebookCallback
    mFacebookCallback = new FacebookCallback<LoginResult>() {
      @Override
      public void onSuccess(final LoginResult loginResult) {
        Toast.makeText(
          mContext,
          "fb login success! :)",
          Toast.LENGTH_LONG
        ).show();
        // App code
        GraphRequest request = GraphRequest.newMeRequest(
          loginResult.getAccessToken(),
          new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(
              JSONObject object,
              GraphResponse response) {
              Log.d("Response",response.getJSONObject().toString());
              if (response.getError() != null) {
                // handle error
              } else {
                String email = object.optString("email");
                String full_name = object.optString("name");
                String fname = object.optString("first_name");
                String lname = object.optString("last_name");
                Log.e("Email",email);
                Log.e("fname",fname);
                Log.e("lname",lname);
                Log.e("full name", full_name);
                //     Log.d("Response", response.getInnerJsobject.toString());
                // l1EQFxapOBG0n+gE3CWOn13Gqk0=
                // ga0RGNYHvNM5d0SLGQfpQWAPGJ8= from mine

                fetchFacebookTokenToFirebase(loginResult.getAccessToken());
              }
            }
          });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday");
        request.setParameters(parameters);
        request.executeAsync();
      }
      @Override
      public void onCancel() {
        Toast.makeText(
          mContext,
          "fb login CANCEL! :)",
          Toast.LENGTH_LONG
        ).show();
      }
      @Override
      public void onError(FacebookException e) {
        Toast.makeText(
          mContext,
          "fb login ERROR! :)",
          Toast.LENGTH_LONG
        ).show();
      }
    };
    mLoginMgr.logInWithReadPermissions(this, Constants.FACEBOOK_PERMISSIONS);
    mLoginMgr.registerCallback(callbackManager, mFacebookCallback);
  }

  /* Facebook and Twitter requires */
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Log.e("TWT", "in onActivityResult()");
    if(callbackManager != null) {
      callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    if (twtAuthClient != null) {
      Log.e("TWT", "twtAuthClient not null in onActivityResult");
      twtAuthClient.onActivityResult(requestCode, resultCode, data);
    }
    return;
  }

  /* Twitter */
  private void twt_login(){
    if (twtAuthClient != null) {
      Log.e("TWT", "twtAuthClient != null");
      return;
    }
    Log.e("TWT", "twtAuthClient == null");
    twtAuthClient = new TwitterAuthClient();
    twtAuthClient.authorize(getActivity(), new Callback<TwitterSession>() {
      @Override
      public void success(Result<TwitterSession> result) {
        Log.e("TWT", "Twitter Login Success");
        Log.e("TWT", result.data.toString());
        final TwitterSession session = result.data;
        if (session != null) {
          //runOnUiThread(new Runnable() {
          //  @Override
          //  public void run() {
          //    doSomethingAwesomeWithTwitterToken(session);
          //  }
          //});
          String twtName = session.getUserName();
          String twtID   = String.valueOf(session.getUserId());
          Log.e("TWT", twtName);
          Log.e("TWT", twtID);

          fetchTwitterSessionToFirebase(session);  ////////////////
        }
        else{
          Log.e("TWT", "session == null");
        }
      }

      @Override
      public void failure(TwitterException exception) {
        twtAuthClient = null;
        // Handle exception
        Log.e("TWT LOGIN", exception.toString());
      }
    });
  } // END twt_login METHOD

  private void fetchTwitterSessionToFirebase(TwitterSession session){
    AuthCredential credential = TwitterAuthProvider.getCredential(
      session.getAuthToken().token,
      session.getAuthToken().secret);

    mFirebaseAuth.signInWithCredential(credential)
      .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
          Log.d("TWT", "signInWithCredential:onComplete:" + task.isSuccessful());

          // If sign in fails, display a message to the user. If sign in succeeds
          // the auth state listener will be notified and logic to handle the
          // signed in user can be handled in the listener.
          if (!task.isSuccessful()) {
            Log.w("TWT", "signInWithCredential", task.getException());
            Toast.makeText(mContext, "Authentication failed.",
              Toast.LENGTH_SHORT).show();
          }
        }
      });
  } // END fetchTwitterSessionToFirebase METHOD

  private void fetchFacebookTokenToFirebase(AccessToken token) {
    Log.d("FB", "handleFacebookAccessToken:" + token);
    AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
    mFirebaseAuth.signInWithCredential(credential)
      .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
          Log.d("FB", "signInWithCredential:onComplete:" + task.isSuccessful());

          // If sign in fails, display a message to the user. If sign in succeeds
          // the auth state listener will be notified and logic to handle the
          // signed in user can be handled in the listener.
          if (!task.isSuccessful()) {
            Log.w("FB", "signInWithCredential", task.getException());
            Toast.makeText(mContext, "Authentication failed.",
              Toast.LENGTH_SHORT).show();
          }
        }
      });
  }
}

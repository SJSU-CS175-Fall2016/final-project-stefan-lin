package com.stefan.jeremy.clubsoda.login;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.stefan.jeremy.clubsoda.R;

public class SignUpFragment extends Fragment {
  private OnFragmentInteractionListener mListener;
  private EditText mEmailText, mPasswordText, mFirstNameText, mLastNameText,
                   mConfirmText;
  private Button   mSignUpButton;
  public SignUpFragment() { }

  public static SignUpFragment newInstance() {
    SignUpFragment fragment = new SignUpFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState){
   instantiateComponents();
   createGUI();
   addActionListeners();
  }

  private void instantiateComponents(){
    mFirstNameText = (EditText) getView().findViewById(R.id.firstNameSignUp);
    mLastNameText  = (EditText) getView().findViewById(R.id.lastNameSignUp);
    mEmailText     = (EditText) getView().findViewById(R.id.emailTextSignUp);
    mPasswordText  = (EditText) getView().findViewById(R.id.passwordTextSignUp);
    mConfirmText   = (EditText) getView().findViewById(R.id.passwordConfirmTextSignUp);
    mSignUpButton  = (Button)   getView().findViewById(R.id.buttonSignUp);
  }

  private void createGUI(){ }

  private void addActionListeners(){
    mSignUpButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        String password = mPasswordText.getText().toString();
        String confirm = mConfirmText.getText().toString();
        if (!confirm.equals(password)) {
          Toast.makeText(getActivity().getApplicationContext(),
                         "Passwords do not match!",
                         Toast.LENGTH_LONG)
                         .show();
          return;
        }
        String email = mEmailText.getText().toString();
        String firstName = mFirstNameText.getText().toString();
        String lastName = mLastNameText.getText().toString();

        mListener.onSignUpComplete(email, password, firstName, lastName);
      }
    });

    /* Mismatch passwords */
    mConfirmText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
            actionId == EditorInfo.IME_ACTION_DONE   ||
            event.getAction() == KeyEvent.ACTION_DOWN &&
            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
          String p = mPasswordText.getText().toString();
          String c = mConfirmText.getText().toString();
          if (!c.equals(p)) {
            badMatchPassToast();
            return true;
          }
        }
        return false; // pass on to other listeners
      }
    });
  }

  private void badMatchPassToast() {
      Toast.makeText(getActivity().getApplicationContext(),
                         "Passwords do not match!",
                         Toast.LENGTH_SHORT)
                         .show();
  }
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_sign_up, container, false);
  }

  @TargetApi(23)
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    Log.e("Stefan", "SignUpFragment.onAttach()");
    if (context instanceof OnFragmentInteractionListener) {
      mListener = (OnFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString()
        + " must implement OnFragmentInteractionListener");
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

  /* For container activity */
  public interface OnFragmentInteractionListener {
    void onSignUpComplete(String email, String password, String firstName, String lastName);
  }
}

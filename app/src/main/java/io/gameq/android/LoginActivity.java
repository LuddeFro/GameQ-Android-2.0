package io.gameq.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private Button mTopButton;
    private Button mBottomButton;
    private Button mForgotButton;

    private View mProgressView;
    private View mLoginFormView;

    private Button mResignKeyboardButton;

    private boolean bolRegistering = false;
    private boolean bolReportingForgottenPassword = false;

    static final String TAG = "GameQ-Login";
    public final Activity myself = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConnectionHandler.instantiateDataModel(getApplicationContext());
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mConfirmPasswordView = (EditText) findViewById(R.id.confirm_password);
        mTopButton = (Button) findViewById(R.id.login_button_top);
        mBottomButton = (Button) findViewById(R.id.login_button_bottom);
        mForgotButton = (Button) findViewById(R.id.login_button_forgot);
        mPasswordView = (EditText) findViewById(R.id.password);
        mConfirmPasswordView.setEnabled(false);
        mLoginFormView = findViewById(R.id.email_login_form);
        mResignKeyboardButton = (Button) findViewById(R.id.btn_resign_keyboard);
        mTopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               pressedTopButton();
            }
        });

        mBottomButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pressedBottomButton();
            }
        });

        mForgotButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pressedForgotButton();
            }
        });

        mResignKeyboardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pressedResignKeyboard();
            }
        });


        mEmailView.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        mPasswordView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mConfirmPasswordView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        mEmailView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    if (!bolReportingForgottenPassword) {
                        mPasswordView.requestFocus();
                    } else {
                        pressedTopButton();
                    }
                    return true;
                }
                return false;
            }
        });

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    if (bolRegistering) {
                        if (textView.getText().length() > 5) {
                            mConfirmPasswordView.requestFocus();
                        } else {
                            mPasswordView.setError(getString(R.string.invalid_password));
                            mPasswordView.requestFocus();
                        }
                    } else {
                        //signing in
                        if (textView.getText().length() > 5) {
                            pressedTopButton();
                        } else {
                            mPasswordView.setError(getString(R.string.invalid_password));
                            mPasswordView.requestFocus();
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        mConfirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    //signing up
                    if (textView.getText().length() > 5) {
                        pressedTopButton();
                    } else {
                        mConfirmPasswordView.setError(getString(R.string.invalid_password));
                        mConfirmPasswordView.requestFocus();
                    }
                    return true;
                }
                return false;
            }
        });



        mProgressView = findViewById(R.id.login_progress);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptConnect() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String cPassword = mConfirmPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!bolReportingForgottenPassword) {
            // Check for a valid password, if the user entered one.
            if (!isPasswordValid(password)) {
                mPasswordView.setError(getString(R.string.error_invalid_password));
                focusView = mPasswordView;
                cancel = true;
                mPasswordView.setText("");
            }
        }


        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (bolRegistering) {
            if (!password.equals(cPassword)) {
                mConfirmPasswordView.setError(getString(R.string.error_password_mismatch));
                focusView = mPasswordView;
                mPasswordView.setText("");
                mConfirmPasswordView.setText("");
                cancel = true;
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            disableAll();
            if (bolRegistering) {
                ConnectionHandler.register(new RegisterHandler(), email, password);
            } else if (bolReportingForgottenPassword) {
                ConnectionHandler.submitForgotPassword(new ForgotHandler(), email);
            } else {
                ConnectionHandler.login(new LoginHandler(), email, password);
            }


        }
    }

    private void enableAll() {
        mTopButton.setEnabled(true);
        mBottomButton.setEnabled(true);
        mForgotButton.setEnabled(true);
        mEmailView.setEnabled(true);
        mPasswordView.setEnabled(true);
        mConfirmPasswordView.setEnabled(false);
        if (bolRegistering) {
            mForgotButton.setEnabled(false);
            mConfirmPasswordView.setEnabled(true);
        } else if (bolReportingForgottenPassword){
            mPasswordView.setEnabled(false);
            mForgotButton.setEnabled(false);
        }

    }

    private void disableAll() {
        mTopButton.setEnabled(false);
        mBottomButton.setEnabled(false);
        mForgotButton.setEnabled(false);
        mEmailView.setEnabled(false);
        mPasswordView.setEnabled(false);
        mConfirmPasswordView.setEnabled(false);
    }

    private boolean isEmailValid(String email) {
        return email.matches("^[a-z0-9._%+\\-]+@[a-z0-9.\\-]+\\.[a-z]{2,4}$");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }








    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }




    private void pressedTopButton() {
        attemptConnect();
    }

    private void pressedResignKeyboard() {
        System.out.println("pressed resign keyboard");
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void pressedBottomButton() {
        if (bolRegistering) {
            bolRegistering = false;
            bolReportingForgottenPassword = false;
            hideSignUp();
        } else if (bolReportingForgottenPassword) {
            bolRegistering = false;
            bolReportingForgottenPassword = false;
            hideForgotPassword();
        } else { // logging in
            bolRegistering = true;
            bolReportingForgottenPassword = false;
            showSignUp();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            if (bolRegistering) {
                pressedBottomButton();
                return true;
            } else if (bolReportingForgottenPassword) {
                pressedBottomButton();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void pressedForgotButton() {
        bolRegistering = false;
        bolReportingForgottenPassword = true;
        showForgotPassword();
    }

    private void showForgotPassword() {
        this.alphaAnimate(mForgotButton, false);
        this.alphaAnimate(mPasswordView, false);
        mTopButton.setText(getString(R.string.action_submit_email));
        mBottomButton.setText(getString(R.string.back));
        mEmailView.setImeOptions(EditorInfo.IME_ACTION_DONE);
    }

    private void alphaAnimate(final View view, boolean show) {
        final float from;
        final float to;
        if (show) {
            from = 0.0f;
            to = 1.0f;
            view.setEnabled(true);
        } else { //hide
            from = 1.0f;
            to = 0.0f;
            view.setEnabled(false);
        }

        AlphaAnimation anim = new AlphaAnimation(from, to);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setAlpha(1.0f);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setAlpha(to);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        anim.setDuration(1000);
        view.startAnimation(anim);
    }

    private void hideForgotPassword() {

        mPasswordView.setText("");
        this.alphaAnimate(mForgotButton, true);
        this.alphaAnimate(mPasswordView, true);
        mTopButton.setText(getString(R.string.action_sign_in));
        mBottomButton.setText(getString(R.string.action_register));
        mEmailView.setImeOptions(EditorInfo.IME_ACTION_NEXT);
    }

    private void showSignUp() {
        this.alphaAnimate(mConfirmPasswordView, true);
        this.alphaAnimate(mForgotButton, false);
        mConfirmPasswordView.setText("");
        mTopButton.setText(getString(R.string.action_register));
        mBottomButton.setText(getString(R.string.back));
        mPasswordView.setImeOptions(EditorInfo.IME_ACTION_NEXT);
    }

    private void hideSignUp() {
        this.alphaAnimate(mForgotButton, true);
        this.alphaAnimate(mConfirmPasswordView, false);
        mPasswordView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mTopButton.setText(getString(R.string.action_sign_in));
        mBottomButton.setText(getString(R.string.action_register));
    }



    public class LoginHandler implements CallbackGeneral {
        @Override
        public void callback(final boolean success, final String error) {
            myself.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    enableAll();
                    showProgress(false);
                    if (success) {
                        Log.d("UI thread", "successful");

                        Intent intent = new Intent(myself, MainActivity.class);
                        intent.putExtra(getString(R.string.intent_inhouse_extra), true);
                        startActivity(intent);
                    } else {
                        Log.d("UI thread", "unsuccessful");

                        mPasswordView.setText("");
                        mPasswordView.setError(error);
                        Log.i(TAG, error);
                        System.out.println(error);
                    }
                }
            });


        }
    }
    public class RegisterHandler implements CallbackGeneral {
        @Override
        public void callback(final boolean success, final String error) {
            myself.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    enableAll();
                    showProgress(false);
                    if (success) {
                        Intent intent = new Intent(myself, MainActivity.class);
                        intent.putExtra(getString(R.string.intent_inhouse_extra), true);
                        startActivity(intent);
                    } else {
                        mPasswordView.setText("");
                        mPasswordView.setError(error);
                        Log.i(TAG, error);
                        System.out.println(error);
                    }
                }
            });


        }
    }

    public class ForgotHandler implements CallbackGeneral {
        @Override
        public void callback(final boolean success, final String error) {
            myself.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    enableAll();
                    showProgress(false);
                    if (success) {
                        hideForgotPassword();
                        new AlertDialog.Builder(myself)
                                .setTitle("Password Reset")
                                .setMessage("A new password has been sent to your email.")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .show();
                    } else {
                        mEmailView.setText("");
                        mEmailView.setError(error);
                        Log.i(TAG, error);
                        System.out.println(error);
                    }
                }
            });


        }
    }





}


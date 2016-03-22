package com.example.sid.campusconnect;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.sid.campusconnect.ForgotPassword.ForgotPassword;
import com.example.sid.campusconnect.Parse_Session.SessionChecker;
import com.example.sid.campusconnect.Signup.SignUpAdmin;
import com.example.sid.campusconnect.Signup.SignupStudent;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;

import static com.parse.ParseUser.logInInBackground;



public class MainActivity extends AppCompatActivity
{
    public  EditText usernameView;
    public EditText passwordView;
    final Context context = this;
    public static String password;
    public String username1;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameView = (EditText) findViewById(R.id.LogName);
        passwordView = (EditText) findViewById(R.id.LogPassword);

        //fetching username of the user registered using this device.
        //note: parse does nt allow fetching password..since it is hashed.
        //it can be overcomed by using shared preferences..but it would create a serious security flaw..
        //so not implemented.

        ParseObject registered_user= ParseInstallation.getCurrentInstallation().getParseObject("user");
        try {
             String username = registered_user.fetchIfNeeded().getString("username");
             usernameView.setText(username);
        } catch (ParseException e) {
            e.printStackTrace();
        }

            // setting up sign in intent
        findViewById(R.id.SignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.popup_signup);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                ImageButton istud = (ImageButton) dialog.findViewById(R.id.PStud);
                ImageButton istaff = (ImageButton) dialog.findViewById(R.id.PStaff);

                dialog.show();

                istud.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this,SignupStudent.class));
                    }
                });

                istaff.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, SignUpAdmin.class));
                    }
                });

            }

        });

        findViewById(R.id.ForgotPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ForgotPassword.class));
            }
        });


        // Set up the submit button click handler
        findViewById(R.id.Login).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {


                // Validate the log in data
                boolean validationError = false;
                StringBuilder validationErrorMessage =
                        new StringBuilder(getResources().getString(R.string.error_intro));

                //empty username field

                if (isEmpty(usernameView)) {
                    validationError = true;
                    validationErrorMessage.append(getResources().getString(R.string.error_blank_username));
                }


                // empty password field

                if (isEmpty(passwordView)) {
                    if (validationError) {
                        validationErrorMessage.append(getResources().getString(R.string.error_join));
                    }
                    validationError = true;
                    validationErrorMessage.append(getResources().getString(R.string.error_blank_password));
                }
                validationErrorMessage.append(getResources().getString(R.string.error_end));

                // If there is a validation error, display the error
                if (validationError) {
                    Toast.makeText(MainActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                //  progress dialog
                final ProgressDialog dlg = new ProgressDialog(MainActivity.this);
                dlg.setTitle("Please wait.");
                dlg.setMessage("Logging in.  Please wait.");
                dlg.show();

                 // Calling the Parse login method
                    ParseUser.logInInBackground(usernameView.getText().toString(), passwordView.getText().toString(), new LogInCallback() {
                        boolean emailVerified,studVerified,complete;

                        @Override
                        public void done(ParseUser user, ParseException e) {

                            username1=usernameView.getText().toString();
                            password=passwordView.getText().toString();
                            dlg.dismiss();

                            if (e != null)
                            {
                                // Show the error message
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                // Start an intent for the dispatch activity
                                emailVerified = user.getBoolean("emailVerified");
                                studVerified  =user.getBoolean("Is_Verified");
                                complete = user.getBoolean("isComplete");

                                Intent intent = new Intent(MainActivity.this, SessionChecker.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);


                              /*  if((emailVerified == true)&&(studVerified==false)&&(complete==false))
                                {
                                    startActivity(new Intent(MainActivity.this,SignUpDetails.class));
                                }

                                if (emailVerified == true) {
                                    //Toast.makeText(MainActivity.this, "Email Yes!", Toast.LENGTH_LONG).show();
                                        if(studVerified==true) {

                                            if (complete == true) {
                                               // Toast.makeText(MainActivity.this, "Complete!", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(MainActivity.this, Home.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }
                                            else {
                                                startActivity(new Intent(MainActivity.this,SignUpDetails.class));
                                            }

                                        }
                                        else {
                                            Toast.makeText(MainActivity.this, "Student Verification pending!", Toast.LENGTH_LONG).show();
                                            //ParseUser.getCurrentUser().logOutInBackground();
                                        }

                                } else {
                                    startActivity(new Intent(MainActivity.this, EmailReverify.class));
                                }*/
                            }

                        }


                    });
            }
        });


        }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }
}



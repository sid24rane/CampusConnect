package com.example.sid.campusconnect.Parse_Session;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.sid.campusconnect.Home.Home;
import com.example.sid.campusconnect.MainActivity;
import com.example.sid.campusconnect.Signup.SignUpDetails;
import com.parse.ParseUser;

/**
 * Created by Sid on 28-Aug-15.
 */
public class SessionChecker extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Check if there is current user info
        if (ParseUser.getCurrentUser() != null) {
            // Start an intent for the logged in activity
            ParseUser user = ParseUser.getCurrentUser();
            if(user.getBoolean("isComplete")==true) {

                if (user.getBoolean("Is_Verified") == false) {
                    user.logOut();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("You are not yet Verified by Staff. Please Try Logging in Later Again Later!")
                            .setTitle("Verifcation Pending!")
                            .setCancelable(false)
                            .setNeutralButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(SessionChecker.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else {
                    int bpoints = (int) user.get("Bad_Points");
                    if (bpoints < 150) {
                        startActivity(new Intent(SessionChecker.this, Home.class));
                    }
                    else if(bpoints==9999)
                    {
                        user.logOut();
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("You are permanently banned from the Campus Connect bitch!!!")
                                .setTitle("Login Error!")
                                .setCancelable(false)
                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(SessionChecker.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                    else {
                        user.logOut();
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("You are temporarily banned from the Campus Connect! Visit your staff & say sorry :P")
                                .setTitle("Login Error!")
                                .setCancelable(false)
                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(SessionChecker.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }

            }
            else
            {
                startActivity(new Intent(SessionChecker.this, SignUpDetails.class));
            }

        } else {
            // Start and intent for the logged out activity
            startActivity(new Intent(SessionChecker.this, MainActivity.class));
        }
    }
}

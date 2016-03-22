package com.example.sid.campusconnect.Signup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.sid.campusconnect.Home.Home;
import com.example.sid.campusconnect.R;
import com.example.sid.campusconnect.Parse_Session.SessionChecker;
import com.parse.ParseUser;

public class Proceed extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proceed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (ParseUser.getCurrentUser().getBoolean("Is_Verified") == true) {
            startActivity(new Intent(Proceed.this, Home.class));
        }
        else{
            ParseUser.getCurrentUser().logOutInBackground();
            startActivity(new Intent(Proceed.this, SessionChecker.class));
        }
    }

}

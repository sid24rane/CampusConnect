package com.example.sid.campusconnect.EmailReverify;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.sid.campusconnect.MainActivity;
import com.example.sid.campusconnect.R;
import com.example.sid.campusconnect.Parse_Session.SessionChecker;
import com.parse.ParseException;
import com.parse.ParseUser;

public class EmailReverify extends MainActivity {
    private String email;
    private String username;
    private String passkey,object_id,usid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_reverify);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username=ParseUser.getCurrentUser().getUsername();
        email=ParseUser.getCurrentUser().getEmail();
        object_id=ParseUser.getCurrentUser().getObjectId();
        passkey = MainActivity.password;

        findViewById(R.id.btn_Resend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            ParseUser user= ParseUser.getCurrentUser();
            user.setEmail("");
                try
                {
                    user.save();
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }

				final ProgressDialog dlg = new ProgressDialog(EmailReverify.this);
                dlg.setTitle("Please wait.");
                dlg.setMessage("Sending email.  Please wait.");
                dlg.show();

                user.setEmail(email);
                try
                {
                    user.save();
                    Toast.makeText(EmailReverify.this, "Email for account verification has been sent to "+email, Toast.LENGTH_LONG).show();
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }
				logout();

            }


        });


    }

    @Override
    public void onDestroy(){
        super.onDestroy();
          logout();
            }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_email_reverify, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop(){
        super.onStop();
        logout();
    }

    public void logout()
    {
        ParseUser.getCurrentUser().logOut();

    }

    public void logout1()
    {
        ParseUser.getCurrentUser().logOut();
        Intent intent = new Intent(EmailReverify.this,SessionChecker.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

}
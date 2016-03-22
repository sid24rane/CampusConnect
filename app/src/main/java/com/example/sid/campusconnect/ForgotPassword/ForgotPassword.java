package com.example.sid.campusconnect.ForgotPassword;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.example.sid.campusconnect.R;
import com.parse.ParseException;
//import java.text.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import java.util.regex.Pattern;


public class ForgotPassword extends AppCompatActivity {

    String user_email;
    protected EditText abc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText abc = (EditText) findViewById(R.id.user_name);

        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account ac : accounts)
        {
            if (emailPattern.matcher(ac.name).matches()) {
                user_email = ac.name;
            }
        }
        abc.setText(user_email);

        //Submit Button Code
        findViewById(R.id.Submit_forgot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username;
                username =abc.getText().toString();

                ParseUser.requestPasswordResetInBackground(username, new RequestPasswordResetCallback() {


                    @Override

                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(ForgotPassword.this, "Done", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ForgotPassword.this, "Undone", Toast.LENGTH_LONG).show();
                        }

                    }

                });
            }
        });

    }

            public boolean onCreateOptionsMenu(Menu menu) {
                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.menu_forgot_password, menu);
                return true;


            }



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



        }


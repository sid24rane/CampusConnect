package com.example.sid.campusconnect.ViewUpvotes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.Question.QuestionDetail;
import com.example.sid.campusconnect.R;
import com.example.sid.campusconnect.UserProfile.UserProfile;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ViewUpvotes extends AppCompatActivity
{
    ParseObject qs;
   LinearLayout container;
    String img_url;
    ScrollView scrollwrapper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_upvotes);

        container=(LinearLayout)findViewById(R.id.lists);


        //Receiving question_id through intent
        Intent intent = getIntent();
        final String question_id = intent.getStringExtra("question_id");

       // Toast toast = Toast.makeText(getApplicationContext(),question_id,Toast.LENGTH_LONG);
        //toast.show();

        // loading
        final ProgressDialog qsupvoteloader = new ProgressDialog(ViewUpvotes.this);
        qsupvoteloader.setTitle("Please wait.");
        qsupvoteloader.setMessage("Loading List of Users..Please Wait");
        qsupvoteloader.show();

        ParseUser current_user = ParseUser.getCurrentUser();
        if (current_user != null)
        {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Question");
            query.getInBackground(question_id, new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        qs = object;
                        ParseQuery<ParseObject> innerquery = ParseQuery.getQuery("Qs_Upvote");
                        innerquery.whereEqualTo("Qs_Id", qs);
                        innerquery.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> qsList, ParseException e) {
                                if (e == null) {
                                    if(qsList.size()==0)
                                    {
                                        Toast toast = Toast.makeText(getApplicationContext(),"No Upvotes !",Toast.LENGTH_LONG);
                                        toast.show();
                                        // title
                                        TextView tds = new TextView(ViewUpvotes.this);
                                        tds.setText("The Question has No Upvotes...Redirecting you to Question Details");
                                        tds.setTextColor(Color.rgb(51, 204, 51));
                                        container.addView(tds);

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent intent = new Intent(ViewUpvotes.this,QuestionDetail.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.putExtra("question_id",question_id);
                                                startActivity(intent);
                                            }
                                        }, 5000);
                                    }
                                    else {

                                        for (ParseObject product : qsList) {
                                            ParseUser users = (ParseUser) product.get("User_Id");
                                            try {
                                                String s = users.fetchIfNeeded().getObjectId();

                                                ParseQuery<ParseObject> querys = ParseQuery.getQuery("_User");
                                                querys.getInBackground(s, new GetCallback<ParseObject>() {
                                                    public void done(ParseObject object, ParseException e) {
                                                        if (e == null) {
                                                            // Rohan
                                                            // improve the ui
                                                            // make it look like --> student_verify_list.xml
                                                            // it should look exactly like it
                                                            // sab data hai..just usse properly display kar.

                                                            String username = object.getString("Name");
                                                            String dept = object.getString("Dept");
                                                            final String user_id = object.getObjectId();

                                                            // title
                                                            TextView td = new TextView(ViewUpvotes.this);
                                                            td.setText("User Name :");
                                                            td.setTextColor(Color.rgb(51, 204, 51));

                                                            // username
                                                            TextView t = new TextView(ViewUpvotes.this);
                                                            t.setText(username);

                                                            // button
                                                            Button profile = new Button(ViewUpvotes.this);
                                                            profile.setText("Visit User Profile");
                                                            profile.setClickable(true);
                                                            // profile.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT));
                                                            profile.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {

                                                                    Intent intent = new Intent(ViewUpvotes.this, UserProfile.class);
                                                                    //NOTE: THE MOST IMP STEP ==> PASSING USERID THROUGH INTENT TO NEXT ACTIVITY
                                                                    intent.putExtra("user_id", user_id);
                                                                    startActivity(intent);
                                                                }
                                                            });

                                                            // for testing whether the scrollview actually works or not
                                                        /*for(int i = 0; i < 20; i++)
                                                        {
                                                            Button b = new Button(ViewUpvotes.this);
                                                            b.setText("Button "+i);
                                                            container.addView(b);
                                                        }

                                                        */

                                                            container.addView(td);
                                                            container.addView(t);
                                                            container.addView(profile);

                                                        } else {
                                                            // something went wrong
                                                            Log.d("Inner query error is : ", "Error: " + e.getMessage());
                                                        }
                                                    }
                                                });
                                                // Toast toast1 = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG);
                                                //toast1.show();
                                            } catch (ParseException e1) {
                                                e1.printStackTrace();
                                            }
                                        }
                                    }
                                    qsupvoteloader.dismiss();

                                } else {
                                    Log.d("Inner query error is : ", "Error: " + e.getMessage());
                                }
                            }
                        });
                    } else {
                        Log.d("Error is : ", e.getMessage());
                    }
                }
            });

        } else {
            Intent intents = new Intent(ViewUpvotes.this,QuestionDetail.class);
            intents.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intents);
        }

    }

}


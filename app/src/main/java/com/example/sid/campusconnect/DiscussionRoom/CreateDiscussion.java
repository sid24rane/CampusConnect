package com.example.sid.campusconnect.DiscussionRoom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sid.campusconnect.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class CreateDiscussion extends AppCompatActivity {

    protected TextView qstopic;
    protected TextView qsdes;
    protected TextView qscategory;
    protected TextView openedby;
    protected Button addmembers;
    String title;
    ParseUser current_user = ParseUser.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_discussion);

        qstopic=(TextView)findViewById(R.id.QsTopic);
        qsdes=(TextView)findViewById(R.id.QsTopicDescription);
        qscategory=(TextView)findViewById(R.id.QsTopicCategory);
        openedby=(TextView)findViewById(R.id.QsTopicOpenedby);
        addmembers=(Button)findViewById(R.id.addmembers);

        //Receiving question_id through intent
        Intent intent = getIntent();
        final String question_id = intent.getStringExtra("question_id");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Question");
        query.getInBackground(question_id, new GetCallback<ParseObject>() {
            public void done(ParseObject question, ParseException e) {
                if (e == null)
                {
                    //getting title
                    String qstitle = question.getString("Title");
                    title=qstitle;
                    //description
                    String qsdess = question.getString("Description");

                    //category
                    final String cate = question.getString("Category");

                    //user
                    ParseUser current_user = ParseUser.getCurrentUser();
                    String username = current_user.getString("Name");

                    //userid
                    String userid = current_user.getObjectId();

                    //setting values
                    qstopic.setText(qstitle);
                    qsdes.setText(qsdess);
                    qscategory.setText(cate);
                    openedby.setText(username);
                }
                else
                {
                    Log.d("Error is : ",e.getMessage());
                }
            }
        });

        addmembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // loading
                final ProgressDialog sendqs = new ProgressDialog(CreateDiscussion.this);
                sendqs.setTitle("Please wait.");
                sendqs.setMessage("Setting up a Discussion..!");
                sendqs.show();

                ParseQuery<ParseObject> query = ParseQuery.getQuery("Question");
                query.getInBackground(question_id, new GetCallback<ParseObject>() {
                    public void done(ParseObject question, ParseException e) {
                        if (e == null) {
                            question.put("Is_Discuss", true);
                            question.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        final ParseObject newdis = new ParseObject("Discuss_Room");
                                        newdis.put("Subject", title);
                                        newdis.put("Status", true);
                                        newdis.put("Opened_By", current_user);
                                        newdis.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    String id = newdis.getObjectId();
                                                    sendqs.dismiss();
                                                    Intent intent = new Intent(CreateDiscussion.this, AddDiscussionMember.class);
                                                    intent.putExtra("discussion_id", id);
                                                    startActivity(intent);
                                                } else {
                                                    Log.d("Error is : ", e.getMessage());
                                                }
                                            }
                                        });
                                    } else {
                                        Log.d("Error is : ", e.getMessage());
                                    }
                                }
                            });
                        } else {
                            Log.d("Error is : ", e.getMessage());
                        }
                    }
                });

            }
        });
    }
}

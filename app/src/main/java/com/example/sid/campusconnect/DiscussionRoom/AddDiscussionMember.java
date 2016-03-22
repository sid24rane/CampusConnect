package com.example.sid.campusconnect.DiscussionRoom;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.Home.Home;
import com.example.sid.campusconnect.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class AddDiscussionMember extends ListActivity {

    protected List<ParseObject> mStatus;
    protected Button addmembers;
    ArrayList<String> selectedstrings = new ArrayList<String>();
    ParseObject dis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_discussion_member);

        //Receiving discussion_id through intent
        Intent intent = getIntent();
        final String discussion_id = intent.getStringExtra("discussion_id");

        // getting discussion object
        ParseQuery<ParseObject> querys = ParseQuery.getQuery("Discuss_Room");
        querys.getInBackground(discussion_id, new GetCallback<ParseObject>() {
            public void done(ParseObject discussion_room, ParseException e) {
                if (e == null)
                {
                   dis = discussion_room;
                }
                else
                {
                    Log.d("Error is : ",e.getMessage());
                }
            }
        });

        addmembers=(Button)findViewById(R.id.addmem);
        addmembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // loading
                final ProgressDialog sendqs = new ProgressDialog(AddDiscussionMember.this);
                sendqs.setTitle("Please wait.");
                sendqs.setMessage("Adding Members..!");
                sendqs.show();

                int size = selectedstrings.size();
                if(size==0)
                {
                    sendqs.dismiss();
                    Toast toast = Toast.makeText(getApplicationContext(),"No members Selected!",Toast.LENGTH_LONG);
                    toast.show();
                }
                else
                {
                    for (String users : selectedstrings)
                    {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
                        query.getInBackground(users, new GetCallback<ParseObject>() {
                            public void done(ParseObject usersid, ParseException e) {
                                if (e == null)
                                {
                                    // adding all the selected dis members
                                    ParseObject dismem = new ParseObject("Dis_Member");
                                    dismem.put("User_Id",usersid);
                                    dismem.put("Dis_Id",dis);
                                    dismem.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null)
                                            {
                                                //success
                                            }
                                            else
                                            {
                                                Log.d("Error is : ", e.getMessage());
                                            }
                                        }
                                    });
                                }
                                else
                                {
                                    Log.d("Error is : ", e.getMessage());
                                }
                            }
                        });
                    }

                    //add the current user to discussion room
                    ParseUser current_user = ParseUser.getCurrentUser();
                    ParseObject dismem = new ParseObject("Dis_Member");
                    dismem.put("User_Id",current_user);
                    dismem.put("Dis_Id",dis);
                    dismem.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null)
                            {
                                sendqs.dismiss();
                            }
                            else
                            {
                                Log.d("Error is : ", e.getMessage());
                            }
                        }
                    });

                    Toast toast = Toast.makeText(getApplicationContext(),"Members Added Successfully!",Toast.LENGTH_LONG);
                    toast.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent gotohome = new Intent(AddDiscussionMember.this, Home.class);
                            gotohome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(gotohome);
                        }
                    }, 2000);

                }

            }
        });


        // loading
        final ProgressDialog sendqs = new ProgressDialog(AddDiscussionMember.this);
        sendqs.setTitle("Please wait.");
        sendqs.setMessage("Retrieving a list of users..!");
        sendqs.show();

        ParseUser current_user = ParseUser.getCurrentUser();
        String userid= current_user.getObjectId();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.whereNotEqualTo("objectId", userid);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> userList, ParseException e) {
                if (e == null) {
                    sendqs.dismiss();
                    mStatus = userList;
                    AddDiscussionMemberListAdapter adapter = new AddDiscussionMemberListAdapter(getListView().getContext(), mStatus);
                    setListAdapter(adapter);
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        CheckBox username = (CheckBox) v.findViewById(R.id.checkusername);
        TextView ids = (TextView)v.findViewById(R.id.userid);

        if (username.isChecked() == false) {
            username.setChecked(true);
            username.setTextColor(Color.rgb(51, 51, 204));
            String user_identify = ids.getText().toString();
            selectedstrings.add(user_identify);

        }
        else
        {
            username.setChecked(false);
            username.setTextColor(Color.rgb(51, 204, 51));
            String user_identifys = ids.getText().toString();
            selectedstrings.remove(user_identifys);
        }


    }
}


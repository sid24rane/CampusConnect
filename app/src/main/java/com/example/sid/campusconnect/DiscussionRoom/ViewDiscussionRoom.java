package com.example.sid.campusconnect.DiscussionRoom;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.Home.Home;
import com.example.sid.campusconnect.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ViewDiscussionRoom extends ListActivity {

    ArrayList<DiscussionGetterSetter> dislist = new ArrayList<DiscussionGetterSetter>();
    String st;
    String sub;
    String disid;
    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_discussion_room);

        container=(LinearLayout)findViewById(R.id.viewroom);

        // loading
        final ProgressDialog QSloader = new ProgressDialog(ViewDiscussionRoom.this);
        QSloader.setTitle("Please wait.");
        QSloader.setMessage("Loading Discussion Rooms..");
        QSloader.show();

        ParseUser current_user = ParseUser.getCurrentUser();

        if (current_user != null)
        {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Dis_Member");
            query.whereEqualTo("User_Id", current_user);
            query.include("Dis_Id");
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> roomsList, ParseException e) {
                    if (e == null) {
                        int length = roomsList.size();
                        if (length == 0) {

                            TextView notice = new TextView(ViewDiscussionRoom.this);
                            notice.setText("Nothing to Discuss for ! Redirecting you to Homepage !");
                            notice.setTextColor(Color.rgb(51, 204, 51));
                            notice.setTextSize(20);
                            container.addView(notice);

                            Toast toast = Toast.makeText(getApplicationContext(), "No Discussion Rooms!", Toast.LENGTH_LONG);
                            toast.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(ViewDiscussionRoom.this, Home.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }, 2000);
                        } else {
                            for (ParseObject disrooms : roomsList) {
                                ParseObject dis = disrooms.getParseObject("Dis_Id");
                                try {
                                    sub = dis.fetchIfNeeded().getString("Subject");
                                    Boolean stat = dis.fetchIfNeeded().getBoolean("Status");
                                    if (stat) {
                                        st = "Open";
                                    } else {
                                        st = "Closed";
                                    }
                                    disid = dis.fetchIfNeeded().getObjectId();
                                    dislist.add(new DiscussionGetterSetter(sub, st, disid));
                                    QSloader.dismiss();
                                    ViewDiscussionRoomListAdapter adapter = new ViewDiscussionRoomListAdapter(ViewDiscussionRoom.this, dislist);
                                    setListAdapter(adapter);
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    } else {
                        Log.d("Discussion ", "Error: " + e.getMessage());
                    }
                }
            });

        }
        else
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent gotohome = new Intent(ViewDiscussionRoom.this, Home.class);
                    gotohome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(gotohome);
                }
            }, 2000);
        }

    }

    public void DiscussionDetailHandler(View v)
    {

        View parentView = (View) v.getParent();
        View greatparent = (View)parentView.getParent();
        TextView sd = (TextView)greatparent.findViewById(R.id.disid);
        final String discussion_id = sd.getText().toString();

        Intent intent = new Intent(ViewDiscussionRoom.this,DiscussionDetail.class);
        //NOTE: THE MOST IMP STEP ==> PASSING QuestionID THROUGH INTENT TO NEXT ACTIVITY
        intent.putExtra("discussion_id", discussion_id);
        startActivity(intent);
    }

    public void DiscussionChatHandler(View v)
    {
        View parentView = (View) v.getParent();
        View greatparent = (View)parentView.getParent();
        TextView sd = (TextView)greatparent.findViewById(R.id.disid);
        final String discussion_id = sd.getText().toString();

        Intent intent = new Intent(ViewDiscussionRoom.this,DiscussionRoom.class);
        //NOTE: THE MOST IMP STEP ==> PASSING QuestionID THROUGH INTENT TO NEXT ACTIVITY
        intent.putExtra("discussion_id",discussion_id);
        startActivity(intent);

    }

    public void DiscussionCloseHandler(View v)
    {
        View parentView = (View) v.getParent();
        View greatparent = (View)parentView.getParent();
        TextView sd = (TextView)greatparent.findViewById(R.id.disid);
        final String discussion_id = sd.getText().toString();

        AlertDialog.Builder discarduser = new AlertDialog.Builder(this);
        discarduser.setMessage("Do you want to Close this Discussion ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // setting status == false
                        // closing the discussion
                        // from discuss_room

                        // loading
                        final ProgressDialog QSloader = new ProgressDialog(ViewDiscussionRoom.this);
                        QSloader.setTitle("Please wait.");
                        QSloader.setMessage("Closing Discussion Room..");
                        QSloader.show();

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Discuss_Room");
                        query.getInBackground(discussion_id, new GetCallback<ParseObject>() {
                            public void done(final ParseObject discussion, ParseException e)
                            {
                                if (e == null)
                                {
                                    discussion.put("Status", false);
                                    discussion.put("Closed_By", ParseUser.getCurrentUser());
                                    discussion.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if(e==null)
                                            {

                                                // removing all the records/members list of the discussion from dis_member

                                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Dis_Member");
                                                query.whereEqualTo("Dis_Id",discussion);
                                                query.findInBackground(new FindCallback<ParseObject>() {
                                                    public void done(List<ParseObject> memList, ParseException e) {
                                                        if (e == null)
                                                        {
                                                            for (ParseObject mem : memList) {
                                                                mem.deleteInBackground(new DeleteCallback() {
                                                                    @Override
                                                                    public void done(ParseException e) {
                                                                        if(e==null)
                                                                        {
                                                                            // success
                                                                        }
                                                                        else
                                                                        {
                                                                            Log.d("error",e.getMessage());
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                            //outside loop
                                                            QSloader.dismiss();
                                                            Toast toast = Toast.makeText(getApplicationContext(),"Discussion Closed Successfully!",Toast.LENGTH_LONG);
                                                            toast.show();

                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Intent gotohome = new Intent(ViewDiscussionRoom.this, Home.class);
                                                                    gotohome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                    startActivity(gotohome);
                                                                }
                                                            }, 1000);
                                                        }
                                                        else
                                                        {
                                                            Log.d("error",e.getMessage());

                                                        }
                                                    }
                                                });

                                            }
                                            else
                                            {
                                                Log.d("Error is :",e.getMessage());
                                            }
                                        }
                                    });
                                }
                                else
                                {
                                    Log.d("Error is :",e.getMessage());
                                }
                            }
                        });

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        //Creating dialog box
        AlertDialog alertbox = discarduser.create();
        //Setting the title manually
        alertbox.setTitle("Close Discussion");
        alertbox.show();
    }


}

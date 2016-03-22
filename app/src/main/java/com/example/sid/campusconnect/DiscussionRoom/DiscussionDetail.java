package com.example.sid.campusconnect.DiscussionRoom;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.Home.Home;
import com.example.sid.campusconnect.R;
import com.example.sid.campusconnect.UserProfile.UserProfile;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class DiscussionDetail extends ListActivity {

    String sub,st,username,opened;
    ArrayList<String> ms = new ArrayList<String>();
    protected TextView s;
    protected TextView sta;
    protected TextView open;
    protected Button leavedis;
    protected TextView dismemnos;
    ParseObject dis;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_detail);

        s=(TextView)findViewById(R.id.discussTopic);
        sta=(TextView)findViewById(R.id.discussStatus);
        open=(TextView)findViewById(R.id.discussOpenedBy);
        leavedis=(Button)findViewById(R.id.leave);
        dismemnos=(TextView)findViewById(R.id.DisMembersNos);

        //Receiving discussion_id through intent
        Intent intent = getIntent();
        final String discussion_id = intent.getStringExtra("discussion_id");

        // getting discussion object
        ParseQuery<ParseObject> querys = ParseQuery.getQuery("Discuss_Room");
        querys.getInBackground(discussion_id, new GetCallback<ParseObject>() {
            public void done(ParseObject discussion_room, ParseException e) {
                if (e == null) {
                    dis = discussion_room;
                } else {
                    Log.d("Error is : ", e.getMessage());
                }
            }
        });

        leavedis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder discarduser = new AlertDialog.Builder(DiscussionDetail.this);
                discarduser.setMessage("Do you want to Exit from this Discussion ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                // loading
                                final ProgressDialog QSloaders = new ProgressDialog(DiscussionDetail.this);
                                QSloaders.setTitle("Please wait.");
                                QSloaders.setMessage("Exiting Discussion Room ! Please wait...!!");
                                QSloaders.show();

                                ParseUser current = ParseUser.getCurrentUser();

                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Dis_Member");
                                query.whereEqualTo("Dis_Id",dis);
                                query.whereEqualTo("User_Id",current);
                                query.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(List<ParseObject> memberList, ParseException e) {
                                        if (e == null) {
                                            for (ParseObject mem : memberList   ) {
                                                mem.deleteInBackground(new DeleteCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        QSloaders.dismiss();
                                                        Toast toast =Toast.makeText(getApplicationContext(),"Discussion Exited Successfully!",Toast.LENGTH_LONG);
                                                        toast.show();
                                                        Intent gotohome = new Intent(DiscussionDetail.this, Home.class);
                                                        gotohome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(gotohome);

                                                    }
                                                });
                                            }

                                        } else {
                                            Log.d("member", "Error: " + e.getMessage());
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
                alertbox.setTitle("Exit Discussion!");
                alertbox.show();//

            }
        });


        // loading
        final ProgressDialog QSloader = new ProgressDialog(DiscussionDetail.this);
        QSloader.setTitle("Please wait.");
        QSloader.setMessage("Loading Discussion Room Details..");
        QSloader.show();

        ParseUser current_user = ParseUser.getCurrentUser();

        if (current_user != null)
        {
            // getting discussion info
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Discuss_Room");
            query.include("Opened_By");
            query.getInBackground(discussion_id, new GetCallback<ParseObject>() {
                public void done(ParseObject dis, ParseException e) {
                    if (e == null) {
                        QSloader.dismiss();
                        sub = dis.getString("Subject");
                        s.setText(sub);

                        Boolean result = dis.getBoolean("Status");
                        if (result) {
                            st = "Open";
                            sta.setText(st);
                        } else {
                            st = "Closed";
                            sta.setText(st);
                        }
                        ParseObject users = dis.getParseObject("Opened_By");
                        try {
                            opened = users.fetchIfNeeded().getString("Name");
                            open.setText(opened);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }

                        // getting members list
                        ParseQuery<ParseObject> querys = ParseQuery.getQuery("Dis_Member");
                        querys.whereEqualTo("Dis_Id", dis);
                        querys.include("User_Id");
                        querys.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> memberList, ParseException e) {
                                if (e == null) {
                                    int length = memberList.size();
                                    dismemnos.setText(String.valueOf(length));
                                    DiscussionMemberListAdapter adapter = new DiscussionMemberListAdapter(getListView().getContext(), memberList);
                                    setListAdapter(adapter);
                                } else {
                                    Log.d("Disussion", "Error: " + e.getMessage());
                                }
                            }
                        });
                    } else {
                        Log.d("Error is : ", e.getMessage());
                    }
                }
            });

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent gotohome = new Intent(DiscussionDetail.this, Home.class);
                    gotohome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(gotohome);
                }
            }, 2000);
        }
    }

    public void viewprofilehandler(View view) {
        TextView userid = (TextView)view.findViewById(R.id.uid);
        String user_id =userid.getText().toString().toString();
        //Toast.makeText(getApplicationContext(),user_id,Toast.LENGTH_LONG).show();
        Intent i = new Intent(DiscussionDetail.this, UserProfile.class);
        i.putExtra("user_id",user_id);
        startActivity(i);
    }
}

package com.example.sid.campusconnect.AskStaff;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.Home.Home;
import com.example.sid.campusconnect.MainActivity;
import com.example.sid.campusconnect.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class AskStaff extends ListActivity {
    protected List<ParseObject> mStatus;
    protected TextView SId;
    public String question_id;
    ParseObject qs_id;
    public String qs_title;
    ParseObject s_id;
    public String s_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_staff);

        //Receiving question_id through intent
        Intent intent = getIntent();
        question_id = intent.getStringExtra("question_id");


        final ProgressDialog StaffLoader = new ProgressDialog(AskStaff.this);
        StaffLoader.setTitle("Please wait.");
        StaffLoader.setMessage("Loading Staff List..");
        StaffLoader.show();

        ParseUser current_user = ParseUser.getCurrentUser();
        final String userid = current_user.getObjectId();

        if (current_user != null) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
            query.whereEqualTo("Is_Admin", true);
            query.whereNotEqualTo("objectId",userid);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> StaffList, ParseException e) {
                    if (e == null) {
                        int length = StaffList.size();
                        if (length == 0) {
                            Toast toast = Toast.makeText(getApplicationContext(), "No Staffs Found!", Toast.LENGTH_LONG);
                            toast.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(AskStaff.this, Home.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }, 5000);
                        } else {
                            StaffLoader.dismiss();
                            mStatus = StaffList;
                            AskStaffListAdapter adapter = new AskStaffListAdapter(getListView().getContext(), mStatus);
                            setListAdapter(adapter);
                        }
                    } else {
                        Log.d("Staff ", "Error: " + e.getMessage());
                    }
                }

            });


        } else {
            Intent intent1 = new Intent(AskStaff.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
        }
    }

    public void Request(View v) {
        final ProgressDialog RSent = new ProgressDialog(AskStaff.this);
        RSent.setTitle("Please wait.");
        RSent.setMessage("Sending Request To Staff..");
        RSent.show();

        View parentView = (View) v.getParent();
        View gparent = (View) parentView.getParent();
        TextView sd = (TextView) gparent.findViewById(R.id.StaffId);
        String staff_id = sd.getText().toString();


        final ParseQuery<ParseObject> StaffUser = ParseQuery.getQuery("_User");
        StaffUser.getInBackground(staff_id, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject user, ParseException e) {
                if (e == null) {
                    s_id = user;
                    s_name = user.get("Name").toString();
                } else {
                    Log.d("Error is : ", e.getMessage());
                }
            }
        });


        final ParseQuery<ParseObject> search = ParseQuery.getQuery("Question");
        search.getInBackground(question_id, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject question, ParseException e) {
                if (e == null) {
                    qs_id = question;
                    qs_title = question.get("Title").toString();
                } else {
                    Log.d("Question", "Error: " + e.getMessage());
                }

            }
        });
        final ParseUser current_user = ParseUser.getCurrentUser();
        final ParseQuery<ParseObject> lastquery = ParseQuery.getQuery("Staff_Request");
        lastquery.whereEqualTo("Qs_Id", qs_id);
        lastquery.whereEqualTo("User_Id", s_id);
        lastquery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e == null) {
                    int length = list.size();
                    if (length != 0) {
                        RSent.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(), "This Staff Is Already Requested !", Toast.LENGTH_LONG);
                        toast.show();
                        finish();
                    } else {
                        ParseObject staff_request = new ParseObject("Staff_Request");
                        staff_request.put("User_Id", s_id);
                        staff_request.put("Qs_Id", qs_id);
                        staff_request.put("Staff_Name", s_name);
                        staff_request.put("Qs_Title", qs_title);
                        staff_request.put("Requested_By", current_user);
                        staff_request.saveInBackground();
                        RSent.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(), "Request Sent Successfully !", Toast.LENGTH_LONG);
                        toast.show();
                        finish();
                    }
                } else {
                    Log.d("Question", "Error: " + e.getMessage());
                }
            }
        });

    }
}

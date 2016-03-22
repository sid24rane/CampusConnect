package com.example.sid.campusconnect.ReportedUser;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.Home.Home;
import com.example.sid.campusconnect.MainActivity;
import com.example.sid.campusconnect.R;
import com.example.sid.campusconnect.UserProfile.UserProfile;
import com.example.sid.campusconnect.VerifyStudent.StudentVerifyListAdapter;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportedUsers extends ListActivity {
    // RelativeLayout container;
    protected List<ParseObject> mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reported_users);

        //  container =(RelativeLayout)findViewById(R.id.studentlist);

        // loading
        final ProgressDialog unverifiedloader = new ProgressDialog(ReportedUsers.this);
        unverifiedloader.setTitle("Please wait.");
        unverifiedloader.setMessage("Loading Unverified Students..");
        unverifiedloader.show();

        ParseUser current_user = ParseUser.getCurrentUser();
        if (current_user != null) {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
            query.whereNotEqualTo("Is_Admin", true);
            query.whereGreaterThanOrEqualTo("Bad_Points", 150);
            query.whereLessThan("Bad_Points",9998);
            query.orderByAscending("Name");

            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> studentList, ParseException e) {
                    if (e == null) {
                        int length = studentList.size();
                        if (length == 0) {
                            unverifiedloader.dismiss();
                            Toast toast = Toast.makeText(getApplicationContext(), "No Unverified Students !", Toast.LENGTH_LONG);
                            toast.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(ReportedUsers.this, Home.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }, 5000);
                        } else {
                            unverifiedloader.dismiss();
                            mStatus = studentList;
                            StudentVerifyListAdapter adapter = new StudentVerifyListAdapter(getListView().getContext(), mStatus);
                            setListAdapter(adapter);
                        }
                    } else {
                        Log.d("User ", "Error: " + e.getMessage());
                    }
                }
            });

        } else {
            Intent intent = new Intent(ReportedUsers.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }


    public void InfoHandler(View v) {
        LinearLayout vwParentRow = (LinearLayout) v.getParent();
        LinearLayout greatparent = (LinearLayout) vwParentRow.getParent();
        TextView btnChild = (TextView) greatparent.getChildAt(2);
        String user_id = btnChild.getText().toString();

        Intent intent = new Intent(ReportedUsers.this, UserProfile.class);
        //NOTE: THE MOST IMP STEP ==> PASSING USERID THROUGH INTENT TO NEXT ACTIVITY
        intent.putExtra("user_id", user_id);
        startActivity(intent);

    }

    public void VerifyHandler(View v){
        LinearLayout vwParentRow = (LinearLayout) v.getParent();
        LinearLayout greatparent = (LinearLayout) vwParentRow.getParent();
        TextView btnChild = (TextView) greatparent.getChildAt(2);
        final String user_id = btnChild.getText().toString();

        AlertDialog.Builder discarduser = new AlertDialog.Builder(this);
        discarduser.setMessage("Do you want to Validate this Student ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("userId", user_id);
                        ParseCloud.callFunctionInBackground("AllowUser", params, new FunctionCallback<Map<String, String>>() {
                            public void done(Map<String, String> result, ParseException e) {
                                if (e == null) {
                                    AutoRefresh();
                                    Toast.makeText(getApplicationContext(), "Student Validated@", Toast.LENGTH_LONG).show();
                                    //remove row/refresh
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
        alertbox.setTitle("Validate Student");
        alertbox.show();

    }

    public void DiscardHandler(View v)
    {
        LinearLayout vwParentRow = (LinearLayout) v.getParent();
        LinearLayout greatparent = (LinearLayout) vwParentRow.getParent();
        TextView btnChild = (TextView) greatparent.getChildAt(2);
        final String user_id = btnChild.getText().toString();

        AlertDialog.Builder discarduser = new AlertDialog.Builder(this);
        discarduser.setMessage("Do you want to Discard this Student ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("userId", user_id);
                        ParseCloud.callFunctionInBackground("DisallowUser", params, new FunctionCallback<Map<String, String>>() {
                            public void done(Map<String, String> result, ParseException e) {
                                if (e == null) {
                                    AutoRefresh();
                                    Toast.makeText(getApplicationContext(), "Studemt Discarded!", Toast.LENGTH_LONG).show();
                                    //remove row/refresh

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
        alertbox.setTitle("Discard Student");
        alertbox.show();

    }

    public void AutoRefresh()
    {
        final ProgressDialog unverifiedloader = new ProgressDialog(ReportedUsers.this);
        unverifiedloader.setTitle("Please wait.");
        unverifiedloader.setMessage("Loading Unverified Students..");
        unverifiedloader.show();


        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.whereNotEqualTo("Is_Admin", true);
        query.whereGreaterThanOrEqualTo("Bad_Points", 150);
        query.whereLessThan("Bad_Points",9998);
        query.orderByAscending("Name");

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> studentList, ParseException e) {
                if (e == null) {
                    int length = studentList.size();
                    if (length == 0) {
                        unverifiedloader.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(), "No Unverified Students !", Toast.LENGTH_LONG);
                        toast.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(ReportedUsers.this, Home.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }, 5000);
                    } else {
                        unverifiedloader.dismiss();
                        mStatus = studentList;
                        StudentVerifyListAdapter adapter = new StudentVerifyListAdapter(getListView().getContext(), mStatus);
                        setListAdapter(adapter);
                    }
                } else {
                    Log.d("User ", "Error: " + e.getMessage());
                }
            }
        });
    }
}

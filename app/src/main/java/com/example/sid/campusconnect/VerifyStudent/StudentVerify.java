package com.example.sid.campusconnect.VerifyStudent;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.Home.Home;
import com.example.sid.campusconnect.MainActivity;
import com.example.sid.campusconnect.R;
import com.example.sid.campusconnect.UserProfile.UserProfile;
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

public class StudentVerify extends ListActivity {

    protected  List<ParseObject> mStatus;
    RelativeLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_verify);

        container =(RelativeLayout)findViewById(R.id.studentlist);

        // loading
        final ProgressDialog unverifiedloader = new ProgressDialog(StudentVerify.this);
        unverifiedloader.setTitle("Please wait.");
        unverifiedloader.setMessage("Loading Unverified Students..");
        unverifiedloader.show();

        ParseUser current_user = ParseUser.getCurrentUser();
        if (current_user != null)
        {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
            query.whereNotEqualTo("Is_Admin", true);
            query.whereNotEqualTo("isComplete", false);
            query.whereNotEqualTo("Is_Verified", true);
            query.orderByAscending("Name");

            query.findInBackground(new FindCallback<ParseObject>()
            {
                public void done(List<ParseObject> studentList, ParseException e) {
                    if (e == null)
                    {
                        int length = studentList.size();
                        if(length==0)
                        {
                            unverifiedloader.dismiss();
                            TextView notice = new TextView(StudentVerify.this);
                            notice.setText("NO Unverified Students ! Redirecting you to Homepage !");
                            notice.setTextColor(Color.rgb(51, 204, 51));
                            notice.setTextSize(20);
                            container.addView(notice);
                            Toast toast = Toast.makeText(getApplicationContext(),"No Unverified Students !",Toast.LENGTH_LONG);
                            toast.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(StudentVerify.this, Home.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }, 5000);
                        }
                        else
                        {
                            unverifiedloader.dismiss();
                            mStatus = studentList;
                            StudentVerifyListAdapter adapter = new StudentVerifyListAdapter(getListView().getContext(), mStatus);
                            setListAdapter(adapter);
                        }
                    }
                    else
                    {
                        Log.d("User ", "Error: " + e.getMessage());
                    }
                }
            });

        } else
        {
            Intent intent = new Intent(StudentVerify.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }

    public void VerifyHandler(View v)
    {

        LinearLayout vwParentRow = (LinearLayout)v.getParent();
        LinearLayout greatparent =(LinearLayout)vwParentRow.getParent();
        TextView btnChild = (TextView)greatparent.getChildAt(2);
        String user_id = btnChild.getText().toString();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", user_id);
        ParseCloud.callFunctionInBackground("VerifyUser", params, new FunctionCallback<Map<String, String>>() {
            public void done(Map<String, String> result, ParseException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), result.get("verify_result"), Toast.LENGTH_LONG).show();
                    AutoRefresh();
                }
            }
        });
    }

    public void InfoHandler(View v)
    {
        // Algorithm:
        /////////////////////

        // first get the middle linearlayout
        // go one level up --> linear
        // to get the textview which is set invisible , but it contains the user id of each user
        // get the userid from the GREAT parent linear layout
        // cast into string
        // pass the user id through intent to the userprofile activity where it is used to fetch the user data

        ///////////////////////

        LinearLayout vwParentRow = (LinearLayout)v.getParent();
        LinearLayout greatparent =(LinearLayout)vwParentRow.getParent();
        TextView btnChild = (TextView)greatparent.getChildAt(2);
        String user_id = btnChild.getText().toString();

        Intent intent = new Intent(StudentVerify.this,UserProfile.class);
        //NOTE: THE MOST IMP STEP ==> PASSING USERID THROUGH INTENT TO NEXT ACTIVITY
        intent.putExtra("user_id", user_id);
        startActivity(intent);

    }
    public void DiscardHandler(final View v)
    {
        //SAME SHIT REPEATED FROM ABOVE
        LinearLayout vwParentRow = (LinearLayout)v.getParent();
        LinearLayout greatparent =(LinearLayout)vwParentRow.getParent();
        TextView btnChild = (TextView)greatparent.getChildAt(2);
        final String user_id = btnChild.getText().toString();

        // for testing :)
        //Toast toast =Toast.makeText(getApplicationContext(),user_id,Toast.LENGTH_LONG);
        //toast.show();

        AlertDialog.Builder discarduser = new AlertDialog.Builder(this);
        discarduser.setMessage("Do you want to Discard this Student ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("userId", user_id);
                        ParseCloud.callFunctionInBackground("DiscardUser", params, new FunctionCallback<Map<String, String>>() {
                            public void done(Map<String, String> result, ParseException e) {
                                if (e == null) {
                                    Toast.makeText(getApplicationContext(), result.get("discard_result"), Toast.LENGTH_LONG).show();
                                    //remove row/refresh
                                    AutoRefresh();
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

        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.whereNotEqualTo("Is_Admin", true);
        query.whereNotEqualTo("isComplete", false);
        query.whereNotEqualTo("Is_Verified", true);
        query.orderByAscending("Name");

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> studentList, ParseException e) {
                if (e == null) {
                    int length = studentList.size();
                    if (length == 0)
                    {
                        Toast toast = Toast.makeText(getApplicationContext(), "No Unverified Students !", Toast.LENGTH_LONG);
                        toast.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(StudentVerify.this, Home.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }, 5000);
                    } else
                    {
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

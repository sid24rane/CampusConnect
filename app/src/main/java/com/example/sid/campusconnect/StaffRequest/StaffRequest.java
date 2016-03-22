package com.example.sid.campusconnect.StaffRequest;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.Home.Home;
import com.example.sid.campusconnect.MainActivity;
import com.example.sid.campusconnect.Question.QuestionDetail;
import com.example.sid.campusconnect.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class StaffRequest extends ListActivity {

    protected List<ParseObject> mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_request);
        request();
    }


    public void request()
    {
        // loading
        final ProgressDialog SRLoader = new ProgressDialog(StaffRequest.this);
        SRLoader.setTitle("Please wait.");
        SRLoader.setMessage("Loading Staff Requests...");
        SRLoader.show();

        ParseUser current_user = ParseUser.getCurrentUser();
        if (current_user != null) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Staff_Request");
            query.whereEqualTo("User_Id", current_user);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        int no = list.size();
                        if (list.size() == 0) {
                            SRLoader.dismiss();
                            Toast toast = Toast.makeText(getApplicationContext(), "No Request Found!", Toast.LENGTH_LONG);
                            toast.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(StaffRequest.this, Home.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }, 2000);
                        } else {
                            SRLoader.dismiss();
                            mStatus = list;
                            StaffRequestListAdapter adapter = new StaffRequestListAdapter(getListView().getContext(), mStatus);
                            setListAdapter(adapter);

                            //set no in action bar
                            //Toast toast = Toast.makeText(getApplicationContext(), "You Have " + no + " Request !", Toast.LENGTH_LONG);
                            //toast.show();
                        }
                    } else {
                        Log.d("Staff ", "Error: " + e.getMessage());
                    }
                }
            });
        } else {
            Intent intent = new Intent(StaffRequest.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public void ReadFullQs(View v) {

        View parentView = (View) v.getParent();
        View gparent = (View) parentView.getParent();
        TextView sd = (TextView) gparent.findViewById(R.id.SRQsId);
        final String question_id = sd.getText().toString();

        Intent intent = new Intent(StaffRequest.this, QuestionDetail.class);
        //NOTE: THE MOST IMP STEP ==> PASSING QuestionID THROUGH INTENT TO NEXT ACTIVITY
        intent.putExtra("question_id", question_id);
        startActivity(intent);

    }

    public void RejectQsHandler(View v) {

        View parentView = (View) v.getParent();
        View gparent = (View) parentView.getParent();

        TextView sd = (TextView) gparent.findViewById(R.id.SRObjId);
        final String obj_id = sd.getText().toString();

        TextView sd1 = (TextView) gparent.findViewById(R.id.SRQsId);
        final String question_id = sd1.getText().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to remove this Request ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // loading
                        final ProgressDialog QSloader = new ProgressDialog(StaffRequest.this);
                        QSloader.setTitle("Please wait.");
                        QSloader.setMessage("Removing Question..");
                        QSloader.show();

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Question");
                        query.getInBackground(question_id, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                if (e == null) {
                                    object.put("Is_Requested", false);
                                    object.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                // do other
                                                ParseQuery<ParseObject> querys = ParseQuery.getQuery("Staff_Request");
                                                querys.getInBackground(obj_id, new GetCallback<ParseObject>() {
                                                    public void done(ParseObject object, ParseException e) {
                                                        if (e == null) {
                                                            object.deleteInBackground(new DeleteCallback() {
                                                                @Override
                                                                public void done(ParseException e) {
                                                                    if (e == null) {
                                                                        QSloader.dismiss();
                                                                        Toast toast = Toast.makeText(getApplicationContext(), "Question Removed Successfully!", Toast.LENGTH_LONG);
                                                                        toast.show();
                                                                        new Handler().postDelayed(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                request();
                                                                            }
                                                                        }, 1500);
                                                                    } else {
                                                                        Log.d("Error is : ", e.getMessage());
                                                                    }
                                                                }
                                                            });

                                                        }
                                                    }
                                                });
                                            } else {
                                                Log.d("Error is :", e.getMessage());
                                            }
                                        }
                                    });
                                } else {
                                    Log.d("Question : Error is : ", e.getMessage());
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
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Remove Request");
        alert.show();
    }

}

package com.example.sid.campusconnect.Answer.ReportAnswer;

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
import com.example.sid.campusconnect.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportedAns extends ListActivity{

    protected List<ParseObject> mStatus;
    ParseObject ans_obj;
    //ParseUser answerer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reported_ans);


        final ProgressDialog validator = new ProgressDialog(ReportedAns.this);
        validator.setTitle("Please wait.");
        validator.setMessage("Loading Reported Answers..");
        validator.show();

        ParseUser current_user = ParseUser.getCurrentUser();

        if (current_user != null)
        {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Answer");
            query.whereEqualTo("IsReported", true);
            query.include("User_Id");
            query.include("Qs_Id");
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> ansList, ParseException e) {
                    if (e == null) {
                        int length = ansList.size();
                        if (length == 0) {
                            Toast toast = Toast.makeText(getApplicationContext(), "No Reported Answers!", Toast.LENGTH_LONG);
                            toast.show();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(ReportedAns.this, Home.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }, 5000);
                        } else {
                            validator.dismiss();
                            mStatus = ansList;
                            ReportedAnsListAdapter adapter = new ReportedAnsListAdapter(getListView().getContext(), mStatus);
                            setListAdapter(adapter);
                        }
                    } else {
                        Log.d("Reported Ans ", "Error: " + e.getMessage());
                    }
                }
            });
        } else
        {
            Intent intent1 = new Intent(ReportedAns.this, MainActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
        }
    }

    public void rep_allowHandler(View v)
    {
        View parentView = (View) v.getParent();
        final ProgressDialog validator = new ProgressDialog(ReportedAns.this);
        validator.setTitle("Please wait.");
        validator.setMessage("Validating Answer");
        validator.show();

        TextView ans_id=(TextView) parentView.findViewById(R.id.rep_aid);

        final String a_id=ans_id.getText().toString();


        //fetching current ans obj

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Answer");
        try {
            ans_obj=query.get(a_id);
        } catch (ParseException e) {
            Toast.makeText(ReportedAns.this,"Query Error ans",Toast.LENGTH_LONG).show();
        }

        ans_obj.put("IsReported", false);
        ans_obj.saveInBackground();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("answer_id", a_id);
        ParseCloud.callFunctionInBackground("Ans_Upvote", params, new FunctionCallback<Map<String, String>>() {
            public void done(Map<String, String> result, ParseException e) {
                if (e == null) {
                    //success
                    validator.dismiss();
                    Toast.makeText(ReportedAns.this, "Answer Validated", Toast.LENGTH_LONG).show();
                    AutoRefresh();
                } else {
                    Log.d("Error is : ", e.getMessage());
                }
            }
        });

    }


    public void rep_discardHandler(View v)
    {
        View parentView = (View) v.getParent();
        final ProgressDialog validator = new ProgressDialog(ReportedAns.this);
        TextView ans_id=(TextView) parentView.findViewById(R.id.rep_aid);
        final String a_id=ans_id.getText().toString();


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to discard the Answer?")
                .setTitle("Discard Answer")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        validator.setTitle("Please wait.");
                        validator.setMessage("Discarding Answer");
                        validator.show();
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Answer");
                        query.getInBackground(a_id, new GetCallback<ParseObject>() {
                            public void done(final ParseObject object, ParseException e) {
                                if (e == null) {
                                    HashMap<String, String> params = new HashMap<String, String>();
                                    params.put("answer_id", a_id);
                                    ParseCloud.callFunctionInBackground("AnswerBadPoints", params, new FunctionCallback<Map<String, String>>() {
                                        public void done(Map<String, String> result, ParseException e) {
                                            Toast.makeText(getApplicationContext(), "Start", Toast.LENGTH_LONG).show();
                                            if (e == null) {
                                                //success
                                                ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Notification");
                                                parseQuery.whereEqualTo("Answer",object);
                                                parseQuery.findInBackground(new FindCallback<ParseObject>() {
                                                    @Override
                                                    public void done(List<ParseObject> list, ParseException e) {
                                                        if (e == null) {
                                                            for (int i = 0; i < list.size(); i++) {
                                                                try {
                                                                    list.get(i).delete();
                                                                } catch (ParseException e1) {
                                                                    e1.printStackTrace();
                                                                }
                                                            }
                                                        }
                                                    }
                                                });
                                                object.deleteInBackground(new DeleteCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if (e == null) {
                                                            validator.dismiss();
                                                            Toast toast = Toast.makeText(getApplicationContext(), "Answer discarded!", Toast.LENGTH_LONG);
                                                            toast.show();
                                                            AutoRefresh();
                                                        } else {
                                                            Log.d("Error is : ", e.getMessage());
                                                        }
                                                    }
                                                });
                                            } else {
                                                Log.d("Error is : ", e.getMessage());
                                                Toast.makeText(getApplicationContext(), "Points NOT reduced", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

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


        AlertDialog alert = builder.create();
        alert.show();





    }


    public void AutoRefresh()
    {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Answer");
        query.whereEqualTo("IsReported", true);
        query.include("User_Id");
        query.include("Qs_Id");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> ansList, ParseException e) {
                if (e == null) {
                    int length = ansList.size();
                    if (length == 0) {
                        Toast toast = Toast.makeText(getApplicationContext(), "No Reported Answers!", Toast.LENGTH_LONG);
                        toast.show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(ReportedAns.this, Home.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }, 5000);
                    } else {
                        mStatus = ansList;
                        ReportedAnsListAdapter adapter = new ReportedAnsListAdapter(getListView().getContext(), mStatus);
                        setListAdapter(adapter);
                    }
                } else {
                    Log.d("Reported Ans ", "Error: " + e.getMessage());
                }
            }
        });
    }
}
package com.example.sid.campusconnect.Question.ReportQuestion;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.Home.Home;
import com.example.sid.campusconnect.MainActivity;
import com.example.sid.campusconnect.Question.QuestionDetail;
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
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportedQuestion extends ListActivity {

    protected List<ParseObject> mStatus;
    RelativeLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reported_question);

        container =(RelativeLayout)findViewById(R.id.listss);

        // loading
        final ProgressDialog QSloader = new ProgressDialog(ReportedQuestion.this);
        QSloader.setTitle("Please wait.");
        QSloader.setMessage("Loading Reported Questions..");
        QSloader.show();

        ParseUser current_user = ParseUser.getCurrentUser();

        if (current_user != null)
        {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Question");
            query.whereEqualTo("Is_Reported", true);
            query.findInBackground(new FindCallback<ParseObject>()
            {
                public void done(List<ParseObject> questionList, ParseException e) {
                    if (e == null)
                    {
                        int length = questionList.size();
                        if(length==0)
                        {
                            QSloader.dismiss();
                            TextView notice = new TextView(ReportedQuestion.this);
                            notice.setText("NO Reported Questions ! Redirecting you to Homepage !");
                            notice.setTextColor(Color.rgb(51, 204, 51));
                            notice.setTextSize(20);
                            container.addView(notice);
                            Toast toast = Toast.makeText(getApplicationContext(),"No Reported Questions!",Toast.LENGTH_LONG);
                            toast.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(ReportedQuestion.this, Home.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }, 3000);
                        }
                        else
                        {
                            QSloader.dismiss();
                            mStatus = questionList;
                            ReportedQsListAdapter adapter = new ReportedQsListAdapter(getListView().getContext(), mStatus);
                            setListAdapter(adapter);
                        }
                    }
                    else
                    {
                        Log.d("Question ", "Error: " + e.getMessage());
                    }
                }
            });

        } else
        {
            Intent intent = new Intent(ReportedQuestion.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public void ReReadFullQsHandler(View v)
    {

        View parentView = (View) v.getParent();
        View gparent =(View)parentView.getParent();
        TextView sd = (TextView)gparent.findViewById(R.id.ReQsId);
        final String question_id = sd.getText().toString();

        Intent intent = new Intent(ReportedQuestion.this,QuestionDetail.class);
        //NOTE: THE MOST IMP STEP ==> PASSING QuestionID THROUGH INTENT TO NEXT ACTIVITY
        intent.putExtra("question_id", question_id);
        startActivity(intent);
    }

    public void AcceptQsHandler(View v)
    {

        // loading
        final ProgressDialog QSloader = new ProgressDialog(ReportedQuestion.this);
        QSloader.setTitle("Please wait.");
        QSloader.setMessage("Accepting Question..");
        QSloader.show();

        View parentView = (View) v.getParent();
        View gparent =(View)parentView.getParent();
        TextView sd = (TextView)gparent.findViewById(R.id.ReQsId);
        final String question_id = sd.getText().toString();


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Question");
        query.getInBackground(question_id, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    object.put("Is_Reported", false);
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {

                                HashMap<String, String> params = new HashMap<String, String>();
                                params.put("question_id", question_id);
                                ParseCloud.callFunctionInBackground("PointsIncrement", params, new FunctionCallback<Map<String, String>>() {
                                    public void done(Map<String, String> result, ParseException e) {
                                        if (e == null)
                                        {
                                            QSloader.dismiss();
                                            Toast toast = Toast.makeText(getApplicationContext(), "Question Allowed !", Toast.LENGTH_LONG);
                                            toast.show();
                                            AutoRefresh();

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
            }
        });

    }

    public void RejectQsHandler(View v)
    {
        // loading
        final ProgressDialog QSloader = new ProgressDialog(ReportedQuestion.this);
        QSloader.setTitle("Please wait.");
        QSloader.setMessage("Removing Question..");
        QSloader.show();

        View parentView = (View) v.getParent();
        View gparent =(View)parentView.getParent();
        TextView sd = (TextView)gparent.findViewById(R.id.ReQsId);
        final String question_id = sd.getText().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to remove this Question ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Question");
                        query.getInBackground(question_id, new GetCallback<ParseObject>() {
                            public void done(final ParseObject object, ParseException e) {
                                if (e == null) {
                                    HashMap<String, String> params = new HashMap<String, String>();
                                    params.put("question_id", question_id);
                                    ParseCloud.callFunctionInBackground("QuestionBadPoints", params, new FunctionCallback<Map<String, String>>() {
                                        public void done(Map<String, String> result, ParseException e) {
                                            Toast.makeText(getApplicationContext(), "Start", Toast.LENGTH_LONG).show();
                                            if (e == null) {
                                                //success
                                                ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Notification");
                                                parseQuery.whereEqualTo("Question", object);
                                                parseQuery.findInBackground(new FindCallback<ParseObject>() {
                                                    @Override
                                                    public void done(List<ParseObject> list, ParseException e) {
                                                        if (e == null) {
                                                            for (int i = 0; i <= list.size(); i++) {
                                                                try {
                                                                    list.get(i).delete();
                                                                } catch (ParseException e1) {
                                                                    e1.printStackTrace();
                                                                }
                                                            }
                                                        }
                                                    }
                                                });
                                                Toast.makeText(getApplicationContext(), "Points reduced", Toast.LENGTH_LONG).show();
                                                object.deleteInBackground(new DeleteCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if (e == null) {
                                                            Toast toast = Toast.makeText(getApplicationContext(), "Answer discarded!", Toast.LENGTH_LONG);
                                                            toast.show();
                                                            QSloader.dismiss();
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
                        QSloader.dismiss();
                        dialog.cancel();
                    }
                });

        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Remove Question");
        alert.show();

    }

    public void AutoRefresh()
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Question");
        query.whereEqualTo("Is_Reported", true);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> questionList, ParseException e) {
                if (e == null) {
                    int length = questionList.size();
                    if (length == 0) {
                        Toast toast = Toast.makeText(getApplicationContext(), "No Reported Questions!", Toast.LENGTH_LONG);
                        toast.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(ReportedQuestion.this, Home.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }, 3000);
                    } else {

                        mStatus = questionList;
                        ReportedQsListAdapter adapter = new ReportedQsListAdapter(getListView().getContext(), mStatus);
                        setListAdapter(adapter);
                    }
                } else {
                    Log.d("Question ", "Error: " + e.getMessage());
                }
            }
        });

    }
}

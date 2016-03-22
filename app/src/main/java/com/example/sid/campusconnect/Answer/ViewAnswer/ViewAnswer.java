package com.example.sid.campusconnect.Answer.ViewAnswer;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.sid.campusconnect.Comment_Reply.AddComment.AddComment;
import com.example.sid.campusconnect.Comment_Reply.ViewComment.CommentView;
import com.example.sid.campusconnect.Answer.EditAnswer.EditAnswer;
import com.example.sid.campusconnect.Home.Home;
import com.example.sid.campusconnect.MainActivity;
import com.example.sid.campusconnect.R;
import com.example.sid.campusconnect.UserProfile.UserProfile;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewAnswer extends ListActivity {

    protected List<ParseObject> mStatus;
    Button upvote, downvote, comment;
    ParseObject ques;
    TextView tv_ques;
    ParseObject ans_obj, tbd;
    TextView ad;
    ParseUser current_user;
    final Context context = this;
    ParseFile file;
    ImageView idata;
    String ques_id;
    Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_answer);

        final ProgressDialog QSloader = new ProgressDialog(ViewAnswer.this);
        QSloader.setTitle("Please wait.");
        QSloader.setMessage("Loading Answers..");
        QSloader.show();




        tv_ques = (TextView) findViewById(R.id.ques_title);
        //upvotehandler=(Button) findViewById(R.id.ans_upvote);


        Intent intent = getIntent();
         ques_id = intent.getStringExtra("ques_id");

        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Question");
        try {
            ques = query1.get(ques_id);
        } catch (ParseException e) {
            Toast.makeText(ViewAnswer.this, "Query Error", Toast.LENGTH_LONG).show();
        }

        String ques_ttl = ques.get("Title").toString();
        tv_ques.setText(ques_ttl);


        ParseUser current_user = ParseUser.getCurrentUser();


        if (current_user != null) {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Answer");
            //query.whereNotEqualTo("IsReported",true);
            query.whereEqualTo("Qs_Id", ques);
            query.include("User_Id");
            query.include("Qs_Id");
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> ansList, ParseException e) {
                    if (e == null) {
                        int length = ansList.size();
                        if (length == 0) {
                            Toast toast = Toast.makeText(getApplicationContext(), "No Answers!", Toast.LENGTH_LONG);
                            toast.show();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(ViewAnswer.this, Home.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }, 5000);
                        } else {
                            QSloader.dismiss();
                            mStatus = ansList;
                            ViewAnswerListAdapter adapter = new ViewAnswerListAdapter(getListView().getContext(), mStatus);
                            setListAdapter(adapter);
                        }
                    } else {
                        Log.d("Question ", "Error: " + e.getMessage());
                    }
                }
            });

        } else {
            Intent intent1 = new Intent(ViewAnswer.this, MainActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
        }


    }

    public void ansUpvoteHandler(View v) {
        View parentView = (View) v.getParent();
        ad = (TextView) parentView.findViewById(R.id.tv_ans_id);
        final String a_id = ad.getText().toString();
        current_user = ParseUser.getCurrentUser();
        upvote = (Button) parentView.findViewById(R.id.ans_upvote);
        downvote = (Button) parentView.findViewById(R.id.ans_downvote);


        //fetching current ans obj
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Answer");
        try {
            ans_obj = query1.get(a_id);
        } catch (ParseException e) {
            Toast.makeText(ViewAnswer.this, "Query Error", Toast.LENGTH_LONG).show();
        }


        if (upvote.getText().toString().equals("Upvote") == true) {
            if (current_user != null) {
                ParseQuery<ParseObject> selftupvotingquery = ParseQuery.getQuery("Answer");
                selftupvotingquery.whereEqualTo("User_Id", current_user);
                selftupvotingquery.whereEqualTo("objectId", a_id);
                selftupvotingquery.findInBackground(new FindCallback<ParseObject>() {

                    public void done(List<ParseObject> selfList, ParseException e) {
                        if (e == null) {
                            if (selfList.size() > 0) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Self Upvote not allowed!", Toast.LENGTH_LONG);
                                toast.show();
                            } else {
                                ParseQuery<ParseObject> firstquery = ParseQuery.getQuery("Ans_Upvote");
                                firstquery.include("Ans_Id");
                                firstquery.include("User_Id");
                                firstquery.whereEqualTo("Ans_Id", ans_obj);
                                firstquery.whereEqualTo("User_Id", current_user);
                                firstquery.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(final List<ParseObject> ansList, ParseException e) {
                                        if (e == null) {
                                            if (ansList.size() == 0) {
                                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Answer");
                                                query.getInBackground(a_id, new GetCallback<ParseObject>() {

                                                    public void done(ParseObject answer, ParseException e) {
                                                        if (e == null) {
                                                            ans_obj = answer;
                                                            answer.increment("Upvote_Count");
                                                            if (downvote.getText().toString().equals("Downvoted")) {

                                                                int dvc = (int) answer.get("Upvote_Count");
                                                                dvc--;
                                                                answer.put("Downvote_Count", dvc);
                                                                //CLoude Code Call
                                                                HashMap<String, String> params = new HashMap<String, String>();
                                                                params.put("answer_id", a_id);
                                                                ParseCloud.callFunctionInBackground("Ans_Upvote", params, new FunctionCallback<Map<String, String>>() {
                                                                    public void done(Map<String, String> result, ParseException e) {
                                                                        if (e == null) {
                                                                            //success
                                                                        } else {
                                                                            Log.d("Error is : ", e.getMessage());
                                                                        }
                                                                    }
                                                                });
                                                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Ans_Downvote");
                                                                query.whereEqualTo("Ans_Id", ans_obj);
                                                                query.findInBackground(new FindCallback<ParseObject>() {
                                                                    @Override
                                                                    public void done(List<ParseObject> list, ParseException e) {
                                                                        if (e == null) {
                                                                            list.get(0).deleteInBackground();
                                                                            downvote.setText("Downvote");

                                                                        } else {
                                                                            Toast.makeText(ViewAnswer.this, "Error", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                            }

                                                            answer.saveInBackground(new SaveCallback() {
                                                                @Override
                                                                public void done(ParseException e) {
                                                                    if (e == null) {
                                                                        ParseObject qs_upvote = new ParseObject("Ans_Upvote");
                                                                        qs_upvote.put("Ans_Id", ans_obj);
                                                                        qs_upvote.put("User_Id", current_user);
                                                                        upvote.setText("Upvoted");
                                                                        try {
                                                                            qs_upvote.save();
                                                                            Toast.makeText(ViewAnswer.this, "Upvote obj saved", Toast.LENGTH_LONG).show();
                                                                        } catch (ParseException e1) {
                                                                            Toast.makeText(ViewAnswer.this, "Error Saving Upvote obj", Toast.LENGTH_LONG).show();
                                                                        }
                                                                        //Parse CLoud COde
                                                                        HashMap<String, String> params = new HashMap<String, String>();
                                                                        params.put("answer_id", a_id);
                                                                        ParseCloud.callFunctionInBackground("Ans_Upvote", params, new FunctionCallback<Map<String, String>>() {
                                                                            public void done(Map<String, String> result, ParseException e) {
                                                                                if (e == null) {
                                                                                    //success
                                                                                } else {
                                                                                    Log.d("Error is : ", e.getMessage());
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }

                                                            });
                                                        }
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(ViewAnswer.this, "Already Upvoted", Toast.LENGTH_LONG).show();
                                                upvote.setText("Upvoted");
                                            }
                                        } else {
                                            Toast.makeText(ViewAnswer.this, "Error Upvoting", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(ViewAnswer.this, "Self Upvoting", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Toast.makeText(ViewAnswer.this, "User Error", Toast.LENGTH_LONG).show();
            }
        }

    }


    public void ansDownvoteHandler(View v) {

        View parentView = (View) v.getParent();
        ad = (TextView) parentView.findViewById(R.id.tv_ans_id);
        final String a_id = ad.getText().toString();
        current_user = ParseUser.getCurrentUser();
        downvote = (Button) parentView.findViewById(R.id.ans_downvote);
        upvote = (Button) parentView.findViewById(R.id.ans_upvote);


        //fetching current ans obj

        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Answer");
        try {
            ans_obj = query1.get(a_id);
        } catch (ParseException e) {
            Toast.makeText(ViewAnswer.this, "Query Error", Toast.LENGTH_LONG).show();
        }


        if (downvote.getText().toString().equals("Downvote") == true) {
            if (current_user != null) {

                ParseQuery<ParseObject> selftupvotingquery = ParseQuery.getQuery("Answer");
                selftupvotingquery.whereEqualTo("User_Id", current_user);
                selftupvotingquery.whereEqualTo("objectId", a_id);
                selftupvotingquery.findInBackground(new FindCallback<ParseObject>() {

                    public void done(List<ParseObject> selfList, ParseException e) {
                        if (e == null) {
                            if (selfList.size() > 0) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Self Downvote not allowed!", Toast.LENGTH_LONG);
                                toast.show();
                            } else {
                                ParseQuery<ParseObject> firstquery = ParseQuery.getQuery("Ans_Downvote");
                                firstquery.include("Ans_Id");
                                firstquery.include("User_Id");
                                firstquery.whereEqualTo("Ans_Id", ans_obj);
                                firstquery.whereEqualTo("User_Id", current_user);
                                firstquery.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(List<ParseObject> ansList, ParseException e) {
                                        if (e == null) {
                                            if (ansList.size() == 0) {
                                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Answer");
                                                query.getInBackground(a_id, new GetCallback<ParseObject>() {

                                                    public void done(final ParseObject answer, ParseException e) {
                                                        if (e == null) {
                                                            ans_obj = answer;
                                                            answer.increment("Downvote_Count");
                                                            if (upvote.getText().toString().equals("Upvoted")) {
                                                                int uvc = (int) answer.get("Upvote_Count");
                                                                uvc = uvc - 1;
                                                                //Parse Cloud Code call
                                                                HashMap<String, String> params = new HashMap<String, String>();
                                                                params.put("answer_id", a_id);
                                                                ParseCloud.callFunctionInBackground("Ans_Downvote", params, new FunctionCallback<Map<String, String>>() {
                                                                    public void done(Map<String, String> result, ParseException e) {
                                                                        if (e == null) {
                                                                            //success
                                                                        } else {
                                                                            Log.d("Error is : ", e.getMessage());
                                                                        }
                                                                    }
                                                                });
                                                                answer.put("Upvote_Count", uvc);
                                                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Ans_Upvote");
                                                                query.whereEqualTo("Ans_Id", ans_obj);
                                                                query.findInBackground(new FindCallback<ParseObject>() {
                                                                    @Override
                                                                    public void done(List<ParseObject> list, ParseException e) {
                                                                        if (e == null) {
                                                                            list.get(0).deleteInBackground();
                                                                            upvote.setText("Upvote");
                                                                        } else {
                                                                            Toast.makeText(ViewAnswer.this, "Error", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });

                                                            }
                                                            //CLoud Code call
                                                            HashMap<String, String> params = new HashMap<String, String>();
                                                            params.put("answer_id", a_id);
                                                            ParseCloud.callFunctionInBackground("Ans_Downvote", params, new FunctionCallback<Map<String, String>>() {
                                                                public void done(Map<String, String> result, ParseException e) {
                                                                    if (e == null) {
                                                                        //success
                                                                    } else {
                                                                        Log.d("Error is : ", e.getMessage());
                                                                    }
                                                                }
                                                            });


                                                            answer.saveInBackground(new SaveCallback() {
                                                                @Override
                                                                public void done(ParseException e) {
                                                                    if (e == null) {
                                                                        ParseObject as_downvote = new ParseObject("Ans_Downvote");
                                                                        as_downvote.put("Ans_Id", ans_obj);
                                                                        as_downvote.put("User_Id", current_user);
                                                                        downvote.setText("Downvoted");
                                                                        try {
                                                                            as_downvote.save();
                                                                            Toast.makeText(ViewAnswer.this, "Downvoted", Toast.LENGTH_LONG).show();
                                                                        } catch (ParseException e1) {
                                                                            Toast.makeText(ViewAnswer.this, "Error Saving Downvote obj", Toast.LENGTH_LONG).show();
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            } else
                                            {
                                                Toast.makeText(ViewAnswer.this, "Already Downvoted", Toast.LENGTH_LONG).show();
                                                downvote.setText("Downvoted");
                                            }
                                        } else
                                        {
                                            Toast.makeText(ViewAnswer.this, "Error Downvoting", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });
                            }

                        } else {
                            Toast.makeText(ViewAnswer.this, "Self Downvoting", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Toast.makeText(ViewAnswer.this, "User Error", Toast.LENGTH_LONG).show();
            }
        }

    }


    public void CommentHandler(View v) {
        View parentView = (View) v.getParent();
        ad = (TextView) parentView.findViewById(R.id.tv_ans_id);
        final String a_id = ad.getText().toString();
        comment = (Button) parentView.findViewById(R.id.btn_comment);

        /*final Dialog dialog = new Dialog(context);

        dialog.setContentView(R.layout.activity_add_comment);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.));
        dialog.show();*/

        Intent intent = new Intent(ViewAnswer.this, AddComment.class);
        //NOTE: THE MOST IMP STEP ==> PASSING QuesID THROUGH INTENT TO NEXT ACTIVITY
        intent.putExtra("ans_id", a_id);
        startActivity(intent);
    }


    public void showMore(View v) {
        View parentView = (View) v.getParent();
        ad = (TextView) parentView.findViewById(R.id.tv_ans_id);
        final String a_id = ad.getText().toString();


        ParseQuery<ParseObject> q = ParseQuery.getQuery("Answer");
        try {
            ans_obj = q.get(a_id);
        } catch (ParseException e) {
            Toast.makeText(ViewAnswer.this, "Query Error", Toast.LENGTH_LONG).show();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setItems(R.array.options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showupvotes(a_id);
                }
                if (which == 1) {

                    file = ans_obj.getParseFile("Data");
                    if (file != null) {
                        String url = file.getUrl();
                        try {
                            Intent bi = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(bi);
                        } catch (ActivityNotFoundException ae) {
                            Toast.makeText(ViewAnswer.this, "No application can handle this request."
                                    + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(ViewAnswer.this, "No attached Files", Toast.LENGTH_LONG).show();
                    }

                }
                if (which == 2) {
                    Intent intent = new Intent(ViewAnswer.this, CommentView.class);
                    //NOTE: THE MOST IMP STEP ==> PASSING AnsID THROUGH INTENT TO NEXT ACTIVITY
                    intent.putExtra("ans_id", a_id);
                    startActivity(intent);
                }

                if (which == 3) {
                    reportanshandler(a_id);
                }
                if (which == 4) {
                    editanswer(a_id);
                }

            }
        });

        AlertDialog d = builder.create();
        d.show();

    }

    public void showdata(View v) {
        View parentView = (View) v.getParent();
        ad = (TextView) parentView.findViewById(R.id.tv_ans_id);
        final String a_id = ad.getText().toString();

        ParseQuery<ParseObject> q = ParseQuery.getQuery("Answer");
        try {
            ans_obj = q.get(a_id);
        } catch (ParseException e) {
            Toast.makeText(ViewAnswer.this, "Query Error", Toast.LENGTH_LONG).show();
        }
        file = ans_obj.getParseFile("Data");
        if (file != null) {
            String url = file.getUrl();
            try {
                Intent bi = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(bi);
            } catch (ActivityNotFoundException ae) {
                Toast.makeText(ViewAnswer.this, "No application can handle this request."
                        + " Please install a webbrowser", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(ViewAnswer.this, "No attached Files", Toast.LENGTH_LONG).show();
        }
    }


    public void reportanshandler(String answer) {
        final String a_id = answer;
        // loading
        final ProgressDialog QSloader = new ProgressDialog(ViewAnswer.this);
        QSloader.setTitle("Please wait.");
        QSloader.setMessage("Reporting Answer..");
        QSloader.show();


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Answer");
        query.getInBackground(a_id, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    if (!object.getBoolean("By_Admin"))
                    {
                        if (!object.getBoolean("IsReported")) {
                            if (!object.getParseObject("User_Id").getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                                object.put("IsReported", true);
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            HashMap<String, String> params = new HashMap<String, String>();
                                            params.put("answer_id", a_id);
                                            ParseCloud.callFunctionInBackground("Ans_Downvote", params, new FunctionCallback<Map<String, String>>() {
                                                public void done(Map<String, String> result, ParseException e) {
                                                    if (e == null) {
                                                        //success
                                                        QSloader.dismiss();
                                                        Toast toast = Toast.makeText(getApplicationContext(), "Answer Reported Successfully !", Toast.LENGTH_LONG);
                                                        toast.show();
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
                                QSloader.dismiss();
                                Toast.makeText(getApplicationContext(), "Owner can not report the question", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            QSloader.dismiss();
                            Toast.makeText(getApplicationContext(), "Already Reported", Toast.LENGTH_LONG).show();
                        }
                }
                    else
                    {
                        QSloader.dismiss();
                        Toast.makeText(getApplicationContext(), "Answer By Staff!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    public void editanswer(String id)
    {

        ParseQuery<ParseObject> q = ParseQuery.getQuery("Answer");
        try {
            ans_obj = q.get(id);
        } catch (ParseException e) {
            Toast.makeText(ViewAnswer.this, "Query Error", Toast.LENGTH_LONG).show();
        }

        if(ans_obj.getParseObject("User_Id").getObjectId().equals(ParseUser.getCurrentUser().getObjectId()))
        {
            Intent intent = new Intent(ViewAnswer.this, EditAnswer.class);
            //NOTE: THE MOST IMP STEP ==> PASSING AnsID THROUGH INTENT TO NEXT ACTIVITY
            intent.putExtra("ans_id", id);
            intent.putExtra("ques_id", ques_id);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(ViewAnswer.this, "Only Owner can edit question", Toast.LENGTH_LONG).show();
        }
    }

    public void showupvotes(String id)
    {
        ParseQuery<ParseObject> q = ParseQuery.getQuery("Answer");
        try {
            ans_obj = q.get(id);
        } catch (ParseException e) {
            Toast.makeText(ViewAnswer.this, "Query Error", Toast.LENGTH_LONG).show();
        }


            ParseQuery<ParseObject> query = ParseQuery.getQuery("Ans_Upvote");
            query.whereEqualTo("Ans_Id", ans_obj);
            query.include("Ans_Id");
            query.include("User_Id");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> usrlist, ParseException e) {
                    if (e == null) {
                        int len = usrlist.size();
                        if (len == 0) {
                            Toast.makeText(ViewAnswer.this, "No Upvotes", Toast.LENGTH_LONG).show();
                        } else {
                            final String[] unames = new String[len];
                            final String[] uids = new String[len];
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);

                            for (int i = 0; i < len; i++) {
                                //String t=list.get(i).getString("Name");
                                uids[i] = usrlist.get(i).getParseUser("User_Id").getObjectId();

                                String you = usrlist.get(i).getParseUser("User_Id").getString("Name");
                                if (you.equals(ParseUser.getCurrentUser().getString("Name"))) {
                                    unames[i] = "You";
                                } else {
                                    unames[i] = usrlist.get(i).getParseUser("User_Id").getString("Name");
                                }
                                builder.setTitle("Upvotes(" + len + ")");

                                builder.setItems(unames, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (unames[which] != "You") {
                                            String obid = uids[which];
                                            Intent intent = new Intent(ViewAnswer.this, UserProfile.class);
                                            //NOTE: THE MOST IMP STEP ==> PASSING USERID THROUGH INTENT TO NEXT ACTIVITY
                                            intent.putExtra("user_id", obid);
                                            startActivity(intent);
                                        }
                                    }
                                });
                            }
                            AlertDialog d = builder.create();
                            d.show();
                        }
                    } else {
                        Toast.makeText(ViewAnswer.this, "Query Error", Toast.LENGTH_LONG).show();
                    }

                }
            });
    }


}



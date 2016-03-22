package com.example.sid.campusconnect.Comment_Reply.ViewComment;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.MainActivity;
import com.example.sid.campusconnect.R;
import com.example.sid.campusconnect.Answer.ViewAnswer.ViewAnswer;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentView extends ListActivity {
    protected List<ParseObject> mStatus;
    ParseObject ans,ques,rep,c_obj;
    ParseObject answerer,cmt_obj;
    TextView tvf,tvq,tag;
    final Context context = this;
    Button extra;

    //Layout replyV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_view);
        Intent intent = getIntent();
        final String a_id = intent.getStringExtra("ans_id");

        final ProgressDialog loader = new ProgressDialog(CommentView.this);
        loader.setTitle("Please wait.");
        loader.setMessage("Loading Comments..");
        loader.show();

        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Answer");
        query1.include("User_Id");
        query1.include("Qs_Id");
        try {
            ans=query1.get(a_id);
        } catch (ParseException e) {
            Toast.makeText(CommentView.this,"Query Error",Toast.LENGTH_LONG).show();
        }

        answerer= (ParseObject) ans.get("User_Id");
        ques= (ParseObject) ans.get("Qs_Id");

        String answer=ans.getString("Description");
        String answrer=answerer.getString("Name");
        String q = ques.getString("Title");

        tvf=(TextView) findViewById(R.id.cmt_ans);
        tvq=(TextView) findViewById(R.id.cmt_ques);
        tag=(TextView) findViewById(R.id.cmttag);
        tvf.setText(answrer+"'s Answer: "+answer);
        tvq.setText(q);

        final String q_id=ques.getObjectId();

        ParseUser current_user = ParseUser.getCurrentUser();

        if (current_user != null)
        {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Comment");
            //query.whereNotEqualTo("IsReported",true);
            query.whereEqualTo("Ans_Id", ans);
            query.include("User_Id");
            query.include("Ans_Id");
            query.include("Tagged_User");
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> ansList, ParseException e) {
                    if (e == null) {
                        int length = ansList.size();
                        if (length == 0) {
                            Toast toast = Toast.makeText(getApplicationContext(), "No Comments available!", Toast.LENGTH_LONG);
                            toast.show();
                            loader.dismiss();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(CommentView.this, ViewAnswer.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("ques_id", q_id);
                                    startActivity(intent);
                                }
                            }, 5000);
                        } else {
                            loader.dismiss();
                            mStatus = ansList;
                            CommentViewAdapter adapter = new CommentViewAdapter(getListView().getContext(), mStatus);
                            setListAdapter(adapter);
                        }
                    } else {
                        Log.d("Answer ", "Error: " + e.getMessage());
                    }
                }
            });

        } else
        {
            Intent intent1 = new Intent(CommentView.this, MainActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
        }
    }

    public void ReplyPopup(View v)
    {
        View parentView = (View) v.getParent();
        Button uvote = (Button) parentView.findViewById(R.id.cmt_upvote);
        final TextView cmtid= (TextView) parentView.findViewById(R.id.tv_cmt_id);
        final String id = cmtid.getText().toString();
        final EditText rply;

        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Comment");
        try {
            cmt_obj=query1.get(id);
        } catch (ParseException e) {
            Toast.makeText(CommentView.this,"Query Error",Toast.LENGTH_LONG).show();
        }


        ImageButton send;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.popup_reply);
        dialog.show();
        send=(ImageButton) dialog.findViewById(R.id.btnsend);
        rply=(EditText) dialog.findViewById(R.id.edt_reply);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String reply=rply.getText().toString();
                if(!reply.trim().equals(""))
                {
                    final ProgressDialog loader = new ProgressDialog(CommentView.this);
                    loader.setTitle("Please wait.");
                    loader.setMessage("Submitting Reply..");
                    loader.show();

                    ParseUser cur = ParseUser.getCurrentUser();
                    if(!cur.getObjectId().equals(cmt_obj.getParseUser("User_Id").getObjectId())) {
                        final ParseObject obj = new ParseObject("Comment");
                        obj.put("User_Id", cur);
                        obj.put("Body", reply);
                        obj.put("By_Admin", cur.getBoolean("Is_Admin"));
                        obj.put("Upvote_Count", 0);
                        obj.put("Downvote_Count", 0);
                        obj.put("is_Reported", false);
                        obj.put("Ans_Id",ans);
                        obj.put("Tagged_User", cmt_obj.getParseUser("User_Id"));
                        try {
                            obj.save();
                            loader.dismiss();
                            dialog.dismiss();
                            Toast.makeText(CommentView.this, "Submitted!", Toast.LENGTH_LONG).show();

                            ParseObject not = new ParseObject("Notification");
                            not.put("Reply", obj);
                            not.put("Comment", obj);
                            not.put("Type", "Comment");
                            not.put("User_Id", cmt_obj.getParseUser("User_Id"));
                            not.put("Notifier", ParseUser.getCurrentUser());
                            not.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
                                        pushQuery.whereEqualTo("user",cmt_obj.getParseUser("User_Id"));



                                        JSONObject jsondata = null;
                                        try {
                                            jsondata = new JSONObject();
                                            jsondata.put("username", ParseUser.getCurrentUser().getString("Name"));
                                            jsondata.put("text","tagged you in a comment");
                                            jsondata.put("comment",reply);
                                            jsondata.put("type","comment");
                                        }
                                        catch (JSONException e1)
                                        {
                                            e1.printStackTrace();
                                        }

                                        //notification
                                        ParsePush push = new ParsePush();
                                        push.setQuery(pushQuery);
                                        push.setData(jsondata);
                                        push.sendInBackground(new SendCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    Toast toast = Toast.makeText(getApplicationContext(), "PUSH Send!", Toast.LENGTH_LONG);
                                                    toast.show();
                                                } else {
                                                    Toast toast = Toast.makeText(getApplicationContext(), "NOT Send!", Toast.LENGTH_LONG);
                                                    toast.show();
                                                }
                                            }
                                        });

                                    } else {
                                        Toast toast = Toast.makeText(CommentView.this, e.getMessage(), Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                }
                            });
                        } catch (ParseException e) {
                            loader.dismiss();
                            e.printStackTrace();
                        }
                    }
                    else{
                        Toast toast = Toast.makeText(CommentView.this, "Cannot reply to yourself!", Toast.LENGTH_LONG);
                        toast.show();
                        loader.dismiss();
                    }

                }
            }
        });

    }

    public void cmtUpvoteHandler(View v)
    {
        View parentView = (View) v.getParent();
        final Button upvote = (Button) parentView.findViewById(R.id.cmt_upvote);
        TextView cmtid= (TextView) parentView.findViewById(R.id.tv_cmt_id);
        final String c_id = cmtid.getText().toString();

        final ParseUser current_user = ParseUser.getCurrentUser();


        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Comment");
        query1.include("Ans_Id");
        query1.include("User_Id");
        try {
            c_obj = query1.get(c_id);
        } catch (ParseException e) {
            Toast.makeText(CommentView.this, "Query Error", Toast.LENGTH_LONG).show();
        }

        if (upvote.getText().toString().equals("Upvote") == true) {
            if (current_user != null) {
                ParseQuery<ParseObject> selftupvotingquery = ParseQuery.getQuery("Comment");
                selftupvotingquery.whereEqualTo("User_Id", current_user);
                selftupvotingquery.whereEqualTo("objectId", c_id);
                selftupvotingquery.findInBackground(new FindCallback<ParseObject>() {

                    public void done(List<ParseObject> selfList, ParseException e) {
                        if (e == null) {
                            if (selfList.size() > 0) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Self Upvote not allowed!", Toast.LENGTH_LONG);
                                toast.show();
                            } else {
                                ParseQuery<ParseObject> firstquery = ParseQuery.getQuery("Cmt_Upvote");
                                firstquery.include("Cmt_Id");
                                firstquery.include("User_Id");
                                firstquery.whereEqualTo("Cmt_Id", c_obj);
                                firstquery.whereEqualTo("User_Id", current_user);
                                firstquery.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(final List<ParseObject> cmtList, ParseException e) {
                                        if (e == null) {
                                            if (cmtList.size() == 0) {
                                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Comment");
                                                query.getInBackground(c_id, new GetCallback<ParseObject>() {

                                                    public void done(ParseObject comment, ParseException e) {
                                                        if (e == null) {
                                                            c_obj = comment;
                                                            comment.increment("Upvote_Count");


                                                            comment.saveInBackground(new SaveCallback() {
                                                                @Override
                                                                public void done(ParseException e) {
                                                                    if (e == null) {
                                                                        ParseObject c_upvote = new ParseObject("Cmt_Upvote");
                                                                        c_upvote.put("Cmt_Id", c_obj);
                                                                        c_upvote.put("User_Id", current_user);
                                                                        c_upvote.put("Ans_Id",c_obj.getParseObject("Ans_Id"));
                                                                        upvote.setText("Upvoted");
                                                                        try {
                                                                            c_upvote.save();
                                                                            Toast.makeText(CommentView.this, "Upvote obj saved", Toast.LENGTH_LONG).show();
                                                                        } catch (ParseException e1) {
                                                                            Toast.makeText(CommentView.this, "Error Saving Upvote obj", Toast.LENGTH_LONG).show();
                                                                        }
                                                                        //Parse CLoud COde
                                                                        HashMap<String, String> params = new HashMap<String, String>();
                                                                        params.put("comment_id", c_id);
                                                                        ParseCloud.callFunctionInBackground("Cmt_Upvote", params, new FunctionCallback<Map<String, String>>() {
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
                                                Toast.makeText(CommentView.this, "Already Upvoted", Toast.LENGTH_LONG).show();
                                                upvote.setText("Upvoted");
                                            }
                                        } else {
                                            Toast.makeText(CommentView.this, "Error Upvoting", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(CommentView.this, "Self Upvoting", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Toast.makeText(CommentView.this, "User Error", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void cmtReportHandler(View v)
    {
        View parentView = (View) v.getParent();
        TextView cmtid= (TextView) parentView.findViewById(R.id.tv_cmt_id);
        final String c_id = cmtid.getText().toString();

        final ProgressDialog QSloader = new ProgressDialog(CommentView.this);
        QSloader.setTitle("Please wait.");
        QSloader.setMessage("Reporting Comment..");
        QSloader.show();

        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Comment");
        query1.include("Ans_Id");
        query1.include("User_Id");
        try {
            c_obj = query1.get(c_id);
        } catch (ParseException e) {
            Toast.makeText(CommentView.this, "Query Error", Toast.LENGTH_LONG).show();
        }

        c_obj.put("is_Reported", true);
        c_obj.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("comment_id", c_id);
                ParseCloud.callFunctionInBackground("Cmt_Downvote", params, new FunctionCallback<Map<String, String>>() {
                    public void done(Map<String, String> result, ParseException e) {
                        if (e == null) {
                            //success
                            QSloader.dismiss();
                            Toast toast = Toast.makeText(getApplicationContext(), "Comment Reported Successfully !", Toast.LENGTH_LONG);
                            toast.show();
                        } else {
                            Log.d("Error is : ", e.getMessage());
                        }
                    }
                });

            }
        });

    }


}

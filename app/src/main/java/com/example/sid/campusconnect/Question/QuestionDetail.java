package com.example.sid.campusconnect.Question;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.Comment_Reply.AddComment.AddComment;
import com.example.sid.campusconnect.Answer.AddAnswer.AddAnswer;
import com.example.sid.campusconnect.Answer.EditAnswer.EditAnswer;
import com.example.sid.campusconnect.MainActivity;
import com.example.sid.campusconnect.Question.AddQuestion.AddQuestion;
import com.example.sid.campusconnect.AskStaff.AskStaff;
import com.example.sid.campusconnect.DiscussionRoom.CreateDiscussion;
import com.example.sid.campusconnect.DiscussionRoom.ViewDiscussionRoom;
import com.example.sid.campusconnect.Question.EditQuestion.EditQuestion;
import com.example.sid.campusconnect.Home.Home;
import com.example.sid.campusconnect.Notification.Notifications;
import com.example.sid.campusconnect.Answer.ReportAnswer.ReportedAns;
import com.example.sid.campusconnect.R;
import com.example.sid.campusconnect.Comment_Reply.ReportComment.ReportedComment;
import com.example.sid.campusconnect.Question.ReportQuestion.ReportedQuestion;
import com.example.sid.campusconnect.ReportedUser.ReportedUsers;
import com.example.sid.campusconnect.Parse_Session.SessionChecker;
import com.example.sid.campusconnect.StaffRequest.StaffRequest;
import com.example.sid.campusconnect.UserProfile.Profile;
import com.example.sid.campusconnect.UserProfile.UserProfile;
import com.example.sid.campusconnect.VerifyStudent.StudentVerify;
import com.example.sid.campusconnect.Answer.ViewAnswer.ViewAnswer;
import com.example.sid.campusconnect.Answer.ViewAnswer.ViewAnswerListAdapter;
import com.example.sid.campusconnect.Comment_Reply.ViewComment.CommentView;
import com.example.sid.campusconnect.ViewUpvotes.ViewUpvotes;
import com.example.sid.campusconnect.Answer.WriteAnswer.WriteAnswer;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionDetail extends Activity implements NavigationView.OnNavigationItemSelectedListener
{
    protected TextView title;
    protected TextView des;
    protected TextView cat;
    protected TextView username;
    protected TextView qupvote;
    protected TextView qdownvote;
    protected TextView adddate;
    protected TextView usertype;
    protected TextView userprofile;
    protected Button upvotehandler;
    protected Button downvotehandler;
    protected Button writeans;
    protected Button reportqs;
    protected String qstitle;
    ParseFile file;

    android.support.v7.widget.Toolbar toolbar;
    TextView fullname;
    ImageView imageView;
    String img_url;

    TextView ad;
    ParseObject obj_qs,object_qs,ans_obj;
    String quest;
    String s;
    java.sql.Date dob = null;
    ParseObject questionid;
    int upvote_count = 0;
    int downvote_count = 0;
    Context context = this;
    ListView ans_List;
    protected List<ParseObject> mStatus;
    ParseUser current_user;
    Button comment,upvote,downvote;     //Answers buttons

    protected boolean edtqs,rptqus,dscs,ifupovted,ifdownvoted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);

        title=(TextView)findViewById(R.id.textView26);
        des=(TextView)findViewById(R.id.textView25);
        cat=(TextView)findViewById(R.id.textView24);
        username=(TextView)findViewById(R.id.textView23);
        qupvote=(TextView)findViewById(R.id.textView22);
        qdownvote=(TextView)findViewById(R.id.textView21);
        adddate=(TextView)findViewById(R.id.textView20);
        usertype=(TextView)findViewById(R.id.textView19);
        edtqs = false;
        rptqus = true;
        dscs = true;
        ans_List = (ListView) findViewById(R.id.ansList);


        //buttons
        upvotehandler=(Button)findViewById(R.id.Upvote);
        downvotehandler=(Button)findViewById(R.id.Downvote);
        writeans=(Button)findViewById(R.id.WriteAns);

        ifupovted=false;
        ifdownvoted=false;


        final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

        //Receiving question_id through intent
        Intent intent = getIntent();
        final String question_id = intent.getStringExtra("question_id");
        quest=question_id;

        //  progress dialog
        final ProgressDialog qsloader = new ProgressDialog(QuestionDetail.this);
        qsloader.setTitle("Please wait.");
        qsloader.setMessage("Loading Question Details..");
        qsloader.show();

        //getting question details
        current_user = ParseUser.getCurrentUser();

        initializeDrawer();

        if (current_user != null)
        {
            final boolean usertyp;

            if (current_user.getBoolean("Is_Admin") == true) {
                usertyp = true;
            }
            else
            {
                usertyp = false;
            }

            final ParseQuery<ParseObject> query = ParseQuery.getQuery("Question");
            query.include("User_id");
            query.getInBackground(question_id, new GetCallback<ParseObject>() {
                        public void done(ParseObject question, ParseException e) {
                            if (e == null) {
                                questionid = question;
                                obj_qs = question;
                                //getting userid
                                ParseObject users = question.getParseUser("User_id");
                                String name = users.getString("Name");
                                username.setText(name);

                                s = users.getObjectId();

                                ParseUser c = ParseUser.getCurrentUser();

                                ParseQuery<ParseObject> precheck = ParseQuery.getQuery("Qs_Upvote");
                                precheck.whereEqualTo("Qs_Id", questionid);
                                precheck.whereEqualTo("User_Id", c);
                                precheck.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(List<ParseObject> resultList, ParseException e) {
                                        if (e == null) {
                                            if (resultList.size() == 0) {
                                            } else {
                                                ifupovted=true;
                                                upvotehandler.setCompoundDrawablesWithIntrinsicBounds(R.drawable.upvotefill, 0, 0, 0);
                                            }
                                        } else {
                                            Log.d("Question", "Error: " + e.getMessage());
                                        }
                                    }
                                });

                                ParseQuery<ParseObject> predowncheck = ParseQuery.getQuery("Qs_Downvote");
                                predowncheck.whereEqualTo("Qs_Id", questionid);
                                predowncheck.whereEqualTo("User_Id", c);
                                predowncheck.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(List<ParseObject> resultList, ParseException e) {
                                        if (e == null) {
                                            if (resultList.size() == 0) {
                                            } else
                                            {
                                                ifdownvoted=true;
                                                downvotehandler.setCompoundDrawablesWithIntrinsicBounds(R.drawable.downvotefill, 0, 0, 0);
                                            }
                                        } else {
                                            Log.d("Question", "Error: " + e.getMessage());
                                        }
                                    }
                                });

                                String q =question_id;

                                ParseQuery<ParseObject> sup = ParseQuery.getQuery("Question");
                                sup.whereEqualTo("User_id", c);
                                sup.whereEqualTo("objectId",q);
                                sup.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(List<ParseObject> reList, ParseException e) {
                                        if (e == null) {
                                            if (reList.size() != 0) {
                                                // implement already reported qs
                                                upvotehandler.setClickable(false);
                                                downvotehandler.setClickable(false);
                                                rptqus = false;
                                                edtqs=true;
                                            } else {
                                            }
                                        } else {
                                            Log.d("score", "Error: " + e.getMessage());
                                        }
                                    }
                                });
                                //getting title
                                qstitle = question.getString("Title");
                                title.setText(qstitle);

                                ParseQuery<ParseObject> discheck = ParseQuery.getQuery("Discuss_Room");
                                discheck.whereEqualTo("Subject", qstitle);
                                discheck.whereEqualTo("Status", true);
                                discheck.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(List<ParseObject> disList, ParseException e) {
                                        if (e == null)
                                        {
                                            int length = disList.size();
                                            if(length==0)
                                            {
                                                //do nothing
                                            }
                                            else
                                            {
                                                dscs=false;
                                            }
                                        }
                                        else
                                        {
                                            Log.d("score", "Error: " + e.getMessage());
                                        }
                                    }
                                });
                                // another check
                                Boolean isdiscuss = question.getBoolean("Is_Discuss");
                                if(isdiscuss)
                                {
                                    dscs=false;
                                }
                                else
                                {
                                    //do nothing
                                }
                                //description
                                String qsdes = question.getString("Description");
                                des.setText(qsdes);

                                //fetching isreported
                                Boolean isreported =question.getBoolean("Is_Reported");
                                if(isreported)
                                {
                                    rptqus = false;
                                }
                                else
                                {
                                    // do nothing
                                }


                                //category
                                String qscat = question.getString("Category");
                                cat.setText(qscat);

                                //upvote

                                upvote_count=question.getInt("Upvote_Count");
                                String upvoted = String.valueOf(question.getInt("Upvote_Count"));
                                qupvote.setText(upvoted);

                                //downvote

                                downvote_count=question.getInt("Downvote_Count");
                                String down = String.valueOf(question.getInt("Downvote_Count"));
                                qdownvote.setText(down);

                                //usertype
                                final boolean usert = question.getBoolean("By_Admin");
                                if (usert) {
                                    usertype.setText("Administrator");
                                } else {
                                    usertype.setText("Student");
                                }

                                //qs added time
                                Date lastactive = question.getUpdatedAt();
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                                String l = lastactive.toString();
                                adddate.setText(formatter.format(lastactive).toString());
                                qsloader.dismiss();
                            }
                            else
                            {
                                Log.d("Question : Error is : ", e.getMessage());
                            }
                        }
                    }

            );
        }
        else
        {
            Intent gotohome = new Intent(QuestionDetail.this, Home.class);
            gotohome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(gotohome);
        }


        // answers

        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Question");
        try {
            object_qs = query1.get(quest);
        } catch (ParseException e) {
            Toast.makeText(QuestionDetail.this, "Query Error", Toast.LENGTH_LONG).show();
        }
        if (current_user != null) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Answer");
            query.whereEqualTo("Qs_Id", object_qs);
            query.include("User_Id");
            query.include("Qs_Id");
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> ansList, ParseException e) {
                    if (e == null) {
                        int length = ansList.size();
                        if (length == 0) {
                            Toast toast = Toast.makeText(getApplicationContext(), "No Answers!", Toast.LENGTH_LONG);
                            toast.show();
                        } else {
                            mStatus = ansList;
                            ViewAnswerListAdapter adapter = new ViewAnswerListAdapter(ans_List.getContext(), mStatus);
                            ans_List.setAdapter(adapter);
                        }
                    } else {
                        Log.d("Question ", "Error: " + e.getMessage());
                    }
                }
            });

        } else {
            Intent intent1 = new Intent(QuestionDetail.this, MainActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
        }


        // upvote qs
        upvotehandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  progress dialog


                //getting current user
                // getting qs id
                // below code :
                // prevents :
                // self upvoting
                // multiple upvotes
                // it does : increment upvote count
                // populating qs upvote table
                // N saving users --> user,qs id values
                // increment the value in the textview of upvote count

                final ParseUser current_user = ParseUser.getCurrentUser();
                final String qs_id = question_id;

                if (current_user != null) {
                    final ProgressDialog dlg = new ProgressDialog(QuestionDetail.this);
                    dlg.setTitle("Please wait.");
                    dlg.setMessage("Upvoting Question.Please wait.");
                    dlg.show();

                    if(ifupovted==false)
                    {
                        ParseQuery<ParseObject> selftupvotingquery = ParseQuery.getQuery("Question");
                        selftupvotingquery.whereEqualTo("User_id", current_user);
                        selftupvotingquery.whereEqualTo("objectId", qs_id);
                        selftupvotingquery.findInBackground(new FindCallback<ParseObject>() {

                            public void done(List<ParseObject> selfList, ParseException e) {
                                if (e == null) {
                                    if (selfList.size() != 0) {
                                        upvotehandler.setClickable(false);
                                        dlg.dismiss();
                                        Toast toast = Toast.makeText(getApplicationContext(), "No Self Upvoting !", Toast.LENGTH_LONG);
                                        toast.show();
                                    } else {
                                        ParseQuery<ParseObject> firstquery = ParseQuery.getQuery("Qs_Upvote");
                                        firstquery.whereEqualTo("Qs_Id", questionid);
                                        firstquery.whereEqualTo("User_Id", current_user);
                                        firstquery.findInBackground(new FindCallback<ParseObject>() {
                                            public void done(List<ParseObject> questionList, ParseException e) {
                                                if (e == null) {
                                                    if (questionList.size() == 0) {
                                                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Question");
                                                        query.getInBackground(qs_id, new GetCallback<ParseObject>() {

                                                            public void done(ParseObject question, ParseException e) {
                                                                if (e == null) {
                                                                    question.increment("Upvote_Count");
                                                                    question.saveInBackground(new SaveCallback() {
                                                                        @Override
                                                                        public void done(ParseException e) {
                                                                            if (e == null) {
                                                                                ParseObject qs_upvote = new ParseObject("Qs_Upvote");
                                                                                qs_upvote.put("Qs_Id", questionid);
                                                                                qs_upvote.put("User_Id", current_user);
                                                                                qs_upvote.saveInBackground(new SaveCallback() {
                                                                                    @Override
                                                                                    public void done(ParseException e) {
                                                                                        if (e == null) {
                                                                                            HashMap<String, String> params = new HashMap<String, String>();
                                                                                            params.put("question_id", question_id);
                                                                                            ParseCloud.callFunctionInBackground("PointsIncrement", params, new FunctionCallback<Map<String, String>>() {
                                                                                                public void done(Map<String, String> result, ParseException e) {
                                                                                                    if (e == null) {
                                                                                                        //success
                                                                                                    } else {
                                                                                                        Log.d("Error is : ", e.getMessage());
                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                            upvote_count = upvote_count + 1;
                                                                                            String u = String.valueOf(upvote_count);
                                                                                            qupvote.setText(u);
                                                                                            ifupovted=true;
                                                                                            upvotehandler.setCompoundDrawablesWithIntrinsicBounds(R.drawable.upvotefill, 0, 0, 0);
                                                                                            dlg.dismiss();
                                                                                            Toast toast = Toast.makeText(getApplicationContext(), "Question Upvoted Successfully!", Toast.LENGTH_LONG);
                                                                                            toast.show();
                                                                                        } else {
                                                                                            Log.d("Upvote Error", e.getMessage());

                                                                                        }
                                                                                    }
                                                                                });
                                                                            } else {
                                                                                Log.d("Upvote Error", e.getMessage());
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });

                                                    } else {
                                                        ifupovted=true;
                                                        upvotehandler.setCompoundDrawablesWithIntrinsicBounds(R.drawable.upvotefill, 0, 0, 0);
                                                        dlg.dismiss();
                                                        Toast toast = Toast.makeText(getApplicationContext(), "Question Already Upvoted!", Toast.LENGTH_LONG);
                                                        toast.show();

                                                    }
                                                } else {
                                                    Log.d("score", "Error: " + e.getMessage());
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    ifdownvoted=true;
                                    dlg.dismiss();
                                    Toast toast = Toast.makeText(getApplicationContext(), "No Self Upvoting !", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            }
                        });

                    }
                } else {
                    Intent gotohome = new Intent(QuestionDetail.this, Home.class);
                    gotohome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(gotohome);
                }

            }
        });


        //downvote qs

        downvotehandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                final ParseUser current_user = ParseUser.getCurrentUser();
                final String qs_id = question_id;

                if (current_user != null) {
                    if(ifdownvoted==false)
                    {
                        final ProgressDialog dlg = new ProgressDialog(QuestionDetail.this);
                        dlg.setTitle("Please wait.");
                        dlg.setMessage("Downvoting Question.Please wait...");
                        dlg.show();

                        ParseQuery<ParseObject> selftupvotingquery = ParseQuery.getQuery("Question");
                        selftupvotingquery.whereEqualTo("User_id", current_user);
                        selftupvotingquery.whereEqualTo("objectId", qs_id);
                        selftupvotingquery.findInBackground(new FindCallback<ParseObject>() {

                            public void done(List<ParseObject> selfList, ParseException e) {
                                if (e == null) {
                                    if (selfList.size() != 0) {
                                        downvotehandler.setClickable(false);
                                        dlg.dismiss();
                                        Toast toast = Toast.makeText(getApplicationContext(), "No Self Downvoting !", Toast.LENGTH_LONG);
                                        toast.show();

                                    } else {
                                        ParseQuery<ParseObject> firstquery = ParseQuery.getQuery("Qs_Downvote");
                                        firstquery.whereEqualTo("Qs_Id", questionid);
                                        firstquery.whereEqualTo("User_Id", current_user);
                                        firstquery.findInBackground(new FindCallback<ParseObject>() {
                                            public void done(List<ParseObject> questionList, ParseException e) {
                                                if (e == null) {
                                                    if (questionList.size() == 0) {
                                                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Question");
                                                        query.getInBackground(qs_id, new GetCallback<ParseObject>() {

                                                            public void done(ParseObject question, ParseException e) {
                                                                if (e == null) {
                                                                    question.increment("Downvote_Count");
                                                                    question.saveInBackground(new SaveCallback() {
                                                                        @Override
                                                                        public void done(ParseException e) {
                                                                            if (e == null) {
                                                                                ParseObject qs_upvote = new ParseObject("Qs_Downvote");
                                                                                qs_upvote.put("Qs_Id", questionid);
                                                                                qs_upvote.put("User_Id", current_user);
                                                                                qs_upvote.saveInBackground(new SaveCallback() {
                                                                                    @Override
                                                                                    public void done(ParseException e) {
                                                                                        if (e == null) {

                                                                                            HashMap<String, String> params = new HashMap<String, String>();
                                                                                            params.put("question_id", question_id);

                                                                                            ParseCloud.callFunctionInBackground("PointsDecrement", params, new FunctionCallback<Map<String, String>>() {
                                                                                                public void done(Map<String, String> result, ParseException e) {
                                                                                                    if (e == null) {
                                                                                                        //success
                                                                                                    } else {
                                                                                                        Log.d("Error is : ", e.getMessage());
                                                                                                    }
                                                                                                }
                                                                                            });


                                                                                            ifdownvoted=true;
                                                                                            downvotehandler.setCompoundDrawablesWithIntrinsicBounds(R.drawable.downvotefill, 0, 0, 0);
                                                                                            downvote_count = downvote_count + 1;
                                                                                            String u = String.valueOf(downvote_count);
                                                                                            qdownvote.setText(u);
                                                                                            dlg.dismiss();
                                                                                            Toast toast = Toast.makeText(getApplicationContext(), "Question Downvoted Successfully!", Toast.LENGTH_LONG);
                                                                                            toast.show();

                                                                                        } else {
                                                                                            Log.d("Downvote Error", e.getMessage());

                                                                                        }
                                                                                    }
                                                                                });
                                                                            } else {
                                                                                Log.d("Downvote Error", e.getMessage());
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });

                                                    } else {
                                                        ifdownvoted=true;
                                                        downvotehandler.setCompoundDrawablesWithIntrinsicBounds(R.drawable.downvotefill, 0, 0, 0);
                                                        dlg.dismiss();
                                                        Toast toast = Toast.makeText(getApplicationContext(), "Question Already Downvoted!", Toast.LENGTH_LONG);
                                                        toast.show();

                                                    }
                                                } else {
                                                    Log.d("error", "Error: " + e.getMessage());
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    downvotehandler.setClickable(false);
                                    dlg.dismiss();
                                    Toast toast = Toast.makeText(getApplicationContext(), "No Self Downvoting !", Toast.LENGTH_LONG);
                                    toast.show();

                                }
                            }
                        });
                    }
                }
                else {
                    Intent gotohome = new Intent(QuestionDetail.this, Home.class);
                    gotohome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(gotohome);
                }
            }
        });

    }

    public void UserProfileHandler(View v) {
        Intent intent = new Intent(QuestionDetail.this, UserProfile.class);
        //NOTE: THE MOST IMP STEP ==> PASSING USERID THROUGH INTENT TO NEXT ACTIVITY
        intent.putExtra("user_id", s);
        startActivity(intent);
    }

    public void ViewUserUpvotesHandler(View v)
    {
        Intent intent = new Intent(QuestionDetail.this,ViewUpvotes.class);
        intent.putExtra("question_id",quest);
        startActivity(intent);
    }

    public void WriteAnsHandler(View v)
    {
        Intent intent = new Intent(QuestionDetail.this,AddAnswer.class);
        //NOTE: THE MOST IMP STEP ==> PASSING QuesID THROUGH INTENT TO NEXT ACTIVITY
        intent.putExtra("ques_id", quest);
        startActivity(intent);
    }
    public void showExtra(View view)
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);


        builder1.setItems(R.array.qsextra, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 1) {
                    // discuss question
                    if (dscs == true) {
                        String questi_id = quest;
                        Intent intent = new Intent(QuestionDetail.this, CreateDiscussion.class);
                        intent.putExtra("question_id", questi_id);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Discussion Room already exists...", Toast.LENGTH_LONG).show();
                    }
                }
                if (which == 0) {
                    //View Answer
                    Intent intent = new Intent(QuestionDetail.this, ViewAnswer.class);
                    //NOTE: THE MOST IMP STEP ==> PASSING QuesID THROUGH INTENT TO NEXT ACTIVITY
                    intent.putExtra("ques_id", quest);
                    startActivity(intent);
                }

                //ask staff
                if (which == 2) {
                    final String quest_id = quest;
                    final ParseUser current_user = ParseUser.getCurrentUser();
                    // loading
                    final ProgressDialog SRLoader = new ProgressDialog(QuestionDetail.this);
                    SRLoader.setTitle("Please wait.");
                    SRLoader.setMessage("Getting staff list...");
                    SRLoader.show();

                    ParseQuery<ParseObject> question = ParseQuery.getQuery("Question");
                    question.getInBackground(quest_id, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if (e == null) {
                                object = questionid;
                            } else {
                                Log.d("Error is : ", e.getMessage());
                            }

                        }
                    });


                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Question");
                    query.getInBackground(quest_id, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            //ParseObject question = new ParseObject("Question");
                            boolean check = object.getBoolean("Is_Requested");

                            if (e == null) {
                                if (check == true) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Question is already requested for a staff !", Toast.LENGTH_LONG);
                                    toast.show();
                                    SRLoader.dismiss();
                                } else {
                                    SRLoader.dismiss();
                                    Intent intent = new Intent(QuestionDetail.this, AskStaff.class);
                                    //NOTE: THE MOST IMP STEP ==> PASSING QuestionID THROUGH INTENT TO NEXT ACTIVITY
                                    intent.putExtra("question_id", quest_id);
                                    startActivity(intent);

                                }


                            } else {
                                Log.d("Error is : ", e.getMessage());
                            }

                        }
                    });
                }

                //edit qs
                if (which == 3) {
                    if (edtqs == true) {
                        String questi_id = quest;
                        Intent intent = new Intent(QuestionDetail.this, EditQuestion.class);
                        intent.putExtra("question_id", questi_id);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Only owner can edit the Question!", Toast.LENGTH_LONG).show();
                    }
                }

                //report qs
                if (which == 4) {
                    //if question posted by admin
                    if (rptqus == true) {
                        Boolean byadmin = obj_qs.getBoolean("By_Admin");
                        if (byadmin) {
                            Toast.makeText(getApplicationContext(), "Cannot report. Question posted by Admin!", Toast.LENGTH_LONG).show();
                        } else {

                            final ProgressDialog QSloader = new ProgressDialog(QuestionDetail.this);
                            QSloader.setTitle("Please wait.");
                            QSloader.setMessage("Reporting Question..");
                            QSloader.show();

                            String quest_id = quest;

                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Question");
                            query.getInBackground(quest_id, new GetCallback<ParseObject>() {
                                public void done(ParseObject object, ParseException e) {
                                    if (e == null) {
                                        object.put("Is_Reported", true);
                                        object.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    HashMap<String, String> params = new HashMap<String, String>();
                                                    params.put("question_id", quest);
                                                    ParseCloud.callFunctionInBackground("PointsDecrement", params, new FunctionCallback<Map<String, String>>() {
                                                        public void done(Map<String, String> result, ParseException e) {
                                                            if (e == null) {
                                                                //success
                                                                reportqs.setText("Reported");
                                                                reportqs.setClickable(false);
                                                                QSloader.dismiss();
                                                                Toast toast = Toast.makeText(getApplicationContext(), "Question Reported Successfully !", Toast.LENGTH_LONG);
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
                                    }
                                }
                            });
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Cannot Report This question", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        builder1.show();
    }

    ////////////////////////////////////////////ANSWERS\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
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
            Toast.makeText(QuestionDetail.this, "Query Error", Toast.LENGTH_LONG).show();
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
                                                                            Toast.makeText(QuestionDetail.this, "Error", Toast.LENGTH_SHORT).show();
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
                                                                            Toast.makeText(QuestionDetail.this, "Upvote obj saved", Toast.LENGTH_LONG).show();
                                                                        } catch (ParseException e1) {
                                                                            Toast.makeText(QuestionDetail.this, "Error Saving Upvote obj", Toast.LENGTH_LONG).show();
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
                                                Toast.makeText(QuestionDetail.this, "Already Upvoted", Toast.LENGTH_LONG).show();
                                                upvote.setText("Upvoted");
                                            }
                                        } else {
                                            Toast.makeText(QuestionDetail.this, "Error Upvoting", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(QuestionDetail.this, "Self Upvoting", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Toast.makeText(QuestionDetail.this, "User Error", Toast.LENGTH_LONG).show();
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
            Toast.makeText(QuestionDetail.this, "Query Error", Toast.LENGTH_LONG).show();
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
                                                                            Toast.makeText(QuestionDetail.this, "Error", Toast.LENGTH_SHORT).show();
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
                                                                            Toast.makeText(QuestionDetail.this, "Downvoted", Toast.LENGTH_LONG).show();
                                                                        } catch (ParseException e1) {
                                                                            Toast.makeText(QuestionDetail.this, "Error Saving Downvote obj", Toast.LENGTH_LONG).show();
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(QuestionDetail.this, "Already Downvoted", Toast.LENGTH_LONG).show();
                                                downvote.setText("Downvoted");
                                            }
                                        } else {
                                            Toast.makeText(QuestionDetail.this, "Error Downvoting", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });
                            }

                        } else {
                            Toast.makeText(QuestionDetail.this, "Self Downvoting", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Toast.makeText(QuestionDetail.this, "User Error", Toast.LENGTH_LONG).show();
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

        Intent intent = new Intent(QuestionDetail.this, AddComment.class);
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
            Toast.makeText(QuestionDetail.this, "Query Error", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(QuestionDetail.this, "No application can handle this request."
                                    + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(QuestionDetail.this, "No attached Files", Toast.LENGTH_LONG).show();
                    }

                }
                if (which == 2) {
                    reportanshandler(a_id);
                }

                if (which == 3) {
                    Intent intent = new Intent(QuestionDetail.this, CommentView.class);
                    //NOTE: THE MOST IMP STEP ==> PASSING AnsID THROUGH INTENT TO NEXT ACTIVITY
                    intent.putExtra("ans_id", a_id);
                    startActivity(intent);

                }
                if(which == 4){
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
            Toast.makeText(QuestionDetail.this, "Query Error", Toast.LENGTH_LONG).show();
        }
        file = ans_obj.getParseFile("Data");
        if (file != null) {
            String url = file.getUrl();
            try {
                Intent bi = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(bi);
            } catch (ActivityNotFoundException ae) {
                Toast.makeText(QuestionDetail.this, "No application can handle this request."
                        + " Please install a webbrowser", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(QuestionDetail.this, "No attached Files", Toast.LENGTH_LONG).show();
        }
    }


    public void reportanshandler(String answer) {
        final String a_id = answer;
        // loading
        final ProgressDialog QSloader = new ProgressDialog(QuestionDetail.this);
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
            Toast.makeText(QuestionDetail.this, "Query Error", Toast.LENGTH_LONG).show();
        }

        if(ans_obj.getParseObject("User_Id").getObjectId().equals(ParseUser.getCurrentUser().getObjectId()))
        {
            Intent intent = new Intent(QuestionDetail.this, EditAnswer.class);
            //NOTE: THE MOST IMP STEP ==> PASSING AnsID THROUGH INTENT TO NEXT ACTIVITY
            intent.putExtra("ans_id", id);
            intent.putExtra("ques_id", quest);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(QuestionDetail.this, "Only Owner can edit question", Toast.LENGTH_LONG).show();
        }
    }

    public void showupvotes(String id)
    {
        ParseQuery<ParseObject> q = ParseQuery.getQuery("Answer");
        try {
            ans_obj = q.get(id);
        } catch (ParseException e) {
            Toast.makeText(QuestionDetail.this, "Query Error", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(QuestionDetail.this, "No Upvotes", Toast.LENGTH_LONG).show();
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
                                        Intent intent = new Intent(QuestionDetail.this, UserProfile.class);
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
                    Toast.makeText(QuestionDetail.this, "Query Error", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void showans(View v)
    {
        TextView toolbar = (TextView) findViewById(R.id.qd_toolbar_title);
        ScrollView sc = (ScrollView)findViewById(R.id.scrl);
        Button showans = (Button)findViewById(R.id.showAns);
        if(ans_List.getVisibility()== View.GONE)
        {
            showans.setText("Show Question");
            sc.setVisibility(View.GONE);
            ans_List.setVisibility(View.VISIBLE);
             toolbar.setText(qstitle);

        }
        else
        {
            showans.setText("Show Answers");
            sc.setVisibility(View.VISIBLE);
            ans_List.setVisibility(View.GONE);
            toolbar.setText("Question Details");
        }

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
        return true;
    }

    return super.onOptionsItemSelected(item);
}

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id==R.id.nav_Discussqs)
        {
            startActivity(new Intent(QuestionDetail.this,ViewDiscussionRoom.class));
        }
        else if(id==R.id.nav_writeans){
            startActivity(new Intent(QuestionDetail.this,WriteAnswer.class));
        }
        else if (id==R.id.nav_Viewqs)
        {
            startActivity(new Intent(QuestionDetail.this,QuestionDetail.class));
        }
        else if(id==R.id.nav_addquestion)
        {
            startActivity(new Intent(QuestionDetail.this,AddQuestion.class));
        }
        else if(id==R.id.nav_Reportqs)
        {
            if(current_user.getBoolean("Is_Admin")==true)
            {
                startActivity(new Intent(QuestionDetail.this,ReportedQuestion.class));
            }
            else
            {
                Toast.makeText(QuestionDetail.this,"Only Staff has this right",Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.nav_Reportas) {
            if (current_user.getBoolean("Is_Admin") == true) {
                startActivity(new Intent(QuestionDetail.this, ReportedAns.class));
            } else {
                Toast.makeText(QuestionDetail.this, "Only Staff has this right", Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.nav_Reportcmt) {
            if (current_user.getBoolean("Is_Admin") == true) {
                startActivity(new Intent(QuestionDetail.this, ReportedComment.class));
            } else {
                Toast.makeText(QuestionDetail.this, "Only Staff has this right", Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.nav_Reportusr) {
            if (current_user.getBoolean("Is_Admin") == true) {
                startActivity(new Intent(QuestionDetail.this, ReportedUsers.class));
            } else {
                Toast.makeText(QuestionDetail.this, "Only Staff has this right", Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.nav_manage)
        {
            if(current_user.getBoolean("Is_Admin")==true)
            {
                startActivity(new Intent(QuestionDetail.this,StudentVerify.class));
            }
            else
            {
                Toast.makeText(QuestionDetail.this,"Only Staff has this right",Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.nav_staffRequest)
        {
            if(current_user.getBoolean("Is_Admin")==true)
            {
                startActivity(new Intent(QuestionDetail.this,StaffRequest.class));
            }
            else
            {
                Toast.makeText(QuestionDetail.this,"Only Staff has this right",Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.nav_pro)
        {
            startActivity(new Intent(QuestionDetail.this,Profile.class));

        } else if (id == R.id.nav_logout) {
            ParseUser.getCurrentUser().logOut();
            Intent intent = new Intent(QuestionDetail.this,SessionChecker.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else if (id==R.id.nav_Notification){
            startActivity(new Intent(QuestionDetail.this,Notifications.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initializeDrawer()
    {
        current_user=ParseUser.getCurrentUser();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        imageView = (ImageView) drawer.findViewById(R.id.nav_imageView);
        ParseFile imageFile = (ParseFile) current_user.get("Profile_pic");
        fullname = (TextView) drawer.findViewById(R.id.nav_fullname);
        fullname.setText(current_user.get("Name").toString());

        try {
            img_url = imageFile.getUrl();
            byte[] bitmapdata = imageFile.getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e1) {
            img_url = "";
            Toast.makeText(QuestionDetail.this, "Please set you DP", Toast.LENGTH_LONG).show();
        }
        NavigationView navigationView = (NavigationView) findViewById(R .id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


}
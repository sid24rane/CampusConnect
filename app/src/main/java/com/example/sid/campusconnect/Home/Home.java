package com.example.sid.campusconnect.Home;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.Comment_Reply.AddComment.AddComment;
import com.example.sid.campusconnect.Question.AddQuestion.AddQuestion;
import com.example.sid.campusconnect.DiscussionRoom.ViewDiscussionRoom;
import com.example.sid.campusconnect.Answer.EditAnswer.EditAnswer;
import com.example.sid.campusconnect.MainActivity;
import com.example.sid.campusconnect.Notification.Notifications;
import com.example.sid.campusconnect.UserProfile.Profile;
import com.example.sid.campusconnect.R;
import com.example.sid.campusconnect.Answer.ReportAnswer.ReportedAns;
import com.example.sid.campusconnect.Comment_Reply.ReportComment.ReportedComment;
import com.example.sid.campusconnect.Question.ReportQuestion.ReportedQuestion;
import com.example.sid.campusconnect.ReportedUser.ReportedUsers;
import com.example.sid.campusconnect.Parse_Session.SessionChecker;
import com.example.sid.campusconnect.StaffRequest.StaffRequest;
import com.example.sid.campusconnect.UserProfile.UserProfile;
import com.example.sid.campusconnect.VerifyStudent.StudentVerify;
import com.example.sid.campusconnect.Comment_Reply.ViewComment.CommentView;
import com.example.sid.campusconnect.Question.ViewQuestion.ViewQuestion;
import com.example.sid.campusconnect.Answer.WriteAnswer.WriteAnswer;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Home extends ListActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    int counter = 10, refresh=0;
    Toolbar toolbar;
    TextView fullname,ad,ustatus,dstatus;
    ImageView imageView;
    String img_url;
    ParseUser user;
    ParseObject ans_obj;

    InputStream in = null;
    private boolean doubleBackToExitPressedOnce = false, isover=false, ifmore = false ;
    int len;
    protected List<ParseObject> mStatus;
    ListView lv;
    HomeAdapter adapter;
    Button upvote,downvote;
    SwipeRefreshLayout swipeLayout;
    Spinner qs_spinner,ans_spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //more = (Button) findViewById(R.id.btn_LoadMore);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        qs_spinner = (Spinner) findViewById(R.id.ques_spinner);
        ans_spinner = (Spinner) findViewById(R.id.ans_spinner);

        user = ParseUser.getCurrentUser();
        //tv1 = (TextView) findViewById(R.id.test_tv);

        final ProgressDialog QSloader = new ProgressDialog(Home.this);
        QSloader.setTitle("Please wait.");
        QSloader.setMessage("Loading Questions..");
        QSloader.show();

        // Associate the device with a user
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user", ParseUser.getCurrentUser());
        installation.saveInBackground();


        user.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, com.parse.ParseException e) {
                if (e == null) {
                    int bpoints = (int) user.get("Bad_Points");

                    if (bpoints > 150) {
                        user.logOut();
                        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                        builder.setMessage("You are temporarily banned from the Campus Connect! Visit your staff & say sorry :P")
                                .setTitle("Login Error!")
                                .setCancelable(false)
                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Home.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            }
        });


        final ParseQuery<ParseObject> qsquery = ParseQuery.getQuery("Question");
        qsquery.whereEqualTo("Is_Answered", true);
        qsquery.orderByDescending("updatedAt");
        qsquery.setLimit(10);
        //query.whereNotEqualTo("Is_Reported",true);
        qsquery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> questionList, com.parse.ParseException e) {
                {
                    int length = questionList.size();
                    if (length == 0) {
                        Toast toast = Toast.makeText(getApplicationContext(), "No Questions!", Toast.LENGTH_LONG);
                        toast.show();
                        QSloader.dismiss();
                    } else {
                        QSloader.dismiss();
                        mStatus = questionList;
                        adapter = new HomeAdapter(getListView().getContext(), mStatus,"Popular");
                        setListAdapter(adapter);
                    }
                }
            }
        });


        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadnew();
                // swipeLayout.setRefreshing(false);
            }
        });

        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            boolean scroll = false;

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                lv = getListView();

                int lastInScreen = firstVisibleItem + visibleItemCount;
                if (lastInScreen == totalItemCount) {
                    scroll = true;
                } else {
                    scroll = false;
                }

                /*if((mLastFirstVisibleItem>firstVisibleItem)&&(firstVisibleItem==0))
                {
                    refresh=true;
                }
                else
                {
                    refresh=false;
                }
                mLastFirstVisibleItem=firstVisibleItem;*/

            }


            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {


                if ((scroll) && (!isover)) {
                    loadmore(counter);
                    counter += 5;
                }


            }
        });


        initializeDrawer();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Notification");
        query.include("User_Id");
        query.whereEqualTo("User_Id", user);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, com.parse.ParseException e) {
                //len=list.size();

            }
        });

        qs_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerAction();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });

        ans_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerAction();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (doubleBackToExitPressedOnce) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        // DrawerLayout dv = (DrawerLayout) findViewById(R.id.drawer_layout);


        if(len>0) {
            NavigationView nv = (NavigationView) findViewById(R.id.nav_view);
            SpannableString s = new SpannableString("Notifications ( " + len + " )");
            s.setSpan(new ForegroundColorSpan(Color.BLUE), 0, s.length(), 0);
            nv.getMenu().getItem(0).setTitle(s);
        }


        return true;
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
            startActivity(new Intent(Home.this,ViewDiscussionRoom.class));
        }
        else if(id==R.id.nav_writeans){
            startActivity(new Intent(Home.this,WriteAnswer.class));
        }
        else if (id==R.id.nav_Viewqs)
        {
            startActivity(new Intent(Home.this,ViewQuestion.class));
        }
        else if(id==R.id.nav_addquestion)
        {
            startActivity(new Intent(Home.this,AddQuestion.class));
        }
        else if(id==R.id.nav_Reportqs)
        {
            if(user.getBoolean("Is_Admin")==true)
            {
                startActivity(new Intent(Home.this,ReportedQuestion.class));
            }
            else
            {
                Toast.makeText(Home.this,"Only Staff has this right",Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.nav_Reportas) {
            if (user.getBoolean("Is_Admin") == true) {
                startActivity(new Intent(Home.this, ReportedAns.class));
            } else {
                Toast.makeText(Home.this, "Only Staff has this right", Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.nav_Reportcmt) {
            if (user.getBoolean("Is_Admin") == true) {
                startActivity(new Intent(Home.this, ReportedComment.class));
            } else {
                Toast.makeText(Home.this, "Only Staff has this right", Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.nav_Reportusr) {
            if (user.getBoolean("Is_Admin") == true) {
                startActivity(new Intent(Home.this, ReportedUsers.class));
            } else {
                Toast.makeText(Home.this, "Only Staff has this right", Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.nav_manage)
        {
            if(user.getBoolean("Is_Admin")==true)
            {
                startActivity(new Intent(Home.this,StudentVerify.class));
            }
            else
            {
                Toast.makeText(Home.this,"Only Staff has this right",Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.nav_staffRequest)
        {
            if(user.getBoolean("Is_Admin")==true)
            {
                startActivity(new Intent(Home.this,StaffRequest.class));
            }
            else
            {
                Toast.makeText(Home.this,"Only Staff has this right",Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.nav_pro)
        {
            startActivity(new Intent(Home.this,Profile.class));

        } else if (id == R.id.nav_logout) {
            ParseUser.getCurrentUser().logOut();
            Intent intent = new Intent(Home.this,SessionChecker.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else if (id==R.id.nav_Notification){
            startActivity(new Intent(Home.this,Notifications.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadmore(int counter) {
        final ProgressDialog QSloader = new ProgressDialog(Home.this);
        QSloader.setTitle("Please wait.");
        QSloader.setMessage("Loading Questions..");
        QSloader.show();

        String selected = String.valueOf(qs_spinner.getSelectedItem());
        final String ans_type = String.valueOf(ans_spinner.getSelectedItem());


        ParseQuery<ParseObject> qsquery = ParseQuery.getQuery("Question");
        qsquery.setSkip(counter + refresh);
        if (!selected.toLowerCase().equals("all"))
        {
            qsquery.whereEqualTo("Category", selected);
        }
        qsquery.setLimit(5);
        qsquery.orderByDescending("updatedAt");
        //query.whereNotEqualTo("Is_Reported",true);
        qsquery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> questionList, com.parse.ParseException e) {
                if (e == null) {
                    int length = questionList.size();
                    if (length == 0) {
                        Toast toast = Toast.makeText(getApplicationContext(), "No more Questions!", Toast.LENGTH_LONG);
                        toast.show();
                        isover = true;
                        QSloader.dismiss();
                    } else {
                        QSloader.dismiss();
                        adapter.addAll(questionList);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Log.d("Question ", "Error: " + e.getMessage());
                }
            }
        });

    }

    public void loadnew() {

        String selected = String.valueOf(qs_spinner.getSelectedItem());
        final String ans_type = String.valueOf(ans_spinner.getSelectedItem());


        final ParseQuery<ParseObject> qsquery = ParseQuery.getQuery("Question");
        qsquery.whereEqualTo("Is_Answered", true);
        if (!selected.toLowerCase().equals("all"))
        {
            qsquery.whereEqualTo("Category", selected);
        }
        qsquery.orderByDescending("updatedAt");
        qsquery.whereGreaterThan("updatedAt", mStatus.get(0).getUpdatedAt());
        //query.whereNotEqualTo("Is_Reported",true);
        qsquery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> questionList, com.parse.ParseException e) {
                if (e == null) {
                    int length = questionList.size();
                    if (length == 0) {
                        Toast toast = Toast.makeText(getApplicationContext(), "No new Questions!", Toast.LENGTH_LONG);
                        toast.show();
                        swipeLayout.setRefreshing(false);
                    } else {
                        for (int i = 0; i < length; i++) {
                            for (int x = 0; x < counter; x++) {
                                if (adapter.getItem(x).getObjectId().equals(questionList.get(i).getObjectId())) {
                                    adapter.remove(adapter.getItem(x));
                                }
                            }
                            adapter.insert(questionList.get(i), i);
                            refresh++;
                        }

                        adapter.notifyDataSetChanged();
                        swipeLayout.setRefreshing(false);
                        Toast.makeText(getApplicationContext(), "New Questions!", Toast.LENGTH_LONG).show();

                    }
                } else {
                    Log.d("Question ", "Error: " + e.getMessage());
                }
            }
        });

    }

    public void initializeDrawer()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        imageView = (ImageView) drawer.findViewById(R.id.nav_imageView);
        fullname = (TextView) drawer.findViewById(R.id.nav_fullname);
        fullname.setText(user.get("Name").toString());
        ParseFile imageFile = (ParseFile) user.get("Profile_pic");

        try {
            img_url = imageFile.getUrl();
            byte[] bitmapdata = imageFile.getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e1) {
            img_url = "";
            Toast.makeText(Home.this, "Please set you DP", Toast.LENGTH_LONG).show();
        }
        NavigationView navigationView = (NavigationView) findViewById(R .id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void ansUpvoteHandler(View v) {
        View parentView = (View) v.getParent();
        ad = (TextView) parentView.findViewById(R.id.tv_ans_id);
        ustatus = (TextView) parentView.findViewById(R.id.ustatus);
        dstatus = (TextView) parentView.findViewById(R.id.dstatus);
        final String a_id = ad.getText().toString();
        user = ParseUser.getCurrentUser();
        upvote = (Button) parentView.findViewById(R.id.ans_upvote);
        downvote = (Button) parentView.findViewById(R.id.ans_downvote);


        //fetching current ans obj
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Answer");
        try {
            ans_obj = query1.get(a_id);
        } catch (com.parse.ParseException e) {
            Toast.makeText(Home.this, "Query Error", Toast.LENGTH_LONG).show();
        }

        if (ustatus.getText().toString().equals("upvote") == true) {
            if (user != null) {
                ParseQuery<ParseObject> selftupvotingquery = ParseQuery.getQuery("Answer");
                selftupvotingquery.whereEqualTo("User_Id", user);
                selftupvotingquery.whereEqualTo("objectId", a_id);
                selftupvotingquery.findInBackground(new FindCallback<ParseObject>() {

                    public void done(List<ParseObject> selfList, com.parse.ParseException e) {
                        if (e == null) {
                            if (selfList.size() > 0) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Self Upvote not allowed!", Toast.LENGTH_LONG);
                                toast.show();
                            } else {
                                ParseQuery<ParseObject> firstquery = ParseQuery.getQuery("Ans_Upvote");
                                firstquery.include("Ans_Id");
                                firstquery.include("User_Id");
                                firstquery.whereEqualTo("Ans_Id", ans_obj);
                                firstquery.whereEqualTo("User_Id", user);
                                firstquery.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(final List<ParseObject> ansList, com.parse.ParseException e) {
                                        if (e == null) {
                                            if (ansList.size() == 0) {
                                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Answer");
                                                query.getInBackground(a_id, new GetCallback<ParseObject>() {

                                                    public void done(ParseObject answer, com.parse.ParseException e) {
                                                        if (e == null) {
                                                            ans_obj = answer;
                                                            answer.increment("Upvote_Count");
                                                            if (dstatus.getText().toString().equals("downvoted")) {

                                                                int dvc = (int) answer.get("Upvote_Count");
                                                                dvc--;
                                                                answer.put("Downvote_Count", dvc);
                                                                //CLoude Code Call
                                                                HashMap<String, String> params = new HashMap<String, String>();
                                                                params.put("answer_id", a_id);
                                                                ParseCloud.callFunctionInBackground("Ans_Upvote", params, new FunctionCallback<Map<String, String>>() {
                                                                    public void done(Map<String, String> result, com.parse.ParseException e) {
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

                                                                    public void done(List<ParseObject> list, com.parse.ParseException e) {
                                                                        if (e == null) {
                                                                            list.get(0).deleteInBackground();
                                                                            dstatus.setText("downvote");
                                                                            downvote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.downvoteblank,0,0,0);

                                                                        } else {
                                                                            Toast.makeText(Home.this, "Error", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                            }

                                                            answer.saveInBackground(new SaveCallback() {
                                                                @Override
                                                                public void done(com.parse.ParseException e) {
                                                                    if (e == null) {
                                                                        ParseObject qs_upvote = new ParseObject("Ans_Upvote");
                                                                        qs_upvote.put("Ans_Id", ans_obj);
                                                                        qs_upvote.put("User_Id", user);
                                                                        ustatus.setText("upvoted");
                                                                        upvote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.upvote,0,0,0);
                                                                        try {
                                                                            qs_upvote.save();
                                                                            Toast.makeText(Home.this, "Upvote obj saved", Toast.LENGTH_LONG).show();
                                                                        }
                                                                        catch (com.parse.ParseException e1) {
                                                                            e1.printStackTrace();
                                                                        }
                                                                        //Parse CLoud COde
                                                                        HashMap<String, String> params = new HashMap<String, String>();
                                                                        params.put("answer_id", a_id);
                                                                        ParseCloud.callFunctionInBackground("Ans_Upvote", params, new FunctionCallback<Map<String, String>>() {
                                                                            @Override
                                                                            public void done(Map<String, String> stringStringMap, com.parse.ParseException e) {
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
                                                Toast.makeText(Home.this, "Already Upvoted", Toast.LENGTH_LONG).show();
                                                ustatus.setText("upvoted");
                                                upvote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.upvote, 0, 0, 0);
                                            }
                                        } else {
                                            Toast.makeText(Home.this, "Error Upvoting", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(Home.this, "Self Upvoting", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Toast.makeText(Home.this, "User Error", Toast.LENGTH_LONG).show();
            }
        }

    }



    public void ansDownvoteHandler(View v) {

        View parentView = (View) v.getParent();
        ad = (TextView) parentView.findViewById(R.id.tv_ans_id);
        final String a_id = ad.getText().toString();
        user = ParseUser.getCurrentUser();
        ustatus = (TextView) parentView.findViewById(R.id.ustatus);
        dstatus = (TextView) parentView.findViewById(R.id.dstatus);
        downvote = (Button) parentView.findViewById(R.id.ans_downvote);
        upvote = (Button) parentView.findViewById(R.id.ans_upvote);


        //fetching current ans obj

        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Answer");
        try {
            ans_obj = query1.get(a_id);
        } catch (com.parse.ParseException e) {
            Toast.makeText(Home.this, "Query Error", Toast.LENGTH_LONG).show();
        }


        if (dstatus.getText().toString().equals("downvote") == true) {
            if (user != null) {

                ParseQuery<ParseObject> selftupvotingquery = ParseQuery.getQuery("Answer");
                selftupvotingquery.whereEqualTo("User_Id", user);
                selftupvotingquery.whereEqualTo("objectId", a_id);
                selftupvotingquery.findInBackground(new FindCallback<ParseObject>() {

                    public void done(List<ParseObject> selfList, com.parse.ParseException e) {
                        if (e == null) {
                            if (selfList.size() > 0) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Self Downvote not allowed!", Toast.LENGTH_LONG);
                                toast.show();
                            } else {
                                ParseQuery<ParseObject> firstquery = ParseQuery.getQuery("Ans_Downvote");
                                firstquery.include("Ans_Id");
                                firstquery.include("User_Id");
                                firstquery.whereEqualTo("Ans_Id", ans_obj);
                                firstquery.whereEqualTo("User_Id", user);
                                firstquery.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(List<ParseObject> ansList, com.parse.ParseException e) {
                                        if (e == null) {
                                            if (ansList.size() == 0) {
                                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Answer");
                                                query.getInBackground(a_id, new GetCallback<ParseObject>() {

                                                    public void done(final ParseObject answer, com.parse.ParseException e) {
                                                        if (e == null) {
                                                            ans_obj = answer;
                                                            answer.increment("Downvote_Count");
                                                            if (ustatus.getText().toString().equals("upvoted")) {
                                                                int uvc = (int) answer.get("Upvote_Count");
                                                                uvc = uvc - 1;
                                                                //Parse Cloud Code call
                                                                HashMap<String, String> params = new HashMap<String, String>();
                                                                params.put("answer_id", a_id);
                                                                ParseCloud.callFunctionInBackground("Ans_Downvote", params, new FunctionCallback<Map<String, String>>() {
                                                                    public void done(Map<String, String> result, com.parse.ParseException e) {
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
                                                                    public void done(List<ParseObject> list, com.parse.ParseException e) {
                                                                        if (e == null) {
                                                                            list.get(0).deleteInBackground();
                                                                            ustatus.setText("upvote");
                                                                            upvote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.upvoteblank,0,0,0);
                                                                        } else {
                                                                            Toast.makeText(Home.this, "Error", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });

                                                            }
                                                            //CLoud Code call
                                                            HashMap<String, String> params = new HashMap<String, String>();
                                                            params.put("answer_id", a_id);
                                                            ParseCloud.callFunctionInBackground("Ans_Downvote", params, new FunctionCallback<Map<String, String>>() {
                                                                public void done(Map<String, String> result, com.parse.ParseException e) {
                                                                    if (e == null) {
                                                                        //success
                                                                    } else {
                                                                        Log.d("Error is : ", e.getMessage());
                                                                    }
                                                                }
                                                            });


                                                            answer.saveInBackground(new SaveCallback() {
                                                                @Override
                                                                public void done(com.parse.ParseException e) {
                                                                    if (e == null) {
                                                                        ParseObject as_downvote = new ParseObject("Ans_Downvote");
                                                                        as_downvote.put("Ans_Id", ans_obj);
                                                                        as_downvote.put("User_Id", user);
                                                                        dstatus.setText("downvoted");
                                                                        downvote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.downvote, 0, 0, 0);
                                                                        try {
                                                                            as_downvote.save();
                                                                            Toast.makeText(Home.this, "Downvoted", Toast.LENGTH_LONG).show();
                                                                        } catch (com.parse.ParseException e1) {
                                                                            Toast.makeText(Home.this, "Error Saving Downvote obj", Toast.LENGTH_LONG).show();
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(Home.this, "Already Downvoted", Toast.LENGTH_LONG).show();
                                                dstatus.setText("downvoted");
                                                downvote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.downvote, 0, 0, 0);
                                            }
                                        } else {
                                            Toast.makeText(Home.this, "Error Downvoting", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });
                            }

                        } else {
                            Toast.makeText(Home.this, "Self Downvoting", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Toast.makeText(Home.this, "User Error", Toast.LENGTH_LONG).show();
            }
        }

    }


    public void CommentHandler(View v) {
                View parentView = (View) v.getParent();
        ad = (TextView) parentView.findViewById(R.id.tv_ans_id);
        final String a_id = ad.getText().toString();


        /*final Dialog dialog = new Dialog(context);

        dialog.setContentView(R.layout.activity_add_comment);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.));
        dialog.show();*/

        Intent intent = new Intent(Home.this, AddComment.class);
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
        } catch (com.parse.ParseException e) {
            Toast.makeText(Home.this, "Query Error", Toast.LENGTH_LONG).show();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);

        builder.setItems(R.array.options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showupvotes(a_id);
                }
                if (which == 1) {

                    ParseFile file = ans_obj.getParseFile("Data");
                    if (file != null) {
                        String url = file.getUrl();
                        try {
                            Intent bi = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(bi);
                        } catch (ActivityNotFoundException ae) {
                            Toast.makeText(Home.this, "No application can handle this request."
                                    + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(Home.this, "No attached Files", Toast.LENGTH_LONG).show();
                    }

                }
                if (which == 2) {
                    Intent intent = new Intent(Home.this, CommentView.class);
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



    public void reportanshandler(String answer) {
        final String a_id = answer;
        // loading
        final ProgressDialog QSloader = new ProgressDialog(Home.this);
        QSloader.setTitle("Please wait.");
        QSloader.setMessage("Reporting Answer..");
        QSloader.show();


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Answer");
        query.getInBackground(a_id, new GetCallback<ParseObject>() {
            public void done(ParseObject object, com.parse.ParseException e) {
                if (e == null) {
                    if (!object.getBoolean("By_Admin")) {
                        if (!object.getBoolean("IsReported")) {
                            if (!object.getParseObject("User_Id").getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                                object.put("IsReported", true);
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(com.parse.ParseException e) {
                                        if (e == null) {
                                            HashMap<String, String> params = new HashMap<String, String>();
                                            params.put("answer_id", a_id);
                                            ParseCloud.callFunctionInBackground("Ans_Downvote", params, new FunctionCallback<Map<String, String>>() {
                                                public void done(Map<String, String> result, com.parse.ParseException e) {
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
                    } else {
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
        q.include("Qs_id");
        try {
            ans_obj = q.get(id);
        } catch (com.parse.ParseException e) {
            Toast.makeText(Home.this, "Query Error", Toast.LENGTH_LONG).show();
        }

        final String ques_id= ans_obj.getParseObject("Qs_Id").getObjectId();



        if(ans_obj.getParseObject("User_Id").getObjectId().equals(ParseUser.getCurrentUser().getObjectId()))
        {
            Intent intent = new Intent(Home.this, EditAnswer.class);
            //NOTE: THE MOST IMP STEP ==> PASSING AnsID THROUGH INTENT TO NEXT ACTIVITY
            intent.putExtra("ans_id", id);
            intent.putExtra("ques_id", ques_id);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(Home.this, "Only Owner can edit question", Toast.LENGTH_LONG).show();
        }
    }




    public void showupvotes(String id)
    {
        ParseQuery<ParseObject> q = ParseQuery.getQuery("Answer");
        try {
            ans_obj = q.get(id);
        } catch (com.parse.ParseException e) {
            Toast.makeText(Home.this, "Query Error", Toast.LENGTH_LONG).show();
        }


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Ans_Upvote");
        query.whereEqualTo("Ans_Id", ans_obj);
        query.include("Ans_Id");
        query.include("User_Id");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> usrlist, com.parse.ParseException e) {
                if (e == null) {
                    int len = usrlist.size();
                    if (len == 0) {
                        Toast.makeText(Home.this, "No Upvotes", Toast.LENGTH_LONG).show();
                    } else {
                        final String[] unames = new String[len];
                        final String[] uids = new String[len];
                        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);

                        for (int i = 0; i < len; i++) {
                            //String t=list.get(i).getString("Name");
                            uids[i] = usrlist.get(i).getParseUser("User_Id").getObjectId();
                            String you = usrlist.get(i).getParseUser("User_Id").getObjectId();
                            if (you.equals(ParseUser.getCurrentUser().getObjectId())) {
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
                                        Intent intent = new Intent(Home.this, UserProfile.class);
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
                    Toast.makeText(Home.this, "Query Error", Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    public void SpinnerAction()
    {

        final ProgressDialog QSloader = new ProgressDialog(Home.this);
        QSloader.setTitle("Please wait.");
        QSloader.setMessage("Loading Questions..");
        QSloader.show();

        String selected = String.valueOf(qs_spinner.getSelectedItem());
        final String ans_type = String.valueOf(ans_spinner.getSelectedItem());
        isover=false;


        if (!selected.toLowerCase().equals("all")) {
            ParseQuery<ParseObject> qsquery1 = ParseQuery.getQuery("Question");
            qsquery1.whereEqualTo("Is_Answered", true);
            qsquery1.whereEqualTo("Category", selected);
            qsquery1.orderByDescending("updatedAt");
            qsquery1.setLimit(10);
            //query.whereNotEqualTo("Is_Reported",true);
            qsquery1.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> questionList, com.parse.ParseException e) {
                    {
                        int length = questionList.size();
                        if (length == 0) {
                            Toast toast = Toast.makeText(getApplicationContext(), "No Questions!", Toast.LENGTH_LONG);
                            toast.show();
                            QSloader.dismiss();
                        } else {
                            QSloader.dismiss();
                            mStatus = questionList;
                            adapter = new HomeAdapter(getListView().getContext(), mStatus,ans_type);
                            setListAdapter(adapter);
                        }
                    }
                }
            });
        } else {
            ParseQuery<ParseObject> qsquery1 = ParseQuery.getQuery("Question");
            qsquery1.whereEqualTo("Is_Answered", true);
            qsquery1.orderByDescending("updatedAt");
            qsquery1.setLimit(10);
            //query.whereNotEqualTo("Is_Reported",true);
            qsquery1.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> questionList, com.parse.ParseException e) {
                    {
                        int length = questionList.size();
                        if (length == 0) {
                            Toast toast = Toast.makeText(getApplicationContext(), "No Questions!", Toast.LENGTH_LONG);
                            toast.show();
                            QSloader.dismiss();
                        } else {
                            QSloader.dismiss();
                            mStatus = questionList;
                            adapter = new HomeAdapter(getListView().getContext(), mStatus,ans_type);
                            setListAdapter(adapter);
                        }
                    }
                }
            });
        }
    }




}

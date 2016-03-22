package com.example.sid.campusconnect.Question.ViewQuestion;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.Question.AddQuestion.AddQuestion;
import com.example.sid.campusconnect.DiscussionRoom.ViewDiscussionRoom;
import com.example.sid.campusconnect.Home.Home;
import com.example.sid.campusconnect.MainActivity;
import com.example.sid.campusconnect.Notification.Notifications;
import com.example.sid.campusconnect.UserProfile.Profile;
import com.example.sid.campusconnect.Question.QuestionDetail;
import com.example.sid.campusconnect.R;
import com.example.sid.campusconnect.Question.ReportQuestion.ReportedQuestion;
import com.example.sid.campusconnect.Answer.ReportAnswer.ReportedAns;
import com.example.sid.campusconnect.Comment_Reply.ReportComment.ReportedComment;
import com.example.sid.campusconnect.ReportedUser.ReportedUsers;
import com.example.sid.campusconnect.Parse_Session.SessionChecker;
import com.example.sid.campusconnect.StaffRequest.StaffRequest;
import com.example.sid.campusconnect.VerifyStudent.StudentVerify;
import com.example.sid.campusconnect.Answer.WriteAnswer.WriteAnswer;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ViewQuestion extends ListActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    protected List<ParseObject> mStatus;
    ParseUser user;
    android.support.v7.widget.Toolbar toolbar;
    TextView fullname;
    ImageView imageView;
    String img_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_question);
        // loading
        final ProgressDialog QSloader = new ProgressDialog(ViewQuestion.this);
        QSloader.setTitle("Please wait.");
        QSloader.setMessage("Loading Questions..");
        QSloader.show();

        user = ParseUser.getCurrentUser();

        initializeDrawer();

        if (user != null)
        {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Question");
            //query.whereNotEqualTo("Is_Reported",true);
            query.orderByDescending("updatedAt");
            query.findInBackground(new FindCallback<ParseObject>()
            {
                public void done(List<ParseObject> questionList, ParseException e) {
                    if (e == null)
                    {
                        int length = questionList.size();
                        if(length==0)
                        {
                            Toast toast = Toast.makeText(getApplicationContext(),"No Questions!",Toast.LENGTH_LONG);
                            toast.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(ViewQuestion.this, Home.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }, 5000);
                        }
                        else
                        {
                            QSloader.dismiss();
                            mStatus = questionList;
                            ViewQuestionListAdapter adapter = new ViewQuestionListAdapter(getListView().getContext(), mStatus);
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
            Intent intent = new Intent(ViewQuestion.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }





    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        // DrawerLayout dv = (DrawerLayout) findViewById(R.id.drawer_layout);

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
            startActivity(new Intent(ViewQuestion.this,ViewDiscussionRoom.class));
        }
        else if(id==R.id.nav_writeans){
            startActivity(new Intent(ViewQuestion.this,WriteAnswer.class));
        }
        else if (id==R.id.nav_Viewqs)
        {
            startActivity(new Intent(ViewQuestion.this,ViewQuestion.class));
        }
        else if(id==R.id.nav_addquestion)
        {
            startActivity(new Intent(ViewQuestion.this,AddQuestion.class));
        }
        else if(id==R.id.nav_Reportqs)
        {
            if(user.getBoolean("Is_Admin")==true)
            {
                startActivity(new Intent(ViewQuestion.this,ReportedQuestion.class));
            }
            else
            {
                Toast.makeText(ViewQuestion.this,"Only Staff has this right",Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.nav_Reportas) {
            if (user.getBoolean("Is_Admin") == true) {
                startActivity(new Intent(ViewQuestion.this, ReportedAns.class));
            } else {
                Toast.makeText(ViewQuestion.this, "Only Staff has this right", Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.nav_Reportcmt) {
            if (user.getBoolean("Is_Admin") == true) {
                startActivity(new Intent(ViewQuestion.this, ReportedComment.class));
            } else {
                Toast.makeText(ViewQuestion.this, "Only Staff has this right", Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.nav_Reportusr) {
            if (user.getBoolean("Is_Admin") == true) {
                startActivity(new Intent(ViewQuestion.this, ReportedUsers.class));
            } else {
                Toast.makeText(ViewQuestion.this, "Only Staff has this right", Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.nav_manage)
        {
            if(user.getBoolean("Is_Admin")==true)
            {
                startActivity(new Intent(ViewQuestion.this,StudentVerify.class));
            }
            else
            {
                Toast.makeText(ViewQuestion.this,"Only Staff has this right",Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.nav_staffRequest)
        {
            if(user.getBoolean("Is_Admin")==true)
            {
                startActivity(new Intent(ViewQuestion.this,StaffRequest.class));
            }
            else
            {
                Toast.makeText(ViewQuestion.this,"Only Staff has this right",Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.nav_pro)
        {
            startActivity(new Intent(ViewQuestion.this,Profile.class));

        } else if (id == R.id.nav_logout) {
            ParseUser.getCurrentUser().logOut();
            Intent intent = new Intent(ViewQuestion.this,SessionChecker.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else if (id==R.id.nav_Notification){
            startActivity(new Intent(ViewQuestion.this,Notifications.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void ReadFullQsHandler(View v)
    {

        //View parentView = (View) v.getParent();
        TextView sd = (TextView)v.findViewById(R.id.QsId);
        final String question_id = sd.getText().toString();

        Intent intent = new Intent(ViewQuestion.this,QuestionDetail.class);
        //NOTE: THE MOST IMP STEP ==> PASSING QuestionID THROUGH INTENT TO NEXT ACTIVITY
        intent.putExtra("question_id", question_id);
        startActivity(intent);
    }


    public void initializeDrawer()
    {
        user=ParseUser.getCurrentUser();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        imageView = (ImageView) drawer.findViewById(R.id.nav_imageView);
        ParseFile imageFile = (ParseFile) user.get("Profile_pic");
        fullname = (TextView) drawer.findViewById(R.id.nav_fullname);
        fullname.setText(user.get("Name").toString());

        try {
            img_url = imageFile.getUrl();
            byte[] bitmapdata = imageFile.getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e1) {
            img_url = "";
            Toast.makeText(ViewQuestion.this, "Please set you DP", Toast.LENGTH_LONG).show();
        }
        NavigationView navigationView = (NavigationView) findViewById(R .id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }



}


package com.example.sid.campusconnect.UserProfile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.Date;

// THIS CLASS/ACTIVITY IS USED TO DISPLAY PROFILE OF THE USER .
// CAN BE RE-USED FOR DISPLAYING PROFILE INFO OF BOTH STUDENTS AND ADMINS
// USAGE::
// PASS THE USER_ID OF THE USER

public class UserProfile extends AppCompatActivity {

    protected TextView name;
    protected TextView emailid;
    protected TextView userty;
    protected TextView sex;
    protected TextView deptment;
    protected TextView dateofb;
    protected TextView lastactivedate;
    protected TextView userpoints;
    protected TextView ratingTv;
    protected RatingBar rat;  // seriously its a RAT
    protected ImageView imageView;
    protected String img_url;

    //List of ratings for users
    //Based on Points

    final String novice="Novice";
    final String apprentice="Apprentice";
    final String adept ="Adept";
    final String expert="Expert";
    final String master="Master";
    java.sql.Date dob = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

        // getting all the textviews (TRUST ME ...PAIN IN the ASS)

        name=(TextView)findViewById(R.id.textView11);
        emailid=(TextView)findViewById(R.id.textView12);
        userty=(TextView)findViewById(R.id.textView13);
        sex=(TextView)findViewById(R.id.textView14);
        deptment=(TextView)findViewById(R.id.textView15);
        dateofb=(TextView)findViewById(R.id.textView16);
        lastactivedate=(TextView)findViewById(R.id.textView17);
        userpoints=(TextView)findViewById(R.id.textView18);
        rat=(RatingBar) findViewById(R.id.ratingBar);
        imageView = (ImageView) findViewById(R.id.ProfilePic);
        ratingTv = (TextView)findViewById(R.id.textView20);

        //Receiving user_id through intent
        Intent intent = getIntent();
        String user_id = intent.getStringExtra("user_id");

        //  progress dialog
        final ProgressDialog dlg = new ProgressDialog(UserProfile.this);
        dlg.setTitle("Please wait.");
        dlg.setMessage("Loading User Profile..");
        dlg.show();

        //getting user profile

        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.getInBackground(user_id , new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e)
            {
                if (e == null)
                {
                    dlg.dismiss();
                    // username
                    String username =object.getString("Name");
                    name.setText(username);

                    //email
                    String email = object.getString("email");
                    emailid.setText(email);

                    //student or admin
                    Boolean isStudent=object.getBoolean("Is_Admin");
                    if(isStudent)
                    {
                        String usertype ="Administrator";
                        userty.setText(usertype);
                    }
                    else
                    {
                        String usertype="Student";
                        userty.setText(usertype);

                    }

                    // gender
                    String gender =object.getString("Gender");
                    sex.setText(gender);

                    //department
                    String dept = object.getString("Dept");
                    deptment.setText(dept);

                    // date of birth of user

                    Date db=object.getDate("Dob");
                    dateofb.setText(dateFormat.format(db).toString());

                    // last seen/activity of user
                    Date lastactive=object.getUpdatedAt();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                    String l=lastactive.toString();
                    lastactivedate.setText(formatter.format(lastactive).toString());

                    //points of user
                    String points = String.valueOf(object.getInt("Points"));
                    userpoints.setText(points);

                    // finally rating of fucking user..
                    String no = "n";
                    String ap = "ap";
                    String ad = "ad";
                    String ex = "e";
                    String ma ="m";

                    String rating=object.getString("Rating");

                    //SETTING RATING BAR COLOR TO GREEN
                    Drawable progress = rat.getProgressDrawable();
                    DrawableCompat.setTint(progress, Color.rgb(51, 204, 51));
                    if(rating.equals(no))
                    {
                        String user_rating=novice;
                        ratingTv.setText(novice);
                        rat.setRating(1);
                        rat.setClickable(false);
                        rat.setFocusable(false);
                    }
                    else if (rating.equals(ap))
                    {
                        String user_rating=apprentice;
                        ratingTv.setText(apprentice);
                        rat.setRating(2);
                        rat.setClickable(false);
                        rat.setFocusable(false);


                    }
                    else if(rating.equals(ad))
                    {
                        String user_rating=adept;
                        ratingTv.setText(adept);
                        rat.setRating(3);
                        rat.setClickable(false);
                        rat.setFocusable(false);

                    }
                    else if(rating.equals(ex))
                    {
                        String user_rating=expert;
                        ratingTv.setText(expert);
                        rat.setRating(4);
                        rat.setClickable(false);
                        rat.setFocusable(false);

                    }
                    else
                    {
                        String user_rating=master;
                        ratingTv.setText(master);
                        rat.setRating(5);
                        rat.setClickable(false);
                        rat.setFocusable(false);
                    }


                    // dp of user
                    ParseFile imageFile = object.getParseFile("Profile_pic");

                    try {
                        img_url = imageFile.getUrl();
                        byte[] bitmapdata = imageFile.getData();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata , 0, bitmapdata.length);
                        imageView.setImageBitmap(bitmap);
                    }
                    catch (Exception e1)
                    {
                        img_url="";
                        Toast.makeText(UserProfile.this,"No Dp for this Student",Toast.LENGTH_LONG).show();
                    }


                    }
                else
                {
                    Log.d("User : Error is : ",e.getMessage());
                }
            }
        });

    }


}

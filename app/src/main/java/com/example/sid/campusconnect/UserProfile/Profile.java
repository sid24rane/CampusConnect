package com.example.sid.campusconnect.UserProfile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.Home.Home;
import com.example.sid.campusconnect.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class Profile extends AppCompatActivity {
    int i_len,s_len;


    private static int RESULT_LOAD_IMAGE = 1;
    ParseUser user;
    private  TextView email,dept,username,name,Uname;
    CollapsingToolbarLayout ct ;
    ImageView imageView;
    String img_url;
    int sizeInBytes = 0;
    int MAX_BYTES = 12000;
    boolean allowed = false;
    Uri selectedImage;
    byte[] image;
    private RatingBar rat;

    final String novice="Novice";
    final String apprentice="Apprentice";
    final String adept ="Adept";
    final String expert="Expert";
    final String master="Master";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ptoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ct = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        email = (TextView) findViewById(R.id.pro_email);
        dept =  (TextView) findViewById(R.id.pro_dept);

        name= (TextView) findViewById(R.id.pro_fullname);
        rat = (RatingBar) findViewById(R.id.ratingBar);

        user= ParseUser.getCurrentUser();
        email.setText(user.getEmail().toString());
        dept.setText(user.get("Dept").toString());
        name.setText(user.get("Name").toString());
        ct.setTitle(user.get("Name").toString());
        imageView = (ImageView) findViewById(R.id.imgview);
        ParseFile imageFile = (ParseFile) user.get("Profile_pic");

        try
        {
            img_url = imageFile.getUrl();
            byte[] bitmapdata = imageFile.getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata , 0, bitmapdata.length);
            imageView.setImageBitmap(bitmap);
        }
        catch (Exception e1)
        {
            img_url="";
            Toast.makeText(Profile.this, "No Dp for this Student", Toast.LENGTH_LONG);
        }
        findViewById(R.id.pro_btn_dp).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View arg0) {

               Intent i = new Intent(
                       Intent.ACTION_PICK,
                       MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, RESULT_LOAD_IMAGE);
           }
       });

        findViewById(R.id.pro_btn_done).setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View arg0) {
                final ProgressDialog sendqs = new ProgressDialog(Profile.this);
                sendqs.setTitle("Please wait.");
                sendqs.setMessage("Updating Profile...!");
                sendqs.show();


                if (allowed == true) {

                    try {


                        ImageView imageView = (ImageView) findViewById(R.id.imgview);
                        ParseFile file = new ParseFile("dp.png", image);
                        file.saveInBackground();

                        user.put("Profile_pic", file);
                        user.save();
                        Toast.makeText(Profile.this, "DP Uploaded", Toast.LENGTH_LONG).show();
                        imageView.setImageURI(selectedImage);
                        sendqs.dismiss();
                    } catch (Exception e1) {
                        Toast.makeText(Profile.this, "File size should be less than 1mb: ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Profile.this, "Please choose a valid file", Toast.LENGTH_SHORT).show();
                }
            }

        });


        String no = "n";
        String ap = "ap";
        String ad = "ad";
        String ex = "e";
        String ma ="m";
        String rating=user.getString("Rating").toString();

        //SETTING RATING BAR COLOR TO GREEN
        Drawable progress = rat.getProgressDrawable();
        DrawableCompat.setTint(progress, Color.rgb(51, 204, 51));

        if(rating.equals(no))
        {
            String user_rating=novice;
            rat.setRating(1);
            rat.setClickable(false);
            rat.setFocusable(false);
        }
        else if (rating.equals(ap))
        {
            String user_rating=apprentice;
            rat.setRating(2);
            rat.setClickable(false);
            rat.setFocusable(false);


        }
        else if(rating.equals(ad))
        {
            String user_rating=adept;
            rat.setRating(3);
            rat.setClickable(false);
            rat.setFocusable(false);

        }
        else if(rating.equals(ex))
        {
            String user_rating=expert;
            rat.setRating(4);
            rat.setClickable(false);
            rat.setFocusable(false);

        }
        else
        {
            String user_rating=master;
            rat.setRating(5);
            rat.setClickable(false);
            rat.setFocusable(false);
        }



    }





    @Override
    public void onBackPressed(){
        startActivity(new Intent(Profile.this, Home.class));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Bitmap bitmap=null;


            try
            {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                sizeInBytes=bitmap.getRowBytes();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                Toast.makeText(Profile.this,"Exception!",Toast.LENGTH_SHORT).show();
                sizeInBytes=800000;
            }


                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            //String picturePath = cursor.getString(columnIndex);
            cursor.close();


            System.out.println(sizeInBytes);
            if(sizeInBytes<=MAX_BYTES) {

                try{
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                image = stream.toByteArray();
                allowed=true;
                Toast.makeText(Profile.this, "File Selected. Please Press Submit to Upload the DP " + sizeInBytes, Toast.LENGTH_SHORT).show();

                        }
                catch (Exception e1) {
                    allowed=false;
                    Toast.makeText(Profile.this, "File size should be less than 1mb ("+sizeInBytes+")", Toast.LENGTH_SHORT).show();
                }
                }

            else
            {
                allowed=false;
                Toast.makeText(Profile.this, "File size should be less than 1mb ("+sizeInBytes+")", Toast.LENGTH_SHORT).show();
            }

        }
    }
    public void SeeSessions(View v)
    {
        ParseQuery<ParseObject> installationQuery = ParseQuery.getQuery("_Installation");
        installationQuery.whereEqualTo("appName", "CampusConnect");
        installationQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    i_len = list.size();
                    Toast.makeText(Profile.this, "Total Installations: " + i_len , Toast.LENGTH_SHORT).show();
                }
            }
        });

//////      timepass code! just checking how many active sessions that user has\\\\\\
        ParseQuery<ParseObject> sessionParseQuery = ParseQuery.getQuery("_Session");
        sessionParseQuery.include("user");
        sessionParseQuery.whereEqualTo("user", ParseUser.getCurrentUser());
        sessionParseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                s_len = list.size();
                Toast.makeText(Profile.this, "Total Sessions: " + s_len, Toast.LENGTH_SHORT).show();
            }

        });


    }



}


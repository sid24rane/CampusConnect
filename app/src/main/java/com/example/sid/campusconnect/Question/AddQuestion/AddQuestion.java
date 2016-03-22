package com.example.sid.campusconnect.Question.AddQuestion;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.Home.Home;
import com.example.sid.campusconnect.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddQuestion extends AppCompatActivity {

    //declaration
    private static int RESULT_LOAD_IMAGE = 1;
    protected Spinner Categories;
    protected EditText title;
    protected EditText description;
    protected Button addquestion;
    protected Button addimagedata;
    protected TextView datatextresult;
    final int initial_upvote = 0;
    final int intial_downvote = 0;
    ParseFile file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);


        // mapping ui to java
        Categories = (Spinner) findViewById(R.id.category);
        title = (EditText) findViewById(R.id.Titletext);
        description = (EditText) findViewById(R.id.Descriptiontext);
        addquestion = (Button) findViewById(R.id.Add);
        addimagedata = (Button) findViewById(R.id.DataImage);
        datatextresult=(TextView)findViewById(R.id.dataresult);

        // add image button click listener
        // currently only image

        addimagedata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        // adding question button click
        // algoritm:
        // 1. start process dialog box
        // 2. get current user and his type (coz if admin ==> then qs wud be displayed in red color in listview otherwise green)
        // 3. get all the values and call save method
        // 4. two conditions ==> file==null (no file is selected) n 2nd one ==> file selected
        // 5. onsuccess --> toast --> delay of 2s --> ask --> want to add more qs --> if yes --> clear all fields --> else -->redirect

        addquestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String t = title.getText().toString();
                final String d = description.getText().toString();
                final String c = String.valueOf(Categories.getSelectedItem());

                if ((t.length() == 0) || (d.length() == 0) || (c.length() == 0)) {
                    Toast errortoast = Toast.makeText(getApplicationContext(), "Please Fill out all the Required fields !", Toast.LENGTH_LONG);
                    errortoast.show();
                }
                else
                {
                    // loading
                    final ProgressDialog sendqs = new ProgressDialog(AddQuestion.this);
                    sendqs.setTitle("Please wait.");
                    sendqs.setMessage("Adding Question..!");
                    sendqs.show();

                    ParseUser current_user = ParseUser.getCurrentUser();

                    final boolean usertype;

                    if (current_user.getBoolean("Is_Admin") == true) {
                        usertype = true;
                    } else {
                        usertype = false;
                    }

                    if (current_user != null) {
                        //getting values
                        ParseObject Question = new ParseObject("Question");
                        Question.put("User_id", current_user);
                        Question.put("Title", t);
                        Question.put("Description", d);
                        Question.put("Upvote_Count", initial_upvote);
                        Question.put("Downvote_Count", intial_downvote);
                        Question.put("Category", c);
                        Question.put("By_Admin", usertype);
                        Question.put("Is_Requested",false);
                        if (file == null) {
                            Question.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        sendqs.dismiss();
                                        Toast toast = Toast.makeText(getApplicationContext(), "Question Added Successfully !", Toast.LENGTH_LONG);
                                        toast.show();

                                        //runnable add
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                AlertDialog.Builder discarduser = new AlertDialog.Builder(AddQuestion.this);
                                                discarduser.setMessage("Do you want to Add more Questions ?")
                                                        .setCancelable(false)
                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                title.setText("");
                                                                description.setText("");
                                                                Categories.setSelection(0);
                                                                file = null;
                                                            }
                                                        })
                                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                dialog.cancel();
                                                                Intent intent = new Intent(AddQuestion.this, Home.class);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                startActivity(intent);
                                                            }
                                                        });

                                                //Creating dialog box
                                                AlertDialog alertbox = discarduser.create();
                                                //Setting the title manually
                                                alertbox.setTitle("Add More Questions");
                                                alertbox.show();
                                            }
                                        }, 1500);

                                    } else {
                                        Log.d("Error is : ", e.getMessage());
                                    }
                                }
                            });
                        } else {
                            Question.put("Data", file);
                            Question.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        sendqs.dismiss();
                                        Toast toast = Toast.makeText(getApplicationContext(), "Question Added Successfully !", Toast.LENGTH_LONG);
                                        toast.show();

                                        final Handler handlers = new Handler();
                                        handlers.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                AlertDialog.Builder discarduser = new AlertDialog.Builder(AddQuestion.this);
                                                discarduser.setMessage("Do you want to Add more Questions ?")
                                                        .setCancelable(false)
                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                title.setText("");
                                                                description.setText("");
                                                                Categories.setSelection(0);
                                                                file = null;
                                                                datatextresult.setText("No Data Uploaded");
                                                            }
                                                        })
                                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                dialog.cancel();
                                                                Intent intent = new Intent(AddQuestion.this, Home.class);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                startActivity(intent);
                                                            }
                                                        });

                                                //Creating dialog box
                                                AlertDialog alertbox = discarduser.create();
                                                //Setting the title manually
                                                alertbox.setTitle("Add More Questions");
                                                alertbox.show();
                                            }
                                        }, 5000);

                                    } else {
                                        Log.d("Error is : ", e.getMessage());
                                    }
                                }
                            });
                        }
                    } else {
                        Intent intent = new Intent(AddQuestion.this,Home.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }


            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] image = stream.toByteArray();
            file = new ParseFile("questiondata.png", image);
            datatextresult.setText("Image Uploaded");
        }
    }

}

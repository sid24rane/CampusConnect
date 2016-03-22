package com.example.sid.campusconnect.Question.EditQuestion;

import android.app.ProgressDialog;
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
import com.example.sid.campusconnect.Question.QuestionDetail;
import com.example.sid.campusconnect.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditQuestion extends AppCompatActivity {

    //declaration
    private static int RESULT_LOAD_IMAGE = 1;
    protected Spinner Categories;
    protected EditText title;
    protected EditText description;
    protected Button addquestion;
    protected Button addimagedata;
    protected TextView datatextresult;
    ParseFile file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_qs);

        // mapping ui to java
        Categories = (Spinner) findViewById(R.id.category);
        title = (EditText) findViewById(R.id.Titletext);
        description = (EditText) findViewById(R.id.Descriptiontext);
        addquestion = (Button) findViewById(R.id.Add);
        addimagedata = (Button) findViewById(R.id.DataImage);
        datatextresult=(TextView)findViewById(R.id.dataresult);

        //Receiving question_id through intent
        Intent intent = getIntent();
        final String question_id = intent.getStringExtra("question_id");

        //  progress dialog
        final ProgressDialog qsloader = new ProgressDialog(EditQuestion.this);
        qsloader.setTitle("Please wait.");
        qsloader.setMessage("Loading Question ..");
        qsloader.show();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Question");
        query.getInBackground(question_id, new GetCallback<ParseObject>() {
            public void done(ParseObject question, ParseException e) {
                if (e == null) {
                    //getting title
                    String qstitle = question.getString("Title");
                    //description
                    String qsdes = question.getString("Description");
                    //category
                    final String cate = question.getString("Category");
                    //data file
                    ParseFile imageFile = (ParseFile) question.get("Data");
                    if (imageFile == null) {
                        datatextresult.setText("No Data Uploaded");
                    } else {
                        datatextresult.setText("Image Uploaded");
                    }

                    // setting values
                    title.setText(qstitle);
                    description.setText(qsdes);
                    Categories.setSelection(getIndex(Categories, cate));
                    qsloader.dismiss();
                } else {
                    Log.d("Error is :",e.getMessage());
                }
            }
        });

        // add image button click listener
        // currently only image

        addimagedata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });


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
                    final ProgressDialog sendqs = new ProgressDialog(EditQuestion.this);
                    sendqs.setTitle("Please wait.");
                    sendqs.setMessage("Updating Question..!");
                    sendqs.show();

                    ParseUser current_user = ParseUser.getCurrentUser();

                    final boolean usertype;

                    if (current_user.getBoolean("Is_Admin") == true) {
                        usertype = true;
                    } else {
                        usertype = false;
                    }

                    if (current_user != null)
                    {
                        //getting values
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Question");
                        query.getInBackground(question_id, new GetCallback<ParseObject>() {
                            public void done(ParseObject question, ParseException e) {
                                if (e == null) {
                                    question.put("Title", t);
                                    question.put("Description", d);
                                    question.put("Category", c);
                                    if (file == null) {
                                        question.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    sendqs.dismiss();
                                                    Toast toast = Toast.makeText(getApplicationContext(), "Question Updated Successfully !", Toast.LENGTH_LONG);
                                                    toast.show();

                                                    //handler
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Intent intent = new Intent(EditQuestion.this,QuestionDetail.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            intent.putExtra("question_id",question_id);
                                                            startActivity(intent);
                                                        }
                                                    }, 3000);
                                                }
                                                else
                                                {
                                                    Log.d("Error is : ", e.getMessage());
                                                }
                                            }
                                        });
                                    } else {
                                        question.put("Data", file);
                                        question.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    sendqs.dismiss();
                                                    Toast toast = Toast.makeText(getApplicationContext(), "Question Edited Successfully !", Toast.LENGTH_LONG);
                                                    toast.show();

                                                    //handler
                                                    //handler
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Intent intent = new Intent(EditQuestion.this,QuestionDetail.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            intent.putExtra("question_id",question_id);
                                                            startActivity(intent);
                                                        }
                                                    }, 3000);

                                                } else {
                                                    Log.d("Error is : ", e.getMessage());
                                                }
                                            }
                                        });
                                    }

                                }
                            }
                        });

                    }
                    else
                    {
                        Intent intent = new Intent(EditQuestion.this, Home.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
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
            file = new ParseFile("editdata.png", image);
            datatextresult.setText("Image Uploaded");
        }
    }

}

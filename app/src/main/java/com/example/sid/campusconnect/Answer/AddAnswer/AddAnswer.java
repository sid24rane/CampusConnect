package com.example.sid.campusconnect.Answer.AddAnswer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.Question.ViewQuestion.ViewQuestion;
import com.example.sid.campusconnect.Question.QuestionDetail;
import com.example.sid.campusconnect.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddAnswer extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE = 1;
    ParseFile file;
    EditText ans;
    protected Button addans;
    protected Button addimagedata;
    String q_title, q_des;
    TextView ques,des,datatextresult,uname;
    ParseObject o_qs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_answer);

        uname = (TextView) findViewById(R.id.u_name);
        ans = (EditText) findViewById(R.id.edt_Ans);
        ques = (TextView) findViewById(R.id.tv_ques);
        des = (TextView) findViewById(R.id.tv_des);
        addans = (Button) findViewById(R.id.add_ans);
        addimagedata = (Button) findViewById(R.id.DataImage);
        datatextresult = (TextView) findViewById(R.id.dataresult);

        Intent intent = getIntent();
        final String ques_id = intent.getStringExtra("ques_id");



        ParseQuery<ParseObject> query = ParseQuery.getQuery("Question");
        query.include("User_id");
        query.getInBackground(ques_id, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    q_title = object.getString("Title");
                    ques.setText(q_title);
                    q_des = object.getString("Description");
                    des.setText(q_des);
                    o_qs = object;
                    ParseObject users = object.getParseUser("User_id");
                    String name = users.getString("Name");
                    uname.setText(name);

                } else {
                    Log.d("Question : Error is : ", e.getMessage());
                }
            }
        });




        addimagedata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        addans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String t=ans.getText().toString();
               if(t!=null)
                {
                    final ProgressDialog sendqs = new ProgressDialog(AddAnswer.this);
                    sendqs.setTitle("Please wait.");
                    sendqs.setMessage("Submitting Answer!!!");
                    sendqs.show();

                    ParseUser current_user = ParseUser.getCurrentUser();

                    final boolean usertype;

                    if (current_user.getBoolean("Is_Admin") == true) {
                        usertype = true;
                    } else {
                        usertype = false;
                    }


                    if(current_user!=null)
                    {
                        final ParseObject Object = new ParseObject("Answer");
                        Object.put("Description",t);
                        Object.put("By_Admin",usertype);
                        Object.put("Qs_Id", o_qs);
                        Object.put("User_Id",ParseUser.getCurrentUser());
                        Object.put("Upvote_Count",0);
                        Object.put("Downvote_Count",0);
                        Object.put("Cmt_Count",0);
                        Object.put("isReported", false);


                        if(file!=null)
                        {
                            Object.put("Data",file);
                            Object.saveInBackground(new SaveCallback(){
                                @Override
                                public void done(ParseException e) {
                                    if(e==null)
                                    {
                                        sendqs.dismiss();
                                        Toast toast = Toast.makeText(getApplicationContext(), "Answer Submitted Successfully !", Toast.LENGTH_LONG);
                                        toast.show();
                                        Intent intent = new Intent(AddAnswer.this, ViewQuestion.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        sendqs.dismiss();
                                        Toast toast = Toast.makeText(AddAnswer.this, "Error saving Obj", Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                }
                            });

                        }
                        else
                        {
                            Object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        sendqs.dismiss();
                                        //updating question object for time(updatedAt)
                                        o_qs.put("Is_Answered", true);
                                        o_qs.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e)
                                            {
                                                Toast toast = Toast.makeText(getApplicationContext(), "Answer Submitted Successfully ! ", Toast.LENGTH_LONG);
                                                toast.show();
                                                Intent intent = new Intent(AddAnswer.this, QuestionDetail.class);
                                                intent.putExtra("question_id",ques_id);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }
                                        });

                                    } else {
                                        Toast toast = Toast.makeText(AddAnswer.this, "Error saving Obj", Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                }
                            });
                        }

                        if(!o_qs.getParseObject("User_id").getObjectId().equals(ParseUser.getCurrentUser().getObjectId()))
                        {
                            final String username=current_user.getString("Name");
                            ParseObject not = new ParseObject("Notification");
                            not.put("Question", o_qs);
                            not.put("Answer", Object);
                            not.put("Type", "Question");
                            not.put("User_Id", o_qs.get("User_id"));
                            not.put("Notifier",ParseUser.getCurrentUser());
                            not.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {

                                        ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
                                        pushQuery.whereEqualTo("user",o_qs.getParseObject("User_id"));



                                        JSONObject jsondata = null;
                                        try {
                                            jsondata = new JSONObject();
                                            jsondata.put("username", username);
                                            jsondata.put("text","answered your question: ");
                                            jsondata.put("qsTitle", o_qs.getString("Title"));
                                            jsondata.put("type","question");
                                            //jsondata.put("title",title);
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
                                        Toast toast = Toast.makeText(AddAnswer.this, e.getMessage(), Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                }
                            });
                        }

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
            file.saveInBackground();
            datatextresult.setText("Image Uploaded");
        }
    }

}

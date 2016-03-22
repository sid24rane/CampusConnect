package com.example.sid.campusconnect.Comment_Reply.AddComment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.Answer.ViewAnswer.ViewAnswer;
import com.example.sid.campusconnect.R;
import com.parse.FindCallback;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddComment extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE = 1;
    ParseFile file;
    EditText com;
    protected Button addcom;
    protected Button addimagedata;
    String answer_text;
    TextView ans,datatextresult,uname,quest;
    ParseObject o_as,o_qs;
    boolean bt,allowed;
    ParseUser tagged;
    AutoCompleteTextView tag;
    Button rem;
    String[] usrs;
    List<ParseObject> ulist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);

        uname = (TextView) findViewById(R.id.a_name);
        com = (EditText) findViewById(R.id.edt_Comment);
        ans = (TextView) findViewById(R.id.tv_ans);
        quest = (TextView) findViewById(R.id.tv_ques);
        addcom = (Button) findViewById(R.id.add_comment);
        addimagedata = (Button) findViewById(R.id.DataImage);
        datatextresult = (TextView) findViewById(R.id.dataresult);
        tag=(AutoCompleteTextView) findViewById(R.id.Tag);
        rem=(Button) findViewById(R.id.btn_rem);

        final ProgressDialog load = new ProgressDialog(AddComment.this);
        load.setTitle("Please wait.");
        load.setMessage("Damn Slow Net!!!");
        load.show();

        Intent intent = getIntent();
        final String ans_id = intent.getStringExtra("ans_id");

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Answer");
        query.include("User_Id");
        query.getInBackground(ans_id, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    answer_text = object.getString("Description");
                    ans.setText(answer_text);
                    o_as = object;
                    ParseObject users = object.getParseUser("User_Id");
                    String name = users.getString("Name");
                    uname.setText(name);
                    o_qs = object.getParseObject("Qs_Id");
                    load.dismiss();


                } else {
                    Log.d("Question : Error is : ", e.getMessage());
                }
            }

        });




        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("_User");
        query1.whereEqualTo("Is_Admin", false);
        query1.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e == null) {
                    if (list.size() == 0) {
                        Toast toast = Toast.makeText(getApplicationContext(), "No Request Found!", Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        ulist = list;
                        int l = list.size();
                        usrs = new String[l];

                        for (int i = 0; i <l; i++) {
                            usrs[i] = ulist.get(i).getString("Name");

                        }

                    }
                } else {
                    Log.d("Users error ", "Error: " + e.getMessage());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (AddComment.this, android.R.layout.select_dialog_item, usrs);
                tag.setAdapter(adapter);
                tag.setThreshold(1);
                tag.setTextColor(Color.BLUE);
            }


        });




        tag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                tag.setEnabled(false);
                tagged= (ParseUser) ulist.get(arg2);
                bt=true;
                rem.setVisibility(View.VISIBLE);
            }
        });


        rem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tag.setText("");
                tag.setEnabled(true);
                tag.setActivated(true);
                bt=false;
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

        addcom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String t = com.getText().toString();
                if (t == null) {

                } else {
                    final ProgressDialog sendqs = new ProgressDialog(AddComment.this);
                    sendqs.setTitle("Please wait.");
                    sendqs.setMessage("Submitting Comment!!!");
                    sendqs.show();
                    allowed=true;

                    ParseUser current_user = ParseUser.getCurrentUser();

                    final boolean usertype;

                    if (current_user.getBoolean("Is_Admin") == true) {
                        usertype = true;
                    } else {
                        usertype = false;
                    }

                    if(bt==true) {
                        if(tagged.getObjectId().equals(ParseUser.getCurrentUser().getObjectId()))
                        {
                            ///self reply check
                            Toast.makeText(AddComment.this, tagged.getObjectId(), Toast.LENGTH_SHORT).show();
                            allowed=false;
                        }
                    }

                    if(allowed == true) {

                        if (current_user != null) {
                            ParseObject Object = new ParseObject("Comment");
                            Object.put("Body", t);
                            Object.put("By_Admin", usertype);
                            Object.put("Ans_Id", o_as);
                            Object.put("User_Id", ParseUser.getCurrentUser());
                            Object.put("Upvote_Count", 0);
                            Object.put("Downvote_Count", 0);
                            Object.put("isReported", false);

                            if (bt == true) {
                                Object.put("Tagged_User", tagged);
                            }


                            o_as.increment("Cmt_Count");

                            if (file != null) {
                                Object.put("Data", file);
                                Object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            final String q_id = o_qs.getObjectId();
                                            sendqs.dismiss();
                                            Toast toast = Toast.makeText(getApplicationContext(), "Comment Submitted Successfully !", Toast.LENGTH_LONG);
                                            toast.show();
                                            Intent intent = new Intent(AddComment.this, ViewAnswer.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra("ques_id", q_id);
                                            startActivity(intent);
                                        } else {
                                            sendqs.dismiss();
                                            Toast toast = Toast.makeText(AddComment.this, "Error saving Obj", Toast.LENGTH_LONG);
                                            toast.show();
                                        }
                                    }
                                });

                            } else {
                                Object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            final String q_id = o_qs.getObjectId();
                                            sendqs.dismiss();
                                            Toast toast = Toast.makeText(getApplicationContext(), "Comment Submitted Successfully !", Toast.LENGTH_LONG);
                                            toast.show();
                                            Intent intent = new Intent(AddComment.this, ViewAnswer.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra("ques_id", q_id);
                                            startActivity(intent);
                                        } else {
                                            Toast toast = Toast.makeText(AddComment.this, "Error saving Obj", Toast.LENGTH_LONG);
                                            toast.show();
                                        }
                                    }
                                });
                            }

                            if (!o_as.getParseObject("User_Id").getObjectId().equals(ParseUser.getCurrentUser().getObjectId()))
                            {
                                ParseObject not = new ParseObject("Notification");
                                not.put("Answer", o_as);
                                not.put("Comment", Object);
                                not.put("Type", "Answer");
                                not.put("User_Id", o_as.get("User_Id"));
                                not.put("Notifier", ParseUser.getCurrentUser());
                                not.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
                                            pushQuery.whereEqualTo("user",o_as.getParseObject("User_Id"));



                                            JSONObject jsondata = null;
                                            try {
                                                jsondata = new JSONObject();
                                                jsondata.put("username", ParseUser.getCurrentUser().getString("Name"));
                                                jsondata.put("text","commented on your answer");
                                                jsondata.put("ans", o_as.getString("Description"));
                                                jsondata.put("type","answer");
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
                                            Toast toast = Toast.makeText(AddComment.this, e.getMessage(), Toast.LENGTH_LONG);
                                            toast.show();
                                        }
                                    }
                                });
                            }
                            if (bt == true) {
                                if (!tagged.getObjectId().equals(o_as.getParseObject("User_Id").getObjectId())) {

                                    ParseObject not1 = new ParseObject("Notification");
                                    not1.put("Reply", Object);
                                    not1.put("Comment", Object);
                                    not1.put("Type", "Comment");
                                    not1.put("User_Id", tagged);
                                    not1.put("Notifier", ParseUser.getCurrentUser());
                                    not1.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
                                                pushQuery.whereEqualTo("user",tagged);



                                                JSONObject jsondata = null;
                                                try {
                                                    jsondata = new JSONObject();
                                                    jsondata.put("username", ParseUser.getCurrentUser().getString("Name"));
                                                    jsondata.put("text","tagged you in a comment");
                                                    jsondata.put("comment",t);
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
                                                Toast toast = Toast.makeText(AddComment.this, e.getMessage(), Toast.LENGTH_LONG);
                                                toast.show();
                                            }
                                        }
                                    });
                                }
                            }

                        }
                    }
                    else{
                        Toast toast = Toast.makeText(AddComment.this, "Cannot reply to yourself!", Toast.LENGTH_LONG);
                        toast.show();
                        tag.setText("");
                        tag.setEnabled(true);
                        tag.setActivated(true);
                        bt=false;
                        sendqs.dismiss();
                    }

                }
            }

        });
    }

    @Override
    protected void onActivityResult ( int requestCode,
                                      int resultCode, Intent data){
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
            file = new ParseFile("commentdata.png", image);
            file.saveInBackground();
            datatextresult.setText("Image Uploaded");
        }
    }
    static String[] convert(List<String[]> from) {
        ArrayList<String> list1 = new ArrayList<String>();
        for (String[] strings : from) {
            Collections.addAll(list1, strings);
        }
        return list1.toArray(new String[list1.size()]);
    }

}


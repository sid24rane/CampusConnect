package com.example.sid.campusconnect.Answer.EditAnswer;

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

import com.example.sid.campusconnect.Question.QuestionDetail;
import com.example.sid.campusconnect.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditAnswer extends AppCompatActivity {

    TextView ques,des,datatextresult,uname;
    ParseFile file;
    EditText ans;
    protected Button addans;
    protected Button addimagedata, remove_data;
    String q_title, q_des;
    private static int RESULT_LOAD_IMAGE = 1;
    ParseObject answer_obj,ques_obj;
    boolean isdatachanged,ifdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_answer);
        uname = (TextView) findViewById(R.id.u_name);
        ans = (EditText) findViewById(R.id.edt_Ans);
        ques = (TextView) findViewById(R.id.tv_ques);
        des = (TextView) findViewById(R.id.tv_des);
        addans = (Button) findViewById(R.id.add_ans);
        remove_data = (Button) findViewById(R.id.remove_data);
        addimagedata = (Button) findViewById(R.id.DataImage);
        datatextresult = (TextView) findViewById(R.id.dataresult);
        isdatachanged=false;
        ifdata=false;


        //Receiving question_id through intent
        Intent intent = getIntent();
        final String ans_id = intent.getStringExtra("ans_id");
        final String ques_id = intent.getStringExtra("ques_id");

        //  progress dialog
        final ProgressDialog qsloader = new ProgressDialog(EditAnswer.this);
        qsloader.setTitle("Please wait.");
        qsloader.setMessage("Loading Answer ..");
        qsloader.show();

        ParseQuery<ParseObject> ans_query = ParseQuery.getQuery("Answer");
        ans_query.getInBackground(ans_id, new GetCallback<ParseObject>() {
            public void done(ParseObject ans_obj, ParseException e) {
                if (e == null) {

                    answer_obj = ans_obj;
                    //answer
                    String answer = ans_obj.getString("Description");
                    ans.setText(answer);
                    //data file
                    ParseFile imageFile = (ParseFile) ans_obj.get("Data");
                    if (imageFile == null) {
                        datatextresult.setText("No Data Uploaded");
                    } else {
                        ifdata = true;
                        datatextresult.setText("Image Uploaded");
                        remove_data.setVisibility(View.VISIBLE);
                    }

                    qsloader.dismiss();
                } else {
                    Log.d("Error is :", e.getMessage());
                }
            }
        });

        ParseQuery<ParseObject> qsquery = ParseQuery.getQuery("Question");
        qsquery.include("User_id");
        qsquery.getInBackground(ques_id, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    ques_obj=object;
                    q_title = object.getString("Title");
                    ques.setText(q_title);
                    q_des = object.getString("Description");
                    des.setText(q_des);
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

                final ProgressDialog qsloader = new ProgressDialog(EditAnswer.this);
                qsloader.setTitle("Please wait.");
                qsloader.setMessage("Updating Answer ..");
                qsloader.show();

                final String t = ans.getText().toString();
                if (t != null) {
                    answer_obj.put("Description",t);
                }
                if(isdatachanged){
                if(ifdata)
                {
                        answer_obj.put("Data",file);
                }
                    else
                {
                        answer_obj.put("Data","");
                }
                }
                answer_obj.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        ques_obj.put("Title", q_title);
                        ques_obj.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                qsloader.dismiss();
                                Intent intent = new Intent(EditAnswer.this, QuestionDetail.class);
                                //NOTE: THE MOST IMP STEP ==> PASSING QuestionID THROUGH INTENT TO NEXT ACTIVITY
                                intent.putExtra("question_id", ques_id);
                                startActivity(intent);
                            }
                        });

                    }
                });
            }

        });

        remove_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ifdata=false;
                datatextresult.setText("No Image Uploaded");
                isdatachanged=true;
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
            isdatachanged=true;
            ifdata=true;
            datatextresult.setText("Image Uploaded");
            remove_data.setVisibility(View.VISIBLE);
        }
    }

}

package com.example.sid.campusconnect.Answer.WriteAnswer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.Answer.AddAnswer.AddAnswer;
import com.example.sid.campusconnect.Home.Home;
import com.example.sid.campusconnect.MainActivity;
import com.example.sid.campusconnect.Question.QuestionDetail;
import com.example.sid.campusconnect.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class WriteAnswer extends AppCompatActivity {

    protected ListView lw;
    protected List<ParseObject> mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_answer);

        lw=(ListView)findViewById(R.id.writeanslist);


        // loading
        final ProgressDialog QSloader = new ProgressDialog(WriteAnswer.this);
        QSloader.setTitle("Please wait.");
        QSloader.setMessage("Loading Questions..");
        QSloader.show();

        ParseUser current_user = ParseUser.getCurrentUser();

        if (current_user != null)
        {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Question");
            query.whereNotEqualTo("Is_Reported",true);
            query.whereNotEqualTo("Is_Answered",true);
            query.findInBackground(new FindCallback<ParseObject>()
            {
                public void done(List<ParseObject> questionList, ParseException e) {
                    if (e == null)
                    {
                        int length = questionList.size();
                        if(length==0)
                        {
                            Toast toast = Toast.makeText(getApplicationContext(),"No Questions to Answer!",Toast.LENGTH_LONG);
                            toast.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(WriteAnswer.this, Home.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }, 5000);
                        }
                        else
                        {
                            QSloader.dismiss();
                            mStatus = questionList;
                            WriteAnswerListAdapter adapter = new WriteAnswerListAdapter(lw.getContext(), mStatus);
                            lw.setAdapter(adapter);
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
            Intent intent = new Intent(WriteAnswer.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public void readqsHandler(View v) {
        View parentView = (View) v.getParent();
        View gparent =(View)parentView.getParent();
        TextView sd = (TextView)gparent.findViewById(R.id.QsIds);
        final String question_id = sd.getText().toString();

        Intent intent = new Intent(WriteAnswer.this,QuestionDetail.class);
        //NOTE: THE MOST IMP STEP ==> PASSING QuestionID THROUGH INTENT TO NEXT ACTIVITY
        intent.putExtra("question_id", question_id);
        startActivity(intent);
    }

    public void writeansHandler(View v) {

        View parentView = (View) v.getParent();
        View gparent =(View)parentView.getParent();
        TextView sd = (TextView)gparent.findViewById(R.id.QsIds);
        final String question_id = sd.getText().toString();

        Intent intent = new Intent(WriteAnswer.this,AddAnswer.class);
        //NOTE: THE MOST IMP STEP ==> PASSING QuestionID THROUGH INTENT TO NEXT ACTIVITY
        intent.putExtra("ques_id", question_id);
        startActivity(intent);
    }
}

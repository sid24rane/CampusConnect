package com.example.sid.campusconnect.Notification;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.Comment_Reply.ViewComment.CommentView;
import com.example.sid.campusconnect.R;
import com.example.sid.campusconnect.Answer.ViewAnswer.ViewAnswer;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class Notifications extends AppCompatActivity {
    protected List<ParseObject> mStatus;
    final Context context = this;
    ParseObject robj;
    TextView tot;
    ParseObject obj;
    protected String img_url;
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        lv= (ListView) findViewById(R.id.not_list);
        tot=(TextView) findViewById(R.id.tot);
        final ProgressDialog QSloader = new ProgressDialog(Notifications.this);
        QSloader.setTitle("Please wait.");
        QSloader.setMessage("Loading Notifications...");
        QSloader.show();

        ParseUser cur = ParseUser.getCurrentUser();

        if(cur!=null){
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Notification");
            query.include("User_Id");
            query.include("Question");
            query.include("Answer");
            query.include("Notifier");
            query.include("Comment");
            query.whereEqualTo("User_Id", cur);
            query.orderByDescending("createdAt");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        int len = list.size();
                        if (len == 0) {
                            tot.setVisibility(View.VISIBLE);
                            lv.setVisibility(View.GONE);
                            QSloader.dismiss();

                        } else {
                            QSloader.dismiss();
                            mStatus = list;
                            NotificationListAdapter adapter = new NotificationListAdapter(lv.getContext(), mStatus);
                            lv.setAdapter(adapter);

                            QSloader.dismiss();
                        }
                        getSupportActionBar().setTitle("Notifications (" + len + ")");

                    }
                    else
                    {
                        Log.d("Nots ", "Error: " + e.getMessage());
                        QSloader.dismiss();
                    }
                }
            });
        }


    }

    public void go(View v) {

        TextView id,tp,data,name,title,cmt_id;
        String oid,type,not,cid;
        ImageView imv;
        Button btn;
        id = (TextView) v.findViewById(R.id.n_id);
        tp = (TextView) v.findViewById(R.id.n_type);
        cmt_id=(TextView) v.findViewById(R.id.n_frreply);



        oid=id.getText().toString();
        type=tp.getText().toString();
        cid=cmt_id.getText().toString();

        final Dialog dialog = new Dialog(context);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_notificaion);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        data = (TextView) dialog.findViewById(R.id.tv_data);
        imv = (ImageView) dialog.findViewById(R.id.nimgdata);
        title = (TextView) dialog.findViewById(R.id.title);
        name = (TextView) findViewById(R.id.n_name);
        btn = (Button) dialog.findViewById(R.id.full);

        not=name.getText().toString();


        if(type.equals("Question")){
            dialog.show();

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Answer");
            query.include("Qs_Id");
            try {
                robj = query.get(oid);
                obj = robj.getParseObject("Qs_Id");
            } catch (ParseException e) {
                Toast.makeText(Notifications.this, "Query Error", Toast.LENGTH_LONG).show();
            }
            title.setText("" + not + "'s answer to your question");
            data.setText(robj.getString("Description"));




            ParseFile file;
            file= (ParseFile) robj.get("Data");
            if(file!=null) {
                try {
                    img_url = file.getUrl();
                    byte[] bitmapdata = file.getData();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                    imv.setImageBitmap(bitmap);
                    imv.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    Toast.makeText(Notifications.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

                imv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent bi = new Intent(Intent.ACTION_VIEW, Uri.parse(img_url));
                            startActivity(bi);
                        } catch (ActivityNotFoundException ae) {
                            Toast.makeText(Notifications.this, "No application can handle this request."
                                    + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            btn.setText("Read All Answers");

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Notifications.this, ViewAnswer.class);
                    intent.putExtra("ques_id", obj.getObjectId());
                    dialog.dismiss();
                    startActivity(intent);


                }
            });

        }
        else if(type.equals("Answer")){

            dialog.show();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Comment");
            query.include("Ans_Id");
            try {
                robj = query.get(oid);
                obj = robj.getParseObject("Ans_Id");
            } catch (ParseException e) {
                Toast.makeText(Notifications.this, "Query Error", Toast.LENGTH_LONG).show();
            }
            title.setText("" + not + "'s comment on your Answer");
            data.setText(robj.getString("Body"));

            ParseFile file;
            file= (ParseFile) robj.get("Data");
            if(file!=null) {
                try {
                    img_url = file.getUrl();
                    byte[] bitmapdata = file.getData();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                    imv.setImageBitmap(bitmap);
                    imv.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    Toast.makeText(Notifications.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

                imv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent bi = new Intent(Intent.ACTION_VIEW, Uri.parse(img_url));
                            startActivity(bi);
                        } catch (ActivityNotFoundException ae) {
                            Toast.makeText(Notifications.this, "No application can handle this request."
                                    + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            btn.setText("Read All Comments");

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Notifications.this, CommentView.class);
                    intent.putExtra("ans_id", obj.getObjectId());
                    dialog.dismiss();
                    startActivity(intent);


                }
            });




        }

        else{

            dialog.show();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Comment");
            try {
                robj = query.get(oid);
            } catch (ParseException e) {
                Toast.makeText(Notifications.this, "Query Error", Toast.LENGTH_LONG).show();
            }

            ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Comment");
            try {
                obj = query1.get(cid);
            } catch (ParseException e) {
                Toast.makeText(Notifications.this, "Query Error", Toast.LENGTH_LONG).show();
            }
            title.setText("" + not + "'s comment");
            data.setText(robj.getString("Body"));
            btn.setText("Show More");

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Notifications.this, CommentView.class);
                    intent.putExtra("ans_id", obj.getParseObject("Ans_Id").getObjectId());
                    dialog.dismiss();
                    startActivity(intent);

                }
            });

        }


//        usr=obj.getParseObject("User_Id");


    }


}

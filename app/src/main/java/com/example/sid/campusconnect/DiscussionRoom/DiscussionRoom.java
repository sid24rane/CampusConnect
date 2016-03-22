package com.example.sid.campusconnect.DiscussionRoom;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.List;

public class DiscussionRoom extends ListActivity  {

    private static int RESULT_LOAD_IMAGE = 1;
    ParseFile file;
    private Handler handler = new Handler();
    protected EditText message;
    protected Button send,allmsg;
    ParseObject dis;
    String disid,title;
    protected List<ParseObject> mStatus;
    protected List<ParseObject> memberstonotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_room);

        message=(EditText)findViewById(R.id.Message);
        send=(Button)findViewById(R.id.Send);


        //Receiving discussion_id through intent
        Intent intent = getIntent();
        final String discussion_id = intent.getStringExtra("discussion_id");
        disid=discussion_id;


        // getting discussion object
        ParseQuery<ParseObject> querys = ParseQuery.getQuery("Discuss_Room");
        querys.getInBackground(discussion_id, new GetCallback<ParseObject>() {
            public void done(ParseObject discussion_room, ParseException e) {
                if (e == null) {
                    dis = discussion_room;
                    title = dis.getString("Subject");
                    getActionBar().setTitle(title);
                } else {
                    Log.d("Error is : ", e.getMessage());
                }
            }
        });

        final int abTitleId = getResources().getIdentifier("action_bar_title", "id", "android");
        findViewById(abTitleId).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DiscussionRoom.this, com.example.sid.campusconnect.DiscussionRoom.DiscussionDetail.class);
                intent1.putExtra("discussion_id",discussion_id);
                startActivity(intent1);
            }
        });


        ReceiveMessage();

        // Run the runnable object defined every 1s
        handler.postDelayed(runnable, 1000);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    // Defines a runnable which is run every 100ms
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            refreshMessages();
            handler.postDelayed(this, 1000);
        }
    };

    private void refreshMessages() {
        Refresh();
    }

    public void sendMessage()
    {
        final String body = message.getText().toString();
        if(body.length()==0)
        {
            Toast t = Toast.makeText(getApplicationContext(),"Empty Message",Toast.LENGTH_LONG);
            t.show();
        }
        else
        {
            final ParseUser current_user = ParseUser.getCurrentUser();
            ParseObject discussion = dis;
            final String username=current_user.getString("Name");

            ParseObject dismessage = new ParseObject("Dis_Msg");
            dismessage.put("Dis_Id", discussion);
            dismessage.put("User_Id", current_user);
            dismessage.put("Message", body);
            dismessage.saveInBackground(new SaveCallback()
            {
                @Override
                public void done(ParseException e)
                {
                    if (e == null)
                    {
                        message.setImeOptions(EditorInfo.IME_ACTION_DONE);
                        message.setText("");

                        ParseQuery<ParseObject> dismem = ParseQuery.getQuery("Dis_Member");
                        dismem.whereEqualTo("Dis_Id", dis);
                        dismem.whereNotEqualTo("User_Id",ParseUser.getCurrentUser());
                        //dismem.whereNotEqualTo("User_Id", ParseUser.getCurrentUser());
                        dismem.findInBackground(new FindCallback<ParseObject>()
                        {
                            public void done(List<ParseObject> memberList, ParseException e)
                            {
                                if (e == null)
                                {
                                    for (ParseObject mem : memberList)
                                    {
                                        ParseUser user = mem.getParseUser("User_Id");

                                        ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
                                        pushQuery.whereEqualTo("user",user);



                                        JSONObject jsondata = null;
                                        try {
                                            //jsondata = new JSONObject(data);
                                            jsondata = new JSONObject();
                                            jsondata.put("username", username);
                                            jsondata.put("disid", disid);
                                            jsondata.put("msg", body);
                                            jsondata.put("title",title);

                                            jsondata.put("type","chat");
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
                                                   //nothing
                                                } else {
                                                    Toast toast = Toast.makeText(getApplicationContext(), "Not Sent!", Toast.LENGTH_LONG);
                                                    toast.show();
                                                }
                                            }
                                        });
                                    }
                                    Refresh();
                                }
                                else
                                {
                                    Log.d("Error is :", e.getMessage());
                                }
                            }
                        });
                    }
                    else {
                        Log.d("Error is :", e.getMessage());
                    }
                }
            });
        }
    }


    public void ReceiveMessage()
    {

        // getting discussion object
        ParseQuery<ParseObject> querys = ParseQuery.getQuery("Discuss_Room");
        querys.getInBackground(disid, new GetCallback<ParseObject>() {
            public void done(ParseObject discussion_room, ParseException e) {
                if (e == null) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Dis_Msg");
                    query.whereEqualTo("Dis_Id", discussion_room);
                    query.orderByDescending("createdAt");
                    query.include("User_Id");
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> messageList, ParseException e) {
                            if (e == null) {
                                int length = messageList.size();
                                if (length == 0) {
                                    // do nothing..sit there tight
                                } else {
                                    mStatus = messageList;
                                    Collections.reverse(mStatus);
                                    DiscussionRoomListAdapter adapter = new DiscussionRoomListAdapter(getListView().getContext(), mStatus);
                                    setListAdapter(adapter);
                                }
                            } else {
                                Log.d("Discussion ", "Error: " + e.getMessage());
                            }
                        }
                    });
                } else {
                    Log.d("Error is : ", e.getMessage());
                }
            }
        });

    }


    public void Refresh()
    {
        // getting discussion object
        ParseQuery<ParseObject> querys = ParseQuery.getQuery("Discuss_Room");
        querys.getInBackground(disid, new GetCallback<ParseObject>() {
            public void done(ParseObject discussion_room, ParseException e) {
                if (e == null) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Dis_Msg");
                    query.whereEqualTo("Dis_Id", discussion_room);
                    query.orderByDescending("createdAt");
                    query.setLimit(10);
                    query.include("User_Id");
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> messageList, ParseException e) {
                            if (e == null) {

                                int length = messageList.size();
                                if (length == 0) {
                                    // do nothing..just sit there tight
                                } else {
                                    mStatus = messageList;
                                    Collections.reverse(mStatus);
                                    DiscussionRoomListAdapter adapter = new DiscussionRoomListAdapter(getListView().getContext(), mStatus);
                                    setListAdapter(adapter);
                                }
                            } else {
                                Log.d("Discussion ", "Error: " + e.getMessage());
                            }
                        }
                    });
                } else {
                    Log.d("Error is : ", e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_discussion_room, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_gallery:
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
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
            file = new ParseFile("msgdata.png", image);
            if(file==null){
                // do nothing
            }
            else {
                // loading
                final ProgressDialog sendqs = new ProgressDialog(DiscussionRoom.this);
                sendqs.setTitle("Please wait.");
                sendqs.setMessage("Sending Image..!");
                sendqs.show();

                ParseUser current_user = ParseUser.getCurrentUser();
                ParseObject discussion = dis;
                ParseObject dismessage = new ParseObject("Dis_Msg");
                dismessage.put("Dis_Id", discussion);
                dismessage.put("User_Id", current_user);
                dismessage.put("Msg_Data",file);
                dismessage.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            sendqs.dismiss();
                            message.setImeOptions(EditorInfo.IME_ACTION_DONE);
                            message.setText("");
                            Refresh();
                        } else {
                            Log.d("Error is :", e.getMessage());
                        }
                    }
                });
            }
        }


    }


}
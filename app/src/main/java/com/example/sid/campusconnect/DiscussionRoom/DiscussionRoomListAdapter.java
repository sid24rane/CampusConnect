package com.example.sid.campusconnect.DiscussionRoom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Sid on 07-Dec-15.
 */

public class DiscussionRoomListAdapter extends ArrayAdapter<ParseObject>
{
    protected Context mContext;
    protected List<ParseObject> mStatus;
    String img_url;
    ImageView imv;

    public DiscussionRoomListAdapter(Context context, List<ParseObject> status) {
        super(context, R.layout.chat_message, status);
        mContext = context;
        mStatus = status;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.chat_message, null);

            holder = new ViewHolder();

            // username
            holder.otherusername = (TextView) convertView
                    .findViewById(R.id.otherusername);

            holder.myusername = (TextView) convertView
                    .findViewById(R.id.myusername);

            //messages

            holder.othermessage = (TextView) convertView
                    .findViewById(R.id.othermessage);

            holder.mymessage = (TextView) convertView
                    .findViewById(R.id.mymessage);


            //file links
            holder.otherdatalink=(TextView) convertView
                    .findViewById(R.id.otherlink);

            holder.mydatalink=(TextView) convertView
                    .findViewById(R.id.mylink);

            //time

            holder.othertime=(TextView)convertView
                    .findViewById(R.id.othertime);

            holder.mytime=(TextView)convertView
                    .findViewById(R.id.mytime);

            // dp
            holder.otherdp = (ImageView)convertView
                    .findViewById(R.id.otherdp);

            holder.mydp = (ImageView)convertView
                    .findViewById(R.id.mydp);

            convertView.setTag(holder);
        }
        else
        {

            holder = (ViewHolder) convertView.getTag();
        }

        ParseObject statusObject = mStatus.get(position);

        // Getting username
        ParseObject user = statusObject.getParseUser("User_Id");

        String message_user_id = null;
        try
        {
            message_user_id = user.fetchIfNeeded().getObjectId();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }


        ParseUser current_user = ParseUser.getCurrentUser();
        String current_user_id = current_user.getObjectId();

        if(message_user_id.equals(current_user_id))
        {

            imv = holder.mydp;
            ParseObject dp = statusObject.getParseObject("User_Id");
            ParseFile imageFile = null;
            try {
                imageFile = dp.fetchIfNeeded().getParseFile("Profile_pic");
            } catch (ParseException e) {
                e.printStackTrace();
            }

            try
            {
                img_url = imageFile.getUrl();
                byte[] bitmapdata = imageFile.getData();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                holder.mydp.setImageBitmap(bitmap);
            }
            catch (Exception e)
            {
                img_url="";
            }

            holder.mydp.setVisibility(View.VISIBLE);
            holder.otherdp.setVisibility(View.GONE);

            holder.otherusername.setVisibility(View.GONE);
            holder.myusername.setText("Me");

            //msgdata
            ParseFile image = (ParseFile) statusObject.get("Msg_Data");
            if(image==null)
            {

                holder.otherdatalink.setVisibility(View.GONE);
                holder.mydatalink.setVisibility(View.GONE);

                // getting message
                holder.othermessage.setVisibility(View.GONE);
                String message = statusObject.getString("Message");
                holder.mymessage.setText(message);

                //getting time
                Date msgtime = statusObject.getCreatedAt();
                SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
                String time = msgtime.toString();
                holder.othertime.setVisibility(View.GONE);
                holder.mytime.setText(formatter.format(msgtime).toString());

            }
            else
            {

                holder.othermessage.setVisibility(View.GONE);
                holder.mymessage.setVisibility(View.VISIBLE);
                holder.mydatalink.setVisibility(View.VISIBLE);
                holder.otherdatalink.setVisibility(View.GONE);
                try
                {
                    img_url = image.getUrl();
                    String extension =img_url.substring(img_url.lastIndexOf(".")+1);
                    if(extension.equals("png"))
                    {
                        holder.mydatalink.setText(img_url);
                        Linkify.addLinks(holder.mydatalink, Linkify.WEB_URLS);
                    }
                    else
                    {
                        // other files here
                    }



                }
                catch (Exception e1)
                {
                    img_url="";
                }

                //getting time
                Date msgtime = statusObject.getCreatedAt();
                SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
                String time = msgtime.toString();
                holder.othertime.setVisibility(View.GONE);
                holder.mytime.setText(formatter.format(msgtime).toString());

            }

        }
        else
        {

            imv = holder.otherdp;
            ParseObject dp = statusObject.getParseObject("User_Id");
            ParseFile imageFile = null;
            try {
                imageFile = dp.fetchIfNeeded().getParseFile("Profile_pic");
            } catch (ParseException e) {
                e.printStackTrace();
            }

            try
            {
                img_url = imageFile.getUrl();
                byte[] bitmapdata = imageFile.getData();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                holder.otherdp.setImageBitmap(bitmap);
            }
            catch (Exception e)
            {
                img_url="";
            }

            holder.otherdp.setVisibility(View.VISIBLE);
            holder.mydp.setVisibility(View.GONE);

            String username = null;
            try
            {
                username = user.fetchIfNeeded().getString("Name");
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }

            holder.myusername.setVisibility(View.GONE);
            holder.otherusername.setText(username);

            //msgdata
            ParseFile image = (ParseFile) statusObject.get("Msg_Data");
            if(image==null)
            {
                holder.otherdatalink.setVisibility(View.GONE);
                holder.mydatalink.setVisibility(View.GONE);


                // getting message
                holder.mymessage.setVisibility(View.GONE);
                String message = statusObject.getString("Message");
                holder.othermessage.setText(message);

                //getting time
                Date msgtime = statusObject.getCreatedAt();
                SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
                String time = msgtime.toString();
                holder.mytime.setVisibility(View.GONE);
                holder.othertime.setText(formatter.format(msgtime).toString());
            }
            else
            {

                holder.mymessage.setVisibility(View.GONE);
                holder.othermessage.setVisibility(View.VISIBLE);
                holder.mydatalink.setVisibility(View.GONE);
                holder.otherdatalink.setVisibility(View.VISIBLE);
                try
                {
                    img_url = image.getUrl();
                    String extension =img_url.substring(img_url.lastIndexOf(".")+1);
                    if(extension.equals("png"))
                    {
                        holder.otherdatalink.setText(img_url);
                        Linkify.addLinks(holder.otherdatalink, Linkify.WEB_URLS);
                    }
                    else
                    {
                        // other files here
                    }

                }
                catch (Exception e1)
                {
                    img_url="";
                }

                //getting time
                Date msgtime = statusObject.getCreatedAt();
                SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
                String time = msgtime.toString();
                holder.mytime.setVisibility(View.GONE);
                holder.othertime.setText(formatter.format(msgtime).toString());
            }

        }


        return convertView;
    }

    public static class ViewHolder
    {
        ImageView otherdp;
        ImageView mydp;
        TextView othertime;
        TextView mytime;
        TextView otherusername;
        TextView othermessage;
        TextView myusername;
        TextView mymessage;
        TextView otherdatalink;
        TextView mydatalink;
    }

}

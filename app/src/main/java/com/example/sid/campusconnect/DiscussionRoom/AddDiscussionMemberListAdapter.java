package com.example.sid.campusconnect.DiscussionRoom;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sid on 05-Dec-15.
 */
public class AddDiscussionMemberListAdapter extends ArrayAdapter<ParseObject> {

    protected Context mContext;
    protected List<ParseObject> mStatus;

    public AddDiscussionMemberListAdapter(Context context, List<ParseObject> status)
    {
        super(context, R.layout.discussion_user_list, status);
        mContext = context;
        mStatus = status;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.discussion_user_list, null);

            holder = new ViewHolder();

            holder.user_name = (CheckBox) convertView
                    .findViewById(R.id.checkusername);

            holder.id = (TextView) convertView
                    .findViewById(R.id.userid);

            convertView.setTag(holder);
        }
        else
        {

            holder = (ViewHolder) convertView.getTag();
        }


        ParseObject statusObject = mStatus.get(position);

        // Getting username
        String usern = statusObject.getString("Name");
        holder.user_name.setText(usern);

        //getting object id of the user
        String user = statusObject.getObjectId();
        holder.id.setText(user);

        return convertView;
    }

    public static class ViewHolder
    {
       CheckBox user_name;
        TextView id;
    }
}

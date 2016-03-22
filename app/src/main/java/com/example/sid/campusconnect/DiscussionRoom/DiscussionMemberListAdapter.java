package com.example.sid.campusconnect.DiscussionRoom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Sid on 14-Jan-16.
 */
public class DiscussionMemberListAdapter extends ArrayAdapter<ParseObject> {

    protected Context mContext;
    protected List<ParseObject> mStatus;

    public DiscussionMemberListAdapter(Context context, List<ParseObject> status)
    {
        super(context, R.layout.discuss_member_list, status);
        mContext = context;
        mStatus = status;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.discuss_member_list, null);

            holder = new ViewHolder();

            holder.user_name = (TextView) convertView
                    .findViewById(R.id.member);

            holder.member_type = (TextView)convertView
                    .findViewById(R.id.membertype);

            holder.id = (TextView) convertView
                    .findViewById(R.id.uid);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        String current_user_name = null;
        try {
            current_user_name = ParseUser.getCurrentUser().fetchIfNeeded().getString("Name");
            //Toast.makeText(getContext(), current_user_name, Toast.LENGTH_LONG).show();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseObject statusObject = mStatus.get(position);

        ParseObject mem = statusObject.getParseObject("User_Id");
        String usern = null;
        try {
            usern = mem.fetchIfNeeded().getString("Name");
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        if (current_user_name.equals(usern)) {
            holder.user_name.setText("You");
        }
        else{
            holder.user_name.setText(usern);
        }

        try {
            Boolean memberty = mem.fetchIfNeeded().getBoolean("Is_Admin");
            if(!memberty){
                holder.member_type.setVisibility(View.GONE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //getting object id of the user
        String user = mem.getObjectId();
        holder.id.setText(user);

        return convertView;
    }

    public static class ViewHolder
    {
        TextView user_name;
        TextView id;
        TextView member_type;
    }
}

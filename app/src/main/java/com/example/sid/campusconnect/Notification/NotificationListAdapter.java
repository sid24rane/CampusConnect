package com.example.sid.campusconnect.Notification;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.R;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Sarthak on 1/7/2016.
 */
public class NotificationListAdapter extends ArrayAdapter<ParseObject> {
    protected Context mContext;
    protected List<ParseObject> mStatus;

    public NotificationListAdapter(Context context, List<ParseObject> status) {
        super(context, R.layout.view_notification_list, status);
        mContext = context;
        mStatus = status;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        ParseUser current_user = ParseUser.getCurrentUser();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.view_notification_list, null);
            holder = new ViewHolder();

            holder.n_data=(TextView) convertView.findViewById(R.id.n_data);
            holder.n_text=(TextView) convertView.findViewById(R.id.n_text);
            holder.n_name=(TextView) convertView.findViewById(R.id.n_name);
            holder.n_type=(TextView) convertView.findViewById(R.id.n_type);
            holder.n_id=(TextView) convertView.findViewById(R.id.n_id);
            holder.l=(RelativeLayout) convertView.findViewById(R.id.n_layout);
            holder.frreply=(TextView) convertView.findViewById(R.id.n_frreply);

            convertView.setTag(holder);

        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        ParseObject statusObject = mStatus.get(position);
        ParseUser not;
        ParseObject obj,res;

        final String type=statusObject.getString("Type");
        not=statusObject.getParseUser("Notifier");
        final String notifier=not.getString("Name");
        holder.n_name.setText(notifier);
        holder.n_type.setText(type);


        if(type.equals("Question")) {
            obj = statusObject.getParseObject("Question");
            res = statusObject.getParseObject("Answer");

            final String dis = " answered your question: ";
            final String dat = obj.getString("Title");
            holder.n_text.setText(dis);
            holder.n_data.setText(dat);
            holder.n_id.setText(res.getObjectId());
        }

        if(type.equals("Answer")) {
            obj = statusObject.getParseObject("Answer");
            res = statusObject.getParseObject("Comment");

            final String dis = " commented on your answer: ";
            final String dat = obj.getString("Description");
            holder.n_text.setText(dis);
            holder.n_data.setText(dat);
            holder.n_id.setText(res.getObjectId());
        }

        if(type.equals("Comment")) {
            obj = statusObject.getParseObject("Comment");
            res = statusObject.getParseObject("Reply");

            final String dis = " tagged you in a comment ";
            final String dat = obj.getString("Title");
            holder.n_text.setText(dis);
            holder.n_data.setText(dat);
            holder.n_id.setText(res.getObjectId());
            holder.frreply.setText(obj.getObjectId());
        }

        return convertView;
    }

    public static class ViewHolder {
        TextView n_name;
        TextView n_text;
        TextView n_data;
        TextView n_type;
        TextView n_id;
        TextView frreply;
        RelativeLayout l;
    }


}

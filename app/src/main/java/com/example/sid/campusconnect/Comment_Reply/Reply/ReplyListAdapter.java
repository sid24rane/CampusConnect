package com.example.sid.campusconnect.Comment_Reply.Reply;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.sid.campusconnect.R;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Sarthak on 12/17/2015.
 */
public class ReplyListAdapter extends ArrayAdapter<ParseObject> {
    protected Context mContext;
    protected List<ParseObject> mStatus;
//    View repV;

    public ReplyListAdapter(Context context, List<ParseObject> status) {
        super(context, R.layout.view_reply_list,status);
        mContext = context;
        mStatus = status;


    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ReplyListAdapter.ViewHolder holder;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) getContext()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.view_reply_list, null);


             holder = new ViewHolder();

             holder.rname = (TextView) convertView.findViewById(R.id.rep_name);
             holder.body = (TextView) convertView.findViewById(R.id.rep_body);


                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                ParseObject statusObject = mStatus.get(position);

                ParseUser usr = (ParseUser) statusObject.get("User_Id");
                String name = usr.getString("Name");
                holder.rname.setText(name);

                String b = statusObject.getString("Body");
                holder.body.setText(b);


        //repV=convertView;

        return convertView;
    }

    public static class ViewHolder {
        TextView rname, body;
    }
}

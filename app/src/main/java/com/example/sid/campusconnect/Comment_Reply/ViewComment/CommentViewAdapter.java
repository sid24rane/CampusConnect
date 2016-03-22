package com.example.sid.campusconnect.Comment_Reply.ViewComment;

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
 * Created by Sarthak on 12/11/2015.
 */
public class CommentViewAdapter extends ArrayAdapter<ParseObject> {
    protected Context mContext;
    protected List<ParseObject> mStatus;

    public CommentViewAdapter(Context context, List<ParseObject> status) {
        super(context, R.layout.view_comment_list, status);
        mContext = context;
        mStatus = status;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        ParseUser current_user = ParseUser.getCurrentUser();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.view_comment_list, null);


            holder = new ViewHolder();

            holder.owner=(TextView) convertView.findViewById(R.id.cmt_owner);
            holder.body=(TextView) convertView.findViewById(R.id.comment);
            holder.cmtid=(TextView) convertView.findViewById(R.id.tv_cmt_id);
            holder.tag=(TextView) convertView.findViewById(R.id.cmttag);
            //holder.list=(ListView) convertView.findViewById(R.id.list_rply);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        ParseObject statusObject = mStatus.get(position);

        ParseUser u= (ParseUser) statusObject.get("User_Id");
        String o=u.getString("Name");
        holder.owner.setText(o);

        String cmt = statusObject.getString("Body");
        holder.body.setText(cmt);

        String id = statusObject.getObjectId();
        holder.cmtid.setText(id);

        if(statusObject.getParseUser("Tagged_User")!=null)
        {
            String tagged=statusObject.getParseUser("Tagged_User").getString("Name");
            holder.tag.setText(tagged);
            holder.tag.setVisibility(View.VISIBLE);
        }

       /*
        /////////////////////////////////////////////////
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Reply");
        //query.whereNotEqualTo("IsReported",true);
        query.whereEqualTo("Cmt_Id", cmt_obj);
        query.include("User_Id");
        query.include("Cmt_Id");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> ansList, ParseException e) {
                if (e == null) {
                    int length = ansList.size();
                    if (length == 0) {
                        //Toast toast = Toast.makeText(getApplicationContext(), "No Replies available!", Toast.LENGTH_LONG);
                        //toast.show();
                        //loader.dismiss();
                    } else {
                        //findViewById(R.id.replysection).setVisibility(View.VISIBLE);
                        //loader.dismiss();
                        mStatus = ansList;
                       // ReplyListAdapter adapter = new ReplyListAdapter(getListView().getContext(), mStatus);
                        //setListAdapter(adapter);
                        holder.list.setAdapter(new ReplyListAdapter());

                    }
                } else {
                    //Toast.makeText(CommentView.this, "Error in query", Toast.LENGTH_LONG).show();
                }
            }
        });*/



        return convertView;
    }


    public static class ViewHolder {
        TextView owner, body,cmtid,tag;
        //ListView list;

    }
}




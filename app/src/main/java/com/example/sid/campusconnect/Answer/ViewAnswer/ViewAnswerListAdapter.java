package com.example.sid.campusconnect.Answer.ViewAnswer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Sarthak on 12/4/2015.
 */
public class ViewAnswerListAdapter extends ArrayAdapter<ParseObject> {

    protected Context mContext;
    protected List<ParseObject> mStatus;
    protected String img_url;

    public ViewAnswerListAdapter(Context context, List<ParseObject> status) {
        super(context, R.layout.view_ans_list, status);
        mContext = context;
        mStatus = status;
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        ParseUser current_user = ParseUser.getCurrentUser();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.view_ans_list, null);

            holder = new ViewHolder();

            holder.ans_des = (TextView) convertView.findViewById(R.id.ans_description);
            holder.usr_name = (TextView) convertView.findViewById(R.id.q_u_name);
            holder.answer_id = (TextView) convertView.findViewById(R.id.tv_ans_id);
            holder.uvote = (Button) convertView.findViewById(R.id.ans_upvote);
            holder.dvote = (Button) convertView.findViewById(R.id.ans_downvote);
            holder.data = (ImageView) convertView.findViewById(R.id.imgdata);

            convertView.setTag(holder);

        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        ParseObject statusObject = mStatus.get(position);

        String ans = statusObject.getString("Description");
        holder.ans_des.setText(ans);

        ParseObject user=statusObject.getParseUser("User_Id");
        String uname = user.get("Name").toString();
        holder.usr_name.setText(uname);

        String aid=statusObject.getObjectId().toString();
        holder.answer_id.setText(aid);

        ParseFile file;
        file= (ParseFile) statusObject.get("Data");
        if(file!=null) {
            try {
                img_url = file.getUrl();
                byte[] bitmapdata = file.getData();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                holder.data.setImageBitmap(bitmap);
            } catch (Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        ParseQuery<ParseObject> firstquery = ParseQuery.getQuery("Ans_Downvote");
        firstquery.include("Ans_Id");
        firstquery.include("User_Id");
        firstquery.whereEqualTo("Ans_Id",statusObject);
        firstquery.whereEqualTo("User_Id", current_user);

        firstquery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> ansList, ParseException e) {
                if (e == null) {
                    if (ansList.size() > 0) {
                        holder.dvote.setText("Downvoted");
                    }
                }

            }
        });

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Ans_Upvote");
        query.include("Ans_Id");
        query.include("User_Id");
        query.whereEqualTo("Ans_Id", statusObject);
        query.whereEqualTo("User_Id", current_user);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> ansList, ParseException e) {
                if (e == null) {
                    if (ansList.size() > 0) {
                        holder.uvote.setText("Upvoted");
                    }
                }

            }
        });


        return convertView;
    }


    public static class ViewHolder {
        TextView ans_des;
        TextView usr_name;
        TextView answer_id;
        Button uvote,dvote;
        ImageView data;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 500;
    }

}



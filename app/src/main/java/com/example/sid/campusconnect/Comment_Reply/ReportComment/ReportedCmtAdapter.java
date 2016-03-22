package com.example.sid.campusconnect.Comment_Reply.ReportComment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.R;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Sarthak on 1/19/2016.
 */
public class ReportedCmtAdapter extends ArrayAdapter <ParseObject> {

    protected Context mContext;
    protected List<ParseObject> mStatus;

    public ReportedCmtAdapter(Context context, List<ParseObject> status) {
        super(context, R.layout.reported_cmt_list, status);
        mContext = context;
        mStatus = status;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.reported_cmt_list, null);

            holder = new ViewHolder();

            holder.Answer = (TextView) convertView
                    .findViewById(R.id.rep_c_ans);

            holder.Commenter = (TextView) convertView
                    .findViewById(R.id.rep_cmtr);

            holder.Comment = (TextView) convertView
                    .findViewById(R.id.rep_cmt);

            holder.idata = (ImageView) convertView
                    .findViewById(R.id.imgdata);

            holder.c_id = (TextView) convertView
                    .findViewById(R.id.rep_cid);

            holder.cu_id = (TextView) convertView
                    .findViewById(R.id.rep_cuid);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        ParseObject statusObject = mStatus.get(position);

        ParseUser answerer;
        answerer=statusObject.getParseUser("User_Id");
        String name=answerer.getString("Name");
        holder.Commenter.setText(name);
        holder.cu_id.setText(answerer.getObjectId().toString());

        ParseObject answr;
        answr=statusObject.getParseObject("Ans_Id");
        String ans=answr.getString("Description");
        holder.Answer.setText(ans);

        String cmt=statusObject.getString("Body");
        holder.Comment.setText(cmt);
        holder.c_id.setText(statusObject.getObjectId().toString());

        ParseFile file;
        file= (ParseFile) statusObject.get("Data");
        if(file!=null) {
            try {
                byte[] bitmapdata = file.getData();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                holder.idata.setImageBitmap(bitmap);
            } catch (Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }



        return convertView;
    }


    public static class ViewHolder {
        TextView Answer;
        TextView Commenter;
        TextView Comment;
        ImageView idata;
        TextView cu_id,c_id;
    }


}


package com.example.sid.campusconnect.Answer.ReportAnswer;

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
 * Created by Sarthak on 12/8/2015.
 */
public class ReportedAnsListAdapter extends ArrayAdapter <ParseObject> {

    protected Context mContext;
    protected List<ParseObject> mStatus;

    public ReportedAnsListAdapter(Context context, List<ParseObject> status) {
        super(context, R.layout.reported_ans_list, status);
        mContext = context;
        mStatus = status;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.reported_ans_list, null);

            holder = new ViewHolder();

            holder.Question = (TextView) convertView
                    .findViewById(R.id.rep_ques);

            holder.Answerer = (TextView) convertView
                    .findViewById(R.id.rep_answerer);

            holder.Answer = (TextView) convertView
                    .findViewById(R.id.rep_answer);

            holder.idata = (ImageView) convertView
                    .findViewById(R.id.imgdata);

            holder.a_id = (TextView) convertView
                    .findViewById(R.id.rep_aid);

            holder.u_id = (TextView) convertView
                    .findViewById(R.id.rep_auid);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        ParseObject statusObject = mStatus.get(position);

        ParseUser answerer;
        answerer=statusObject.getParseUser("User_Id");
        String name=answerer.getString("Name");
        holder.Answerer.setText(name);
        holder.u_id.setText(answerer.getObjectId().toString());

        ParseObject ques;
        ques=statusObject.getParseObject("Qs_Id");
        String quest=ques.getString("Title");
        holder.Question.setText(quest);

        String ans=statusObject.getString("Description");
        holder.Answer.setText(ans);
        holder.a_id.setText(statusObject.getObjectId().toString());

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
        TextView Question;
        TextView Answerer;
        TextView Answer;
        ImageView idata;
        TextView u_id,a_id;
    }

}


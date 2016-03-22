package com.example.sid.campusconnect.Question.ReportQuestion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.sid.campusconnect.R;
import com.parse.ParseObject;

import java.util.List;

/**
 * Created by Sid on 03-Dec-15.
 */
public class ReportedQsListAdapter extends ArrayAdapter<ParseObject> {

    protected Context mContext;
    protected List<ParseObject> mStatus;

    public ReportedQsListAdapter(Context context, List<ParseObject> status)
    {
        super(context, R.layout.reported_qs_list, status);
        mContext = context;
        mStatus = status;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.reported_qs_list, null);

            holder = new ViewHolder();

            holder.QuestionTitle = (TextView) convertView
                    .findViewById(R.id.ReQsTitle);

            holder.QuestionCategory = (TextView) convertView
                    .findViewById(R.id.ReQsCategory);

            holder.QuestionId = (TextView) convertView
                    .findViewById(R.id.ReQsId);

            convertView.setTag(holder);
        }
        else
        {

            holder = (ViewHolder) convertView.getTag();
        }
        ParseObject statusObject = mStatus.get(position);

        // Getting username
        String Qstitle = statusObject.getString("Title");
        holder.QuestionTitle.setText(Qstitle);

        // getting Department
        String Qscategory = statusObject.getString("Category");
        holder.QuestionCategory.setText(Qscategory);

        //getting object id of the user
        String qsid = statusObject.getObjectId();
        holder.QuestionId.setText(qsid);

        final View finalConvertView = convertView;
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                finalConvertView.requestFocus();
                return false;
            }
        });


        return convertView;

    }

    public static class ViewHolder
    {
        TextView QuestionTitle;
        TextView QuestionCategory;
        TextView QuestionId;
    }
}

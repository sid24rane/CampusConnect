package com.example.sid.campusconnect.Answer.WriteAnswer;

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
 * Created by Sid on 20-Jan-16.
 */
public class WriteAnswerListAdapter extends ArrayAdapter<ParseObject> {
    protected Context mContext;
    protected List<ParseObject> mStatus;

    public WriteAnswerListAdapter(Context context, List<ParseObject> status) {
        super(context, R.layout.write_ans_list, status);
        mContext = context;
        mStatus = status;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.write_ans_list, null);

            holder = new ViewHolder();

            holder.QuestionTitle = (TextView) convertView
                    .findViewById(R.id.QsTitle);

            holder.QuestionCategory = (TextView) convertView
                    .findViewById(R.id.QsCategory);

            holder.QuestionId = (TextView) convertView
                    .findViewById(R.id.QsIds);

            convertView.setTag(holder);
        } else {

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

        return convertView;
    }

    public static class ViewHolder {
        TextView QuestionTitle;
        TextView QuestionCategory;
        TextView QuestionId;
    }

}
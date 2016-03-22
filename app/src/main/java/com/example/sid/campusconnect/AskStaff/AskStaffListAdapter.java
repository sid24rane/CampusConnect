package com.example.sid.campusconnect.AskStaff;

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
 * Created by ROHAN on 04-12-2015.
 */
public class AskStaffListAdapter extends ArrayAdapter<ParseObject>
{
    protected Context mContext;
    protected List<ParseObject> mStatus;

    public AskStaffListAdapter(Context context, List<ParseObject> status) {
        super(context, R.layout.ask_staff_list, status);
        mContext = context;
        mStatus = status;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.ask_staff_list, null);

            holder = new ViewHolder();

            holder.StaffName = (TextView) convertView
                    .findViewById(R.id.StaffName);

            holder.StaffId = (TextView) convertView
                    .findViewById(R.id.StaffId);


            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        ParseObject statusObject = mStatus.get(position);

        // Getting staffname
        String SName = statusObject.getString("Name");
        holder.StaffName.setText(SName);

        String SId = statusObject.getObjectId();
        holder.StaffId.setText(SId);


        return convertView;
    }

    public static class ViewHolder
    {
        TextView StaffName;
        TextView StaffId;

    }
}

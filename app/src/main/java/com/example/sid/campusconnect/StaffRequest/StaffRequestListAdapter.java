package com.example.sid.campusconnect.StaffRequest;

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
 * Created by ROHAN on 06-12-2015.
 */
public class StaffRequestListAdapter extends ArrayAdapter<ParseObject>
{
    protected Context mContext;
    protected List<ParseObject> mStatus;

    public StaffRequestListAdapter(Context context, List<ParseObject> status) {
        super(context, R.layout.staff_request_list, status);
        mContext = context;
        mStatus = status;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.staff_request_list, null);

            holder = new ViewHolder();


            holder.QsTitle = (TextView) convertView
                    .findViewById(R.id.srqTitle);

            holder.QsId = (TextView) convertView
                    .findViewById(R.id.SRQsId);

            holder.ObjId = (TextView) convertView
                    .findViewById(R.id.SRObjId);


            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        ParseObject statusObject = mStatus.get(position);


        String SId = statusObject.getString("Qs_Title");
        holder.QsTitle.setText(SId);

        String Obj_Id = statusObject.getObjectId();
        holder.ObjId.setText(Obj_Id);

        //String QId = statusObject.getString("Qs_Id");
        ParseObject QueId = statusObject.getParseObject("Qs_Id");
        String QId = QueId.getObjectId();

        holder.QsId.setText(QId);


        return convertView;
    }

    public static class ViewHolder
    {
        TextView QsTitle;
        TextView QsId;
        TextView ObjId;

    }
}

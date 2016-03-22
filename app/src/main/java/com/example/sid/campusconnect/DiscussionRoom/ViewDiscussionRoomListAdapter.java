package com.example.sid.campusconnect.DiscussionRoom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.sid.campusconnect.R;

import java.util.ArrayList;

/**
 * Created by Sid on 06-Dec-15.
 */
public class ViewDiscussionRoomListAdapter extends ArrayAdapter<DiscussionGetterSetter> {
    private final Context context;
    private final ArrayList<DiscussionGetterSetter> itemsArrayList;

    public ViewDiscussionRoomListAdapter(Context context, ArrayList<DiscussionGetterSetter> itemsArrayList) {

        super(context, R.layout.view_discussion_room_list, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder holder;

        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.view_discussion_room_list, null);

            holder = new ViewHolder();

            holder.dTitle = (TextView) convertView
                    .findViewById(R.id.DisTopic);

            holder.dstatus = (TextView) convertView
                    .findViewById(R.id.DisStatus);

            holder.dId = (TextView) convertView
                    .findViewById(R.id.disid);

            convertView.setTag(holder);
        }
        else
        {

            holder = (ViewHolder) convertView.getTag();
        }

        // Getting username
        //  String Qstitle = statusObject.getString("Title");
        holder.dTitle.setText(itemsArrayList.get(position).getSubject());

        // getting Department
        //String Qscategory = statusObject.getString("Category");
        holder.dstatus.setText(itemsArrayList.get(position).getStatus());

        //getting object id of the user
        //String qsid = statusObject.getObjectId();
        holder.dId.setText(itemsArrayList.get(position).getDisid());

        return convertView;
    }

    public static class ViewHolder
    {
        TextView dTitle;
        TextView dstatus;
        TextView dId;
    }

    /*
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.view_discussion_room_list, parent, false);

        TextView subject = (TextView) rowView.findViewById(R.id.DisTopic);
        TextView status = (TextView) rowView.findViewById(R.id.DisStatus);
        TextView id = (TextView) rowView.findViewById(R.id.disid);

        subject.setText(itemsArrayList.get(position).getSubject());
        status.setText(itemsArrayList.get(position).getStatus());
        id.setText(itemsArrayList.get(position).getDisid());

        return rowView;
*/
}

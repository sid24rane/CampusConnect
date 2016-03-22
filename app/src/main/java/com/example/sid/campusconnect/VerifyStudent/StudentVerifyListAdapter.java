package com.example.sid.campusconnect.VerifyStudent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import com.parse.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by Sid on 27-Nov-15.
 */
public class StudentVerifyListAdapter extends ArrayAdapter<ParseObject>
{
    protected Context mContext;
    protected List<ParseObject> mStatus;
    String img_url;
    ImageView imv;

    public StudentVerifyListAdapter(Context context, List<ParseObject> status) {
        super(context, R.layout.student_verify_list, status);
        mContext = context;
        mStatus = status;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.student_verify_list, null);

            holder = new ViewHolder();

            holder.UserName = (TextView) convertView
                    .findViewById(R.id.UserName);

            holder.Department = (TextView) convertView
                    .findViewById(R.id.Department);

            holder.ObjectId = (TextView) convertView
                    .findViewById(R.id.UserId);

            holder.Profile_Pic = (ImageView) convertView
                    .findViewById(R.id.ProfilePic);

            convertView.setTag(holder);

        }
        else
        {

            holder = (ViewHolder) convertView.getTag();
        }


        ParseObject statusObject = mStatus.get(position);

        // Getting username
        String username = statusObject.getString("Name");
        holder.UserName.setText(username);

        // getting Department
        String deptname = statusObject.getString("Dept");
        holder.Department.setText(deptname);

        //getting object id of the user
        String objectid = statusObject.getObjectId();
        holder.ObjectId.setText(objectid);

        //getting profile pic of the user
        imv = holder.Profile_Pic;
        ParseFile imageFile = statusObject.getParseFile("Profile_pic");

        try
        {
            img_url = imageFile.getUrl();
            byte[] bitmapdata = imageFile.getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata , 0, bitmapdata.length);
            holder.Profile_Pic.setImageBitmap(bitmap);
        }
        catch (Exception e)
            {
            img_url="";
            }

        return convertView;
    }

    public static class ViewHolder {
        TextView UserName;
        TextView Department;
        TextView ObjectId;
        ImageView Profile_Pic;
    }

}
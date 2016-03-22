package com.example.sid.campusconnect.Home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.Question.QuestionDetail;
import com.example.sid.campusconnect.R;
import com.example.sid.campusconnect.UserProfile.UserProfile;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Sarthak on 1/21/2016.
 */
public class HomeAdapter extends ArrayAdapter<ParseObject> {
    protected Context mContext;
    protected List<ParseObject> mStatus;
    ParseObject ans_obj;
    Context ctx;
    String img_url;
    String ans_type;


    public HomeAdapter(Context context, List<ParseObject> status, String answer_type) {
        super(context, R.layout.view_list_home, status);
        mContext = context;
        mStatus = status;
        this.ctx = context;
        ans_type=answer_type;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final ParseUser current_user = ParseUser.getCurrentUser();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.view_list_home, null);


            holder = new ViewHolder();

            holder.QsTitle = (TextView) convertView.findViewById(R.id.QsTitle);
            holder.QsCategory = (TextView) convertView.findViewById(R.id.QsCategory);
            holder.QsDescription = (TextView) convertView.findViewById(R.id.QsDescription);
            holder.QsId = (TextView) convertView.findViewById(R.id.QsId);
            holder.Answer = (TextView) convertView.findViewById(R.id.Ans);
            holder.Answerer = (TextView) convertView.findViewById(R.id.Answerer);
            holder.imv = (ImageView) convertView.findViewById(R.id.imgdata);
            holder.showmore = (Button) convertView.findViewById(R.id.viewmore);
            holder.ansLayout = (LinearLayout) convertView.findViewById(R.id.layout_ans);
            holder.uvote = (Button) convertView.findViewById(R.id.ans_upvote);
            holder.dvote = (Button) convertView.findViewById(R.id.ans_downvote);
            holder.ansid = (TextView) convertView.findViewById(R.id.tv_ans_id);
            holder.ustatus = (TextView) convertView.findViewById(R.id.ustatus);
            holder.dstatus = (TextView) convertView.findViewById(R.id.dstatus);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ParseObject statusObject = mStatus.get(position);

        final String qsid = statusObject.getObjectId();
        holder.QsId.setText(qsid);

        final String qscat = statusObject.getString("Category");
        holder.QsCategory.setText(qscat);

        final String qstitle = statusObject.getString("Title");
        holder.QsTitle.setText(qstitle);

        holder.QsTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, QuestionDetail.class);
                //NOTE: THE MOST IMP STEP ==> PASSING QSID THROUGH INTENT TO NEXT ACTIVITY
                intent.putExtra("question_id", qsid);
                ctx.startActivity(intent);
            }
        });

        final String qsDes = statusObject.getString("Description");
        holder.QsDescription.setText(qsDes);


        holder.showmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> ansquery = ParseQuery.getQuery("Answer");
                ansquery.whereEqualTo("Qs_Id", statusObject);
                ansquery.include("User_Id");
                if(ans_type.toLowerCase().equals("latest")){
                    ansquery.orderByDescending("updatedAt");
                }
                else
                {
                    ansquery.orderByDescending("Upvote_Count");
                }
                    ansquery.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, com.parse.ParseException e) {
                        if (e == null) {
                            ans_obj = parseObject;
                            holder.Answerer.setText(parseObject.getParseUser("User_Id").getString("Name").toString());
                            holder.ansid.setText(ans_obj.getObjectId());
                            final String user_id = ans_obj.getParseUser("User_Id").getObjectId();
                            holder.Answerer.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ctx, UserProfile.class);
                                    //NOTE: THE MOST IMP STEP ==> PASSING USERID THROUGH INTENT TO NEXT ACTIVITY
                                    intent.putExtra("user_id", user_id);
                                    ctx.startActivity(intent);
                                }
                            });


                            holder.Answer.setText(parseObject.getString("Description"));
                            holder.ansLayout.setVisibility(View.VISIBLE);
                            holder.showmore.setVisibility(View.GONE);
                            notifyDataSetChanged();
                            ParseFile imageFile = (ParseFile) ans_obj.get("Data");

                            try {
                                img_url = imageFile.getUrl();
                                byte[] bitmapdata = imageFile.getData();
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                                holder.imv.setImageBitmap(bitmap);
                            } catch (Exception e1) {
                            }

                            ParseQuery<ParseObject> firstquery = ParseQuery.getQuery("Ans_Downvote");
                            firstquery.include("Ans_Id");
                            firstquery.include("User_Id");
                            firstquery.whereEqualTo("Ans_Id", ans_obj);
                            firstquery.whereEqualTo("User_Id", current_user);

                            firstquery.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> ansList, ParseException e) {
                                    if (e == null) {
                                        if (ansList.size() > 0) {
                                            holder.dvote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.downvote, 0, 0, 0);
                                            holder.dstatus.setText("downvoted");
                                        }
                                    }

                                }
                            });

                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Ans_Upvote");
                            query.include("Ans_Id");
                            query.include("User_Id");
                            query.whereEqualTo("Ans_Id", ans_obj

                            );
                            query.whereEqualTo("User_Id", current_user);
                            query.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> ansList, ParseException e) {
                                    if (e == null) {
                                        if (ansList.size() > 0) {
                                            holder.uvote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.upvote, 0, 0, 0);
                                            holder.ustatus.setText("upvoted");
                                        }
                                    }

                                }
                            });


                        } else {
                            Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });


            }
        });

        return convertView;
    }

    public static class ViewHolder {
        TextView QsTitle, QsCategory, QsDescription, QsId, Answerer, Answer, ansid,ustatus,dstatus;
        ImageView imv;
        Button showmore;
        Button uvote,dvote;
        LinearLayout ansLayout;

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

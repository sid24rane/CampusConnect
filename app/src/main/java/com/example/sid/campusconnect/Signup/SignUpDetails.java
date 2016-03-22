package com.example.sid.campusconnect.Signup;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid.campusconnect.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SignUpDetails extends AppCompatActivity {

    final Context context = this;
    DatePicker calendar;
    java.sql.Date dob = null;
    Dialog dialog;
    DatePickerDialog dpg;
    ParseUser user;
    TextView dpif;
    int day,year,month;
    int sizeInBytes = 0;
    int MAX_BYTES = 12000;

    private boolean isdp = false;

    static final int DATE_DIALOG_ID = 999;
    private static int RESULT_LOAD_IMAGE = 1;


    EditText fname, lname;
    TextView date;
    String dept, gender;
    Spinner sdept, sgender;
    Bitmap imv1, imv2, imv3;
    private boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_details);
        user = ParseUser.getCurrentUser();

        fname = (EditText) findViewById(R.id.edt_Fname);
        lname = (EditText) findViewById(R.id.edt_Lname);
        sdept = (Spinner) findViewById(R.id.spinner_dept);
        sgender = (Spinner) findViewById(R.id.spinner_gender);
        date = (TextView) findViewById(R.id.dob);
        dpif = (TextView) findViewById(R.id.sign_ifdp);

       // addListenerOnButton();

        findViewById(R.id.sign_btn_dob).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*dialog = new Dialog(context);
                dialog.setContentView(R.layout.popup_dob);
                dialog.show();*/

                showDialog(DATE_DIALOG_ID);
                //setCurrentDateOnView();
                //onDateChanged(calendar, year, day, month);

            }
        });

        findViewById(R.id.sign_btn_dp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });


        findViewById(R.id.sign_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dlg = new ProgressDialog(SignUpDetails.this);
                dlg.setTitle("Please wait.");
                dlg.setMessage("Updating Profile. A moment please...");
                dlg.show();

                imv1 = BitmapFactory.decodeResource(getResources(), R.drawable.male);
                imv2 = BitmapFactory.decodeResource(getResources(), R.drawable.female);
                imv3 = BitmapFactory.decodeResource(getResources(), R.drawable.unspecified);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();



                String st = fname.getText().toString() + " " + lname.getText().toString();

                dept = String.valueOf(sdept.getSelectedItem());
                gender = String.valueOf(sgender.getSelectedItem());




                if(isdp==false) {
                    if (gender.equals("Male")) {
                        imv1.compress(Bitmap.CompressFormat.PNG, 100, bos);
                        byte[] data = bos.toByteArray();
                        ParseFile file = new ParseFile("dp.png", data);
                        user.put("Profile_pic", file);
                        file.saveInBackground();
                        //user.saveInBackground();
                    } else if (gender.equals("Female")) {
                        imv2.compress(Bitmap.CompressFormat.PNG, 100, bos);
                        byte[] data = bos.toByteArray();
                        ParseFile file = new ParseFile("dp.png", data);
                        user.put("Profile_pic", file);
                        file.saveInBackground();
                        // user.saveInBackground();
                    } else {
                        imv3.compress(Bitmap.CompressFormat.PNG, 100, bos);
                        byte[] data = bos.toByteArray();
                        ParseFile file = new ParseFile("dp.png", data);
                        user.put("Profile_pic", file);
                        file.saveInBackground();
                        //user.saveInBackground();
                    }
                }

                if (fname.getText().toString().equals("")||lname.getText().toString().equals("")||dob==null){
                    Toast.makeText(SignUpDetails.this, "Please Enter Complete Details", Toast.LENGTH_LONG).show();
                    dlg.hide();
                }
                else {
                    user.put("Name", st);
                    user.put("Dept", dept);
                    user.put("Gender", gender);
                    user.put("Dob", dob);
                    user.put("isComplete", true);
                    try {
                        user.save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(isdp==false) {
                        Toast.makeText(SignUpDetails.this, "Details Updated. Default Profile pic is set", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(SignUpDetails.this, "Details Updated", Toast.LENGTH_LONG).show();
                    }

                    Intent intent = new Intent(SignUpDetails.this, Proceed.class);
                    startActivity(intent);
                }

            }
        });

    }



    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            dob = new java.sql.Date(year,month,day);
            dob.setYear(year - 1900);
            String dt =dob.toString();
            Toast.makeText(SignUpDetails.this,dt,Toast.LENGTH_LONG).show();
            date.setText(dt);
            dob.setDate(day + 1);

        }
    };


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
//                calendar.init(year, month, day, null);

                return new DatePickerDialog(this, datePickerListener,
                        year, month,day);
        }
        return null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                sizeInBytes=bitmap.getRowBytes();
            } catch (IOException e) {
                e.printStackTrace();
                sizeInBytes=80000;
            }




            if(sizeInBytes<=MAX_BYTES) {
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();


                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] image = stream.toByteArray();

                ParseFile file = new ParseFile("dp.png", image);
                file.saveInBackground();

                user.put("Profile_pic", file);
                //user.saveInBackground();
                isdp = true;
                Toast.makeText(SignUpDetails.this, "File Uploaded", Toast.LENGTH_SHORT).show();
                dpif.setText("DP Selected");
            }
            else
            {
                Toast.makeText(SignUpDetails.this, "File not Uploaded. File size should be less than  1 mb", Toast.LENGTH_SHORT).show();
            }


        }
    }


}

package com.schwifty.bits_delivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.schwifty.bits_delivery.UTILS.Loader;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    ImageView imageView;
    TextView textName, textEmail;
    FirebaseAuth mAuth;

    String Name;
    String Email,RawEmail;
    DatabaseReference userRef;

    DatePickerDialog datePickerDialog;

    View vLogout,vHome;
    Button vMessSkip;
    int i=0;
    int flag =0;
    //TextView vMessSkipStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        imageView = findViewById(R.id.imageView);
        textName = findViewById(R.id.textViewName);
        textEmail = findViewById(R.id.textViewEmail);
        vLogout = findViewById(R.id.Logout);
        vHome = findViewById(R.id.Home);
        vMessSkip =findViewById(R.id.mess_skip);
        //vMessSkipStatus = findViewById(R.id.status_mess_skip);



        FirebaseUser user = mAuth.getCurrentUser();

        Glide.with(this)
                .load(user.getPhotoUrl())
                .into(imageView);

        textName.setText(user.getDisplayName());
        textEmail.setText(user.getEmail());
        Email = user.getEmail().toString();
        RawEmail = Email;
        Name = user.getDisplayName().toString();

        if(Email.contains("."))Email = Email.replace('.','_');

        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Email);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //if the user is not logged in
        //opening the login activity
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, Login_Register.class));
        }

        vLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                vLogout.setEnabled(false);

                Intent i = new Intent(ProfileActivity.this,Login_Register.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        vHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this,MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

        Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Loader l = new Loader("",R.layout.loading,ProfileActivity.this,false);
                l.getDialog().show();
                if(!dataSnapshot.hasChild("Name"))
                {
                    userRef.child("Name").setValue(Name);
                }

                if(!dataSnapshot.hasChild("Email"))
                {
                    userRef.child("Email").setValue(RawEmail);
                }
                if(!dataSnapshot.hasChild("Id"))
                {
                    Ion.with(ProfileActivity.this)
                            .load("https://us-central1-bitsdelivery-6a7e4.cloudfunctions.net/getStudentDetails?flag=true&dest="+RawEmail)
                            .asString()
                            .withResponse()
                            .setCallback(new FutureCallback<Response<String>>() {
                                @Override
                                public void onCompleted(Exception e, Response<String> result) {

                                    l.getDialog().dismiss();
                                    ((TextView)findViewById(R.id.____Id)).setText(result.getResult().split("Id :")[1].trim().split("</p>")[0]);
                                }
                            });

                }
                else
                {
                    l.getDialog().dismiss();
                    ((TextView)findViewById(R.id.____Id)).setText(dataSnapshot.child("Id").getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        String todayAsString = dateFormat.format(today);
        final String tomorrowAsString = dateFormat.format(tomorrow);

        vMessSkip.setEnabled(true);

        vMessSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(ProfileActivity.this);
                dialog.setContentView(R.layout.dialog_confirm_mess_skip);
                dialog.setTitle("Enter the passcode");
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(false);

                View v_ok, v_cancel;
                TextView vMessege;

                v_ok = dialog.findViewById(R.id.confirm_corr);
                v_cancel = dialog.findViewById(R.id.confirm_cancel);
                vMessege = dialog.findViewById(R.id.confirm_text);

                vMessege.setText("Are you sure to skip the mess tomorrow(" + tomorrowAsString + ") ?");

                v_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final Loader _l = new Loader("",R.layout.loading,ProfileActivity.this,false);
                        _l.getDialog().show();;


                        Ion.with(ProfileActivity.this)
                                .load("https://us-central1-bitsdelivery-6a7e4.cloudfunctions.net/setTodayMessOption?dest="+RawEmail)
                                .asString()
                                .withResponse()
                                .setCallback(new FutureCallback<Response<String>>() {
                                    @Override
                                    public void onCompleted(Exception e, Response<String> result) {

                                        String status = result.getResult().split(":")[1].trim();

                                        final Snackbar snackbar;

                                        if(status.equals("1"))
                                        {
                                            snackbar = Snackbar.make(findViewById(R.id.ProfileContent),"Your response has been successfully recorded.",Snackbar.LENGTH_INDEFINITE);
                                        }
                                        else if(status.equals("2"))
                                        {
                                            snackbar = Snackbar.make(findViewById(R.id.ProfileContent),"You've already skipped the mess twice this month.",Snackbar.LENGTH_INDEFINITE);

                                        }
                                        else if (status.equals("3"))
                                        {
                                            snackbar = Snackbar.make(findViewById(R.id.ProfileContent),"You're already skipping the mess tomorrow.",Snackbar.LENGTH_INDEFINITE);

                                        }else
                                        {
                                            snackbar = Snackbar.make(findViewById(R.id.ProfileContent),"Something went wrong, try again.",Snackbar.LENGTH_INDEFINITE);

                                        }

                                        snackbar.setAction("close",new View.OnClickListener(){

                                            @Override
                                            public void onClick(View view) {
                                                snackbar.dismiss();
                                            }
                                        });
                                        snackbar.show();

                                        dialog.dismiss();
                                        _l.getDialog().dismiss();

                                    }
                                });

                    }
                });

                v_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vMessSkip.setEnabled(true);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }});

    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2)
    {
        SetSkipMessInFirebase(i2,i1+1,i);
    }

    private void SetSkipMessInFirebase(int d, int m ,int y)
    {
        String key = userRef.child("SkipMess").child(Integer.toString(m)+"_"+Integer.toString(y)).push().getKey().toString();
        userRef.child("SkipMess").child(Integer.toString(m)+"_"+Integer.toString(y)).child(key).child("day").setValue(Integer.toString(d));

    }
}
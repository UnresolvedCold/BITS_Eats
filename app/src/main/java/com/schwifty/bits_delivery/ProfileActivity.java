package com.schwifty.bits_delivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    View vLogout,vMessSkip;
    TextView vMessSkipStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        imageView = findViewById(R.id.imageView);
        textName = findViewById(R.id.textViewName);
        textEmail = findViewById(R.id.textViewEmail);
        vLogout = findViewById(R.id.Logout);
        vMessSkip =findViewById(R.id.mess_skip);
        vMessSkipStatus = findViewById(R.id.status_mess_skip);


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

        Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild("Name"))
                {
                    userRef.child("Name").setValue(Name);
                }

                if(!dataSnapshot.hasChild("Email"))
                {
                    userRef.child("Email").setValue(RawEmail);
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

        vMessSkip.setEnabled(false);

        userRef.child("SkipMess").child(Integer.toString(mMonth+1)+"_"+Integer.toString(mYear))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {

                        if(dataSnapshot.getChildrenCount()>=2)
                        {

                            vMessSkip.setEnabled(false);
                            vMessSkip.setVisibility(View.GONE);
                            vMessSkipStatus.setText("You've already skipped the mess 2 times this month");
                            vMessSkipStatus.setVisibility(View.VISIBLE);
                            Toast.makeText(ProfileActivity.this, "Not allowed more than twice", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            int i=0;
                            for(DataSnapshot snapshot : dataSnapshot.getChildren())
                            {
                                String days = snapshot.child("day").getValue().toString();

                                if(days.equals(tomorrowAsString.split("-")[0]))
                                {
                                    i++;
                                }
                            }

                            if(i>0)
                            {
                                vMessSkip.setEnabled(false);
                                vMessSkip.setVisibility(View.GONE);
                                vMessSkipStatus.setText("You're skipping mess tomorrow");
                                vMessSkipStatus.setVisibility(View.VISIBLE);
                            }
                            else {
                                vMessSkip.setEnabled(true);
                                vMessSkip.setVisibility(View.VISIBLE);
                                vMessSkipStatus.setText("You can skip "+(2-i)+" time");
                                if(i>1)vMessSkipStatus.append("s");
                                vMessSkipStatus.setVisibility(View.VISIBLE);
                            }

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        vMessSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                vMessSkip.setEnabled(false);
                Calendar calendar = Calendar.getInstance();
                Date today = calendar.getTime();

                calendar.add(Calendar.DAY_OF_YEAR, 1);
                Date tomorrow = calendar.getTime();
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

                String todayAsString = dateFormat.format(today);
                String tomorrowAsString = dateFormat.format(tomorrow);

                SetSkipMessInFirebase(Integer.parseInt(tomorrowAsString.split("-")[0]),
                        Integer.parseInt(tomorrowAsString.split("-")[1]),
                        Integer.parseInt(tomorrowAsString.split("-")[2])
                );

                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                {
                    datePickerDialog = new DatePickerDialog(ProfileActivity.this,ProfileActivity.this, mYear, mMonth, mDay);

                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()+(long)24*60*60*1000);

                    datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() +
                            (long)(Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)-mDay)*24*60*60*1000);

                    datePickerDialog.show();
                }
                else
                {

                }*/
            }
        });
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
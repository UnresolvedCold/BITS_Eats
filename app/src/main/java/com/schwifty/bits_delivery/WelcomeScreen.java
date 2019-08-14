package com.schwifty.bits_delivery;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

public class WelcomeScreen extends AppCompatActivity {

    private DatabaseReference ref;

    private String appVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        appVersion = "1.0.0";

        /*ref=FirebaseDatabase.getInstance().getReference().child("Variables");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("appVersion")&&dataSnapshot.hasChild("isOn")
                        &&dataSnapshot.hasChild("timeToWait"))
                {

                    if(!dataSnapshot.child("appVersion").getValue().toString().equals(appVersion))
                    {
                        WaitForSomeTimeThenQuit(Toast.LENGTH_LONG,"Update the app");
                    }
                    else
                    if(!dataSnapshot.child("isOn").getValue().toString().equals("true"))
                    {
                        WaitForSomeTimeThenQuit(Toast.LENGTH_LONG,"Sorry but our server is off\nTry after sometime");
                    }
                    else
                    if(dataSnapshot.hasChild("timeToWait"))
                    {
                        long t = Long.parseLong(dataSnapshot.child("timeToWait").getValue().toString());
                      //  WaitForSomeTimeThenStart(t);
                    }
                }
                else
                {
                    WaitForSomeTimeThenQuit(Toast.LENGTH_LONG,"Sorry!! some error occured,\nTry again after sometime");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        /*FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(
                WelcomeScreen.this,
                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        instanceIdResult.getToken();
                    }
                }
        );*/

        FirebaseMessaging.getInstance().subscribeToTopic("Delivery")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                        }

                    }
                });

        WaitForSomeTimeThenStart(4000);


    }

    private void WaitForSomeTimeThenStart(final long t)
    {
        Thread thread = new Thread(){
            @Override
            public void run() {
                try
                {
                    sleep(t);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    Intent intent = new Intent(WelcomeScreen.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                }
            }
        };

        thread.start();
    }

    private void WaitForSomeTimeThenQuit(final long t, String message)
    {
        Toast.makeText(WelcomeScreen.this, message, Toast.LENGTH_LONG).show();

        Thread thread = new Thread(){
            @Override
            public void run() {
                try
                {
                    sleep(t);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                        finishAndRemoveTask();
                    } else{
                        System.exit(0);
                    }
                }
            }
        };

        thread.start();
    }


}

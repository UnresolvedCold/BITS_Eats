package com.schwifty.bits_delivery;

import android.support.annotation.NonNull;
import android.app.Activity;
import instamojo.library.InstapayListener;
import instamojo.library.InstamojoPay;
import instamojo.library.Config;
import org.json.JSONObject;
import org.json.JSONException;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ManageActivity extends AppCompatActivity {

    EditText vOption,vDay,vName,vPrice,vIsAvailable;

    View AddBtn;

    DatabaseReference menuRef;

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        vOption=findViewById(R.id.manage_Option);
        vDay=findViewById(R.id.manage_Day);
        vName=findViewById(R.id.manage_Name);
        vPrice=findViewById(R.id.manage_Price);
        vIsAvailable=findViewById(R.id.manage_isAvailable);

        AddBtn=findViewById(R.id.manage_Add);

        menuRef = FirebaseDatabase.getInstance().getReference().child("Menu").child("Mess");

        AddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String option,day,name,price,isAvailable;
                option = vOption.getText().toString();
                day=vDay.getText().toString();
                name=vName.getText().toString();
                price=vPrice.getText().toString();
                isAvailable=vIsAvailable.getText().toString();

                if(!(
                        TextUtils.isEmpty(option)&&
                        TextUtils.isEmpty(day)&&
                        TextUtils.isEmpty(name)&&
                        TextUtils.isEmpty(price)&&
                        TextUtils.isEmpty(isAvailable)
                    ))
                {
                    DatabaseReference ref = menuRef.child(option).child(day);
                    String uid = ref.push().getKey();

                    ref.child(uid).child("Name").setValue(name);
                    ref.child(uid).child("Price").setValue(price);
                    ref.child(uid).child("isAvailable").setValue(isAvailable)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(ManageActivity.this, "Item Added Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }
        });


    }
}

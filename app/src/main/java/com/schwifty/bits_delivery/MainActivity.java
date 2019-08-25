package com.schwifty.bits_delivery;

import android.content.Intent;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.floatingnavigationview.FloatingNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.schwifty.bits_delivery.UTILS.Loader;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static int CURR_VERSION = 12;

    private long transitionTime = 300;
    Animation anim;


    private View messA,messC,messD,fk,NC,FJ,INS;
    int option_selected=0;//0 = none selected, 1 = NC 2 = FJ
    private View mainContainer;
    private ProgressBar progressBar;

    private Toolbar mToolbar;

    TextView chooseOption;

    DatabaseReference variablesRef;

    String currTime2;
    int currTimeInt2;

    View TrackOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fadein);

        mainContainer = findViewById(R.id.main_OptionsContainer);
        mainContainer.setVisibility(View.GONE);

        progressBar = findViewById(R.id.main_Progress);

        variablesRef = FirebaseDatabase.getInstance().getReference().child("Variables").child("Status");

        mToolbar=findViewById(R.id.main_appbar);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setTitle("  BITS Food Delivery");

        /*Drawable dr = getResources().getDrawable(R.drawable.logo);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(),
                Bitmap.createScaledBitmap(bitmap, 75, 75, true));

        getSupportActionBar().setIcon(d);

*/

        chooseOption = findViewById(R.id.Chooseanoption);

        messA = (View) findViewById(R.id.main_option_a);
        messC = (View) findViewById(R.id.main_option_c);
        messD = (View) findViewById(R.id.main_option_d);
        fk = (View) findViewById(R.id.main_option_FK);
        NC = (View) findViewById(R.id.main_option_NC);
        FJ = (View) findViewById(R.id.main_option_FJ);
        INS = (View) findViewById(R.id.main_option_INS);

        TrackOrder = findViewById(R.id.main_trackButton);

        final Intent intent = new Intent(MainActivity.this,PlaceOrderActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);


        //to continuously update the time if app is open
        final Calendar cal = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("HH");
        currTime2 =sdf.format(cal.getTime());
        currTimeInt2 = Integer.parseInt(currTime2);

        final Handler handler = new Handler();
        final int delay = 1000; //milliseconds
        handler.postDelayed(new Runnable(){
            public void run(){

                currTime2 =sdf.format(cal.getTime());
                currTimeInt2 = Integer.parseInt(currTime2);

                Log.d("merchant",currTimeInt2+"");

                handler.postDelayed(this, delay);
            }
        }, delay);

        NavigationalView((FloatingNavigationView)findViewById(R.id.floating_navigation_view));

        TrackOrder.setOnClickListener(trackListner);

        final Loader l = new Loader("",R.layout.loading,this,false);
        l.getDialog().show();

        variablesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mainContainer.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                if(option_selected==0) {
                    chooseOption.setText("Choose an Option to Continue");

                    NC.setVisibility(View.VISIBLE);
                    FJ.setVisibility(View.VISIBLE);
                }
                else
                {
                    NC.setVisibility(View.GONE);
                    FJ.setVisibility(View.GONE);
                }


                if(Integer.parseInt(dataSnapshot.child("appVersion").getValue().toString())<=CURR_VERSION)
                {
                    NC.setOnClickListener(NC_listner);
                    FJ.setOnClickListener(FJ_listner);

                    // Mess
                    if (dataSnapshot.hasChild("MessA") && dataSnapshot.hasChild("isDevA"))
                    {
                        Log.d("hundred_4", dataSnapshot.child("MessA").getValue().toString());
                        Log.d("hundred_4", dataSnapshot.child("isDevA").getValue().toString());

                        if (dataSnapshot.child("MessA").getValue().toString().equals("opened")
                                && dataSnapshot.child("isDevA").getValue().toString().equals("true")) {
                            if (!(currTimeInt2 == 23 || currTimeInt2 == 0 || currTimeInt2 == 1)) {
                                messA.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                /*Animation anim = AnimationUtils.loadAnimation(MainActivity.this,R.anim.);
                                messA.startAnimation(anim);*/

                                        intent.putExtra("messOption", "A");
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                    }
                                });
                            } else {
                                messA.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Toast.makeText(MainActivity.this, "We only operate from 11pm to 2am", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }

                        } else {

                            messA.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(MainActivity.this, "Sorry, we are not serving for mess A now", Toast.LENGTH_LONG).show();

                                }
                            });

                        }


                        //C Mess
                        if (dataSnapshot.child("MessC").getValue().equals("opened")
                                && dataSnapshot.child("isDevA").getValue().toString().equals("true")) {
                            if ((currTimeInt2 == 23 || currTimeInt2 == 0 || currTimeInt2 == 1)) {
                                messC.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                  /*  Animation anim = AnimationUtils.loadAnimation(MainActivity.this,R.anim.zoomin);
                                   messC.startAnimation(anim);
                                   */
                                        intent.putExtra("messOption", "C");
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                    }
                                });
                            } else {
                                messC.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Toast.makeText(MainActivity.this, "We only operate from 11pm to 2am", Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }

                        } else {
                            messC.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Toast.makeText(MainActivity.this, "Sorry, we are not serving for mess C now", Toast.LENGTH_LONG).show();

                                }
                            });

                        }


                        //D Mess
                        if (dataSnapshot.child("MessD").getValue().equals("opened")
                                && dataSnapshot.child("isDevA").getValue().toString().equals("true"))
                        {
                            if ((currTimeInt2 == 23 || currTimeInt2 == 0 || currTimeInt2 == 1)) {
                                messD.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                  /*  Animation anim = AnimationUtils.loadAnimation(MainActivity.this,R.anim.zoomin);
                                   messD.startAnimation(anim);
                                   */
                                        intent.putExtra("messOption", "D");
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                    }
                                });
                            } else {
                                messD.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Toast.makeText(MainActivity.this, "We only operate from 11pm to 2am", Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }

                        } else
                        {
                            messD.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Toast.makeText(MainActivity.this, "Sorry, we are not serving for mess D now", Toast.LENGTH_LONG).show();

                                }
                            });

                        }


                        //Food King
                        if (dataSnapshot.child("FK").getValue().equals("opened")
                                && dataSnapshot.child("isDevA").getValue().toString().equals("true"))
                        {
                            //  if ((currTimeInt2 == 23 || currTimeInt2 == 0 || currTimeInt2 == 1))
                            {
                                fk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {


                                        intent.putExtra("messOption", "FK");
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                    }
                                });
                            }

                        } else
                        {
                            fk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Toast.makeText(MainActivity.this, "Sorry, Food King is not open yet", Toast.LENGTH_LONG).show();

                                }
                            });
                        }

                        //Ice n Spice
                        if (dataSnapshot.child("INS").getValue().equals("opened")
                                && dataSnapshot.child("isDevA").getValue().toString().equals("true"))
                        {
                            //  if ((currTimeInt2 == 23 || currTimeInt2 == 0 || currTimeInt2 == 1))
                            {
                                INS.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {


                                        intent.putExtra("messOption", "INS");
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                    }
                                });
                            }

                        } else
                        {
                            INS.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Toast.makeText(MainActivity.this, "Sorry, Ice n Spice is not open yet", Toast.LENGTH_LONG).show();

                                }
                            });

                        }


                        //End

                    }
                }
                else
                {
                    NC.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(MainActivity.this, "Please update your app to proceed", Toast.LENGTH_SHORT).show();
                        }
                    });
                    FJ.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(MainActivity.this, "Please update your app to proceed", Toast.LENGTH_SHORT).show();
                        }
                    });

                    messA.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(MainActivity.this, "Please update your app to proceed", Toast.LENGTH_SHORT).show();
                        }
                    });

                    messC.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(MainActivity.this, "Please update your app to proceed", Toast.LENGTH_SHORT).show();

                        }
                    });

                    messD.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(MainActivity.this, "Please update your app to proceed", Toast.LENGTH_SHORT).show();

                        }
                    });

                    fk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(MainActivity.this, "Please update your app to proceed", Toast.LENGTH_SHORT).show();

                        }
                    });
                    INS.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(MainActivity.this, "Please update your app to proceed", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

                l.getDialog().dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(MainActivity.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                l.getDialog().dismiss();
            }
        });





    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.main_menu_track)
        {
            Intent intent = new Intent(MainActivity.this,TrackItem.class);
            startActivity(intent);
        }

        return true;
    }
    */

 @Override
    public void onBackPressed()
    {
        if(option_selected==0)
        {
            super.onBackPressed();
        }
        else
        {
            option_selected=0;

            chooseOption.setText("");
            messC.setVisibility(View.GONE);
            messA.setVisibility(View.GONE);
            messD.setVisibility(View.GONE);
            fk.setVisibility(View.GONE);
            INS.setVisibility(View.GONE);

            messC.clearAnimation();
            messD.clearAnimation();
            messA.clearAnimation();
            fk.clearAnimation();
            INS.clearAnimation();

            Runnable r = new Runnable() {
                @Override
                public void run(){
                    chooseOption.setText("Choose an Option to Continue");

                    chooseOption.setAnimation(anim);
                    FJ.startAnimation(anim);
                    NC.startAnimation(anim);

                    FJ.setVisibility(View.VISIBLE);
                    NC.setVisibility(View.VISIBLE);
                }
            };

            Handler h = new Handler();
            h.postDelayed(r, transitionTime);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        messC.clearAnimation();
        messD.clearAnimation();
        messA.clearAnimation();
    }

    View.OnClickListener trackListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this,TrackOrder.class);
            startActivity(intent);
        }
    };

    View.OnClickListener NC_listner = new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            option_selected=1;

            chooseOption.setText("");
            FJ.setVisibility(View.GONE);
            NC.setVisibility(View.GONE);
            FJ.clearAnimation();
            NC.clearAnimation();

            fk.setVisibility(View.GONE);
            INS.setVisibility(View.GONE);

            Runnable r = new Runnable() {
                @Override
                public void run(){

                    chooseOption.setText("Choose a Mess to Order Food");

                    chooseOption.startAnimation(anim);
                    messC.startAnimation(anim);
                    messA.startAnimation(anim);
                    messD.startAnimation(anim);

                    messC.setVisibility(View.VISIBLE);
                    messA.setVisibility(View.VISIBLE);
                    messD.setVisibility(View.VISIBLE);

                }
            };

            Handler h = new Handler();
            h.postDelayed(r, transitionTime);



        }
    };

    View.OnClickListener FJ_listner = new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            option_selected=2;

            chooseOption.setText("");
            NC.setVisibility(View.GONE);
            FJ.setVisibility(View.GONE);
            FJ.clearAnimation();
            NC.clearAnimation();

            messC.setVisibility(View.GONE);
            messA.setVisibility(View.GONE);
            messD.setVisibility(View.GONE);


            Runnable r = new Runnable() {
                @Override
                public void run(){

                    chooseOption.setText("Choose a Food Joint to Order Food");

                    chooseOption.startAnimation(anim);
                    fk.startAnimation(anim);
                    INS.startAnimation(anim);

                    fk.setVisibility(View.VISIBLE);
                    INS.setVisibility(View.VISIBLE);

                }
            };

            Handler h = new Handler();
            h.postDelayed(r, transitionTime);

        }
    };


    private void NavigationalView(final FloatingNavigationView mFloatingNavigationView) {

        mFloatingNavigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mFloatingNavigationView!=null)mFloatingNavigationView.open();
            }
        });
        mFloatingNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem menuItem)
            {
                // Snackbar.make((View) mFloatingNavigationView.getParent(), menuItem.getTitle() + " Selected!", Snackbar.LENGTH_LONG).show();
                mFloatingNavigationView.close();

                String Option = menuItem.getTitle().toString();

                if(Option.equals("Track Order"))
                {
                    Intent intent = new Intent(MainActivity.this,TrackOrder.class);
                    startActivity(intent);
                }

                if(Option.equals("Skip Mess"))
                {
                    startActivity(new Intent(MainActivity.this,Login_Register.class));
                }

                if(Option.equals("About"))
                {

                }
                if(Option.equals("Contact"))
                {

                }


                return true;
            }

        });
    }


}

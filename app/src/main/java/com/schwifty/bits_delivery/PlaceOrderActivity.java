package com.schwifty.bits_delivery;

import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.WindowManager;

import com.schwifty.bits_delivery.UTILS.SectionPagerAdapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class PlaceOrderActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsPagerAdapter myTabsPagerAdapter;
    private View vCart;

    private SectionPagerAdapter mSectionPagerAdapter;

    public SelectedItemsList selListTest;
    public String MessOption;

    Boolean isFirstPageActive =true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        MessOption = getIntent().getStringExtra("messOption").toString();

        mToolbar = findViewById(R.id.po_appbar);
        setSupportActionBar(mToolbar);
       // getSupportActionBar().setTitle("  Food Delivery");
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*Drawable dr = getResources().getDrawable(R.drawable.logo);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(),
                Bitmap.createScaledBitmap(bitmap, 75, 75, true));

        getSupportActionBar().setIcon(d);
*/
        myViewPager = findViewById(R.id.po_tabs_pager);

        myTabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsPagerAdapter);
        myTabLayout = (TabLayout) findViewById(R.id.po_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
        myViewPager.setCurrentItem(0);

        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());

        vCart=findViewById(R.id.menu_cart);
        vCart.setOnClickListener(cartClicked);

        myViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if(i==0)
                {
                    isFirstPageActive=true;
                    MenuFragment menuFragment = (MenuFragment)
                    getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.po_tabs_pager + ":" + i);
                    vCart.setVisibility(VISIBLE);
                }
                else if(i==1) {
                    isFirstPageActive=false;
                    CartFragment cartFragment = (CartFragment)
                    getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.po_tabs_pager + ":" + i);

                    cartFragment.Update();
                    vCart.clearAnimation();
                    vCart.setVisibility(GONE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        selListTest = new SelectedItemsList();
    }

    @Override
    public void onBackPressed() {

        if(isFirstPageActive)
        {
            super.onBackPressed();
        }
        else
        {
            myViewPager.setCurrentItem(0);
        }


    }

    View.OnClickListener cartClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            myViewPager.setCurrentItem(1);
            vCart.clearAnimation();
            vCart.setVisibility(GONE);
        }
    };

}

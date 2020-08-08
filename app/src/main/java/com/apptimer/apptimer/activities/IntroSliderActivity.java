package com.apptimer.apptimer.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.Button;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apptimer.apptimer.Others.MyApplication;
import com.apptimer.apptimer.R;
import com.apptimer.apptimer.utils.DepthPageTransformer;

public class IntroSliderActivity extends AppCompatActivity {


        private LinearLayout Layout_bars;
        private TextView[] bottomBars;
        private int[] screens;
    private Button Next;
        private ViewPager vp;
        private MyViewPagerAdapter myvpAdapter;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Making notification bar transparent
            if (Build.VERSION.SDK_INT >= 17) {
                View decorView = getWindow().getDecorView();
                // Hide the status bar.
                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
                //// Remember that you should never show the action bar if the
                //// status bar is hidden, so hide that too if necessary.
                //                ActionBar actionBar = getActionBar();
                //                actionBar.hide();
            }

            setContentView(R.layout.activity_intro_slider);
            vp = findViewById(R.id.view_pager);
            Layout_bars = findViewById(R.id.layoutBars);
//            Skip = (Button) findViewById(R.id.skip);
            Next = findViewById(R.id.next);
            screens = new int[]{
                    R.layout.intro_screen1,
                    R.layout.intro_screen2,
                    R.layout.intro_screen3
            };
            myvpAdapter = new MyViewPagerAdapter();
            vp.setAdapter(myvpAdapter);
            vp.setPageTransformer(true,new DepthPageTransformer());

            vp.addOnPageChangeListener(viewPagerPageChangeListener);
            if (!MyApplication.getPref().isFirstTimeLaunch()) {
                launchMain();
                finish();
            }

            ColoredBars(0);
        }


        public void next(View v) {
            int i = getItem(+1);
            if (i < screens.length) {
                vp.setCurrentItem(i,true);
            } else {
                launchMain();
            }
        }

//        public void skip(View view) {
//            launchMain();
//        }

        private void ColoredBars(int thisScreen) {
            int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);
            int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
            bottomBars = new TextView[screens.length];

            Layout_bars.removeAllViews();
            for (int i = 0; i < bottomBars.length; i++) {
                bottomBars[i] = new TextView(this);
                bottomBars[i].setTextSize(70);
                bottomBars[i].setText(Html.fromHtml("¯"));
                Layout_bars.addView(bottomBars[i]);
                bottomBars[i].setTextColor(colorsInactive[thisScreen]);
            }
            if (bottomBars.length > 0)
                bottomBars[thisScreen].setTextColor(colorsActive[thisScreen]);
        }

        private int getItem(int i) {
            return vp.getCurrentItem() + i;
        }

        private void launchMain() {
            MyApplication.getPref().setFirstTimeLaunch(false);
            startActivity(new Intent(IntroSliderActivity.this, MainActivity.class));
            finish();
        }

        private ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                ColoredBars(position);
                if (position == screens.length - 1) {
                    Next.setText(R.string.start);
//                    Skip.setVisibility(View.GONE);
                } else {
                    Next.setText(getString(R.string.next));
//                    Skip.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        };

        class MyViewPagerAdapter extends PagerAdapter {
            private LayoutInflater inflater;

            private MyViewPagerAdapter() {
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(screens[position], container, false);
                container.addView(view);
                return view;
            }

            @Override
            public int getCount() {
                return screens.length;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                View v = (View) object;
                container.removeView(v);
            }

            @Override
            public boolean isViewFromObject(View v, Object object) {
                return v == object;
            }
        }




    }
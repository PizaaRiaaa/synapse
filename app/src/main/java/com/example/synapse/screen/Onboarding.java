package com.example.synapse.screen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.synapse.R;
import com.example.synapse.screen.util.adapter.SliderAdapter;

public class Onboarding extends AppCompatActivity {

    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;
    private TextView[] mDots;
    private SliderAdapter sliderAdapter;

    private Button mFinishBtn;
    private int mCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding);

        mSlideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.dotsLayout);
        mFinishBtn = (Button) findViewById(R.id.finishBtn);

        sliderAdapter = new SliderAdapter(this);
        mSlideViewPager.setAdapter(sliderAdapter);
        addDotsIndicator(0);

        mSlideViewPager.addOnPageChangeListener(viewListener);

        // proceed to login screen
        mFinishBtn.setOnClickListener(view -> {
            startActivity(new Intent(Onboarding.this, Login.class));
            finish();
        });

    }

    // display three dots navigation
    public void addDotsIndicator(int position) {
        mDots = new TextView[3];
        mDotLayout.removeAllViews();

        for (int i = 0; i < mDots.length; i++) {

            mDots[i] = new TextView(this);
            mDots[i].setText(HtmlCompat.fromHtml("&#8226;", HtmlCompat.FROM_HTML_MODE_LEGACY));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(ContextCompat.getColor(this, R.color.grey3));

            mDotLayout.addView(mDots[i]);
        }

        // display dark grey indicator for current screen
        if (mDots.length > 0) {
            mDots[position].setTextColor(ContextCompat.getColor(this, R.color.grey5));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {

            addDotsIndicator(i);
            mCurrentPage = i;

            if(i == 0){
                mFinishBtn.setEnabled(false);
                mFinishBtn.setVisibility(View.INVISIBLE);
            }else if(i == mDots.length - 1){
                mFinishBtn.setEnabled(true);
                mFinishBtn.setVisibility(View.VISIBLE);
            }else{
                mFinishBtn.setEnabled(false);
                mFinishBtn.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };
}
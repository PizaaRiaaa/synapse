package com.example.synapse.screen.util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.synapse.R;


// ONBOARDING SLIDER
public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context){
        this.context = context;
    }

    // IMAGES
    public int[] slide_images = {
            R.drawable.ic_logo_with_text,
            R.drawable.ic_onboarding_2,
            R.drawable.ic_onboarding_3
    };

    // HEADINGS
    public String[] slide_headings = {
            "Synapse",
            "Do Notify",
            "Get Notifs"
    };

    // DESCRIPTIONS
    public String[] slide_descriptions = {
            "It promotes good quality of life, memory stimulation, and a healthy life style for senior citizens.",
            "For carers - send reminder notifications like medication, appointments, physical activity, and games. It also provides tracking senior's health, and location.",
            "For seniors - receive reminder notifications for memory support, send your location, play games for memory stimulation, and have access to emergency hotline."
    };

    @Override
    public int getCount(){
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object o){
        return view == (LinearLayout) o;
    }


    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position){
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // HOOKS
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_image);
        TextView slideHeading = (TextView) view.findViewById(R.id.slide_heading);
        TextView slideDescription = (TextView) view.findViewById(R.id.slide_desc);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDescription.setText((slide_descriptions[position]));

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        container.removeView((LinearLayout)object);
    }
}








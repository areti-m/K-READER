package com.KReader.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

public class CustomView extends FrameLayout {

    TextView textView;
    LinearLayout linierLayout;

    static final String[] color = new String[] {"#D11DB0", "#F800CB","#A734FF","#00B8EB","#4EB9D6","#2A9F00","#FF6D12",};

    @SuppressLint("ResourceType")
    public CustomView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.category_select_item, this);
        textView = (TextView)getRootView().findViewById(R.id.item_text);
        linierLayout = (LinearLayout)getRootView().findViewById(R.id.bg_container);
    }

    public void display(String text, boolean isSelected) {
        textView.setText(text);
        display(isSelected);
    }

    public void display(boolean isSelected) {
       // linierLayout.setBackgroundColor(isSelected? Color.RED : Color.LTGRAY);

       String cl= color[new Random().nextInt(color.length)];

        if(isSelected){

            linierLayout.setBackgroundResource(R.drawable.row_layout_bg_selected);
            textView.setTextColor(Color.WHITE);

            GradientDrawable drawable = (GradientDrawable) linierLayout.getBackground();
            drawable.setColor(Color.parseColor(cl));

        }else{
            linierLayout.setBackgroundResource(R.drawable.row_layout_bg);
            //textView.setTextColor(Color.BLACK);
        }


    }
}

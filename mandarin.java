package com.example.mandaringame;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class mandarin extends LinearLayout {
    public int number;
    private ImageView imageView;

    public mandarin(Context context, int num) {
        super(context);
        this.number = num;

        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        setPadding(10, 10, 10, 10);
        setBackgroundColor(Color.TRANSPARENT);
        setLayoutParams(new ViewGroup.LayoutParams(200, 200));

        imageView = new ImageView(context);
        imageView.setImageResource(getImageResIdForNumber(num));
        imageView.setLayoutParams(new LayoutParams(120, 120));
        addView(imageView);
    }

    private int getImageResIdForNumber(int number) {
        switch (number) {
            case 1: return R.drawable.mandarin1;
            case 2: return R.drawable.mandarin2;
            case 3: return R.drawable.mandarin3;
            case 4: return R.drawable.mandarin4;
            case 5: return R.drawable.mandarin5;
            case 6: return R.drawable.mandarin6;
            case 7: return R.drawable.mandarin7;
            case 8: return R.drawable.mandarin8;
            case 9: return R.drawable.mandarin9;
            default: return R.drawable.mandarin;
        }
    }
}
package com.example.mandaringame_test;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class mandarin extends LinearLayout {
    public int number;
    public boolean isHanrabong = false;
    public boolean isDolhareubang = false;

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
        imageView.setLayoutParams(new LayoutParams(120, 120));
        imageView.setImageResource(getMandarinResId(num));
        addView(imageView);
    }

    private int getMandarinResId(int num) {
        switch (num) {
            case 0: return R.drawable.dolhareubang;
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

    private int getHanrabongResId(int num) {
        switch (num) {
            case 1: return R.drawable.hanrabong1;
            case 2: return R.drawable.hanrabong2;
            case 3: return R.drawable.hanrabong3;
            case 4: return R.drawable.hanrabong4;
            case 5: return R.drawable.hanrabong5;
            case 6: return R.drawable.hanrabong6;
            case 7: return R.drawable.hanrabong7;
            case 8: return R.drawable.hanrabong8;
            case 9: return R.drawable.hanrabong9;
            default: return R.drawable.hanrabong;
        }
    }

    public void markAsHanrabong() {
        isHanrabong = true;
        setBackgroundColor(Color.parseColor("#89B45B"));
        imageView.setImageResource(getHanrabongResId(number));
    }

    public void markAsDolhareubang() {
        isDolhareubang = true;
        setBackgroundColor(Color.GRAY);
        imageView.setImageResource(R.drawable.dolhareubang);
    }
}

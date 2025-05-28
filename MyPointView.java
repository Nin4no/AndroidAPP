package com.example.mandaringame_test;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;

public class MyPointView extends View {
    private Paint paint;
    private int startX = -1, startY = -1, stopX = -1, stopY = -1;
    private GridLayout gameField;
    private List<Pair<mandarin, View>> mandarinViews;

    private MainActivity mainActivity;

    public MyPointView(Context context, GridLayout gameField, List<Pair<mandarin, View>> mandarinViews) {
        super(context);
        this.gameField = gameField;
        this.mandarinViews = mandarinViews;
        this.mainActivity = (MainActivity) context;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!MainActivity.Gaming) return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getX();
                startY = (int) event.getY();
                stopX = stopY = -1;
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                stopX = (int) event.getX();
                stopY = (int) event.getY();
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                stopX = (int) event.getX();
                stopY = (int) event.getY();
                if (startX != -1 && stopX != -1) {
                    Rect dragRect = new Rect(
                            Math.min(startX, stopX), Math.min(startY, stopY),
                            Math.max(startX, stopX), Math.max(startY, stopY)
                    );
                    int sum = 0;
                    List<Integer> toRemoveIndices = new ArrayList<>();
                    int hanrabongRemovedCount = 0; // 한라봉이 제거된 개수를 세는 변수
                    for (int i = 0; i < mandarinViews.size(); i++) { //
                        Pair<mandarin, View> pair = mandarinViews.get(i); //
                        View v = pair.second; //
                        Rect mRect = new Rect( //
                                (int) v.getX(), (int) v.getY(), //
                                (int) (v.getX() + v.getWidth()), //
                                (int) (v.getY() + v.getHeight()) //
                        );
                        if (Rect.intersects(dragRect, mRect)) { //
                            sum += pair.first.number; //
                            toRemoveIndices.add(i); //
                            if (pair.first.isHanrabong) { // 한라봉이면 카운트 증가
                                hanrabongRemovedCount++; //
                            }
                        }
                    }
                    if (sum == 10) { //
                        toRemoveIndices.sort((a, b) -> b - a); //
                        for (int index : toRemoveIndices) { //
                            gameField.removeView(mandarinViews.get(index).second); //
                            mandarinViews.remove(index); //
                        }
                        int scoreToAdd = toRemoveIndices.size() + (hanrabongRemovedCount * 5); // 한라봉 개수만큼 추가 점수
                        mainActivity.updateScore(scoreToAdd); //
                    }
                }
                startX = startY = stopX = stopY = -1;
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (startX != -1 && stopX != -1) { //
            canvas.drawRect( //
                    Math.min(startX, stopX), Math.min(startY, stopY), //
                    Math.max(startX, stopX), Math.max(startY, stopY), //
                    paint //
            );
        }
    }

    public void updateMandarinViews(List<Pair<mandarin, View>> newMandarinViews) {
        this.mandarinViews = newMandarinViews; //
    }
}

package com.teensgeeker.funsports.model;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;

public class BallOnTouchListener implements View.OnTouchListener {
    private float originalX;
    private float originalY;

    private ImageView basket;

    private float latestXSpeed = 0;
    private float latestYSpeed = 0;
    private VelocityTracker mVelocityTracker;

    public Handler handler;
    public TextView scoreTextView;

    FlingAnimation flingAnimX;
    FlingAnimation flingAnimY;
    public boolean isAnimationPlaying = false;

    float deltaX = 0;
    float deltaY = 0;

    private String who;

    public BallOnTouchListener(Handler handler,TextView textView, ImageView basket,String who){
        this.handler = handler;
        this.scoreTextView = textView;
        this.basket = basket;
        this.who = who;
    }

    public void endThrowing(View v){
        float vLeft = v.getX();
        float vRight = v.getX()+v.getWidth();
        float vTop = v.getY();
        float vBottom = v.getY()+v.getHeight();

        LinearLayout basketParent = (LinearLayout) basket.getParent();

        float w = basket.getMeasuredWidth();

        float basketLeft = basketParent.getLeft() + basket.getLeft() + basket.getMeasuredWidth()*((float)((250.0-128.0)/2.0/250.0));


        float basketRight = basketParent.getRight() - basket.getMeasuredWidth()*((float)((250.0-128.0)/2.0/250.0));
        float basketTop = 0;
        float basketBottom = 0;
        if(who.equals("blue")) {
            basketTop = basketParent.getTop();
            basketBottom = basketParent.getBottom() - basket.getBottom() - basket.getMeasuredHeight() * ((float) (13.3 / 250.0));
        }else{
            basketTop = basketParent.getTop() + basket.getTop() + basket.getMeasuredHeight() * ((float) (13.3 / 250.0));
            basketBottom = basketParent.getBottom();
        }

        if(vLeft>basketLeft && vRight<basketRight && vTop>basketTop && vBottom<basketBottom) {
            scoreTextView.setText(String.valueOf(Integer.valueOf(scoreTextView.getText().toString())+1));
            System.out.println("中！");
        }

        v.setX(originalX);
        v.setY(originalY);
        isAnimationPlaying = false;
    }

    @Override
    public boolean onTouch(final View v, MotionEvent event) {
        final int action = event.getAction() & MotionEvent.ACTION_MASK;
        mVelocityTracker = VelocityTracker.obtain();
        mVelocityTracker.addMovement(event);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) v.getLayoutParams();

        if(!isAnimationPlaying) {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    originalX = v.getX();
                    originalY = v.getY();
                    deltaX = event.getRawX() - v.getX();
                    deltaY = event.getRawY() - v.getY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    mVelocityTracker.computeCurrentVelocity(1000);
                    latestXSpeed = mVelocityTracker.getXVelocity();
                    latestYSpeed = mVelocityTracker.getYVelocity();

                    v.setX(event.getRawX()-deltaX);
                    v.setY(event.getRawY()-deltaY);
                    break;

                case MotionEvent.ACTION_UP:
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            flingAnimX = new FlingAnimation(v, DynamicAnimation.TRANSLATION_X);
                            flingAnimY = new FlingAnimation(v, DynamicAnimation.TRANSLATION_Y);
                            flingAnimX.setStartVelocity(latestXSpeed).start();
                            flingAnimY.setStartVelocity(latestYSpeed).start();
                            isAnimationPlaying = true;
                            flingAnimX.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                                @Override
                                public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                                    if (!flingAnimY.isRunning()) {
                                        endThrowing(v);
                                    }
                                }
                            });
                            flingAnimY.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                                @Override
                                public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                                    if (!flingAnimX.isRunning()) {
                                        endThrowing(v);
                                    }
                                }
                            });
                        }
                    });
                    break;
                case MotionEvent.ACTION_CANCEL:
                    mVelocityTracker.recycle();
                    break;
            }
        }
        return true;
    }
}

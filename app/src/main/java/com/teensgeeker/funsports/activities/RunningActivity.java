package com.teensgeeker.funsports.activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.teensgeeker.funsports.R;
import com.teensgeeker.funsports.databinding.ActivityRunningBinding;

public class RunningActivity extends AppCompatActivity {

    private ActivityRunningBinding binding;


    private ImageView destinationLine;

    private ImageView yellow;
    private ImageView blue;

    private float destination;

    private ObjectAnimator yellowAnimation;
    private ObjectAnimator blueAnimation;

    private boolean yellowLeftStep = false;
    private boolean yellowRightStep = false;
    private boolean blueLeftStep = false;
    private boolean blueRightStep = false;

    private float yellowTranslationY = -10;
    private float blueTranslationY = -10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRunningBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        yellow = binding.yellowImage;
        blue = binding.blueImage;

        destinationLine = binding.destinationLine;
        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        destinationLine.measure(w,h);

        view.post(new Runnable() {
            @Override
            public void run() {
                destination = destinationLine.getBottom() - (float)(destinationLine.getMeasuredHeight()/2.0);
                System.out.println(destination);
            }
        });

    }

    public void yellowStep(){
        yellowAnimation = ObjectAnimator.ofFloat(yellow,"translationY",yellowTranslationY,yellowTranslationY-10);
        yellowAnimation.setDuration(300);
        yellowAnimation.start();
        yellowAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(yellow.getTop()+yellowTranslationY <= destination){
                    gameOver("yellow");
                } else {
                    yellowTranslationY -= 10;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        yellowLeftStep = false;
        yellowRightStep = false;
    }

    public void onClickYellowLeft(View view){
        if(!yellowLeftStep && !yellowRightStep) {
            yellowLeftStep = true;
        } else if(yellowRightStep){
            yellowStep();
        }
    }

    public void onClickYellowRight(View view){
        if(!yellowRightStep && !yellowLeftStep) {
            yellowRightStep = true;
        } else if(yellowLeftStep){
            yellowStep();
        }
    }

    public void gameOver(String winner){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.game_over_dialog);
        TextView playerTextView = (TextView) dialog.findViewById(R.id.playerTextView);
        if(winner.equals("yellow")){
            playerTextView.setText("黄方");
            playerTextView.setTextColor(Color.parseColor("#FCCE0D"));
        } else if (winner.equals("blue")){
            playerTextView.setText("蓝方");
            playerTextView.setTextColor(Color.parseColor("#00FFFF"));
        }

        Button exitBtn = (Button) dialog.findViewById(R.id.exit_btn);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RunningActivity.super.onBackPressed();
            }
        });
        Button restartBtn = (Button) dialog.findViewById(R.id.restart_btn);
        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
        dialog.show();
    }

    public void blueStep(){
        blueAnimation = ObjectAnimator.ofFloat(blue,"translationY",blueTranslationY,blueTranslationY-10);
        blueAnimation.setDuration(300);
        blueAnimation.start();
        blueAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(blue.getY()+blueTranslationY <= destination){
                    gameOver("blue");
                } else {
                    blueTranslationY -= 10;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        blueLeftStep = false;
        blueRightStep = false;
    }

    public void onClickBlueLeft(View view){
        if(!blueLeftStep && !blueRightStep) {
            blueLeftStep = true;
        } else if(blueRightStep){
            blueStep();
        }
    }

    public void onClickBlueRight(View view){
        if(!blueRightStep && !blueLeftStep) {
            blueRightStep = true;
        } else if(blueLeftStep){
            blueStep();
        }
    }

    public void showMenu(View view){
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.menu_dialog);
        Button exitBtn = (Button) dialog.findViewById(R.id.exit_btn);
        Switch switchSound = (Switch) dialog.findViewById(R.id.switch_sound);
        switchSound.setVisibility(View.GONE);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RunningActivity.super.onBackPressed();
            }
        });
        dialog.show();
    }

}
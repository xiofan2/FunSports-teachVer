package com.teensgeeker.funsports.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.teensgeeker.funsports.R;
import com.teensgeeker.funsports.model.BallOnTouchListener;
import com.teensgeeker.funsports.databinding.ActivityBasketBallBinding;

import java.util.Timer;
import java.util.TimerTask;

import android.widget.TextView;

public class BasketBallActivity extends AppCompatActivity {
    private ImageView redBall;
    private ImageView blueBall;

    private ImageView redGuide;
    private ImageView blueGuide;

    private ImageView redBasket;
    private ImageView blueBasket;

    private ImageButton redMore;
    private ImageButton blueMore;

    private ActivityBasketBallBinding binding;

    private TextView redScore;
    private TextView blueScore;

    private VelocityTracker mVelocityTracker;

    private Handler handler;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBasketBallBinding.inflate(getLayoutInflater());
        final View view = binding.getRoot();
        setContentView(view);

        redBall = binding.ballRed;
        blueBall = binding.ballBlue;

        redGuide = binding.redGuide;
        blueGuide = binding.blueGuide;

        redScore = binding.redScoreTextBall;
        blueScore = binding.blueScoreTextBall;

        redBasket = binding.redBasket;
        blueBasket = binding.blueBasket;

        redMore = binding.basketballMenuMoreRed;
        blueMore = binding.basketballMenuMoreBlue;

        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);

        redBasket.measure(w,h);
        blueBasket.measure(w,h);

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        redGuide.setVisibility(View.GONE);
                        blueGuide.setVisibility(View.GONE);
                        redBall.setVisibility(View.VISIBLE);
                        blueBall.setVisibility(View.VISIBLE);
                    }
                });
            }
        };
        timer.schedule(task,1000);

        handler = new Handler();

        redBall.setOnTouchListener(new BallOnTouchListener(handler,redScore,redBasket,"red"));
        blueBall.setOnTouchListener(new BallOnTouchListener(handler,blueScore,blueBasket,"blue"));
    }

    @Override
    public void onBackPressed(){
        onClickMenu(redMore);
    }

    public void onClickMenu(View view){
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.menu_dialog);
        Button exitBtn = (Button) dialog.findViewById(R.id.exit_btn);
        Switch switchSound = (Switch) dialog.findViewById(R.id.switch_sound);
        switchSound.setVisibility(View.GONE);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BasketBallActivity.super.onBackPressed();
            }
        });
        dialog.show();
    }
}
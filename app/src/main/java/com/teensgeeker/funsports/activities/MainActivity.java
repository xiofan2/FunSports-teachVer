package com.teensgeeker.funsports.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.teensgeeker.funsports.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private LinearLayout sumo;
    private LinearLayout basketball;
    private LinearLayout running;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置界面绑定
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //绑定界面与按钮
        sumo = binding.sumoBtn;
        basketball = binding.basketballBtn;
        running = binding.runningBtn;
    }

    //点击方法：跳转到相扑界面
    public void onClickSumoBtn(View view){
        Intent intent = new Intent(MainActivity.this,SumoActivity.class);
        startActivity(intent);
    }

    public void onClickBasketBall(View view){
        Intent intent = new Intent(MainActivity.this,BasketBallActivity.class);
        startActivity(intent);
    }

    public void onClickRunning(View view){
        Intent intent = new Intent(MainActivity.this,RunningActivity.class);
        startActivity(intent);
    }

}
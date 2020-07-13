package com.teensgeeker.funsports.activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teensgeeker.funsports.R;
import com.teensgeeker.funsports.databinding.ActivitySumoBinding;
import com.teensgeeker.funsports.model.Question;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SumoActivity extends AppCompatActivity {

    public static final int ANSWER_RIGHT = 5;
    public static final int ANSWER_WRONG = 6;
    public static final int ANSWER_LATE = 7;

    public static final int RED = 11;
    public static final int BLUE = 12;
    public static final int NOONE = 13;

    public Button redReadyBtn;
    public Button blueReadyBtn;

    private ImageButton more;

    public ImageView redReadyImageView;
    public ImageView blueReadyImageView;

    private Boolean redOK = false;
    private Boolean blueOK = false;

    public Boolean isPlaySound = true;

    private ActivitySumoBinding binding;

    public Timer timer;
    public MediaPlayer mediaPlayer;

    private ConstraintLayout redAnswerLayout;
    private ConstraintLayout blueAnswerLayout;

    private LinearLayout redCardLayout;
    private LinearLayout blueCardLayout;

    private LinearLayout redChoiceLayout;
    private LinearLayout blueChoiceLayout;

    private TextView redScoreTextView;
    private TextView blueScoreTextView;

    private TextView redQuestion;
    private TextView blueQuestion;

    private TextView redChoiceATextView;
    private TextView redChoiceBTextView;
    private TextView redChoiceCTextView;
    private TextView redChoiceDTextView;

    private TextView blueChoiceATextView;
    private TextView blueChoiceBTextView;
    private TextView blueChoiceCTextView;
    private TextView blueChoiceDTextView;

    private TextView redResult;
    private TextView blueResult;

    private ArrayList<Question> questions;
    private int questionNow = 0;
    private int winner = NOONE;
    private int redRightNumber = 0;
    private int blueRightNumber = 0;

    private ObjectAnimator animationRed;
    private ObjectAnimator animationBlue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //将布局文件中的控件与控件对象绑定
        binding = ActivitySumoBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        //准备按钮
        redReadyBtn = binding.readyRed;
        blueReadyBtn = binding.readyBlue;

        //角色图片
        redReadyImageView = binding.redFighter;
        blueReadyImageView = binding.blueFighter;

        //答题框外框布局
        redAnswerLayout = binding.redAnswerLayout;
        blueAnswerLayout = binding.blueAnswerLayout;

        //答题框卡片布局
        redCardLayout = binding.redCardLayout;
        blueCardLayout = binding.blueCardLayout;

        //选项框布局
        redChoiceLayout = binding.redChoiceLayout;
        blueChoiceLayout = binding.blueChoiceLayout;

        //分数牌
        redScoreTextView = binding.redScoreText;
        blueScoreTextView = binding.blueScoreText;

        //问题
        redQuestion=binding.redQuestion;
        blueQuestion=binding.blueQuestion;

        //选项
        redChoiceATextView = binding.redChoiceAtext;
        redChoiceBTextView = binding.redChoiceBtext;
        redChoiceCTextView = binding.redChoiceCtext;
        redChoiceDTextView = binding.redChoiceDtext;

        blueChoiceATextView = binding.blueChoiceAtext;
        blueChoiceBTextView = binding.blueChoiceBtext;
        blueChoiceCTextView = binding.blueChoiceCtext;
        blueChoiceDTextView = binding.blueChoiceDtext;

        //菜单（红方）
        more = binding.moreRed;

        //初始化播放器
        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(this, R.raw.washing_hands);
    }

    public void preparing(){
        //准备中
        //设置准备中文字，洗手图片
        redReadyBtn.setText("Preparing...");
        redReadyBtn.setClickable(false); //不可点击，防止多次执行该方法导致崩溃
        redReadyImageView.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.washing_hands));

        blueReadyBtn.setText("Preparing...");
        blueReadyBtn.setClickable(false);
        blueReadyImageView.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.washing_hands));

        blueReadyImageView.setRotation(180);

        //播放声音
        if(isPlaySound) {
            mediaPlayer.start();
        }

        //设置播放时长
        timer = new Timer();
        TimerTask task = new TimerTask(){
            @Override
            public void run(){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(isPlaySound) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                        }
                        prepared();
                    }
                });
            }
        };
        timer.schedule(task,4000); //开始倒计时并执行task中设定的任务
    }


    public void prepared(){
        //准备完成，设置文字和图案
        redReadyBtn.setText("OK!");
        redReadyImageView.setImageDrawable(ContextCompat.getDrawable(SumoActivity.this,R.drawable.preparing_ok));

        blueReadyBtn.setText("OK!");
        blueReadyImageView.setImageDrawable(ContextCompat.getDrawable(SumoActivity.this,R.drawable.preparing_ok));

        //设置准备时常，结束后弹出答题卡
        TimerTask task = new TimerTask(){
            @Override
            public void run(){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ViewGroup layout = (ViewGroup) redReadyBtn.getParent();
                        if(layout!=null){
                            layout.removeView(redReadyBtn);
                            layout.removeView(blueReadyBtn);
                        }

                        //红方答题卡弹出动画
                        animationRed = ObjectAnimator.ofFloat(redAnswerLayout,"translationY",170,30);
                        animationRed.setDuration(500);
                        animationRed.start();
                        redCardLayout.setVisibility(View.VISIBLE); //将布局文件中的默认设定隐藏改为显示

                        //蓝方答题卡弹出动画
                        animationBlue = ObjectAnimator.ofFloat(blueAnswerLayout,"translationY",-170,-30);
                        animationBlue.setDuration(500);
                        animationBlue.start();
                        blueCardLayout.setVisibility(View.VISIBLE);

                        //相扑选手比赛图案
                        redReadyImageView.setImageDrawable(ContextCompat.getDrawable(SumoActivity.this,R.drawable.sumo_fighter_red));
                        blueReadyImageView.setImageDrawable(ContextCompat.getDrawable(SumoActivity.this,R.drawable.sumo_fighter_blue));
                        //因为图案方向原因，此前颠倒180度的图片需转正
                        blueReadyImageView.setRotation(0);

                        redScoreTextView.setVisibility(View.VISIBLE);
                        blueScoreTextView.setVisibility(View.VISIBLE);
                    }
                });
            }
        };
        timer.schedule(task,1000);
        startGame();
    }

    public String inputStreamToString(InputStream inputStream) {
        //通过InputStream读入文件并返回字符串
        try {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, bytes.length);
            String json = new String(bytes);
            return json;
        } catch (IOException e) {
            return null;
        }
    }

    public void readQuestions(){
        String myJson = inputStreamToString(this.getResources().openRawResource(R.raw.questions)); //从res中读入json，如在网络编程中可改为通过Uri读入json
        questions = new Gson().fromJson(myJson,new TypeToken<ArrayList<Question>>(){}.getType()); //将json字符串解析为Question类对象
    }

    public void startGame(){
        readQuestions();

        redQuestion.setText(questions.get(0).question);
        redChoiceATextView.setText(questions.get(0).choices.get(0));
        redChoiceBTextView.setText(questions.get(0).choices.get(1));
        redChoiceCTextView.setText(questions.get(0).choices.get(2));
        redChoiceDTextView.setText(questions.get(0).choices.get(3));

        blueQuestion.setText(questions.get(0).question);
        blueChoiceATextView.setText(questions.get(0).choices.get(0));
        blueChoiceBTextView.setText(questions.get(0).choices.get(1));
        blueChoiceCTextView.setText(questions.get(0).choices.get(2));
        blueChoiceDTextView.setText(questions.get(0).choices.get(3));
    }

    public void gameOver(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.game_over_dialog);
        TextView playerTextView = (TextView) dialog.findViewById(R.id.playerTextView);
        if(redRightNumber>blueRightNumber){
            playerTextView.setText("红方");
            playerTextView.setTextColor(Color.parseColor("#C95252"));
        } else if (redRightNumber<blueRightNumber){
            playerTextView.setText("蓝方");
            playerTextView.setTextColor(Color.parseColor("#0055BA"));
        } else {
            TextView status = (TextView) dialog.findViewById(R.id.status_textview);
            playerTextView.setText("");
            status.setText("平局");
        }
        Button exitBtn = (Button) dialog.findViewById(R.id.exit_btn);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SumoActivity.super.onBackPressed();
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

    @Override
    public void onBackPressed(){
        showMenu(more);
    }

    public void onClickRedReadyBtn(View view){
        redOK = true;
        redReadyBtn.setBackgroundColor(Color.TRANSPARENT);
        redReadyBtn.setTextColor(getColor(R.color.colorPrimary));
        redReadyBtn.setText("Waiting...");
        if(blueOK){
            redReadyBtn.setBackgroundColor(Color.TRANSPARENT);
            redReadyBtn.setTextColor(getColor(R.color.colorPrimary));
            preparing();
        }
    }

    public void onClickBlueReadyBtn(View v){
        blueOK = true;
        blueReadyBtn.setBackgroundColor(Color.TRANSPARENT);
        blueReadyBtn.setTextColor(getColor(R.color.colorPrimary));
        blueReadyBtn.setText("Waiting...");
        if(redOK){
            blueReadyBtn.setBackgroundColor(Color.TRANSPARENT);
            blueReadyBtn.setTextColor(getColor(R.color.colorPrimary));
            preparing();
        }
    }

    public TextView createNoticeTextView(String choice,int result) {
        TextView noticeTextView = new TextView(this);
        String notice= choice + " ";
        switch (result) {
            case ANSWER_RIGHT:
                notice += "你答对了！";
                break;
            case ANSWER_WRONG:
                notice += "你答错了！";
                break;
            case ANSWER_LATE:
                notice += "你答慢了！";
                break;
            default:
                notice += "数据异常。";
        }
        noticeTextView.setText(notice);
        noticeTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        noticeTextView.setTextSize(20);
        noticeTextView.setTextColor(ContextCompat.getColor(this,R.color.color_light));
        noticeTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        return noticeTextView;
    }

    public void clearNotice(){
        redCardLayout.removeView(redResult);
        blueCardLayout.removeView(blueResult);
    }

    public void pushOut(int loser){
        if(loser==BLUE){
            redReadyImageView.setImageDrawable(getDrawable(R.drawable.sumo_fighter_fighting_red));
            blueReadyImageView.setImageDrawable(getDrawable(R.drawable.sumo_fighter_fighting_blue));
            animationRed = ObjectAnimator.ofFloat(redReadyImageView,"translationY",0,-100,0);
            animationRed.setDuration(1000);
            animationBlue = ObjectAnimator.ofFloat(blueReadyImageView,"translationY",0,-500,0);
            animationBlue.setDuration(2000);
            animationRed.start();
            animationBlue.start();
        } else if(loser==RED){
            redReadyImageView.setImageDrawable(getDrawable(R.drawable.sumo_fighter_fighting_red));
            blueReadyImageView.setImageDrawable(getDrawable(R.drawable.sumo_fighter_fighting_blue));
            animationBlue = ObjectAnimator.ofFloat(blueReadyImageView,"translationY",0,100,0);
            animationBlue.setDuration(1000);
            animationRed = ObjectAnimator.ofFloat(redReadyImageView,"translationY",0,500,0);
            animationRed.setDuration(2000);
            animationRed.start();
            animationBlue.start();
        }
    }

    public void loadNextQuestion(){
        clearNotice();
        questionNow += 1;
        if(questionNow < questions.size()) {
            redReadyImageView.setImageDrawable(getDrawable(R.drawable.sumo_fighter_red));
            redQuestion.setText(questions.get(questionNow).question);
            redChoiceATextView.setText(questions.get(questionNow).choices.get(0));
            redChoiceBTextView.setText(questions.get(questionNow).choices.get(1));
            redChoiceCTextView.setText(questions.get(questionNow).choices.get(2));
            redChoiceDTextView.setText(questions.get(questionNow).choices.get(3));

            blueReadyImageView.setImageDrawable(getDrawable(R.drawable.sumo_fighter_blue));
            blueQuestion.setText(questions.get(questionNow).question);
            blueChoiceATextView.setText(questions.get(questionNow).choices.get(0));
            blueChoiceBTextView.setText(questions.get(questionNow).choices.get(1));
            blueChoiceCTextView.setText(questions.get(questionNow).choices.get(2));
            blueChoiceDTextView.setText(questions.get(questionNow).choices.get(3));

            redChoiceLayout.setVisibility(View.VISIBLE);
            blueChoiceLayout.setVisibility(View.VISIBLE);
        } else {
            gameOver();
        }
    }

    public void runAfterAnimationPlayed(){
        animationRed.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(!animationBlue.isRunning()) {
                    loadNextQuestion();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animationBlue.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(!animationRed.isRunning()) {
                    loadNextQuestion();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void onClickRedChoice(View v){
        redChoiceLayout.setVisibility(View.GONE);
        blueChoiceLayout.setVisibility(View.GONE);

        LinearLayout thisLayout = findViewById(v.getId());
        TextView childTextView = (TextView) thisLayout.getChildAt(0);
        String answer = childTextView.getText().toString();
        if(questions.get(questionNow).answer.equals(answer)){
            if(winner==NOONE){
                redResult = createNoticeTextView(answer, ANSWER_RIGHT);
                redCardLayout.addView(redResult);

                winner = RED;
                redRightNumber++;
                redScoreTextView.setText(String.valueOf(redRightNumber));
                winner = NOONE;
                pushOut(BLUE);
            }else{
                redResult = createNoticeTextView(answer,ANSWER_LATE);

                redCardLayout.addView(redResult);
            }
        } else {
            redResult = createNoticeTextView(answer,ANSWER_WRONG);
            redCardLayout.addView(redResult);

            winner = BLUE;
            blueRightNumber++;
            pushOut(RED);
            blueScoreTextView.setText(String.valueOf(blueRightNumber));
        }
        runAfterAnimationPlayed();
    }

    public void onClickBlueChoice(View v){
        redChoiceLayout.setVisibility(View.GONE);
        blueChoiceLayout.setVisibility(View.GONE);
        LinearLayout thisLayout = findViewById(v.getId());
        TextView childTextView = (TextView) thisLayout.getChildAt(0);
        String answer = childTextView.getText().toString();
        if(questions.get(questionNow).answer.equals(answer)){
            if(winner==NOONE){
                blueResult = createNoticeTextView(answer, ANSWER_RIGHT);
                blueCardLayout.addView(blueResult);

                winner = BLUE;
                blueRightNumber ++;
                blueScoreTextView.setText(String.valueOf(blueRightNumber));
                winner = NOONE;
                pushOut(RED);
            }else{
                blueResult = createNoticeTextView(answer,ANSWER_LATE);
                blueCardLayout.addView(blueResult);
            }
        } else {
            blueResult = createNoticeTextView(answer,ANSWER_WRONG);
            blueCardLayout.addView(blueResult);

            winner = RED;
            redRightNumber++;
            pushOut(BLUE);
            redScoreTextView.setText(String.valueOf(redRightNumber));
        }
        runAfterAnimationPlayed();
    }

    public void showMenu(View v){
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.menu_dialog);
        Button exitBtn = (Button) dialog.findViewById(R.id.exit_btn);
        Switch switchSound = (Switch) dialog.findViewById(R.id.switch_sound);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SumoActivity.super.onBackPressed();
            }
        });

        switchSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mediaPlayer != null) {
                    if(!isChecked){
                        if(mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                        }
                        isPlaySound = false;
                    }
                }
            }
        });
        dialog.show();
    }
}
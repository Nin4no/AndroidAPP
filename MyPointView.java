package com.example.test1;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

   
    static int index_layout = 0;

    LinearLayout start_layout, inGame_layout, setting_layout, end_layout;
    FrameLayout inGame_play_layout;
    TextView Timer, scoreTextView, finalScoreTextView;
    CountDownTimer countDownTimer;
    int score = 0;

    GridLayout game_field;
    List<Pair<mandarin, View>> mandarinViews = new ArrayList<>();
    MyPointView myPointView;
    Random random = new Random();

    private MediaPlayer mp1;
    private boolean isMuted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Timer              = findViewById(R.id.TimeDisplay);
        scoreTextView      = findViewById(R.id.ScoreDisplay);
        finalScoreTextView = findViewById(R.id.FinalScoreDisplay);

        Button btn_start               = findViewById(R.id.btn_Start);
        Button btn_ingame_setting      = findViewById(R.id.btn_InGame_Setting);
        Button btn_settingpage_back    = findViewById(R.id.btn_SettingPage_Back);
        Button btn_settingpage_restart = findViewById(R.id.btn_SettingPage_Restart);
        Button btn_settingpage_title   = findViewById(R.id.btn_SettingPage_Title);
        Button btn_endpage_again       = findViewById(R.id.btn_EndPage_Again);
        Button btn_endpage_title       = findViewById(R.id.btn_EndPage_Title);

        start_layout      = findViewById(R.id.layout_First_Page);
        inGame_layout     = findViewById(R.id.layout_InGame);
        setting_layout    = findViewById(R.id.layout_SettingPage);
        end_layout        = findViewById(R.id.layout_EndPage);
        inGame_play_layout= findViewById(R.id.inGame_play);
        game_field        = findViewById(R.id.inGame_field);

        
        mp1 = MediaPlayer.create(this, R.raw.stikato);
        mp1.setLooping(true);
        mp1.setVolume(0.5f, 0.5f);

        CheckBox chkMute = findViewById(R.id.Mute);
        SeekBar volumeBar = findViewById(R.id.VolumeControl);
        chkMute.setOnCheckedChangeListener((b, checked) -> {
            isMuted = checked;
            mp1.setVolume(checked? 0f : 0.5f, checked? 0f : 0.5f);
        });
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int p, boolean u) {
                float vol = p / 100f;
                mp1.setVolume(isMuted? 0f : vol, isMuted? 0f : vol);
            }
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {}
        });


        myPointView = new MyPointView(this, game_field, mandarinViews);
        inGame_play_layout.addView(myPointView);


        updateScoreDisplay();
        finalScoreTextView.setText(String.valueOf(score));
        change_layout();


        btn_start.setOnClickListener(v -> {
            Toast.makeText(this, "게임을 시작합니다.", Toast.LENGTH_SHORT).show();
            resetGame();
            index_layout = 1;
            change_layout();
            startTimer();
            mp1.start();
        });

        btn_ingame_setting.setOnClickListener(v -> {

            index_layout = 2;
            change_layout();
        });

        btn_settingpage_back.setOnClickListener(v -> {

            index_layout = 1;
            change_layout();
        });

        btn_settingpage_restart.setOnClickListener(v -> {

            resetGame();
            index_layout = 1;
            change_layout();
            startTimer();
        });

        btn_settingpage_title.setOnClickListener(v -> {
            // 설정 → 타이틀
            title_layout();
        });

        btn_endpage_again.setOnClickListener(v -> {
            // 엔드페이지에서 다시 시작
            resetGame();
            index_layout = 1;
            change_layout();
            startTimer();
        });

        btn_endpage_title.setOnClickListener(v -> {
            // 엔드페이지 → 타이틀
            title_layout();
        });
    }

    /** 점수뷰 업데이트 **/
    private void updateScoreDisplay() {
        scoreTextView.setText("Score: " + score);
    }

    /** 점수 추가 **/
    public void addScore(int count) {
        score += count;
        updateScoreDisplay();
    }

    /** 타이머 시작 **/
    public void startTimer() {
        // 게임 시작 전 점수 초기화
        score = 0;
        updateScoreDisplay();
        finalScoreTextView.setText("0");

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(60_000, 1_000) {
            public void onTick(long millisUntilFinished) {
                int min = (int) (millisUntilFinished / 60_000);
                int sec = (int) ((millisUntilFinished % 60_000) / 1_000);
                Timer.setText(String.format("%d:%02d", min, sec));
            }

            public void onFinish() {
                // 타이머 종료 시 엔드페이지로
                finalScoreTextView.setText(String.valueOf(score));
                index_layout = 3;
                change_layout();
            }
        }.start();
    }

    /** 레이아웃 전환 **/
    private void change_layout() {
        start_layout.setVisibility(index_layout == 0 ? View.VISIBLE : View.INVISIBLE);
        inGame_layout .setVisibility(index_layout == 1 ? View.VISIBLE : View.INVISIBLE);
        setting_layout .setVisibility(index_layout == 2 ? View.VISIBLE : View.INVISIBLE);
        end_layout     .setVisibility(index_layout == 3 ? View.VISIBLE : View.INVISIBLE);
    }

    /** 타이틀 화면으로 **/
    private void title_layout() {
        if (countDownTimer != null) countDownTimer.cancel();
        mp1.pause();
        index_layout = 0;
        change_layout();
    }

    /** 게임 리셋: 점수, 블록 모두 초기 상태로 **/
    private void resetGame() {
        // 기존 타이머 취소
        if (countDownTimer != null) countDownTimer.cancel();

        // 점수 초기화
        score = 0;
        updateScoreDisplay();

        // 필드 비우기
        game_field.removeAllViews();
        mandarinViews.clear();

        // 새 블록 생성
        for (int i = 0; i < 98; i++) {
            int num = random.nextInt(9) + 1;
            mandarin block = new mandarin(this, num);
            int row = i / 7, col = i % 7;
            GridLayout.Spec rowSpec = GridLayout.spec(row, 1);
            GridLayout.Spec colSpec = GridLayout.spec(col, 1);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, colSpec);
            params.width = 150;
            params.height = 150;
            params.setMargins(10, 10, 10, 10);
            game_field.addView(block, params);
            mandarinViews.add(new Pair<>(block, block));
        }
    }
}

package com.example.mandaringame_test;


import android.animation.ValueAnimator;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Pair;
import android.view.View;
import android.view.animation.LinearInterpolator;
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
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    LinearLayout start_layout, inGame_layout, setting_layout, end_layout;
    FrameLayout inGame_play_layout;
    private MediaPlayer mp1;
    private boolean isMuted = false;

    CountDownTimer countDownTimer;
    private boolean timerRunning = false;
    private long timeLeftInMillis;
    private boolean isFirstResume = true;
    TextView Timer;

    MyPointView myPointView;
    GridLayout game_field;
    static boolean Gaming = false;

    List<Pair<mandarin, View>> mandarinViews = new ArrayList<>();

    private int currentScore = 0;
    private TextView scoreDisplay;
    private TextView finalScoreDisplay;

    private View timerBar;
    private int maxTimerDuration = 30000; // 30초
    private ValueAnimator barAnim;


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

        timerBar = findViewById(R.id.TimerBar);

        Button btn_start = findViewById(R.id.btn_Start);
        Button btn_ingame_setting = findViewById(R.id.btn_InGame_Setting);
        Button btn_settingpage_back = findViewById(R.id.btn_SettingPage_Back);
        Button btn_settingpage_title = findViewById(R.id.btn_SettingPage_Title);
        Button btn_endpage_again = findViewById(R.id.btn_EndPage_Again);
        Button btn_endpage_title = findViewById(R.id.btn_EndPage_Title);
        Button btn_settingpage_restart = findViewById(R.id.btn_SettingPage_Restart);

        start_layout = findViewById(R.id.layout_First_Page);
        inGame_layout = findViewById(R.id.layout_InGame);
        inGame_play_layout = findViewById(R.id.inGame_play);
        setting_layout = findViewById(R.id.layout_SettingPage);
        end_layout = findViewById(R.id.layout_EndPage);

        CheckBox chk1 = findViewById(R.id.Mute);
        SeekBar volumebar = findViewById(R.id.VolumeControl);
        mp1 = MediaPlayer.create(this, R.raw.stikato);

        scoreDisplay = findViewById(R.id.ScoreDisplay);
        finalScoreDisplay = findViewById(R.id.FinalScoreDisplay);

        change_layout(5); // 초기 화면 설정

        game_field = findViewById(R.id.inGame_field);
        game_field.setColumnCount(6); // 컬럼 수 명시적으로 설정

        // initializeGameField는 게임 시작 시에만 호출
        // initializeGameField(); // onCreate에서는 게임 시작 전에 호출 안 함

        myPointView = new MyPointView(this, game_field, mandarinViews);
        FrameLayout.LayoutParams overlayParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        myPointView.setLayoutParams(overlayParams);
        inGame_play_layout.addView(myPointView);


        mp1.start();
        mp1.setLooping(true);
        mp1.setVolume(0.5f, 0.5f);

        Timer = findViewById(R.id.TimeDisplay);

        btn_start.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "게임을 시작합니다.", Toast.LENGTH_SHORT).show();
            change_layout(0); // 인게임 레이아웃으로 변경
            restartGame(); // 게임 재시작 (필드 초기화, 점수 초기화, 타이머 시작)
        });

        btn_ingame_setting.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "설정으로 이동합니다.", Toast.LENGTH_SHORT).show();
            change_layout(1);
            pauseTimer();
        });
        btn_settingpage_back.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "게임으로 이동합니다.", Toast.LENGTH_SHORT).show();
            change_layout(2);
            resumeTimer();
        });
        btn_settingpage_restart.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "게임을 재시작합니다.", Toast.LENGTH_SHORT).show();
            change_layout(2);
            restartGame();
        });
        btn_settingpage_title.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "타이틀로 이동합니다.", Toast.LENGTH_SHORT).show();
            change_layout(5);
        });
        btn_endpage_again.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "게임을 재시작합니다.", Toast.LENGTH_SHORT).show();
            change_layout(4); // 인게임 레이아웃으로 변경
            restartGame();
        });
        btn_endpage_title.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "타이틀로 이동합니다.", Toast.LENGTH_SHORT).show();
            change_layout(5);
        });

        chk1.setOnClickListener(v -> {
            if (chk1.isChecked()) {
                volumebar.setProgress(0);
            } else {
                volumebar.setProgress(50);
            }
        });
        volumebar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && progress != 0) {
                    chk1.setChecked(false);
                }
                float volume = progress / 100f;

                mp1.setVolume(volume, volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseTimer();
        if (mp1 != null && mp1.isPlaying()) {
            mp1.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!timerRunning && !isFirstResume) {
            resumeTimer();
        }
        if (mp1 != null && !mp1.isPlaying() && !isMuted) {
            mp1.start();
        }
        isFirstResume = false;
    }

    public void change_layout(int i) {
        int Layout_Index = i;

        if (Layout_Index == 0) { // InGame 시작
            start_layout.setVisibility(View.INVISIBLE);
            inGame_layout.setVisibility(View.VISIBLE);
            setting_layout.setVisibility(View.INVISIBLE); // 설정 레이아웃도 숨김
            end_layout.setVisibility(View.INVISIBLE); // 종료 레이아웃도 숨김
            Gaming = true;
        } else if (Layout_Index == 1) { // 설정 페이지
            start_layout.setVisibility(View.INVISIBLE);
            inGame_layout.setVisibility(View.INVISIBLE);
            setting_layout.setVisibility(View.VISIBLE);
            end_layout.setVisibility(View.INVISIBLE);
            Gaming = false;
        } else if (Layout_Index == 2) { // 게임으로 돌아가기 (설정 -> 인게임)
            start_layout.setVisibility(View.INVISIBLE);
            inGame_layout.setVisibility(View.VISIBLE);
            setting_layout.setVisibility(View.INVISIBLE);
            end_layout.setVisibility(View.INVISIBLE);
            Gaming = true;
        } else if (Layout_Index == 3) { // 게임 종료 (EndPage)
            start_layout.setVisibility(View.INVISIBLE);
            inGame_layout.setVisibility(View.INVISIBLE);
            setting_layout.setVisibility(View.INVISIBLE);
            end_layout.setVisibility(View.VISIBLE);
            Gaming = false;
        } else if (Layout_Index == 4) { // 게임 재시작 (EndPage -> InGame)
            start_layout.setVisibility(View.INVISIBLE);
            inGame_layout.setVisibility(View.VISIBLE);
            setting_layout.setVisibility(View.INVISIBLE);
            end_layout.setVisibility(View.INVISIBLE);
            Gaming = true;
        } else if (Layout_Index == 5) { // 타이틀 화면
            start_layout.setVisibility(View.VISIBLE);
            inGame_layout.setVisibility(View.INVISIBLE);
            setting_layout.setVisibility(View.INVISIBLE);
            end_layout.setVisibility(View.INVISIBLE);
            Gaming = false;
        }
    }

    public void startTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        if (barAnim != null) barAnim.cancel();

        int fullWidth = getResources().getDisplayMetrics().widthPixels;
        barAnim = ValueAnimator.ofInt(fullWidth, 0);
        barAnim.setDuration(maxTimerDuration);
        barAnim.setInterpolator(new LinearInterpolator());
        barAnim.addUpdateListener(animation -> {
            int width = (int) animation.getAnimatedValue();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT);
            timerBar.setLayoutParams(params);
        });
        barAnim.start();


        timeLeftInMillis = maxTimerDuration;
        countDownTimer = new CountDownTimer(maxTimerDuration, 1000) {
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                int min = (int) (millisUntilFinished / 60000);
                int sec = (int) ((millisUntilFinished % 60000) / 1000);
                String time = String.format("%d:%02d", min, sec);
                Timer.setText(time);
            }

            public void onFinish() {
                timerRunning = false;
                change_layout(3); // 게임 종료 화면으로
            }
        }.start();
        timerRunning = true;
    }

    public void pauseTimer() {
        if (timerRunning) {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            if (barAnim != null && barAnim.isRunning()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    barAnim.pause();
                } else {
                    barAnim.cancel();
                }
            }
            timerRunning = false;
        }
    }

    public void resumeTimer() {
        if (!timerRunning) {
            countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
                public void onTick(long millisUntilFinished) {
                    timeLeftInMillis = millisUntilFinished;
                    int min = (int) (millisUntilFinished / 60000);
                    int sec = (int) ((millisUntilFinished % 60000) / 1000);
                    String time = String.format("%d:%02d", min, sec);
                    Timer.setText(time);
                }

                public void onFinish() {
                    timerRunning = false;
                    change_layout(3); // 게임 종료 화면으로
                }
            }.start();
            if (barAnim != null) {
                int currentWidth = timerBar.getLayoutParams().width;
                barAnim.cancel();
                barAnim = ValueAnimator.ofInt(currentWidth, 0);
                barAnim.setDuration(timeLeftInMillis);
                barAnim.setInterpolator(new LinearInterpolator());
                barAnim.addUpdateListener(anim -> {
                    int w = (int) anim.getAnimatedValue();
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            w, LinearLayout.LayoutParams.MATCH_PARENT
                    );
                    timerBar.setLayoutParams(lp);
                });
                barAnim.start();
            }
            timerRunning = true;
        }
    }

    // 게임 필드 초기화 (새로운 게임 시작 시에만 호출)
    public void initializeGameField() {
        game_field.removeAllViews(); // 기존 귤 제거
        mandarinViews.clear(); // 리스트 초기화

        Random random = new Random();

        int hanrabongCount = random.nextInt(4) + 7;
        Set<Integer> hanrabongIndices = new HashSet<>();
        while (hanrabongIndices.size() < hanrabongCount) {
            hanrabongIndices.add(random.nextInt(54));
        }

        for (int i = 0; i < 54; i++) {
            int num = random.nextInt(9) + 1;
            mandarin block = new mandarin(this, num);
            if (hanrabongIndices.contains(i)) {
                block.markAsHanrabong();
            }

            int row = i / 6;
            int col = i % 6;
            GridLayout.Spec rowSpec = GridLayout.spec(row, 1);
            GridLayout.Spec colSpec = GridLayout.spec(col, 1);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, colSpec);
            params.width = 150;
            params.height = 150;
            params.setMargins(10, 10, 10, 10);

            game_field.addView(block, params);
            mandarinViews.add(new Pair<>(block, block));
        }
        if (myPointView != null) {
            myPointView.updateMandarinViews(mandarinViews); // MyPointView 갱신
        }
    }

    // 이 메서드는 객체가 없어질 때 바로 재생성되지 않도록 하기 위해 제거됩니다.
    // public void refillGameField() { ... }

    public void updateScore(int scoreToAdd) {
        currentScore += scoreToAdd;
        scoreDisplay.setText("Score: " + currentScore);
        finalScoreDisplay.setText(String.valueOf(currentScore));
    }

    public void restartGame() {
        initializeGameField(); // 항상 전체 그리드를 새로 만듭니다.
        currentScore = 0;
        updateScore(0);
        startTimer();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        timerBar.setLayoutParams(params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp1 != null) {
            mp1.release();
            mp1 = null;
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (barAnim != null) {
            barAnim.cancel();
        }
    }
}

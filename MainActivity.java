package com.example.mandaringame;

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
import android.widget.ImageButton;
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
    //private boolean isMuted = false;

    CountDownTimer countDownTimer;
    private boolean timerRunning = false;
    private long timeLeftInMillis;
    private boolean isFirstResume = true;
    TextView Timer;

    MyPointView myPointView;
    GridLayout game_field;
    static boolean Gaming = false;
    private int currentScore = 0;
    private TextView scoreDisplay;
    private TextView finalScoreDisplay;

    private View timerBar;
    private int maxTimerDuration = 120000;
    private ValueAnimator barAnim;

    List<Pair<mandarin, View>> mandarinViews = new ArrayList<>();

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

        //기능버튼
        Button btn_start = findViewById(R.id.btn_Start);
        ImageButton btn_ingame_setting = findViewById(R.id.btn_InGame_Setting);
        Button btn_settingpage_back = findViewById(R.id.btn_SettingPage_Back);
        Button btn_settingpage_title = findViewById(R.id.btn_SettingPage_Title);
        Button btn_endpage_again = findViewById(R.id.btn_EndPage_Again);
        Button btn_endpage_title = findViewById(R.id.btn_EndPage_Title);
        Button btn_settingpage_restart = findViewById(R.id.btn_SettingPage_Restart);

        //레이아웃
        start_layout = findViewById(R.id.layout_First_Page);
        inGame_layout = findViewById(R.id.layout_InGame);
        inGame_play_layout = findViewById(R.id.inGame_play);
        setting_layout = findViewById(R.id.layout_SettingPage);
        end_layout = findViewById(R.id.layout_EndPage);
        timerBar = findViewById(R.id.TimerBar);
        scoreDisplay = findViewById(R.id.ScoreDisplay);
        finalScoreDisplay = findViewById(R.id.FinalScoreDisplay);

        //배경음악
        CheckBox chk1 = findViewById(R.id.Mute);
        SeekBar volumebar = findViewById(R.id.VolumeControl);
        mp1 = MediaPlayer.create(this, R.raw.stikato);

        //기본 화면으로
        change_layout(5);

        //인게임 객체
        game_field = findViewById(R.id.inGame_field);
        game_field.setColumnCount(7); 

        myPointView = new MyPointView(this, game_field, mandarinViews);
        FrameLayout.LayoutParams overlayParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        myPointView.setLayoutParams(overlayParams);
        inGame_play_layout.addView(myPointView);


        //배경음악
        mp1.start();
        mp1.setLooping(true);
        mp1.setVolume(0.5f, 0.5f);

        //타이머
        Timer = findViewById(R.id.TimeDisplay);

        // 버튼 클릭 리스너
        btn_start.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "게임을 시작합니다.", Toast.LENGTH_SHORT).show();
            change_layout(0);
            restartGame(); 
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
            change_layout(4);
            restartGame();
        });
        btn_endpage_title.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "타이틀로 이동합니다.", Toast.LENGTH_SHORT).show();
            change_layout(5);
        });

        //Mute버튼체크 확인
        chk1.setOnClickListener(v -> {
            if (chk1.isChecked()) {
                volumebar.setProgress(0);
                //isMuted = true;
            } else {
                volumebar.setProgress(50);
               // isMuted = false;
            }
        });
        //볼륨바
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

    //백그라운드로 넘어갔을때
    @Override
    protected void onPause() {
        super.onPause();
        pauseTimer();
        if (mp1 != null && mp1.isPlaying()) {
            mp1.pause();
        }
    }

    //메인으로 돌아올때
    @Override
    protected void onResume() {
        super.onResume();
        if (!timerRunning && !isFirstResume && Gaming){
            resumeTimer();
        }
        if (mp1 != null && !mp1.isPlaying()) {
            mp1.start();
        }
        isFirstResume = false;
    }

    //화면 바꿀때
    public void change_layout(int i) {
        int Layout_Index = i;

        if (Layout_Index == 0) {
            start_layout.setVisibility(View.INVISIBLE);
            inGame_layout.setVisibility(View.VISIBLE);
            Gaming = true;
        } else if (Layout_Index == 1) {
            setting_layout.setVisibility(View.VISIBLE);
            Gaming = false;
        } else if (Layout_Index == 2) {
            setting_layout.setVisibility(View.INVISIBLE);
            Gaming = true;
        } else if (Layout_Index == 3) {
            end_layout.setVisibility(View.VISIBLE);
            setting_layout.setVisibility(View.INVISIBLE);
            Gaming = false;
        } else if (Layout_Index == 4) {
            end_layout.setVisibility(View.INVISIBLE);
            Gaming = true;
        } else if (Layout_Index == 5) {
            end_layout.setVisibility(View.INVISIBLE);
            setting_layout.setVisibility(View.INVISIBLE);
            inGame_layout.setVisibility(View.INVISIBLE);
            start_layout.setVisibility(View.VISIBLE);
            Gaming = false;
        }

    }

    //타이머
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
                    change_layout(3);
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
                        change_layout(3);
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


        public void extendTimer(long timeToAddMillis) {
            if (timerRunning) {
                pauseTimer();
                timeLeftInMillis += timeToAddMillis;

                int min = (int) (timeLeftInMillis / 60000);
                int sec = (int) ((timeLeftInMillis % 60000) / 1000);
                String time = String.format("%d:%02d", min, sec);
                Timer.setText(time);

               // int fullWidth = getResources().getDisplayMetrics().widthPixels;

                int currentTimerBarWidth = timerBar.getWidth();
                long newDurationForAnim = timeLeftInMillis;

                if (barAnim != null) barAnim.cancel();

                barAnim = ValueAnimator.ofInt(currentTimerBarWidth, 0);
                barAnim.setDuration(newDurationForAnim);
                barAnim.setInterpolator(new LinearInterpolator());
                barAnim.addUpdateListener(animation -> {
                    int width = (int) animation.getAnimatedValue();
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT);
                    timerBar.setLayoutParams(params);
                });
                barAnim.start();

                resumeTimer();
            } else {

                timeLeftInMillis += timeToAddMillis;
                int min = (int) (timeLeftInMillis / 60000);
                int sec = (int) ((timeLeftInMillis % 60000) / 1000);
                String time = String.format("%d:%02d", min, sec);
                Timer.setText(time);
            }
        }


        public void initializeGameField() {
            game_field.removeAllViews();
            mandarinViews.clear();

            Random random = new Random();

            int hanrabongCount = random.nextInt(4) + 7;
            Set<Integer> hanrabongIndices = new HashSet<>();
            while (hanrabongIndices.size() < hanrabongCount) {
                hanrabongIndices.add(random.nextInt(98));
            }


            int dolhareubangCount = 3;
            Set<Integer> dolhareubangIndices = new HashSet<>();
            while (dolhareubangIndices.size() < dolhareubangCount) {
                int newIndex = random.nextInt(98);
                if (!hanrabongIndices.contains(newIndex) && !dolhareubangIndices.contains(newIndex)) {
                    dolhareubangIndices.add(newIndex);
                }
            }

            for (int i = 0; i < 98; i++) {
                int num;
                mandarin block;

                if (dolhareubangIndices.contains(i)) {
                    num = 0;
                    block = new mandarin(this, num);
                    block.markAsDolhareubang();
                } else {
                    num = random.nextInt(9) + 1;
                    block = new mandarin(this, num);
                    if (hanrabongIndices.contains(i)) {
                        block.markAsHanrabong();
                    }
                }

                int row = i / 7;
                int col = i % 7;
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
                myPointView.updateMandarinViews(mandarinViews);
            }
        }

        public void updateScore(int scoreToAdd) {
            currentScore += scoreToAdd;
            scoreDisplay.setText("Score: " + currentScore);
            finalScoreDisplay.setText(String.valueOf(currentScore));
        }

        public void restartGame() {
            initializeGameField();
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

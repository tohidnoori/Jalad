package com.thdnoori.Jalad.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.thdnoori.Jalad.Adapter.WordAdapter;
import com.thdnoori.Jalad.Database.WordTimerDb;
import com.thdnoori.Jalad.Model.GoodPrefs;
import com.thdnoori.Jalad.Model.Letter;
import com.thdnoori.Jalad.R;
import com.thdnoori.Jalad.databinding.ActivityTimerGameBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellAdRequestListener;
import ir.tapsell.sdk.TapsellAdRequestOptions;
import ir.tapsell.sdk.TapsellAdShowListener;
import ir.tapsell.sdk.TapsellShowOptions;

public class TimerGameActivity extends AppCompatActivity {
    ActivityTimerGameBinding binding;
    WordAdapter adapter;
    LinearLayout winLayoutPlayNext;
    String adId;
    long time;
    MediaPlayer correctSound, incorrectSound, winSound, loseSound, clickSound;
    WordTimerDb w;
    int randomWord;
    boolean isFinished = false;
    Dialog winLayout, failLayout;
    int countMisses = 2;
    CardView watchAd;
    DisplayMetrics displayMetrics = new DisplayMetrics();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimerGameBinding.inflate(getLayoutInflater());
        GoodPrefs.init(getApplicationContext());
        setContentView(binding.getRoot());
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        clickSound = MediaPlayer.create(TimerGameActivity.this, R.raw.click);
        winLayout = new Dialog(TimerGameActivity.this);
        winLayout.setContentView(R.layout.win_dailog);
        winLayout.getWindow().setGravity(Gravity.BOTTOM);
        winLayout.getWindow().setLayout(displayMetrics.widthPixels, (int) (displayMetrics.heightPixels * 4.5 / 10));
        winLayout.setCancelable(false);
        failLayout = new Dialog(TimerGameActivity.this);
        failLayout.setContentView(R.layout.fail_dailog);
        failLayout.getWindow().setGravity(Gravity.BOTTOM);
        failLayout.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) (displayMetrics.heightPixels * 4.5 / 10));
        failLayout.setCancelable(false);
        watchAd = failLayout.findViewById(R.id.watchAd);
        failLayout.findViewById(R.id.payMoneyChance).setOnClickListener(view -> {
            clickSound.start();
            if (GoodPrefs.getInstance().getInt("coin", 0) >= 70) {
                GoodPrefs.getInstance().saveInt("coin", GoodPrefs.getInstance().getInt("coin", 0) - 70);
                winStatement(true,true);
            }
        });
        winLayoutPlayNext = winLayout.findViewById(R.id.winLayoutPlayNext);
        winLayoutPlayNext.setOnClickListener(view -> {
            try {
                winSound.stop();
                loseSound.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(TimerGameActivity.this, TimerGameActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
        if (GoodPrefs.getInstance().getBoolean("soundEnable", true)) {
            correctSound = MediaPlayer.create(TimerGameActivity.this, R.raw.correct);
            incorrectSound = MediaPlayer.create(TimerGameActivity.this, R.raw.incorrect);
            switch (GoodPrefs.getInstance().getInt("executionImage", 0)) {
                case 0:
                    winSound = MediaPlayer.create(TimerGameActivity.this, R.raw.win_sound);
                    loseSound = MediaPlayer.create(TimerGameActivity.this, R.raw.lose_sound);
                    break;
                case 1:
                    winSound = MediaPlayer.create(TimerGameActivity.this, R.raw.win_sound1);
                    loseSound = MediaPlayer.create(TimerGameActivity.this, R.raw.lose_sound);
                    break;
                case 2:
                    winSound = MediaPlayer.create(TimerGameActivity.this, R.raw.win_sound2);
                    loseSound = MediaPlayer.create(TimerGameActivity.this, R.raw.lose_sound2);
                    break;
                case 3:
                    winSound = MediaPlayer.create(TimerGameActivity.this, R.raw.win_sound3);
                    loseSound = MediaPlayer.create(TimerGameActivity.this, R.raw.lose_sound2);
                    break;
                case 4:
                    winSound = MediaPlayer.create(TimerGameActivity.this, R.raw.win_sound1);
                    loseSound = MediaPlayer.create(TimerGameActivity.this, R.raw.lose_sound1);
                    loseSound.setLooping(true);
                    break;
            }
        }
        switch (GoodPrefs.getInstance().getInt("executionImage", 0)) {
            case 0:
                binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope3));
                break;
            case 1:
                binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_p3));
                break;
            case 2:
                binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_pp3));
                break;
            case 3:
                binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_ppp3));
                break;
            case 4:
                binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_pppp3));
                break;

        }
        binding.coins.setText(String.valueOf(GoodPrefs.getInstance().getInt("coin", 0)));
        binding.record.setText("رکورد : " + GoodPrefs.getInstance().getInt("recordTimer", 0));
        binding.round.setText("راند : " + GoodPrefs.getInstance().getInt("roundTimer", 1));
        binding.goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSound.start();
                startActivity(new Intent(TimerGameActivity.this, MainActivity.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        binding.goHelp.setOnClickListener(view -> {
            Dialog d = new Dialog(TimerGameActivity.this);
            d.setContentView(R.layout.help_dialog);
            d.getWindow().setLayout(displayMetrics.widthPixels, (int) (displayMetrics.heightPixels * 2.7 / 10));
            d.findViewById(R.id.showWord).setOnClickListener(view1 -> {
                clickSound.start();
                if (GoodPrefs.getInstance().getInt("coin", 0) >= 30) {
                    showCoinChanges(30);
                    adapter.visiblePaidWord();
                    GoodPrefs.getInstance().saveInt("coin", GoodPrefs.getInstance().getInt("coin", 0) - 30);
                    binding.coins.setText(String.valueOf(GoodPrefs.getInstance().getInt("coin", 0)));
                    if (adapter.checkWinStatement()) {
                        isFinished = true;
                        winLayout.show();
                        winAnimation();
                        try {
                            winSound.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        new Handler().post(() -> {
                            GoodPrefs.getInstance().saveInt("adRecord", GoodPrefs.getInstance().getInt("adRecord", 0) + 1);
                            GoodPrefs.getInstance().saveInt("roundTimer", GoodPrefs.getInstance().getInt("roundTimer", 1) + 1);
                            if (GoodPrefs.getInstance().getInt("roundTimer", 1) > GoodPrefs.getInstance().getInt("recordTimer", 0)) {
                                GoodPrefs.getInstance().saveInt("recordTimer", GoodPrefs.getInstance().getInt("roundTimer", 1) - 1);
                            }
                            w.delete();
                            switch (GoodPrefs.getInstance().getInt("executionImage", 0)) {
                                case 0:
                                    Glide.with(TimerGameActivity.this).load(R.drawable.theme1_win).into(binding.executionRope);
                                    break;
                                case 1:
                                    Glide.with(TimerGameActivity.this).load(R.drawable.theme2_win).into(binding.executionRope);
                                    break;
                                case 2:
                                    Glide.with(TimerGameActivity.this).load(R.drawable.theme3_win).into(binding.executionRope);
                                    break;
                                case 3:
                                    Glide.with(TimerGameActivity.this).load(R.drawable.theme4_win).into(binding.executionRope);
                                    break;
                                case 4:
                                    Glide.with(TimerGameActivity.this).load(R.drawable.theme5_win).into(binding.executionRope);
                                    break;
                            }
                        });
                    }
                    d.dismiss();
                }
            });
            d.findViewById(R.id.delete5words).setVisibility(View.GONE);
            d.findViewById(R.id.passLevel).setOnClickListener(view1 -> {
                clickSound.start();
                if (GoodPrefs.getInstance().getInt("coin", 0) >= 70) {
                    d.dismiss();
                    GoodPrefs.getInstance().saveInt("coin", GoodPrefs.getInstance().getInt("coin", 0) - 70);
                    winStatement(true,false);
                }
            });
            d.show();
        });
        binding.wordRec.setLayoutManager(new LinearLayoutManager(TimerGameActivity.this, RecyclerView.HORIZONTAL, true));
        getNormalWord();
        initLetterLayout();
        adapter = new WordAdapter(TimerGameActivity.this, w);
        binding.wordRec.setAdapter(adapter);
        binding.wordCategory.setText(w.getWordCategory());
        adapter.notifyDataSetChanged();
        timerInit();
    }

    public void check(View view) {
        String x = ((TextView) view).getText().toString();
        if (w.getWordName().contains(x)) {
            try {
                correctSound.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            adapter.makeITVisible(x);
             view.setBackground(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.tick));
            if (adapter.checkWinStatement()) {
                winStatement(false,false);
            }
        } else {
            if (countMisses == 9) {
                isFinished = true;
                try {
                    loseSound.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                switch (GoodPrefs.getInstance().getInt("executionImage", 0)) {
                    case 0:
                        Glide.with(TimerGameActivity.this).load(R.drawable.theme1_lose).into(binding.executionRope);
                        break;
                    case 1:
                        Glide.with(TimerGameActivity.this).load(R.drawable.theme2_lose).into(binding.executionRope);
                        break;
                    case 2:
                        Glide.with(TimerGameActivity.this).load(R.drawable.theme3_lose).into(binding.executionRope);
                        break;
                    case 3:
                        Glide.with(TimerGameActivity.this).load(R.drawable.theme4_lose).into(binding.executionRope);
                        break;
                    case 4:
                        Glide.with(TimerGameActivity.this).load(R.drawable.theme5_lose).into(binding.executionRope);
                        break;
                }
                adapter.visibleMissingWord();
                failLayout.show();
                countMisses = 12;
                Tapsell.requestAd(TimerGameActivity.this,
                        "5f6f5c677b431900012ce286",
                        new TapsellAdRequestOptions(),
                        new TapsellAdRequestListener() {
                            @Override
                            public void onAdAvailable(String adid) {
                                adId = adid;
                                watchAd.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError(String message) {
                                watchAd.setVisibility(View.GONE);
                            }
                        });
                watchAd.setOnClickListener(view1 -> {
                    try {
                        loseSound.stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Tapsell.showAd(TimerGameActivity.this,
                            "5f6f5c677b431900012ce286",
                            adId,
                            new TapsellShowOptions(),
                            new TapsellAdShowListener() {
                                @Override
                                public void onOpened() {
                                }

                                @Override
                                public void onClosed() {
                                }

                                @Override
                                public void onError(String message) {
                                }

                                @Override
                                public void onRewarded(boolean completed) {
                                    if (completed) {
                                        GoodPrefs.getInstance().saveInt("adRecord", GoodPrefs.getInstance().getInt("adRecord", 0) + 1);
                                        GoodPrefs.getInstance().saveInt("coin", GoodPrefs.getInstance().getInt("coin", 0) + 6);
                                        GoodPrefs.getInstance().saveInt("roundTimer", GoodPrefs.getInstance().getInt("roundTimer", 1) + 1);
                                        if (GoodPrefs.getInstance().getInt("roundTimer", 1) > GoodPrefs.getInstance().getInt("recordTimer", 0)) {
                                            GoodPrefs.getInstance().saveInt("recordTimer", GoodPrefs.getInstance().getInt("roundTimer", 1) - 1);
                                        }
                                        w.delete();
                                        Toast.makeText(TimerGameActivity.this, "شانس دوباره گرفتی", Toast.LENGTH_LONG).show();
                                    } else {
                                        AsyncTask.execute(() -> {
                                            GoodPrefs.getInstance().saveInt("roundTimer", 1);
                                            importTimerWords();
                                        });
                                    }
                                    startActivity(new Intent(TimerGameActivity.this, TimerGameActivity.class));
                                    finish();
                                }
                            });
                });
            } else if (countMisses < 9) {
                try {
                    incorrectSound.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                view.setBackground(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.cancel));
                countMisses++;
                checkExecutionPic(countMisses);
            }
        }
         view.setClickable(false);
    }
    public void  winStatement(boolean isPayed,boolean isPayedAtTheEnd){
        isFinished = true;
        if (isPayed){
            adapter.visibleMissingWord();
        }
        new Handler().post(() -> {
            GoodPrefs.getInstance().saveInt("adRecord", GoodPrefs.getInstance().getInt("adRecord", 0) + 1);
            GoodPrefs.getInstance().saveInt("roundTimer", GoodPrefs.getInstance().getInt("roundTimer", 1) + 1);
            if (GoodPrefs.getInstance().getInt("roundTimer", 1) > GoodPrefs.getInstance().getInt("recordTimer", 0)) {
                GoodPrefs.getInstance().saveInt("recordTimer", GoodPrefs.getInstance().getInt("roundTimer", 1) - 1);
            }
            w.delete();
        });
        if(!isPayedAtTheEnd){
            switch (GoodPrefs.getInstance().getInt("executionImage", 0)) {
                case 0:
                    Glide.with(TimerGameActivity.this).load(R.drawable.theme1_win).into(binding.executionRope);
                    break;
                case 1:
                    Glide.with(TimerGameActivity.this).load(R.drawable.theme2_win).into(binding.executionRope);
                    break;
                case 2:
                    Glide.with(TimerGameActivity.this).load(R.drawable.theme3_win).into(binding.executionRope);
                    break;
                case 3:
                    Glide.with(TimerGameActivity.this).load(R.drawable.theme4_win).into(binding.executionRope);
                    break;
                case 4:
                    Glide.with(TimerGameActivity.this).load(R.drawable.theme5_win).into(binding.executionRope);
                    break;
            }
            winLayout.show();
            winAnimation();
            try {
                winSound.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            try {
                loseSound.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            startActivity(new Intent(TimerGameActivity.this, TimerGameActivity.class));
            Toast.makeText(this, "شانس دوباره گرفتی!", Toast.LENGTH_LONG).show();
            finish();
        }

    }
    public void timerInit() {
        GoodPrefs.getInstance().saveLong("timerTime", System.currentTimeMillis() + 36000);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                time = GoodPrefs.getInstance().getLong("timerTime", 0) - System.currentTimeMillis();
                time = (int) (time / 1000);
                if (isFinished) {
                    timer.purge();
                    timer.cancel();
                }
                if (time <= 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                loseSound.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            switch (GoodPrefs.getInstance().getInt("executionImage", 0)) {
                                case 0:
                                    Glide.with(TimerGameActivity.this).load(R.drawable.theme1_lose).into(binding.executionRope);
                                    break;
                                case 1:
                                    Glide.with(TimerGameActivity.this).load(R.drawable.theme2_lose).into(binding.executionRope);
                                    break;
                                case 2:
                                    Glide.with(TimerGameActivity.this).load(R.drawable.theme3_lose).into(binding.executionRope);
                                    break;
                                case 3:
                                    Glide.with(TimerGameActivity.this).load(R.drawable.theme4_lose).into(binding.executionRope);
                                    break;
                                case 4:
                                    Glide.with(TimerGameActivity.this).load(R.drawable.theme5_lose).into(binding.executionRope);
                                    break;
                            }
                            adapter.visibleMissingWord();
                            failLayout.show();
                            countMisses = 10;
                            Tapsell.requestAd(TimerGameActivity.this,
                                    "5f6f5c677b431900012ce286",
                                    new TapsellAdRequestOptions(),
                                    new TapsellAdRequestListener() {
                                        @Override
                                        public void onAdAvailable(String adid) {
                                            adId = adid;
                                            watchAd.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onError(String message) {
                                        }
                                    });
                        }
                    });
                    timer.purge();
                    timer.cancel();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Spannable timeString = new SpannableString("زمان : " + time);
                        if (time > 20) {
                            timeString.setSpan(new ForegroundColorSpan(Color.parseColor("#009688")), 7, timeString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        } else if (time > 10) {
                            timeString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF9800")), 7, timeString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        } else {
                            timeString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), 7, timeString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        binding.timer.setText(timeString);
                    }
                });
            }
        }, 0, 1000);
    }
    public void winAnimation() {
        switch (new Random().nextInt(4)) {
            case 0:
                ((TextView) ((LinearLayout) winLayout.findViewById(R.id.aivaldari)).getChildAt(1)).setText("ایول ");
                ((TextView) ((LinearLayout) winLayout.findViewById(R.id.aivaldari)).getChildAt(0)).setText("داری");
                break;
            case 1:
                ((TextView) ((LinearLayout) winLayout.findViewById(R.id.aivaldari)).getChildAt(1)).setText("درجه");
                ((TextView) ((LinearLayout) winLayout.findViewById(R.id.aivaldari)).getChildAt(0)).setText("  یک");
                break;
            case 2:
                ((TextView) ((LinearLayout) winLayout.findViewById(R.id.aivaldari)).getChildAt(1)).setText("چقد  ");
                ((TextView) ((LinearLayout) winLayout.findViewById(R.id.aivaldari)).getChildAt(0)).setText("خفنی");
                break;
        }
        ((LinearLayout) winLayout.findViewById(R.id.aivaldari)).getChildAt(0).startAnimation(AnimationUtils.loadAnimation(TimerGameActivity.this, R.anim.bounce_left));
        ((LinearLayout) winLayout.findViewById(R.id.aivaldari)).getChildAt(1).startAnimation(AnimationUtils.loadAnimation(TimerGameActivity.this, R.anim.bounce_right));
    }
    public void getNormalWord() {
        List<WordTimerDb> list = WordTimerDb.listAll(WordTimerDb.class);
        if (list.isEmpty()) {
            Toast.makeText(TimerGameActivity.this, "بابا دمت گرم دیتابیس ما ته کشید از اول برو حال کن.", Toast.LENGTH_SHORT).show();
            GoodPrefs.getInstance().saveInt("roundTimer", 1);
        }
        randomWord = new Random().nextInt(list.size());
        w = list.get(randomWord);

    }
    public void initLetterLayout() {
        ArrayList<Letter> letters = new ArrayList<>();
        for (int i = 0; i < binding.lettersLayout.getChildCount(); i++) {
            LinearLayout ln = (LinearLayout) binding.lettersLayout.getChildAt(i);

            for (int j = 0; j < ln.getChildCount(); j++) {
                letters.add(new Letter(i, j));
            }
        }
        StringBuilder stringBuilder = new StringBuilder(w.getWordName());
        int wordLength = stringBuilder.length();
        List<String> persianAlphabet = getPersianLetters();
        for (int i = 0; i < wordLength; i++) {
            int randomChar = new Random().nextInt(stringBuilder.length());
            if (persianAlphabet.contains(String.valueOf(stringBuilder.charAt(randomChar)))) {
                int randomLetter = new Random().nextInt(letters.size());
                ((TextView) ((LinearLayout) binding.lettersLayout.getChildAt(letters.get(randomLetter).getRow())).getChildAt(letters.get(randomLetter).getCol())).setText(String.valueOf(stringBuilder.charAt(randomChar)));
                persianAlphabet.remove(String.valueOf(stringBuilder.charAt(randomChar)));
                letters.remove(randomLetter);
            }
            stringBuilder.deleteCharAt(randomChar);
        }
        int remaining = letters.size();
        for (int i = 0; i < remaining; i++) {
            int randomChar = new Random().nextInt(persianAlphabet.size());
            int randomLetter = new Random().nextInt(letters.size());
            ((TextView) ((LinearLayout) binding.lettersLayout.getChildAt(letters.get(randomLetter).getRow())).getChildAt(letters.get(randomLetter).getCol())).setText(persianAlphabet.get(randomChar));
            persianAlphabet.remove(randomChar);
            letters.remove(randomLetter);
        }

    }
    public List<String> getPersianLetters() {
        List<String> strings = new ArrayList<>();
        strings.add("ا");
        strings.add("ب");
        strings.add("پ");
        strings.add("ت");
        strings.add("ث");
        strings.add("ج");
        strings.add("چ");
        strings.add("ح");
        strings.add("خ");
        strings.add("د");
        strings.add("ذ");
        strings.add("ر");
        strings.add("ز");
        strings.add("ژ");
        strings.add("س");
        strings.add("ش");
        strings.add("ص");
        strings.add("ض");
        strings.add("ط");
        strings.add("ظ");
        strings.add("ع");
        strings.add("غ");
        strings.add("ف");
        strings.add("ق");
        strings.add("ک");
        strings.add("گ");
        strings.add("ل");
        strings.add("م");
        strings.add("ن");
        strings.add("و");
        strings.add("ه");
        strings.add("ی");
        return strings;
    }
    public void checkExecutionPic(int which) {
        switch (GoodPrefs.getInstance().getInt("executionImage", 0)) {
            case 0:
                switch (which) {
                    case 3:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope4));
                        break;
                    case 4:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope5));
                        break;
                    case 5:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope6));
                        break;
                    case 6:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope7));
                        break;
                    case 7:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope8));
                        break;
                    case 8:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope9));
                        break;
                }
                break;
            case 1:
                switch (which) {
                    case 3:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_p4));
                        break;
                    case 4:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_p5));
                        break;
                    case 5:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_p6));
                        break;
                    case 6:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_p7));
                        break;
                    case 7:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_p8));
                        break;
                    case 8:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_p9));
                        break;
                }
                break;
            case 2:
                switch (which) {
                    case 3:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_pp4));
                        break;
                    case 4:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_pp5));
                        break;
                    case 5:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_pp6));
                        break;
                    case 6:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_pp7));
                        break;
                    case 7:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_pp8));
                        break;
                    case 8:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_pp9));
                        break;
                }
                break;
            case 3:
                switch (which) {
                    case 3:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_ppp4));
                        break;
                    case 4:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_ppp5));
                        break;
                    case 5:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_ppp6));
                        break;
                    case 6:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_ppp7));
                        break;
                    case 7:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_ppp8));
                        break;
                    case 8:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_ppp9));
                        break;
                }
                break;
            case 4:
                switch (which) {
                    case 1:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_pppp2));
                        break;
                    case 2:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_pppp3));
                        break;
                    case 3:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_pppp4));
                        break;
                    case 4:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_pppp5));
                        break;
                    case 5:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_pppp6));
                        break;
                    case 6:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_pppp7));
                        break;
                    case 7:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_pppp8));
                        break;
                    case 8:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(TimerGameActivity.this, R.drawable.rope_pppp9));
                        break;
                }
                break;
        }
    }
    public void showCoinChanges(int price) {
        Dialog d = new Dialog(TimerGameActivity.this);
        d.setContentView(R.layout.coin_changes);
        TextView tv = d.findViewById(R.id.coinChangesPrice);
        tv.setText("" + price);
        d.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        d.show();
        Handler h = new Handler();
        Runnable r = () -> d.dismiss();
        h.postDelayed(r, 500);
    }
    public void importTimerWords() {
        AsyncTask.execute(() -> {
            WordTimerDb.deleteAll(WordTimerDb.class);
            List<String> words = Arrays.asList(getResources().getStringArray(R.array.wordTimer));
            List<String> categories = Arrays.asList(getResources().getStringArray(R.array.word_timer_category));
            for (int i = 0; i < words.size(); i++) {
                WordTimerDb word = new WordTimerDb();
                word.setWordCategory(categories.get(i));
                word.setWordName(words.get(i));
                word.save();
            }
        });
    }
    public void playAgain(View v) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                GoodPrefs.getInstance().saveInt("roundTimer", 1);
                importTimerWords();
            }
        });

        try {
            winSound.stop();
            loseSound.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(TimerGameActivity.this, TimerGameActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
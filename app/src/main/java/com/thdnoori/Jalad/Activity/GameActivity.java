package com.thdnoori.Jalad.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.thdnoori.Jalad.Database.WordDb;
import com.thdnoori.Jalad.Model.GoodPrefs;
import com.thdnoori.Jalad.Model.Letter;
import com.thdnoori.Jalad.R;
import com.thdnoori.Jalad.databinding.ActivityGameBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellAdRequestListener;
import ir.tapsell.sdk.TapsellAdRequestOptions;
import ir.tapsell.sdk.TapsellAdShowListener;
import ir.tapsell.sdk.TapsellShowOptions;
import ir.tapsell.sdk.bannerads.TapsellBannerType;

public class GameActivity extends AppCompatActivity {
    WordAdapter adapter;
    LinearLayout winLayoutPlayNext;
    String adId;
    public ActivityGameBinding binding;
    MediaPlayer correctSound, incorrectSound, winSound, loseSound, clickSound;
    WordDb w;
    int randomWord;
    int countMisses = 0;
    Dialog winLayout, failLayout;
    CardView watchAd;
    View view;
    DisplayMetrics displayMetrics = new DisplayMetrics();

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        binding = ActivityGameBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        binding.coins.setText(String.valueOf(GoodPrefs.getInstance().getInt("coin", 0)));
        checkExecutionPic(countMisses);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("dick","راند : " + GoodPrefs.getInstance().getInt("roundWord", 1));
        GoodPrefs.init(GameActivity.this);
        Tapsell.initialize(getApplication(), "tlgtabhnkiemkdralidtddnpjedenfrikafococtlobqjeoalrbjiqtaiilcbcfgigoeir");
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        binding = ActivityGameBinding.inflate(getLayoutInflater());
        binding.banner.loadAd(GameActivity.this, "5f6f5a3e7b431900012ce285", TapsellBannerType.BANNER_320x50);
        setContentView(binding.getRoot());
        clickSound = MediaPlayer.create(GameActivity.this, R.raw.click);
        winLayout = new Dialog(GameActivity.this);
        winLayout.setContentView(R.layout.win_dailog);
        winLayout.getWindow().setGravity(Gravity.BOTTOM);
        winLayout.getWindow().setLayout(displayMetrics.widthPixels, (int) (displayMetrics.heightPixels * 4.5 / 10));
        winLayout.setCancelable(false);
        failLayout = new Dialog(GameActivity.this);
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
            Intent intent = new Intent(GameActivity.this, GameActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
        if (GoodPrefs.getInstance().getString("type", "").equals("normal")) {
            getNormalWord();
            binding.record.setText("رکورد : " + GoodPrefs.getInstance().getInt("recordWord", 0));
            binding.round.setText("راند : " + GoodPrefs.getInstance().getInt("roundWord", 1));
        } else {
            getWord3();
            binding.record.setText("رکورد : " + GoodPrefs.getInstance().getInt("recordWord3", 0));
            binding.round.setText("راند : " + GoodPrefs.getInstance().getInt("roundWord3", 1));
        }
        if (GoodPrefs.getInstance().getBoolean("soundEnable", true)) {
            correctSound = MediaPlayer.create(GameActivity.this, R.raw.correct);
            incorrectSound = MediaPlayer.create(GameActivity.this, R.raw.incorrect);
            switch (GoodPrefs.getInstance().getInt("executionImage", 0)) {
                case 0:
                    winSound = MediaPlayer.create(GameActivity.this, R.raw.win_sound);
                    loseSound = MediaPlayer.create(GameActivity.this, R.raw.lose_sound);
                    break;
                case 1:
                    winSound = MediaPlayer.create(GameActivity.this, R.raw.win_sound1);
                    loseSound = MediaPlayer.create(GameActivity.this, R.raw.lose_sound);
                    break;
                case 2:
                    winSound = MediaPlayer.create(GameActivity.this, R.raw.win_sound2);
                    loseSound = MediaPlayer.create(GameActivity.this, R.raw.lose_sound2);
                    break;
                case 3:
                    winSound = MediaPlayer.create(GameActivity.this, R.raw.win_sound3);
                    loseSound = MediaPlayer.create(GameActivity.this, R.raw.lose_sound2);
                    break;
                case 4:
                    winSound = MediaPlayer.create(GameActivity.this, R.raw.win_sound1);
                    loseSound = MediaPlayer.create(GameActivity.this, R.raw.lose_sound1);
                    loseSound.setLooping(true);
                    break;
            }
        }
        adapter = new WordAdapter(GameActivity.this, w);
        switch (GoodPrefs.getInstance().getInt("executionImage", 0)) {
            case 0:
                binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope1));
                break;
            case 1:
                binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_p1));
                break;
            case 2:
                binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_pp1));
                break;
            case 3:
                binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_ppp1));
                break;
            case 4:
                binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_pppp1));
                break;

        }
        binding.coins.setText(String.valueOf(GoodPrefs.getInstance().getInt("coin", 0)));
        binding.goHome.setOnClickListener(view -> {
            clickSound.start();
            startActivity(new Intent(GameActivity.this, MainActivity.class));
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
        binding.goHelp.setOnClickListener(view -> {
            Dialog d = new Dialog(GameActivity.this);
            d.setContentView(R.layout.help_dialog);
            d.getWindow().setLayout(displayMetrics.widthPixels, (int) (displayMetrics.heightPixels * 4 / 10));
            d.findViewById(R.id.showWord).setOnClickListener(view1 -> {
                clickSound.start();
                if (GoodPrefs.getInstance().getInt("coin", 0) >= 30) {
                    showCoinChanges(30);
                    adapter.visiblePaidWord();
                    GoodPrefs.getInstance().saveInt("coin", GoodPrefs.getInstance().getInt("coin", 0) - 30);
                    binding.coins.setText(String.valueOf(GoodPrefs.getInstance().getInt("coin", 0)));
                    if (adapter.checkWinStatement()) {
                        winStatement(false,false);
                    }
                    d.dismiss();
                }
            });
            d.findViewById(R.id.delete5words).setOnClickListener(view1 -> {
                clickSound.start();
                if (GoodPrefs.getInstance().getInt("coin", 0) >= 15) {
                    GoodPrefs.getInstance().saveInt("coin", GoodPrefs.getInstance().getInt("coin", 0) - 15);
                    binding.coins.setText(String.valueOf(GoodPrefs.getInstance().getInt("coin", 0)));
                    deleteFiveLetters();
                    d.dismiss();
                }
            });
            d.findViewById(R.id.passLevel).setOnClickListener(view1 -> {
                clickSound.start();
                if (GoodPrefs.getInstance().getInt("coin", 0) >= 70) {
                    d.dismiss();
                    GoodPrefs.getInstance().saveInt("coin", GoodPrefs.getInstance().getInt("coin", 0) - 70);
                    winStatement(true,false);
                    adapter.visibleMissingWord();
                }
            });
            d.show();
        });
        binding.wordRec.setLayoutManager(new LinearLayoutManager(GameActivity.this, RecyclerView.HORIZONTAL, true));
        binding.wordRec.setAdapter(adapter);
        binding.wordCategory.setText(w.getWordCategory());
        adapter.notifyDataSetChanged();
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
            view.setBackground(ContextCompat.getDrawable(GameActivity.this, R.drawable.tick));
            if (adapter.checkWinStatement()) {
                winStatement(false,false);
            }
        } else {
            countMisses++;
            if (countMisses == 10) {
                failLayout.show();
                try {
                    loseSound.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                switch (GoodPrefs.getInstance().getInt("executionImage", 0)) {
                    case 0:
                        Glide.with(GameActivity.this).load(R.drawable.theme1_lose).into(binding.executionRope);
                        break;
                    case 1:
                        Glide.with(GameActivity.this).load(R.drawable.theme2_lose).into(binding.executionRope);
                        break;
                    case 2:
                        Glide.with(GameActivity.this).load(R.drawable.theme3_lose).into(binding.executionRope);
                        break;
                    case 3:
                        Glide.with(GameActivity.this).load(R.drawable.theme4_lose).into(binding.executionRope);
                        break;
                    case 4:
                        Glide.with(GameActivity.this).load(R.drawable.theme5_lose).into(binding.executionRope);
                        break;
                }
                adapter.visibleMissingWord();
                countMisses = 10;
                Tapsell.requestAd(GameActivity.this,
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
                    Tapsell.showAd(GameActivity.this,
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
                                        if (GoodPrefs.getInstance().getString("type", "").equals("normal")) {
                                            GoodPrefs.getInstance().saveInt("coin", GoodPrefs.getInstance().getInt("coin", 0) + 1);
                                            GoodPrefs.getInstance().saveInt("roundWord", GoodPrefs.getInstance().getInt("roundWord", 1) + 1);
                                            if (GoodPrefs.getInstance().getInt("roundWord", 1) > GoodPrefs.getInstance().getInt("recordWord", 0)) {
                                                GoodPrefs.getInstance().saveInt("recordWord", GoodPrefs.getInstance().getInt("roundWord", 1) - 1);
                                            }
                                        } else {
                                            GoodPrefs.getInstance().saveInt("coin", GoodPrefs.getInstance().getInt("coin", 0) + 3);
                                            GoodPrefs.getInstance().saveInt("roundWord3", GoodPrefs.getInstance().getInt("roundWord3", 1) + 1);
                                            if (GoodPrefs.getInstance().getInt("roundWord3", 1) > GoodPrefs.getInstance().getInt("recordWord3", 0)) {
                                                GoodPrefs.getInstance().saveInt("recordWord3", GoodPrefs.getInstance().getInt("roundWord3", 0) - 1);
                                            }
                                        }
                                        w.delete();
                                        Toast.makeText(GameActivity.this, "شانس دوباره گرفتی!", Toast.LENGTH_LONG).show();
                                    } else {
                                        AsyncTask.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (GoodPrefs.getInstance().getString("type", "").equals("normal")) {
                                                    importNormalWords();
                                                    GoodPrefs.getInstance().saveInt("roundWord", 1);
                                                } else {
                                                    importWord3();
                                                    GoodPrefs.getInstance().saveInt("roundWord3", 1);
                                                }
                                            }
                                        });
                                    }
                                    startActivity(new Intent(GameActivity.this, GameActivity.class));
                                    finish();
                                }
                            });
                });
            } else if (countMisses <= 9) {
                try {
                    incorrectSound.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                view.setBackground(ContextCompat.getDrawable(GameActivity.this, R.drawable.cancel));
                checkExecutionPic(countMisses);
            }
        }

        view.setClickable(false);
    }

    public void winStatement(boolean isPayed, boolean isPayedAtTheEnd) {
        winLayout.show();
        winAnimation();
        if (isPayed) {
            adapter.visibleMissingWord();
        }
        new Handler().post(() -> {
            if (GoodPrefs.getInstance().getString("type", "").equals("normal")) {
                GoodPrefs.getInstance().saveInt("coin", GoodPrefs.getInstance().getInt("coin", 0) + 1);
                GoodPrefs.getInstance().saveInt("roundWord", GoodPrefs.getInstance().getInt("roundWord", 1) + 1);
                if (GoodPrefs.getInstance().getInt("roundWord", 1) > GoodPrefs.getInstance().getInt("recordWord", 0)) {
                    GoodPrefs.getInstance().saveInt("recordWord", GoodPrefs.getInstance().getInt("roundWord", 1) - 1);
                }
            } else {
                GoodPrefs.getInstance().saveInt("coin", GoodPrefs.getInstance().getInt("coin", 0) + 3);
                GoodPrefs.getInstance().saveInt("roundWord3", GoodPrefs.getInstance().getInt("roundWord3", 1) + 1);
                if (GoodPrefs.getInstance().getInt("roundWord3", 1) > GoodPrefs.getInstance().getInt("recordWord3", 0)) {
                    GoodPrefs.getInstance().saveInt("recordWord3", GoodPrefs.getInstance().getInt("roundWord3", 0) - 1);
                }
            }
            if (countMisses == 0) {
                GoodPrefs.getInstance().saveInt("noMistake", GoodPrefs.getInstance().getInt("noMistake", 0) + 1);
            }
            w.delete();
        });
        if (!isPayedAtTheEnd) {
            switch (GoodPrefs.getInstance().getInt("executionImage", 0)) {
                case 0:
                    Glide.with(GameActivity.this).load(R.drawable.theme1_win).into(binding.executionRope);
                    break;
                case 1:
                    Glide.with(GameActivity.this).load(R.drawable.theme2_win).into(binding.executionRope);
                    break;
                case 2:
                    Glide.with(GameActivity.this).load(R.drawable.theme3_win).into(binding.executionRope);
                    break;
                case 3:
                    Glide.with(GameActivity.this).load(R.drawable.theme4_win).into(binding.executionRope);
                    break;
                case 4:
                    Glide.with(GameActivity.this).load(R.drawable.theme5_win).into(binding.executionRope);
                    break;
            }
            winLayout.show();
            winAnimation();
            try {
                winSound.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                loseSound.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            startActivity(new Intent(GameActivity.this, GameActivity.class));
            Toast.makeText(this, "شانس دوباره گرفتی!", Toast.LENGTH_LONG).show();
            finish();
        }
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
        ((LinearLayout) winLayout.findViewById(R.id.aivaldari)).getChildAt(0).startAnimation(AnimationUtils.loadAnimation(GameActivity.this, R.anim.bounce_left));
        ((LinearLayout) winLayout.findViewById(R.id.aivaldari)).getChildAt(1).startAnimation(AnimationUtils.loadAnimation(GameActivity.this, R.anim.bounce_right));
    }

    public void checkExecutionPic(int which) {
        switch (GoodPrefs.getInstance().getInt("executionImage", 0)) {
            case 0:
                switch (which) {
                    case 1:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope2));
                        break;
                    case 2:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope3));
                        break;
                    case 3:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope4));
                        break;
                    case 4:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope5));
                        break;
                    case 5:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope6));
                        break;
                    case 6:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope7));
                        break;
                    case 7:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope8));
                        break;
                    case 8:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope9));
                        break;
                    case 9:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope10));
                        break;
                }
                break;
            case 1:
                switch (which) {
                    case 1:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_p2));
                        break;
                    case 2:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_p3));
                        break;
                    case 3:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_p4));
                        break;
                    case 4:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_p5));
                        break;
                    case 5:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_p6));
                        break;
                    case 6:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_p7));
                        break;
                    case 7:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_p8));
                        break;
                    case 8:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_p9));
                        break;
                    case 9:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_p10));
                        break;
                }
                break;
            case 2:
                switch (which) {
                    case 1:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_pp2));
                        break;
                    case 2:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_pp3));
                        break;
                    case 3:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_pp4));
                        break;
                    case 4:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_pp5));
                        break;
                    case 5:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_pp6));
                        break;
                    case 6:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_pp7));
                        break;
                    case 7:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_pp8));
                        break;
                    case 8:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_pp9));
                        break;
                    case 9:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_pp10));
                        break;
                }
                break;
            case 3:
                switch (which) {
                    case 1:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_ppp2));
                        break;
                    case 2:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_ppp3));
                        break;
                    case 3:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_ppp4));
                        break;
                    case 4:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_ppp5));
                        break;
                    case 5:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_ppp6));
                        break;
                    case 6:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_ppp7));
                        break;
                    case 7:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_ppp8));
                        break;
                    case 8:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_ppp9));
                        break;
                    case 9:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_ppp10));
                        break;
                }
                break;
            case 4:
                switch (which) {
                    case 1:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_pppp2));
                        break;
                    case 2:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_pppp3));
                        break;
                    case 3:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_pppp4));
                        break;
                    case 4:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_pppp5));
                        break;
                    case 5:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_pppp6));
                        break;
                    case 6:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_pppp7));
                        break;
                    case 7:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_pppp8));
                        break;
                    case 8:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_pppp9));
                        break;
                    case 9:
                        binding.executionRope.setImageDrawable(ContextCompat.getDrawable(GameActivity.this, R.drawable.rope_pppp10));
                        break;
                }
                break;
        }
    }

    public void playAgain(View v) {
        if (GoodPrefs.getInstance().getString("type", "").equals("normal")) {
                importNormalWords();
                GoodPrefs.getInstance().saveInt("roundWord", 1);
            } else {
                importWord3();
                GoodPrefs.getInstance().saveInt("roundWord3", 1);
            }
        Log.e("KIR",GoodPrefs.getInstance().getInt("roundWord", 1)+"");
        try {
            winSound.stop();
            loseSound.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(GameActivity.this, GameActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void deleteFiveLetters() {
        ArrayList<Letter> letters = new ArrayList<>();
        for (int i = 0; i < binding.lettersLayout.getChildCount(); i++) {
            LinearLayout ln = (LinearLayout) binding.lettersLayout.getChildAt(i);
            for (int j = 0; j < ln.getChildCount(); j++) {
                if (ln.getChildAt(j).isClickable() && !w.getWordName().contains(((TextView) ln.getChildAt(j)).getText().toString())) {
                    letters.add(new Letter(i, j));
                }
            }
        }
        if (!letters.isEmpty()) {
            int size = 5;
            if (letters.size() < 5) {
                size = letters.size();
            }
            for (int i = 0; i < size; i++) {
                int random = new Random().nextInt(letters.size());
                ((LinearLayout) binding.lettersLayout.getChildAt(letters.get(random).getRow())).getChildAt(letters.get(random).getCol()).setBackground(getResources().getDrawable(R.drawable.cancel));
                ((LinearLayout) binding.lettersLayout.getChildAt(letters.get(random).getRow())).getChildAt(letters.get(random).getCol()).setClickable(false);
                letters.remove(random);
            }
        }
    }

    public void getNormalWord() {
        List<WordDb> list = WordDb.listAll(WordDb.class);
        List<WordDb> normalWords = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getWordName().length() > 3) {
                normalWords.add(list.get(i));
            }
        }
        if (list.isEmpty()) {
            Toast.makeText(GameActivity.this, "بابا دمت گرم دیتابیس ما ته کشید از اول برو حال کن.", Toast.LENGTH_SHORT).show();
            GoodPrefs.getInstance().saveInt("roundWord3", 0);
            importNormalWords();
            list = WordDb.listAll(WordDb.class);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getWordName().length() > 3) {
                    normalWords.add(list.get(i));
                }
            }
        }
        randomWord = new Random().nextInt(normalWords.size());
        w = normalWords.get(randomWord);

    }

    public void getWord3() {
        List<WordDb> list = WordDb.listAll(WordDb.class);
        List<WordDb> word3 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getWordName().length() == 3) {
                word3.add(list.get(i));
            }
        }
        if (word3.isEmpty()) {
            Toast.makeText(GameActivity.this, "بابا دمت گرم دیتابیس ما ته کشید از اول برو حال کن.", Toast.LENGTH_SHORT).show();
            GoodPrefs.getInstance().saveInt("roundWord3", 0);
            importWord3();
            list = WordDb.listAll(WordDb.class);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getWordName().length() == 3) {
                    word3.add(list.get(i));
                }
            }
        }
        randomWord = new Random().nextInt(word3.size());
        w = word3.get(randomWord);
    }

    public void importNormalWords() {
        AsyncTask.execute(() -> {
            List<WordDb> list = WordDb.listAll(WordDb.class);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getWordName().length() > 3) {
                    list.get(i).delete();
                }
            }
            List<String> words = Arrays.asList(getResources().getStringArray(R.array.words));
            List<String> categories = Arrays.asList(getResources().getStringArray(R.array.word_category));
            for (int i = 0; i < words.size(); i++) {
                WordDb word = new WordDb();
                word.setWordCategory(categories.get(i));
                word.setWordName(words.get(i));
                word.save();
            }
        });
    }

    public void importWord3() {
        AsyncTask.execute(()->{
            List<WordDb> list = WordDb.listAll(WordDb.class);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getWordName().length() == 3) {
                    list.get(i).delete();
                }
            }
            List<String> categories = Arrays.asList(getResources().getStringArray(R.array.word3_category));
            List<String> words = Arrays.asList(getResources().getStringArray(R.array.word3));
            for (int i = 0; i < words.size(); i++) {
                WordDb word = new WordDb();
                word.setWordCategory(categories.get(i));
                word.setWordName(words.get(i));
                word.save();
            }
        });
    }

    public void showCoinChanges(int price) {
        Dialog d = new Dialog(GameActivity.this);
        d.setContentView(R.layout.coin_changes);
        TextView tv = d.findViewById(R.id.coinChangesPrice);
        tv.setText("" + price);
        d.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        d.show();
        Handler h = new Handler();
        Runnable r = () -> d.dismiss();
        h.postDelayed(r, 500);
    }
}
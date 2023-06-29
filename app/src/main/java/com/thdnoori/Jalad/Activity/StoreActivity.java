package com.thdnoori.Jalad.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thdnoori.Jalad.Adapter.PackAdapter;
import com.thdnoori.Jalad.Database.Pack;
import com.thdnoori.Jalad.Model.GoodPrefs;
import com.thdnoori.Jalad.R;
import com.thdnoori.Jalad.databinding.ActivityStoreBinding;

import java.util.List;
import java.util.Locale;

import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellAdRequestListener;
import ir.tapsell.sdk.TapsellAdRequestOptions;
import ir.tapsell.sdk.TapsellAdShowListener;
import ir.tapsell.sdk.TapsellShowOptions;

public class StoreActivity extends AppCompatActivity {
    ActivityStoreBinding binding;
    long timeLeft;
    CountDownTimer cdt;
    String adId;
    PackAdapter packAdapter;
    List<Pack> packList;
    MediaPlayer backgroundMusic, clickSound;

    @Override
    protected void onPause() {
        super.onPause();
        if (GoodPrefs.getInstance().getBoolean("soundEnable", true)) {
            backgroundMusic.stop();
            GoodPrefs.getInstance().saveInt("musicPosition", backgroundMusic.getCurrentPosition());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (GoodPrefs.getInstance().getBoolean("soundEnable", true)) {
            if(GoodPrefs.getInstance().getInt("appMusic",0)==0){
                backgroundMusic = MediaPlayer.create(StoreActivity.this, R.raw.app_music);
            }else {
                backgroundMusic = MediaPlayer.create(StoreActivity.this, R.raw.app_music1);
            }
            backgroundMusic.seekTo(GoodPrefs.getInstance().getInt("musicPosition", 0));
            backgroundMusic.setLooping(true);
            backgroundMusic.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStoreBinding.inflate(getLayoutInflater());
        if (GoodPrefs.getInstance().getBoolean("soundEnable", true)) {
            if(GoodPrefs.getInstance().getInt("appMusic",0)==0){
                backgroundMusic = MediaPlayer.create(StoreActivity.this, R.raw.app_music);
            }else {
                backgroundMusic = MediaPlayer.create(StoreActivity.this, R.raw.app_music1);
            }            backgroundMusic.seekTo(GoodPrefs.getInstance().getInt("musicPosition", 0));
            backgroundMusic.setLooping(true);
            backgroundMusic.start();
        }
        setContentView(binding.getRoot());
        clickSound = MediaPlayer.create(StoreActivity.this, R.raw.click);
        packList = Pack.listAll(Pack.class);
        binding.myAllCoin.setText("×" + GoodPrefs.getInstance().getInt("coin", 0) + "×");
        TapsellAdRequestOptions options = new TapsellAdRequestOptions();
        options.setCacheType(TapsellAdRequestOptions.CACHE_TYPE_CACHED);
        Tapsell.requestAd(StoreActivity.this,
                "5f6f5c677b431900012ce286",
                options,
                new TapsellAdRequestListener() {
                    @Override
                    public void onAdAvailable(String adid) {
                        adId = adid;
                        binding.watchAd.setEnabled(true);
                        binding.watchAd.setBackgroundColor(Color.parseColor("#00BFA5"));
                    }

                    @Override
                    public void onError(String message) {
                    }
                });

        binding.watchAd.setOnClickListener(view -> {
            clickSound.start();

            Tapsell.showAd(StoreActivity.this,
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
                                GoodPrefs.getInstance().saveInt("coin", GoodPrefs.getInstance().getInt("coin", 0) + 20);
                                binding.myAllCoin.setText("×" + GoodPrefs.getInstance().getInt("coin", 0) + "×");
                                GoodPrefs.getInstance().saveInt("adRecord", GoodPrefs.getInstance().getInt("adRecord", 0) + 1);
                            }
                        }
                    });
        });
        packAdapter = new PackAdapter(StoreActivity.this, packList);
        binding.packRec.setLayoutManager(new LinearLayoutManager(StoreActivity.this, RecyclerView.VERTICAL, false));
        binding.packRec.setAdapter(packAdapter);
        timeLeft = GoodPrefs.getInstance().getLong("triggerTime", 0) - System.currentTimeMillis();
        if (timeLeft > 0) {
            try {
                cdt = new CountDownTimer(timeLeft, 1000) {
                    @Override
                    public void onTick(long l) {
                        int minute = (int) ((l / 1000) / 60) % 60;
                        int second = (int) (l / 1000) % 60;
                        int hour = (int) (l / 1000) / 3600;
                        String timeFormatted;
                        if (hour > 0) {
                            timeFormatted = String.format(Locale.getDefault(),
                                    "%d:%02d:%02d", hour, minute, second);
                        } else {
                            timeFormatted = String.format(Locale.getDefault(),
                                    "%02d:%02d", minute, second);
                        }
                        binding.goWheelTv.setText(timeFormatted);
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onFinish() {
                        binding.goWheelTv.setText("چرخه سکه");
                    }
                }.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        binding.luckyWheelStore.setVisibility(View.VISIBLE);
        binding.goWheel.setOnClickListener(view ->
        {
            clickSound.start();
            if (timeLeft < 0) {
                startActivity(new Intent(StoreActivity.this, WheelActivity.class));
                finish();
            }
        });
    }


}
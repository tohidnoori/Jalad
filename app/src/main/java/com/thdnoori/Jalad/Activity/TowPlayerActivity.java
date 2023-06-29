package com.thdnoori.Jalad.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.thdnoori.Jalad.Adapter.TowPlayerWordAdapter;
import com.thdnoori.Jalad.Adapter.WordAdapter;
import com.thdnoori.Jalad.Database.WordDb;
import com.thdnoori.Jalad.Model.GoodPrefs;
import com.thdnoori.Jalad.R;
import com.thdnoori.Jalad.databinding.ActivityTowPlayerBinding;
import com.thdnoori.Jalad.databinding.EnterWord2playerBinding;

import java.util.Locale;
import java.util.Random;


public class TowPlayerActivity extends AppCompatActivity {
    TowPlayerWordAdapter dialogAdapter;
    WordAdapter adapter;
    private ActivityTowPlayerBinding binding;
    private EnterWord2playerBinding dialogBinding;
    MediaPlayer correctSound, incorrectSound, clickSound, winSound, loseSound;
    WordDb w;
    Dialog winLayout, failLayout,d;
    int countMisses = 0;
    DisplayMetrics displayMetrics = new DisplayMetrics();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        winLayout = new BottomSheetDialog(TowPlayerActivity.this);
        winLayout.setContentView(R.layout.win_dailog);
        winLayout.setCancelable(false);
        winLayout = new Dialog(TowPlayerActivity.this);
        winLayout.setContentView(R.layout.win_dailog);
        winLayout.getWindow().setGravity(Gravity.BOTTOM);
        winLayout.getWindow().setLayout(displayMetrics.widthPixels, (int) (displayMetrics.heightPixels * 4.3 / 10));
        winLayout.setCancelable(false);
        failLayout = new Dialog(TowPlayerActivity.this);
        failLayout.setContentView(R.layout.fail_dailog);
        failLayout.getWindow().setGravity(Gravity.BOTTOM);
        failLayout.findViewById(R.id.payMoneyChance).setVisibility(View.GONE);
        failLayout.findViewById(R.id.watchAd).setVisibility(View.GONE);
        failLayout.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) (displayMetrics.heightPixels * 4.3 / 10));
        failLayout.setCancelable(false);
        dialogBinding = EnterWord2playerBinding.inflate(getLayoutInflater());
        dialogInit();
        binding = ActivityTowPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.goHomeTowPlayer.setOnClickListener(view -> {
            startActivity(new Intent(TowPlayerActivity.this, MainActivity.class));
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
        d.setCancelable(false);
        d.show();

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
            view.setBackground(ContextCompat.getDrawable(TowPlayerActivity.this, R.drawable.tick));
            if (adapter.checkWinStatement()) {
                Glide.with(TowPlayerActivity.this).load(R.drawable.theme1_win).into(binding.executionRopeTowPlayer);
                winLayout.show();
                winLayout.findViewById(R.id.winLayoutPlayNext).setOnClickListener(view1 -> {
                    Log.i("kir","kir");
                    try {
                        winSound.stop();
                        loseSound.stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(TowPlayerActivity.this, TowPlayerActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                });
                winAnimation();
                try {
                    winSound.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                incorrectSound.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            view.setBackground(ContextCompat.getDrawable(TowPlayerActivity.this, R.drawable.cancel));
            countMisses++;
            checkExecutionPic(countMisses);
            if (countMisses > 9) {
                adapter.visibleMissingWord();
                Glide.with(TowPlayerActivity.this).load(R.drawable.theme1_lose).into(binding.executionRopeTowPlayer);
                failLayout.show();
                try {
                    loseSound.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        view.setClickable(false);
    }

    public void dialogCheck(View view) {
        clickSound.start();
        String x = ((TextView) view).getText().toString();
        dialogAdapter.showEnteredWord(x);
    }

    public void checkExecutionPic(int which) {
        switch (which) {
            case 1:
                binding.executionRopeTowPlayer.setImageDrawable(ContextCompat.getDrawable(TowPlayerActivity.this, R.drawable.rope2));
                break;
            case 2:
                binding.executionRopeTowPlayer.setImageDrawable(ContextCompat.getDrawable(TowPlayerActivity.this, R.drawable.rope3));
                break;
            case 3:
                binding.executionRopeTowPlayer.setImageDrawable(ContextCompat.getDrawable(TowPlayerActivity.this, R.drawable.rope4));
                break;
            case 4:
                binding.executionRopeTowPlayer.setImageDrawable(ContextCompat.getDrawable(TowPlayerActivity.this, R.drawable.rope5));
                break;
            case 5:
                binding.executionRopeTowPlayer.setImageDrawable(ContextCompat.getDrawable(TowPlayerActivity.this, R.drawable.rope6));
                break;
            case 6:
                binding.executionRopeTowPlayer.setImageDrawable(ContextCompat.getDrawable(TowPlayerActivity.this, R.drawable.rope7));
                break;
            case 7:
                binding.executionRopeTowPlayer.setImageDrawable(ContextCompat.getDrawable(TowPlayerActivity.this, R.drawable.rope8));
                break;
            case 8:
                binding.executionRopeTowPlayer.setImageDrawable(ContextCompat.getDrawable(TowPlayerActivity.this, R.drawable.rope9));
                break;
            case 9:
                binding.executionRopeTowPlayer.setImageDrawable(ContextCompat.getDrawable(TowPlayerActivity.this, R.drawable.rope10));
                break;
        }
    }

    public void dialogInit() {
        clickSound = MediaPlayer.create(TowPlayerActivity.this, R.raw.click);
        d = new Dialog(this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        d.setContentView(dialogBinding.getRoot());
        WordDb w1 = new WordDb();
        w1.setWordName("             ");
        w1.setWordCategory("");
        dialogAdapter = new TowPlayerWordAdapter(TowPlayerActivity.this, w1);
        dialogBinding.selectWord.setAdapter(dialogAdapter);
        if (Locale.getDefault().getDisplayLanguage().equals("فارسی")) {
            dialogBinding.selectWord.setLayoutManager(new LinearLayoutManager(TowPlayerActivity.this, RecyclerView.HORIZONTAL, false));
        }else{
            dialogBinding.selectWord.setLayoutManager(new LinearLayoutManager(TowPlayerActivity.this, RecyclerView.HORIZONTAL, true));
        }
        dialogBinding.deleteWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAdapter.deleteLastWord();
                clickSound.start();
            }
        });
        dialogBinding.eraseWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSound.start();
                dialogAdapter.eraseAll();
            }
        });
        dialogBinding.goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSound.start();
                startActivity(new Intent(TowPlayerActivity.this, MainActivity.class));
                finish();
            }
        });
        dialogBinding.start2PlayerGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSound.start();
                if (!dialogAdapter.getFinalWord().isEmpty()){
                    w = new WordDb();
                    w.setWordName(dialogAdapter.getFinalWord());
                    if (GoodPrefs.getInstance().getBoolean("soundEnable", true)) {
                        correctSound = MediaPlayer.create(TowPlayerActivity.this, R.raw.correct);
                        incorrectSound = MediaPlayer.create(TowPlayerActivity.this, R.raw.incorrect);
                        winSound = MediaPlayer.create(TowPlayerActivity.this, R.raw.win_sound);
                        loseSound = MediaPlayer.create(TowPlayerActivity.this, R.raw.lose_sound);
                    }
                    adapter = new WordAdapter(TowPlayerActivity.this, w);
                    if (Locale.getDefault().getDisplayLanguage().equals("فارسی")) {
                        binding.wordRecTowPlayer.setLayoutManager(new LinearLayoutManager(TowPlayerActivity.this, RecyclerView.HORIZONTAL, false));
                    } else {
                        binding.wordRecTowPlayer.setLayoutManager(new LinearLayoutManager(TowPlayerActivity.this, RecyclerView.HORIZONTAL, true));
                    }
                    binding.wordRecTowPlayer.setAdapter(adapter);
                    d.dismiss();
                }
            }
        });
    }

    public void playAgain(View v) {
        Intent intent = new Intent(TowPlayerActivity.this, TowPlayerActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
        ((LinearLayout) winLayout.findViewById(R.id.aivaldari)).getChildAt(0).startAnimation(AnimationUtils.loadAnimation(TowPlayerActivity.this, R.anim.bounce_left));
        ((LinearLayout) winLayout.findViewById(R.id.aivaldari)).getChildAt(1).startAnimation(AnimationUtils.loadAnimation(TowPlayerActivity.this, R.anim.bounce_right));
    }

}
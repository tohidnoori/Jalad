package com.thdnoori.Jalad.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.thdnoori.Jalad.Model.GoodPrefs;
import com.thdnoori.Jalad.R;
import com.thdnoori.Jalad.databinding.ActivitySettingBinding;

public class SettingActivity extends AppCompatActivity {
    ActivitySettingBinding binding;
    MediaPlayer  clickSound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        GoodPrefs.init(SettingActivity.this);
        clickSound = MediaPlayer.create(SettingActivity.this, R.raw.click);
        if (GoodPrefs.getInstance().getInt("appMusic",0)==0){
            binding.musicTheme1.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.bg_choose_circle));
        }else{
            binding.musicTheme2.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.bg_choose_circle));
        }
        binding.musicTheme1.setOnClickListener(view ->{
            clickSound.start();
            binding.musicTheme1.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.bg_choose_circle));
            GoodPrefs.getInstance().saveInt("appMusic",0);
            GoodPrefs.getInstance().saveInt("musicPosition", 0);
            binding.musicTheme2.setBackgroundColor(Color.parseColor("#00000000"));
        });
        binding.musicTheme2.setOnClickListener(view ->{
            clickSound.start();
            binding.musicTheme2.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.bg_choose_circle));
            GoodPrefs.getInstance().saveInt("appMusic",1);
            GoodPrefs.getInstance().saveInt("musicPosition", 0);
            binding.musicTheme1.setBackgroundColor(Color.parseColor("#00000000"));
        });
        binding.soundSwitch.setChecked(GoodPrefs.getInstance().getBoolean("soundEnable",true));
        binding.soundSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            clickSound.start();
            GoodPrefs.getInstance().saveBoolean("soundEnable",isChecked);
            GoodPrefs.getInstance().saveInt("musicPosition", 0);
        });
        binding.rateUs.setOnClickListener(view ->{
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.thdnoori.Jalad")));
        });

    }
}
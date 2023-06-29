package com.thdnoori.Jalad.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.thdnoori.Jalad.Model.GoodPrefs;
import com.thdnoori.Jalad.Model.LuckyItem;
import com.thdnoori.Jalad.Model.LuckyWheelView;
import com.thdnoori.Jalad.R;
import com.thdnoori.Jalad.databinding.ActivityWheelBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class WheelActivity extends AppCompatActivity {
    ActivityWheelBinding binding;
    List<LuckyItem> luckyItems;
    LuckyItem luckyItem;
    int winIndex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoodPrefs.init(WheelActivity.this);
        binding = ActivityWheelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        generateItems();
        binding.luckyWheelView.setTouchEnabled(false);
        binding.rollWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random r = new Random();
                binding.luckyWheelView.startLuckyWheelWithTargetIndex(r.nextInt(luckyItems.size()) - 1);
            }
        });
        binding.luckyWheelView.setLuckyRoundItemSelectedListener(new LuckyWheelView.LuckyRoundItemSelectedListener() {
            @Override
            public void LuckyRoundItemSelected(int index) {
                binding.rollWheel.setVisibility(View.GONE);
                winIndex = index;
                binding.coinEarnLayout.setVisibility(View.VISIBLE);
                binding.lkwTvPraise.setText("جایزه ی شما "+luckyItems.get(index).topText +" سکه است" );
                GoodPrefs.getInstance().saveInt("coin", GoodPrefs.getInstance().getInt("coin", 0) + Integer.parseInt(luckyItems.get(winIndex).topText));
                GoodPrefs.getInstance().saveLong("triggerTime",System.currentTimeMillis() + 14400000);
                GoodPrefs.getInstance().saveInt("wheelRecord",GoodPrefs.getInstance().getInt("wheelRecord",0)+1);
            }
        });

        binding.lkwGetPraise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WheelActivity.this,StoreActivity.class));
                finish();
            }
        });
    }

    private void generateItems() {
        luckyItems = new ArrayList<>();
        luckyItem = new LuckyItem("10", R.drawable.little_coin, Color.parseColor("#E84820"));
        luckyItems.add(luckyItem);
        luckyItem = new LuckyItem("15", R.drawable.little_coin, Color.parseColor("#FA872E"));
        luckyItems.add(luckyItem);
        luckyItem = new LuckyItem("30", R.drawable.little_coin, Color.parseColor("#F4E078"));
        luckyItems.add(luckyItem);
        luckyItem = new LuckyItem("5", R.drawable.little_coin, Color.parseColor("#E84820"));
        luckyItems.add(luckyItem);
        luckyItem = new LuckyItem("20", R.drawable.little_coin, Color.parseColor("#AF20F6"));
        luckyItems.add(luckyItem);
        luckyItem = new LuckyItem("12", R.drawable.little_coin, Color.parseColor("#00B8D4"));
        luckyItems.add(luckyItem);
        luckyItem = new LuckyItem("5", R.drawable.little_coin, Color.parseColor("#E84820"));
        luckyItems.add(luckyItem);
        luckyItem = new LuckyItem("25", R.drawable.little_coin, Color.parseColor("#AEEA00"));
        luckyItems.add(luckyItem);
        luckyItem = new LuckyItem("12", R.drawable.little_coin, Color.parseColor("#00B8D4"));
        luckyItems.add(luckyItem);
        luckyItem = new LuckyItem("10", R.drawable.little_coin, Color.parseColor("#E84820"));
        luckyItems.add(luckyItem);
        luckyItem = new LuckyItem("20", R.drawable.little_coin, Color.parseColor("#AF20F6"));
        luckyItems.add(luckyItem);
        luckyItem = new LuckyItem("12", R.drawable.little_coin, Color.parseColor("#00B8D4"));
        luckyItems.add(luckyItem);
        luckyItem = new LuckyItem("10", R.drawable.little_coin, Color.parseColor("#E84820"));
        luckyItems.add(luckyItem);
        luckyItem = new LuckyItem("25", R.drawable.little_coin, Color.parseColor("#AEEA00"));
        luckyItems.add(luckyItem);
        luckyItem = new LuckyItem("15", R.drawable.little_coin, Color.parseColor("#FA872E"));
        luckyItems.add(luckyItem);
        luckyItem = new LuckyItem("10", R.drawable.little_coin, Color.parseColor("#E84820"));
        luckyItems.add(luckyItem);
        luckyItem = new LuckyItem("12", R.drawable.little_coin, Color.parseColor("#00B8D4"));
        luckyItems.add(luckyItem);
        luckyItem = new LuckyItem("15", R.drawable.little_coin, Color.parseColor("#FA872E"));
        luckyItems.add(luckyItem);
        binding.luckyWheelView.setData(luckyItems);
        binding.luckyWheelView.setRound(12);

    }
}
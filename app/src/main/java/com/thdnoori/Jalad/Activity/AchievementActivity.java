package com.thdnoori.Jalad.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thdnoori.Jalad.Adapter.AchievementAdapter;
import com.thdnoori.Jalad.Database.Achievement;
import com.thdnoori.Jalad.Model.GoodPrefs;
import com.thdnoori.Jalad.databinding.ActivityAchievementBinding;

import java.util.List;

public class AchievementActivity extends AppCompatActivity {
    ActivityAchievementBinding binding;
    AchievementAdapter adapter;
    List<Achievement> achievementList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAchievementBinding.inflate(getLayoutInflater());
        achievementList = Achievement.listAll(Achievement.class);
        setContentView(binding.getRoot());
        for (int i =0 ; i<achievementList.size();i++){
            if (!achievementList.get(i).isOnce()){
                if (GoodPrefs.getInstance().getInt(achievementList.get(i).getPerfName(),0)>=achievementList.get(i).getRecord()) {
                    achievementList.get(i).setReady(true);
                    achievementList.get(i).save();
                }
            }else{
                if (GoodPrefs.getInstance().getBoolean(achievementList.get(i).getPerfName(), false)) {
                    achievementList.get(i).setReady(true);
                    achievementList.get(i).save();
                }
            }
            }
        adapter = new AchievementAdapter(AchievementActivity.this,achievementList);
        binding.achievementRec.setLayoutManager(new LinearLayoutManager(AchievementActivity.this, RecyclerView.VERTICAL,false));
        binding.achievementRec.setAdapter(adapter);
    }
}
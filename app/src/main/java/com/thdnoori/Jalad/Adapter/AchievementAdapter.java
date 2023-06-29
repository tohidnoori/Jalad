package com.thdnoori.Jalad.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thdnoori.Jalad.Database.Achievement;
import com.thdnoori.Jalad.Model.GoodPrefs;
import com.thdnoori.Jalad.R;

import java.util.List;


public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.MyView> {
    private Context activity;
    List<Achievement> achievementList;
    MediaPlayer clickSound;
    Dialog coinChanges;
    TextView coinChangesPrice;

    public AchievementAdapter(Context activity, List<Achievement> achievementList) {
        this.achievementList = achievementList;
        this.activity = activity;
        clickSound = MediaPlayer.create(activity, R.raw.click);
        coinChanges = new Dialog(activity);
        coinChanges.setContentView(R.layout.coin_changes);
        coinChangesPrice = coinChanges.findViewById(R.id.coinChangesPrice);
    }

    @NonNull
    @Override

    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_achievement, parent, false);
        return new MyView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, int position) {
        holder.name.setText(achievementList.get(position).getName());
        if (achievementList.get(position).isCompleted()) {
            holder.get.setText("تمام شده");
            holder.get.setBackgroundColor(Color.parseColor("#FF6D00"));
        } else if (achievementList.get(position).isReady()) {
            holder.get.setBackgroundColor(Color.parseColor("#BA46F4"));
            holder.get.setText(achievementList.get(position).getPrice() + " بگیر");
            holder.get.setOnClickListener(view -> {
                if (achievementList.get(position).isOnce() && !achievementList.get(position).isCompleted()) {
                    GoodPrefs.getInstance().saveInt("coin", GoodPrefs.getInstance().getInt("coin", 0) + achievementList.get(position).getPrice());
                    showCoinChanges(achievementList.get(position).getPrice());
                    achievementList.get(position).setCompleted(true);
                    achievementList.get(position).save();
                    notifyDataSetChanged();
                } else {
                    GoodPrefs.getInstance().saveInt("coin", GoodPrefs.getInstance().getInt("coin", 0) + achievementList.get(position).getPrice());
                    showCoinChanges(achievementList.get(position).getPrice());
                    achievementList.get(position).setPrice(achievementList.get(position).getPrice() + 5);
                    achievementList.get(position).setRecord(achievementList.get(position).getRecord() + 10);
                    achievementList.get(position).setReady(false);
                    achievementList.get(position).save();
                    myNotify(position);
                    notifyDataSetChanged();
                }
            });
        } else {
            holder.get.setBackgroundColor(Color.parseColor("#979E9D"));
            if (achievementList.get(position).isOnce()) {
                holder.get.setText("0 / 1");
            } else {
                int remaining = GoodPrefs.getInstance().getInt(achievementList.get(position).getPerfName(), 0);
                holder.get.setText(remaining + " / " + achievementList.get(position).getRecord());
            }
        }

    }

    @Override
    public int getItemCount() {
        return achievementList.size();
    }

    public class MyView extends RecyclerView.ViewHolder {
        TextView name, get;

        public MyView(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.achievementName);
            get = itemView.findViewById(R.id.achievementGet);
        }
    }

    public void myNotify(int i) {

        if (!achievementList.get(i).isOnce()) {
            if (GoodPrefs.getInstance().getInt(achievementList.get(i).getPerfName(), 0) >= achievementList.get(i).getRecord()) {
                achievementList.get(i).setReady(true);
                achievementList.get(i).save();
            }
        } else {
            if (GoodPrefs.getInstance().getBoolean(achievementList.get(i).getPerfName(), false)) {
                achievementList.get(i).setReady(true);
                achievementList.get(i).save();
            }
        }

    }

    public void showCoinChanges(int price){
        coinChangesPrice.setText("+"+price);
        coinChanges.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        coinChanges.show();
        Handler h= new Handler();
        Runnable r = () -> coinChanges.dismiss();
        h.postDelayed(r,1000);
    }
}

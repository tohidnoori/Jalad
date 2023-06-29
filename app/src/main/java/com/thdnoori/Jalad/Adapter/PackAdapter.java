package com.thdnoori.Jalad.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thdnoori.Jalad.Database.Pack;
import com.thdnoori.Jalad.Model.GoodPrefs;
import com.thdnoori.Jalad.R;

import java.util.List;


public class PackAdapter extends RecyclerView.Adapter<PackAdapter.MyView> {
    private Context activity;
    List<Pack> packList;
    MediaPlayer clickSound;
    public PackAdapter(Context activity, List<Pack> packList) {
        this.packList = packList;
        this.activity = activity;
        clickSound = MediaPlayer.create(activity, R.raw.click);
    }

    @NonNull
    @Override

    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pack, parent, false);
        return new MyView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, int position) {
        if (packList.get(position).isPurchased()) {
            holder.buyPack.setVisibility(View.GONE);
            holder.enablePack.setVisibility(View.VISIBLE);
            if (packList.get(position).isEnable()) {
                holder.enablePack.setBackgroundColor(Color.parseColor("#4ADCC8"));
                holder.enablePack.setText("فعال");
            } else {
                holder.enablePack.setBackgroundColor(Color.parseColor("#979E9D"));
                holder.enablePack.setText("فعال کن");
            }
        }
        holder.price.setText(String.valueOf(packList.get(position).getPrice()));
        holder.name.setText(packList.get(position).getName());
        holder.img.setBackground(activity.getResources().getDrawable(packList.get(position).getImageResourceID()));
        holder.buyPack.setOnClickListener(view -> {
            clickSound.start();
            if (GoodPrefs.getInstance().getInt("coin", 0) >= packList.get(position).getPrice()) {
                showCoinChanges(-packList.get(position).getPrice());
                GoodPrefs.getInstance().saveInt("coin", GoodPrefs.getInstance().getInt("coin", 0) - packList.get(position).getPrice());
                GoodPrefs.getInstance().saveBoolean(packList.get(position).getPerfName(),true);
                packList.get(position).setPurchased(true);
                packList.get(position).save();
                notifyDataSetChanged();
            }
        });
        holder.enablePack.setOnClickListener(view -> {
            clickSound.start();
            if (!packList.get(position).isEnable()) {
                GoodPrefs.getInstance().saveInt("executionImage", position);
                packList.get(position).setEnable(true);
                packList.get(position).save();
                for (int i = 0; i<packList.size();i++){
                    if (i != position){
                        packList.get(i).setEnable(false);
                        packList.get(i).save();
                    }
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return packList.size();
    }

    public class MyView extends RecyclerView.ViewHolder {
        TextView name, price, enablePack;
        ImageView img;
        LinearLayout buyPack;

        public MyView(@NonNull View itemView) {
            super(itemView);
            enablePack = itemView.findViewById(R.id.packEnable);
            buyPack = itemView.findViewById(R.id.buyPack);
            name = itemView.findViewById(R.id.packName);
            price = itemView.findViewById(R.id.packPrice);
            img = itemView.findViewById(R.id.packImage);
        }
    }
    public void showCoinChanges(int price){
        Dialog d = new Dialog(activity);
        d.setContentView(R.layout.coin_changes);
        TextView tv = d.findViewById(R.id.coinChangesPrice);
        tv.setText(""+price);
        d.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        d.show();
        Handler h= new Handler();
        Runnable r = () -> d.dismiss();
        h.postDelayed(r,500);
    }
}

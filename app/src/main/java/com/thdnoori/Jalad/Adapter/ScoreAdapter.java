package com.thdnoori.Jalad.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thdnoori.Jalad.Model.Score;
import com.thdnoori.Jalad.R;

import java.util.List;


public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.MyView> {
    private Context activity;
    List<Score> scoreList;
    public ScoreAdapter(Context activity, List<Score> packList) {
        this.scoreList = packList;
        this.activity = activity;
    }

    @NonNull
    @Override

    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_score, parent, false);
        return new MyView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, int position) {
        if (position == 0  ) {
            holder.itemView.setBackgroundColor(Color.parseColor("#FC97C4"));
        } else if (position % 2 == 1) {
            holder.itemView.setBackgroundColor(Color.parseColor("#DEFF7A"));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FAE46D"));
        }
        if (position != 0){
            holder.rank.setText("Rank "+(position));
        }else{
            holder.rank.setText("Rank "+scoreList.get(position).getRank());
        }
        holder.name.setText(scoreList.get(position).getUsername());
        holder.record.setText(scoreList.get(position).getRecord());
    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }

    public class MyView extends RecyclerView.ViewHolder {
        TextView name, record,rank;

        public MyView(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.usernameScore);
            record = itemView.findViewById(R.id.recordScore);
            rank = itemView.findViewById(R.id.rankScore);
        }
    }
}

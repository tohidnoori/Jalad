package com.thdnoori.Jalad.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.thdnoori.Jalad.Database.WordDb;
import com.thdnoori.Jalad.Database.WordTimerDb;
import com.thdnoori.Jalad.Model.MyChar;
import com.thdnoori.Jalad.R;

import java.util.ArrayList;
import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.MyView> {
    private Context activity;
    private List<MyChar> charList = new ArrayList<>();

    public WordAdapter(Context activity, WordDb word) {
        this.activity = activity;
        List<MyChar> letters = new ArrayList<>();
        for (int i = 0; i<word.getWordName().length();i++){
            MyChar m = new MyChar(word.getWordName().charAt(i));
            letters.add(m);
        }
        charList = letters;
    }
    public WordAdapter(Context activity, WordTimerDb word) {
        this.activity = activity;
        List<MyChar> letters = new ArrayList<>();
        for (int i = 0; i<word.getWordName().length();i++){
            MyChar m = new MyChar(word.getWordName().charAt(i));
            letters.add(m);
        }
        charList = letters;
    }
    @NonNull
    @Override

    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_word_letter, parent, false);
        return new MyView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, int position) {
        if (charList.get(position).getChar() == ' ') {
            holder.underLine.setVisibility(View.INVISIBLE);
            charList.get(position).setVisibility(true);
        }
        if (charList.get(position).getVisibility()) {
            holder.letter.setVisibility(View.VISIBLE);
        } else {
            holder.letter.setVisibility(View.INVISIBLE);
        }
        if (charList.get(position).getColor()){
            holder.letter.setTextColor(Color.parseColor("#D50000"));
        }else{
            holder.letter.setTextColor(Color.parseColor("#000000"));
        }
        holder.letter.setText(String.valueOf(charList.get(position).getChar()));
    }

    @Override
    public int getItemCount() {
        return charList.size();
    }

    public class MyView extends RecyclerView.ViewHolder {
        TextView letter;
        TextView underLine;

        public MyView(@NonNull View itemView) {
            super(itemView);
            letter = itemView.findViewById(R.id.letter);
            underLine = itemView.findViewById(R.id.underline);
        }
    }

    public void makeITVisible(String charachter) {
        for (int i = 0; i < charList.size(); i++) {
            if (charachter.equals(charList.get(i).getChar().toString())) {
                MyChar m = new MyChar(charList.get(i).getChar());
                m.setVisibility(true);
                charList.set(i, m);
                notifyItemChanged(i);
            }
        }
    }
    public void visibleMissingWord(){
        for(int i = 0; i<charList.size();i++){
            if (!charList.get(i).getVisibility()){
                MyChar m = new MyChar(charList.get(i).getChar());
                m.setVisibility(true);
                m.setColor(true);
                charList.set(i, m);
                notifyItemChanged(i);
            }
        }
    }

    public Boolean checkWinStatement(){
        boolean winResult = true;
        for (int i = 0 ; i<charList.size();i++){
            if (!charList.get(i).getVisibility()){
                winResult = false;
            }
        }
        return winResult;
    }

    public void visiblePaidWord(){
        for(int i = 0; i<charList.size();i++){
            if (!charList.get(i).getVisibility()){
                charList.get(i).setVisibility(true);
                notifyItemChanged(i);
                break;
            }
        }
    }
}

package com.thdnoori.Jalad.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thdnoori.Jalad.Database.WordDb;
import com.thdnoori.Jalad.Model.MyChar;
import com.thdnoori.Jalad.R;

import java.util.ArrayList;
import java.util.List;

public class TowPlayerWordAdapter extends RecyclerView.Adapter<TowPlayerWordAdapter.MyView> {
    private Context activity;
    private List<MyChar> charList = new ArrayList<>();

    public TowPlayerWordAdapter(Context activity, WordDb word) {
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
        holder.letter.setText(String.valueOf(charList.get(position).getChar()));
    }

    @Override
    public int getItemCount() {
        return 13;
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


    public void showEnteredWord(String x){
        for (int i = 0; i<charList.size(); i++){
            if (charList.get(i).getChar().equals(' ')){
                charList.set(i,new MyChar(x.charAt(0)));
                notifyItemChanged(i);
                break;
            }
        }
    }
    public void deleteLastWord(){
        for (int i =charList.size()-1 ; i>-1; i--){
            if (!charList.get(i).getChar().equals(' ')){
                charList.set(i,new MyChar(' '));
                notifyItemChanged(i);
                break;
            }
        }
    }
    public void eraseAll(){
        for (int i = 0; i<charList.size(); i++){
            if (!charList.get(i).getChar().equals(' ')){
                charList.set(i, new MyChar(' '));
            }
        }
        notifyDataSetChanged();
    }
    public String getFinalWord(){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i<charList.size(); i++){
            if (!charList.get(i).getChar().equals(' ')){
                builder.append(charList.get(i).getChar());
            }else {
                return builder.toString();
            }
        }

        return builder.toString();
    }
}

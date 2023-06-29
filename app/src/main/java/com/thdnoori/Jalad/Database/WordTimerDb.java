package com.thdnoori.Jalad.Database;

import com.orm.SugarRecord;

public class WordTimerDb extends  SugarRecord{
    private String wordCategory;
    private String wordName;

    public void setWordName(String wordName) {
        this.wordName = wordName;
    }

    public void setWordCategory(String wordCategory) {
        this.wordCategory = wordCategory;
    }

    public String getWordCategory() {
        return wordCategory;
    }

    public String getWordName() {
        return wordName;
    }
}
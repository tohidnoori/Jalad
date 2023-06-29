package com.thdnoori.Jalad.Model;

public class Score {
private String username;
private String record;
private int rank;
    public String getUsername() {
        return username;
    }

    public String getRecord() {
        return record;
    }

    public int getRank() {
        return rank;
    }

    public Score(String username, String record, int rank) {
        this.username = username;
        this.record = record;
        this.rank = rank;
    }
}

package com.thdnoori.Jalad.Database;

import com.orm.SugarRecord;

public class Achievement extends SugarRecord {
    private String name;
    private int price;
    private int record;
    private boolean once;
    private boolean completed;

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isCompleted() {
        return completed;
    }

    private String perfName;

    public void setPerfName(String perfName) {
        this.perfName = perfName;
    }

    public String getPerfName() {
        return perfName;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean isReady() {
        return ready;
    }

    private boolean ready;

    public void setOnce(boolean once) {
        this.once = once;
    }

    public boolean isOnce() {
        return once;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setRecord(int record) {
        this.record = record;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getRecord() {
        return record;
    }
}

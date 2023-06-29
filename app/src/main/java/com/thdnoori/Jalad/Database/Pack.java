package com.thdnoori.Jalad.Database;

import com.orm.SugarRecord;

public class Pack extends SugarRecord {
    private String name;
    private String perfName;
    private int price;
    private int imageResourceID;
    private boolean isEnable;
    private boolean isPurchased;

    public int getImageResourceID() {
        return imageResourceID;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public boolean isPurchased() {
        return isPurchased;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setImageResourceID(int imageResourceID) {
        this.imageResourceID = imageResourceID;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public void setPurchased(boolean purchased) {
        isPurchased = purchased;
    }

    public String getName() {
        return name;
    }

    public void setPerfName(String perfName) {
        this.perfName = perfName;
    }

    public String getPerfName() {
        return perfName;
    }

    public int getPrice() {
        return price;
    }

}

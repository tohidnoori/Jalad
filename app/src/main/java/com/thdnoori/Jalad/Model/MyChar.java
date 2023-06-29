package com.thdnoori.Jalad.Model;

public class MyChar
{
    private Character Char;
    private Boolean visibility = false;
    private Boolean color = false;

    public MyChar(Character c1) {
        Char = c1;
    }

    public Character getChar() {
        return Char;
    }

    public Boolean getVisibility() {
        return visibility;
    }

    public void setColor(Boolean color) {
        this.color = color;
    }

    public Boolean getColor() {
        return color;
    }

    public void setVisibility(Boolean visibility) {
        this.visibility = visibility;
    }
}

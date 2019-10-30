package com.mrex.ncs;

public class HomeList {
    private int image;
    private String textTop, textBottom;

    public HomeList(int image, String textTop, String textBottom) {
        this.image = image;
        this.textTop = textTop;
        this.textBottom = textBottom;
    }

    public int getImage() {
        return image;
    }

    public String getTextTop() {
        return textTop;
    }

    public String getTextBottom() {
        return textBottom;
    }
}

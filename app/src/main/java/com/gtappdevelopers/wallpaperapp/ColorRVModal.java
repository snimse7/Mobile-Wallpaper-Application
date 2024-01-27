package com.gtappdevelopers.wallpaperapp;

public class ColorRVModal {//creating variaable fot category and image url  on below line.
    private String color;
    private final String imgUrl1;

    public ColorRVModal(String color, String imgUrl1) {
        this.color = color;
        this.imgUrl1 = imgUrl1;
    }

    //creating a constructor, getter and setter methods.
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImgUrl1() {
        return imgUrl1;
    }


}
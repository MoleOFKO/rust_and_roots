/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import javafx.scene.image.ImageView;

/**
 *
 * @author Shane Khant Ko
 */
public class mobs {
    private int mobs_id;
    private String mobs_name;
    private String mobs_type;
    private int str;
    private int agi;
    private int vid;
    private int dex;
    private int hp;
    private int stimana;
    private ImageView image;
    private String imagePath;

    public int getMobs_id() {
        return mobs_id;
    }

    public void setMobs_id(int mobs_id) {
        this.mobs_id = mobs_id;
    }

    public String getMobs_name() {
        return mobs_name;
    }

    public void setMobs_name(String mobs_name) {
        this.mobs_name = mobs_name;
    }

    public String getMobs_type() {
        return mobs_type;
    }

    public void setMobs_type(String mobs_type) {
        this.mobs_type = mobs_type;
    }

    public int getStr() {
        return str;
    }

    public void setStr(int str) {
        this.str = str;
    }

    public int getAgi() {
        return agi;
    }

    public void setAgi(int agi) {
        this.agi = agi;
    }

    public int getVid() {
        return vid;
    }

    public void setVid(int vid) {
        this.vid = vid;
    }

    public int getDex() {
        return dex;
    }

    public void setDex(int dex) {
        this.dex = dex;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getStimana() {
        return stimana;
    }

    public void setStimana(int stimana) {
        this.stimana = stimana;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public mobs(int mobs_id, String mobs_name, String mobs_type, int str, int agi, int vid, int dex, int hp, int stimana, ImageView image) {
        this(mobs_id, mobs_name, mobs_type, str, agi, vid, dex, hp, stimana, image, null);
    }

    public mobs(int mobs_id, String mobs_name, String mobs_type, int str, int agi, int vid, int dex, int hp, int stimana, ImageView image, String imagePath) {
        this.mobs_id = mobs_id;
        this.mobs_name = mobs_name;
        this.mobs_type = mobs_type;
        this.str = str;
        this.agi = agi;
        this.vid = vid;
        this.dex = dex;
        this.hp = hp;
        this.stimana = stimana;
        this.image = image;
        this.imagePath = imagePath;
    }
    
}

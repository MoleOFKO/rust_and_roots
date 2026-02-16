/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import javafx.scene.image.ImageView;

/**
 *
 * @author MITUSER-1
 */
public class item {
    private int item_id;
    private String item_name;
    private int price;
    private int str;
    private int agi;
    private int vid;
    private int dex;
    private int hp;
    private int stimana;
    private ImageView image;

    public item(int item_id, String item_name, int price, int str, int agi, int vid, int dex, int hp, int stimana, ImageView image) {
        this.item_id = item_id;
        this.item_name = item_name;
        this.price = price;
        this.str = str;
        this.agi = agi;
        this.vid = vid;
        this.dex = dex;
        this.hp = hp;
        this.stimana = stimana;
        this.image = image;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
    

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
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

  
}

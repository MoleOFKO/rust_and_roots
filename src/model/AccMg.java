/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Shane Khant Ko
 */
public class AccMg {
    private int account_id;
    private String account_name;
    private String email;
    private String account_stats;
    private String created_date;


    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccount_stats() {
        return account_stats;
    }

    public void setAccount_stats(String account_status) {
        this.account_stats = account_status;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public AccMg(String email) {
        this.email = email;
    }

    public AccMg(int account_id, String account_name, String email, String account_stats, String created_date) {
        this.account_id = account_id;
        this.account_name = account_name;
        this.email = email;
        this.account_stats = account_stats;
        this.created_date = created_date;
    }
}

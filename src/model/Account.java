/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Shane Khant Ko
 */
public class Account {
    private int account_id;
    private String account_name,email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    private String user_type;
    private String account_stats;
    private String created_date;

    public Account(String account_name, String user_type,String email, String account_stats, String created_date) {
        this.account_name = account_name;
        this.user_type = user_type;
        this.email = email;
        this.account_stats = account_stats;
        this.created_date = created_date;
    }

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

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
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

    public Account(int account_id, String account_name, String user_type, String account_stats, String created_date) {
        this.account_id = account_id;
        this.account_name = account_name;
        this.user_type = user_type;
        this.account_stats = account_stats;
        this.created_date = created_date;
    }
}

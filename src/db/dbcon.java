/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import java.sql.Connection;

/**
 *
 * @author larry
 */
public class dbcon {

    public Connection getConnection() throws ClassNotFoundException {
        return Database.openConnection();
    }
}

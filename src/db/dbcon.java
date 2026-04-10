/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author larry
 */
public class dbcon {
    
    private String username="root";
    private String password="123123";
    
    public Connection getConnection() throws ClassNotFoundException{
        Connection con=null;
        String url="jdbc:mysql://localhost:3306/rust_and_root";
        Class.forName("com.mysql.cj.jdbc.Driver");
        System.out.println("load driver..........");
        try{
            con =DriverManager.getConnection(url, username, password);
            DatabaseSchemaManager.ensureSchema(con);
            System.out.println("database connected");
        }catch(SQLException e){
            System.out.println("not connect");
            throw new IllegalStateException("Failed to connect or initialize database schema", e);
        }
        return con;
      }
    
}

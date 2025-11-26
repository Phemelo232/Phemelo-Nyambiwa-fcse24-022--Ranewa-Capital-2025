package com.ooad.ranewacapital.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:postgresql://aws-1-us-east-2.pooler.supabase.com:6543/postgres?user=postgres.lgksjkqrqnpetrftfgtv&password=Ranewa@232";

    public static void getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);

        try{
            Class.forName("org.postgresql.Driver");
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }

        if(conn == null){
            System.out.println("Database not connected");
        }else{
            System.out.println("Database connected");
        }
    }

    public static void main(String[] args) throws SQLException {
        getConnection();
    }
}


package com.banksampah.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/sistem_bank_sampah"
            + "?useSSL=false&serverTimezone=Asia/Jakarta&allowPublicKeyRetrieval=true"
            + "&autoReconnect=true&useUnicode=true&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver tidak ditemukan!", e);
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
package com.banksampah.dao;

import com.banksampah.model.User;
import com.banksampah.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setIdUser(rs.getInt("id_user"));
                u.setNama(rs.getString("nama"));
                u.setUsername(rs.getString("username"));
                u.setRole(rs.getString("role"));
                u.setNoTelp(rs.getString("no_telp"));
                u.setAlamat(rs.getString("alamat"));
                return u;
            }
        }
        return null;
    }

    public List<User> getAll() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY nama";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                User u = new User();
                u.setIdUser(rs.getInt("id_user"));
                u.setNama(rs.getString("nama"));
                u.setUsername(rs.getString("username"));
                u.setRole(rs.getString("role"));
                u.setNoTelp(rs.getString("no_telp"));
                u.setAlamat(rs.getString("alamat"));
                list.add(u);
            }
        }
        return list;
    }

    public List<User> getNasabah() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role='user' ORDER BY nama";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                User u = new User();
                u.setIdUser(rs.getInt("id_user"));
                u.setNama(rs.getString("nama"));
                u.setUsername(rs.getString("username"));
                u.setRole(rs.getString("role"));
                list.add(u);
            }
        }
        return list;
    }

    public void insert(User u) throws SQLException {
        String sql = "INSERT INTO users (nama,username,password,role,no_telp,alamat) VALUES (?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getNama());
            ps.setString(2, u.getUsername());
            ps.setString(3, u.getPassword());
            ps.setString(4, u.getRole());
            ps.setString(5, u.getNoTelp());
            ps.setString(6, u.getAlamat());
            ps.executeUpdate();
        }
    }

    public void update(User u) throws SQLException {
        String sql = "UPDATE users SET nama=?,username=?,role=?,no_telp=?,alamat=? WHERE id_user=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getNama());
            ps.setString(2, u.getUsername());
            ps.setString(3, u.getRole());
            ps.setString(4, u.getNoTelp());
            ps.setString(5, u.getAlamat());
            ps.setInt(6, u.getIdUser());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM users WHERE id_user=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public int countNasabah() throws SQLException {
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users WHERE role='user'")) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }
}

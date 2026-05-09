package com.banksampah.dao;

import com.banksampah.model.JenisSampah;
import com.banksampah.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JenisSampahDAO {

    public List<JenisSampah> getAll() throws SQLException {
        List<JenisSampah> list = new ArrayList<>();
        String sql = "SELECT j.*, k.nama_kategori FROM jenis_sampah j " +
                     "JOIN kategori_sampah k ON j.id_kategori=k.id_kategori ORDER BY k.nama_kategori, j.nama_jenis";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                JenisSampah j = new JenisSampah();
                j.setIdJenis(rs.getInt("id_jenis"));
                j.setIdKategori(rs.getInt("id_kategori"));
                j.setNamaJenis(rs.getString("nama_jenis"));
                j.setHargaPerKg(rs.getDouble("harga_per_kg"));
                j.setNamaKategori(rs.getString("nama_kategori"));
                list.add(j);
            }
        }
        return list;
    }

    public JenisSampah getById(int id) throws SQLException {
        String sql = "SELECT j.*, k.nama_kategori FROM jenis_sampah j " +
                     "JOIN kategori_sampah k ON j.id_kategori=k.id_kategori WHERE j.id_jenis=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                JenisSampah j = new JenisSampah();
                j.setIdJenis(rs.getInt("id_jenis"));
                j.setNamaJenis(rs.getString("nama_jenis"));
                j.setHargaPerKg(rs.getDouble("harga_per_kg"));
                j.setNamaKategori(rs.getString("nama_kategori"));
                return j;
            }
        }
        return null;
    }

    public void insert(JenisSampah j) throws SQLException {
        String sql = "INSERT INTO jenis_sampah (id_kategori, nama_jenis, harga_per_kg) VALUES (?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, j.getIdKategori());
            ps.setString(2, j.getNamaJenis());
            ps.setDouble(3, j.getHargaPerKg());
            ps.executeUpdate();
        }
    }

    public void update(JenisSampah j) throws SQLException {
        String sql = "UPDATE jenis_sampah SET id_kategori=?, nama_jenis=?, harga_per_kg=? WHERE id_jenis=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, j.getIdKategori());
            ps.setString(2, j.getNamaJenis());
            ps.setDouble(3, j.getHargaPerKg());
            ps.setInt(4, j.getIdJenis());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM jenis_sampah WHERE id_jenis=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}

package com.banksampah.dao;

import com.banksampah.model.DetailSetor;
import com.banksampah.model.SetorSampah;
import com.banksampah.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SetorSampahDAO {

    public List<SetorSampah> getAll() throws SQLException {
        List<SetorSampah> list = new ArrayList<>();
        String sql = "SELECT s.*, u.nama AS nama_nasabah, " +
                     "COALESCE((SELECT SUM(d.subtotal) FROM detail_setor d WHERE d.id_setor=s.id_setor),0) AS total " +
                     "FROM setor_sampah s JOIN users u ON s.id_user=u.id_user ORDER BY s.tanggal DESC";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                SetorSampah s = new SetorSampah();
                s.setIdSetor(rs.getInt("id_setor"));
                s.setIdUser(rs.getInt("id_user"));
                s.setTanggal(rs.getDate("tanggal").toLocalDate());
                s.setStatus(rs.getString("status"));
                s.setCatatan(rs.getString("catatan"));
                s.setNamaNasabah(rs.getString("nama_nasabah"));
                s.setTotalSubtotal(rs.getDouble("total"));
                list.add(s);
            }
        }
        return list;
    }

    public int insert(SetorSampah s) throws SQLException {
        String sql = "INSERT INTO setor_sampah (id_user, tanggal, status, catatan) VALUES (?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, s.getIdUser());
            ps.setDate(2, Date.valueOf(s.getTanggal()));
            ps.setString(3, s.getStatus() != null ? s.getStatus() : "menunggu");
            ps.setString(4, s.getCatatan());
            ps.executeUpdate();
            ResultSet gk = ps.getGeneratedKeys();
            if (gk.next()) return gk.getInt(1);
        }
        return -1;
    }

    public void update(SetorSampah s) throws SQLException {
        String sql = "UPDATE setor_sampah SET id_user=?, tanggal=?, status=?, catatan=? WHERE id_setor=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, s.getIdUser());
            ps.setDate(2, Date.valueOf(s.getTanggal()));
            ps.setString(3, s.getStatus());
            ps.setString(4, s.getCatatan());
            ps.setInt(5, s.getIdSetor());
            ps.executeUpdate();
        }
    }

    public void updateStatus(int idSetor, String status, int idVerifikasi) throws SQLException {
        String sql = "UPDATE setor_sampah SET status=?, id_verifikasi=? WHERE id_setor=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, idVerifikasi);
            ps.setInt(3, idSetor);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps1 = c.prepareStatement("DELETE FROM detail_setor WHERE id_setor=?");
            ps1.setInt(1, id); ps1.executeUpdate();
            PreparedStatement ps2 = c.prepareStatement("DELETE FROM setor_sampah WHERE id_setor=?");
            ps2.setInt(1, id); ps2.executeUpdate();
        }
    }

    public List<DetailSetor> getDetail(int idSetor) throws SQLException {
        List<DetailSetor> list = new ArrayList<>();
        String sql = "SELECT d.*, j.nama_jenis FROM detail_setor d JOIN jenis_sampah j ON d.id_jenis=j.id_jenis WHERE d.id_setor=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idSetor);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DetailSetor d = new DetailSetor();
                d.setIdDetail(rs.getInt("id_detail"));
                d.setIdSetor(rs.getInt("id_setor"));
                d.setIdJenis(rs.getInt("id_jenis"));
                d.setBerat(rs.getDouble("berat"));
                d.setHargaSatuan(rs.getDouble("harga_satuan"));
                d.setSubtotal(rs.getDouble("subtotal"));
                d.setNamaJenis(rs.getString("nama_jenis"));
                list.add(d);
            }
        }
        return list;
    }

    public void insertDetail(DetailSetor d) throws SQLException {
        String sql = "INSERT INTO detail_setor (id_setor, id_jenis, berat, harga_satuan, subtotal) VALUES (?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, d.getIdSetor());
            ps.setInt(2, d.getIdJenis());
            ps.setDouble(3, d.getBerat());
            ps.setDouble(4, d.getHargaSatuan());
            ps.setDouble(5, d.getSubtotal());
            ps.executeUpdate();
        }
    }

    public void deleteDetail(int idSetor) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM detail_setor WHERE id_setor=?")) {
            ps.setInt(1, idSetor);
            ps.executeUpdate();
        }
    }

    public int countMenunggu() throws SQLException {
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM setor_sampah WHERE status='menunggu'")) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public double totalPendapatan() throws SQLException {
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COALESCE(SUM(d.subtotal),0) FROM detail_setor d JOIN setor_sampah s ON d.id_setor=s.id_setor WHERE s.status='diterima'")) {
            if (rs.next()) return rs.getDouble(1);
        }
        return 0;
    }

    public int countBulanIni() throws SQLException {
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM setor_sampah WHERE MONTH(tanggal)=MONTH(CURDATE()) AND YEAR(tanggal)=YEAR(CURDATE())")) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    // ===== Methods untuk USER (filter by id_user) =====

    public List<SetorSampah> getByUser(int idUser) throws SQLException {
        List<SetorSampah> list = new ArrayList<>();
        String sql = "SELECT s.*, u.nama AS nama_nasabah, " +
                     "COALESCE((SELECT SUM(d.subtotal) FROM detail_setor d WHERE d.id_setor=s.id_setor),0) AS total " +
                     "FROM setor_sampah s JOIN users u ON s.id_user=u.id_user WHERE s.id_user=? ORDER BY s.tanggal DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SetorSampah s = new SetorSampah();
                s.setIdSetor(rs.getInt("id_setor"));
                s.setIdUser(rs.getInt("id_user"));
                s.setTanggal(rs.getDate("tanggal").toLocalDate());
                s.setStatus(rs.getString("status"));
                s.setCatatan(rs.getString("catatan"));
                s.setNamaNasabah(rs.getString("nama_nasabah"));
                s.setTotalSubtotal(rs.getDouble("total"));
                list.add(s);
            }
        }
        return list;
    }

    public int countByUser(int idUser) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM setor_sampah WHERE id_user=?")) {
            ps.setInt(1, idUser);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int countMenungguByUser(int idUser) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM setor_sampah WHERE id_user=? AND status='menunggu'")) {
            ps.setInt(1, idUser);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int countDiterimaByUser(int idUser) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM setor_sampah WHERE id_user=? AND status='diterima'")) {
            ps.setInt(1, idUser);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public double totalPendapatanByUser(int idUser) throws SQLException {
        String sql = "SELECT COALESCE(SUM(d.subtotal),0) FROM detail_setor d " +
                     "JOIN setor_sampah s ON d.id_setor=s.id_setor WHERE s.id_user=? AND s.status='diterima'";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        }
        return 0;
    }
}
package com.banksampah.model;

import java.time.LocalDate;

public class SetorSampah {
    private int idSetor;
    private int idUser;
    private Integer idVerifikasi;
    private LocalDate tanggal;
    private String status;
    private String catatan;
    private String namaNasabah;
    private double totalSubtotal;

    public int getIdSetor() { return idSetor; }
    public void setIdSetor(int idSetor) { this.idSetor = idSetor; }
    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }
    public Integer getIdVerifikasi() { return idVerifikasi; }
    public void setIdVerifikasi(Integer idVerifikasi) { this.idVerifikasi = idVerifikasi; }
    public LocalDate getTanggal() { return tanggal; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }
    public String getNamaNasabah() { return namaNasabah; }
    public void setNamaNasabah(String namaNasabah) { this.namaNasabah = namaNasabah; }
    public double getTotalSubtotal() { return totalSubtotal; }
    public void setTotalSubtotal(double totalSubtotal) { this.totalSubtotal = totalSubtotal; }
}

package com.banksampah.model;

public class DetailSetor {
    private int idDetail;
    private int idSetor;
    private int idJenis;
    private double berat;
    private double hargaSatuan;
    private double subtotal;
    private String namaJenis;

    public int getIdDetail() { return idDetail; }
    public void setIdDetail(int idDetail) { this.idDetail = idDetail; }
    public int getIdSetor() { return idSetor; }
    public void setIdSetor(int idSetor) { this.idSetor = idSetor; }
    public int getIdJenis() { return idJenis; }
    public void setIdJenis(int idJenis) { this.idJenis = idJenis; }
    public double getBerat() { return berat; }
    public void setBerat(double berat) { this.berat = berat; }
    public double getHargaSatuan() { return hargaSatuan; }
    public void setHargaSatuan(double hargaSatuan) { this.hargaSatuan = hargaSatuan; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public String getNamaJenis() { return namaJenis; }
    public void setNamaJenis(String namaJenis) { this.namaJenis = namaJenis; }
}

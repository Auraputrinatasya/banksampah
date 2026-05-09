package com.banksampah.model;

public class JenisSampah {
    private int idJenis;
    private int idKategori;
    private String namaJenis;
    private double hargaPerKg;
    private String namaKategori;

    public int getIdJenis() { return idJenis; }
    public void setIdJenis(int idJenis) { this.idJenis = idJenis; }
    public int getIdKategori() { return idKategori; }
    public void setIdKategori(int idKategori) { this.idKategori = idKategori; }
    public String getNamaJenis() { return namaJenis; }
    public void setNamaJenis(String namaJenis) { this.namaJenis = namaJenis; }
    public double getHargaPerKg() { return hargaPerKg; }
    public void setHargaPerKg(double hargaPerKg) { this.hargaPerKg = hargaPerKg; }
    public String getNamaKategori() { return namaKategori; }
    public void setNamaKategori(String namaKategori) { this.namaKategori = namaKategori; }

    @Override public String toString() { return namaJenis + " (Rp " + (int)hargaPerKg + "/kg)"; }
}

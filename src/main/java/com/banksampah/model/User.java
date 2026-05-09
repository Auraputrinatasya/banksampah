package com.banksampah.model;

public class User {
    private int idUser;
    private String nama;
    private String username;
    private String password;
    private String role;
    private String noTelp;
    private String alamat;

    public User() {}
    public User(int idUser, String nama, String username, String role) {
        this.idUser = idUser; this.nama = nama; this.username = username; this.role = role;
    }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getNoTelp() { return noTelp; }
    public void setNoTelp(String noTelp) { this.noTelp = noTelp; }
    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    @Override public String toString() { return nama; }
}

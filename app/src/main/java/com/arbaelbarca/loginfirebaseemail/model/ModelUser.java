package com.arbaelbarca.loginfirebaseemail.model;

import java.io.Serializable;

public class ModelUser implements Serializable {
    private String id, email, username, nama, imageUrl, phone, token_id, gender, alamat, pass, status;
    private String saldoku, nosaldoku;


    public String getNosaldoku() {
        return nosaldoku;
    }

    public void setNosaldoku(String nosaldoku) {
        this.nosaldoku = nosaldoku;
    }

    public String getSaldoku() {
        return saldoku;
    }

    public void setSaldoku(String saldoku) {
        this.saldoku = saldoku;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }
}

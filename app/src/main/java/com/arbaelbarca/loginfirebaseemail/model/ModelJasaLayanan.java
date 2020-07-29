package com.arbaelbarca.loginfirebaseemail.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ModelJasaLayanan implements Parcelable {
    private String idUser, uuid, statusOrder;
    private String imageJasa, textNama, textKategori, textDeskripsi, textHarga;
    private String textTambahan, textOngkir, textTotalPembayaran, textAlamatToko, textAlamatPemesan;
    private String getImageTukang, getNamaTukang, getNoTukang, getPlatTukang;

    private double latPemesan, lotPemesan, latToko, lotToko;

    private String namaPemesan, noTlpnPemesan, imagePemesan, payment, ratingUlasan, textUlasan;


    protected ModelJasaLayanan(Parcel in) {
        idUser = in.readString();
        uuid = in.readString();
        statusOrder = in.readString();
        imageJasa = in.readString();
        textNama = in.readString();
        textKategori = in.readString();
        textDeskripsi = in.readString();
        textHarga = in.readString();
        textTambahan = in.readString();
        textOngkir = in.readString();
        textTotalPembayaran = in.readString();
        textAlamatToko = in.readString();
        textAlamatPemesan = in.readString();
        getImageTukang = in.readString();
        getNamaTukang = in.readString();
        getNoTukang = in.readString();
        getPlatTukang = in.readString();
        latPemesan = in.readDouble();
        lotPemesan = in.readDouble();
        latToko = in.readDouble();
        lotToko = in.readDouble();
        namaPemesan = in.readString();
        noTlpnPemesan = in.readString();
        imagePemesan = in.readString();
        payment = in.readString();
        ratingUlasan = in.readString();
        textUlasan = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idUser);
        dest.writeString(uuid);
        dest.writeString(statusOrder);
        dest.writeString(imageJasa);
        dest.writeString(textNama);
        dest.writeString(textKategori);
        dest.writeString(textDeskripsi);
        dest.writeString(textHarga);
        dest.writeString(textTambahan);
        dest.writeString(textOngkir);
        dest.writeString(textTotalPembayaran);
        dest.writeString(textAlamatToko);
        dest.writeString(textAlamatPemesan);
        dest.writeString(getImageTukang);
        dest.writeString(getNamaTukang);
        dest.writeString(getNoTukang);
        dest.writeString(getPlatTukang);
        dest.writeDouble(latPemesan);
        dest.writeDouble(lotPemesan);
        dest.writeDouble(latToko);
        dest.writeDouble(lotToko);
        dest.writeString(namaPemesan);
        dest.writeString(noTlpnPemesan);
        dest.writeString(imagePemesan);
        dest.writeString(payment);
        dest.writeString(ratingUlasan);
        dest.writeString(textUlasan);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ModelJasaLayanan> CREATOR = new Creator<ModelJasaLayanan>() {
        @Override
        public ModelJasaLayanan createFromParcel(Parcel in) {
            return new ModelJasaLayanan(in);
        }

        @Override
        public ModelJasaLayanan[] newArray(int size) {
            return new ModelJasaLayanan[size];
        }
    };

    public String getRatingUlasan() {
        return ratingUlasan;
    }

    public void setRatingUlasan(String ratingUlasan) {
        this.ratingUlasan = ratingUlasan;
    }

    public String getTextUlasan() {
        return textUlasan;
    }

    public void setTextUlasan(String textUlasan) {
        this.textUlasan = textUlasan;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getNamaPemesan() {
        return namaPemesan;
    }

    public void setNamaPemesan(String namaPemesan) {
        this.namaPemesan = namaPemesan;
    }

    public String getNoTlpnPemesan() {
        return noTlpnPemesan;
    }

    public void setNoTlpnPemesan(String noTlpnPemesan) {
        this.noTlpnPemesan = noTlpnPemesan;
    }

    public String getImagePemesan() {
        return imagePemesan;
    }

    public void setImagePemesan(String imagePemesan) {
        this.imagePemesan = imagePemesan;
    }

    public String getGetPlatTukang() {
        return getPlatTukang;
    }

    public void setGetPlatTukang(String getPlatTukang) {
        this.getPlatTukang = getPlatTukang;
    }

    public String getGetImageTukang() {
        return getImageTukang;
    }

    public void setGetImageTukang(String getImageTukang) {
        this.getImageTukang = getImageTukang;
    }

    public String getGetNamaTukang() {
        return getNamaTukang;
    }

    public void setGetNamaTukang(String getNamaTukang) {
        this.getNamaTukang = getNamaTukang;
    }

    public String getGetNoTukang() {
        return getNoTukang;
    }

    public void setGetNoTukang(String getNoTukang) {
        this.getNoTukang = getNoTukang;
    }

    public String getStatusOrder() {
        return statusOrder;
    }

    public void setStatusOrder(String statusOrder) {
        this.statusOrder = statusOrder;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public double getLatPemesan() {
        return latPemesan;
    }

    public void setLatPemesan(double latPemesan) {
        this.latPemesan = latPemesan;
    }

    public double getLotPemesan() {
        return lotPemesan;
    }

    public void setLotPemesan(double lotPemesan) {
        this.lotPemesan = lotPemesan;
    }

    public double getLatToko() {
        return latToko;
    }

    public void setLatToko(double latToko) {
        this.latToko = latToko;
    }

    public double getLotToko() {
        return lotToko;
    }

    public void setLotToko(double lotToko) {
        this.lotToko = lotToko;
    }

    public String getTextTambahan() {
        return textTambahan;
    }

    public void setTextTambahan(String textTambahan) {
        this.textTambahan = textTambahan;
    }

    public String getTextOngkir() {
        return textOngkir;
    }

    public void setTextOngkir(String textOngkir) {
        this.textOngkir = textOngkir;
    }

    public String getTextTotalPembayaran() {
        return textTotalPembayaran;
    }

    public void setTextTotalPembayaran(String textTotalPembayaran) {
        this.textTotalPembayaran = textTotalPembayaran;
    }

    public String getTextAlamatToko() {
        return textAlamatToko;
    }

    public void setTextAlamatToko(String textAlamatToko) {
        this.textAlamatToko = textAlamatToko;
    }

    public String getTextAlamatPemesan() {
        return textAlamatPemesan;
    }

    public void setTextAlamatPemesan(String textAlamatPemesan) {
        this.textAlamatPemesan = textAlamatPemesan;
    }

    public ModelJasaLayanan() {

    }


    public String getTextHarga() {
        return textHarga;
    }

    public void setTextHarga(String textHarga) {
        this.textHarga = textHarga;
    }

    public String getImageJasa() {
        return imageJasa;
    }

    public void setImageJasa(String imageJasa) {
        this.imageJasa = imageJasa;
    }

    public String getTextNama() {
        return textNama;
    }

    public void setTextNama(String textNama) {
        this.textNama = textNama;
    }

    public String getTextKategori() {
        return textKategori;
    }

    public void setTextKategori(String textKategori) {
        this.textKategori = textKategori;
    }

    public String getTextDeskripsi() {
        return textDeskripsi;
    }

    public void setTextDeskripsi(String textDeskripsi) {
        this.textDeskripsi = textDeskripsi;
    }


}

package com.arbaelbarca.loginfirebaseemail.model.modeltopup;

import android.os.Parcel;
import android.os.Parcelable;

public class ModelTopup implements Parcelable {
    private String idUser, namaUser, nominalTopup, noSaldo, saldoku, imageBukti, statusTopup;
    private String emailUser, photoUser, phoneUser, keyId;


    public ModelTopup() {

    }

    protected ModelTopup(Parcel in) {
        idUser = in.readString();
        namaUser = in.readString();
        nominalTopup = in.readString();
        noSaldo = in.readString();
        saldoku = in.readString();
        imageBukti = in.readString();
        statusTopup = in.readString();
        emailUser = in.readString();
        photoUser = in.readString();
        phoneUser = in.readString();
        keyId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idUser);
        dest.writeString(namaUser);
        dest.writeString(nominalTopup);
        dest.writeString(noSaldo);
        dest.writeString(saldoku);
        dest.writeString(imageBukti);
        dest.writeString(statusTopup);
        dest.writeString(emailUser);
        dest.writeString(photoUser);
        dest.writeString(phoneUser);
        dest.writeString(keyId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ModelTopup> CREATOR = new Creator<ModelTopup>() {
        @Override
        public ModelTopup createFromParcel(Parcel in) {
            return new ModelTopup(in);
        }

        @Override
        public ModelTopup[] newArray(int size) {
            return new ModelTopup[size];
        }
    };

    public String getSaldoku() {
        return saldoku;
    }

    public void setSaldoku(String saldoku) {
        this.saldoku = saldoku;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getPhotoUser() {
        return photoUser;
    }

    public void setPhotoUser(String photoUser) {
        this.photoUser = photoUser;
    }

    public String getPhoneUser() {
        return phoneUser;
    }

    public void setPhoneUser(String phoneUser) {
        this.phoneUser = phoneUser;
    }

    public String getStatusTopup() {
        return statusTopup;
    }

    public void setStatusTopup(String statusTopup) {
        this.statusTopup = statusTopup;
    }

    public String getNamaUser() {
        return namaUser;
    }

    public void setNamaUser(String namaUser) {
        this.namaUser = namaUser;
    }

    public String getImageBukti() {
        return imageBukti;
    }

    public void setImageBukti(String imageBukti) {
        this.imageBukti = imageBukti;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNominalTopup() {
        return nominalTopup;
    }

    public void setNominalTopup(String nominalTopup) {
        this.nominalTopup = nominalTopup;
    }

    public String getNoSaldo() {
        return noSaldo;
    }

    public void setNoSaldo(String noSaldo) {
        this.noSaldo = noSaldo;
    }

}

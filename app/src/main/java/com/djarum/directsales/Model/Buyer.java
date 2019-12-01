package com.djarum.directsales.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Buyer implements Parcelable {
    private String alamat;
    private String domisili;
    private String email;
    private String gender;
    private String nama;
    private String notes;
    private String phoneNumber;
    private String buyerId;
    private String photoURL;

    public Buyer() {
    }


    protected Buyer(Parcel in) {
        alamat = in.readString();
        domisili = in.readString();
        email = in.readString();
        gender = in.readString();
        nama = in.readString();
        notes = in.readString();
        phoneNumber = in.readString();
        buyerId = in.readString();
        photoURL = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(alamat);
        dest.writeString(domisili);
        dest.writeString(email);
        dest.writeString(gender);
        dest.writeString(nama);
        dest.writeString(notes);
        dest.writeString(phoneNumber);
        dest.writeString(buyerId);
        dest.writeString(photoURL);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Buyer> CREATOR = new Creator<Buyer>() {
        @Override
        public Buyer createFromParcel(Parcel in) {
            return new Buyer(in);
        }

        @Override
        public Buyer[] newArray(int size) {
            return new Buyer[size];
        }
    };

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getDomisili() {
        return domisili;
    }

    public void setDomisili(String domisili) {
        this.domisili = domisili;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }
}

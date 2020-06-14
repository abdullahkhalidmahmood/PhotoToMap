package com.example.phototomap.model;

import android.graphics.Bitmap;
import android.net.Uri;

public class LocationModel {
    String id, lat, lng, address;
    byte[] img;

    public LocationModel(String id, String lat, String lng, String address, byte[] img) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.img = img;
    }
    public LocationModel() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }
}

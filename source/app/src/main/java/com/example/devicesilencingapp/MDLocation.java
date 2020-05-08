package com.example.devicesilencingapp;

public class MDLocation {
    private String trangthai;
    private String tendiadiem;
    private String diadiem;
    private String img;

    public MDLocation(String trangthai, String tendiadiem, String diadiem, String img) {
        this.trangthai = trangthai;
        this.tendiadiem = tendiadiem;
        this.diadiem = diadiem;
        this.img = img;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }

    public String getTendiadiem() {
        return tendiadiem;
    }

    public void setTendiadiem(String tendiadiem) {
        this.tendiadiem = tendiadiem;
    }

    public String getDiadiem() {
        return diadiem;
    }

    public void setDiadiem(String diadiem) {
        this.diadiem = diadiem;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}

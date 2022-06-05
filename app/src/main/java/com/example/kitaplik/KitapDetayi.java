package com.example.kitaplik;

import android.graphics.Bitmap;

public class KitapDetayi {


    private Integer kitapId;
    private String kitapAdi, kitapYazari, kitapOzeti;
    private Bitmap kitapResimi;

    public KitapDetayi(Integer kitapId,String kitapAdi, String kitapYazari, String kitapOzeti, Bitmap kitapResimi) {
        this.kitapId=kitapId;
        this.kitapAdi = kitapAdi;
        this.kitapYazari = kitapYazari;
        this.kitapOzeti = kitapOzeti;
        this.kitapResimi = kitapResimi;
    }
    public Integer getKitapId() {
        return kitapId;
    }
    public String getKitapAdi() {
        return kitapAdi;
    }

    public String getKitapYazari() {
        return kitapYazari;
    }

    public String getKitapOzeti() {
        return kitapOzeti;
    }

    public Bitmap getKitapResimi() {
        return kitapResimi;
    }
}

package com.example.kitaplik;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    private Integer kitapId;

    private ImageView imgKitapResimi;
    private TextView txtKitapAdi, txtKitapYazari, txtKitapOzeti;
    private String kitapAdi, kitapYazari, kitapOzeti; //Burda tekrar değişken tanımlamamıza gerek yoktu
                                                      //Kodu kısaltmak için tanımlıyoruz
    private Bitmap kitapResimi;
    private Button btnSil,btnGuncelle;

    private void init()
    {
        txtKitapAdi = (TextView) findViewById(R.id.detay_activity_textViewKitapAdi);
        txtKitapYazari = (TextView) findViewById(R.id.detay_activity_textViewKitapYazari);
        txtKitapOzeti = (TextView) findViewById(R.id.detay_activity_textViewKitapOzeti);
        imgKitapResimi = (ImageView) findViewById(R.id.detay_activity_imageViewKitapResim);
        btnSil = (Button) findViewById(R.id.btnSil);
        btnGuncelle = (Button) findViewById(R.id.btnGuncelle);


        kitapAdi = MainActivity.kitapDetayi.getKitapAdi();
        kitapYazari = MainActivity.kitapDetayi.getKitapYazari();
        kitapOzeti = MainActivity.kitapDetayi.getKitapOzeti();
        kitapResimi = MainActivity.kitapDetayi.getKitapResimi();
        kitapId = MainActivity.kitapDetayi.getKitapId();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        init();

        if(!TextUtils.isEmpty(kitapAdi) && !TextUtils.isEmpty(kitapYazari) && !TextUtils.isEmpty(kitapOzeti))
        {
            txtKitapAdi.setText(kitapAdi);
            txtKitapYazari.setText(kitapYazari);
            txtKitapOzeti.setText(kitapOzeti);
            imgKitapResimi.setImageBitmap(kitapResimi);
        }

        btnSil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kitapSil(kitapId);
            }
        });

        btnGuncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kitapGuncelle(kitapId);
            }
        });
    }

    public void kitapSil(Integer id) {
        SQLiteDatabase database = this.openOrCreateDatabase("Kitaplar",MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS kitaplar (id Integer PRIMARY KEY AUTOINCREMENT, " +
                "kitapAdi VARCHAR, kitapYazari VARCHAR, kitapOzeti VARCHAR, kitapResim BLOB)");
        String sqlSorgusu = "Delete from kitaplar where id='"+id+"'";
        SQLiteStatement statement = database.compileStatement(sqlSorgusu);
        statement.execute();
        AddBookActivity a = new AddBookActivity();
        //a.showToast("Kitap Silindi");
        Intent detayIntent = new Intent(DetailsActivity.this, MainActivity.class);
        startActivity(detayIntent);
    }

    public void kitapGuncelle(Integer id) {
        Intent detayIntent = new Intent(DetailsActivity.this, GuncelleActivity.class);
        detayIntent.putExtra("kitapID",kitapId);
        startActivity(detayIntent);
    }
}
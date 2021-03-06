package com.example.kitaplik;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class GuncelleActivity extends AppCompatActivity {

    private EditText editTextKitapIsmi, editTextKitapYazari, editTextKitapOzet;
    private ImageView imgKitapResim;
    private Button buttonGuncelle;
    private String kitapIsmi, kitapYazari, kitapOzeti;
    private int imgIzinAlmaKodu = 0, imgIzinAlKodu = 1;
    private Bitmap secilenResim, kucultulenResim, enBastakiResim;
    private Integer gelenKitapId;

    private void init() {
        editTextKitapIsmi = findViewById(R.id.editTextKitapIsmÄ±);
        editTextKitapYazari = findViewById(R.id.editTextYazarAdÄ±);
        editTextKitapOzet = findViewById(R.id.editTextKitapOzeti);
        buttonGuncelle = findViewById(R.id.btnGuncelle);
        imgKitapResim = findViewById(R.id.imageGuncelle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guncelle);
        Intent intent = getIntent();
        gelenKitapId=intent.getIntExtra("kitapID",0);
        init();
    }

    public void kitapGuncelle(View v) {
        kitapIsmi = editTextKitapIsmi.getText().toString();
        kitapYazari = editTextKitapYazari.getText().toString();
        kitapOzeti = editTextKitapOzet.getText().toString();

        if (!TextUtils.isEmpty(kitapIsmi)) {
            if (!TextUtils.isEmpty(kitapYazari)) {
                if (!TextUtils.isEmpty(kitapOzeti)) {
                    //GÃ¼ncelle
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();   // Resimlerin formatlanmasÄ±
                    kucultulenResim = resmiKucult(secilenResim);
                    kucultulenResim.compress(Bitmap.CompressFormat.PNG, 75, outputStream);
                    byte[] kaydedilecekResim = outputStream.toByteArray();

                    try {       // VERÄ°TABANI
                        SQLiteDatabase database = this.openOrCreateDatabase("Kitaplar",MODE_PRIVATE, null);
                        database.execSQL("CREATE TABLE IF NOT EXISTS kitaplar (id INTEGER PRIMARY KEY, kitapAdi VARCHAR, " +
                                "kitapYazari VARCHAR, kitapOzeti VARCHAR, kitapResim BLOB)");
                        String sqlSorgusu = "Update kitaplar set kitapAdi=?, kitapYazari=?, kitapOzeti=?, kitapResim=? where id='"+gelenKitapId+"'";
                        SQLiteStatement statement = database.compileStatement(sqlSorgusu);
                        statement.bindString(1,kitapIsmi);
                        statement.bindString(2,kitapYazari);
                        statement.bindString(3,kitapOzeti);
                        statement.bindBlob(4,kaydedilecekResim);
                        statement.execute();

                        nesneleriTemizle();
                        showToast("Kitap GÃ¼ncellendi.");
                        Intent detayIntent = new Intent(GuncelleActivity.this, MainActivity.class);
                        startActivity(detayIntent);
                        finish();
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                } else
                    showToast("Kitap Ãzeti BoÅ BÄ±rakÄ±lamaz");
            } else
                showToast("Kitap YazarÄ± BoÅ BÄ±rakÄ±lamaz");
        } else
            showToast("Kitap AdÄ± BoÅ BÄ±rakÄ±lamaz");
    }

    private Bitmap resmiKucult(Bitmap resim)
    {
        //RECYCLEVIEW'DE KASMAMASI Ä°ÃÄ°N RESÄ°M BOYUTUNU YARIYA Ä°NDÄ°REN FONKSÄ°YON
        return Bitmap.createScaledBitmap(resim, 120, 150, true);
    }

    private void showToast(String mesaj) {
        Toast.makeText(getApplicationContext(), mesaj, Toast.LENGTH_SHORT).show();
    }

    private void nesneleriTemizle()
    {
        editTextKitapIsmi.setText("");
        editTextKitapYazari.setText("");
        editTextKitapOzet.setText("");
        enBastakiResim = BitmapFactory.decodeResource(this.getResources(), R.drawable.bosresim);
        imgKitapResim.setImageBitmap(enBastakiResim);
        buttonGuncelle.setEnabled(true);
    }

    public void resimSec2(View v)
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, imgIzinAlmaKodu);
        }
        else
        {
            Intent resimiAl = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(resimiAl, imgIzinAlKodu);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // KULLANICININ Ä°ZÄ°N VERÄ°P VERMEDÄ°ÄÄ°NÄ° KONTROL EDER
        if(requestCode == imgIzinAlmaKodu)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) //KullanÄ±cÄ± izin vermiÅse
            {
                Intent resimiAl = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(resimiAl, imgIzinAlKodu);    // FONKSÄ°YON KULLANIMDAN KALDIRILMIÅ
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // GALERÄ°YE GÄ°TTÄ°KTEN SONRA SEÃÄ°LEN RESMÄ°N DEÄERÄ°NÄ° ALIR
        if(requestCode == imgIzinAlKodu)
        {
            if(resultCode == RESULT_OK && data != null) //KullanÄ±cÄ± resmi seÃ§miÅse
            {
                Uri resimUri = data.getData();

                try {
                    if(Build.VERSION.SDK_INT >= 28)     // BITMAP YENÄ° SÃRÃM
                    {
                        ImageDecoder.Source resimSource = ImageDecoder.createSource(this.getContentResolver(), resimUri);
                        secilenResim = ImageDecoder.decodeBitmap(resimSource);
                        imgKitapResim.setImageBitmap(secilenResim);
                    }
                    else                                // BITMAP ESKÄ° SÃRÃM
                    {
                        secilenResim = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resimUri);
                        imgKitapResim.setImageBitmap(secilenResim);
                    }

                    //buttonGuncelle.setEnabled(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {   //geri gelindiÄinde Ã§alÄ±ÅÄ±yor
        Intent backIntent = new Intent(this, MainActivity.class);
        finish();
        startActivity(backIntent);
    }
}
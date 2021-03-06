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

public class AddBookActivity extends AppCompatActivity {

    private EditText editTextKitapIsmi, editTextKitapYazari, editTextKitapOzet;
    private ImageView imgKitapResim;
    private Button buttonKaydet;
    private String kitapIsmi, kitapYazari, kitapOzeti;
    private int imgIzinAlmaKodu = 0, imgIzinAlKodu = 1;
    private Bitmap secilenResim, kucultulenResim, enBastakiResim;

    private void init() {
        editTextKitapIsmi = findViewById(R.id.add_book_activity_editTextKitapIsmi);
        editTextKitapYazari = findViewById(R.id.add_book_activity_editTextKitapYazari);
        editTextKitapOzet = findViewById(R.id.add_book_activity_editTextKitapOzeti);
        buttonKaydet = findViewById(R.id.btnKaydet);
        imgKitapResim = findViewById(R.id.add_book_activity_imageViewKitapResmi);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        init();
    }

    public void kitapKaydet(View v) {
        kitapIsmi = editTextKitapIsmi.getText().toString();
        kitapYazari = editTextKitapYazari.getText().toString();
        kitapOzeti = editTextKitapOzet.getText().toString();

        if (!TextUtils.isEmpty(kitapIsmi)) {
            if (!TextUtils.isEmpty(kitapYazari)) {
                if (!TextUtils.isEmpty(kitapOzeti)) {
                    //Kaydet
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();   // Resimlerin formatlanmas??
                    kucultulenResim = resmiKucult(secilenResim);
                    kucultulenResim.compress(Bitmap.CompressFormat.PNG, 75, outputStream);
                    byte[] kaydedilecekResim = outputStream.toByteArray();

                    try {       // VER??TABANI
                        SQLiteDatabase database = this.openOrCreateDatabase("Kitaplar",MODE_PRIVATE, null);
                        database.execSQL("CREATE TABLE IF NOT EXISTS kitaplar (id INTEGER PRIMARY KEY, kitapAdi VARCHAR, kitapYazari VARCHAR," +
                                " kitapOzeti VARCHAR, kitapResim BLOB)");
                        String sqlSorgusu = "INSERT INTO kitaplar (kitapAdi, kitapYazari, kitapOzeti, kitapResim) VALUES (?,?,?,?)";
                        SQLiteStatement statement = database.compileStatement(sqlSorgusu);
                        statement.bindString(1,kitapIsmi);
                        statement.bindString(2,kitapYazari);
                        statement.bindString(3,kitapOzeti);
                        statement.bindBlob(4,kaydedilecekResim);
                        statement.execute();

                        nesneleriTemizle();
                        showToast("Kitap Kaydedildi");
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                } else
                    showToast("Kitap ??zeti Bo?? B??rak??lamaz");
            } else
                showToast("Kitap Yazar?? Bo?? B??rak??lamaz");
        } else
            showToast("Kitap Ad?? Bo?? B??rak??lamaz");
    }

    private Bitmap resmiKucult(Bitmap resim)
    {
        //RECYCLEVIEW'DE KASMAMASI ??????N RES??M BOYUTUNU YARIYA ??ND??REN FONKS??YON
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
        buttonKaydet.setEnabled(false);
    }

    public void resimSec(View v)
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
    // KULLANICININ ??Z??N VER??P VERMED??????N?? KONTROL EDER
        if(requestCode == imgIzinAlmaKodu)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) //Kullan??c?? izin vermi??se
            {
                Intent resimiAl = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(resimiAl, imgIzinAlKodu);    // FONKS??YON KULLANIMDAN KALDIRILMI??
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    // GALER??YE G??TT??KTEN SONRA SE????LEN RESM??N DE??ER??N?? ALIR
        if(requestCode == imgIzinAlKodu)
        {
            if(resultCode == RESULT_OK && data != null) //Kullan??c?? resmi se??mi??se
            {
                Uri resimUri = data.getData();

                try {
                    if(Build.VERSION.SDK_INT >= 28)     // BITMAP YEN?? S??R??M
                    {
                        ImageDecoder.Source resimSource = ImageDecoder.createSource(this.getContentResolver(), resimUri);
                        secilenResim = ImageDecoder.decodeBitmap(resimSource);
                        imgKitapResim.setImageBitmap(secilenResim);
                    }
                    else                                // BITMAP ESK?? S??R??M
                    {
                        secilenResim = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resimUri);
                        imgKitapResim.setImageBitmap(secilenResim);
                    }

                    buttonKaydet.setEnabled(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {   //geri gelindi??inde ??al??????yor
        Intent backIntent = new Intent(this, MainActivity.class);
        finish();
        startActivity(backIntent);
    }
}
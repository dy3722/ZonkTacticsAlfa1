package com.example.zonktacticsalfa1;

import static com.example.zonktacticsalfa1.FBRef.refPhoto;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TwoActivity extends MasterActivity {
    private static final int REQUEST_PHOTO_IMAGE = 201;
    private String lastPhoto;
    private StorageReference refImg;
    private ImageView iv2;
    private File localFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);

        iv2 = findViewById(R.id.iv2);

        SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
        lastPhoto = sp.getString("lastPhoto", null);
    }

    public void takePhoto(View view) {
        Intent takePicIntent = new Intent();
        takePicIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePicIntent, REQUEST_PHOTO_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data_back) {
        super.onActivityResult(requestCode, resultCode, data_back);

        if (resultCode == Activity.RESULT_OK && data_back != null) {
            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
            final ProgressDialog pd;
            switch (requestCode) {
                case (REQUEST_PHOTO_IMAGE):
                    Bundle extras = data_back.getExtras();
                    if (extras != null) {
                        pd = ProgressDialog.show(this, "העלת תמונה", "העלה...", true);
                        lastPhoto = dateFormat.format(date);
                        refImg = refPhoto.child(lastPhoto + ".png");
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] data = baos.toByteArray();
                        refImg.putBytes(data)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        pd.dismiss();
                                        SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
                                        sp.edit().putString("lastPhoto", lastPhoto).apply();
                                        Toast.makeText(TwoActivity.this, "התמונה הועלתה!", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        pd.dismiss();
                                        Toast.makeText(TwoActivity.this, "שגיאה בהעלה!!!", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                    break;
            }
        }
    }

    public void downloadPhoto(View view) {
        if (lastPhoto == null || lastPhoto.isEmpty()) {
            Toast.makeText(this, "אין תמונה להורדה... קודם תעלה/י תמונה!", Toast.LENGTH_LONG).show();
            return;
        }

        refImg = refPhoto.child(lastPhoto + ".png");
        try {
            localFile = File.createTempFile(lastPhoto, ".png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final ProgressDialog pd = ProgressDialog.show(this, "הורדת התמונה", "הורדה...", true);
        refImg.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Toast.makeText(TwoActivity.this, "התמונה ירדה בהצלחה!", Toast.LENGTH_LONG).show();
                String filePath = localFile.getPath();
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                iv2.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                pd.dismiss();
                Toast.makeText(TwoActivity.this, "שגיאה בהורדת התמונה!!!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
package com.example.zonktacticsalfa1;

import static com.example.zonktacticsalfa1.FBRef.refGallery;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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

public class OneActivity extends MasterActivity {
    private static final int REQUEST_PICK_IMAGE = 301;
    private String lastGallery;
    private StorageReference refImg;
    private ImageView iv1;
    private File localFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);

        iv1 = findViewById(R.id.iv1);

        SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
        lastGallery = sp.getString("lastGallery", null);
    }

    public void photoChoose(View view) {
        Intent si = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(si, REQUEST_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data_back) {
        super.onActivityResult(requestCode, resultCode, data_back);

        if (resultCode == Activity.RESULT_OK && data_back != null) {
            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
            final ProgressDialog pd;
            switch (requestCode) {
                case REQUEST_PICK_IMAGE:
                    Uri file = data_back.getData();
                    if (file != null) {
                        pd = ProgressDialog.show(this, "העלת תמונה", "העלה...", true);
                        lastGallery = dateFormat.format(date);
                        refImg = refGallery.child(lastGallery + ".jpg");
                        refImg.putFile(file)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        pd.dismiss();
                                        SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
                                        sp.edit().putString("lastGallery", lastGallery).apply();
                                        Toast.makeText(OneActivity.this, "התמונה הועלתה!", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        pd.dismiss();
                                        Toast.makeText(OneActivity.this, "שגיאה בהעלה!!!", Toast.LENGTH_LONG).show();
                                    }
                                });
                    } else {
                        Toast.makeText(this, "לא נבחרה תמונה...", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    }

    public void downloadPhoto(View view) {
        if (lastGallery == null || lastGallery.isEmpty()) {
            Toast.makeText(this, "אין תמונה להורדה... קודם תעלה/י תמונה!", Toast.LENGTH_LONG).show();
            return;
        }

        refImg = refGallery.child(lastGallery + ".jpg");
        try {
            localFile = File.createTempFile(lastGallery, ".jpg");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final ProgressDialog pd = ProgressDialog.show(this, "הורדת התמונה", "הורדה...", true);
        refImg.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Toast.makeText(OneActivity.this, "התמונה ירדה בהצלחה!", Toast.LENGTH_LONG).show();
                String filePath = localFile.getPath();
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                iv1.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                pd.dismiss();
                Toast.makeText(OneActivity.this, "שגיאה בהורדת התמונה!!!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
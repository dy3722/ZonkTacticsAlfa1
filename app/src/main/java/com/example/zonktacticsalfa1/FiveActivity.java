package com.example.zonktacticsalfa1;

import static com.example.zonktacticsalfa1.FBRef.refJSON;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class FiveActivity extends MasterActivity {
    private EditText etA, etB, etC;
    private StorageReference refFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five);

        etA = findViewById(R.id.etA);
        etB = findViewById(R.id.etB);
        etC = findViewById(R.id.etC);

        refFile = refJSON.child("coefficients.json");
    }

    public void uploadJSON(View view) {
        String a = etA.getText().toString().trim();
        String b = etB.getText().toString().trim();
        String c = etC.getText().toString().trim();

        if (a.isEmpty() && b.isEmpty() && c.isEmpty()) {
            Toast.makeText(this, "נא להזין ערכים לפחות באחד מהשדות", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("a", a);
            jsonObject.put("b", b);
            jsonObject.put("c", c);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonString = jsonObject.toString();
        byte[] bytes = jsonString.getBytes(StandardCharsets.UTF_8);

        final ProgressDialog pd = ProgressDialog.show(this, "העלאת JSON", "מעלה קובץ...", true);
        refFile.putBytes(bytes)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
                        sp.edit().putBoolean("isJsonUploaded", true).apply();
                        Toast.makeText(FiveActivity.this, "קובץ ה-JSON הועלה בהצלחה!", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        Toast.makeText(FiveActivity.this, "שגיאה בהעלאת הקובץ!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void clear(View view) {
        etA.setText("");
        etB.setText("");
        etC.setText("");
    }

    public void downloadJSON(View view) {
        SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean isUploaded = sp.getBoolean("isJsonUploaded", false);
        if (!isUploaded) {
            Toast.makeText(this, "אין קובץ להורדה... קודם תעלה/י קובץ!", Toast.LENGTH_LONG).show();
            return;
        }

        final ProgressDialog pd = ProgressDialog.show(this, "הורדת JSON", "מוריד קובץ...", true);
        final long ONE_MEGABYTE = 1024 * 1024;
        refFile.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        pd.dismiss();
                        try {
                            String jsonString = new String(bytes, StandardCharsets.UTF_8);
                            JSONObject jsonObject = new JSONObject(jsonString);
                            String a = jsonObject.optString("a", "");
                            String b = jsonObject.optString("b", "");
                            String c = jsonObject.optString("c", "");

                            etA.setText(a);
                            etB.setText(b);
                            etC.setText(c);

                            Toast.makeText(FiveActivity.this, "הקובץ הורד ועובד בהצלחה!", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            Toast.makeText(FiveActivity.this, "שגיאה בפענוח ה-JSON!", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        Toast.makeText(FiveActivity.this, "שגיאה בהורדת הקובץ מהענן!", Toast.LENGTH_LONG).show();
                    }
                });
    }
}

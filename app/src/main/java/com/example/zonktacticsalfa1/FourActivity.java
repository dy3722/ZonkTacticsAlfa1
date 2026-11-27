package com.example.zonktacticsalfa1;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class FourActivity extends MasterActivity {

    private TextView tvInfoListen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four);

        tvInfoListen = findViewById(R.id.tvInfoListen);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        if (NetworkReceiver.isListening()) {
            tvInfoListen.setText("מאזין כעת לחיבור אינטרנט");
            tvInfoListen.setTextColor(Color.parseColor("#008000"));
        } else {
            tvInfoListen.setText("לא מאזין כעת");
            tvInfoListen.setTextColor(Color.parseColor("#DB0000"));
        }
    }

    public void startListen(View view) {
        if (!NetworkReceiver.isListening()) {
            NetworkReceiver.startListening(this);
            updateUI();
            Toast.makeText(this, "האזנה לחיבור אינטרנט הופעלה", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "ההאזנה כבר פעילה", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopListen(View view) {
        if (NetworkReceiver.isListening()) {
            NetworkReceiver.stopListening(this);
            updateUI();
            Toast.makeText(this, "האזנה לחיבור אינטרנט הופסקה", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "ההאזנה אינה פעילה", Toast.LENGTH_SHORT).show();
        }
    }
}

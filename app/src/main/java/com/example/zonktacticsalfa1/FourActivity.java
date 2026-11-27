package com.example.zonktacticsalfa1;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class FourActivity extends MasterActivity {

    private TextView tvInfoListen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four);

        tvInfoListen = findViewById(R.id.tvInfoListen);
    }

    public void startListen(View view) {
    }

    public void stopListen(View view) {
    }
}

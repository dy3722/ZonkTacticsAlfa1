package com.example.zonktacticsalfa1;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MasterActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuActivity1) {
            openActivity(OneActivity.class);
        } else if (id == R.id.menuActivity2) {
            openActivity(TwoActivity.class);
        } else if (id == R.id.menuActivity3) {
            openActivity(ThreeActivity.class);
        } else if (id == R.id.menuActivity4) {
            openActivity(FourActivity.class);
        } else if (id == R.id.menuActivity5) {
            openActivity(FiveActivity.class);
        } else if (id == R.id.menuActivity6) {
            openActivity(SixActivity.class);
        } else if (id == R.id.menuActivity7) {
            openActivity(SevenActivity.class);
        } else if (id == R.id.menuActivity8) {
            openActivity(EightActivity.class);
        } else if (id == R.id.menuActivity9) {
            openActivity(NineActivity.class);
        }

        return super.onOptionsItemSelected(item);
    }

    private void openActivity(Class<?> targetActivityClass) {
        if (this.getClass() != targetActivityClass) {
            startActivity(new Intent(this, targetActivityClass));
            finish();
        }
    }
}

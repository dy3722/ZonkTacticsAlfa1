package com.example.zonktacticsalfa1;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class ThreeActivity extends MasterActivity {
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private int ALARM_RQST_CODE;
    private EditText editTextTextForNoti;
    private String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);

        editTextTextForNoti = findViewById(R.id.editTextTextForNoti);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences alarmsPrefs = getSharedPreferences("AlarmsPrefs", MODE_PRIVATE);
        ALARM_RQST_CODE = alarmsPrefs.getInt("last_request_code", 0);
    }

    private void openTimePickerDialog(boolean is24r) {
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), is24r);
        timePickerDialog.setTitle("בחר שעה");
        timePickerDialog.show();
    }

    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            Calendar calNow = Calendar.getInstance();
            Calendar calSet = (Calendar) calNow.clone();

            calSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calSet.set(Calendar.MINUTE, minute);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);

            if (calSet.compareTo(calNow) <= 0) {
                calSet.add(Calendar.DATE, 1);
            }
            setAlarm(calSet);
        }
    };

    private void setAlarm(Calendar calSet) {
        ALARM_RQST_CODE++;
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("msg", msg);
        alarmIntent = PendingIntent.getBroadcast(this,
                ALARM_RQST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.set(AlarmManager.RTC_WAKEUP,
                calSet.getTimeInMillis(), alarmIntent);
        saveAlarmParameters(ALARM_RQST_CODE, calSet.getTimeInMillis(), false, false);

        String timeStr = String.format("%02d:%02d", calSet.get(Calendar.HOUR_OF_DAY), calSet.get(Calendar.MINUTE));
        Toast.makeText(this, "ההתראה הוגדרה לשעה " + timeStr, Toast.LENGTH_SHORT).show();
        Log.i("ThreeActivity", "setAlarm: Set TOD alarm with requestCode = " + ALARM_RQST_CODE + " for " + calSet.getTime());
    }

    private void saveAlarmParameters(int requestCode, long timeInMillis, boolean repeat, boolean snooze) {
        SharedPreferences alarmsPrefs = getSharedPreferences("AlarmsPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = alarmsPrefs.edit();
        if (repeat) {
            if (snooze) {
                editor.putLong("snooze_repeat_alarm_time_" + requestCode, timeInMillis);
            } else {
                editor.putLong("repeat_alarm_time_" + requestCode, timeInMillis);
            }
        } else {
            editor.putLong("alarm_time_" + requestCode, timeInMillis);
        }
        editor.putInt("last_request_code", requestCode);
        editor.apply();
        Log.d("ThreeActivity", "saveAlarmParameters: Saved alarm [requestCode=" + requestCode + ", timeInMillis=" + timeInMillis + ", repeat=" + repeat + ", snooze=" + snooze + "]");
    }

    public void choseTime(View view) {
        msg = editTextTextForNoti.getText().toString();
        if (msg.trim().isEmpty()) {
            Toast.makeText(this, "נא להקליד תוכן להודעה לפני בחירת השעה", Toast.LENGTH_SHORT).show();
            return;
        }
        openTimePickerDialog(true);
    }
}

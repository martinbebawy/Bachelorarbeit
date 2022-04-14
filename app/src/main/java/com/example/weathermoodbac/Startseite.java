package com.example.weathermoodbac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Startseite extends BaseActivity implements View.OnClickListener {
    Button btn_stimmungserfassung, btn_auswertung, btn_einstellungen, btn_log_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startseite);
        checkPermissions();
        btn_stimmungserfassung = findViewById(R.id.stimmungserfassung);
        btn_auswertung = findViewById(R.id.auswertung_eintellungen);
        btn_einstellungen = findViewById(R.id.einstellungen);
        btn_log_out = findViewById(R.id.log_out);
        btn_stimmungserfassung.setOnClickListener(this);
        btn_auswertung.setOnClickListener(this);
        btn_einstellungen.setOnClickListener(this);
        btn_log_out.setOnClickListener(this);
        startNotificator();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (didChangeStartseite) {
            recreate();
            didChangeStartseite = false;
        }
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Startseite.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0:
                if (!(grantResults.length > 0) || !(grantResults[0] == PackageManager.PERMISSION_GRANTED) || !(grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Startseite.this,R.style.RoundedDialog);
                    builder.setMessage("Für die Nutzung dieser App ist der Standortzugriff erforderlich. Bitte starten Sie die App erneut.")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialog, id) -> finish());
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        Context ctx = v.getContext();
        switch (v.getId()) {
            case R.id.stimmungserfassung:
                intent = new Intent(ctx, Stimmungserfassung.class);
                startActivity(intent);
                break;
            case R.id.auswertung_eintellungen:
                intent = new Intent(ctx, AuswertungEinstellungen.class);
                startActivity(intent);
                break;
            case R.id.einstellungen:
                intent = new Intent(ctx, Einstellungen.class);
                startActivity(intent);
                break;
            case R.id.log_out:
                signOut();
                break;
            default:
                break;
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> finish());
    }

    private void startNotificator() {
        Notificator.initBeginAndEndTime();
        Notificator.createNotificationChannel(this);
        Notificator.loadSharedData(this);
        Notificator.initSharedData(this);
        Notificator.setNotification(this);
    }

}

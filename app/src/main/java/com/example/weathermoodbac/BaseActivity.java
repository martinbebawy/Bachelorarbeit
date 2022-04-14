package com.example.weathermoodbac;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.text.ParseException;
import java.util.Calendar;

public class BaseActivity extends AppCompatActivity {
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String NOTIFICATION_TIME = null;
    public static final String CONTRAST = ""; //0..low, 1..middle, 2..high
    public static final String FONT = ""; //0..low, 1..middle, 2..high
    public static boolean didChangeStartseite;
    public static boolean didChangeAnmeldung;
    final String urlWebserver = "http://192.168.0.192";
    public GoogleSignInOptions gso;
    public GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContrastInSharedPrefOnCreate();
        setFontInSharedPrefOnCreate();
    }


    public void setContrastInSharedPref(int val) {
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, this.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("CONTRAST", sharedPreferences.getInt("CONTRAST", 99) + val);
        editor.commit();
        didChangeStartseite = true;
        didChangeAnmeldung = true;
        recreate();
    }

    public void setFontInSharedPref(int val) {
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, this.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("FONT", sharedPreferences.getInt("FONT", 99) + val);
        editor.commit();
        didChangeStartseite = true;
        didChangeAnmeldung = true;
        recreate();
    }

    public int getContrastInSharedPref() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, this.MODE_PRIVATE);
        return sharedPreferences.getInt("CONTRAST", 99);
    }

    public int getFontInSharedPref() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, this.MODE_PRIVATE);
        return sharedPreferences.getInt("FONT", 99);
    }

    public void setContrastInSharedPrefOnCreate() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, this.MODE_PRIVATE);
        if (sharedPreferences.getInt("CONTRAST", 99) == 99) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("CONTRAST", 0);
            editor.commit();
        } else {
            switch (sharedPreferences.getInt("CONTRAST", 99)) {
                case 0:
                    setTheme(R.style.Theme_LowContrast);
                    break;
                case 1:
                    setTheme(R.style.Theme_MiddleContrast);
                    break;
                case 2:
                    setTheme(R.style.Theme_HighContrast);
                    break;
                default:
                    break;

            }
        }
    }

    public void setFontInSharedPrefOnCreate() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, this.MODE_PRIVATE);
        if (sharedPreferences.getInt("FONT", 99) == 99) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("FONT", 0);
            editor.commit();
        } else {
            switch (sharedPreferences.getInt("FONT", 99)) {
                case 0:
                    setTheme(R.style.FontSizeSmall);
                    break;
                case 1:
                    setTheme(R.style.FontSizeMedium);
                    break;
                case 2:
                    setTheme(R.style.FontSizeLarge);
                    break;
                default:
                    break;
            }
        }
    }
}
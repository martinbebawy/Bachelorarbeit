package com.example.weathermoodbac;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Array;

public class Einstellungen extends BaseActivity implements View.OnClickListener {
    Button btn_schriftgroeße_plus, btn_schriftgroeße_minus, btn_kontrast_plus, btn_kontrast_minus, btn_zurueck;
    TextView tv_kontrast_option, tv_schriftgroesse_option;
    String[] groesse = {"klein", "mittel", "groß"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_einstellungen);
        btn_schriftgroeße_plus = findViewById(R.id.einstellungen_schriftgröße_plus);
        btn_schriftgroeße_minus = findViewById(R.id.einstellungen_schriftgröße_minus);
        btn_kontrast_plus = findViewById(R.id.einstellungen_kontrast_plus);
        btn_kontrast_minus = findViewById(R.id.einstellungen_kontrast_minus);
        btn_zurueck = findViewById(R.id.einstellungen_zurueck);
        btn_schriftgroeße_plus.setOnClickListener(this);
        btn_schriftgroeße_minus.setOnClickListener(this);
        btn_kontrast_plus.setOnClickListener(this);
        btn_kontrast_minus.setOnClickListener(this);
        btn_zurueck.setOnClickListener(this);
        tv_kontrast_option = findViewById(R.id.einstellungen_kontrast_option);
        tv_kontrast_option.setText(groesse[this.getContrastInSharedPref()]);
        tv_schriftgroesse_option = findViewById(R.id.einstellungen_schriftgröße_option);
        tv_schriftgroesse_option.setText(groesse[this.getFontInSharedPref()]);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.einstellungen_schriftgröße_plus:
                if (tv_schriftgroesse_option.getText() != groesse[2]) {
                    tv_schriftgroesse_option.setText(groesse[this.getFontInSharedPref() + 1]);
                    this.setFontInSharedPref(1);
                    Log.d("1","111");
                }
                break;
            case R.id.einstellungen_schriftgröße_minus:
                if (tv_schriftgroesse_option.getText() != groesse[0]) {
                    tv_schriftgroesse_option.setText(groesse[this.getFontInSharedPref() - 1]);
                    this.setFontInSharedPref(-1);
                    Log.d("2","222");
                }
                break;
            case R.id.einstellungen_kontrast_plus:
                if (tv_kontrast_option.getText() != groesse[2]) {
                    tv_kontrast_option.setText(groesse[this.getContrastInSharedPref() + 1]);
                    this.setContrastInSharedPref(1);
                    Log.d("3","333");
                }
                break;
            case R.id.einstellungen_kontrast_minus:
                if (tv_kontrast_option.getText() != groesse[0]) {
                    tv_kontrast_option.setText(groesse[this.getContrastInSharedPref() - 1]);
                    this.setContrastInSharedPref(-1);
                    Log.d("4","444");
                }
                break;
            case R.id.einstellungen_zurueck:
                finish();
                break;
            default:
                break;
        }
    }
}
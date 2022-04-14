package com.example.weathermoodbac;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AuswertungEinstellungen extends BaseActivity implements View.OnClickListener {
    Button btn_plus, btn_minus, btn_zurueck, btn_weiter;
    TextView wetter_option;
    AlertDialog alert;
    RequestQueue queue;
    String responseStimmungen;
    int wetterSkala = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auswertung_einstellungen);
        btn_plus = findViewById(R.id.auswertung_einstellungen_plus);
        btn_minus = findViewById(R.id.auswertung_einstellungen_minus);
        btn_zurueck = findViewById(R.id.auswertung_einstellungen_zurueck);
        btn_weiter = findViewById(R.id.auswertung_einstellungen_weiter);
        btn_plus.setOnClickListener(this);
        btn_minus.setOnClickListener(this);
        btn_zurueck.setOnClickListener(this);
        btn_weiter.setOnClickListener(this);
        wetter_option = findViewById(R.id.auswertung_einstellungen_wetter_option);
        queue = Volley.newRequestQueue(AuswertungEinstellungen.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.auswertung_einstellungen_plus:
                if (wetterSkala == 0) {
                    wetter_option.setText("mittel");
                    wetterSkala += 1;
                } else if (wetterSkala == 1) {
                    wetter_option.setText("schön");
                    wetterSkala += 1;
                }
                break;
            case R.id.auswertung_einstellungen_minus:
                if (wetterSkala == 1) {
                    wetter_option.setText("schlecht");
                    wetterSkala -= 1;
                } else if (wetterSkala == 2) {
                    wetter_option.setText("mittel");
                    wetterSkala -= 1;
                }
                break;
            case R.id.auswertung_einstellungen_zurueck:
                finish();
                break;
            case R.id.auswertung_einstellungen_weiter:
                getStimmungen(() -> {
                    Intent intent = new Intent(v.getContext(), Auswertung.class);
                    intent.putExtra("stimmungen", responseStimmungen);
                    intent.putExtra("wetterOption", String.valueOf(wetter_option.getText()));
                    startActivityForResult(intent, 242);
                });
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 242) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    private void getStimmungen(final VolleyCallback callback) {
        ScheduledExecutorService executor =
                Executors.newSingleThreadScheduledExecutor();
        Runnable hideDialog = new Runnable() {
            public void run() {
                alert.dismiss();
                finish();
            }
        };
        String tempUrl = urlWebserver + "/selectStimmung.php";
        StringRequest requestLocalWebserver = new StringRequest(Request.Method.POST, tempUrl, response -> {
            Log.d("repsonseLocalWebserver", response);
            responseStimmungen = response;
            callback.onSuccess();
        }, error -> {
            Log.d("errorLocalWebserver", "" + error);
            AlertDialog.Builder builder = new AlertDialog.Builder(AuswertungEinstellungen.this, R.style.RoundedDialog);
            builder.setMessage("Keine Verbindung zum Server möglich. Bitte prüfen Sie ihre Internetverbindung.")
                    .setCancelable(false);
            alert = builder.create();
            alert.show();
            executor.schedule(hideDialog, 5, TimeUnit.SECONDS);
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(AuswertungEinstellungen.this);
                map.put("personId", account.getId());
                return map;
            }
        };
        queue.add(requestLocalWebserver);
    }

}
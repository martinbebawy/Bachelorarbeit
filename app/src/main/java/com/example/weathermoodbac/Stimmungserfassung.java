package com.example.weathermoodbac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Stimmungserfassung extends BaseActivity implements View.OnClickListener {
    Button btn_plus, btn_minus, btn_zurueck, btn_speichern;
    ImageView img_skala;
    final String urlOpenWeatherMap = "http://api.openweathermap.org/data/2.5/weather";
    final String apiKey = "dd6c25dac807969addc87f8ca302eb87";
    GPSTracker gps = new GPSTracker(this);
    Wetter wetter;
    RequestQueue queue;
    int skala = 2;
    ProgressDialog progressDialog;
    AlertDialog alert;
    GoogleSignInAccount acct;
    String personGivenName;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
    double latitude;
    double longitude;
    TextView text_wetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stimmungserfassung);
        btn_plus = findViewById(R.id.stimmungserfassung_plus);
        btn_minus = findViewById(R.id.stimmungserfassung_minus);
        btn_zurueck = findViewById(R.id.stimmungserfassung_zurueck);
        btn_speichern = findViewById(R.id.stimmungserfassung_speichern);
        btn_plus.setOnClickListener(this);
        btn_minus.setOnClickListener(this);
        btn_zurueck.setOnClickListener(this);
        btn_speichern.setOnClickListener(this);
        img_skala = findViewById(R.id.stimmungserfassung_skala);
        text_wetter = findViewById(R.id.text_wetter);
        gps.requestLocationUpdates();
        queue = Volley.newRequestQueue(Stimmungserfassung.this);
        progressDialog = new ProgressDialog(Stimmungserfassung.this);
        acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            personGivenName = acct.getGivenName();
        }
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();
        getWetter(() -> {
            if (wetter != null){
                double temp = wetter.getTemp();
                double gefuehlteTemperatur = 13.12 + (0.6215 * temp) + ((0.3965 * temp) - 11.37);
                if ((gefuehlteTemperatur >= 0) && (gefuehlteTemperatur <= 20)){
                    text_wetter.setText("Das aktuelle Wetter ist schön.");
                }else if((gefuehlteTemperatur > 20 && gefuehlteTemperatur <= 26) || (gefuehlteTemperatur >= -13 && gefuehlteTemperatur < 0)){
                    text_wetter.setText("Das aktuelle Wetter ist mittel.");
                }else{
                    text_wetter.setText("Das aktuelle Wetter ist schlecht.");
                }
            }
        }, latitude, longitude);
    }


    @Override
    protected void onStop() {
        super.onStop();
        gps.removeLocationUpdates();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stimmungserfassung_plus:
                if (skala == 0) {
                    img_skala.setImageResource(R.drawable.confused);
                    skala += 1;
                } else if (skala == 1) {
                    img_skala.setImageResource(R.drawable.smile);
                    skala += 1;
                }
                break;
            case R.id.stimmungserfassung_minus:
                if (skala == 1) {
                    img_skala.setImageResource(R.drawable.sad);
                    skala -= 1;
                } else if (skala == 2) {
                    img_skala.setImageResource(R.drawable.confused);
                    skala -= 1;
                }
                break;
            case R.id.stimmungserfassung_zurueck:
                finish();
                break;
            case R.id.stimmungserfassung_speichern:
                if (wetter != null){
                    insertWetterToLocalServer(() -> wetter = null, latitude, longitude);
                }
                //getWetter(() -> { //Methode onRepsone in getWetter ist asynchron --> deswegen Interface mit Methode onSuccess als Übergabeparameter für Methode getWetter
                //    if (wetter != null) {
                //        insertWetterToLocalServer(() -> wetter = null, latitude, longitude);
                //    }
                //}, latitude, longitude);

            default:
                break;
        }
    }

    private void getWetter(final VolleyCallback callback, double latitude, double longitude) {
        String tempUrl = urlOpenWeatherMap + "?lat=" + latitude + "&lon=" + longitude + "&appid=" + apiKey;
        StringRequest weatherRequest = new StringRequest(Request.Method.POST, tempUrl, response -> {
            double temp = 0;
            double rain = 0;
            int humidity = 0;
            int pressure = 0;
            double wind = 0;
            try {
                JSONObject jsonResponse = new JSONObject(response);
                JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                temp = jsonObjectMain.getDouble("temp") - 273.15; //-273.15 fuer Temperatur in Celsius
                humidity = jsonObjectMain.getInt("humidity");
                JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                wind = jsonObjectWind.getDouble("speed");
                pressure = jsonObjectMain.getInt("pressure");
                wetter = new Wetter(temp, rain, humidity, pressure, (wind * 3.6));
                callback.onSuccess();
            } catch (JSONException e) {
                e.printStackTrace();
                wetter = null;
            }
        }, error -> Toast.makeText(Stimmungserfassung.this, "Error OpenWeatherMap is:" + error, Toast.LENGTH_LONG).show());
        queue.add(weatherRequest);
    }

    private void insertWetterToLocalServer(final VolleyCallback callback, double latitude, double longitude) {
        String tempUrl = urlWebserver + "/stimmungserfassung.php";
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ScheduledExecutorService executor =
                Executors.newSingleThreadScheduledExecutor();
        Runnable hideDialog = new Runnable() {
            public void run() {
                alert.dismiss();
                finish();
            }
        };
        StringRequest requestLocalWebserver = new StringRequest(Request.Method.POST, tempUrl, response -> {
            Log.d("repsonseLocalWebserver", response);
            AlertDialog.Builder builder = new AlertDialog.Builder(Stimmungserfassung.this, R.style.RoundedDialog);
            builder.setMessage("Stimmung erfolgreich erfasst.")
                    .setCancelable(false);
            alert = builder.create();
            alert.show();
            executor.schedule(hideDialog, 5, TimeUnit.SECONDS);
            progressDialog.dismiss();
        }, error -> {
            Log.d("errorLocalWebserver", "" + error);
            AlertDialog.Builder builder = new AlertDialog.Builder(Stimmungserfassung.this, R.style.RoundedDialog);
            builder.setMessage("Keine Verbindung zum Server möglich.")
                    .setCancelable(false);
            alert = builder.create();
            alert.show();
            executor.schedule(hideDialog, 5, TimeUnit.SECONDS);
            progressDialog.dismiss();
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("personId", acct.getId());
                map.put("skala", String.valueOf(skala));
                map.put("temperatur", String.valueOf(wetter.getTemp()));
                map.put("luftfeuchtigkeit", String.valueOf(wetter.getHumidity()));
                map.put("latitude", String.valueOf(latitude));
                map.put("longitude", String.valueOf(longitude));
                map.put("wind", String.valueOf(wetter.getWind()));
                map.put("niederschlag", String.valueOf(wetter.getRain()));
                map.put("luftdruck", String.valueOf(wetter.getPressure()));
                Calendar res = Calendar.getInstance();
                map.put("zeitpunkt", sdf.format(res.getTime()));
                return map;
            }
        };
        queue.add(requestLocalWebserver);
    }


}
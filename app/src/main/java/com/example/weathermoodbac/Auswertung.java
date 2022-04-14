package com.example.weathermoodbac;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Auswertung extends BaseActivity {
    Button btn_startseite;
    BarChart barChart;
    TextView text_ausgabe, ueberschrift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auswertung);
        btn_startseite = findViewById(R.id.auswertung_zur_startseite);
        btn_startseite.setOnClickListener(v -> {
            setResult(-1);
            finish();
        });
        text_ausgabe = findViewById(R.id.text_ausgabe);
        ueberschrift = findViewById(R.id.ueberschrift);
        Bundle extras = getIntent().getExtras();
        String stimmungen = extras.getString("stimmungen");
        String wetterOption = extras.getString("wetterOption");
        int count[] = new int[3];
        int countGesamt = 0;
        int countProzent[] = {0, 0, 0};
        try {
            JSONArray jsonArray = new JSONArray(stimmungen);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString("h").equals(wetterOption)) {
                    count[i] = jsonObject.getInt("count(skala)");
                    countGesamt += count[i];
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (countGesamt != 0) {
            for (int i = 0; i < count.length; i++) {
                countProzent[i] = count[i] * 100 / countGesamt;
            }
            text_ausgabe.setText("Zu " + countProzent[0] + "% ging es Ihnen schlecht, zu " + countProzent[1] + "% mittelmäßig\nund zu " + countProzent[2] + "% schlecht.");
        } else {
            text_ausgabe.setText("Es wurden noch keine Stimmungen erfasst.");
        }

        switch (wetterOption) {
            case "schlecht":
                ueberschrift.setText("Auswertung für schlechtes Wetter");
                break;
            case "mittel":
                ueberschrift.setText("Auswertung für mittleres Wetter");
                break;
            case "schön":
                ueberschrift.setText("Auswertung für schönes Wetter");
                break;
            default:
                break;
        }

        barChart = (BarChart) findViewById(R.id.barChart);
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(countProzent[0], 0));
        entries.add(new BarEntry(countProzent[1], 1));
        entries.add(new BarEntry(countProzent[2], 2));
        BarDataSet bardataset = new BarDataSet(entries, "Stimmung in Prozent [%]");
        ArrayList<String> labels = new ArrayList<String>();
        labels.add("schlechte Stimmung");
        labels.add("mittlere Stimmung");
        labels.add("gute Stimmung");
        bardataset.setDrawValues(true);
        bardataset.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return String.valueOf((int) value);
            }
        });
        bardataset.setColor(Color.parseColor("#304567"));
        bardataset.setValueTextSize(25);
        BarData data = new BarData(labels, bardataset);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.setTouchEnabled(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setEnabled(true);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setTextSize(11);
        barChart.getLegend().setTextSize(30);
        barChart.setData(data); // set the data and list of labels into chart
        barChart.setDescription("");
        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREFS, this.MODE_PRIVATE);
        int sharedPreferencesInt = sharedPreferences.getInt("CONTRAST", 99);
        switch(sharedPreferencesInt){
            case 0:
                bardataset.setColors(ColorTemplate.PASTEL_COLORS);
                break;
            case 1:
                bardataset.setColors(ColorTemplate.JOYFUL_COLORS);
                break;
            case 2:
                bardataset.setColors(ColorTemplate.createColors(new int[]{Color.GRAY,Color.BLACK,Color.DKGRAY}));
                break;
            default:
                break;
        }
    }
}
package com.example.weathermoodbac;

public class Wetter {
    private double temp, rain ;
    private int humidity;
    private int pressure;
    private double wind;

    public Wetter(double temp, double rain, int humidity, int pressure, double wind) {
        this.temp = temp;
        this.rain = rain;
        this.humidity = humidity;
        this.pressure = pressure;
        this.wind = wind;
    }

    public double getTemp() {
        return temp;
    }

    public double getRain() {
        return rain;
    }

    public int getHumidity() {
        return humidity;
    }

    public float getPressure() {
        return pressure;
    }

    public double getWind() {
        return wind;
    }

    @Override
    public String toString() {
        return "Wetter{" +
                "temp=" + temp +
                ", rain=" + rain +
                ", humidity=" + humidity +
                ", pressure=" + pressure +
                ", wind='" + wind + '\'' +
                '}';
    }
}

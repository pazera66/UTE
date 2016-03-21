package com.example.pazera.biketrails;

/**
 * Created by pazera on 01-02-2016.
 */
public class Coords {
    private double lat;
    private double lng;
    final private int distance = 500;

    @Override public String toString() {
        return String.format("%.8f,%.8f,%d", lng, lat, distance);
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}

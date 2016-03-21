package com.example.pazera.biketrails;

import lombok.Data;

/**
 * Created by pazera on 31-01-2016.
 */
//@Data
public class Coordinates {

    double lat;

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    double lon;



}

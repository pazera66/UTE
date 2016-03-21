package com.example.pazera.biketrails;

import lombok.Data;

/**
 * Created by pazera on 31-01-2016.
 */
//@Data
public class Geometry {

    String type;
    Coordinates coordinates;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }
}

package com.example.pazera.biketrails;

import java.util.List;

/**
 * Created by pazera on 31-01-2016.
 */
public class Data{
    Geometry geometry;
    List<Property> properties;

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }


}

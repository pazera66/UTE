package com.example.pazera.biketrails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pazera on 19-08-2015.
 */
//@lombok.Data
public class Stacja {
    Data data;




    












    private int user_ID;
    private String name;
    private double distance;
    private int time;
    private double calories;
    private double avgSpeed;
    private List<Double> latitudeList = new ArrayList<Double>();
    private List<Double> longtitudeList = new ArrayList<Double>();

}

package com.example.pazera.biketrails;


import com.google.android.gms.gcm.Task;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.EncodedQuery;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by pazera on 14-08-2015.
 */
public interface NetworkAPI {

    @GET("/login.php")
    public void getLoginResponse(@Header("Authorization") String authorization, Callback<DataForServer> cb);

    @POST("/insertUser.php")
    public void getRegisterResponse(@Body DataForServer dataForServer, @Header("Authorization") String authorization, Callback<RegisterResponse> rr);

    @POST("/getMyTrails.php")
    public void getMyTrails(@Body DataForServer dataForServer, Callback<List<Trail>> myTrailsCallbackList);

    @POST("/getAllTrails.php")
    public void getAllTrails(@Body DataForServer dataForServer, Callback<List<Trail>> allTrailsCallbackList);

    @POST("/insertTrail.php")
    public void insertTrail(@Body Trail trailToSend, Callback<DataForServer> dataForServer);

    @GET("/sendsms")
    public void sendSMS(@Header("Authorization") String authorization, @Query("to") String to, @Query("from") String from, @Query("msg") String tresc, Callback<SmsResult> cb);

    @GET("/veturilo")
    public void getVeturilo(@Header("Authorization") String authorization,  @EncodedQuery("circle") Coords coords, @Query("maxFeatures") int feat, Callback<Top> top);

    @GET("/metroEntrances")
    public void getMetro(@Header("Authorization") String authorization,  @EncodedQuery("circle") Coords coords, @Query("maxFeatures") int feat, Callback<Top2> top2);

    /*@GET("/veturilo")
    public void getVeturilo(@Header("Authorization") String authorization,  @Query("circle") String query, Callback<List<Data>> data);*/

    /*@GET("/veturilo?circle={lon},{lat},1000&maxFeatures=1")
    public void getVeturilo(@Header("Authorization") String authorization,  @Query("lon") double lon, @Query("lat") double lat, Callback<List<Data>> data);*/
}

package com.example.pazera.biketrails;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import android.os.Handler;
import android.widget.Toast;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import retrofit.Callback;
import retrofit.Endpoint;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.OkClient;
import retrofit.client.Response;

/**
 * Created by pazera on 17-08-2015.
 */
public class MapActivity extends FragmentActivity implements

        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static GoogleMap map;
    private MapFragment mapFragment;
    private FusedLocationProviderApi locationProvider = LocationServices.FusedLocationApi;
    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 5; //15 seconds  interval
    private static final long FASTEST_INTERVAL = 1000 * 4;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private Location currentLocation;
    PolylineOptions lineDrawer;
    private String username, password, userID, trailname;
    private Trail trail = new Trail();
    private int DIALOGFLAG = 0, seconds = 0, minutes = 0, hours = 0, meters = 0,
            mseconds = 0;
    private float zoomLevel = 0, avgSpeed = 0;
    boolean pauseFlag = false, trackingFlag = false, cameraFlag = true;
    public static boolean FOLLOWINGFLAG = false;
    Communicator2 comm2;
    EditText trailNameEditText;
    View view;
    AlertDialog dialog;
    List<Data> data;

    private Handler handler;
    private boolean Running = true;
    private Thread timerThread;
    private String timeTemplate;

    private Button pauseButton, startStopButton, gotoMyTrailsButton, browseToMyTrailsButton;
    private TextView timeView, caloriesView, distanceView, speedView;

    private Polyline line;
    private double currentLatitude = 0;
    private double currentLongtitude = 0;
    private double lon;
    private double lat;

    private final double METeq = 0.0175, weight = 100;
    private double MET = 0, calories = 0, km = 0;

    final private int feat = 10;
    private Top daneVeturilo = new Top();
    private Top2 daneMetro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isGooglePlayServicesAvailable()) {
            finish();
        }

        setContentView(R.layout.map_activity);

        initializeGoogleApiClient();

        createLocationRequest();

        initializeLayoutComponents();


        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        map = mapFragment.getMap();
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);


    }


    private void initializeLayoutComponents() {
        pauseButton = (Button) findViewById(R.id.pauseButton);
        startStopButton = (Button) findViewById(R.id.startStopButton);
        //timeView = (TextView) findViewById(R.id.time);
        /*caloriesView = (TextView) findViewById(R.id.calories);
        speedView = (TextView) findViewById(R.id.speed);
        distanceView = (TextView) findViewById(R.id.distance);*/
    }


    private void handleNewLocation(Location currentLocation) {
        Log.d(TAG, currentLocation.toString());
        currentLatitude = currentLocation.getLatitude();
        currentLongtitude = currentLocation.getLongitude();
        zoomLevel = map.getCameraPosition().zoom;

        if (cameraFlag) {
            animateCamera(15);
            cameraFlag = false;
        }

        if (trackingFlag) {
            animateCamera(zoomLevel);





            }




    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }


    private void animateCamera(float i) {
        LatLng latlng = new LatLng(currentLatitude, currentLongtitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlng, i);//10
        map.animateCamera(cameraUpdate);
    }




    public void findVeturilo(View view) {

        LoginScreen.ENDPOINT = "https://api.bihapi.pl/wfs/warszawa";
        final TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }
        };

        // Install the all-trusting trust manager
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        // Create an ssl socket factory with our all-trusting manager
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient();

        client.setSslSocketFactory(sslSocketFactory);



        client.interceptors().add(logging);

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(LoginScreen.ENDPOINT)
                .setClient(new OkClient(client))
                .setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("PAZERA"))
                .build();

        String username = "ute";
        String password = "ute!2016";
        String credentials = username + ":" + password;
        String string = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        NetworkAPI api = adapter.create(NetworkAPI.class);

        final Location myLocation = LocationServices.FusedLocationApi.getLastLocation((googleApiClient));
        lat = myLocation.getLatitude();
        lon = myLocation.getLongitude();

        map.clear();
        Coords coords = new Coords();
        coords.setLat(lat);
        coords.setLng(lon);

        //String query = lon + "," + lat + ",1000&maxFeatures=1";
        //String query = "veturilo?circle=21.01190776,52.21993162,1000&maxFeatures=1";
        String test = coords.toString();


        api.getVeturilo(string, coords, feat, new Callback<Top>() {
            @Override
            public void success(Top top, Response response) {
                Location target = new Location("target");
                int id = 0;
                daneVeturilo = top;
                int distance = 1500;
                Toast.makeText(MapActivity.this, "Veturilo Success", Toast.LENGTH_SHORT).show();
                for (int i = 0 ; i < daneVeturilo.data.size(); i++){
                    Location probe = new Location("probe");
                    probe.setLatitude(daneVeturilo.data.get(i).geometry.coordinates.getLat());
                    probe.setLongitude(daneVeturilo.data.get(i).geometry.coordinates.getLon());
                    if (Math.round(probe.distanceTo(myLocation)) < distance ){
                        target = probe;
                        id = i;
                        distance = Math.round(probe.distanceTo(myLocation));
                    }
                }

                map.addMarker(new MarkerOptions()
                    .position(new LatLng(target.getLatitude(), target.getLongitude()))
                    .title("Veturilo"));

                String tresc = "Numer stacji: " + daneVeturilo.data.get(id).properties.get(2).getValue() + ", Lokalizacja: "
                        + daneVeturilo.data.get(id).properties.get(1).getValue() + ", Liczba rowerow: "
                        + daneVeturilo.data.get(id).properties.get(3).getValue() + ", Odległosc: " + distance + " metrow.";
                sendSMS(tresc);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(MapActivity.this, "Veturilo Error", Toast.LENGTH_SHORT).show();

            }
        });




    }

    private void sendSMS(String tresc) {
        final TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }
        };

        // Install the all-trusting trust manager
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        // Create an ssl socket factory with our all-trusting manager
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient();

        client.setSslSocketFactory(sslSocketFactory);



        client.interceptors().add(logging);

        LoginScreen.ENDPOINT = "https://api.bihapi.pl/orange/oracle";

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(LoginScreen.ENDPOINT)
                .setClient(new OkClient(client))
                .setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("PAZERA"))
                .build();

        String username = "ute";
        String password = "ute!2016";
        String credentials = username + ":" + password;
        String string = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        NetworkAPI api = adapter.create(NetworkAPI.class);

        api.sendSMS(string, "48504216720", "UTE", tresc, new Callback<SmsResult>() {
            @Override
            public void success(SmsResult smsResult, Response response) {
                Toast.makeText(MapActivity.this, "SMS sent", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(MapActivity.this, "SMS error", Toast.LENGTH_SHORT).show();
            }
        });


    };




    public void findMetro(View view) {
        LoginScreen.ENDPOINT = "https://api.bihapi.pl/wfs/warszawa";
        final TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }
        };

        // Install the all-trusting trust manager
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        // Create an ssl socket factory with our all-trusting manager
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient();

        client.setSslSocketFactory(sslSocketFactory);



        client.interceptors().add(logging);

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(LoginScreen.ENDPOINT)
                .setClient(new OkClient(client))
                .setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("PAZERA"))
                .build();

        String username = "ute";
        String password = "ute!2016";
        String credentials = username + ":" + password;
        String string = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        NetworkAPI api = adapter.create(NetworkAPI.class);

        final Location myLocation = LocationServices.FusedLocationApi.getLastLocation((googleApiClient));
        lat = myLocation.getLatitude();
        lon = myLocation.getLongitude();

        map.clear();
        Coords coords = new Coords();
        coords.setLat(lat);
        coords.setLng(lon);

        //String query = lon + "," + lat + ",1000&maxFeatures=1";
        //String query = "veturilo?circle=21.01190776,52.21993162,1000&maxFeatures=1";
        String test = coords.toString();


        api.getMetro(string, coords, feat, new Callback<Top2>() {
            @Override
            public void success(Top2 top2, Response response) {
                Location target = new Location("target");
                int id = 0;
                daneMetro = top2;
                int distance = 1500;
                Toast.makeText(MapActivity.this, "Metro Success", Toast.LENGTH_SHORT).show();
                for (int i = 0 ; i < daneMetro.data.size(); i++){
                    Location probe = new Location("probe");
                    probe.setLatitude(daneMetro.data.get(i).geometry.coordinates.getLat());
                    probe.setLongitude(daneMetro.data.get(i).geometry.coordinates.getLon());
                    if (Math.round(probe.distanceTo(myLocation)) < distance ){
                        target = probe;
                        id = i;
                        distance = Math.round(probe.distanceTo(myLocation));
                    }
                }

                map.addMarker(new MarkerOptions()
                        .position(new LatLng(target.getLatitude(), target.getLongitude()))
                        .title("Metro Entrance"));

                String tresc = "Najblizsze wejscie do metra jest oddalone o: " + distance + " metrow";
                sendSMS(tresc);

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(MapActivity.this, "Metro Error", Toast.LENGTH_SHORT).show();

            }
        });
    }




    @Override
    protected void onResume() {
        super.onResume();
        googleApiClient.connect();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected");
        currentLocation = LocationServices.FusedLocationApi.getLastLocation((googleApiClient));
        if (currentLocation == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

        }

    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, getString(R.string.locationServicesSuspended));

    }


    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void initializeGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void gotoMyTrails(View view) {
        MyTrailsFragment myTrailsFragment = new MyTrailsFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.map_activity_layout, myTrailsFragment, "mytrailsfragment")
                .addToBackStack("")
                .commit();

    }




}


package com.example.japjot.weatherapp;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final String secretkey = "ef6219ef15b4c1d436be9129da679946";
    public static final String webapi = "https://api.darksky.net/forecast/";
    double longitudeNetwork, latitudeNetwork;
    JSONObject jsonObject;
    private LocationListener locationListener;

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // why isn't this working
        final LocationListener locationListenerNetwork = new LocationListener() {

        public void onLocationChanged(Location location) {
            longitudeNetwork = location.getLongitude();
            latitudeNetwork = location.getLatitude();

            runOnUiThread(new Runnable() {

                //get my stuff
                @Override
                public void run() {
                      class RetrieveDataTask extends AsyncTask<Object, Void, JSONObject> {
                        String latitude, longitude;
                        View view;

                        public RetrieveDataTask(String latitude, String longitude) {
                            this.latitude = latitude;
                            this.longitude = longitude;
                        }

                        protected JSONObject doInBackground(Object... objects) {

                            String urlString = webapi + secretkey + "/" + latitude + "," + longitude;

                            try {

                                // url hubbies
                                URL url = new URL(urlString);
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                conn.setRequestMethod("GET");
                                InputStream in = new BufferedInputStream(conn.getInputStream());
                                String response = convertStreamToString(in);
                                JSONObject json = new JSONObject(response);
                                return json;

                            } catch (Exception e) {
                                Log.e("URL_ERROR", urlString);
                                return null;
                            }

                        }

                        // info stuff
                        @Override
                        protected void onPostExecute(JSONObject jsonObject) {
                            super.onPostExecute(jsonObject);
                            try {

                                String temperatureMin = ((JSONObject) (jsonObject.getJSONObject("daily").getJSONArray("data").get(0))).getString("temperatureMin");
                                ((TextView) view.findViewById(R.id.mintemp)).setText(temperatureMin);

                                String temperatureMax = ((JSONObject) (jsonObject.getJSONObject("daily").getJSONArray("data").get(0))).getString("temperatureMax");
                                ((TextView) view.findViewById(R.id.maxtemp)).setText(temperatureMax);

                                String summary = ((JSONObject) (jsonObject.getJSONObject("daily").getJSONArray("data").get(0))).getString("summary");
                                ((TextView) view.findViewById(R.id.description)).setText(summary);

                                String precipProb = ((JSONObject) (jsonObject.getJSONObject("daily").getJSONArray("data").get(0))).getString("precipProbability");
                                ((TextView) view.findViewById(R.id.precipprob)).setText(precipProb);

                                String precipType = ((JSONObject) (jsonObject.getJSONObject("daily").getJSONArray("data").get(0))).getString("precipType");
                                ((TextView) view.findViewById(R.id.preciptype)).setText(precipType);

                                Date date = new Date(((JSONObject) (jsonObject.getJSONObject("daily").getJSONArray("data").get(0))).getString("precipIntensityMaxTime"));
                                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

                                String s = formatter.format(date);

                                ((TextView) view.findViewById(R.id.preciptime)).setText(s);
                                String icon = ((JSONObject) (jsonObject.getJSONObject("daily").getJSONArray("data").get(0))).getString("icon");
                                ImageView weatherIcon = view.findViewById(R.id.weathericon);

                                switch (icon) {
                                    case "rain":
                                        weatherIcon.setImageResource(R.drawable.ic_cloud_rain);
                                        break;
                                    case "snow":
                                        weatherIcon.setImageResource(R.drawable.ic_cloud_snow_alt);
                                        break;
                                    case "sleet":
                                        weatherIcon.setImageResource(R.drawable.ic_cloud_hail);
                                        break;
                                    case "wind":
                                        weatherIcon.setImageResource(R.drawable.ic_wind);
                                        break;
                                    case "fog":
                                        weatherIcon.setImageResource(R.drawable.ic_cloud_fog);
                                        break;
                                    case "cloudy":
                                        weatherIcon.setImageResource(R.drawable.ic_cloud);
                                        break;
                                    case "partly-cloudy-day":
                                        weatherIcon.setImageResource(R.drawable.ic_cloud_sun);
                                        break;
                                    case "partly-cloudy-night":
                                        weatherIcon.setImageResource(R.drawable.ic_cloud_moon);
                                        break;
                                    case "clear-day":
                                        weatherIcon.setImageResource(R.drawable.ic_sun);
                                        break;
                                    case "clear-night":
                                        weatherIcon.setImageResource(R.drawable.ic_moon);
                                        break;
                                }

                            } catch (JSONException f) {
                                f.printStackTrace();
                            }


                        }
                    }
                }
            });


        }

            //implemented stuffs
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        // OH why this Null Pointer
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(new com.example.japjot.weatherapp.ListAdapter(this, jsonObject));

        LocationManager lm = (LocationManager)getSystemService(getParent().LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                new RetrieveDataTask.RetrieveDataTask().execute(View.inflate(getApplicationContext(), R.layout.activity_main, null), location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }
}


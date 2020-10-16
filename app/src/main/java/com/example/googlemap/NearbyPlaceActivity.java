package com.example.googlemap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class NearbyPlaceActivity extends AppCompatActivity {
    GoogleMap map;
    private Button btnFind;
    private Spinner sprType;
    double currentLat = 0,  currentLon = 0;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_place);

        btnFind = findViewById(R.id.btn_find);
        sprType = findViewById(R.id.sp_type);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);

        final String[] placeTypeList = {"atm", "bank", "hospital","movie_theater", "restaurant"};

        String[] placeNameList = {"ATM", "Bank", "Hospital", "Movie Theater", "Restaurant"};

        sprType.setAdapter(new ArrayAdapter<>(NearbyPlaceActivity.this
        , android.R.layout.simple_spinner_dropdown_item, placeNameList));

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if(ActivityCompat.checkSelfPermission(NearbyPlaceActivity.this
        , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getCurrentLcation();
        }else {
            ActivityCompat.requestPermissions(NearbyPlaceActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = sprType.getSelectedItemPosition();
                String url =  "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +"?location=" + currentLat + "," + currentLon + "&radius=5000" + "&type=" + placeTypeList[i] + "&sensor=true" + "&key=" + getResources().getString(R.string.map_key);
                Log.d("CLAPCLAP" , " 11111 " + url);
                new PaceTask().execute(url);
            }
        });
    }

    private void getCurrentLcation() {
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {

                if(location != null){
                    currentLat = location.getLatitude();
                    currentLon  = location.getLongitude();

                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            map = googleMap;
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(currentLat, currentLon),10
                            ));
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 44){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLcation();
            }
        }
    }

    private class PaceTask extends AsyncTask<String , Integer, String > {

        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            Log.d("CLAPCLAP" , " 22222222222 ");

            try {
                data = downloadUrl(strings[0]);
                Log.d("CLAPCLAP" , " 3333333 " + data);

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("CLAPCLAP" , " 4444444 " + e);

            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            new ParserTask().execute(s);
        }
    }

    private String downloadUrl(String string) throws IOException{
        URL url = new URL(string);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();

        InputStream stream = connection.getInputStream();
        BufferedReader reader =  new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        String line = "";
        while((line = reader.readLine()) != null){
            builder.append(line);
        }

        String data = builder.toString();
        reader.close();
        return  data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            JsonParser jsonParser = new JsonParser();
            List<HashMap<String , String >> mapList = null;
            JSONObject object = null;
            try {
                object = new JSONObject(strings[0]);
                mapList = jsonParser.parseResult(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            map.clear();
            for (int i = 0; i<hashMaps.size(); i++){
                HashMap<String, String> hashMap = hashMaps.get(i);
                double lat = Double.parseDouble(hashMap.get("lat"));
                double lng = Double.parseDouble(hashMap.get("lng"));

                String name = hashMap.get("name");
                LatLng latLng = new LatLng(lat, lng);

                MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                        .title(name);

                map.addMarker(markerOptions);
            }
        }
    }
}

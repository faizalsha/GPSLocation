package com.google.firebase.udacity.gpslocation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    TextView textView;
    ProgressBar progressBar;
    Button button;
    Geocoder geocoder;
    List<Address> addressList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textview);
        button = findViewById(R.id.button);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                textView.setText(showAddress(location.getLatitude(), location.getLongitude()));
                progressBar.setVisibility(View.GONE);
                locationManager.removeUpdates(locationListener);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(MainActivity.this, "GPS is Off", Toast.LENGTH_SHORT).show();
                Intent settingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(settingIntent);
            }
        };

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locate();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 10)
            if (!(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
            }
    }

    void locate(){
        //Check for GPS permission before attaching locationListener to locationManager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},10);
            }
            //return to app activity from request_permission activity
            return;
        }
        //attaching location listener to locationManager
        locationManager.requestLocationUpdates("gps", 1, 20, locationListener);
        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Getting your location", Toast.LENGTH_SHORT).show();
    }

    String showAddress(Double latitude, Double longitude){
        geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        try {
            addressList = geocoder.getFromLocation(latitude,longitude,1);
            String address = addressList.get(0).getAddressLine(0);
            String area = addressList.get(0).getLocality();
            String city = addressList.get(0).getAdminArea();
            String country = addressList.get(0).getCountryName();
            String postalCode = addressList.get(0).getPostalCode();
            return (address + ", " + area + ", " + city + ", " + country + ", " + postalCode);
        } catch (IOException e) {
            e.printStackTrace();
            return "Some Error Occured";
        }
    }

}

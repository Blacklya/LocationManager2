package com.example.locationmanager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    MapView map;
    Marker marcadorPosicaoAtual;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = findViewById(R.id.textview);
        map = findViewById(R.id.map);
        mapinit();
        Configuration.getInstance().setUserAgentValue(getPackageName());
        checkLocationPermission();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0, 0, location -> {
                        tv.setText("Latitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude() + "\nAltitude: " + location.getAltitude() + "\nVelocidade: " +location.getSpeed());
                        map.setMultiTouchControls(true);
                        map.getController().setZoom(15.0);
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        GeoPoint userLocation = new GeoPoint(latitude,longitude);
                        map.getController().setCenter(userLocation);
                        map.getController().setZoom(18.0);
                        map.getController().animateTo(userLocation);
                        Marker marker = new Marker(map);
                        marker.setPosition(userLocation);
                        showLocationOnMap(location);
                    }
            );

            Location localizacao = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (localizacao != null) {
                double latitude = localizacao.getLatitude();
                double longitude = localizacao.getLongitude();
                tv.setText("Latitude: " + latitude + "\nLongitude: " + longitude);
            } else {
                tv.setText("Localização não disponível");
            }
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }


    public void mapinit (){
        map.setMultiTouchControls(true);
        map.getController().setZoom(16.0);
    }

    public void showLocationOnMap (Location location){
        double latitude=location.getLatitude();
        double longitude=location.getLongitude();
        GeoPoint mLocation = new GeoPoint (latitude, longitude);
        map.getController().setCenter(mLocation);


        if (marcadorPosicaoAtual == null){
            marcadorPosicaoAtual = new Marker (map);
        }


        marcadorPosicaoAtual.setPosition(mLocation);
        marcadorPosicaoAtual.setTitle("Minha Localização");

        map.getOverlays().add(marcadorPosicaoAtual);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recreate();
            } else {
                TextView tv = findViewById(R.id.textview);
                tv.setText("Permissão de localização negada");
            }
        }
    }
}
package com.example.abidat.trafficreport;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 99;
    private GoogleMap mMap;
    private static final String TAG = "MapsActivity";
    private boolean locationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toast.makeText(this, "Welcome to Traffic Report!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: Application stopped!");
        MapStateManager mapStateManager = new MapStateManager(this);
        mapStateManager.saveMapState(mMap);
        Log.i(TAG, "onStop: Application saved state!");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        MapStateManager mapStateManager = new MapStateManager(this);
        CameraPosition position = mapStateManager.getSavedCameraPosition();

        int savedMapType = mapStateManager.getMapType();
        setMapType(savedMapType);
        Log.i(TAG, "onMapReady: MapType set!");

        setStartingLocation(position);
        getDeviceLocation();
    }

    /**
     *
     * @param savedMapType is the MapType that the user used in his previous session
     * Method sets the default MapType if there was no previous session
     */
    public void setMapType(int savedMapType) {
        if (savedMapType == 0) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else {
            if (savedMapType == GoogleMap.MAP_TYPE_NORMAL) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            } else {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        }
        Log.d(TAG, "setMapType: MapType is: " + savedMapType);
    }

    public void setStartingLocation(CameraPosition cameraPosition){
        if (cameraPosition != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            if (mMap == null) {
                return;
            } else {
                mMap.moveCamera(cameraUpdate);
            }
        } else {
            LatLng prishtina = new LatLng(42.662654, 21.163325);
            mMap.addMarker(new MarkerOptions().position(prishtina).title("Qendra e Prishtines"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(prishtina, 15));
        }
        Log.i(TAG, "onMapReady: Start location set!");
    }

    /**
     *
     * @param v is the Id of the button being clicked in the MapsActivity
     * Methods handles all the button clicking available
     */
    public void buttonClicked(View v) {
        switch (v.getId()) {
            //TODO: Change name to switch between NORMAL and SATELITE TYPE
            case R.id.btnNormalMap:
                if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.btnMarker:
                LatLng fakultetiPozita = new LatLng(42.648676, 21.167128);
                Marker marker = mMap.addMarker(new MarkerOptions().position(fakultetiPozita));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(fakultetiPozita));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(fakultetiPozita));

                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(fakultetiPozita);
                circleOptions.radius(500);
                mMap.addCircle(circleOptions);
                break;
            case R.id.btnSearch:
                getDeviceLocation();
                break;

            default:
                Log.i(TAG, "buttonClicked: Returned!");
                return;
        }
    }

    //TODO: Main button to do the reporting!
    public void raportoClick(View v) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //mMap.setMyLocationEnabled(true);
    }

    //TODO: The dialog to request permissions shows up, but you need to configure the results
    private void getDeviceLocation() {
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        if (locationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            locationPermissionGranted = false;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
    }
}
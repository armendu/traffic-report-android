package com.example.trafficmenu;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * Saves the state of the Map after closing and calls them after re-opening
 */
public class MapStateManager {
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String ZOOM = "zoom";
    private static final String BEARING = "bearing";
    private static final String TILT = "tilt";
    private static final String MAPTYPE = "maptype";

    private static final String PREFS_NAME = "mapCameraState";
    private SharedPreferences mapStatePrefs;


    public MapStateManager(Context context){
        mapStatePrefs = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
    }

    public void saveMapState(GoogleMap map){
        if(map!=null) {
            SharedPreferences.Editor sharedPreferencesEditor = mapStatePrefs.edit();
            CameraPosition cameraPosition = map.getCameraPosition();

            sharedPreferencesEditor.putFloat(LATITUDE, (float) cameraPosition.target.latitude);
            sharedPreferencesEditor.putFloat(LONGITUDE, (float) cameraPosition.target.longitude);
            sharedPreferencesEditor.putFloat(ZOOM, cameraPosition.zoom);
            sharedPreferencesEditor.putFloat(TILT, cameraPosition.tilt);
            sharedPreferencesEditor.putFloat(BEARING, cameraPosition.bearing);
            sharedPreferencesEditor.putInt(MAPTYPE, map.getMapType());

            sharedPreferencesEditor.commit();
        }
        else {
            SharedPreferences.Editor sharedPreferencesEditor = mapStatePrefs.edit();
            sharedPreferencesEditor.putInt(MAPTYPE, GoogleMap.MAP_TYPE_SATELLITE);
        }
    }

    public CameraPosition getSavedCameraPosition(){

        double latitude = mapStatePrefs.getFloat(LATITUDE,(float)42.6484899);
        double longitude = mapStatePrefs.getFloat(LONGITUDE, (float)21.1670806);
        LatLng target = new LatLng(latitude,longitude);

        float zoom = mapStatePrefs.getFloat(ZOOM, 15);
        float bearing = mapStatePrefs.getFloat(BEARING, 0);
        float tilt = mapStatePrefs.getFloat(TILT, 0);

        CameraPosition cameraPosition = new CameraPosition(target, zoom, tilt, bearing);
        return cameraPosition;
    }

    public int getMapType(){
        int mapType = mapStatePrefs.getInt(MAPTYPE, 0);
        if(mapType == 0){
            return GoogleMap.MAP_TYPE_NORMAL;
        }
        return mapType;
    }
}
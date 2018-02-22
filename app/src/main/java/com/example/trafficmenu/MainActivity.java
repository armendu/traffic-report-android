package com.example.trafficmenu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.trafficmenu.R.string.aboutus;
import static com.example.trafficmenu.R.string.allhistory;
import static com.example.trafficmenu.R.string.app_name;
import static com.example.trafficmenu.R.string.delete;
import static com.example.trafficmenu.R.string.history;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {

    private Marker marker; //Markers that show on the map
    private ArrayList<LatLng> MarkerPoints; //List of LatLng, actual points of the Markers
    SupportMapFragment supportMapFragment;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private boolean reportMode;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check for network availability
        if(!NetworkAvailability.isNetworkAvailable(this)){
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Please make sure you have and Internet connection!");
            alertDialog.setIcon(R.drawable.warning);
            alertDialog.setCancelable(false);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            System.exit(0); //Exit the application
                        }
                    });
            alertDialog.show();
        }
        setContentView(R.layout.activity_main);

        MarkerPoints = new ArrayList<>();
        supportMapFragment = SupportMapFragment.newInstance();

        // Set the Identifier.androidId which is the advertising Id of the android device
        GetAdertisingId getAdertisingId = new GetAdertisingId(this);
        getAdertisingId.execute();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        Main action button initialization and its onClick method
         */
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(reportMode){
                    if(MarkerPoints.size() == 2){
                        LatLng origin = MarkerPoints.get(0);
                        LatLng dest = MarkerPoints.get(1);
                        String method = "report";
                        DatabaseBackgroundTasks databaseBackgroundTasks = new DatabaseBackgroundTasks(MainActivity.this);
                        databaseBackgroundTasks.execute(method,Identifiers.android_id,"1",String.valueOf(origin.latitude),
                                String.valueOf(origin.longitude),String.valueOf(dest.latitude),
                                String.valueOf(dest.longitude));

                        Log.i(TAG, "onClick: androidId is:" + Identifiers.android_id);
                        marker.remove();

                        mMap.getUiSettings().setAllGesturesEnabled(true);
                        mMap.clear();
                        reportMode = false;
                        getSupportActionBar().show();
                    }
                    else {
                        Toast.makeText(MainActivity.this,"Please select another location!",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(MainActivity.this,"Please enter report mode!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Replaces the frame_content with a new MapFragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_content, new MapFragment()).commit();

        //Set supportMapFragment
        supportMapFragment.getMapAsync(this);
        //Get the SupportFragmentManager
        android.support.v4.app.FragmentManager fragmentManager1 = getSupportFragmentManager();

        //With the support fragmentManager1, set the resource R.id.map to the supportMapFragment whose value was a map
        fragmentManager1.beginTransaction().add(R.id.map,supportMapFragment).commit();
        //Show that supportMapFragment, commit to do those changes
        fragmentManager1.beginTransaction().show(supportMapFragment).commit();

        // If android is Android Marshmallow or above
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        /*
         * If the intent is called from another class get all the values and execute FetchUrl()
         */
        Intent i = getIntent();
        // Receiving the Data
        if(i.hasExtra("originlat")){
            String originlat = i.getStringExtra("originlat");
            String originlng = i.getStringExtra("originlng");
            String destinationlat = i.getStringExtra("destinationlat");
            String destinationlng = i.getStringExtra("destinationlng");

            LatLng origin = new LatLng(Double.parseDouble(originlat),Double.parseDouble(originlng));
            LatLng dest = new LatLng(Double.parseDouble(destinationlat),Double.parseDouble(destinationlng));
            // Getting URL to the Google Directions API
            UrlGetSet urlGetSet = new UrlGetSet();
            String url = urlGetSet.getUrl(origin, dest);
            Log.d("onMapClick", url.toString());

            FetchUrl FetchUrl = new FetchUrl();
            // Start downloading json data from Google Directions API
            FetchUrl.execute(url);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent i = getIntent();
        // If the Intent is called from another class:
        if(i.hasExtra("originlat")){
            String originlat = i.getStringExtra("originlat");
            String originlng = i.getStringExtra("originlng");
            LatLng origin = new LatLng(Double.parseDouble(originlat),Double.parseDouble(originlng));

            CameraPosition cameraPosition = new CameraPosition(origin,16,0,0);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            mMap.moveCamera(cameraUpdate);
            Log.i(TAG, "onMapReady: Camera changed!" + cameraPosition);
        }
        else {
            MapStateManager mapStateManager = new MapStateManager(this);
            mMap.setMapType(mapStateManager.getMapType());
            CameraPosition cameraPosition = mapStateManager.getSavedCameraPosition();
            if(cameraPosition!=null) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                mMap.moveCamera(cameraUpdate);
                Log.i(TAG, "onMapReady: Camera changed!" + cameraPosition);
            }
            else {
                Log.i(TAG, "onMapReady: No previous session!");
            }
        }
        
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            Log.i(TAG, "onMapReady: Location set!");
        }

        // OnMyLocationButton will enable users to switch modes
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if(reportMode){
                    getSupportActionBar().show();
                    reportMode = false;
                    mMap.getUiSettings().setAllGesturesEnabled(true);
                    mMap.resetMinMaxZoomPreference();
                    mMap.clear();
                    Toast.makeText(MainActivity.this,"You are now in Viewer mode. Press the Location Button to enter Report Mode!",Toast.LENGTH_SHORT).show();
                }
                else {
                    mMap.getUiSettings().setScrollGesturesEnabled(false);
                    mMap.getUiSettings().setTiltGesturesEnabled(false);
                    mMap.setMinZoomPreference(16);
                    reportMode = true;
                    getSupportActionBar().hide();
                    MarkerPoints.clear();
                    Toast.makeText(MainActivity.this,"You are now in Report mode. Press the Location Button to enter Viewer Mode!",Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        // Setting onclick event listener for the map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                // Is user in report mode
                if(reportMode){
                    // Already two locations
                    if (MarkerPoints.size() > 1) {

                        MarkerPoints.clear();
                        mMap.clear();
                    }
                    // Adding new item to the ArrayList
                    MarkerPoints.add(point);

                    // Creating MarkerOptions
                    MarkerOptions options = new MarkerOptions();

                    // Setting the position of the marker
                    options.position(point);

                    /**
                     * For the start location, the color of marker is GREEN
                     * and for the end location the color of marker is RED.
                     */
                    if (MarkerPoints.size() == 1) {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    } else if (MarkerPoints.size() == 2) {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        marker.remove();
                    }
                    // Add new marker to the Google Map Android API V2
                    marker = mMap.addMarker(options);
                    //marker.remove();

                    // Checks whether start and end locations are captured
                    if (MarkerPoints.size() >= 2) {

                        LatLng origin = MarkerPoints.get(0);
                        LatLng dest = MarkerPoints.get(1);
                        // Getting URL to the Google Directions API
                        UrlGetSet urlGetSet = new UrlGetSet();
                        String url = urlGetSet.getUrl(origin, dest);
                        Log.d("onMapClick", url.toString());

                        FetchUrl FetchUrl = new FetchUrl();
                        // Start downloading json data from Google Directions API
                        FetchUrl.execute(url);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(getTitle().equals(getString(app_name))){
            menu.clear();
            getMenuInflater().inflate(R.menu.main, menu);
        }

        if(getTitle().equals(getString(history))){
            menu.clear();
            getMenuInflater().inflate(R.menu.reportmenu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Change the map type
        if (id == R.id.settings_normal) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        else if(id == R.id.settings_terrain){
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }
        else if(id==R.id.settings_satelite){
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //Main Menu
        if (id == R.id.nav_home) {
            fab.show();

            //Get the fragment manager, can be done even without these two rows
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_content, new MapFragment()).commit();

            android.support.v4.app.FragmentManager fragmentManager1 = getSupportFragmentManager();
            fragmentManager1.beginTransaction().show(supportMapFragment).commit();
            setTitle(getString(app_name));
            mMap.clear();
        }
        else if (id == R.id.nav_personal_history) {
            //Personal history fragment called
            fab.hide();
            ReportList fr = new ReportList();

            //Get the fragment manager
            FragmentManager fm = getFragmentManager();

            //Start the transaction
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            //Get the supportFragmentManager
            android.support.v4.app.FragmentManager fragmentManager1 = getSupportFragmentManager();

            //Hide the supportMapFragment and then show the new fragment
            fragmentManager1.beginTransaction().hide(supportMapFragment).commit();

            fragmentTransaction.show(fr);
            fragmentTransaction.replace(R.id.frame_content, fr);
            fragmentTransaction.commit();
            setTitle(getString(history));
        }
        else if (id == R.id.nav_history) {
            //All reports history fragment called
            fab.hide();
            AllReportsList fr = new AllReportsList();

            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            android.support.v4.app.FragmentManager fragmentManager1 = getSupportFragmentManager();

            fragmentManager1.beginTransaction().hide(supportMapFragment).commit();

            fragmentTransaction.show(fr);
            fragmentTransaction.replace(R.id.frame_content, fr);
            fragmentTransaction.commit();
            setTitle(getString(allhistory));
        }
        else if (id == R.id.nav_manage) {
            //Delete report fragment called
            fab.hide();
            DeleteReport fr = new DeleteReport();

            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            android.support.v4.app.FragmentManager fragmentManager1 = getSupportFragmentManager();

            fragmentManager1.beginTransaction().hide(supportMapFragment).commit();

            fragmentTransaction.show(fr);
            fragmentTransaction.replace(R.id.frame_content, fr);
            fragmentTransaction.commit();
            setTitle(getString(delete));
        }
        else if (id == R.id.nav_share) {
            //TODO: ADD FEATURE
        }
        else if (id == R.id.nav_aboutus) {
            fab.hide();
            AboutUsFragment fr = new AboutUsFragment();

            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            android.support.v4.app.FragmentManager fragmentManager1 = getSupportFragmentManager();

            fragmentManager1.beginTransaction().hide(supportMapFragment).commit();

            fragmentTransaction.show(fr);
            fragmentTransaction.replace(R.id.frame_content, fr);
            fragmentTransaction.commit();
            setTitle(getString(aboutus));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * After back button is pressed (Applies for the menu)
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // Request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Save the state of the map after exiting
        MapStateManager mapStateManager = new MapStateManager(this);
        mapStateManager.saveMapState(mMap);
        Log.i(TAG, "onStop: Application saved state!");
    }

    /*
    After connecting, set the location requests interval and priority
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Make request to get the users location
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        MapStateManager mapStateManager = new MapStateManager(this);
        mMap.setMapType(mapStateManager.getMapType());
        Log.i(TAG, "onConnected: Current location and saved maptype set!");
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    // Permission denied, show alert
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Please allow the location permission!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    checkLocationPermission();
                                }
                            });
                    alertDialog.show();
                }
                return;
            }
        }
    }

    /**
     * Implements onLocationChanged method from the Google interface 'LocationListener'
     * @param location is the location of the user
     */
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        // Find current location
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//
//        MapStateManager mapStateManager = new MapStateManager(this);
//        CameraPosition position = mapStateManager.getSavedCameraPosition();
//        if(position!=null) {
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(position);
//            mMap.moveCamera(cameraUpdate);
//            Log.i(TAG, "onLocationChanged: Application loaded a previous session!");
//        }
//        else {
//            CameraPosition position2 = new CameraPosition(latLng,15,0,0);
//            CameraUpdate cameraUpdate2 = CameraUpdateFactory.newCameraPosition(position2);
//            mMap.moveCamera(cameraUpdate2);
//            Log.i(TAG, "onLocationChanged: Application started a new session!");
//        }
        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    /**
     * Builds a new GoogleApiClient
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        Log.d(TAG, "buildGoogleApiClient: " + mGoogleApiClient);
    }

    /**
     * Fetches data from the url that was passed to it
     */
    private class FetchUrl extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Finding the shortest route!");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                UrlGetSet objUrlGetSet = new UrlGetSet();
                data = objUrlGetSet.downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
            progressDialog.cancel();
        }
    }

    /**
     * New thread to get the shortest route from origin to destination
     */
    public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>>
                    routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(12);

                lineOptions.color(Color.RED);

                Log.d("onPostExecute","onPostExecute lineoptions decoded: " + lineOptions.toString());
            }
            // UrlGetSet polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
                if(marker!=null){
                    marker.remove();
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "Lines not drawn!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
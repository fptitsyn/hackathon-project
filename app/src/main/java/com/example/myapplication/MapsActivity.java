package com.example.myapplication;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.example.myapplication.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.Maps;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends AppCompatActivity
        implements
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        RoutingListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    public GoogleMap map;
    View mapView;
    private ActivityMapsBinding binding;

    private List<Polyline> polylines = null;
    private final LatLng start = new LatLng(55.7458409734953, 37.64766302246173);

    private List<LatLng> places = new ArrayList<>();

    SearchView searchView;

    private ClusterManager<ClusterMarker> clusterManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        places.add(start);
        Intent intent = getIntent();
        LatLng address = intent.getParcelableExtra("ADDRESS");
        places.add(address);

        Intent intent2 = getIntent();
        if (intent2.getIntExtra("ROUTE", 0) == 1) {
            places.add(new LatLng(55.753884949680305, 37.691154336772904));
            places.add(new LatLng(55.75208968204676, 37.58545511591237));
        }
// 55.75208968204676, 37.58545511591237
//        places.add(new LatLng(55.753884949680305, 37.691154336772904));
//        places.add(new LatLng(55.764817, 37.591245));
//        places.add(new LatLng(55.664817, 37.491245));

//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//                .permitAll().build();
//        StrictMode.setThreadPolicy(policy);

        searchView = findViewById(R.id.idSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || location.equals("")) {
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        // getting an address from name
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // get the location
                    try {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                        // marker and camera movement
                        ClusterMarker clusterMarker = new ClusterMarker(latLng.latitude,
                                latLng.longitude, "", "");
                        clusterManager.addItem(clusterMarker);
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                        findRoutes(latLng);
                    }
                    catch (IndexOutOfBoundsException e) {
                        Toast.makeText(MapsActivity.this,"Address not found",Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        // at last we calling our map fragment to update.
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        UiSettings settings = map.getUiSettings();
        setStartPosition();
        enableMyLocation();
        settings.setMyLocationButtonEnabled(true);
        settings.setCompassEnabled(true);
        settings.setZoomControlsEnabled(true);
        map.setPadding(0, 180, 0, 0);

        setUpClusterer();

        Manager manager1 = new Manager(map, 0);
        startManager(manager1);
    }

    private void startManager(Manager manager) {
        manager.findRoutes(places);
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                map.setMyLocationEnabled(true);
            }
        }
        else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
    }

    public void setStartPosition() {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(start)
                .zoom(15)
                .tilt(20)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.animateCamera(cameraUpdate);
    }

    private void setUpClusterer() {
        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = new ClusterManager<>(this, map);
        DefaultClusterRenderer<ClusterMarker> clusterRenderer =
                new DefaultClusterRenderer<>(this, map, clusterManager);
        clusterRenderer.setMinClusterSize(3);
        clusterManager.setRenderer(clusterRenderer);
        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {
        for (int i = 0; i < places.size(); i++) {
            ClusterMarker clusterMarker = new ClusterMarker(places.get(i).latitude,
                    places.get(i).longitude, "", "");
            clusterManager.addItem(clusterMarker);
        }
    }

    public void findRoutes(List<LatLng> places) {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .optimize(true)
                .waypoints(places)
                .key("AIzaSyAhYnFeAWHRIzhEYqWAWHUSMkVUVMv0mDM")
                .build();
        routing.execute();
    }

    public void findRoutes(LatLng point) {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .optimize(false)
                .waypoints(start, point)
                .key("AIzaSyAhYnFeAWHRIzhEYqWAWHUSMkVUVMv0mDM")
                .build();
        routing.execute();
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, "Couldn't find a route", Snackbar.LENGTH_LONG);
        snackbar.show();
//        findRoutes(places);
    }

    @Override
    public void onRoutingStart() {
//        Toast.makeText(MapsActivity.this,"Finding Route...", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        CameraUpdateFactory.newLatLng(start);
        CameraUpdateFactory.zoomTo(16);
        if (polylines != null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i < route.size(); i++) {
            if (i == shortestRouteIndex) {
                polyOptions.color(getResources().getColor(R.color.colorPrimary, null));
                polyOptions.width(8);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = map.addPolyline(polyOptions);
                int k = polyline.getPoints().size();
                polylines.add(polyline);
            }
        }
    }

    @Override
    public void onRoutingCancelled() {
//        findRoutes(places);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public int getWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        return width;
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
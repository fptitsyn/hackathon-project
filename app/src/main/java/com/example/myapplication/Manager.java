package com.example.myapplication;

import android.graphics.Color;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class Manager extends AppCompatActivity
        implements RoutingListener {

    private List<Polyline> polylines = null;
    private final LatLng start = new LatLng(55.745832120381706, 37.64766317887026);
    private GoogleMap map;
    public int managerCount;

    public Manager(GoogleMap map, int managerCount) {
        this.map = map;
        this.managerCount = managerCount;
        this.managerCount++;
    }

//    public List<Integer> sortDurations(List<Integer> durations) {
//        Collections.sort(durations);
//        return durations;
//    }

//    public List<Integer> sortDurations(List<Integer> durations) {
//        Collections.sort(durations);
//        int groups = durations.size() / managerCount;
//        List<Integer> ownRoute = new ArrayList<>();
//        for (int i = 0; i < groups; i++) {
//            ownRoute.add(durations.get(i));
////            durations.remove(i);
//        }
//        return ownRoute;
//    }

    public void findRoutes(List<LatLng> places) {
//        List<LatLng> ownPlaces = new ArrayList<>();
//        int ownPlacesSize = places.size() / managerCount;
//        for (int i = 0; i < ownPlacesSize; i++) {
//            ownPlaces.add(places.get(i));
//            places.remove(i);
//        }
        if (places.size() > 2) {
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
        else {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .optimize(false)
                    .waypoints(places.get(0), places.get(1))
                    .key("AIzaSyAhYnFeAWHRIzhEYqWAWHUSMkVUVMv0mDM")
                    .build();
            routing.execute();
        }
    }

    public void findRoutes(LatLng origin, LatLng destination) {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.WALKING)
                .withListener(this)
                .alternativeRoutes(true)
                .optimize(false)
                .waypoints(origin, destination)
                .key("AIzaSyAhYnFeAWHRIzhEYqWAWHUSMkVUVMv0mDM")
                .build();
        routing.execute();
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, "Couldn't find a route", Snackbar.LENGTH_LONG);
        snackbar.show();
//        managerFindRoutes(places);
    }

    @Override
    public void onRoutingStart() {
//        Toast.makeText(MapsActivity.this,"Finding Route...", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        if(polylines!=null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng = null;
        LatLng polylineEndLatLng = null;

        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i < route.size(); i++) {
            if (i == shortestRouteIndex) {
                polyOptions.color(Color.RED);
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = map.addPolyline(polyOptions);
                polylineStartLatLng = polyline.getPoints().get(0);
                int k = polyline.getPoints().size();
                polylineEndLatLng = polyline.getPoints().get(k - 1);
                polylines.add(polyline);
            }
        }
    }

    @Override
    public void onRoutingCancelled() {
//        managerFindRoutes(places);
    }
}

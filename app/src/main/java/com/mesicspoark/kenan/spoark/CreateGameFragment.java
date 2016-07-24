package com.mesicspoark.kenan.spoark;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by kenmesi on 8/30/15.
 */
public class CreateGameFragment extends Fragment implements OnMapReadyCallback, LocationListener, AdapterView.OnItemSelectedListener  {
    View rootView;

    private final static String TAG = CreateGameFragment.class.getSimpleName();
    private String sport;
    private String description;
    private static View view;
    private Location currentLocation;
    private GoogleMap googleMap;


    public static Fragment newInstance() {
        Fragment fragment = new CreateGameFragment();
        return fragment;
    }

    public void updateCurrentLocation(Location currentLocation) {
        this.currentLocation = new Location(currentLocation);
        Log.d("CreateGame", "Location" + currentLocation.getLatitude());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.create_game, container, false);

        Spinner spinner = (Spinner) rootView.findViewById(R.id.select_sport);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sports, R.layout.spinner_layout);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        Button nextButton = (Button) rootView.findViewById(R.id.next_create_game_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //Create map to display in the frame layout
        SupportMapFragment mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map_view, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);

        return rootView;
    }

    // Callback function that gets called when the map is done loading
    @Override
    public void onMapReady(GoogleMap map) {
        //final LatLng PERTH = new LatLng(-31.90, 115.86);
        googleMap = map;
        map.addMarker(new MarkerOptions()
                .position(new LatLng(45, 55))
                .title("Marker")
                .visible(true)
                .draggable(true));
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
            this.getLocationUpdates();
        }
    }

    private void getLocationUpdates() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        String provider = locationManager.getBestProvider(criteria, true);

        try {
        Location location = null;
        location = locationManager.getLastKnownLocation(provider);


        if(location!=null){
            onLocationChanged(location);
        }

        // get location updates every 20 sec...time is in milliseconds
        locationManager.requestLocationUpdates(provider, 20000, 0, this);
        }
        catch(SecurityException e) {
            Log.d(TAG, "Permission denied");
            return;
        }

    }

    @Override
    public void onLocationChanged(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        LatLng latlng = new LatLng(latitude, longitude);
        if(googleMap != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }

        Log.i(TAG, "Location changed, " + location.getAccuracy() + " , " + location.getLatitude() + "," + location.getLongitude());
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Method needed for Location Listener
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Method needed for Location Listener
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Method needed for Location Listener
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}

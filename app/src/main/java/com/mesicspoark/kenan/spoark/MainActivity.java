package com.mesicspoark.kenan.spoark;

import android.content.IntentSender;
import android.location.Location;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import com.parse.ParseObject;


/**
 * Created by Kenan Mesic on 9/3/15.
 */
public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener,
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener,
        ResultCallback<LocationSettingsResult> {

    // Used for logging android app
    public static final String TAG = MainActivity.class.getSimpleName();

    // Used for request in going to settings
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;


    // Used for request to getting Google Play Services
    protected static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    // Used for saving the position of the navigation drawer when the orientation changes
    protected final static String STATE_SELECTED_POSITION = "position";

    // General time to update the location of the user
    protected static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // Fasted time to update the location of the user
    protected static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    protected final static String KEY_LOCATION = "location";

    protected Toolbar mToolbar;
    protected DrawerLayout mDrawerLayout;
    protected NavigationView mNavigationView;
    protected int mCurrentSelectedPosition;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest locationRequest;
    protected boolean requestingLocationUpdates = false;
    protected Location currentLocation;
    protected LocationSettingsRequest locationSettingsRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


        /*ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();*/

        //Change color of the status bar on top...this only works in Android SDK 21+
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.primary_dark));
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //Create the toolbar on top of the activity
        setUpToolbar();

        //Create a navigation menu on the right side
        mNavigationView = ((NavigationView)findViewById(R.id.navigation));
        mNavigationView.setNavigationItemSelectedListener(this);
        updateValuesFromBundle(savedInstanceState);

        //if there was a saved instance of the where in the menu it was
        if (savedInstanceState != null) {
            mCurrentSelectedPosition =
                    savedInstanceState.getInt(STATE_SELECTED_POSITION);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.container,  HomeFragment.newInstance("TEST")).commit();
        }

        mNavigationView.getMenu().getItem(mCurrentSelectedPosition).setChecked(true);

        // Create a location request for receiving the location
        createLocationRequest();
        // Create the location settings request to see if correct permissions are enabled
        // on the application
        buildLocationSettingsRequest();
        //Create Google API Client
        buildGoogleAPIClient();

    }

    //onStart for activity
    @Override
    protected void onStart() {
        super.onStart();
        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    //onStop for activity
    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Check the location settings...in onResult method if everything is okay
        // then it will call startLocationUpdates.
        if(!requestingLocationUpdates) {
            checkLocationSettings();
        }
    }

    @Override
    public void onConnectionSuspended(int suspended) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult failed) {
        Log.i(TAG, "Connection failed. Error: " + failed.getErrorCode());
    }

    //Create a location request
    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        locationSettingsRequest
                );
        result.setResultCallback(this);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else {
                Toast.makeText(this, "This device is not supported.",
                        Toast.LENGTH_LONG).show();
                finish();
            }

            return false;
        }

        return true;
    }

    protected void buildGoogleAPIClient() {
        // Get connected to the Google Location APIs
        if(checkPlayServices()) {
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
        }
    }
    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
    }

    // Start location updates by adding request to the google api
    protected boolean startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, locationRequest, this);
            if(currentLocation == null) {
                currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                CreateGameFragment createGame = (CreateGameFragment) getSupportFragmentManager().findFragmentByTag("create-game");
                if(createGame != null) {
                    createGame.updateCurrentLocation(currentLocation);
                }
            }
            return true;
        }
        catch (SecurityException e) {
            Log.i(TAG, "Unable to get last location!");
            return false;
        }
    }

    // Stop location updates by removing the requests
    protected void stopLocationUpdates() {
        if(requestingLocationUpdates) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
            requestingLocationUpdates = false;
        }
    }

    /**
     * The callback invoked when
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} is called. Examines the
     * {@link com.google.android.gms.location.LocationSettingsResult} object and determines if
     * location settings are adequate. If they are not, begins the process of presenting a location
     * settings dialog to the user.
     */
    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
                if(startLocationUpdates()) {
                 requestingLocationUpdates = true;
                }
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }


    // When the location changes set the current location to it
    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        CreateGameFragment createGame = (CreateGameFragment) getSupportFragmentManager().findFragmentByTag("create-game");
        if(createGame != null) {
            createGame.updateCurrentLocation(location);
        }
    }

    //Creates the activity toolbar...gets called by the onCreate method of Activity
    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            mToolbar.setNavigationIcon(R.drawable.ic_menu);
            mToolbar.setNavigationOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);

        switch (menuItem.getItemId()) {
            case R.id.item_home:
                mCurrentSelectedPosition = 0;
                getSupportFragmentManager().beginTransaction().replace(R.id.container, HomeFragment.newInstance("Home"), "home").commit();
                mDrawerLayout.closeDrawers();
                return true;

            case R.id.item_create_game:
                mCurrentSelectedPosition = 0;
                getSupportFragmentManager().beginTransaction().replace(R.id.container, CreateGameFragment.newInstance(), "create-game").commit();
                mDrawerLayout.closeDrawers();
                return true;

            case R.id.item_search:
                mCurrentSelectedPosition = 0;
                getSupportFragmentManager().beginTransaction().replace(R.id.container, SearchFragment.newInstance("Search"), "search").commit();
                mDrawerLayout.closeDrawers();
                return true;

            case R.id.item_mygames:
                mCurrentSelectedPosition = 0;
                getSupportFragmentManager().beginTransaction().replace(R.id.container, MyGamesFragment.newInstance("My Games"), "my-games").commit();
                mDrawerLayout.closeDrawers();
                return true;

            case R.id.item_myparks:
                mCurrentSelectedPosition = 0;
                getSupportFragmentManager().beginTransaction().replace(R.id.container, MyParksFragment.newInstance("My Parks"), "my-parks").commit();
                mDrawerLayout.closeDrawers();
                return true;

            case R.id.item_profile:
                mCurrentSelectedPosition = 0;
                getSupportFragmentManager().beginTransaction().replace(R.id.container, ProfileFragment.newInstance("Profile"), "profile").commit();
                mDrawerLayout.closeDrawers();
                return true;

            case R.id.item_settings:
                mCurrentSelectedPosition = 0;
                getSupportFragmentManager().beginTransaction().replace(R.id.container, SettingsFragment.newInstance("Settings"), "settings").commit();
                mDrawerLayout.closeDrawers();
                return true;
        }

        return false;
    }

    /*
    Used to save the instance of the selected item and then when orientation changes it can still be applied
     */

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
        outState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates);
        outState.putParcelable(KEY_LOCATION, currentLocation);
    }

    /*
    Used to get whatever was saved last to apply when orientation changes on the nav drawer
     */

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION, 0);
        Menu menu = mNavigationView.getMenu();
        menu.getItem(mCurrentSelectedPosition).setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mDrawerLayout != null && !mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.my, menu);
            return true;
        }
        else if(mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            getMenuInflater().inflate(R.menu.global, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up next_button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                requestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                currentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }

        }
    }

    /*
     * Method for replacing fragments of the create game fragments
     *
     * @param None
     */
    private void updateCreateGameFragments(String sport, Location location) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container,
                            CreateGameFinishFragment.newInstance(sport, Double.toString(location.getLatitude()),
                                                                        Double.toString(location.getLongitude())));
        transaction.commit();
    }
}

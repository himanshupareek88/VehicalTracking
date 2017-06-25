package com.vehicaltracking;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.vehicaltracking.Utitlty.MarshmallowPermission;
import com.vehicaltracking.Utitlty.SaveCard_Service;
import com.vehicaltracking.database.DatabaseHandler;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LocationActivity extends AppCompatActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, CompoundButton.OnCheckedChangeListener {
    private LocationManager locationManager;


    GoogleApiClient mGoogleApiClient;
    private LocationRequest mlocationRequest;
    LocationSettingsRequest.Builder builder;
    private final String TAG = LocationActivity.class.getSimpleName();
    public static Location mprev_location;
    private Switch switch_location;
    private String provider;

    long interval = 30, old_interval = 30;
    double speed_OLD, speed_new;

    double speedinterval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        setUpView();
        setUpActionView();
    }


    //initialize views
    private void setUpView() {
        switch_location = (Switch) findViewById(R.id.swlocation);
    }

    //implement all listener here
    private void setUpActionView() {
        switch_location.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 23) {
            if (MarshmallowPermission.getInstance(this).LocationPermission()) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    //Request location updates:
                    settingsLocationRequest();
                    if (mGoogleApiClient.isConnected()) {
                        startLocationUpdates();
                        Log.d(TAG, "Location update resumed .....................");
                    }
                }
            }

            if (!MarshmallowPermission.getInstance(this).isWritetoStorageAllowed()) {
                MarshmallowPermission.getInstance(this).requestStoragePermission();
            }
        } else {
            settingsLocationRequest();

        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }


    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mlocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }


    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MarshmallowPermission.REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        settingsLocationRequest();
                    }

                } else {

                    // permission denied, Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MarshmallowPermission.STORAGE_PERMISSION_CODE: {

                //If permission is granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "permission grated", Toast.LENGTH_LONG).show();

                } else {
                    //Displaying another toast if permission is not granted
                    Toast.makeText(this, "Oops We need this  permission", Toast.LENGTH_LONG).show();
                }
            }


        }
    }


    public void settingsLocationRequest() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(LocationActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            mGoogleApiClient.connect();
        }
        CreateLocationRequest();

        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient
        //**************************

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        if (mGoogleApiClient.isConnected()) {
                            startLocationUpdates();
                            Log.d(TAG, "Location update resumed 1 .....................");
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(LocationActivity.this, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });


    }

    private void CreateLocationRequest() {
        mlocationRequest = LocationRequest.create();
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mlocationRequest.setInterval(interval * 1000);
        mlocationRequest.setFastestInterval(interval * 1000);

        builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mlocationRequest);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }


    @Override
    public void onLocationChanged(Location location) {
          TrackLocation(location);

    }

    private void TrackLocation(Location location) {

        if (location != null) {
            DatabaseHandler.getDatabaseInstance(this).addContactLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
            if (mprev_location == null)
                mprev_location = location;

            double distance = location.distanceTo(mprev_location);
            double diffTime_sec = (double) (location.getTime() - mprev_location.getTime()) / 1000.0;
            if (distance > 0 && diffTime_sec > 0) {
                speed_new = (distance / diffTime_sec);  //speed in m/s
                speed_new = speed_new * 3.6f;
                Log.d("cal", "distance meter..............." + distance);
                Log.d("cal", "time second..............." + diffTime_sec);
                Log.d("cal", "oldspeed..............." + speed_OLD);
                Log.d("cal", "newspeed.in k/h.............." + speed_new + "  speedinterval " + speedinterval);

                speedinterval = Math.abs(speed_OLD - speed_new);
                if (speedinterval >= 80) {
                    interval = 30;
                } else if (speedinterval >= 60 && speedinterval < 80) {
                    interval = 60;
                } else if (speedinterval >= 30 && speedinterval < 60) {
                    interval = 60 * 2;
                } else if (speedinterval > 0 && speedinterval < 30) {
                    interval = 60 * 5;
                }

                stopLocationUpdates();
                settingsLocationRequest();

            }

            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy-hh:mm:ss:SSS");

            Date date = new Date(location.getTime());
            String date_str = format.format(date);
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            String text_print = date_str + " " + String.valueOf(decimalFormat.format(location.getLatitude())) + " " + String.valueOf(decimalFormat.format(location.getLongitude())) + " " + old_interval + " " + interval;


            Log.d(TAG, "text_print ..............." + text_print);

            if (MarshmallowPermission.getInstance(this).isWritetoStorageAllowed()) {
                Intent intent = new Intent(LocationActivity.this, SaveCard_Service.class);
                intent.putExtra("textdata", text_print);
                startService(intent);
            }


            speed_OLD = speed_new;
            old_interval = interval;
            mprev_location = location;

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean is_checked) {
        if (is_checked) {
            //enable location tracking
            startLocationUpdates();
        } else {
            //stoplocationtracking
            stopLocationUpdates();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseHandler.getDatabaseInstance(this).close();
    }
}

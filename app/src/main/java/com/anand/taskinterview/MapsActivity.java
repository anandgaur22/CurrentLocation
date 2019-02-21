package com.anand.taskinterview;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback
        {

            private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 5445;

            private GoogleMap googleMap;
            private FusedLocationProviderClient fusedLocationProviderClient;
            private Marker currentLocationMarker;
            private Location currentLocation;
            private boolean firstTimeFlag = true;

            Database database;
            File newImageFile;

            private final View.OnClickListener clickListener = view -> {
                if (view.getId() == R.id.currentLocationImageButton && googleMap != null && currentLocation != null)
                    animateCamera(currentLocation);
            };

            private final LocationCallback mLocationCallback = new LocationCallback() {

                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    if (locationResult.getLastLocation() == null)
                        return;
                    currentLocation = locationResult.getLastLocation();
                    if (firstTimeFlag && googleMap != null) {
                        animateCamera(currentLocation);
                        firstTimeFlag = false;
                    }
                    showMarker(currentLocation);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        supportMapFragment.getMapAsync(this);
        findViewById(R.id.currentLocationImageButton).setOnClickListener(clickListener);

        database = new Database(MapsActivity.this);
        database.open();
        String retrieveImage =database.getImagePath();
         newImageFile = new File(retrieveImage);
    }



            @Override
            public void onMapReady(GoogleMap googleMap) {
                this.googleMap = googleMap;
            }

            private void startCurrentLocationUpdates() {
                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(3000);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MapsActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                        return;
                    }
                }
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
            }

            private boolean isGooglePlayServicesAvailable() {
                GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
                int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
                if (ConnectionResult.SUCCESS == status)
                    return true;
                else {
                    if (googleApiAvailability.isUserResolvableError(status))
                        Toast.makeText(this, "Please Install google play services to use this application", Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                        Toast.makeText(this, "Permission denied by uses", Toast.LENGTH_SHORT).show();
                    else if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        startCurrentLocationUpdates();
                }
            }

            private void animateCamera(@NonNull Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(latLng)));
            }

            @NonNull
            private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
                return new CameraPosition.Builder().target(latLng).zoom(16).build();
            }

            private void showMarker(@NonNull Location currentLocation) {



               try {
                   LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                   if (String.valueOf(newImageFile).toString().equalsIgnoreCase("null")){

                       if (currentLocationMarker == null)
                           currentLocationMarker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource((R.drawable.ic_room_black_24dp))).position(latLng));
                       else
                           MarkerAnimation.animateMarkerToGB(currentLocationMarker, latLng, new LatLngInterpolator.Spherical());


                   }else {
                       if (currentLocationMarker == null)
                           currentLocationMarker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromPath(String.valueOf(newImageFile))).position(latLng));
                       else
                           MarkerAnimation.animateMarkerToGB(currentLocationMarker, latLng, new LatLngInterpolator.Spherical());


                   }


               }catch (Exception e){

                   e.printStackTrace();
               }


    }

            @Override
            protected void onStop() {
                super.onStop();
                if (fusedLocationProviderClient != null)
                    fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
            }

            @Override
            protected void onResume() {
                super.onResume();
                if (isGooglePlayServicesAvailable()) {
                    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                    startCurrentLocationUpdates();
                }
            }

            @Override
            protected void onDestroy() {
                super.onDestroy();
                fusedLocationProviderClient = null;
                googleMap = null;
            }
}

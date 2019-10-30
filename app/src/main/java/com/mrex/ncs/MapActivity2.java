//package com.mrex.ncs;
//
//import android.Manifest;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.inputmethodservice.InputMethodService;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.os.Build;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.inputmethod.EditorInfo;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.FragmentActivity;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.UiSettings;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Locale;
//
//public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener {
//
//    final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10;
//    final int DEFAULT_ZOOM = 15;
//    final LatLng DEFAULT_LOCATION = new LatLng(37.56647, 126.977963);
//
//    private GoogleMap gMap;
//    private Boolean isLocationPermissionGranted;
//    private Location lastLocation;
//    private Location currentLocation;
//    private LatLng latLngCenter;
//    private LatLng searchedLatLng;
//    private Task locationResult;
//    private Marker currentMarker = null;
//    private String markerTitle;
//    private String address1;
//    private TextView tvAddress;
//    private EditText etSearch;
//    private String addressSearch;
//    private Intent intent;
//
//    private FusedLocationProviderClient fusedLocationProviderClient;
//    private Geocoder geocoder;
//    private InputMethodManager inputMethodManager;
////    private CameraUpdate cameraUpdate;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_map2);
//
//        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//
//        tvAddress = findViewById(R.id.tv_address);
//        etSearch = findViewById(R.id.et_address_search);
//        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    inputMethodManager.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
//                    geoCoding();
//                    return true;
//                }
//                return false;
//            }
//        });
//
//
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//
//
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//
//        gMap = googleMap;
//
//        gMap.setOnCameraIdleListener(this);
//
//        setDefaultLocation();
//
//
//    }
//
//    @Override
//    public void onCameraIdle() {
//        reverseGeoCoding();
//    }
//
//    public void reverseGeoCoding() {
//        Toast.makeText(this, "reverseGeoCoding", Toast.LENGTH_SHORT).show();
//        Log.v("LOG", "reverseGeoCoding");
//        latLngCenter = gMap.getCameraPosition().target;
//
//        geocoder = new Geocoder(this, Locale.KOREA);
//
//        try {
//            List<Address> addresses = geocoder.getFromLocation(latLngCenter.latitude, latLngCenter.longitude, 1);
//            StringBuffer buffer = new StringBuffer();
//            for (Address t : addresses) {
//                buffer.append(t.getAddressLine(0) + " ");
//            }
//            if (buffer.length() > 5) {
//                address1 = (buffer.toString()).substring(5);
//                tvAddress.setText(address1);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public void geoCoding() {
//        Toast.makeText(this, "geoCoding", Toast.LENGTH_SHORT).show();
//        addressSearch = etSearch.getText().toString();
//
//        geocoder = new Geocoder(this, Locale.KOREA);
//
//        try {
//            List<Address> addresses = geocoder.getFromLocationName(addressSearch, 1);
//
//            for (Address t : addresses) {
//                searchedLatLng = new LatLng(t.getLatitude(), t.getLongitude());
//
//            }
//            if (searchedLatLng==null) {
//                Toast.makeText(this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show();
//                etSearch.setText("");
//                return;
//            }
//
//            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchedLatLng, DEFAULT_ZOOM));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public void setDefaultLocation() {
//
//
//    }
//
//    public void back(View view) {
//        finish();
//    }
//
//    private void getLocationPermission() {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                isLocationPermissionGranted = true;
//            } else {
//                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//                isLocationPermissionGranted = false;
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    isLocationPermissionGranted = true;
//                    Toast.makeText(this, "위치정보제공에 동의하셨습니다.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }
//
//    public void myLocation(View view) {
////        getLocationPermission();
////        if (isLocationPermissionGranted) {
////            locationResult = fusedLocationProviderClient.getLastLocation();
////            locationResult.addOnCompleteListener(this, new OnCompleteListener() {
////                @Override
////                public void onComplete(@NonNull Task task) {
////                    if (task.isSuccessful()) {
////                        lastLocation = (Location) task.getResult();
////                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), DEFAULT_ZOOM));
////                    } else {
////                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));
////                    }
////                }
////            });
////
////        }
//    }
//
//
//    public void next(View view) {
//
//    }
//}

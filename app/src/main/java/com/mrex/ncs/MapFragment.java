package com.mrex.ncs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.github.clans.fab.FloatingActionButton;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

public class MapFragment extends Fragment implements MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener {

    final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10;
    final int DEFAULT_ZOOM = 15;
//    final LatLng DEFAULT_LOCATION = new LatLng(37.56647, 126.977963);

    private MapView map;
    private Boolean isLocationPermissionGranted;
    private MapPoint.GeoCoordinate currentLocation;
    private MapReverseGeoCoder mapReverseGeoCoder;
    private TextView tvAddress1;
    private TextView tvNext;
    private ViewGroup mapViewContainer;

    private String selectedPlaceName = "";
    private String selectedAddress;
    private Double selectedLng;
    private Double selectedLat;


    public MapFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.e("TAG", "MF onCreateView");

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mapViewContainer = view.findViewById(R.id.map_view);

        tvNext = view.findViewById(R.id.tv_next);
        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddressActivity.class);
                intent.putExtra("lat", selectedLat);
                intent.putExtra("lng", selectedLng);
                intent.putExtra("address", selectedAddress);
                intent.putExtra("place", selectedPlaceName);
                startActivity(intent);
            }
        });

        tvAddress1 = view.findViewById(R.id.tv_address1);

        FloatingActionButton fab = view.findViewById(R.id.fab_my_location);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLocation();
            }
        });

//        bundle = getArguments();
//        if (bundle != null) {
//            selectedPlaceName = bundle.getString("placeName", "");
//            selectedAddress = bundle.getString("address", "");
//            selectedX = bundle.getDouble("x");
//            selectedY = bundle.getDouble("y");
////            selectedMapPoint = MapPoint.mapPointWithGeoCoord(bundle.getDouble("x"), bundle.getDouble("y"));
//            Log.e("TAG", selectedPlaceName + selectedAddress + selectedX + "   " + selectedY);
//        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("TAG","MF onResume");
        map = new MapView(getActivity());
        mapViewContainer.addView(map);

        map.setMapViewEventListener(mapEventListener);
        map.setCurrentLocationEventListener(this);
    }

    @Override
    public void onPause() {
        Log.e("TAG","MF onPause");
        super.onPause();
        mapViewContainer.removeView(map);
        map = null;
    }

    private void getLocationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionGranted = true;

            } else {
                isLocationPermissionGranted = false;
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isLocationPermissionGranted = true;
                    Toast.makeText(getContext(), "위치정보제공에 동의하셨습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void myLocation() {
        getLocationPermission();
        if (isLocationPermissionGranted) {
            map.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        }
    }

    MapEventListener mapEventListener = new MapEventListener() {
        @Override
        public void onMapViewInitialized(MapView mapView) {
            super.onMapViewInitialized(mapView);
            Log.e("TAG                    ", "onMapViewInitialized");

            mapReverseGeoCoder = new MapReverseGeoCoder(getString(R.string.kakao_app_key), mapView.getMapCenterPoint(), MapFragment.this, getActivity());
            mapReverseGeoCoder.startFindingAddress();

//            if (bundle != null) {
//                Log.e("TAG", "animateCamera");
//                map.animateCamera(CameraUpdateFactory.newMapPoint(MapPoint.mapPointWithGeoCoord(selectedY,selectedX)));
//                tvAddress1.setText(selectedAddress+" "+selectedPlaceName);
//            }

        }

        @Override
        public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
            Log.e("TAG                    ", "onMapViewMoveFinished");
            super.onMapViewMoveFinished(mapView, mapPoint);
            mapReverseGeoCoder = new MapReverseGeoCoder(getString(R.string.kakao_app_key), mapView.getMapCenterPoint(), MapFragment.this, getActivity());
            mapReverseGeoCoder.startFindingAddress();
            selectedLat = mapView.getMapCenterPoint().getMapPointGeoCoord().latitude;
            selectedLng = mapView.getMapCenterPoint().getMapPointGeoCoord().longitude;
        }
    };

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        tvAddress1.setText(s);
        selectedAddress = s;
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {

        currentLocation = mapPoint.getMapPointGeoCoord();
        map.animateCamera(CameraUpdateFactory.newMapPoint(MapPoint.mapPointWithGeoCoord(currentLocation.latitude, currentLocation.longitude)));
        map.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        map.setShowCurrentLocationMarker(false);
    }

    //MapView.CurrentLocationEventListener
    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    //MapReverseGeoCoder.ReverseGeoCodingResultListener
    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {

    }
}

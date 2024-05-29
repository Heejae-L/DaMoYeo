package android.org.firebasetest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient; // FusedLocationProviderClient 선언
    private static final int LOCATION_PERMISSION_REQUEST = 1;
    private List<Marker> markers = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        // FusedLocationProviderClient 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // 지도 프래그먼트 설정
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        requestLocationPermission(); // 위치 권한 요청 메서드 호출

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) { // 지도 준비 완료 시 호출
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false); // 확대/축소 컨트롤 활성화
        mMap.getUiSettings().setMyLocationButtonEnabled(true); // 기본 '내 위치' 버튼 비활성화
        mMap.setOnMapClickListener(this::handleMapClick); // 지도 클릭 이벤트

        // 위치 권한이 있는 경우, 현재 위치로 지도 이동
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            initializeMapToCurrentLocation(); // 현재 위치로 지도 초기화 메서드 호출
        }

        mMap.setOnMarkerClickListener(marker -> {
            marker.remove();
            markers.remove(marker);
            return true;
        });
    }

    private void requestLocationPermission() { // 위치 권한 요청
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        }
    }

    private void handleMapClick(LatLng latLng) { // 지도 클릭 시 마커 추가
        mMap.addMarker(new MarkerOptions().position(latLng));
        Toast.makeText(requireActivity(), "Saved Location: " + latLng.latitude + ", " + latLng.longitude, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { // 권한 요청 결과 처리
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    initializeMapToCurrentLocation(); // 위치 권한이 허용된 경우, 현재 위치로 지도 초기화 메서드 호출
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                Toast.makeText(requireActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeMapToCurrentLocation() {
        // Check if the location permission is granted
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Get the last known location
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    // Convert the current location into a LatLng object
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    // Move the camera to the current location and zoom in
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                } else {
                    // If no location is available, inform the user
                    Toast.makeText(requireActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Permission is not granted, inform the user
            Toast.makeText(requireActivity(), "Location permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

}

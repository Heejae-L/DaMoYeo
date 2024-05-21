package android.org.firebasetest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Location mLastKnownLocation;
    private static final int LOCATION_PERMISSION_REQUEST = 1;
    private List<Marker> markers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_location);

        // Google Places API 초기화
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyBECizExzq2-38B-_v10_Dqdcb5u_RdGd4");
        }

        // 자동완성 프래그먼트 설정
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT); // 검색 필터 설정: 업체
        autocompleteFragment.setCountries("KR"); // 검색 국가 제한: 한국
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)); // 장소 필드 설정
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) { // 장소 선택 시 이벤트 핸들러
                LatLng latLng = place.getLatLng(); // 선택된 장소의 위도, 경도 정보
                if (latLng != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15)); // 지도 카메라 이동
                    mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName())); // 마커 추가
                    Toast.makeText(MapActivity.this, "Location: " + place.getName(), Toast.LENGTH_LONG).show(); // 토스트 메시지 출력
                }
            }

            @Override
            public void onError(com.google.android.gms.common.api.Status status) { // 오류 발생 시 처리
                Toast.makeText(MapActivity.this, "An error occurred: " + status, Toast.LENGTH_SHORT).show();
            }
        });

        // 지도 프래그먼트 설정
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        requestLocationPermission(); // 위치 권한 요청 메서드 호출
    }

    @Override
    public void onMapReady(GoogleMap googleMap) { // 지도 준비 완료 시 호출
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true); // 확대/축소 컨트롤 활성화
        mMap.getUiSettings().setMyLocationButtonEnabled(true); // 기본 '내 위치' 버튼 비활성화
        mMap.setOnMapClickListener(this::handleMapClick); // 지도 클릭 이벤트

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true); // 위치 권한이 있는 경우, '내 위치' 표시 활성화
        }

        mMap.setOnMarkerClickListener(marker -> {
            marker.remove();
            markers.remove(marker);
            return true;
        });
    }

    private void requestLocationPermission() { // 위치 권한 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        }
    }

    /*
    private void resetToCurrentLocation() { // 현재 위치로 리셋
        if (mLastKnownLocation != null) {
            LatLng currentLatLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
        } else {
            Toast.makeText(this, "Current location not available", Toast.LENGTH_SHORT).show();
        }
    }

     */

    private void handleMapClick(LatLng latLng) { // 지도 클릭 시 마커 추가
        mMap.addMarker(new MarkerOptions().position(latLng));
        Toast.makeText(this, "Saved Location: " + latLng.latitude + ", " + latLng.longitude, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) { // 권한 요청 결과 처리
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

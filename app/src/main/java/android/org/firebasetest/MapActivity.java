package android.org.firebasetest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST = 1;
    private MapManager mapManager;
    private String groupId;
    private Group group;
    private Map map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        // Google Places API 초기화
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyBECizExzq2-38B-_v10_Dqdcb5u_RdGd4");
        }

        group = getIntent().getParcelableExtra("group");
        mapManager = new MapManager(group);

        refreshMaps();

        // 자동완성 프래그먼트 설정
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT);
        autocompleteFragment.setCountries("KR");
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng latLng = place.getLatLng();
                if (latLng != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()).snippet("Place ID: " + place.getId()));
                    marker.showInfoWindow();
                    Toast.makeText(MapActivity.this, "Location: " + place.getName(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(com.google.android.gms.common.api.Status status) {
                Toast.makeText(MapActivity.this, "An error occurred: " + status, Toast.LENGTH_SHORT).show();
            }
        });

        // 지도 프래그먼트 설정
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        requestLocationPermission();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMapClickListener(this::handleMapClick);

        mMap.setOnMarkerClickListener(marker -> {
            showPopupWindow(marker);
            return true;
        });
    }

    private void fetchMarkerData(String markerId, MarkerInfoWindowAdapter adapter, Marker marker) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("maps").child(markerId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map map = dataSnapshot.getValue(Map.class);
                if (map != null) {
                    adapter.setMarkerData(map.getMaptitle(), map.getMapdate(), map.getMapinfo());
                    marker.showInfoWindow();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MapActivity", "Error fetching marker data: " + databaseError.getMessage());
            }
        });
    }



    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        }
    }

    private void handleMapClick(LatLng latLng) {
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Clicked Location").snippet("Lat: " + latLng.latitude + ", Lng: " + latLng.longitude));
        marker.showInfoWindow();
        Toast.makeText(this, "Saved Location: " + latLng.latitude + ", " + latLng.longitude, Toast.LENGTH_LONG).show();
    }

    private void showPopupWindow(Marker marker) {
        // 팝업 윈도우 레이아웃을 인플레이트
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.marker_info_window, null);

        // 팝업 윈도우 생성
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // 팝업 외부를 터치하면 닫힘
        PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // 팝업 윈도우에 마커 정보 설정
        TextView titleView = popupView.findViewById(R.id.title);
        TextView dateView = popupView.findViewById(R.id.date_info); // EditText를 TextView로 변경
        TextView additionalInfoView = popupView.findViewById(R.id.additional_info);
        Button addButton = popupView.findViewById(R.id.add_button); // 추가 버튼

        titleView.setText(marker.getTitle());
        dateView.setText("Date"); // 기본 텍스트 또는 로직에 따라 설정
        additionalInfoView.setText("Additional information");

        // 팝업 윈도우 표시
        popupWindow.showAtLocation(findViewById(R.id.map), Gravity.CENTER, 0, 0);

        // 삭제 버튼 동작 설정
        Button deleteButton = popupView.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(v -> {
            marker.remove();
            popupWindow.dismiss();
        });

        addButton.setOnClickListener(v -> {
            // MapTitle을 TextView로 변환
            TextView titleTextView = new TextView(MapActivity.this);
            titleTextView.setLayoutParams(titleView.getLayoutParams());
            titleTextView.setText(titleView.getText());
            titleTextView.setTextSize(16);
            titleTextView.setTextColor(ContextCompat.getColor(MapActivity.this, android.R.color.black));
            titleTextView.setPadding(0, 10, 0, 10);
            titleTextView.setFocusable(false); // TextView를 편집할 수 없게 설정
            titleTextView.setClickable(false); // TextView를 클릭할 수 없게 설정

            // Additional Info를 TextView로 변환
            TextView additionalInfoTextView = new TextView(MapActivity.this);
            additionalInfoTextView.setLayoutParams(additionalInfoView.getLayoutParams());
            additionalInfoTextView.setText(additionalInfoView.getText());
            additionalInfoTextView.setTextSize(16);
            additionalInfoTextView.setTextColor(ContextCompat.getColor(MapActivity.this, android.R.color.black));
            additionalInfoTextView.setPadding(0, 10, 0, 10);
            additionalInfoTextView.setFocusable(false); // TextView를 편집할 수 없게 설정
            additionalInfoTextView.setClickable(false); // TextView를 클릭할 수 없게 설정

            // EditText를 TextView로 변환하고, PopupWindow 내의 뷰 교체
            LinearLayout parentLayout = (LinearLayout) titleView.getParent();
            int indexTitle = parentLayout.indexOfChild(titleView);
            int indexAdditionalInfo = parentLayout.indexOfChild(additionalInfoView);
            parentLayout.removeView(titleView);
            parentLayout.removeView(additionalInfoView);
            parentLayout.addView(titleTextView, indexTitle);
            parentLayout.addView(additionalInfoTextView, indexAdditionalInfo);

            // Map 객체 생성 저장
            String mapId = mapManager.getDatabase().push().getKey(); // Generate unique ID for the memo
            String title = titleTextView.getText().toString();
            String dateInfo = dateView.getText().toString();
            String additionalInfo = additionalInfoTextView.getText().toString();
            LatLng position = marker.getPosition();

            group = getIntent().getParcelableExtra("group");
            groupId = getIntent().getStringExtra("groupId");

            Log.e("MapActivity","mapId : "+ mapId);

            Map map = new Map(mapId, title, dateInfo, additionalInfo, position.latitude, position.longitude, groupId);
            Log.d("Map","Map:"+title);

            mapManager.saveMap(group, map);

            // Show a success message
            Toast.makeText(MapActivity.this, "Map saved successfully", Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
        });

        // 마커 정보 로그 출력
        Log.d("MapActivity", "Marker clicked: " + marker.getTitle() + ", Position: " + marker.getPosition().latitude + ", " + marker.getPosition().longitude);
    }

    private void refreshMaps() {
        group = getIntent().getParcelableExtra("group");
        groupId = group.getGroupId();

        Log.e("MapActivity", "refreshMap 실행 ");


        if (groupId != null) {
            mapManager.fetchMapsByGroupId(groupId, new MapManager.MapsCallback() {

                @Override
                public void onMapsRetrieved(List<Map> maps) {
                    if (mMap != null) { // mMap이 null이 아닐 때에만 마커 추가
                        for (Map map : maps) {
                            // 맵 데이터에서 위치 정보 가져오기
                            LatLng position = new LatLng(map.getLatitude(), map.getLongitude());
                            // 마커 추가
                            MarkerOptions markerOptions = new MarkerOptions().position(position)
                                    .title(map.getMaptitle())
                                    .snippet("Date: " + map.getMapdate() + "\nInfo: " + map.getMapinfo());
                            mMap.addMarker(markerOptions);
                            Log.e("MapActivity", position + " 위치");
                        }
                    } else {
                        Log.e("MapActivity", "mMap is null, cannot add markers");
                    }
                }

                @Override
                public void onError(Exception exception) {
                    Log.e("MapActivity", "Error fetching maps: " + exception.getMessage());
                }
            });
        } else {
            Toast.makeText(this, "Group ID is not specified", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

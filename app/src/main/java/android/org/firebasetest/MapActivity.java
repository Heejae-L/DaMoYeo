package android.org.firebasetest;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.org.firebasetest.MapManager;
import android.org.firebasetest.MarkerInfoWindowAdapter;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import java.util.HashMap;
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
        groupId = group.getGroupId();
        Log.e("MapActivity","groupId "+groupId);

        mapManager = new MapManager(group);


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
            String markerId = (String) marker.getTag();
            if (markerId != null) {
                showPopupWindow(marker);
            }
            return true;
        });

        loadMarkersFromDatabase();
    }

    private void handleMapClick(LatLng latLng) {
        String markerId = FirebaseDatabase.getInstance().getReference("maps").push().getKey();
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Clicked Location").snippet("Lat: " + latLng.latitude + ", Lng: " + latLng.longitude));
        marker.setTag(markerId);
        showPopupWindow(marker);
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        }
    }


    private void showPopupWindow(Marker marker) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.marker_info_window, null);
        PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        TextView titleView = popupView.findViewById(R.id.title);
        TextView dateView = popupView.findViewById(R.id.date_info);
        TextView additionalInfoView = popupView.findViewById(R.id.additional_info);
        Button addButton = popupView.findViewById(R.id.add_button);

        String markerId = (String) marker.getTag();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("maps").child(markerId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map map = dataSnapshot.getValue(Map.class);
                if (map != null) {
                    titleView.setText(map.getMaptitle());
                    dateView.setText(map.getMapdate());
                    additionalInfoView.setText(map.getMapinfo());
                } else {
                    titleView.setHint("Enter title");
                    dateView.setHint("Enter date");
                    additionalInfoView.setHint("Enter additional info");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MapActivity", "Error fetching marker data: " + databaseError.getMessage());
            }
        });

        Button deleteButton = popupView.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(v -> {
            if (marker.getTag() != null) {
                //String markerId = marker.getTag().toString();
                mapManager.deleteMap(markerId); // Call to your MapManager to handle deletion in Firebase
                marker.remove(); // Remove the marker from the map
                popupWindow.dismiss(); // Dismiss the popup window
                Toast.makeText(MapActivity.this, "Marker deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MapActivity.this, "Error deleting marker", Toast.LENGTH_SHORT).show();
            }
        });


        addButton.setOnClickListener(v -> {
            saveMarkerData(markerId, titleView, dateView, additionalInfoView, marker);
            popupWindow.dismiss();
        });



        addButton.setOnClickListener(v -> {

            saveMarkerData(markerId, titleView, dateView, additionalInfoView, marker);

            String title = titleView.getText().toString();
            String date = dateView.getText().toString();
            String additionalInfo = additionalInfoView.getText().toString();

            if (title.isEmpty() || date.isEmpty() || additionalInfo.isEmpty()) {
                Toast.makeText(MapActivity.this, "All fields are required.", Toast.LENGTH_LONG).show();
                return;
            }

            LatLng position = marker.getPosition();
            Map newMap = new Map(markerId, title, date, additionalInfo, position.latitude, position.longitude, groupId);
            DatabaseReference mapsRef = FirebaseDatabase.getInstance().getReference("maps").child(markerId);
            mapsRef.setValue(newMap).addOnSuccessListener(aVoid -> {
                Toast.makeText(MapActivity.this, "Map saved successfully", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();

                Log.e("savedpoint","markerId "+markerId);
                Log.e("savedpoint","title "+title);
                Log.e("savedpoint","additonalInfo "+additionalInfo);
                Log.e("savedpoint","groupId "+groupId);

            }).addOnFailureListener(e -> {
                Toast.makeText(MapActivity.this, "Failed to save map", Toast.LENGTH_SHORT).show();
            });
        });


        popupWindow.showAtLocation(findViewById(R.id.map), Gravity.CENTER, 0, 0);
    }

    private void saveMarkerData(String markerId, TextView titleView, TextView dateView, TextView additionalInfoView, Marker marker) {
        String title = titleView.getText().toString();
        String date = dateView.getText().toString();
        String additionalInfo = additionalInfoView.getText().toString();

        if (title.isEmpty() || date.isEmpty() || additionalInfo.isEmpty()) {
            Toast.makeText(MapActivity.this, "All fields are required.", Toast.LENGTH_LONG).show();
            return;
        }

        LatLng position = marker.getPosition();
        Map map = new Map(markerId, title, date, additionalInfo, position.latitude, position.longitude, groupId);
        mapManager.saveMap(group, map);
        DatabaseReference mapsRef = FirebaseDatabase.getInstance().getReference("maps").child(markerId);

    }

    private void loadMarkersFromDatabase() {
        if (groupId == null) {
            Log.e("MapActivity", "groupId is null");
            return;  // groupId가 null이면 함수 실행을 중단합니다.
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("groups").child(groupId).child("maps");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //mMap.clear(); // 이전에 추가된 마커를 지우고 새로운 데이터로 마커를 추가합니다.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map map = snapshot.getValue(Map.class);
                    if (map != null) {
                        LatLng position = new LatLng(map.getLatitude(), map.getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(position)
                                .title(map.getMaptitle())
                                .snippet("Date: " + map.getMapdate() + "\nInfo: " + map.getMapinfo());
                        Marker marker = mMap.addMarker(markerOptions);
                        marker.setTag(map.getMapId());  // 마커에 고유 ID를 태그로 저장합니다.
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MapActivity", "Error loading markers: " + databaseError.getMessage());
            }
        });
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
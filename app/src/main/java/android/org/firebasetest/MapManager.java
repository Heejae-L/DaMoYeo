package android.org.firebasetest;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapManager {
    private DatabaseReference database;

    public MapManager(Group group) {
        database = FirebaseDatabase.getInstance().getReference("groups").child(group.getGroupId()).child("maps");
    }


    public DatabaseReference getDatabase() {
        return database;
    }

    public void saveMap(Group group, Map map) {
        database.child(map.getMapId()).setValue(map);
        Log.d("saveMap","Map:"+map.getMaptitle());
    }

    public void deleteMap(String mapId) {
        database.child(mapId).removeValue();
    }

    public void fetchMapsByGroupId(String groupId, MapsCallback callback) {
        Log.d("MapManager", "fetchMapsByGroupId called with groupId: " + groupId);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("MapManager", "DataSnapshot received: " + dataSnapshot.toString());
                List<Map> groupMaps = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map map = snapshot.getValue(Map.class);
                    groupMaps.add(map);
                }
                callback.onMapsRetrieved(groupMaps);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MapManager", "Error fetching maps: " + databaseError.getMessage());
                callback.onError(databaseError.toException());
            }
        });
    }

    // MapManager 클래스에 메소드 추가
    public void fetchMapById(String mapId, MapCallback callback) {
        database.child(mapId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map map = dataSnapshot.getValue(Map.class);
                if (map != null) {
                    callback.onMapRetrieved(map);
                } else {
                    callback.onError(new Exception("Map not found"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }


    public interface MapsCallback {
        void onMapsRetrieved(List<Map> maps);
        void onError(Exception exception);
    }

    public interface MapCallback {
        void onMapRetrieved(Map map);
        void onError(Exception exception);
    }
}

package android.org.firebasetest;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Map implements Parcelable {

    private String MapId;
    private String Maptitle;
    private String Mapdate;
    private String Mapinfo;
    private double latitude;
    private double longitude;
    private String groupId;

    public Map(){}
    public Map(String MapId, String Maptitle, String Mapdate, String Mapinfo, double latitude, double longitude, String groupId) {
        this.MapId = MapId;
        this.Maptitle = Maptitle;
        this.Mapdate = Mapdate;
        this.Mapinfo = Mapinfo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.groupId = groupId;
    }

    protected Map(Parcel in) {
        MapId = in.readString();
        Maptitle = in.readString();
        Mapdate = in.readString();
        Mapinfo = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        groupId = in.readString();
    }

    public static final Creator<Map> CREATOR = new Creator<Map>() {
        @Override
        public Map createFromParcel(Parcel in) {
            return new Map(in);
        }

        @Override
        public Map[] newArray(int size) {
            return new Map[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(MapId);
        dest.writeString(Maptitle);
        dest.writeString(Mapdate);
        dest.writeString(Mapinfo);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(groupId);
    }

    public String getMapId() { return MapId; }
    public void setMapId(String MapId) { this.MapId = MapId; }
    public String getMaptitle() { return Maptitle; }
    public void setMaptitle(String Maptitle) { this.Maptitle = Maptitle; }
    public String getMapdate() { return Mapdate; }
    public void setMapdate(String Mapdate) { this.Mapdate = Mapdate; }
    public String getMapinfo() { return Mapinfo; }
    public void setMapinfo(String Mapinfo) { this.Mapinfo = Mapinfo; }
    public Double getLatitude(){return latitude;}
    public void setLatitude(Double latitude){this.latitude = latitude;}
    public Double getLongitude(){return longitude;}
    public void setLongitude(Double longitude){this.longitude = longitude;}



}

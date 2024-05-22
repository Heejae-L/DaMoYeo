package android.org.firebasetest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View window;
    private String title;
    private String dateInfo;
    private String additionalInfo;

    public MarkerInfoWindowAdapter(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        window = inflater.inflate(R.layout.marker_info_window, null);
    }

    public void setMarkerData(String title, String dateInfo, String additionalInfo) {
        this.title = title;
        this.dateInfo = dateInfo;
        this.additionalInfo = additionalInfo;
    }

    private void renderWindowText(Marker marker, View view) {
        TextView titleView = view.findViewById(R.id.title);
        if (title != null) {
            titleView.setText(title);
        }

        TextView dateInfoView = view.findViewById(R.id.date_info);
        if (dateInfo != null) {
            dateInfoView.setText(dateInfo);
        }

        TextView additionalInfoView = view.findViewById(R.id.additional_info);
        if (additionalInfo != null) {
            additionalInfoView.setText(additionalInfo);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, window);
        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, window);
        return window;
    }
}

package com.example.android.placesearch;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final LayoutInflater mInflater;

    public CustomInfoWindowAdapter(LayoutInflater inflater) {
        this.mInflater = inflater;
    }

    @Override public View getInfoWindow(Marker marker) {
        final View v = mInflater.inflate(R.layout.info_marker, null);
        Log.e("Title","<--------------------- getInfo Window-------------------------->");
//        ((TextView) popup.findViewById(R.id.address_vicinity)).setText(marker.getTitle());
        TextView address_name = v.findViewById(R.id.address_vicinity);

        TextView latlng = v.findViewById(R.id.latlng);
        TextView vicinity = v.findViewById(R.id.address);
        TextView rating = v.findViewById(R.id.rating_info);
        address_name.setText(marker.getTitle());
        vicinity.setText(marker.getSnippet());
        LatLng latLng = marker.getPosition();
        rating.setText(String.valueOf(marker.getAlpha()));
        String latitude_longitude = String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude);
        latlng.setText(latitude_longitude);
        return v;
        //return null;
    }

    @Override public View getInfoContents(Marker marker) {
        final View v = mInflater.inflate(R.layout.info_marker, null);
        Log.e("Title","<--------------------- getInfo Contents-------------------------->");


        return v;
    }
}

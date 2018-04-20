package com.example.android.placesearch.helper_classes;

import com.google.android.gms.maps.model.LatLng;

public interface ClusterItem {

    /**
     * The position of this marker. This must always return the same value.
     */
    LatLng getPosition();

    /**
     * The title of this marker.
     */
    String getPlaceTitle();

    /**
     * The description of this marker.
     */
    String getSnippet();
}

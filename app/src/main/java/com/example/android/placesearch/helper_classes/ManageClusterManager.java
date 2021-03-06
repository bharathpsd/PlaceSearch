package com.example.android.placesearch.helper_classes;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class ManageClusterManager extends DefaultClusterRenderer<MyItem> {

    private GoogleMap googleMap;
    private Context mContext = null;

    public ManageClusterManager(Context context, GoogleMap map, ClusterManager<MyItem> clusterManager) {
        super(context, map, clusterManager);
        googleMap = map;
        mContext = context;
    }

    @Override
    public void setMinClusterSize(int minClusterSize) {
        super.setMinClusterSize(minClusterSize);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<MyItem> cluster) {
        return cluster.getSize() > 3;
    }

    @Override
    public void setOnClusterItemClickListener(ClusterManager.OnClusterItemClickListener<MyItem> listener) {
        super.setOnClusterItemClickListener(listener);
    }

    @Override
    public void setOnClusterItemInfoWindowClickListener(ClusterManager.OnClusterItemInfoWindowClickListener<MyItem> listener) {
        super.setOnClusterItemInfoWindowClickListener(listener);
    }

    @Override
    protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);
        markerOptions.title(item.getPlaceTitle());
        markerOptions.snippet(item.getSnippet());
    }
}

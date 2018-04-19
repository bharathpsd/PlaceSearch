package com.example.android.placesearch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class InfoActivity extends AppCompatActivity {
    TextView address_name;
    TextView rating;
    TextView vicinity;
    String icon_uri;
    String name,vicinity_info,lat_info, lng_info,image_info,rating_info;
    ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_window);
        address_name = findViewById(R.id.address_vicinity_info);
        rating = findViewById(R.id.rating_info);
        vicinity = findViewById(R.id.address_info);
        imageView = findViewById(R.id.imageview_info);

        name = getIntent().getStringExtra("Address");
        vicinity_info = getIntent().getStringExtra("Vicinity");
        lat_info = getIntent().getStringExtra("Latitude");
        lng_info = getIntent().getStringExtra("Longitude");
        String location = String.valueOf(lat_info) + "," + String.valueOf(lng_info);
        image_info = getIntent().getStringExtra("Icon");
        rating_info = getIntent().getStringExtra("Rating");
        String setRating = "Rating  : " + rating_info;
        address_name.setText(name);
//        latlng.setText(location);
        vicinity.setText(vicinity_info);
        rating.setText(setRating);
        Picasso.get().load(image_info).into(imageView);
    }
}

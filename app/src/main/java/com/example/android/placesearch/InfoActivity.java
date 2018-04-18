package com.example.android.placesearch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {
    TextView address_name;
    TextView latlng;
    TextView vicinity;

    String address,vicinity_info,latlng_info;
    ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_window);
        address_name = findViewById(R.id.address_vicinity_info);
        latlng = findViewById(R.id.latlng_info);
        vicinity = findViewById(R.id.address_info);
        imageView = findViewById(R.id.imageview_info);

        address = getIntent().getStringExtra("Address");
        vicinity_info = getIntent().getStringExtra("Vicinity");
        latlng_info = getIntent().getStringExtra("Location");

        address_name.setText(address);
        latlng.setText(latlng_info);
        vicinity.setText(vicinity_info);
    }
}

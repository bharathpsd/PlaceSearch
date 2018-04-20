package com.example.android.placesearch.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.placesearch.R;
import com.example.android.placesearch.model.DatabaseInfoModel;
import com.example.android.placesearch.presentor.MapHelper;
import com.example.android.placesearch.presentor.MyRecyclerAdapter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.lang.reflect.Field;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int ERROR_DIALOG_REQUEST = 9001;
//    private String searchString = "food";

    //widget
    AutoCompleteTextView editText;
    ImageView imageView;
    FloatingActionButton fab_main,fab_grid,fab_maps;
    private static boolean fab_visibility;
    Animation show_fab,hide_fab;
    MapHelper mapHelper;
    RecyclerView recyclerView;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
        editText = findViewById(R.id.input_search);
        imageView = findViewById(R.id.imageview_main);
        fab_main = findViewById(R.id.fab_main);
        fab_grid = findViewById(R.id.fab_1);
        fab_maps = findViewById(R.id.fab_2);
        show_fab = AnimationUtils.loadAnimation(this,R.anim.show_fab);
        hide_fab = AnimationUtils.loadAnimation(this,R.anim.hide_fab);
        recyclerView = findViewById(R.id.recyclerview);
        fab_visibility = false;
        loadRecyclerView("food",recyclerView);
        //Checking whether the Google Play Services are available or not
        mapHelper = new MapHelper(this,recyclerView);
        if (mapHelper.googlePlayServicesAvailability(ERROR_DIALOG_REQUEST)) {
            Toast.makeText(this, "Google Play Services are Available", Toast.LENGTH_SHORT).show();
            Log.e("##################", "Play Services are available");
            if(mapHelper.getPermissionLocation()){
                initMap();
            }
        } else {
            Toast.makeText(this, "Something is wrong", Toast.LENGTH_SHORT).show();
            Log.e("##################", "Play Services not available");
        }

        fab_main.setImageResource(R.drawable.settings);
        fab_main.setBackgroundColor(Color.parseColor("#448AFF"));
        fab_grid.setImageResource(R.drawable.grid_layout);
        fab_maps.setImageResource(R.drawable.maps);
        mapHelper.getDeviceLocation();
        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fab_visibility == false){
                    fab_visibility = true;
                    fab_grid.setVisibility(View.VISIBLE);
                    fab_maps.setVisibility(View.VISIBLE);
                    fab_maps.startAnimation(show_fab);
                    fab_grid.startAnimation(show_fab);
                } else {
                    fab_visibility = false;
                    fab_grid.setVisibility(View.INVISIBLE);
                    fab_maps.setVisibility(View.INVISIBLE);
                    fab_maps.startAnimation(hide_fab);
                    fab_grid.startAnimation(hide_fab);
                }
            }
        });

        fab_grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,GridRecycler.class);
                intent.putExtra("search_string",editText.getText().toString());
                Log.e("Latitude","<------------------" + String.valueOf(mapHelper.latitude));
                intent.putExtra("pre_lat",String.valueOf(mapHelper.latitude));
                intent.putExtra("pre_lng",String.valueOf(mapHelper.longitude));
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void initMap() {
        Log.e("##################", "Initializing Map");
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
        hideSoftKeyBoard(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapHelper.mapReady(googleMap,editText);
        hideSoftKeyBoard(this);
    }


    private void hideSoftKeyBoard(Activity activity) {
        editText.clearFocus();
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void loadRecyclerView(String searchString,RecyclerView recycler) {
        realm = Realm.getDefaultInstance();
        List<DatabaseInfoModel> result1 = queryData(searchString,realm);
        MyRecyclerAdapter myAdapter = new MyRecyclerAdapter(result1,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setAdapter(myAdapter);
    }

    public List<DatabaseInfoModel> queryData (String searchString, Realm realm){
        // Build the query looking at all users:
        RealmQuery<DatabaseInfoModel> query = realm.where(DatabaseInfoModel.class);
        // Add query conditions:
        query.equalTo("searchString", searchString);
        // Execute the query:
        return query.findAll();
    }


}
package com.example.android.placesearch;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.placesearch.model.DatabaseInfoModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;

public class GridRecycler extends AppCompatActivity {

    RecyclerView recyclerView;
    String searchString;
    Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_view_layout);
        realm = Realm.getDefaultInstance();
//        realm.beginTransaction();
        recyclerView = findViewById(R.id.recyclerview_grid);
        searchString = getIntent().getStringExtra("search_string");
        List<DatabaseInfoModel> result1 = queryData(searchString);
        MyAdapter myAdapter = new MyAdapter(result1);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(myAdapter);

    }

    public List<DatabaseInfoModel> queryData(String searchString) {
        // Build the query looking at all users:
        RealmQuery<DatabaseInfoModel> query = realm.where(DatabaseInfoModel.class);
        // Add query conditions:
        query.equalTo("searchString", searchString);
        // Execute the query:
        return query.findAll();

    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MainViewHolder>{

        List<DatabaseInfoModel> list;

        MyAdapter(List<DatabaseInfoModel> queryResults){
            list = queryResults;
        }

        @NonNull
        @Override
        public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(GridRecycler.this).inflate(R.layout.item_layout_grid,parent,false);
            return new MainViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
            holder.placeName.setText(list.get(position).getName());
            holder.address_grid.setText(list.get(position).getVicinity());
            String location_grid = String.valueOf(list.get(position).getLat()) + "," + String.valueOf(list.get(position).getLng());
            holder.location.setText(location_grid);
            holder.rating_grid.setText(list.get(position).getRating());
            Picasso.get().load(list.get(position).getIcon()).into(holder.imageView);
        }


        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MainViewHolder extends RecyclerView.ViewHolder{
            TextView placeName;
            TextView address_grid;
            TextView location;
            TextView rating_grid;
            ImageView imageView;
            public MainViewHolder(View itemView) {
                super(itemView);
                placeName = itemView.findViewById(R.id.address_grid);
                address_grid = itemView.findViewById(R.id.address_vicinity_grid);
                location = itemView.findViewById(R.id.location_grid);
                rating_grid = itemView.findViewById(R.id.rating_grid);
                imageView = itemView.findViewById(R.id.imageview_grid);
            }
        }
    }

}

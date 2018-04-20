package com.example.android.placesearch.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.placesearch.R;
import com.example.android.placesearch.model.DatabaseInfoModel;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;

public class RecyclerFragment extends Fragment {

    RecyclerView recyclerView;
    String searchString;
    Realm realm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one,container,false);
        recyclerView = view.findViewById(R.id.recyclerview);
        realm = Realm.getDefaultInstance();
        searchString = getArguments().getString("search_string");
        List<DatabaseInfoModel> result1 = queryData(searchString);
        MyAdapter myAdapter = new MyAdapter(result1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(myAdapter);
        return view;
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MainViewHolder>{

        List<DatabaseInfoModel> list;

        MyAdapter(List<DatabaseInfoModel> listincoming){
            list = listincoming;
        }

        @NonNull
        @Override
        public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.list_items,parent,false);
            return new MainViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
            holder.textView.setText(list.get(position).getName());
            holder.textView1.setText(list.get(position).getRating());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MainViewHolder extends RecyclerView.ViewHolder{
            TextView textView,textView1;
            public MainViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.textview1);
                textView1 = itemView.findViewById(R.id.textview2);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(),InfoActivity.class);
                        intent.putExtra("Address",list.get(getAdapterPosition()).getName());
                        intent.putExtra("Vicinity",list.get(getAdapterPosition()).getVicinity());
                        intent.putExtra("Latitude",list.get(getAdapterPosition()).getLat());
                        intent.putExtra("Longitude",list.get(getAdapterPosition()).getLng());
                        intent.putExtra("Icon",list.get(getAdapterPosition()).getIcon());
                        intent.putExtra("Rating",list.get(getAdapterPosition()).getRating());
                        startActivityForResult(intent,1);
                    }
                });

            }
        }
    }

    public List<DatabaseInfoModel> queryData(String searchString) {
        // Build the query looking at all users:
        RealmQuery<DatabaseInfoModel> query = realm.where(DatabaseInfoModel.class);
        // Add query conditions:
        query.equalTo("searchString", searchString);
        // Execute the query:

        return query.findAll();

    }

}

package com.example.android.placesearch.presentor;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.placesearch.R;
import com.example.android.placesearch.model.DatabaseInfoModel;
import com.example.android.placesearch.view.InfoActivity;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MainViewHolder> {

    private Context context;
    private List<DatabaseInfoModel> list;

    public MyRecyclerAdapter(List<DatabaseInfoModel> listincoming,Context context) {
            list = listincoming;
            this.context = context;
        }

        @NonNull
        @Override
        public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
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

        public class MainViewHolder extends RecyclerView.ViewHolder {
            TextView textView, textView1;

            public MainViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.textview1);
                textView1 = itemView.findViewById(R.id.textview2);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, InfoActivity.class);
                        intent.putExtra("Address", list.get(getAdapterPosition()).getName());
                        intent.putExtra("Vicinity", list.get(getAdapterPosition()).getVicinity());
                        intent.putExtra("Latitude", list.get(getAdapterPosition()).getLat());
                        intent.putExtra("Longitude", list.get(getAdapterPosition()).getLng());
                        intent.putExtra("Icon", list.get(getAdapterPosition()).getIcon());
                        intent.putExtra("Rating", list.get(getAdapterPosition()).getRating());
                        context.startActivity(intent);
                    }
                });

            }
        }
    }

package com.example.android.placesearch.presentor;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.placesearch.R;
import com.example.android.placesearch.model.DatabaseInfoModel;
import com.example.android.placesearch.view.InfoActivity;
import com.example.android.placesearch.view.MainActivity;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MainViewHolder> {

    private Context context1;
    private List<DatabaseInfoModel> list;
    public MainActivity activity;



    public MyRecyclerAdapter(List<DatabaseInfoModel> list, Context context1) {
            this.list = list;
            this.context1 = context1;
            activity = new MainActivity();
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
            LinearLayout relativeLayout;

            public MainViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.textview1);
                textView1 = itemView.findViewById(R.id.textview2);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("Context:","--------------------------------> "+ context1.toString());
//                        Toast.makeText(context1.getApplicationContext(),"Clicked something",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context1, InfoActivity.class);
                        intent.putExtra("Address", list.get(getAdapterPosition()).getName());
                        intent.putExtra("Vicinity", list.get(getAdapterPosition()).getVicinity());
                        intent.putExtra("Latitude", list.get(getAdapterPosition()).getLat());
                        intent.putExtra("Longitude",list.get(getAdapterPosition()).getLng());
                        intent.putExtra("Icon", list.get(getAdapterPosition()).getIcon());
                        intent.putExtra("Rating", list.get(getAdapterPosition()).getRating());
                        context1.startActivity(intent);
                    }
                });
            }
        }
    }

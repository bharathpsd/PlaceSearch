package com.example.android.placesearch;

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

import java.util.ArrayList;
import java.util.List;

public class RecyclerFragment extends Fragment {

    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one,container,false);
        recyclerView = view.findViewById(R.id.recyclerview);

        List<String> list = new ArrayList<>();
        list.add("String 1");
        list.add("String 1");
        list.add("String 1");
        list.add("String 1");
        list.add("String 1");
        list.add("String 1");
        list.add("String 1");
        MyAdapter myAdapter = new MyAdapter(list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(myAdapter);
        return view;
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MainViewHolder>{

        List<String> list;

        MyAdapter(List<String> listincoming){
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
            holder.textView.setText(list.get(position));
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
            }
        }
    }

}

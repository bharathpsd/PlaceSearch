package com.example.android.placesearch;

//import android.content.Context;
import android.content.Context;
import android.util.Log;

import com.example.android.placesearch.model.DatabaseInfoModel;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class WriteData {
    private Realm realm;

    WriteData(Context context) {
        initRealm(context);
    }

    private void initRealm(Context context){
        // Initialize Realm
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
    }


    public void writeToRealm(final String searchString, final String placeName, final String vicinity, final double lat, final double lng, final String icon, final String rating) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.e("Inside","<----------------- Write to Realm------------->");
                DatabaseInfoModel info = realm.createObject(DatabaseInfoModel.class,vicinity);
                info.setSearchString(searchString);
                info.setName(placeName);
                info.setLat(String.valueOf(lat));
                info.setLng(String.valueOf(lng));
                info.setIcon(icon);
                info.setRating(rating);
            }


        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.e("Stored Successfully","Realm Database stored these values");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e("Storing Error","Realm Database has some error storing the values",error);
            }
        });
    }

}

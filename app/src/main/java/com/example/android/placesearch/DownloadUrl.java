package com.example.android.placesearch;


//public class DownloadUrl {
//    public String readUrl(String myUrl) throws IOException {
//        Log.e("Tracing","<----------------- Read URL ------------------>");
//        String data="";
//        InputStream inputStream = null;
//        HttpURLConnection urlConnection = null;
//        try {
//            URL url = new URL(myUrl);
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.connect();
//
//            inputStream = urlConnection.getInputStream();
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//            StringBuffer sb = new StringBuffer();
//
//            String line = "";
//            while ((line = bufferedReader.readLine()) != null){
//                sb.append(line);
//            }
//
//            data = sb.toString();
//            bufferedReader.close();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } finally {
//                inputStream.close();
//                urlConnection.disconnect();
//        }
//
////        Log.e("Data from Download URL",data);
//        return data;
//    }

//    public interface API {
//        String BASE_URL = "https://maps.googleapis.com";
//
//        @GET("/maps/api/place/autocomplete/json")
//        Call<PlacesResults> getCityResults(@Query("types") String types, @Query("input") String input, @Query("location") String location, @Query("radius") Integer radius, @Query("key") String key);
//    }


//    Retrofit retrofit = new Retrofit.Builder()
//            .baseUrl(API.BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build();
//
//    API service = retrofit.create(API.class);
//
//
//service.getCityResults(types, input, location, radius, key).enqueue(new Callback<PlacesResults>() {
//        @Override
//        public void onResponse(Call<PlacesResults> call, Response<PlacesResults> response) {
//            PlacesResults places = response.body();
//        }
//
//        @Override
//        public void onFailure(Call<PlacesResults> call, Throwable t) {
//            t.printStackTrace();
//        }
//    });
//}


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DownloadUrl{
    String BASE_URL = "https://maps.googleapis.com";

    @GET("/maps/api/place/nearbysearch/json")
    Call<PlaceResponse> getPlaceDetailsForQuery(@Query("location") String location,
                                                @Query("radius") String radius,
                                                @Query("type") String searchString,
                                                @Query("sensor") String senor,
                                                @Query("key") String key);
        }
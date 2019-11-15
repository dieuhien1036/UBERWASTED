package com.example.ggmap_getlocationtextview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class joinDialog extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;
    private TextView txt_address;
    private Double currentLatitude ;
    private Double currentLongtitude ;
    private Double wasteLatitude ;
    private Double wasteLongtitude ;
    private ImageView img_wasted;
    private ImageButton btn_direction;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.join_dialog_layout, container, false);
//        Bundle bundle = getArguments();
//        String str = bundle.getString("key","");
//        Log.e("AAA",str);
        getData("http://192.168.1.3/upload/uploads/1.jpg");
        return v;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        img_wasted = view.findViewById(R.id.img_wasted);
        final String str = getArguments().getString("address");

        currentLatitude = getArguments().getDouble("currentLatitude",0);
        currentLongtitude = getArguments().getDouble("currentLongtitude",0);

        wasteLatitude = getArguments().getDouble("wasteLatitude",0);
        wasteLongtitude = getArguments().getDouble("wasteLongtitude",0);


        TextView txt_address = view.findViewById(R.id.txt_address);
        txt_address.setText(str);

        getData("http://192.168.1.3/androidwebservice/imageswasted.php");

        Button btn_join = view.findViewById(R.id.btn_join);
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), JoinActivity.class);
                intent.putExtra("address", str);
                startActivity(intent);

            }
        });

        btn_direction = view.findViewById(R.id.btn_direction);
        btn_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequestDirection();
            }
        });

    }

     private void sendRequestDirection(){

        String origin = currentLatitude.toString();

    }
    public interface BottomSheetListener {
        void onButtonClicked(String text);
    }

    private void getData(String url) {
         RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        Double lattitude = object.getDouble("wasteLocation_latitude");
                        Double longtitude = object.getDouble("wasteLocation_longtitude");
                        if((wasteLatitude.equals(lattitude)) && (wasteLongtitude.equals(longtitude))) {
                            String image_url = object.getString("image_url").trim();
                            new LoadImages().execute(image_url);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    //class doc du lieu anh
    private class LoadImages extends AsyncTask<String, Void, Bitmap> {
        Bitmap bitmaphinh;
        InputStream inputStream = null;
        @Override
        protected Bitmap doInBackground(String... strings) {
            Log.e("bitmapnao2","1");
            try {
                URL url = new URL(strings[0]);
                Log.e("url",url+" ");
                try {
                    Log.e("bitmapnao2","2");
                    //lỗi ngay đây => vào catch
                    URLConnection connection = url.openConnection();
                    Log.e("bitmapnao2","3");
                    connection.connect();
                    inputStream = connection.getInputStream();
                } catch (IOException e) {
                    Log.e("bitmapnao2",e + " ");
                    e.printStackTrace();
                }
                try {
                    Log.e("bitmapnao2","3");
                    bitmaphinh = BitmapFactory.decodeStream(inputStream);
                } catch (Exception e) {
                    Log.e("bitmapnao2","loi bit map");
                    e.printStackTrace();
                }

                Log.e("bitmapnao2","5");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Log.e("bitmapnao2","6");
            return bitmaphinh;
        }


        private  View view;
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.join_dialog_layout, container, false);
            return view;
        }
        @Override //hien thi anh len imageview
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            Log.e("bitmapnao2","vao bit map");
            img_wasted.setImageBitmap(bitmaphinh);
        }

    }
}

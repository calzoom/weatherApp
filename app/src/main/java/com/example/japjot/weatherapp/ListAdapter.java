package com.example.japjot.weatherapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by japjot on 3/19/18.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.CustomViewHolder>{

    Context context;
    JSONObject jsonObject;

    //constdisbetch
    ListAdapter(Context context, JSONObject jsonObject) {
        this.context = context;
        this.jsonObject = jsonObject;
    }

    //load the BB
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent,false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return 7;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView maxtemp, mintemp, date;
        ImageView icon;

        public CustomViewHolder(View itemView) {
            super(itemView);
            maxtemp = itemView.findViewById(R.id.maxtemp);
            mintemp = itemView.findViewById(R.id.mintemp);
            date = itemView.findViewById(R.id.date);
            icon = itemView.findViewById(R.id.weathericon);
        }

        void bind(int i) {

            //init these
            String weektempMin = "";
            String weektempMax = "";
            String t2 = "";
            String weekicon = "";

            try {
                weektempMin = ((JSONObject) (jsonObject.getJSONObject("daily").getJSONArray("data").get(i))).getString("temperatureMin");
                weektempMax = ((JSONObject) (jsonObject.getJSONObject("daily").getJSONArray("data").get(i))).getString("temperatureMax");
                Date time = new Date(((JSONObject) (jsonObject.getJSONObject("daily").getJSONArray("data").get(i))).getString("time"));
                SimpleDateFormat formatter2 = new SimpleDateFormat("MM/dd");
                t2 = formatter2.format(time);
                weekicon = ((JSONObject) (jsonObject.getJSONObject("daily").getJSONArray("data").get(i))).getString("icon");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            maxtemp.setText(weektempMax);
            mintemp.setText(weektempMax);
            date.setText(t2);
            switch (weekicon) {
                case "rain":
                    icon.setImageResource(R.drawable.ic_cloud_rain);
                    break;
                case "snow":
                    icon.setImageResource(R.drawable.ic_cloud_snow_alt);
                    break;
                case "sleet":
                    icon.setImageResource(R.drawable.ic_cloud_hail);
                    break;
                case "wind":
                    icon.setImageResource(R.drawable.ic_wind);
                    break;
                case "fog":
                    icon.setImageResource(R.drawable.ic_cloud_fog);
                    break;
                case "cloudy":
                    icon.setImageResource(R.drawable.ic_cloud);
                    break;
                case "partly-cloudy-day":
                    icon.setImageResource(R.drawable.ic_cloud_sun);
                    break;
                case "partly-cloudy-night":
                    icon.setImageResource(R.drawable.ic_cloud_moon);
                    break;
                case "clear-day":
                    icon.setImageResource(R.drawable.ic_sun);
                    break;
                case "clear-night":
                    icon.setImageResource(R.drawable.ic_moon);
                    break;
            }
        }
    }

}

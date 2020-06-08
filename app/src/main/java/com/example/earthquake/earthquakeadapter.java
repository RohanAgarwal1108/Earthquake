package com.example.earthquake;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class earthquakeadapter extends ArrayAdapter<Earthquake> {
    private static final String separator="of";

    public earthquakeadapter(@NonNull Activity context, @NonNull ArrayList<Earthquake> earthquake) {
        super(context, 0, earthquake);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView= LayoutInflater.from(getContext()).inflate(
                    R.layout.quakeitem, parent, false);
        }
        Earthquake earthquake=getItem(position);
        TextView magnitude=listItemView.findViewById(R.id.magnitude);
        String outputter=formatdecimal(earthquake.getMag());
        magnitude.setText(outputter);
        TextView direction_text_view=listItemView.findViewById(R.id.direction);
        TextView loca=listItemView.findViewById(R.id.loca);
        String location=earthquake.getlocation();
        String current_location;
        String direction;
        if(location.contains(separator))
        {
            String[] parts=location.split(separator);
            direction=parts[0]+separator;
            current_location=parts[1].trim();
        }
        else
        {
            direction="Near the";
            current_location=location;
        }
        direction_text_view.setText(direction);
        loca.setText(current_location);
        TextView datem=listItemView.findViewById(R.id.date);
        TextView mtime=listItemView.findViewById(R.id.time);
        Date date=new Date(earthquake.getdate());
        String formatted_date=formatdate(date);
        String formatted_time=formattime(date);
        datem.setText(formatted_date);
        mtime.setText(formatted_time);
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitude.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(earthquake.getMag());

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);
        return listItemView;
    }
    private String formatdate(Date date)
    {
        SimpleDateFormat formatter=new SimpleDateFormat("LLL dd, yyyy");
        return formatter.format(date);
    }
    private String formattime(Date date)
    {
        SimpleDateFormat timeformatter=new SimpleDateFormat("h:mm a");
        return timeformatter.format(date);
    }
    private String formatdecimal(double inputter){
        DecimalFormat formatter=new DecimalFormat("0.0");
        return formatter.format(inputter);
    }
    private int getMagnitudeColor(double current_mag){
        int magnitudeColorResourceId;
        int formatted_mag=(int) Math.floor(current_mag);
        switch (formatted_mag){
            case 0:
            case 1:
            magnitudeColorResourceId=R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId=R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId=R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId=R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId=R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId=R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId=R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId=R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId=R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId=R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }
}

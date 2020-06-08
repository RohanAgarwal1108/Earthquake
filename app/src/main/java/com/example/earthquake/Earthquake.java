package com.example.earthquake;

public class Earthquake {
    private double mag;
    private String location;
    private long timeinmillisecs;
    private String url;

    public Earthquake(double mag1, String location1,long timeinmillisecs1,String murl)
    {
        url=murl;
        mag=mag1;
        location=location1;
        timeinmillisecs=timeinmillisecs1;
    }
    double getMag(){return mag;}
    String getlocation(){return location;}
    long getdate(){return timeinmillisecs;}
    String geturl(){return url;}
}

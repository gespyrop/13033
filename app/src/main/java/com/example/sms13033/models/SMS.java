package com.example.sms13033.models;

import java.sql.Timestamp;

/**
 * Model for an SMS sent to 13033.
 * Stores the location, timestamp
 * and transport reason of the SMS message.
 * */
public class SMS {
    private double latitude, longitude;
    private Timestamp timestamp;
    private TransportReason transportReason;

    public SMS(double latitude, double longitude, TransportReason transportReason) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.transportReason = transportReason;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public SMS(double latitude, double longitude, TransportReason transportReason, Timestamp timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.transportReason = transportReason;
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public TransportReason getTransportReason() {
        return transportReason;
    }
}

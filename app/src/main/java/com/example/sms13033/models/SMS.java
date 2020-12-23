package com.example.sms13033.models;

import java.sql.Timestamp;

public class SMS {
    private double latitude, longitude;
    private Timestamp timestamp;
    private TransportReason transportReason;

    public SMS(TransportReason transportReason) {
        this.transportReason = transportReason;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }
}

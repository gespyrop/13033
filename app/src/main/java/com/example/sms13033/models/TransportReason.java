package com.example.sms13033.models;

public class TransportReason {
    private int code, id;
    private String description;

    public TransportReason() {}

    public TransportReason(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public TransportReason(int id, int code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }
}

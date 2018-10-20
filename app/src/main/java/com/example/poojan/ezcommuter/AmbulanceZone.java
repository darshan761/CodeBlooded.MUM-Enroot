package com.example.poojan.ezcommuter;

public class AmbulanceZone {
    String sosId , sosLat , sasLong , sosRadius ;

    public AmbulanceZone(){}

    public AmbulanceZone(String sosId, String sosLat, String sasLong, String sosRadius) {
        this.sosId = sosId;
        this.sosLat = sosLat;
        this.sasLong = sasLong;
        this.sosRadius = sosRadius;
    }

    public String getSosId() {
        return sosId;
    }

    public void setSosId(String sosId) {
        this.sosId = sosId;
    }

    public String getSosLat() {
        return sosLat;
    }

    public void setSosLat(String sosLat) {
        this.sosLat = sosLat;
    }

    public String getSasLong() {
        return sasLong;
    }

    public void setSasLong(String sasLong) {
        this.sasLong = sasLong;
    }

    public String getSosRadius() {
        return sosRadius;
    }

    public void setSosRadius(String sosRadius) {
        this.sosRadius = sosRadius;
    }
}

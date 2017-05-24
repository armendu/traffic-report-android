package com.example.abidat.trafficmenu;

public class clsReports {
    int reportId;
    String androidId;
    int reportStatus;
    float originLat;
    float originLng;
    float destinationLat;
    float destinationLng;

    public clsReports(int reportId, String androidId, int reportStatus,
                      float originLat, float originLng,
                      float destinationLat, float destinationLng) {
        this.reportId = reportId;
        this.androidId = androidId;
        reportStatus = reportStatus;
        originLat = originLat;
        originLng = originLng;
        destinationLat = destinationLat;
        destinationLng = destinationLng;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public int getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(int reportStatus) {
        this.reportStatus = reportStatus;
    }

    public float getOriginLat() {
        return originLat;
    }

    public void setOriginLat(float originLat) {
        this.originLat = originLat;
    }

    public float getOriginLng() {
        return originLng;
    }

    public void setOriginLng(float originLng) {
        this.originLng = originLng;
    }

    public float getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(float destinationLat) {
        this.destinationLat = destinationLat;
    }

    public float getDestinationLng() {
        return destinationLng;
    }

    public void setDestinationLng(float destinationLng) {
        this.destinationLng = destinationLng;
    }
}
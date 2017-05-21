package com.example.abidat.trafficmenu;

import java.util.Date;

public class ReportInformation {
    public String googleApiClient;
    public String timeOfReport;
    public int status;
    public float originLat;
    public float originLng;
    public float destinationLat;
    public float destinationLng;
    public int typeOfReportId;

    public ReportInformation() {
    }

    public ReportInformation(String googleApiClient, String timeOfReport,
                             int status, float originLat, float originLng,
                             float destinationLat, float destinationLng, int typeOfReportId) {
        this.googleApiClient = googleApiClient;
        this.timeOfReport = timeOfReport;
        this.status = status;
        this.originLat = originLat;
        this.originLng = originLng;
        this.destinationLat = destinationLat;
        this.destinationLng = destinationLng;
        this.typeOfReportId = typeOfReportId;
    }
}

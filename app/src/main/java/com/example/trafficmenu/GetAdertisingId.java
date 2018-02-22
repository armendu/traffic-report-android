package com.example.trafficmenu;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;

/**
 * Android has a unique AdvertisingID for each device
 */
public class GetAdertisingId extends AsyncTask<String,Void,String> {
    Context context;

    public GetAdertisingId(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        AdvertisingIdClient.Info adInfo = null;
        try {
            adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }

        return adInfo.getId();
    }

    @Override
    protected void onPostExecute(String result) {
        Identifiers.android_id = result;
    }
}

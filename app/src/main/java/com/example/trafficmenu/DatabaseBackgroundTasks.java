package com.example.trafficmenu;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static android.content.ContentValues.TAG;

public class DatabaseBackgroundTasks extends AsyncTask<String,Void,String> {

    Context context;
    ProgressDialog progressDialog;

    DatabaseBackgroundTasks(Context context) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage("Saving your report, please wait..");
        progressDialog.setIcon(R.drawable.insert_in_database);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        String reportUrl = Identifiers.restUrl + "report.php";

        String method = params[0];
        if(method.equals("report")){
            String googleApiClient = params[1];
            String reportStatus = params[2];
            String originLat = params[3];
            String originLng = params[4];
            String destinationLat = params[5];
            String destinationLng = params[6];
            try {
                URL url = new URL(reportUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String dataEncoded = URLEncoder.encode("googleapiclient","UTF-8") + "=" +
                        URLEncoder.encode(googleApiClient,"UTF-8") + "&" +
                        URLEncoder.encode("reportstatus","UTF-8") + "=" + URLEncoder.encode(reportStatus,"UTF-8") + "&"+
                        URLEncoder.encode("originlat","UTF-8") + "=" + URLEncoder.encode(originLat,"UTF-8") + "&"+
                        URLEncoder.encode("originlng","UTF-8") + "=" + URLEncoder.encode(originLng,"UTF-8") + "&"+
                        URLEncoder.encode("destinationlat","UTF-8") + "=" + URLEncoder.encode(destinationLat,"UTF-8") + "&"+
                        URLEncoder.encode("destinationlng","UTF-8") + "=" + URLEncoder.encode(destinationLng,"UTF-8");

                bufferedWriter.write(dataEncoded);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                inputStream.close();
                httpURLConnection.disconnect();
                return "Report was successfully saved!";

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.i(TAG, "doInBackground: MalformedURLException " + e);
                return "Reporting failed, please try again!";
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(TAG, "doInBackground: IOException " + e);
                return "Reporting failed, please try again!";
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if(result != null){
            if(result == "Report was successfully saved!"){
                progressDialog.setIcon(R.drawable.insert_success);
            }
            else {
                progressDialog.setIcon(R.drawable.insert_failed);
            }
            progressDialog.setMessage(result);
            progressDialog.cancel();
            Toast.makeText(context,result,Toast.LENGTH_LONG).show();
        }
    }
}

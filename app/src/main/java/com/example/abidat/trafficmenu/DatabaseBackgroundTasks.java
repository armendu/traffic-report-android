package com.example.abidat.trafficmenu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.games.stats.Stats;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
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
        String reportUrl = "http://10.0.2.2/android/report.php";
        String getReportUrl = "http://10.0.2.2/android/getreport.php";

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
                return "Reporting failed";
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(TAG, "doInBackground: IOException " + e);
                return "Reporting failed";
            }
        }
        else if(method.equals("getreport")){
            String androidId = params[1];

            try {
                URL url = new URL(getReportUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String dataEncoded = URLEncoder.encode("googleapiclient","UTF-8") + "=" + URLEncoder.encode(androidId,"UTF-8");
                bufferedWriter.write(dataEncoded);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                String response = "";
                String line = "";

                while((line = bufferedReader.readLine())!=null){

                    response += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return response;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
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

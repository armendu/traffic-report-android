package com.example.abidat.trafficmenu;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.ArrayList;

import static android.content.ContentValues.TAG;
//TODO: Select * from reported routes where time<15min and id = android_id, and allow those to be deleted
//TODO: nav_tools will serve for that purpose
//TODO: Send also the reported routes id so that it can be identified!!

public class DeleteReport extends Fragment{
    ArrayAdapter<String> listViewAdapter;
    ArrayList<String> resultList;
    String reportId;
    String method;
    public DeleteReport() {
        //empty constructor required
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.delete_report, container, false);

        DeleteReport.DatabaseBackgroundTasks2 databaseBackgroundTasks2 = new DeleteReport.DatabaseBackgroundTasks2(getContext());
        databaseBackgroundTasks2.execute("deletereports",Identifiers.android_id);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final ListView listView = (ListView) rootView.findViewById(R.id.list3);

        listViewAdapter = new ArrayAdapter<String>(getActivity(),R.layout.rowlayout,resultList);

        listView.setDividerHeight(10);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Are you sure you want to delete this item?");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DeleteReport.DatabaseBackgroundTasks2 databaseBackgroundTasks2 = new DeleteReport.DatabaseBackgroundTasks2(getContext());
                                databaseBackgroundTasks2.execute("deleteSingleReport",reportId);
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        listView.setAdapter(listViewAdapter);
        Log.i(TAG, "onCreateView: " + listView);

        return rootView;
    }

    class DatabaseBackgroundTasks2 extends AsyncTask<String,Void,String> {
        Context context;

        DatabaseBackgroundTasks2(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {

            String deleteReportsUrl = "http://10.0.2.2/android/deletereports.php";
            String deleteSingleReportUrl = "http://10.0.2.2/android/deletereport.php";

            method = params[0];

            if(method.equals("deletereports")){
                String androidId = params[1];

                try {
                    URL url = new URL(deleteReportsUrl);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setReadTimeout(5000);
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

                    resultList = new ArrayList<>();
                    while((line = bufferedReader.readLine())!=null){
                        String[] parts = line.split(";");
                        reportId = parts[0];
                        String part1 = parts[1];
                        String part2 = parts[2];
                        String part3 = parts[3];
                        String part4 = parts[4];

                        resultList.add(part1 + "\n" + part2 + "\n" + part3 + "\n" + part4);
                        response += line;
                    }

                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();

                    return "Reports were successfully loaded!";

                } catch (MalformedURLException e) {
                    Toast.makeText(this.context,"Could not access the database, please check your connection and try again!",Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "doInBackground: MalformedURLException :" + resultList);
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    Toast.makeText(this.context,"Could not access the database, please check your connection and try again!",Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "doInBackground: ProtocolException :" + resultList);
                    e.printStackTrace();
                } catch (IOException e) {
                    Toast.makeText(this.context,"Could not access the database, please check your connection and try again!",Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "doInBackground: IOException :" + resultList);
                    e.printStackTrace();
                }
            }
            else if(method.equals("deleteSingleReport")){
                String androidId = params[1];
                try {
                    URL url = new URL(deleteSingleReportUrl);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setReadTimeout(5000);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                    String dataEncoded = URLEncoder.encode("reportid","UTF-8") + "=" + URLEncoder.encode(reportId,"UTF-8");
                    bufferedWriter.write(dataEncoded);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return "Report was successfully removed!";

                } catch (MalformedURLException e) {
                    Toast.makeText(this.context,"Could not access the database, please check your connection and try again!",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    Toast.makeText(this.context,"Could not access the database, please check your connection and try again!",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (IOException e) {
                    Toast.makeText(this.context,"Could not access the database, please check your connection and try again!",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            return "Failed";
        }

        @Override
        protected void onPostExecute(String result) {
            if(listViewAdapter != null){
                listViewAdapter.notifyDataSetChanged();
            }
            if(method.equals("deletereports")){
                Toast.makeText(this.context,"Loaded successfully",Toast.LENGTH_LONG).show();
            }
            else if(method.equals("deleteSingleReport")){
                listViewAdapter.notifyDataSetChanged();
                Toast.makeText(this.context,"Removed successfully",Toast.LENGTH_LONG).show();
            }
        }
    }
}

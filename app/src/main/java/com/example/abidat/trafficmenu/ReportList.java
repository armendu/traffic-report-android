package com.example.abidat.trafficmenu;

import android.app.Fragment;
import android.content.Context;
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

public class ReportList extends Fragment {
    ArrayAdapter<String> listViewAdapter;
    ArrayList<String> resultList;
    public ReportList() {
        //empty constructor required
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_report_list, container, false);

        String method = "getreports";
        DatabaseBackgroundTasks2 databaseBackgroundTasks2 = new DatabaseBackgroundTasks2(getContext());
        databaseBackgroundTasks2.execute(method,Identifiers.android_id);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ListView listView = (ListView) rootView.findViewById(R.id.list);
        listView.setDividerHeight(15);

        listViewAdapter = new ArrayAdapter<String>(getActivity(),R.layout.rowlayout,resultList);

        listView.setDividerHeight(10);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] parts = resultList.get(position).split("\n");
                String part1 = parts[0];
                String part2 = parts[1];
                String part3 = parts[2];
                String part4 = parts[3];

                Toast.makeText(getContext(), part2, Toast.LENGTH_SHORT).show();
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

            String getReportUrl = "http://10.0.2.2/android/getreport.php";

            String method = params[0];

            if(method.equals("getreports")){
                String androidId = params[1];

                try {
                    URL url = new URL(getReportUrl);
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
                        String part1 = parts[0];
                        String part2 = parts[1];
                        String part3 = parts[2];
                        String part4 = parts[3];

                        resultList.add(part1 + "\n" + part2 + "\n" + part3 + "\n" + part4);
                        response += line;
                    }

                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();

                    return "Report was successfully saved!";

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
            return "Failed";
        }

        @Override
        protected void onPostExecute(String result) {
            if(listViewAdapter != null){
                listViewAdapter.notifyDataSetChanged();
                Toast.makeText(this.context,"Loaded successfully",Toast.LENGTH_LONG).show();
            }
        }
    }
}



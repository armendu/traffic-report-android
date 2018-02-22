package com.example.trafficmenu;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class AllReportsList extends Fragment {
    ArrayAdapter<String> listViewAdapter;
    ArrayList<String> resultList;
    ArrayList<String> coordinatesList;
    OkHttpClient client = new OkHttpClient();

    public AllReportsList() {
        //empty constructor required
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.all_reports_list, container, false);

        String method = "getallreports";
        AllReportsList.DatabaseBackgroundReport databaseBackgroundReport = new AllReportsList.DatabaseBackgroundReport(getContext());
        databaseBackgroundReport.execute(method,Identifiers.android_id);

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ListView listView = (ListView) rootView.findViewById(R.id.list2);

        if(resultList!=null){
            listViewAdapter = new ArrayAdapter<String>(getActivity(),R.layout.rowlayout,resultList);
        }
        else{
            rootView = inflater.inflate(R.layout.noresults, container, false);
        }

        listView.setDividerHeight(10);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!resultList.get(0).equals("No results found!")){
                    String[] parts = coordinatesList.get(position).split("\n");
                    String originlat = parts[0];
                    String originlng = parts[1];
                    String destinationlat = parts[2];
                    String destinationlng = parts[3];

                    Intent mainActivityIntent = new Intent(getContext(), MainActivity.class);

                    //Sending data to another Activity
                    mainActivityIntent.putExtra("originlat", originlat);
                    mainActivityIntent.putExtra("originlng", originlng);
                    mainActivityIntent.putExtra("destinationlat", destinationlat);
                    mainActivityIntent.putExtra("destinationlng", destinationlng);

                    startActivity(mainActivityIntent);
                }
            }
        });

        listView.setAdapter(listViewAdapter);
        Log.i(TAG, "onCreateView: " + listView);

        return rootView;
    }

    private class DatabaseBackgroundReport extends AsyncTask<String,Void,String> {
        Context context;

        DatabaseBackgroundReport(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {

            Request request = new Request.Builder()
                    .url(Identifiers.restUrl + "getallreportsjson.php")
                    .get()
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    client.dispatcher().cancelAll();
                    call.cancel();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    } else {
                        try {
                            Log.i("responseCode",response.code()+"");
                            resultList = new ArrayList<>();
                            coordinatesList = new ArrayList<>();
                            String strResponse = response.body().string();
                            try {
                                JSONArray jr = new JSONArray(strResponse);
                                //take all the results
                                for(int i=0;i<jr.length();i++){
                                    JSONObject jObject = jr.getJSONObject(i);
                                    String[] parts = new String[6];
                                    parts[0] = jObject.getString("timeofreport");
                                    parts[1] = jObject.getString("originlat");
                                    parts[2] = jObject.getString("originlng");
                                    parts[3] = jObject.getString("destinationlat");
                                    parts[4] = jObject.getString("destinationlng");
                                    parts[5] = jObject.getString("reportstatus");

                                    resultList.add("Time of report: " + parts[0] + "\nOrigin: " + parts[1] + ", " + parts[2] + "\nDestination: " + parts[3]+ ", " + parts[4] + "\nStatus: " + parts[5]);
                                    coordinatesList.add(parts[1]+"\n"+parts[2]+"\n"+parts[3]+"\n"+parts[4]);
                                    response.body().close();
                                }
                            } catch (JSONException e) {
                                Log.i("failure",e.getMessage());
                                e.printStackTrace();
                            }
                        } catch (Exception e){
                            Log.i("failure", e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            });
            return "";
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
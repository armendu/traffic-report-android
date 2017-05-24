package com.example.abidat.trafficmenu;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ReportList extends Fragment {
    ArrayList<clsReports> clsReportsArrayList = new ArrayList<>(); //lista me raporte
    Adapteri objAdapteri;

    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //ListView objListView = (ListView)  findViewById(R.id.list);
        clsReportsArrayList.add(new clsReports(1000, "FilanFisteku", 1,(float)0,(float)0,(float)0,(float)0));
        ListView objListView = (ListView)getView().findViewById(R.id.list);

        objAdapteri = new Adapteri();
        objListView.setAdapter(objAdapteri);
        clsReportsArrayList.add(new clsReports(1000, "FilanFisteku", 1,(float)0,(float)0,(float)0,(float)0));
        objListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(ReportList.this, ((clsReports) clsReportsArrayList.get(position)).getReportId(), Toast.LENGTH_SHORT).show();

                clsReportsArrayList.add(new clsReports(3, "FilanFisteku", 1,(float)0,(float)0,(float)0,(float)0));
                objAdapteri.notifyDataSetChanged(); //nuk ndryshon pa e prek
            }
        });

        //Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_report_list, container, false);
    }


    public class Adapteri extends ArrayAdapter<clsReports> {
        public Adapteri() {
            super(getActivity().getApplicationContext(),R.layout.listrows,clsReportsArrayList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            clsReportsHolder holder = null;

            if(row==null){
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(R.layout.listrows,parent,false);
                holder = new clsReportsHolder(row);
                row.setTag(holder);
            } else {
                holder = (clsReportsHolder) row.getTag();
            }

            holder.getTvEmri().setText(clsReportsArrayList.get(position).getReportStatus());
            holder.getTvDrejtimi().setText(clsReportsArrayList.get(position).getReportStatus());

            return row;
        }
    }
}


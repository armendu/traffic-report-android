package com.example.abidat.trafficmenu;

import android.view.View;
import android.widget.TextView;

public class clsReportsHolder {

    View base;
    TextView tvEmri;
    TextView tvDrejtimi;

    //qiky holder perdoret si ndermjetesus
    public clsReportsHolder(View base) {
        this.base = base;
    }

    public TextView getTvEmri() {
        if(tvEmri == null){
            tvEmri = (TextView)base.findViewById(R.id.tvEmriRow);
        }
        return tvEmri;
    }

    public void setTvEmri(TextView tvEmri) {
        this.tvEmri = tvEmri;
    }

    public TextView getTvDrejtimi() {
        if(tvDrejtimi == null){
            tvDrejtimi = (TextView)base.findViewById(R.id.tvDrejtimiRow);
        }
        return tvDrejtimi;
    }

    public void setTvDrejtimi(TextView tvDrejtimi) {
        this.tvDrejtimi = tvDrejtimi;
    }
}

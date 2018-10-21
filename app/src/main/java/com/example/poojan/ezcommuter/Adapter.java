package com.example.poojan.ezcommuter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by HP on 21-10-2018.
 */

public class Adapter extends RecyclerView.ViewHolder {

    private TextView officer, type, amt;
    Context context;
    String key;
    public Adapter(View itemView) {
        super(itemView);
        officer = itemView.findViewById(R.id.Officer);
        type = itemView.findViewById(R.id.FineType);
        amt = itemView.findViewById(R.id.Amt);
    }

    public void setKey(String key){ this.key = key; }
    public void setContext(Context context){
        this.context = context;
    }
    public void setOfficer(String mOfficer){
        officer.setText(officer.getText().toString()+" : "+mOfficer);
    }
    public void setType(String mType){
        type.setText(type.getText().toString()+" : "+mType);
    }
    public void setAmt(String mAmt){
        amt.setText(amt.getText().toString()+" : "+mAmt);
    }
}
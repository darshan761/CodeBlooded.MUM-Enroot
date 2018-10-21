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

public class Adapter1 extends RecyclerView.ViewHolder {

    private TextView type,  amt;
    Context context;
    String key;
    public Adapter1(View itemView) {
        super(itemView);
        type = itemView.findViewById(R.id.FINE);
        amt = itemView.findViewById(R.id.AMOUNT);
    }

    public void setKey(String key){ this.key = key; }
    public void setContext(Context context){
        this.context = context;
    }
   public void setType(String mType){
        type.setText(type.getText().toString()+" : "+mType);
    }
    public void setAmt(String mAmt){
        amt.setText(amt.getText().toString()+" : "+mAmt);
    }
}
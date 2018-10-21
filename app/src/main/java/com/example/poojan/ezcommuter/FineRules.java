package com.example.poojan.ezcommuter;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FineRules extends AppCompatActivity {

    FirebaseAuth auth;
    android.support.v7.widget.Toolbar toolbar;
    private FirebaseUser user;
    RecyclerView recyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    LinearLayoutManager layoutManager;
    FirebaseRecyclerAdapter<Long, Adapter1> adapter;
    private static final int ITEM_TO_LOAD = 30;
    private int mCurrentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fine_rules);

        toolbar = findViewById(R.id.feature_req_toolbar);
        toolbar.setTitle("Fines Rates");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        auth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.card_view1);
        mSwipeRefreshLayout = findViewById(R.id.swip);

        loaddata();
        refreshPage();

    }

    public void loaddata(){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("FineType");
        dbref.keepSynced(true);

        adapter = new FirebaseRecyclerAdapter<Long, Adapter1>
                (Long.class, R.layout.cardview1,
                        Adapter1.class,
                        dbref.limitToLast(mCurrentPage * ITEM_TO_LOAD)) {

            public void populateViewHolder(final Adapter1 fadapter,
                                           final Long fc, final int position) {
                String key = this.getRef(position).getKey().toString();
                Log.d("key",key);
                Log.d("Position", String.valueOf(position));
                fadapter.setKey(key);
                fadapter.setContext(getApplicationContext());
                fadapter.setType(key);
                fadapter.setAmt(fc.toString());
            }
        };
        recyclerView.setAdapter(adapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }
    public void refreshPage(){
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage ++;

                loaddata();
                adapter.notifyDataSetChanged();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.smoothScrollToPosition(0);
                    }
                }, 200);


            }
        });
    }
}

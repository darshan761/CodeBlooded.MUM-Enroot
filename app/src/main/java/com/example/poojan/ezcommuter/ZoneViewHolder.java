package com.example.poojan.ezcommuter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ZoneViewHolder extends RecyclerView.ViewHolder {

    private TextView userName, title, info, solution,like,dislike;
    LinearLayout wholeCard;
    private ImageView up,down;
    private ImageView zoneImage;
    Context context;
    FirebaseAuth auth;
    String key, type;
    public ZoneViewHolder(View itemView) {
        super(itemView);
        userName = itemView.findViewById(R.id.postUserName);
        title = itemView.findViewById(R.id.postTitle);
        info = itemView.findViewById(R.id.postInfo);
        solution = itemView.findViewById(R.id.postSolution);
        zoneImage = itemView.findViewById(R.id.postImage);
        wholeCard = itemView.findViewById(R.id.wholeCard);
        like = itemView.findViewById(R.id.likecounter);
        dislike = itemView.findViewById(R.id.dislikecounter);
        up = (ImageView)itemView.findViewById(R.id.like);
        down = (ImageView)itemView.findViewById(R.id.dislike);
        }


    public void setContext(Context context,final String ID){
        this.context = context;
        auth = FirebaseAuth.getInstance();
        Query db = FirebaseDatabase.getInstance().getReference().child("user_details").orderByChild("userType");//;
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue().toString().equals("officer")) {
                    wholeCard.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    // Log.d("Logout", "Inside onClick()");

                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            //DELETE POST
                                            DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Zones")
                                                    .child(key);
                                            // Log.d("Delete Post CommId",CommunityId);
                                            // Log.d("Delete Post postd",postid);

                                            db.setValue(null);
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            //No button clicked, do not logout
                                            break;
                                    }
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setMessage("Do you really want to delete this post?")
                                    .setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();

                            //Toast.makeText(context, "Long Pressed Designation", Toast.LENGTH_LONG).show();

                            return true;
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
                up.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final DatabaseReference v = FirebaseDatabase.getInstance().getReference().child("Votes").push();
                            DatabaseReference r = FirebaseDatabase.getInstance().getReference().child("Zones");
                            r.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot x: dataSnapshot.getChildren()){
                                        if(x.child("zoneID").getValue().toString().equals(ID)){
                                            long un = x.child("zoneStatusUpVote").getValue(long.class);
                                            v.child("zoneID").setValue(ID);
                                            v.child("userID").setValue(auth.getCurrentUser().getUid());
                                            v.child("type").setValue("UPVote");
                                            un++;
                                          //  Log.d("ID",x.child("zoneId").getValue().toString());
                                            Log.d("count",x.child("zoneStatusUpVote").getValue().toString());
                                            x.child("zoneStatusUpVote").getRef().setValue(un);
                                            up.setEnabled(false);
                                            up.setBackgroundResource(R.drawable.blue_like);

                                        };
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    });
                    down.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DatabaseReference r = FirebaseDatabase.getInstance().getReference().child("Zones");
                            final DatabaseReference v = FirebaseDatabase.getInstance().getReference().child("Votes").push();
                            r.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot x: dataSnapshot.getChildren()){
                                        if(x.child("zoneID").getValue().toString().equals(ID)){
                                            long un = x.child("zoneStatusDownVote").getValue(long.class);
                                            un++;
                                            v.child("zoneID").setValue(ID);
                                            v.child("userID").setValue(auth.getCurrentUser().getUid());
                                            v.child("type").setValue("DOWNVote");
                                            x.child("zoneStatusDownVote").getRef().setValue(un);
                                            down.setEnabled(false);
                                            down.setBackgroundResource(R.drawable.blue_dislike);
                                        };
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    });
                }

    public void setKey(String key){
        this.key = key;
    }
    public void setTitle(String postTitle){
        title.setText(postTitle);
    }
    public void setInfo(String postInfo){
        info.setText(postInfo);
    }
    public void setSolution(String postSolution){
        solution.setText(postSolution);
    }
    public void setUserAuthID(String authKey) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("user_details");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userName.setText(user.getUserName());
                type = user.getUserType();
              //  Log.d("TYPE",type);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void setCounter(long up,long down){
        like.setText(Long.toString(up));
        dislike.setText(Long.toString(down));
    }
    public void setImage(String imgUri){
        Glide.with(context).load(imgUri).into(zoneImage);
    }
}

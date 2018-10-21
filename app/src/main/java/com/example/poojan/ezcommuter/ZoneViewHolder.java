package com.example.poojan.ezcommuter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.database.ValueEventListener;

public class ZoneViewHolder extends RecyclerView.ViewHolder {

    private TextView userName, title, info, solution;
    LinearLayout wholeCard;
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

        }


    public void setContext(Context context){
        this.context = context;
        auth = FirebaseAuth.getInstance();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("user_details").child(auth.getUid()).child("userType");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class).equals("officer")) {
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
        DatabaseReference db = FirebaseDatabase.getInstance().getReference()
                .child("user_details")
                .child(authKey);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userName.setText(user.getUserName());
                type = user.getUserType();
                Log.d("TYPE",type);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void setImage(String imgUri){
        Glide.with(context).load(imgUri).into(zoneImage);
    }
}

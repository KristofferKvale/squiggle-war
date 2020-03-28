package com.mygdx.game.models;

import androidx.annotation.NonNull;

import com.badlogic.gdx.math.Vector2;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LineModel implements Line {

    private ArrayList<Vector2> points = new ArrayList<>();
    String gameID;
    String playerID;
    private DatabaseReference mDatabase;


    LineModel(Vector2 start, String playerID){
        this.playerID = playerID;
        points.add(start);
        float x = start.x;
        float y = start.y;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("players").child(playerID).child("points");
        String key = mDatabase.push().getKey();
        mDatabase.child(key).setValue(start);


    }

    @Override
    public void addPoint(Vector2 point) {
        this.points.add(point);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("players").child(playerID).child("points");
        String key = mDatabase.push().getKey();
        mDatabase.child(key).setValue(point);

    }

    @Override
    public ArrayList<Vector2> getPoints() {
/*         final ArrayList<Vector2> points = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("players").child(playerID).child("points");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Vector2 point = singleSnapshot.getValue(Vector2.class);
                    points.add(point);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        this.points = points;*/
        return points;
    }

    @Override
    public Vector2 getLastPoint() {
        return this.points.get(points.size() - 1);
    }


}

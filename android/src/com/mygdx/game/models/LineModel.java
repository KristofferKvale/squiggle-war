package com.mygdx.game.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.badlogic.gdx.math.Vector2;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.game.Game;

import java.util.ArrayList;
import java.util.List;

public class LineModel implements Line {

    private ArrayList<Vector2> points = new ArrayList<>();
    String gameID;
    String playerID;
    private DatabaseReference mDatabase;


    LineModel(Vector2 start, String playerID) {
        this.playerID = playerID;
        points.add(start);
        float x = start.x;
        float y = start.y;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("players2").child(playerID).child("points");
        String key = mDatabase.push().getKey();
        mDatabase.child(key).setValue(start);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("players2").child(playerID).child("points");
        mDatabase.addChildEventListener(new ChildEventListener() {

            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try{
                    Vector2 point = dataSnapshot.getValue(Vector2.class);
                    points.add(point);

                }catch (Exception e) {

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void addPoint(Vector2 point) {

        mDatabase = FirebaseDatabase.getInstance().getReference().child("players2").child(playerID).child("points");
        String key = mDatabase.push().getKey();
        mDatabase.child(key).setValue(point);

    }

    @Override
    public ArrayList<Vector2> getPoints() {

        return points;
    }

    @Override
    public Vector2 getLastPoint() {
        return this.points.get(points.size() - 1);
    }

    public void delete() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("players2").child(playerID).child("points");
        mDatabase.removeValue();
        this.points = new ArrayList<>();
        points.add(Game.randomPosition());
    }

}

package com.mygdx.game.models;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class OpponentModel {
    private String playerID;
    private String roomID;
    private String username;
    private int score = 0;
    private boolean crashed = false;
    private Color color = Color.BLUE;
    private DatabaseReference mDatabase;
    private ArrayList<Vector2> points = new ArrayList<>();


    public OpponentModel(String playerId, String roomId) {
        this.playerID = playerId;
        this.roomID = roomId;

        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players").child(playerID).child("score");
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try{
                    score = dataSnapshot.getValue(Integer.class);

                }catch (Exception e){
                    Log.e("ERR", "Err: " + e.toString());
                }
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

        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players").child(playerID).child("crashed");
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try{
                    crashed = dataSnapshot.getValue(Boolean.class);

                }catch (Exception e){
                    Log.e("ERR", "Err: " + e.toString());
                }
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

        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players").child(playerID).child("points");
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

        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players").child(playerID).child("color");
        mDatabase.addChildEventListener(new ChildEventListener() {

            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try{
                    color = dataSnapshot.getValue(Color.class);

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

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public boolean isCrashed() {
        return crashed;
    }

    public ArrayList<Vector2> getPoints() {
        return points;
    }

    public Color getColor() {
        return color;
    }

    public void nextGame() {
        points = new ArrayList<>();
    }

    public Vector2 getPosition() {
        if(points.size() >= 1) {
            return this.points.get(points.size() - 1);
        }else{
            return new Vector2(-100,-100);
        }
    }
}

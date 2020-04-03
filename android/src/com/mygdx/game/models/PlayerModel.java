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
import com.mygdx.game.Game;

import java.util.ArrayList;


public class PlayerModel {
    String username;
    Color color;
    LineModel line;
    boolean active;
    float angle;
    String playerID;
    String gameID;
    private DatabaseReference mDatabase;
    private int score;
    private boolean crashed = false;
    private String roomID;


    //RoomModel trenger en tom constructor for å lese fra db (??)
    public PlayerModel(){}

    public PlayerModel(final String username, Color color, String roomID) {
        this.username = username;
        this.active = true;
        this.color = color;
        this.roomID = roomID;
        this.score = 0;
        this.angle = (float) (Math.random() * 2 * Math.PI);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players");
        playerID = mDatabase.push().getKey();
        mDatabase.child(playerID).child("username").setValue(username);
        mDatabase.child(playerID).child("score").setValue(score);
        mDatabase.child(playerID).child("crashed").setValue(crashed);
        mDatabase.child(playerID).child("color").setValue(color);
        this.line = new LineModel(Game.randomPosition(100), playerID, roomID);
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

    }

    public ArrayList<Vector2> getLinePoints() {
        return this.line.getPoints();
    }

    public String getUsername() {
        return this.username;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setNotActive() {
        this.active = false;
    }

    public void setActive() {
        this.active = true;
    }

    public Vector2 getPosition() {
        return this.line.getLastPoint();
    }

    public Color getColor() {
        return this.color;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players").child(playerID).child("score");
        mDatabase.setValue(score);
    }

    public void incScore() {
        this.score += 1;
        Log.d("MSG", "Score incremented" + score);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players").child(playerID).child("score");
        mDatabase.setValue(score);


    }

    public boolean isCrashed() {
       return crashed;
    }

    public void setCrashed(boolean crashed) {
        this.crashed = crashed;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players").child(playerID).child("crashed");
        mDatabase.setValue(crashed);
    }

    public void turnLeft() {
        this.angle += Game.ROTATION_SPEED;
    }

    public void turnRight() {
        this.angle -= Game.ROTATION_SPEED;
    }

    public void move() {
        Vector2 coords = this.getPosition();
        float x = coords.x;
        float y = coords.y;
        x += (Game.SPEED * Math.cos(this.angle));
        y += (Game.SPEED * Math.sin(this.angle));

        setNewPoint(Math.round(x), Math.round(y));
    }


    public void setNewPoint(int x, int y){
        Vector2 point = new Vector2(x,y);

        if ((int) point.x == (int) this.line.getLastPoint().x && (int) point.y == (int) this.line.getLastPoint().y){

        }
        else {
            this.line.addPoint(point);
        }
    }

    public void nextGame() {
        this.line.delete();
        this.setCrashed(false);

    }

}

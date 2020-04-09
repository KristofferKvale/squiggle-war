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
import java.util.Random;


public class PlayerModel {
    //Gap timer values
    private static final int MIN_GAP_TIME = 20;
    private static final int MAX_GAP_TIME = 50;

    private static final int MIN_LINE_TIME = 200;
    private static final int MAX_LINE_TIME = 250;

    String username;
    Color color;
    LineModel line;
    boolean active;
    float angle;
    String playerID;
    String gameID;
    ArrayList<PowerUpModel> powerups;
    private DatabaseReference mDatabase;
    private int score;
    private boolean crashed = false;
    private boolean ready = false;
    private String roomID;

    boolean line_on;
    private int line_timer = 0;
    private int line_gap_timer = 0;

    private Vector2 position;


    //RoomModel trenger en tom constructor for å lese fra db (??)
    public PlayerModel(){}

    public PlayerModel(final String username, Color color, String roomID) {
        this.username = username;
        this.color = color;
        this.active = true;
        this.color = color;
        this.roomID = roomID;
        this.score = 0;
        this.angle = (float) (Math.random() * 2 * Math.PI);
        this.powerups = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players");
        playerID = mDatabase.push().getKey();
        mDatabase.child(playerID).child("username").setValue(username);
        mDatabase.child(playerID).child("score").setValue(score);
        mDatabase.child(playerID).child("crashed").setValue(crashed);
        mDatabase.child(playerID).child("color").setValue(color);
        mDatabase.child(playerID).child("ready").setValue(ready);
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

        this.position = this.line.getLastPoint();
        this.powerups = new ArrayList<>();
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

    public void setReadyState(boolean ready) { this.ready = ready; }

    public boolean getReadyState() { return this.ready; }

    public void setActive() {
        this.active = true;
    }

    public Vector2 getPosition() {
        //return this.line.getLastPoint();
        return this.position;
    }

    public Vector2 getLastLinePosition() {
        return this.line.getLastPoint();
    }

    public Color getColor() {
        return this.color;
    }

    public int getScore() {
        return score;
    }

    public String getPlayerID() { return playerID; }

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
        int speed = Game.SPEED;
        float x = coords.x;
        float y = coords.y;
        if (this.hasSpeedBoost()){
            speed *= 2;
        }

        x += (speed * Math.cos(this.angle));
        y += (speed * Math.sin(this.angle));

        this.updateTimer();

        setNewPoint(Math.round(x), Math.round(y));
    }


    public void setNewPoint(int x, int y){
        Vector2 point = new Vector2(x,y);

        if ((int) point.x == (int) this.line.getLastPoint().x && (int) point.y == (int) this.line.getLastPoint().y){

        }
        else {
            this.position = point;
            if (this.line_on) {
                this.line.addPoint(point);
            }
        }
    }

    public void nextGame() {
        this.line.delete();
        this.setCrashed(false);

    }

    private boolean hasSpeedBoost(){
        int x = 0;
        for (PowerUpModel powerup:this.powerups){
            if (powerup.name.equals("Speed_boost") && powerup.checkStatus()){
                x = 1;
            }
        }
        return x == 1;
    }

    public boolean isGhost(){
        int x = 0;
        for (PowerUpModel powerup:this.powerups){
            if (powerup.name.equals("Ghost") && powerup.checkStatus()){
                x = 1;
            }
        }
        return x == 1;
    }

    private void updateTimer(){
        if (this.isGhost()) {
            this.line_on = false;
            this.line_timer = 0;
            this.line_gap_timer = 0;
        } else {
            if (this.line_timer == 0 && this.line_gap_timer == 0){
                this.line_on = true;
                this.line_timer = randomLineTime();
            }
            if (this.line_timer > this.line_gap_timer){
                this.line_timer -= 1;
                if (this.line_timer == 0) {
                    this.line_on = false;
                    this.line_gap_timer = randomGapTime();
                }
            } else {
                this.line_gap_timer -= 1;
                if (this.line_gap_timer == 0) {
                    this.line_on = true;
                    this.line_timer = randomLineTime();
                }
            }
        }
    }

    private static int randomGapTime() {
        Random rand = new Random();
        return rand.nextInt(MAX_GAP_TIME-MIN_GAP_TIME) + MIN_GAP_TIME;
    }

    private static int randomLineTime() {
        Random rand = new Random();
        return rand.nextInt(MAX_LINE_TIME-MIN_LINE_TIME) + MIN_LINE_TIME;
    }
}

package com.mygdx.game.models;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.game.Game;

import java.util.ArrayList;
import java.util.Random;


public class PlayerModel {
    //Gap timer values
    private static final int MIN_GAP_TIME = 30;
    private static final int MAX_GAP_TIME = 80;

    private static final int MIN_LINE_TIME = 200;
    private static final int MAX_LINE_TIME = 250;

    String username;
    private Color color;
    LineModel line;
    boolean active;
    float angle;
    public String playerID;
    String gameID;
    private ArrayList<PowerUpModel> powerups;
    private DatabaseReference mDatabase;
    private int score;
    private boolean crashed = false;
    private boolean ready = false;
    private String roomID;

    boolean line_on;
    private int line_timer = 0;
    private int line_gap_timer = 0;

    private Vector3 position;


    //RoomModel trenger en tom constructor for å lese fra db (??)
    public PlayerModel(){}

    public PlayerModel(final String username, Color color, String roomID) {
        this.username = username;
        this.active = true;
        this.color = color;
        this.roomID = roomID;
        this.score = 0;
        this.powerups = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players");
        playerID = mDatabase.push().getKey();
        mDatabase.child(playerID).child("username").setValue(username);
        mDatabase.child(playerID).child("score").setValue(score);
        mDatabase.child(playerID).child("crashed").setValue(crashed);
        mDatabase.child(playerID).child("color").setValue(color);
        mDatabase.child(playerID).child("ready").setValue(ready);
        this.line = new LineModel(Game.randomPlayerPosition(100), playerID, roomID);
        /*mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players").child(playerID).child("score");
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
        });*/

        this.position = this.line.getLastPoint();
        this.setStartAngle();
        this.powerups = new ArrayList<>();

        // Get a reference to colors
        DatabaseReference colDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players").child(playerID).child("color");

        // Attach a listener to read the data at our color reference
        colDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Color c = dataSnapshot.getValue(Color.class);
                Log.d("INSIDE", "color: " + c);
                setColor(c);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public ArrayList<Vector3> getLinePoints() {
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

    public Vector3 getPosition() {
        //return this.line.getLastPoint();
        return this.position;
    }

    public Vector3 getLastLinePosition() {
        return this.line.getLastPoint();
    }

    private void setColor(Color c){
        this.color = c;
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
        String key = mDatabase.push().getKey();
        mDatabase.child(key).setValue(score);
    }

    public boolean isCrashed() {
       return crashed;
    }

    public void setCrashed(boolean crashed) {
        this.crashed = crashed;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID).child("players").child(playerID).child("crashed");
        String key = mDatabase.push().getKey();
        mDatabase.child(key).setValue(crashed);
    }


    private void setStartAngle(){
        Vector3 pos = this.getPosition();
        float direction;
        if (pos.x <= (float) Game.WIDTH/2 && pos.y <= (float) Game.HEIGHT/2)        direction = (float)(Math.random() * 0.5f);
        else if (pos.x > (float) Game.WIDTH/2 && pos.y <= (float) Game.HEIGHT/2)    direction = (float)(Math.random() * 0.5f) + 0.5f;
        else if (pos.x > (float) Game.WIDTH/2 && pos.y > (float) Game.HEIGHT/2)     direction = (float)(Math.random() * 0.5f) + 1f;
        else                                                                        direction = (float)(Math.random() * 0.5f) + 1.5f;
        this.angle = (float) (direction * Math.PI);
    }


    public void turnLeft() {
        if (this.hasSpeedBoost()){
            this.angle += Game.ROTATION_SPEED * 1.4;
        } else {
            this.angle += Game.ROTATION_SPEED;
        }
    }

    public void turnRight() {
        if (this.hasSpeedBoost()){
            this.angle -= Game.ROTATION_SPEED * 1.4;
        } else {
            this.angle -= Game.ROTATION_SPEED;
        }
    }

    public void move(float dt) {
        Vector3 coords = this.getPosition();
        int speed = Game.SPEED;
        float x = coords.x;
        float y = coords.y;
        if (this.hasSpeedBoost()){
            speed *= 1.5;
        }

        x += (speed * Math.cos(this.angle) * dt);
        y += (speed * Math.sin(this.angle) * dt);

        this.updateTimer(dt);

        setNewPoint(Math.round(x), Math.round(y));
    }


    public void setNewPoint(int x, int y){
        Vector3 point = new Vector3(x,y, getSize());

        if ((int) point.x != (int) this.line.getLastPoint().x || (int) point.y != (int) this.line.getLastPoint().y){
            this.position = point;
            if (this.line_on) {
                this.line.addPoint(point);
            }
        }
    }

    public void nextGame() {
        this.line.delete();
        this.position = this.line.getLastPoint();
        this.setStartAngle();
        this.setCrashed(false);
    }

    public ArrayList<PowerUpModel> getPowerups(){ return this.powerups; }

    public void addPowerup(PowerUpModel powerup){ this.powerups.add(powerup); }

    private boolean hasSpeedBoost(){
        int x = 0;
        for (PowerUpModel powerup:this.powerups){
            if (powerup.name.equals("Speed_boost") && powerup.checkStatus()){
                x = 1;
            }
        }
        return x == 1;
    }

    boolean isGhost(){
        int x = 0;
        for (PowerUpModel powerup:this.powerups){
            if (powerup.name.equals("Ghost") && powerup.checkStatus()){
                x = 1;
            }
        }
        return x == 1;
    }

    private boolean isBig(){
        int x = 0;
        for (PowerUpModel powerup:this.powerups){
            if (powerup.name.equals("Grow") && powerup.checkStatus()){
                x = 1;
            }
        }
        return x == 1;
    }

    private boolean isSmall(){
        int x = 0;
        for (PowerUpModel powerup:this.powerups){
            if (powerup.name.equals("Shrink") && powerup.checkStatus()){
                x = 1;
            }
        }
        return x == 1;
    }


    private int getSize(){
        int size = Game.DEFAULT_SIZE;
        if (this.isBig() && this.isSmall()){
            long bigDelta = 0;
            long smallDelta = 0;
            for (PowerUpModel powerup:this.powerups){
                if (powerup.name.equals("Grow")){
                    bigDelta = powerup.getTimeDelta();
                } else if (powerup.name.equals("Shrink")){
                    smallDelta = powerup.getTimeDelta();
                }
            }
            if (bigDelta >= smallDelta){
                size = Game.SMALL_SIZE;
            } else {
                size = Game.BIG_SIZE;
            }
        } else if (this.isSmall()){
            size = Game.SMALL_SIZE;
        } else if (this.isBig()){
            size = Game.BIG_SIZE;
        }
        return size;
    }


    public int getCurrentHeadSize() {
        int z = getSize();
        if (z == Game.SMALL_SIZE){
            return Game.SMALL_HEAD_SIZE;
        } else if (z == Game.BIG_SIZE){
            return Game.BIG_HEAD_SIZE;
        }  else {
            return Game.DEFAULT_HEAD_SIZE;
        }
    }


    public int getHeadSize(int z) {
        if (z == Game.SMALL_SIZE){
            return Game.SMALL_HEAD_SIZE;
        } else if (z == Game.BIG_SIZE){
            return Game.BIG_HEAD_SIZE;
        }  else {
            return Game.DEFAULT_HEAD_SIZE;
        }
    }


    private void updateTimer(float dt){
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
                this.line_timer -= dt;
                if (this.line_timer == 0) {
                    this.line_on = false;
                    this.line_gap_timer = randomGapTime();
                }
            } else {
                this.line_gap_timer -= dt;
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

    private float dist(int x1, int y1, int x2, int y2){
        return (float) Math.sqrt(((x1-x2) * (x1-x2)) + ((y1-y2) * (y1-y2)));
    }

    boolean CollisionTestCircle(int x, int y, int r){
        Vector3 pos = getPosition();
        return (dist((int) pos.x, (int) pos.y, x, y) <= r + getCurrentHeadSize());
    }

    boolean CollisionTestRectangle(int x, int y, int w, int h){
        Vector3 pos = getPosition();
        int testX = (int) pos.x;
        int testY = (int) pos.y;

        if (pos.x < x)          testX = x;        // left edge
        else if (pos.x > x + w) testX = x + w;     // right edge
        if (pos.y < y)          testY = y;        // top edge
        else if (pos.y > y + h) testY = y + h;     // bottom edge

        return (dist((int) pos.x, (int) pos.y, testX, testY) <= getCurrentHeadSize());
    }
}

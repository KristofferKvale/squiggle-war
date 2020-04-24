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
import java.util.Arrays;
import java.util.List;


public class BoardModel {
    private DatabaseReference mDatabase;

    private List<PowerUpModel> powerups = new ArrayList<>();
    public float timeseconds = 0f;
    private float postCrash = 0f;
    private ArrayList<OpponentModel> opponents;
    private float timeBeforeStart = 4f;
    private PlayerModel player;
    private RoomModel room;
    public boolean finished = false;
    private String adminID;


    BoardModel(ArrayList<OpponentModel> opponents, PlayerModel player) {
        this.opponents = opponents;
        this.player = player;

        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(player.getRoomID()).child("players");

        //Adds new players to "players":
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String playerID = dataSnapshot.getKey();
                    removePlayer(playerID);
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        DatabaseReference powerupDB = FirebaseDatabase.getInstance().getReference().child("rooms").child(this.player.getRoomID()).child("powerups");
        powerupDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                long x = (long) dataSnapshot.child("x").getValue();
                long y = (long) dataSnapshot.child("y").getValue();
                Vector2 pos = new Vector2(x, y);
                String powerupName = dataSnapshot.getKey();
                if (Arrays.asList(Game.AVAILABLE_POWERUPS).contains(powerupName)) {
                    PowerUpModel addedPowerup = new PowerUpModel(powerupName, pos);
                    addPowerUp(addedPowerup);
                } else {
                    throw new Error("Powerup name not collected properly from firebase");
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                long x = (long) dataSnapshot.child("x").getValue();
                long y = (long) dataSnapshot.child("y").getValue();
                Vector2 pos = new Vector2(x, y);
                String powerupName = dataSnapshot.getKey();
                if (Arrays.asList(Game.AVAILABLE_POWERUPS).contains(powerupName)) {
                    PowerUpModel addedPowerup = new PowerUpModel(powerupName, pos);
                    addPowerUp(addedPowerup);
                } else {
                    throw new Error("Powerup name not collected properly from firebase");
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                try {
                    long x = (long) dataSnapshot.child("x").getValue();
                    long y = (long) dataSnapshot.child("y").getValue();
                    Vector2 pos = new Vector2();
                    pos.x = x;
                    pos.y = y;
                    for (PowerUpModel powerup : getPowerUps()) {
                        if (powerup.position.equals(pos)) {
                            removePowerUp(powerup);
                        }
                    }
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        try {
            DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(this.player.getRoomID()).child("admin");
            mdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    adminID = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gameFinished() {
        this.finished = true;
    }

    public float getTime(){
        return timeseconds;
    }

    public void incrementTime(float dt){
        timeseconds += dt;
    }

    public float getTimeBeforeStart(){
        return timeBeforeStart;
    }

    public float getPostCrash(){
        return postCrash;
    }

    public void incrementPostCrash(float dt){
        postCrash += dt;
    }

    void setRoom(RoomModel room) {
        this.room = room;
    }

    public RoomModel getRoom() {
        return this.room;
    }

    public String getAdminID() {
        return this.adminID;
    }

    public void setAdminID(String id) {
        this.adminID = id;
    }

    public List<PowerUpModel> getPowerUps(){
        return powerups;
    }

    public void addPowerUp(PowerUpModel powerUp){
        this.powerups.add(powerUp);
    }

    public void removePowerUp(PowerUpModel powerUp){
        this.powerups.remove(powerUp);
    }


    public ArrayList<OpponentModel> getOpponents() {
        return opponents;
    }

    private void removePlayer(String ID) {
        for (OpponentModel opp : opponents) {
            if (opp.getPlayerID().equals(ID)) {
                opponents.remove(opp);
            }
        }
    }

    public PlayerModel getPlayer() {
        return player;
    }

    public void reset() {
        powerups = new ArrayList<>();
        postCrash = 0f;
        timeseconds = 0f;
    }

    public boolean gameStarted() {
        return this.getTime() > this.getTimeBeforeStart();
    }
}


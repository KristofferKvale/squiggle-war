package com.mygdx.game.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mygdx.game.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BoardModel {
    //TO DO:
    // Push position to firebase
    // Push collided boolean to firebase
    // Clear lines and start new round

    public String AdminID;
    private DatabaseReference mDatabase;

    public List<PowerUpModel> powerups = new ArrayList<>();
    public float timeseconds = 0f;
    public float postCrash = 0f;
    ArrayList<OpponentModel> opponents;
    float period = 4f;
    private int width = Gdx.graphics.getWidth();
    private int height = Gdx.graphics.getHeight();
    private PlayerModel player;
    private RoomModel room;
    public boolean finished = false;


    public BoardModel(ArrayList<OpponentModel> opponents, final PlayerModel player) {
        this.opponents = opponents;
        this.player = player;

        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(player.getRoomID()).child("players");
        //mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID);

        //Legger til nye spillere i "players":
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                try{
                    String playerID = dataSnapshot.getKey();
                    removePlayer(playerID);
                }catch(Exception e){}
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        DatabaseReference powerupDB = FirebaseDatabase.getInstance().getReference().child("rooms").child(this.player.getRoomID()).child("powerups");
        final BoardModel this_board = this;
        powerupDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                long x = (long) dataSnapshot.child("x").getValue();
                long y = (long) dataSnapshot.child("y").getValue();
                Vector2 pos = new Vector2();
                pos.x = x;
                pos.y = y;
                String powerupName = dataSnapshot.getKey();
                if (Arrays.asList(Game.AVAILABLE_POWERUPS).contains(powerupName)) {
                    PowerUpModel addedPowerup = new PowerUpModel(powerupName, pos);
                    this_board.powerups.add(addedPowerup);
                } else {
                    throw new Error("Powerup name not collected properly from firebase");
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                long x = (long) dataSnapshot.child("x").getValue();
                long y = (long) dataSnapshot.child("y").getValue();
                Vector2 pos = new Vector2();
                pos.x = x;
                pos.y = y;
                String powerupName = dataSnapshot.getKey();
                if (Arrays.asList(Game.AVAILABLE_POWERUPS).contains(powerupName)) {
                    PowerUpModel addedPowerup = new PowerUpModel(powerupName, pos);
                    this_board.powerups.add(addedPowerup);
                } else {
                    throw new Error("Powerup name not collected properly from firebase");
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                try{
                    long x = (long) dataSnapshot.child("x").getValue();
                    long y = (long) dataSnapshot.child("y").getValue();
                    Vector2 pos = new Vector2();
                    pos.x = x;
                    pos.y = y;
                    for (PowerUpModel powerup : this_board.powerups){
                        if (powerup.position.equals(pos)){
                            this_board.powerups.remove(powerup);
                        }
                    }
                }catch(Exception e){}
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        for (String powerupName : Game.AVAILABLE_POWERUPS) {
            PowerUpModel powerUpModel = new PowerUpModel(powerupName);
            pushPowerup(powerUpModel);
        }
    }

    //Function that returns a player if it has collided with a player or a wall
    public void Collision() {
        if (!player.isCrashed()) {
            CollisionPowerup();
            if (CollisionWalls()) {
                player.setCrashed(true);
            }

            if (!player.isGhost()) {
                if (CollisionPlayer() && player.line_on) {
                    player.setCrashed(true);
                }
                try {
                    if (CollisionOpponent() && player.line_on) {
                        player.setCrashed(true);
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    //Help function that checks if one player is outside the board
    private boolean CollisionWalls() {
        Vector3 point = player.getPosition();
        int x = (int) point.x;
        int y = (int) point.y;
        int z = this.player.getCurrentHeadSize();
        return x + z > Game.PLAYABLE_WIDTH|| x - z < 0|| y + z > Game.PLAYABLE_HEIGHT || y - z < 0;
    }

    //Help function that checks if a players position has been visited

    private boolean CollisionOpponent() {
        for (OpponentModel opponent : this.opponents) {
            ArrayList<Vector3> oppPoints;
            oppPoints = opponent.getPoints();
            for (Vector3 pos : oppPoints) {
                if (player.CollisionTestCircle((int) pos.x, (int) pos.y, (int) pos.z)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean CollisionPlayer() {
        ArrayList<Vector3> playerPoints = new ArrayList<>(player.getLinePoints());
        if (playerPoints.size() > 50) {
            playerPoints.subList(playerPoints.size() - 50, playerPoints.size()).clear();
            for (Vector3 pos : playerPoints) {
                if (player.CollisionTestCircle((int) pos.x, (int) pos.y, (int) pos.z)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void CollisionPowerup() {
        try {
            for (PowerUpModel powerup : this.powerups) {
                int powerUpX = (int) powerup.position.x;
                int powerUpY = Game.HEIGHT - (int) powerup.position.y - 40;
                if (player.CollisionTestRectangle(powerUpX, powerUpY, 40, 40)) {
                    powerup.activate();
                    this.player.addPowerup(powerup);
                    this.powerups.remove(powerup);
                    DatabaseReference powerupsDB = FirebaseDatabase.getInstance().getReference().child("rooms").child(this.player.getRoomID()).child("powerups");
                    powerupsDB.child(powerup.name).removeValue();
                }
            }
        } catch (Exception ignored) {
        }
    }

    public void update(float dt) {
        int score = this.player.getScore();
        for (OpponentModel opp : opponents){
            score += opp.getScore();
        }
        if (score>6){
            this.finished = true;
        }
        timeseconds += dt;
        if (timeseconds > period) {
            this.Collision();
            if (!player.isCrashed() && postCrash == 0) {
                player.move(dt);
            }
            this.playersCrashed(dt);
        }
    }

    public void playersCrashed(float dt) {
        //Get status from opponents
        int numPlayerCrash = 0; // Skal være 0
        if (player.isCrashed()) {
            numPlayerCrash++;
        }
        for (OpponentModel opponent : opponents) {
            if (opponent.isCrashed()) {
                numPlayerCrash++;
            }
        }
        if ((numPlayerCrash >= opponents.size() && (!Game.PLAY_TESTING || player.isCrashed())) || postCrash > 0f) {
            if (postCrash < 6.1f) {
                postCrash += dt;
            } else {
                this.reset();
            }
        }
    }

    private void reset() {
        if (!player.isCrashed()) {
            player.incScore();
        }
        for (OpponentModel opp : opponents) {
            opp.nextGame();
        }
        player.nextGame();
        postCrash = 0f;
        timeseconds = 0f;


        String adminID = this.room.AdminID;
        if (this.player.playerID == adminID) {
            addSpeedBoost();
            addGhost();
            addGrow();
            addShrink();
        }
    }

    public void setRoom(RoomModel room) {
        this.room = room;
    }

    private void pushPowerup(PowerUpModel powerup) {
        DatabaseReference powerupsDB = FirebaseDatabase.getInstance().getReference().child("rooms").child(this.player.getRoomID()).child("powerups").child(powerup.name);
        try {
            powerupsDB.setValue(powerup.position);
            System.out.println("pushed");
        } catch (Exception e) {
            System.out.println("This never works");
        }

    }

    public void addRandomPowerUp() {
        int rnd = (int) (Math.random() * Game.AVAILABLE_POWERUPS.length);
        PowerUpModel new_powerup = new PowerUpModel(Game.AVAILABLE_POWERUPS[rnd]);
        pushPowerup(new_powerup);
    }

    public void addSpeedBoost() {
        PowerUpModel new_powerup = new PowerUpModel("Speed_boost");
        pushPowerup(new_powerup);
    }

    public void addGhost() {
        PowerUpModel new_powerup = new PowerUpModel("Ghost");
        pushPowerup(new_powerup);
    }

    public void addGrow() {
        PowerUpModel new_powerup = new PowerUpModel("Grow");
        pushPowerup(new_powerup);
    }

    public void addShrink() {
        PowerUpModel new_powerup = new PowerUpModel("Shrink");
        pushPowerup(new_powerup);
    }


    public ArrayList<OpponentModel> getOpponents() {
        return opponents;
    }

    public void removePlayer(String ID){
        for (OpponentModel opp : opponents) {
            if (opp.getPlayerID().equals(ID)){
                opponents.remove(opp);
            }
        }
    }

    public PlayerModel getPlayer() {
        return player;
    }
}


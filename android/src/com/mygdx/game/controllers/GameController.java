package com.mygdx.game.controllers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.game.Game;
import com.mygdx.game.models.BoardModel;
import com.mygdx.game.models.OpponentModel;
import com.mygdx.game.models.PlayerModel;
import com.mygdx.game.models.PowerUpModel;

import java.util.ArrayList;
import java.util.Date;

public class GameController extends Controller{
    private final String playerID = this.player.getPlayerID();
    private final String roomID = this.player.getRoomID();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomID);
    private Float pingtimer = 0f;
    private Float getAllPingTimer = 0f;

    public GameController(PlayerModel player, BoardModel board) {
        super(player, board);

        if (this.player.getPlayerID().equals(this.board.getRoom().getAdminID())) {
            for (String powerUpName : Game.AVAILABLE_POWERUPS) {
                PowerUpModel powerUp = new PowerUpModel(powerUpName);
                this.board.addPowerUp(powerUp);
                pushPowerup(powerUp);
            }
        }
    }

    @Override
    public void update(float dt) {
        ping(dt);
        int score = this.player.getScore();
        ArrayList<OpponentModel> opponents = this.board.getOpponents();
        for (OpponentModel opp : opponents) {
            score += opp.getScore();
        }
        if (score > 6) {
            this.board.gameFinished();
        }
        this.board.incrementTime(dt);
        if (this.board.gameStarted()) {
            this.playersCrashed(dt, opponents);
        }
    }

    private void playersCrashed(float dt, ArrayList<OpponentModel> opponents) {
        //Get status from opponents
        int numPlayerCrash = 0; // Skal være 0
        if (this.player.isCrashed()) {
            numPlayerCrash++;
        }
        for (OpponentModel opponent : opponents) {
            if (opponent.isCrashed()) {
                numPlayerCrash++;
            }
        }

        if ((numPlayerCrash >= opponents.size() && (!Game.PLAY_TESTING || player.isCrashed())) || this.board.getPostCrash() > 0f) {
            if (this.board.getPostCrash() < 6.1f) {
                this.board.incrementPostCrash(dt);
            } else {
                reset();
            }
        }
    }

    private void reset() {
        if (!player.isCrashed()) {
            incPlayerScore();
        }
        resetPlayer();
        resetOpponents();
        this.board.reset();

        String adminID = this.board.getRoom().getAdminID();
        if (this.player.getPlayerID().equals(adminID)) {
            mDatabase.child("powerups").removeValue();
            addSpeedBoost();
            addGhost();
            addGrow();
            addShrink();
        }
    }


    private void pushPowerup(PowerUpModel powerup) {
        try {
            mDatabase.child("powerups").child(powerup.name).setValue(powerup.position);
            System.out.println("pushed");
        } catch (Exception e) {
            System.out.println("This never works");
        }
    }

    public void addRandomPowerUp() {
        int rnd = (int) (Math.random() * Game.AVAILABLE_POWERUPS.length);
        PowerUpModel new_powerup = new PowerUpModel(Game.AVAILABLE_POWERUPS[rnd]);
        this.board.addPowerUp(new_powerup);
        pushPowerup(new_powerup);
    }

    private void addSpeedBoost() {
        PowerUpModel new_powerup = new PowerUpModel("Speed_boost");
        this.board.addPowerUp(new_powerup);
        pushPowerup(new_powerup);
    }

    private void addGhost() {
        PowerUpModel new_powerup = new PowerUpModel("Ghost");
        this.board.addPowerUp(new_powerup);
        pushPowerup(new_powerup);
    }

    private void addGrow() {
        PowerUpModel new_powerup = new PowerUpModel("Grow");
        this.board.addPowerUp(new_powerup);
        pushPowerup(new_powerup);
    }

    private void addShrink() {
        PowerUpModel new_powerup = new PowerUpModel("Shrink");
        this.board.addPowerUp(new_powerup);
        pushPowerup(new_powerup);
    }

    private void resetPlayer(){
        this.player.deleteLine();
        this.player.setPosition(this.player.getLastLinePosition());
        this.player.setAngle(Game.startDirection(this.player.getLastLinePosition()));
        this.player.setCrashed(false);
        String key = mDatabase.child("players").child(playerID).child("crashed").push().getKey();
        assert key != null;
        mDatabase.child("players").child(playerID).child("crashed").child(key).setValue(false);
    }

    private void resetOpponents(){
        for (OpponentModel opponent : this.board.getOpponents()) {
            opponent.resetPoints();
        }
    }

    private void incPlayerScore() {
        this.player.incScore();
        Log.d("MSG", "Score incremented" + this.player.getScore());
        String key = mDatabase.child("players").child(this.player.getPlayerID()).child("score").push().getKey();
        assert key != null;
        mDatabase.child("players").child(this.player.getPlayerID()).child("score").child(key).setValue(this.player.getScore());
    }

    private void ping(float dt){
        pingtimer += dt;
        if (pingtimer > 1f){
            Date d = new Date();
            mDatabase.child("players").child(player.getPlayerID()).child("ping").setValue(d);
            pingtimer = 0f;
        }

        getAllPingTimer +=dt;
        ArrayList<OpponentModel> opponents = this.board.getOpponents();
        if (getAllPingTimer > 5f) {
            getAllPingTimer = 0f;
            if (opponents.size()> 0) {
                final String adminID = this.board.getAdminID();
                for (final OpponentModel opponent : opponents) {
                    try {
                        mDatabase.child("players").child(opponent.playerID).child("ping").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                try {
                                    Date d = dataSnapshot.getValue(Date.class);
                                    Date now = new Date();
                                    assert d != null;
                                    long l = now.getTime() - d.getTime();
                                    if (l > 5000) {
                                        mDatabase.child("players").child(opponent.playerID).removeValue();
                                        if (opponent.playerID.equals(adminID)) {
                                            mDatabase.child("admin").setValue(playerID);
                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

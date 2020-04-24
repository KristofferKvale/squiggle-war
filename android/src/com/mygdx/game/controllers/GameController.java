package com.mygdx.game.controllers;

import android.util.Log;

import com.badlogic.gdx.math.Vector3;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mygdx.game.Game;
import com.mygdx.game.models.BoardModel;
import com.mygdx.game.models.OpponentModel;
import com.mygdx.game.models.PlayerModel;
import com.mygdx.game.models.PowerUpModel;

import java.util.ArrayList;
import java.util.List;

public class GameController extends Controller{
    private DatabaseReference mDatabase;

    public GameController(PlayerModel player, BoardModel board) {
        super(player, board);

        for (String powerUpName : Game.AVAILABLE_POWERUPS) {
            PowerUpModel powerUp = new PowerUpModel(powerUpName);
            this.board.addPowerUp(powerUp);
            pushPowerup(powerUp);
        }
    }

    @Override
    public void update(float dt) {
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
            List<PowerUpModel> powerUps = this.board.getPowerUps();
            if(powerUps.size() > 0) {
                try {
                    for (PowerUpModel powerUp : powerUps) {
                        this.board.removePowerUp(powerUp);
                        DatabaseReference powerUpsDB = FirebaseDatabase.getInstance().getReference().child("rooms").child(this.player.getRoomID()).child("powerups");
                        powerUpsDB.child(powerUp.name).removeValue();
                    }
                } catch (Exception ignored){
                }
            }
            addSpeedBoost();
            addGhost();
            addGrow();
            addShrink();
        }
    }


    private void pushPowerup(PowerUpModel powerup) {
        DatabaseReference powerUpsDB = FirebaseDatabase.getInstance().getReference().child("rooms").child(this.player.getRoomID()).child("powerups").child(powerup.name);
        try {
            powerUpsDB.setValue(powerup.position);
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
        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(player.getRoomID()).child("players").child(player.getPlayerID()).child("crashed");
        String key = mDatabase.push().getKey();
        assert key != null;
        mDatabase.child(key).setValue(false);
    }

    private void resetOpponents(){
        for (OpponentModel opponent : this.board.getOpponents()) {
            opponent.resetPoints();
        }
    }

    private void incPlayerScore() {
        this.player.incScore();
        Log.d("MSG", "Score incremented" + this.player.getScore());
        mDatabase = FirebaseDatabase.getInstance().getReference().child("rooms").child(this.player.getRoomID()).child("players").child(this.player.getPlayerID()).child("score");
        String key = mDatabase.push().getKey();
        assert key != null;
        mDatabase.child(key).setValue(this.player.getScore());
    }

}

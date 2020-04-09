package com.mygdx.game.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Game;


import java.util.ArrayList;
import java.util.List;


public class BoardModel {
    //TO DO:
    // Push position to firebase
    // Push collided boolean to firebase
    // Clear lines and start new round

    private int width = Gdx.graphics.getWidth();
    private int height = Gdx.graphics.getHeight();
    public String AdminID;
    ArrayList<OpponentModel> opponents;
    public List<PowerUpModel> powerups  = new ArrayList<>();
    private PlayerModel player;
    public float timeseconds= 0f;
    public float postCrash = 0f;
    float period = 4f;

    public BoardModel(ArrayList<OpponentModel> opponents, PlayerModel player){
        this.opponents = opponents;
        this.player = player;
    }

    //Function that returns a player if it has collided with a player or a wall
    public void Collision() {
        if (!player.isCrashed()) {
            CollisionPowerup();
            if (CollisionWalls()) {
                player.setCrashed(true);
            }
            if (CollisionPlayer() && player.line_on) {
                player.setCrashed(true);
            }
            try {
                if (CollisionOpponent() && player.line_on) {
                    player.setCrashed(true);
                }
            }catch (Exception ignored) {}
        }
    }

    //Help function that checks if one player is outside the board
    private boolean CollisionWalls(){
        ArrayList<Vector2> points = this.player.getLinePoints();
        Vector2 point = points.get(points.size() -1);
        int x = (int) point.x;
        int y = (int) point.y;
        if (x > this.width || x < 0 || y > this.height || y < 0){
            return true;
        } else {
            return false;
        }
    }

    //Help function that checks if a players position has been visited

    private boolean CollisionOpponent(){
        for(OpponentModel opponent : this.opponents){
            Vector2 lastPlayerPos = this.player.getPosition();
            ArrayList<Vector2> oppPoints;
            oppPoints = opponent.getPoints();
            for (Vector2 pos : oppPoints){
                if(Math.abs(lastPlayerPos.x - pos.x) < 12 && Math.abs(lastPlayerPos.y - pos.y) < 12) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean CollisionPlayer() {
        ArrayList<Vector2> playerPoints = new ArrayList<>();
        playerPoints.addAll(player.getLinePoints());
        Vector2 lastPlayerPos = this.player.getPosition();
        if(playerPoints.size()>50) {
            playerPoints.subList(playerPoints.size()-50,playerPoints.size()).clear();
            for (Vector2 pos : playerPoints) {
                if(Math.abs(lastPlayerPos.x - pos.x) < 12 && Math.abs(lastPlayerPos.y - pos.y) < 12){
                    return true;
                }
            }
        }
        return false;
    }

    private void CollisionPowerup(){
        Vector2 point = this.player.getPosition();
        int x = (int) point.x;
        int y = (int) point.y;
        try {
            for (PowerUpModel powerup : this.powerups) {
                int powerUpX = (int) powerup.position.x;
                int powerUpY = Game.HEIGHT - (int) powerup.position.y;
                if (powerUpX <= x && x <= powerUpX + 30) {
                    if (powerUpY - 30 <= y && y <= powerUpY) {
                        powerup.activate();
                        this.player.powerups.add(powerup);
                        this.powerups.remove(powerup);
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    public void update(float dt) {
        timeseconds += dt;
        if (timeseconds > period) {
            this.Collision();
            if (!player.isCrashed()) {
                player.move();
            }
            this.playersCrashed(dt);
        }
    }

    public void playersCrashed(float dt){
        //Get status from opponents
        int numPlayerCrash = 0; // Skal være 0
        if (player.isCrashed()){numPlayerCrash++;}
        for(OpponentModel opponent : opponents) {
            if(opponent.isCrashed()) {
                numPlayerCrash++;
            }
        }
        if(numPlayerCrash >= opponents.size()) {
            if (!player.isCrashed()){player.incScore();} //Skal være !player.isCrashed
            if(postCrash < 6.1f) {
                postCrash += dt;
            } else{
                for(OpponentModel opp : opponents) {
                    opp.nextGame();
                }
                player.nextGame();
                postCrash = 0f;
                timeseconds= 0f;
            }

        }
    }

    public void addRandomPowerUp(){
        int rnd = (int)(Math.random()* Game.AVAILABLE_POWERUPS.length);
        this.powerups.add(new PowerUpModel(Game.AVAILABLE_POWERUPS[rnd]));
    }

    public void addSpeedBoost(){
        this.powerups.add(new PowerUpModel("Speed_boost"));
    }

    public void addGhost(){
        this.powerups.add(new PowerUpModel("Ghost"));
    }


    public ArrayList<OpponentModel> getOpponents() {
        return opponents;
    }

    public PlayerModel getPlayer() {
        return player;
    }
}


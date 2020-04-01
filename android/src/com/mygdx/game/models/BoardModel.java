package com.mygdx.game.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;


import java.util.ArrayList;


public class BoardModel {
    //TO DO:
    // Push position to firebase
    // Push collided boolean to firebase
    // Clear lines and start new round

    private int width = Gdx.graphics.getWidth();
    private int height = Gdx.graphics.getHeight();
    ArrayList<OpponentModel> opponents;
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
            if (CollisionWalls()) {
                player.setCrashed(true);
            }
            if (CollisionPlayer()) {
                player.setCrashed(true);
            }
            try {
                if (CollisionOpponent()) {
                    player.setCrashed(true);
                }
            }catch (Exception e) {}

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


    public ArrayList<OpponentModel> getOpponents() {
        return opponents;
    }

    public PlayerModel getPlayer() {
        return player;
    }

}


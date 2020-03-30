package com.mygdx.game.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import  com.mygdx.game.models.PlayerModel;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BoardModel {
    //TO DO:
    // Push position to firebase
    // Push collided boolean to firebase
    // Clear lines and start new round

    private int width = Gdx.graphics.getWidth();
    private int height = Gdx.graphics.getHeight();
    ArrayList<PlayerModel> opponents;
    private PlayerModel player;
    public PlayerModel collidedWith = null;
    public float timeseconds= 0f;
    float period = 4f;

    public BoardModel(ArrayList<PlayerModel> opponents, PlayerModel player){
        this.opponents = opponents;
        this.player = player;
    }

    //Function that returns a player if it has collided with a player or a wall
    public PlayerModel Collision() {
        if (collidedWith != null) {
            return collidedWith;
        }
        if (CollisionWalls()) {
            return collidedWith = player;
        }
        if (CollisionPlayer()) {
            return collidedWith = player;
        }
        return CollisionOpponent();
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


    private ArrayList<Vector2> getSurroundingPoints(){
        ArrayList<Vector2> lastPlayerPosSurrounding = new ArrayList<Vector2>();
        Vector2 lastPlayerPos = this.player.getPosition();
        lastPlayerPosSurrounding.add(new Vector2(lastPlayerPos.x, lastPlayerPos.y));
        lastPlayerPosSurrounding.add(new Vector2(lastPlayerPos.x, lastPlayerPos.y + 1));
        lastPlayerPosSurrounding.add(new Vector2(lastPlayerPos.x + 1, lastPlayerPos.y + 1));
        lastPlayerPosSurrounding.add(new Vector2(lastPlayerPos.x + 1, lastPlayerPos.y));
        lastPlayerPosSurrounding.add(new Vector2(lastPlayerPos.x + 1, lastPlayerPos.y - 1));
        lastPlayerPosSurrounding.add(new Vector2(lastPlayerPos.x, lastPlayerPos.y - 1));
        lastPlayerPosSurrounding.add(new Vector2(lastPlayerPos.x - 1, lastPlayerPos.y - 1));
        lastPlayerPosSurrounding.add(new Vector2(lastPlayerPos.x - 1, lastPlayerPos.y));
        lastPlayerPosSurrounding.add(new Vector2(lastPlayerPos.x - 1, lastPlayerPos.y + 1));
        return lastPlayerPosSurrounding;
    }
    //Help function that checks if a players position has been visited
    private PlayerModel CollisionOpponent(){

        for(PlayerModel opponent : this.opponents){
            ArrayList<Vector2> opponentPoints = opponent.getLinePoints();
            for (Vector2 pos : getSurroundingPoints()){
                if(opponentPoints.contains(pos)) {
                    return collidedWith = opponent;
                }
            }
        }

        return null;
    }

    private boolean CollisionPlayer() {
        ArrayList<Vector2> playerPoints = new ArrayList<>();
        playerPoints.addAll(player.getLinePoints());
        Vector2 lastPlayerPos = this.player.getPosition();
        if(playerPoints.size()>5) {
            playerPoints.subList(playerPoints.size()-5,playerPoints.size()).clear();
            for (Vector2 pos : getSurroundingPoints()) {
                if(playerPoints.contains(pos)){
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
            if (collidedWith == null) {
                player.move();
                //Kun for testing
                for (PlayerModel opp : opponents) {
                    opp.move();
                }
            } else {
                //Push crashed boolean to player and firebase
            }
            this.playersCrashed();
        }
    }

    public void playersCrashed(){
        //Get status from opponents
        int crashed = 0;
        //if (player.crashed){crashed++};
        for(PlayerModel opponent : opponents) {
            /*if(opponent.crashed) {
            crashed++;
            }*/
        }
        if(crashed >= opponents.size()) {
            //End round
            //Reset lines
            //Start new round
        }
    }

    public ArrayList<PlayerModel> getPlayers() {
        ArrayList<PlayerModel> players = new ArrayList<>();
        players.add(player);
        players.addAll(opponents);
        return players;
    }

    public ArrayList<PlayerModel> getOpponents() {
        return opponents;
    }

    public PlayerModel getPlayer() {
        return player;
    }
}


package com.mygdx.game.models;

import com.badlogic.gdx.Gdx;
import  com.mygdx.game.models.Point;
import  com.mygdx.game.models.PlayerModel;


import java.util.List;


public class BoardModel {
    //TO DO:
    //Create board which renders board and lines based on input (players).
    //Also need outer boarders.
    //Needs function to check for collision between players or outer boarders

    private int width = Gdx.graphics.getWidth();
    private int height = Gdx.graphics.getHeight();
    List<PlayerModel> players;
    List<Line> lines;

    public BoardModel(List<PlayerModel> players){
        this.players = players;
    }


    //Function that returns a player if it has collided with a player or a wall
    public PlayerModel WallCollisions(List<PlayerModel> players){
        for (PlayerModel player : players){
            if (CollisionWalls(player, height, width) || CollisionPlayer(player)){
                return player;
        }
    }

    //Help function that checks if one player is outside the board
    private boolean CollisionWalls(PlayerModel player, int width, int height){
        List<Point> points = player.getLinePoints();
        Point point = points.get(points.size() -1);
        int x = point.getX();
        int y = point.getY();
        if (x > width || x < 0 || y > height || y < 0){
            return true;
        } else {
            return false;
        }
    }

    //Help function that checks if a players position has been visited
    private boolean CheckPlayerCollision(PlayerModel player){
        // check for every player if their "last point (current position)" is also in another players LinePoints list?
            return true;
        else {
            return false;
           }
    }


    }

}


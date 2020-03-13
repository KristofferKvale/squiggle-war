package com.mygdx.game.models;


import com.badlogic.gdx.graphics.Color;

import java.util.List;

import com.badlogic.gdx.math.Vector2;

public class PlayerModel {
            String username;
            Color color;
            LineModel line;
            boolean active;

            PlayerModel(String username, Color color, Vector2 start){
                this.username = username;
                this.color = color;
                this.line = new LineModel(start);
                this.active = true;
            }

            public List<Vector2> getLinePoints(){
                return this.line.getPoints();
            }

            public void setNotActive(){
                this.active = false;
            }
            public void setActive(){
                this.active = true;
    }

    public void setNewPoint(int x, int y){
        Vector2 point = new Vector2(x,y);
        this.line.addPoint(point);
    }

}

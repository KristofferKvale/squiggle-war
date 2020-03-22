package com.mygdx.game.models;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import com.mygdx.game.Game;

import java.util.List;


public class PlayerModel {
            String username;
            Color color;
            LineModel line;
            boolean active;
			float angle;

            PlayerModel(String username, Color color, Vector2 start){
                this.username = username;
                this.color = color;
                this.line = new LineModel(start);
                this.active = true;
				this.angle = 0;
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

			public void turnLeft(){
				this.angle += Game.ROTATION_SPEED;
			}

			public void turnRight(){
				this.angle -= Game.ROTATION_SPEED;
			}



    public void setNewPoint(int x, int y){
        Vector2 point = new Vector2(x,y);
        this.line.addPoint(point);
    }

}
